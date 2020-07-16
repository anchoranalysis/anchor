/* (C)2020 */
package org.anchoranalysis.io.bean.filepath.generator;

import java.nio.file.Path;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class Utilities {

    /** Converts backslashes to forward slashes in a path, and returns as a string */
    public static String convertBackslashes(Path pathIn) {
        return pathIn.toString().replace('\\', '/');
    }
}
