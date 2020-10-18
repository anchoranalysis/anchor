/*-
 * #%L
 * anchor-image-io
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
package org.anchoranalysis.image.io.stack;

import java.util.Optional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.io.generator.raster.StackGenerator;
import org.anchoranalysis.io.generator.collection.NamedProviderOutputter;
import org.anchoranalysis.io.generator.sequence.OutputSequence;
import org.anchoranalysis.io.generator.sequence.OutputSequenceFactory;
import org.anchoranalysis.io.generator.sequence.OutputSequenceIncrementing;
import org.anchoranalysis.io.generator.sequence.OutputSequenceIndexed;
import org.anchoranalysis.io.generator.sequence.pattern.OutputPatternIntegerSuffix;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.outputter.OutputterChecked;

/**
 * Creates {@link OutputSequence} of different kinds for writing stacks to a directory.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OutputSequenceStackFactory {

    /** A factory with no restrictions on what kind of stacks can be outputted. */
    public static final OutputSequenceStackFactory NO_RESTRICTIONS =
            new OutputSequenceStackFactory(new StackGenerator(false, Optional.empty(), false));

    /**
     * The stacks that are outputted are guaranteed to be two-dimensional.
     *
     * @param manifestFunction the manifest-function to use when outputting.
     * @return a newly created factory
     */
    public static OutputSequenceStackFactory always2D(String manifestFunction) {
        return new OutputSequenceStackFactory(new StackGenerator(manifestFunction, true));
    }

    /**
     * The stacks that are outputted are guaranteed to be two-dimensional.
     *
     * @param manifestFunction the manifest-function to use when outputting.
     * @return a newly created factory
     */
    public static OutputSequenceStackFactory withManifestFunction(String manifestFunction) {
        return new OutputSequenceStackFactory(new StackGenerator(manifestFunction, false));
    }

    /** The generator to be repeatedly called for writing each element in the sequence. */
    private final StackGenerator generator;

    /**
     * Creates an sequence of stacks in a subdirectory with a number in the outputted file name that
     * increments each time by one.
     *
     * @param subdirectoryName the name of the subdirectory in which stacks will be written
     *     (relative to {@code context}.
     * @param outputter determines where and how the outputting occurs
     * @return the created output-sequence (and started)
     * @throws OutputWriteFailedException
     */
    public OutputSequenceIncrementing<Stack> incrementingByOne(
            String subdirectoryName, OutputterChecked outputter) throws OutputWriteFailedException {
        return new OutputSequenceFactory<>(generator, outputter)
                .incrementingByOne(new OutputPatternIntegerSuffix(subdirectoryName, true));
    }

    /**
     * Creates a sequence of stacks in the current context's directory that has no pattern.
     *
     * @param outputter determines where and how the outputting occurs
     * @return the created output-sequence (and started)
     * @throws OutputWriteFailedException
     */
    public OutputSequenceIndexed<Stack, String> withoutOrderCurrentDirectory(
            String outputName, OutputterChecked outputter) throws OutputWriteFailedException {
        return new OutputSequenceFactory<>(generator, outputter)
                .withoutOrderCurrentDirectory(outputName);
    }

    /**
     * Creates a sequence of stacks in the current context's directory with a number in the
     * outputted file name that increments each time by one.
     *
     * @param outputter determines where and how the outputting occurs
     * @return the created output-sequence (and started)
     * @throws OutputWriteFailedException
     */
    public OutputSequenceIncrementing<Stack> incrementingByOneCurrentDirectory(
            String outputName, String prefix, int numberDigits, OutputterChecked outputter)
            throws OutputWriteFailedException {
        return new OutputSequenceFactory<>(generator, outputter)
                .incrementingByOneCurrentDirectory(outputName, prefix, numberDigits);
    }

    /**
     * Writes all or a subset from a set of named-stacks to a directory.
     *
     * <p>A second-level output manager filters which stacks are written.
     *
     * @param stacks the stacks to output (or a subset thereof according to the second-level output
     *     manager)
     * @param outputName name to use for the directory, for checking if it is allowed, and for the
     *     second-level outputs
     * @param suppressSubdirectory if true, a separate subdirectory is not created, and rather the
     *     outputs occur in the parent directory.
     * @param outputter determines where and how the outputting occurs
     * @throws OutputWriteFailedException if any output cannot be written.
     */
    public void withoutOrderSubset(
            NamedProvider<Stack> stacks,
            String outputName,
            boolean suppressSubdirectory,
            OutputterChecked outputter)
            throws OutputWriteFailedException {

        new NamedProviderOutputter<>(stacks, generator, outputter)
                .output(outputName, suppressSubdirectory);
    }
}
