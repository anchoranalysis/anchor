/* (C)2020 */
package org.anchoranalysis.io.bean.file.matcher;

import java.nio.file.Path;
import java.util.Set;
import java.util.function.Predicate;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.OptionalBean;
import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.io.params.InputContextParams;
import org.apache.commons.io.FilenameUtils;

/**
 * Maybe imposes a file-extension condition, optionally on top of an existing matcher
 *
 * <p>The extensions are always checked in a case-insensitive manner
 *
 * @author Owen Feehan
 */
public class MatchExtensions extends FileMatcher {

    // START BEAN PROPERTIES
    @BeanField @OptionalBean @Getter @Setter private FileMatcher matcher;

    /**
     * A set of file-extensions (without the period), one of which must match the end of a path.
     *
     * <p>If an empty set is passed then, no check occurs, and no extension is checked
     *
     * <p>If null, then a default set is populated from the inputContext
     */
    @BeanField @OptionalBean @Getter @Setter private Set<String> extensions;
    // END BEAN PROPERTIES

    @Override
    protected Predicate<Path> createMatcherFile(Path dir, InputContextParams inputContext)
            throws AnchorIOException {

        Set<String> fileExtensions = fileExtensions(inputContext);

        if (matcher != null) {
            Predicate<Path> firstPred = matcher.createMatcherFile(dir, inputContext);
            return path -> firstPred.test(path) && matchesAnyExtension(path, fileExtensions);
        } else {
            return path -> matchesAnyExtension(path, fileExtensions);
        }
    }

    // Does a path end with at least one of the extensions? Or fileExtensions are empty.
    private boolean matchesAnyExtension(Path path, Set<String> fileExtensions) {

        if (fileExtensions.isEmpty()) {
            // Note SPECIAL CASE. When empty, the check isn't applied, so is always TRUE
            return true;
        }

        // Extract extension from path
        return fileExtensions.contains(FilenameUtils.getExtension(path.toString()).toLowerCase());
    }

    private Set<String> fileExtensions(InputContextParams inputContext) {
        if (extensions != null) {
            return extensions;
        } else {
            return inputContext.getInputFilterExtensions();
        }
    }
}
