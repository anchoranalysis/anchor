/* (C)2020 */
package org.anchoranalysis.annotation.io.mark;

import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class TempPathCreator {

    public static Path deriveTempPath(Path annotationPath) {
        String fileName = annotationPath.getFileName().toString();
        Path fileNameNew = Paths.get(fileName + ".temp");
        return annotationPath.resolveSibling(fileNameNew);
    }
}
