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
import org.anchoranalysis.core.cache.WrapOperationWithProgressReporterAsCached;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.image.io.generator.raster.StackGenerator;
import org.anchoranalysis.image.stack.NamedStacks;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.generator.collection.IterableGeneratorOutputHelper;
import org.anchoranalysis.io.output.bean.allowed.OutputAllowed;
import org.anchoranalysis.io.output.bound.BoundIOContext;
import org.anchoranalysis.io.output.bound.BoundOutputManager;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StackCollectionOutputter {

    private static final String OUTPUT_NAME = "stackCollection";
    private static final String PREFIX = "";

    /**
     * Only outputs stacks whose names are allowed by the StackCollection part of the OutputManager
     */
    public static void outputSubset(
            NamedProvider<Stack> stacks,
            String secondLevelOutputKey,
            boolean suppressSubfolders,
            BoundIOContext context) {
        BoundOutputManagerRouteErrors outputManager = context.getOutputManager();

        assert (outputManager.getOutputWriteSettings().hasBeenInit());
        StackCollectionOutputter.output(
                stackSubset(stacks, secondLevelOutputKey, outputManager),
                outputManager.getDelegate(),
                OUTPUT_NAME,
                PREFIX,
                context.getErrorReporter(),
                suppressSubfolders);
    }

    /**
     * Only outputs stacks whose names are allowed by the StackCollection part of the OutputManager
     *
     * @throws OutputWriteFailedException
     */
    public static void outputSubsetWithException(
            NamedProvider<Stack> stacks,
            BoundOutputManagerRouteErrors outputManager,
            String secondLevelOutputKey,
            boolean suppressSubfolders)
            throws OutputWriteFailedException {

        if (!outputManager.getOutputWriteSettings().hasBeenInit()) {
            throw new OutputWriteFailedException(
                    "OutputManager's settings have not yet been initialized");
        }

        StackCollectionOutputter.outputWithException(
                stackSubset(stacks, secondLevelOutputKey, outputManager),
                outputManager.getDelegate(),
                OUTPUT_NAME,
                PREFIX,
                suppressSubfolders);
    }

    public static void output(
            NamedStacks namedCollection,
            BoundOutputManager outputManager,
            String outputName,
            String prefix,
            ErrorReporter errorReporter,
            boolean suppressSubfoldersIn) {
        StackGenerator generator = createStackGenerator();
        IterableGeneratorOutputHelper.output(
                namedCollection,
                generator,
                outputManager,
                outputName,
                prefix,
                errorReporter,
                suppressSubfoldersIn);
    }

    private static void outputWithException(
            NamedStacks namedCollection,
            BoundOutputManager outputManager,
            String outputName,
            String suffix,
            boolean suppressSubfoldersIn)
            throws OutputWriteFailedException {
        StackGenerator generator = createStackGenerator();
        IterableGeneratorOutputHelper.outputWithException(
                namedCollection,
                generator,
                outputManager,
                outputName,
                suffix,
                suppressSubfoldersIn);
    }

    public static NamedStacks subset(
            NamedProvider<Stack> stackCollection, OutputAllowed oa) {

        NamedStacks out = new NamedStacks();

        for (String name : stackCollection.keys()) {

            if (oa.isOutputAllowed(name)) {
                out.addImageStack(name, extractStackCached(stackCollection, name));
            }
        }

        return out;
    }

    private static OperationWithProgressReporter<Stack, OperationFailedException>
            extractStackCached(NamedProvider<Stack> stackCollection, String name) {
        return new WrapOperationWithProgressReporterAsCached<>(
                () -> {
                    try {
                        return stackCollection.getException(name);
                    } catch (NamedProviderGetException e) {
                        throw new OperationFailedException(e);
                    }
                });
    }

    private static StackGenerator createStackGenerator() {
        String manifestFunction = "stackFromCollection";
        return new StackGenerator(true, manifestFunction);
    }

    private static NamedStacks stackSubset(
            NamedProvider<Stack> stacks,
            String secondLevelOutputKey,
            BoundOutputManagerRouteErrors outputManager) {
        return StackCollectionOutputter.subset(
                stacks, outputManager.outputAllowedSecondLevel(secondLevelOutputKey));
    }
}
