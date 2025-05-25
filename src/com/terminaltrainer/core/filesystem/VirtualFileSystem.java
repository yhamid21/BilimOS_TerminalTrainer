package com.terminaltrainer.core.filesystem;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the virtual file system structure.
 * Provides methods for navigating the file system, creating and manipulating
 * files and directories, and resolving paths.
 */
public class VirtualFileSystem {
    private final VirtualDirectory root;

    /**
     * Creates a new virtual file system with a basic Linux-like structure.
     */
    public VirtualFileSystem() {
        // Create the root directory
        this.root = new VirtualDirectory("", null);
        
        // Create basic Linux directory structure
        VirtualDirectory bin = new VirtualDirectory("bin", root);
        VirtualDirectory etc = new VirtualDirectory("etc", root);
        VirtualDirectory home = new VirtualDirectory("home", root);
        VirtualDirectory usr = new VirtualDirectory("usr", root);
        VirtualDirectory var = new VirtualDirectory("var", root);
        VirtualDirectory tmp = new VirtualDirectory("tmp", root);
        
        root.addChild(bin);
        root.addChild(etc);
        root.addChild(home);
        root.addChild(usr);
        root.addChild(var);
        root.addChild(tmp);
        
        // Create user home directory
        VirtualDirectory userHome = new VirtualDirectory("user", home);
        home.addChild(userHome);
        
        // Add some basic directories in user's home
        userHome.addChild(new VirtualDirectory("Documents", userHome));
        userHome.addChild(new VirtualDirectory("Pictures", userHome));
        
        // Add some example files
        VirtualDirectory documents = (VirtualDirectory) userHome.getChild("Documents");
        documents.addChild(new VirtualFile("welcome.txt", documents, 
                "Welcome to Terminal Trainer!\n\n" +
                "This educational application will help you learn basic Linux commands.\n" +
                "Try using commands like 'ls', 'cd', and 'pwd' to navigate the file system.\n" +
                "Type 'help' for more information on available commands.\n"));
        
        // Add some system files
        bin.addChild(new VirtualFile("ls", bin, "#!/bin/bash\n# This is a simulated executable file"));
        bin.addChild(new VirtualFile("cd", bin, "#!/bin/bash\n# This is a simulated executable file"));
        bin.addChild(new VirtualFile("pwd", bin, "#!/bin/bash\n# This is a simulated executable file"));
    }

    /**
     * Gets the root directory of the file system.
     *
     * @return The root directory
     */
    public VirtualDirectory getRoot() {
        return root;
    }

    /**
     * Resolves a path to a file system node.
     *
     * @param path The path to resolve
     * @param currentDirectory The current directory (for relative paths)
     * @return The resolved node, or null if the path does not exist
     */
    public VirtualFileSystemNode resolvePath(String path, String currentDirectory) {
        if (path == null || path.isEmpty()) {
            return null;
        }
        
        // Handle absolute paths
        if (path.startsWith("/")) {
            return resolveAbsolutePath(path);
        }
        
        // Handle special paths
        if (path.equals(".")) {
            return resolveAbsolutePath(currentDirectory);
        }
        
        if (path.equals("..")) {
            VirtualFileSystemNode current = resolveAbsolutePath(currentDirectory);
            if (current != null && current.isDirectory() && current.getParent() != null) {
                return current.getParent();
            }
            return root; // If at root, stay at root
        }
        
        // Handle relative paths
        if (!currentDirectory.endsWith("/")) {
            currentDirectory += "/";
        }
        
        return resolveAbsolutePath(currentDirectory + path);
    }

    /**
     * Resolves an absolute path to a file system node.
     *
     * @param path The absolute path to resolve
     * @return The resolved node, or null if the path does not exist
     */
    private VirtualFileSystemNode resolveAbsolutePath(String path) {
        if (path == null) {
            return null;
        }
        
        // Handle root path
        if (path.equals("/")) {
            return root;
        }
        
        // Remove trailing slash if present
        if (path.endsWith("/") && path.length() > 1) {
            path = path.substring(0, path.length() - 1);
        }
        
        // Remove leading slash
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        
        // Split path into components
        String[] components = path.split("/");
        
        // Start at root
        VirtualFileSystemNode current = root;
        
        // Traverse the path
        for (String component : components) {
            if (component.isEmpty()) {
                continue;
            }
            
            if (!current.isDirectory()) {
                return null; // Cannot traverse into a file
            }
            
            VirtualDirectory currentDir = (VirtualDirectory) current;
            current = currentDir.getChild(component);
            
            if (current == null) {
                return null; // Path component not found
            }
        }
        
        return current;
    }

    /**
     * Creates a new directory at the specified path.
     *
     * @param path The path where to create the directory
     * @param currentDirectory The current directory (for relative paths)
     * @return true if the directory was created, false otherwise
     */
    public boolean createDirectory(String path, String currentDirectory) {
        // Extract parent path and directory name
        String parentPath = getParentPath(path);
        String dirName = getBaseName(path);
        
        if (dirName.isEmpty()) {
            return false;
        }
        
        // Resolve parent path
        VirtualFileSystemNode parent = resolvePath(parentPath, currentDirectory);
        
        if (parent == null || !parent.isDirectory()) {
            return false;
        }
        
        VirtualDirectory parentDir = (VirtualDirectory) parent;
        
        // Check if a node with this name already exists
        if (parentDir.containsChild(dirName)) {
            return false;
        }
        
        // Create and add the new directory
        VirtualDirectory newDir = new VirtualDirectory(dirName, parentDir);
        return parentDir.addChild(newDir);
    }

    /**
     * Creates a new file at the specified path.
     *
     * @param path The path where to create the file
     * @param currentDirectory The current directory (for relative paths)
     * @param content The initial content of the file
     * @return true if the file was created, false otherwise
     */
    public boolean createFile(String path, String currentDirectory, String content) {
        // Extract parent path and file name
        String parentPath = getParentPath(path);
        String fileName = getBaseName(path);
        
        if (fileName.isEmpty()) {
            return false;
        }
        
        // Resolve parent path
        VirtualFileSystemNode parent = resolvePath(parentPath, currentDirectory);
        
        if (parent == null || !parent.isDirectory()) {
            return false;
        }
        
        VirtualDirectory parentDir = (VirtualDirectory) parent;
        
        // Check if a node with this name already exists
        if (parentDir.containsChild(fileName)) {
            return false;
        }
        
        // Create and add the new file
        VirtualFile newFile = new VirtualFile(fileName, parentDir, content);
        return parentDir.addChild(newFile);
    }

    /**
     * Deletes a file or directory at the specified path.
     *
     * @param path The path to the file or directory to delete
     * @param currentDirectory The current directory (for relative paths)
     * @param recursive Whether to recursively delete directories
     * @return true if the node was deleted, false otherwise
     */
    public boolean delete(String path, String currentDirectory, boolean recursive) {
        // Extract parent path and node name
        String parentPath = getParentPath(path);
        String nodeName = getBaseName(path);
        
        if (nodeName.isEmpty()) {
            return false;
        }
        
        // Resolve parent path
        VirtualFileSystemNode parent = resolvePath(parentPath, currentDirectory);
        
        if (parent == null || !parent.isDirectory()) {
            return false;
        }
        
        VirtualDirectory parentDir = (VirtualDirectory) parent;
        
        // Get the node to delete
        VirtualFileSystemNode nodeToDelete = parentDir.getChild(nodeName);
        
        if (nodeToDelete == null) {
            return false;
        }
        
        // Check if it's a non-empty directory and recursive is false
        if (nodeToDelete.isDirectory() && !recursive) {
            VirtualDirectory dirToDelete = (VirtualDirectory) nodeToDelete;
            if (!dirToDelete.isEmpty()) {
                return false; // Cannot delete non-empty directory without recursive flag
            }
        }
        
        // Delete the node
        return parentDir.removeChild(nodeName) != null;
    }

    /**
     * Gets the parent path of a path.
     *
     * @param path The path
     * @return The parent path
     */
    private String getParentPath(String path) {
        if (path == null || path.isEmpty() || path.equals("/")) {
            return "/";
        }
        
        // Remove trailing slash if present
        if (path.endsWith("/") && path.length() > 1) {
            path = path.substring(0, path.length() - 1);
        }
        
        int lastSlashIndex = path.lastIndexOf('/');
        
        if (lastSlashIndex == -1) {
            return "."; // Relative path with no slashes
        }
        
        if (lastSlashIndex == 0) {
            return "/"; // Direct child of root
        }
        
        return path.substring(0, lastSlashIndex);
    }

    /**
     * Gets the base name (file or directory name) of a path.
     *
     * @param path The path
     * @return The base name
     */
    private String getBaseName(String path) {
        if (path == null || path.isEmpty() || path.equals("/")) {
            return "";
        }
        
        // Remove trailing slash if present
        if (path.endsWith("/") && path.length() > 1) {
            path = path.substring(0, path.length() - 1);
        }
        
        int lastSlashIndex = path.lastIndexOf('/');
        
        if (lastSlashIndex == -1) {
            return path; // No slashes, return the whole path
        }
        
        return path.substring(lastSlashIndex + 1);
    }

    /**
     * Lists the contents of a directory.
     *
     * @param path The path to the directory
     * @param currentDirectory The current directory (for relative paths)
     * @return A list of nodes in the directory, or null if the path does not exist or is not a directory
     */
    public List<VirtualFileSystemNode> listDirectory(String path, String currentDirectory) {
        VirtualFileSystemNode node = resolvePath(path, currentDirectory);
        
        if (node == null || !node.isDirectory()) {
            return null;
        }
        
        VirtualDirectory dir = (VirtualDirectory) node;
        return dir.getChildren();
    }

    /**
     * Moves or renames a file or directory.
     *
     * @param sourcePath The source path
     * @param destinationPath The destination path
     * @param currentDirectory The current directory (for relative paths)
     * @return true if the move was successful, false otherwise
     */
    public boolean move(String sourcePath, String destinationPath, String currentDirectory) {
        // Resolve source node
        VirtualFileSystemNode sourceNode = resolvePath(sourcePath, currentDirectory);
        
        if (sourceNode == null) {
            return false;
        }
        
        // Get source parent
        VirtualDirectory sourceParent = sourceNode.getParent();
        
        if (sourceParent == null) {
            return false; // Cannot move root
        }
        
        // Extract destination parent path and new name
        String destParentPath = getParentPath(destinationPath);
        String destName = getBaseName(destinationPath);
        
        if (destName.isEmpty()) {
            return false;
        }
        
        // Resolve destination parent
        VirtualFileSystemNode destParentNode = resolvePath(destParentPath, currentDirectory);
        
        if (destParentNode == null || !destParentNode.isDirectory()) {
            return false;
        }
        
        VirtualDirectory destParent = (VirtualDirectory) destParentNode;
        
        // Check if destination already exists
        if (destParent.containsChild(destName)) {
            return false;
        }
        
        // Remove from source parent
        sourceParent.removeChild(sourceNode.getName());
        
        // Update node name and parent
        sourceNode.setName(destName);
        sourceNode.setParent(destParent);
        
        // Add to destination parent
        return destParent.addChild(sourceNode);
    }

    /**
     * Copies a file or directory.
     *
     * @param sourcePath The source path
     * @param destinationPath The destination path
     * @param currentDirectory The current directory (for relative paths)
     * @param recursive Whether to recursively copy directories
     * @return true if the copy was successful, false otherwise
     */
    public boolean copy(String sourcePath, String destinationPath, String currentDirectory, boolean recursive) {
        // Resolve source node
        VirtualFileSystemNode sourceNode = resolvePath(sourcePath, currentDirectory);
        
        if (sourceNode == null) {
            return false;
        }
        
        // Extract destination parent path and new name
        String destParentPath = getParentPath(destinationPath);
        String destName = getBaseName(destinationPath);
        
        if (destName.isEmpty()) {
            return false;
        }
        
        // Resolve destination parent
        VirtualFileSystemNode destParentNode = resolvePath(destParentPath, currentDirectory);
        
        if (destParentNode == null || !destParentNode.isDirectory()) {
            return false;
        }
        
        VirtualDirectory destParent = (VirtualDirectory) destParentNode;
        
        // Check if destination already exists
        if (destParent.containsChild(destName)) {
            return false;
        }
        
        // Copy the node
        if (sourceNode.isFile()) {
            VirtualFile sourceFile = (VirtualFile) sourceNode;
            VirtualFile newFile = new VirtualFile(destName, destParent, sourceFile.getContent());
            return destParent.addChild(newFile);
        } else {
            // It's a directory
            VirtualDirectory sourceDir = (VirtualDirectory) sourceNode;
            VirtualDirectory newDir = new VirtualDirectory(destName, destParent);
            
            if (!destParent.addChild(newDir)) {
                return false;
            }
            
            // If recursive, copy all children
            if (recursive) {
                for (VirtualFileSystemNode child : sourceDir.getChildren()) {
                    if (child.isFile()) {
                        VirtualFile childFile = (VirtualFile) child;
                        VirtualFile newChildFile = new VirtualFile(child.getName(), newDir, childFile.getContent());
                        newDir.addChild(newChildFile);
                    } else {
                        // Recursively copy subdirectories
                        String childSourcePath = sourcePath + "/" + child.getName();
                        String childDestPath = destinationPath + "/" + child.getName();
                        if (!copy(childSourcePath, childDestPath, currentDirectory, true)) {
                            return false;
                        }
                    }
                }
            }
            
            return true;
        }
    }
}