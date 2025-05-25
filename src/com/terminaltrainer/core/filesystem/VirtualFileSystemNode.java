package com.terminaltrainer.core.filesystem;

import java.time.LocalDateTime;

/**
 * Base class for all nodes in the virtual file system.
 * This includes both files and directories.
 */
public abstract class VirtualFileSystemNode {
    private String name;
    private VirtualDirectory parent;
    private final LocalDateTime creationTime;
    private LocalDateTime modificationTime;
    private String owner;
    private String group;
    private int permissions; // Unix-style permissions (e.g., 755)

    /**
     * Creates a new file system node.
     *
     * @param name The name of the node
     * @param parent The parent directory, or null if this is the root
     */
    public VirtualFileSystemNode(String name, VirtualDirectory parent) {
        this.name = name;
        this.parent = parent;
        this.creationTime = LocalDateTime.now();
        this.modificationTime = LocalDateTime.now();
        this.owner = "user";
        this.group = "user";
        this.permissions = 0755; // Default permissions: rwxr-xr-x
    }

    /**
     * Gets the name of the node.
     *
     * @return The node name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the node.
     *
     * @param name The new name
     */
    public void setName(String name) {
        this.name = name;
        this.modificationTime = LocalDateTime.now();
    }

    /**
     * Gets the parent directory.
     *
     * @return The parent directory, or null if this is the root
     */
    public VirtualDirectory getParent() {
        return parent;
    }

    /**
     * Sets the parent directory.
     *
     * @param parent The new parent directory
     */
    public void setParent(VirtualDirectory parent) {
        this.parent = parent;
        this.modificationTime = LocalDateTime.now();
    }

    /**
     * Gets the absolute path of this node.
     *
     * @return The absolute path
     */
    public String getPath() {
        if (parent == null) {
            return "/" + name;
        }
        
        String parentPath = parent.getPath();
        if (parentPath.equals("/")) {
            return parentPath + name;
        } else {
            return parentPath + "/" + name;
        }
    }

    /**
     * Gets the creation time.
     *
     * @return The creation time
     */
    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    /**
     * Gets the modification time.
     *
     * @return The modification time
     */
    public LocalDateTime getModificationTime() {
        return modificationTime;
    }

    /**
     * Updates the modification time to the current time.
     */
    public void updateModificationTime() {
        this.modificationTime = LocalDateTime.now();
    }

    /**
     * Gets the owner of the node.
     *
     * @return The owner
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Sets the owner of the node.
     *
     * @param owner The new owner
     */
    public void setOwner(String owner) {
        this.owner = owner;
        this.modificationTime = LocalDateTime.now();
    }

    /**
     * Gets the group of the node.
     *
     * @return The group
     */
    public String getGroup() {
        return group;
    }

    /**
     * Sets the group of the node.
     *
     * @param group The new group
     */
    public void setGroup(String group) {
        this.group = group;
        this.modificationTime = LocalDateTime.now();
    }

    /**
     * Gets the permissions of the node.
     *
     * @return The permissions
     */
    public int getPermissions() {
        return permissions;
    }

    /**
     * Sets the permissions of the node.
     *
     * @param permissions The new permissions
     */
    public void setPermissions(int permissions) {
        this.permissions = permissions;
        this.modificationTime = LocalDateTime.now();
    }

    /**
     * Gets the permissions as a string (e.g., "rwxr-xr-x").
     *
     * @return The permissions string
     */
    public String getPermissionsString() {
        StringBuilder sb = new StringBuilder();
        
        // Is this a directory?
        sb.append(isDirectory() ? 'd' : '-');
        
        // Owner permissions
        sb.append((permissions & 0400) != 0 ? 'r' : '-');
        sb.append((permissions & 0200) != 0 ? 'w' : '-');
        sb.append((permissions & 0100) != 0 ? 'x' : '-');
        
        // Group permissions
        sb.append((permissions & 0040) != 0 ? 'r' : '-');
        sb.append((permissions & 0020) != 0 ? 'w' : '-');
        sb.append((permissions & 0010) != 0 ? 'x' : '-');
        
        // Others permissions
        sb.append((permissions & 0004) != 0 ? 'r' : '-');
        sb.append((permissions & 0002) != 0 ? 'w' : '-');
        sb.append((permissions & 0001) != 0 ? 'x' : '-');
        
        return sb.toString();
    }

    /**
     * Checks if this node is a directory.
     *
     * @return true if this is a directory, false otherwise
     */
    public abstract boolean isDirectory();

    /**
     * Checks if this node is a file.
     *
     * @return true if this is a file, false otherwise
     */
    public abstract boolean isFile();

    /**
     * Gets the size of the node in bytes.
     *
     * @return The size in bytes
     */
    public abstract long getSize();
}