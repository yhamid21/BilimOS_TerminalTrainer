package com.terminaltrainer.core.filesystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a directory in the virtual file system.
 */
public class VirtualDirectory extends VirtualFileSystemNode {
    private final Map<String, VirtualFileSystemNode> children;

    /**
     * Creates a new virtual directory.
     *
     * @param name The name of the directory
     * @param parent The parent directory, or null if this is the root
     */
    public VirtualDirectory(String name, VirtualDirectory parent) {
        super(name, parent);
        this.children = new HashMap<>();
    }

    /**
     * Adds a child node to this directory.
     *
     * @param node The child node to add
     * @return true if the node was added, false if a node with the same name already exists
     */
    public boolean addChild(VirtualFileSystemNode node) {
        if (node == null || children.containsKey(node.getName())) {
            return false;
        }
        
        children.put(node.getName(), node);
        updateModificationTime();
        return true;
    }

    /**
     * Removes a child node from this directory.
     *
     * @param name The name of the child node to remove
     * @return The removed node, or null if no node with the given name exists
     */
    public VirtualFileSystemNode removeChild(String name) {
        if (name == null || !children.containsKey(name)) {
            return null;
        }
        
        VirtualFileSystemNode removed = children.remove(name);
        updateModificationTime();
        return removed;
    }

    /**
     * Gets a child node by name.
     *
     * @param name The name of the child node
     * @return The child node, or null if no node with the given name exists
     */
    public VirtualFileSystemNode getChild(String name) {
        return children.get(name);
    }

    /**
     * Gets all child nodes.
     *
     * @return A list of all child nodes
     */
    public List<VirtualFileSystemNode> getChildren() {
        return new ArrayList<>(children.values());
    }

    /**
     * Gets all child files.
     *
     * @return A list of all child files
     */
    public List<VirtualFile> getFiles() {
        List<VirtualFile> files = new ArrayList<>();
        for (VirtualFileSystemNode node : children.values()) {
            if (node.isFile()) {
                files.add((VirtualFile) node);
            }
        }
        return files;
    }

    /**
     * Gets all child directories.
     *
     * @return A list of all child directories
     */
    public List<VirtualDirectory> getDirectories() {
        List<VirtualDirectory> directories = new ArrayList<>();
        for (VirtualFileSystemNode node : children.values()) {
            if (node.isDirectory()) {
                directories.add((VirtualDirectory) node);
            }
        }
        return directories;
    }

    /**
     * Gets the number of child nodes.
     *
     * @return The number of children
     */
    public int getChildCount() {
        return children.size();
    }

    /**
     * Checks if this directory has any children.
     *
     * @return true if this directory has no children, false otherwise
     */
    public boolean isEmpty() {
        return children.isEmpty();
    }

    /**
     * Checks if this directory contains a child with the given name.
     *
     * @param name The name to check
     * @return true if a child with the given name exists, false otherwise
     */
    public boolean containsChild(String name) {
        return children.containsKey(name);
    }

    /**
     * Clears all children from this directory.
     */
    public void clear() {
        children.clear();
        updateModificationTime();
    }

    @Override
    public boolean isDirectory() {
        return true;
    }

    @Override
    public boolean isFile() {
        return false;
    }

    @Override
    public long getSize() {
        long size = 0;
        for (VirtualFileSystemNode node : children.values()) {
            size += node.getSize();
        }
        return size;
    }

    @Override
    public String toString() {
        return getName() + "/";
    }
}