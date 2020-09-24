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

import java.util.Collection;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.functional.function.CheckedSupplier;
import org.anchoranalysis.io.generator.Generator;
import org.anchoranalysis.io.generator.sequence.CollectionGenerator;
import org.anchoranalysis.io.output.bound.BoundOutputManager;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.writer.WritableItem;
import org.anchoranalysis.io.output.writer.Writer;
import org.anchoranalysis.io.output.writer.WriterRouterErrors;

/**
 * Writes a collection of items via a generator into a subfolder.
 * 
 * @author Owen Feehan
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GeneratorSubfolderWriter {

    public static <T> void writeSubfolder(
            BoundOutputManager outputManager,
            String outputNameFolder,
            String outputNameSubfolder,
            Generator<T> generatorIterable,
            Collection<T> collection,
            boolean checkIfAllowed)
            throws OutputWriteFailedException {
        extractWriter(outputManager, checkIfAllowed)
                .writeSubdirectoryWithGenerator(
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
            CheckedSupplier<Generator<T>, OutputWriteFailedException> generatorIterable,
            Collection<T> collection,
            boolean checkIfAllowed) {
        extractWriter(outputManager, checkIfAllowed)
                .writeSubfolderWithGenerator(
                        outputNameSubfolder,
                        () ->
                                createOutputWriter(
                                        collection,
                                        outputNameFolder,
                                        generatorIterable.get(),
                                        outputManager.getDelegate(),
                                        checkIfAllowed));
    }

    private static Writer extractWriter(BoundOutputManager outputManager, boolean checkIfAllowed) {
        if (checkIfAllowed) {
            return outputManager.getWriters().checkIfAllowed();
        } else {
            return outputManager.getWriters().alwaysAllowed();
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
            Generator<T> generatorIterable,
            BoundOutputManager outputManager,
            boolean checkIfAllowed) {
        return new CollectionGenerator<>(
                outputNameFolder, generatorIterable, outputManager, 3, checkIfAllowed, collection);
    }
}
