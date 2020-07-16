/* (C)2020 */
package org.anchoranalysis.io.bean.filepath.generator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.StringJoiner;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.shared.regex.RegEx;
import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.io.filepath.FilePathToUnixStyleConverter;

// Generates an out string where $digit$ is replaced with the #digit group from a regex

/**
 * Generates an outstring of the form
 *
 * <p>group1/group2/group3
 *
 * <p>etc. for as many groups as are found in the regular expression
 *
 * @author Owen Feehan
 */
public class FilePathGeneratorRegExBean extends FilePathGenerator {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private RegEx regEx;
    // END BEAN PROPERTIES

    @Override
    public Path outFilePath(Path pathIn, boolean debugMode) throws AnchorIOException {

        String pathInStr = FilePathToUnixStyleConverter.toStringUnixStyle(pathIn);

        String[] components =
                regEx.match(pathInStr)
                        .orElseThrow(
                                () ->
                                        new AnchorIOException(
                                                String.format(
                                                        "RegEx string '%s' does not match '%s'",
                                                        regEx, pathInStr)));

        return Paths.get(createOutString(components));
    }

    // String in the form g1/g2/g3 for group 1, 2, 3 etc.
    private String createOutString(String[] components) {
        StringJoiner sj = new StringJoiner("/");
        for (int i = 0; i < components.length; i++) {
            sj.add(components[i]);
        }
        return sj.toString();
    }
}
