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
import java.util.function.Predicate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.AllowEmpty;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.io.input.InputContextParams;
import org.anchoranalysis.io.input.InputReadFailedException;

@NoArgsConstructor
@AllArgsConstructor
public class MatchGlob extends PathMatcher {

    // START BEAN FIELDS
    /**
     * The string describing a glob e.g. "*.jpg". If empty, then the inputFilterGlob from
     * inputContext is used
     */
    @BeanField @AllowEmpty @Getter @Setter private String glob = "";
    // END BEAN FIELDS

    @Override
    protected Predicate<Path> createMatcherFile(
            Path directory, Optional<InputContextParams> inputContext)
            throws InputReadFailedException {
        return FilterPathHelper.createPredicate(directory, "glob", globString(inputContext));
    }

    private String globString(Optional<InputContextParams> inputContext)
            throws InputReadFailedException {
        if (!glob.isEmpty()) {
            return glob;
        } else if (inputContext.isPresent()) {
            return inputContext.get().getInputFilterGlob();
        } else {
            throw new InputReadFailedException(
                    "Neither a glob is defined as a bean-field, nor as part of the input-context params. At least one must be defined.");
        }
    }
}
