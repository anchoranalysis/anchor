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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import java.util.Optional;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.core.name.store.StoreSupplier;
import org.anchoranalysis.image.io.generator.raster.StackGenerator;
import org.anchoranalysis.image.stack.NamedStacks;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.generator.collection.GeneratorOutputHelper;
import org.anchoranalysis.io.output.enabled.single.SingleLevelOutputEnabled;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.outputter.InputOutputContext;
import org.anchoranalysis.io.output.outputter.Outputter;

/**
 * Outputs a named-set of stacks, performing appropriate checks on what is enabled or not.
 * 
 * @author Owen Feehan
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StacksOutputter {

    private static final String MANIFEST_FUNCTION = "stackFromCollection";

    /**
     * Writes all or a subset from a set of named-stacks to a directory as a raster.
     * 
     * <p>A second-level output manager filters which stacks are written. 
     * 
     * @param stacks the stacks to output (or a subset thereof according to the second-level output manager)
     * @param outputName name to use for the directory, for checking if it is allowed, and for the second-level outputs
     * @param suppressSubfolders if true, a separate subdirectory is not created, and rather the outputs occur in the parent directory.
     * @param context determines where and how the outputting occurs
     * @throws OutputWriteFailedException if the output cannot be written. 
     */
    public static void output(
            NamedProvider<Stack> stacks, String outputName, boolean suppressSubfolders, InputOutputContext context)
            throws OutputWriteFailedException {

        if (context.getOutputter().outputsEnabled().isOutputEnabled(outputName)) {
            outputAfterSubsetting(
                    stackSubset(stacks, outputName, context.getOutputter()),
                    context,
                    outputName,
                    suppressSubfolders);
        }
    }

    private static void outputAfterSubsetting(
            NamedStacks stacks,
            InputOutputContext context,
            String outputName,
            boolean suppressSubfoldersIn)
            throws OutputWriteFailedException {
        
        GeneratorOutputHelper.output(
                stacks, createStackGenerator(), context, outputName, "", suppressSubfoldersIn);
    }

    
    private static NamedStacks subset(
            NamedProvider<Stack> stacks, SingleLevelOutputEnabled outputEnabled) {

        NamedStacks out = new NamedStacks();

        for (String name : stacks.keys()) {

            if (outputEnabled.isOutputEnabled(name)) {
                out.add(name, extractStackCached(stacks, name));
            }
        }

        return out;
    }
    
    private static StoreSupplier<Stack> extractStackCached(
            NamedProvider<Stack> stacks, String name) {
        return StoreSupplier.cache(
                () -> {
                    try {
                        return stacks.getException(name);
                    } catch (NamedProviderGetException e) {
                        throw new OperationFailedException(e);
                    }
                });
    }

    private static StackGenerator createStackGenerator() {
        return new StackGenerator(true, Optional.of(MANIFEST_FUNCTION), false);
    }

    private static NamedStacks stackSubset(
            NamedProvider<Stack> stacks, String secondLevelOutputKey, Outputter outputter) {
        return StacksOutputter.subset(
                stacks, outputter.outputsEnabled().second(secondLevelOutputKey));
    }
}
