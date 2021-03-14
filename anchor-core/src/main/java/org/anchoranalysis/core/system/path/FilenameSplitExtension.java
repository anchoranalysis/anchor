package org.anchoranalysis.core.system.path;

import java.io.File;
import java.util.Optional;
import lombok.Value;

/**
 * A file-name with the base (without extension) split from the extension.
 *
 * <p>A period between the base and the extension exists in the filename, but this belongs to
 * neither component.
 *
 * @author Owen Feehan
 */
@Value
public class FilenameSplitExtension {

    /** The part of the filename without an extension. */
    private String baseName;

    /** The extension of the filename. */
    private Optional<String> extension;

    /**
     * Creates from a path without an extension.
     *
     * @param pathWithoutExtension a path without an extension (any directory components are
     *     ignored)
     * @param extension the extension.
     */
    public FilenameSplitExtension(String pathWithoutExtension, Optional<String> extension) {
        this.baseName = new File(pathWithoutExtension).getName();
        this.extension = extension;
    }

    /**
     * Inserts a suffix after the base-name, but before the extension, immutably.
     *
     * @param suffix the suffix to insert
     * @return the full filename with an extension inserted after the basename, but before the
     *     extension
     */
    public String combineWithSuffix(String suffix) {
        String withSuffix = baseName + suffix;
        if (extension.isPresent()) {
            return withSuffix + "." + extension;
        } else {
            return withSuffix;
        }
    }
}
