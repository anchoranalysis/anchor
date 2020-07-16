/* (C)2020 */
package org.anchoranalysis.io.bean.filepath.generator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.io.error.AnchorIOException;

// Generates an out string where $digit$ is replaced with the #digit group from a regex
public class FilePathGeneratorRegEx extends FilePathGenerator {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private String regEx = "";

    @BeanField @Getter @Setter private String outPath = "";
    // END BEAN PROPERTIES

    public static Matcher match(Path pathIn, String regEx) throws OperationFailedException {

        String pathInStr = Utilities.convertBackslashes(pathIn);

        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(pathInStr);

        if (!m.matches()) {
            throw new OperationFailedException(
                    String.format("RegEx string '%s' does not match '%s'", regEx, pathInStr));
        }

        return m;
    }

    @Override
    public Path outFilePath(Path pathIn, boolean debugMode) throws AnchorIOException {

        Matcher m;
        try {
            m = match(pathIn, regEx);
        } catch (OperationFailedException e) {
            throw new AnchorIOException("Cannot match against the regular expression", e);
        }

        String outStr = outPath;

        // We loop through each possible group, and replace if its found, counting down, so that we
        // don't mistake
        // 11 for 1 (for example)
        for (int g = m.groupCount(); g > 0; g--) {
            outStr = outStr.replaceAll("\\$" + Integer.toString(g), m.group(g));
        }

        return Paths.get(outStr);
    }
}
