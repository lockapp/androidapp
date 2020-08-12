package com.rodrigo.lock.core.archives;


import java.util.Date;

/**
 * Represents an entry of an archive.
 */
public interface ArchiveEntry {

    /**
     * Gets the name of the entry in this archive. May refer to a file or directory or other item.
     * 
     * @return The name of this entry in the archive.
     */
    String getName();

    /**
     * Gets the uncompressed size of this entry. May be -1 (SIZE_UNKNOWN) if the size is unknown
     * 
     * @return the uncompressed size of this entry.
     */
    long getSize();

    /** Special value indicating that the size is unknown */
    long SIZE_UNKNOWN = -1;

    /**
     * Returns true if this entry refers to a directory.
     * 
     * @return true if this entry refers to a directory.
     */
    boolean isDirectory();

    /**
     * Gets the last modified date of this entry.
     * 
     * @return the last modified date of this entry.
     * @since 1.1
     */
    Date getLastModifiedDate();
}