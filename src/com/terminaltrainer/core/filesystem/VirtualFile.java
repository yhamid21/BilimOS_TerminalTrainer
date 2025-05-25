package com.terminaltrainer.core.filesystem;

/**
 * Represents a file in the virtual file system.
 */
public class VirtualFile extends VirtualFileSystemNode {
    private String content;

    /**
     * Creates a new virtual file.
     *
     * @param name The name of the file
     * @param parent The parent directory
     */
    public VirtualFile(String name, VirtualDirectory parent) {
        super(name, parent);
        this.content = "";
    }

    /**
     * Creates a new virtual file with initial content.
     *
     * @param name The name of the file
     * @param parent The parent directory
     * @param content The initial content of the file
     */
    public VirtualFile(String name, VirtualDirectory parent, String content) {
        super(name, parent);
        this.content = content != null ? content : "";
    }

    /**
     * Gets the content of the file.
     *
     * @return The file content
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the content of the file.
     *
     * @param content The new content
     */
    public void setContent(String content) {
        this.content = content != null ? content : "";
        updateModificationTime();
    }

    /**
     * Appends content to the file.
     *
     * @param additionalContent The content to append
     */
    public void appendContent(String additionalContent) {
        if (additionalContent != null) {
            this.content += additionalContent;
            updateModificationTime();
        }
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public boolean isFile() {
        return true;
    }

    @Override
    public long getSize() {
        return content.length();
    }

    @Override
    public String toString() {
        return getName();
    }
}