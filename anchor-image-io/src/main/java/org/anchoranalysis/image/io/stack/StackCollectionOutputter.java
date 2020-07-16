/* (C)2020 */
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
import org.anchoranalysis.image.stack.NamedImgStackCollection;
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
            NamedImgStackCollection namedCollection,
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
            NamedImgStackCollection namedCollection,
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

    public static NamedImgStackCollection subset(
            NamedProvider<Stack> stackCollection, OutputAllowed oa) {

        NamedImgStackCollection out = new NamedImgStackCollection();

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

    private static NamedImgStackCollection stackSubset(
            NamedProvider<Stack> stacks,
            String secondLevelOutputKey,
            BoundOutputManagerRouteErrors outputManager) {
        return StackCollectionOutputter.subset(
                stacks, outputManager.outputAllowedSecondLevel(secondLevelOutputKey));
    }
}
