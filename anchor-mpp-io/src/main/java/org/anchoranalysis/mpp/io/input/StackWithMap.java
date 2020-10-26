/*-
 * #%L
 * anchor-mpp-io
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

package org.anchoranalysis.mpp.io.input;

import java.nio.file.Path;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.identifier.provider.store.NamedProviderStore;
import org.anchoranalysis.core.identifier.provider.store.StoreSupplier;
import org.anchoranalysis.core.log.error.ErrorReporter;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.image.core.stack.TimeSequence;
import org.anchoranalysis.image.io.stack.input.ProvidesStackInput;

/** Combines a Stack with a map of other stacks */
@RequiredArgsConstructor
public class StackWithMap implements MultiInputSubMap<TimeSequence> {

    // START REQUIRED ARGUMENTS
    /** Needed for getting main-stack */
    private final String mainObjectName;

    private final ProvidesStackInput mainInputObject;
    // END REQUIRED ARGUMENTS

    // Where the other stacks are stored
    private OperationMap<TimeSequence> map = new OperationMap<>();

    @Override
    public void addToStore(NamedProviderStore<TimeSequence> stackCollection)
            throws OperationFailedException {
        addToStore(stackCollection, 0, ProgressReporterNull.get());
    }

    public void addToStore(
            NamedProviderStore<TimeSequence> stackCollection,
            int seriesNum,
            ProgressReporter progressReporter)
            throws OperationFailedException {

        // We add the main object
        mainInputObject.addToStoreWithName(
                mainObjectName, stackCollection, seriesNum, progressReporter);

        // We add the other objects
        map.addToStore(stackCollection);
    }

    @Override
    public void add(String name, StoreSupplier<TimeSequence> op) {
        map.add(name, op);
    }

    public void close(ErrorReporter errorReporter) {
        mainInputObject.close(errorReporter);
        map = null;
    }

    public String getMainObjectName() {
        return mainObjectName;
    }

    public String inputName() {
        return mainInputObject.name();
    }

    public Optional<Path> pathForBinding() {
        return mainInputObject.pathForBinding();
    }

    public int numFrames() throws OperationFailedException {
        return mainInputObject.numberFrames();
    }

    @Override
    public StoreSupplier<TimeSequence> get(String name) throws OperationFailedException {

        if (name.equals(mainObjectName)) {
            throw new OperationFailedException("Retrieving the main-object name is not allowed");
        }

        return map.get(name);
    }
}
