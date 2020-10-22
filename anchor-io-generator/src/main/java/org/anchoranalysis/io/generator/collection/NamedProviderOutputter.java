/*-
 * #%L
 * anchor-io-generator
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

package org.anchoranalysis.io.generator.collection;

import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.exception.combinable.AnchorCombinableException;
import org.anchoranalysis.core.exception.friendly.HasFriendlyErrorMessage;
import org.anchoranalysis.core.identifier.provider.NamedProvider;
import org.anchoranalysis.core.identifier.provider.NamedProviderGetException;
import org.anchoranalysis.io.generator.Generator;
import org.anchoranalysis.io.generator.sequence.OutputSequenceFactory;
import org.anchoranalysis.io.generator.sequence.OutputSequenceIndexed;
import org.anchoranalysis.io.generator.sequence.pattern.OutputPatternStringSuffix;
import org.anchoranalysis.io.output.enabled.single.SingleLevelOutputEnabled;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.outputter.OutputterChecked;

/**
 * Outputs entities from a {@link NamedProvider} into a directory using the names of each entity for
 * a corresponding generated file.
 *
 * @author Owen Feehan
 * @param <T> element-type in {@link Generator} and in {@link NamedProvider}.
 */
@AllArgsConstructor
public class NamedProviderOutputter<T> {

    /**
     * The {@link NamedProvider} whose entities (or a subselection thereof) will be written to the
     * file-system.
     */
    private NamedProvider<T> provider;

    /** The generator to be repeatedly called for writing each element in the sequence. */
    private Generator<T> generator;

    /**
     * The root directory where writing occurs to, and in which the sub-directories are created, if
     * enabled.
     */
    private OutputterChecked outputter;

    /**
     * Outputs the entities using a particular output-name.
     *
     * @param outputName the output-name to use, which also determines the subdirectory name if it
     *     is not suppressed.
     * @param suppressSubdirectory if true, a separate subdirectory is not created, and rather the
     *     outputs occur in the parent directory.
     * @throws OutputWriteFailedException if any output cannot be written.
     */
    public void output(String outputName, boolean suppressSubdirectory)
            throws OutputWriteFailedException {

        if (provider.isEmpty() || !outputter.getOutputsEnabled().isOutputEnabled(outputName)) {
            return;
        }

        Set<String> allowedKeys =
                subset(provider.keys(), outputter.getOutputsEnabled().second(outputName));

        // If no outputs are allowed, exit early
        if (!allowedKeys.isEmpty()) {
            outputAllowed(allowedKeys, new OutputPatternStringSuffix(outputName, suppressSubdirectory));
        }
    }

    private void outputAllowed(Set<String> allowedKeys, OutputPatternStringSuffix sequenceDirectory)
            throws OutputWriteFailedException {

        OutputSequenceFactory<T> factory = new OutputSequenceFactory<>(generator, outputter);

        OutputSequenceIndexed<T, String> writer = factory.withoutOrder(sequenceDirectory);
        for (String key : allowedKeys) {
            try {
                // Determine what file extension will be used for the particular file
                String extension = ".tif";
                
                writer.add(provider.getException(key), key + extension);
            } catch (NamedProviderGetException e) {
                throwExceptionInWriter(e, key);
            }
        }
    }

    private static Set<String> subset(Set<String> keys, SingleLevelOutputEnabled outputEnabled) {
        return keys.stream()
                .filter(outputEnabled::isOutputEnabled)
                .collect(Collectors.toCollection(TreeSet::new));
    }

    private static void throwExceptionInWriter(Exception e, String name)
            throws OutputWriteFailedException {

        String errorMessage = String.format("An error occurred outputting %s", name);

        if (e instanceof HasFriendlyErrorMessage) {
            HasFriendlyErrorMessage eCast = (HasFriendlyErrorMessage) e;
            throw new OutputWriteFailedException(errorMessage, eCast);
        }

        if (e instanceof AnchorCombinableException) {
            AnchorCombinableException eCast = (AnchorCombinableException) e;
            throw new OutputWriteFailedException(errorMessage, eCast);
        }

        throw new OutputWriteFailedException(errorMessage + ":" + e);
    }
}
