/*-
 * #%L
 * anchor-io
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

package org.anchoranalysis.io.input.bean.path.matcher;

import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.OptionalBean;
import org.anchoranalysis.core.system.path.ExtensionUtilities;
import org.anchoranalysis.io.input.InputContextParams;
import org.anchoranalysis.io.input.InputReadFailedException;

/**
 * Maybe imposes a file-extension condition, optionally on top of an existing matcher
 *
 * <p>The extensions are always checked in a case-insensitive manner
 *
 * @author Owen Feehan
 */
public class MatchExtensions extends PathMatcher {

    // START BEAN PROPERTIES
    @BeanField @OptionalBean @Getter @Setter private PathMatcher matcher;

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
    protected Predicate<Path> createMatcherFile(
            Path directory, Optional<InputContextParams> inputContext)
            throws InputReadFailedException {

        Set<String> fileExtensions = fileExtensions(inputContext);

        if (matcher != null) {
            Predicate<Path> firstPred = matcher.createMatcherFile(directory, inputContext);
            return path -> firstPred.test(path) && matchesAnyExtension(path, fileExtensions);
        } else {
            return path -> matchesAnyExtension(path, fileExtensions);
        }
    }

    // Does a path end with at least one of the extensions? Or fileExtensions are empty.
    private boolean matchesAnyExtension(Path path, Set<String> fileExtensions) {

        if (fileExtensions.isEmpty()) {
            // Note SPECIAL CASE. When empty, the check isn't applied, so is always true
            return true;
        }

        // Extract extension from path
        Optional<String> extension = ExtensionUtilities.extractExtension(path);
        if (extension.isPresent()) {
            return fileExtensions.contains(extension.get().toLowerCase());
        } else {
            return false;
        }
    }

    private Set<String> fileExtensions(Optional<InputContextParams> inputContext)
            throws InputReadFailedException {
        if (extensions != null) {
            return extensions;
        } else if (inputContext.isPresent()) {
            return inputContext.get().getInputFilterExtensions();
        } else {
            throw new InputReadFailedException(
                    "Either a set of extensions must be specified in the bean, or via an input-context, but neither has been specified.");
        }
    }
}
