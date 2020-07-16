/* (C)2020 */
package org.anchoranalysis.io.bean.file.matcher;

import java.nio.file.Path;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.io.filepath.FilePathToUnixStyleConverter;
import org.anchoranalysis.io.params.InputContextParams;

/**
 * Predicates that matches a file-path against a regular expression
 *
 * @author Owen Feehan
 */
public class MatchRegEx extends FileMatcher {

    // START BEAN FIELDS
    /**
     * If true, the filter is applied to the path as a whole, not just the filename (using forward
     * slashes as directory seperators)
     */
    @BeanField @Getter @Setter private boolean applyToPath = false;

    @BeanField @Getter @Setter private String expression;
    // END BEAN FIELDS

    @Override
    protected Predicate<Path> createMatcherFile(Path dir, InputContextParams inputContext) {
        if (applyToPath) {
            Pattern pattern = Pattern.compile(expression);
            return p -> acceptPathViaRegEx(p, pattern);
        } else {
            return PathMatcherUtilities.filter(dir, "regex", expression);
        }
    }

    public static boolean acceptPathViaRegEx(Path path, Pattern pattern) {
        return pattern.matcher(pathAsString(path)).matches();
    }

    private static String pathAsString(Path p) {
        return FilePathToUnixStyleConverter.toStringUnixStyle(p.toFile().toString());
    }
}
