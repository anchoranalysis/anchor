package org.anchoranalysis.image.core.stack;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Date;
import lombok.Getter;
import lombok.Value;

/**
 * Timestamps associated with an image.
 * 
 * @author Owen Feehan
 *
 */
@Value
public class ImageFileTimestamps {
    
    /** The <i>creation</i> timestamp on the file the image was loaded from. */
    @Getter private Date fileCreationTime;
    
    /** The <i>last modified</i> timestamp on the file the image was loaded from. */
    @Getter private Date fileModificationTime;
    
    /** The <i>last access</i> timestamp on the file the image was loaded from. */
    @Getter private Date fileAccessTime;
    
    /**
     * Reads {@link ImageFileTimestamps} from a path.
     * 
     * @param path the path.
     * @return newly created {@link ImageFileTimestamps}.
     * @throws IOException if the timestamps cannot be read.
     */
    public static ImageFileTimestamps fromPath(Path path) throws IOException {
        BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
        return new ImageFileTimestamps(
            convertToDate(attr.creationTime()),
            convertToDate(attr.lastModifiedTime()),
            convertToDate(attr.lastAccessTime())
       );
    }
    
    /** Converts from a {@link FileTime} to a {@link Date}. */
    private static Date convertToDate(FileTime time) {
        return new Date(time.toMillis());
    }
}
