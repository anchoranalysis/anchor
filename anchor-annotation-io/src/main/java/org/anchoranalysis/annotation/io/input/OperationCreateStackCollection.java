/* (C)2020 */
package org.anchoranalysis.annotation.io.input;

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.progress.CachedOperationWithProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.image.io.input.ProvidesStackInput;
import org.anchoranalysis.image.stack.NamedImgStackCollection;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.stack.wrap.WrapStackAsTimeSequenceStore;

class OperationCreateStackCollection
        extends CachedOperationWithProgressReporter<NamedProvider<Stack>, CreateException> {

    private ProvidesStackInput inputObject;
    private int seriesNum;
    private int t;

    public OperationCreateStackCollection(ProvidesStackInput inputObject) {
        this(inputObject, 0, 0);
    }

    public OperationCreateStackCollection(ProvidesStackInput inputObject, int seriesNum, int t) {
        super();
        this.inputObject = inputObject;
        this.seriesNum = seriesNum;
        this.t = t;
    }

    @Override
    protected NamedImgStackCollection execute(ProgressReporter progressReporter)
            throws CreateException {
        try {
            return doOperationWithException(progressReporter);
        } catch (OperationFailedException e) {
            throw new CreateException(e);
        }
    }

    private NamedImgStackCollection doOperationWithException(ProgressReporter progressReporter)
            throws OperationFailedException {
        NamedImgStackCollection stackCollection = new NamedImgStackCollection();
        inputObject.addToStore(
                new WrapStackAsTimeSequenceStore(stackCollection, t), seriesNum, progressReporter);
        return stackCollection;
    }
}
