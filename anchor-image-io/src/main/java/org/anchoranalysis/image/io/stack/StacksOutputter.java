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
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
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
import org.anchoranalysis.io.output.outputter.OutputterChecked;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StacksOutputter {

    public static final String OUTPUT_STACKS = "stacks";
    
    private static final String PREFIX = "";

    /**
     * Only outputs stacks whose names are allowed by the output-manager - and logs if anything goes
     * wrong
     */
    public static void outputSubset(
            NamedProvider<Stack> stacks,
            boolean suppressSubfolders,
            InputOutputContext context) {
        Outputter outputter = context.getOutputter();
        
        if (outputter.outputsEnabled().isOutputEnabled(StacksOutputter.OUTPUT_STACKS)) {
            StacksOutputter.output(
                    stackSubset(stacks, OUTPUT_STACKS, outputter),
                    outputter.getChecked(),
                    OUTPUT_STACKS,
                    PREFIX,
                    context.getErrorReporter(),
                    suppressSubfolders);
        }
    }

    /**
     * Only outputs stacks whose names are allowed by the output-manager - and throws an exception
     * if anything goes wrong
     *
     * @throws OutputWriteFailedException if anything goes wrong
     */
    public static void outputSubsetWithException(
            NamedProvider<Stack> stacks,
            Outputter outputter,
            boolean suppressSubfolders)
            throws OutputWriteFailedException {
        
        if (outputter.outputsEnabled().isOutputEnabled(StacksOutputter.OUTPUT_STACKS)) {
            StacksOutputter.outputWithException(
                    stackSubset(stacks, StacksOutputter.OUTPUT_STACKS, outputter),
                    outputter.getChecked(),
                    StacksOutputter.OUTPUT_STACKS,
                    PREFIX,
                    suppressSubfolders);
        }
    }
    
    public static void output(
            NamedStacks stacks,
            OutputterChecked outputter,
            String outputName,
            String prefix,
            ErrorReporter errorReporter,
            boolean suppressSubfoldersIn) {
        StackGenerator generator = createStackGenerator();
        GeneratorOutputHelper.output(
                stacks,
                generator,
                outputter,
                outputName,
                prefix,
                errorReporter,
                suppressSubfoldersIn);
    }
    
    private static void outputWithException(
            NamedStacks stacks,
            OutputterChecked outputter,
            String outputName,
            String suffix,
            boolean suppressSubfoldersIn)
            throws OutputWriteFailedException {
        StackGenerator generator = createStackGenerator();
        GeneratorOutputHelper.outputWithException(
                stacks, generator, outputter, outputName, suffix, suppressSubfoldersIn);
    }

    public static NamedStacks subset(NamedProvider<Stack> stackCollection, SingleLevelOutputEnabled outputEnabled) {

        NamedStacks out = new NamedStacks();

        for (String name : stackCollection.keys()) {

            if (outputEnabled.isOutputEnabled(name)) {
                out.add(name, extractStackCached(stackCollection, name));
            }
        }

        return out;
    }

    private static StoreSupplier<Stack> extractStackCached(
            NamedProvider<Stack> stackCollection, String name) {
        return StoreSupplier.cache(
                () -> {
                    try {
                        return stackCollection.getException(name);
                    } catch (NamedProviderGetException e) {
                        throw new OperationFailedException(e);
                    }
                });
    }

    private static StackGenerator createStackGenerator() {
        return new StackGenerator(true, "stackFromCollection", false);
    }

    private static NamedStacks stackSubset(
            NamedProvider<Stack> stacks, String secondLevelOutputKey, Outputter outputter) {
        return StacksOutputter.subset(
                stacks, outputter.outputsEnabled().second(secondLevelOutputKey));
    }
}
