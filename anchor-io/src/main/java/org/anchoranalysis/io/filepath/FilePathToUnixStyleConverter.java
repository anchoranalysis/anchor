/* (C)2020 */
package org.anchoranalysis.io.filepath;

import java.nio.file.Path;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.io.FilenameUtils;

/**
 * Conversion to Unix-style separators
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FilePathToUnixStyleConverter {

    /**
     * Converts a path to a string using Unix-style separators (forward-slashes)
     *
     * @param path with either Windows-style (blackslashes) or Unix-style separators (forward
     *     slashes)
     * @return identical path but with Unix-style separators
     */
    public static String toStringUnixStyle(Path path) {
        return toStringUnixStyle(path.toString());
    }

    /**
     * Converts a path to a string using Unix-style separators (forward-slashes)
     *
     * @param path with either Windows-style (blackslashes) or Unix-style separators (forward
     *     slashes)
     * @return identical path but with Unix-style separators
     */
    public static String toStringUnixStyle(String path) {
        return FilenameUtils.separatorsToUnix(path);
    }
}
