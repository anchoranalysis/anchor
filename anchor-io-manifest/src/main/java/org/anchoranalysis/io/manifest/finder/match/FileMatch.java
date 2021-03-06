/*-
 * #%L
 * anchor-io-manifest
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

package org.anchoranalysis.io.manifest.finder.match;

import java.util.Optional;
import java.util.function.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.file.OutputtedFile;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileMatch {

    /**
     * Match a file whose manifest-description has <b>either of two specific function and a specific
     * type <u>and</u> whose output-name matches a specific value</b>.
     *
     * @param function1 a possible matching function-value
     * @param function2 another possible matching function-value
     * @param type the matching type-value
     * @param outputName the output-name
     * @return a predicate that matches files that fulfills the condition.
     */
    public static Predicate<OutputtedFile> descriptionAndOutputName(
            String function1, String function2, String type, String outputName) {
        return description(function1, function2, type).and(outputName(outputName));
    }

    /**
     * Match a file whose <b>output-name matches a specific value</b>.
     *
     * @param outputName the output-name
     * @return a predicate that matches files that fulfills the condition.
     */
    public static Predicate<OutputtedFile> outputName(String outputName) {
        return directory -> directory.getOutputName().equals(outputName);
    }

    /**
     * Match a file whose manifest-description has a specific <b>function and type</b>.
     *
     * @param function the matching function-value
     * @param type the matching type-value
     * @return a predicate that matches files that fulfills the condition.
     */
    public static Predicate<OutputtedFile> description(String function, String type) {
        return description(DescriptionMatch.functionAndType(function, type));
    }

    /**
     * Match a file whose manifest-description has a specific <b>function</b>.
     *
     * @param function the matching function-value
     * @return a predicate that matches files that fulfills the condition.
     */
    public static Predicate<OutputtedFile> description(String function) {
        return description(DescriptionMatch.function(function));
    }

    /**
     * Match a file whose manifest-description has <b>either of two specific function and a specific
     * type</b>.
     *
     * @param function1 a possible matching function-value
     * @param function2 another possible matching function-value
     * @param type the matching type-value
     * @return a predicate that matches files that fulfills the condition.
     */
    public static Predicate<OutputtedFile> description(
            String function1, String function2, String type) {
        return description(DescriptionMatch.eitherFunctionAndType(function1, function2, type));
    }

    private static Predicate<OutputtedFile> description(
            Predicate<ManifestDescription> manifestDescription) {
        return file -> nonNullAnd(file, manifestDescription);
    }

    private static boolean nonNullAnd(
            OutputtedFile file, Predicate<ManifestDescription> predicateManifestDescription) {
        Optional<ManifestDescription> description = file.getDescription();
        return description.isPresent() && predicateManifestDescription.test(description.get());
    }
}
