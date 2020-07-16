/* (C)2020 */
package org.anchoranalysis.io.bean.filepath.generator;

import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.io.error.AnchorIOException;

/**
 * Generates a file-path by replacing all occurrences of a string-pattern with another
 *
 * <p>Paths always use forward-slashes regardless of operating system.
 */
public class FilePathGeneratorReplace extends FilePathGenerator {

    // START BEAN FIELDS
    /** Regular expression to match against string */
    @BeanField @Getter @Setter private String regex;

    /** What to replace in the path */
    @BeanField @Getter @Setter private String replacement;
    // END BEAN FIELDS

    @Override
    public Path outFilePath(Path pathIn, boolean debugMode) throws AnchorIOException {

        String outStr = Utilities.convertBackslashes(pathIn);

        String replaced = outStr.replaceAll(regex, replacement);

        return Paths.get(replaced);
    }
}
