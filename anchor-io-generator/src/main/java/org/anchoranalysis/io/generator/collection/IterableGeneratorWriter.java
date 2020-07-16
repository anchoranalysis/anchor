/* (C)2020 */
package org.anchoranalysis.io.generator.collection;

import java.util.Collection;
import org.anchoranalysis.core.functional.Operation;
import org.anchoranalysis.io.generator.IterableGenerator;
import org.anchoranalysis.io.generator.sequence.CollectionGenerator;
import org.anchoranalysis.io.output.bound.BoundOutputManager;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.writer.WritableItem;
import org.anchoranalysis.io.output.writer.Writer;
import org.anchoranalysis.io.output.writer.WriterRouterErrors;

public class IterableGeneratorWriter {

    private IterableGeneratorWriter() {}

    public static <T> void writeSubfolder(
            BoundOutputManager outputManager,
            String outputNameFolder,
            String outputNameSubfolder,
            IterableGenerator<T> generatorIterable,
            Collection<T> collection,
            boolean checkIfAllowed)
            throws OutputWriteFailedException {
        extractWriter(outputManager, checkIfAllowed)
                .writeSubfolder(
                        outputNameSubfolder,
                        () ->
                                createOutputWriter(
                                        collection,
                                        outputNameFolder,
                                        generatorIterable,
                                        outputManager,
                                        checkIfAllowed));
    }

    public static <T> void writeSubfolder(
            BoundOutputManagerRouteErrors outputManager,
            String outputNameFolder,
            String outputNameSubfolder,
            Operation<IterableGenerator<T>, OutputWriteFailedException> generatorIterable,
            Collection<T> collection,
            boolean checkIfAllowed) {
        extractWriter(outputManager, checkIfAllowed)
                .writeSubfolder(
                        outputNameSubfolder,
                        () ->
                                createOutputWriter(
                                        collection,
                                        outputNameFolder,
                                        generatorIterable.doOperation(),
                                        outputManager.getDelegate(),
                                        checkIfAllowed));
    }

    private static Writer extractWriter(BoundOutputManager outputManager, boolean checkIfAllowed) {
        if (checkIfAllowed) {
            return outputManager.getWriterCheckIfAllowed();
        } else {
            return outputManager.getWriterAlwaysAllowed();
        }
    }

    private static WriterRouterErrors extractWriter(
            BoundOutputManagerRouteErrors outputManager, boolean checkIfAllowed) {
        if (checkIfAllowed) {
            return outputManager.getWriterCheckIfAllowed();
        } else {
            return outputManager.getWriterAlwaysAllowed();
        }
    }

    private static <T> WritableItem createOutputWriter(
            Collection<T> collection,
            String outputNameFolder,
            IterableGenerator<T> generatorIterable,
            BoundOutputManager outputManager,
            boolean checkIfAllowed) {
        return new CollectionGenerator<>(
                collection, outputNameFolder, generatorIterable, outputManager, 3, checkIfAllowed);
    }
}
