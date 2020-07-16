/* (C)2020 */
package org.anchoranalysis.image.io.input;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.name.store.NamedProviderStore;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.image.stack.NamedImgStackCollection;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.stack.TimeSequence;
import org.anchoranalysis.image.stack.wrap.WrapStackAsTimeSequenceStore;
import org.anchoranalysis.io.input.InputFromManager;

/**
 * Base class for inputs which somehow eventually send up providing stacks, with or without names
 *
 * @author Owen Feehan
 */
public abstract class ProvidesStackInput implements InputFromManager {

    /** Used temporarily to extract a stack via a collection */
    private static final String TEMP_STACK_NAME = "tempName";

    // Adds the current object to a NamedItemStore of ImgStack
    //
    // stackCollection: destination to add to
    // seriesNum: a seriesNum to add
    // t: a timepoint to add
    // progressReporter
    public abstract void addToStore(
            NamedProviderStore<TimeSequence> stackCollection,
            int seriesNum,
            ProgressReporter progressReporter)
            throws OperationFailedException;

    public abstract void addToStoreWithName(
            String name,
            NamedProviderStore<TimeSequence> stackCollection,
            int seriesNum,
            ProgressReporter progressReporter)
            throws OperationFailedException;

    public abstract int numFrames() throws OperationFailedException;

    public Stack extractSingleStack() throws OperationFailedException {
        NamedImgStackCollection store = new NamedImgStackCollection();
        addToStoreWithName(
                TEMP_STACK_NAME,
                new WrapStackAsTimeSequenceStore(store),
                0,
                ProgressReporterNull.get());
        return store.getAsOperation(TEMP_STACK_NAME)
                .get()
                .doOperation(ProgressReporterNull.get()); // NOSONAR
    }
}
