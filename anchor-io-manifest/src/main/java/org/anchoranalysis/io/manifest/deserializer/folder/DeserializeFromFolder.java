/* (C)2020 */
package org.anchoranalysis.io.manifest.deserializer.folder;

import org.anchoranalysis.core.index.ITypedGetFromIndex;
import org.anchoranalysis.core.index.container.BoundedIndexContainer;
import org.anchoranalysis.io.deserializer.DeserializationFailedException;
import org.anchoranalysis.io.manifest.folder.SequencedFolder;
import org.anchoranalysis.io.manifest.sequencetype.SequenceType;

public abstract class DeserializeFromFolder<T> implements HistoryCreator<T> {

    private SequencedFolder folder;

    public DeserializeFromFolder(SequencedFolder folder) {
        this.folder = folder;
    }

    @Override
    public LoadContainer<T> create() throws DeserializationFailedException {

        SequenceType sequenceType = folder.getAssociatedSequence();
        ITypedGetFromIndex<T> cntr = createCtnr(folder);

        assert (sequenceType != null);

        LoadContainer<T> history = new LoadContainer<>();

        BoundedIndexContainer<T> boundedContainer =
                new BoundsFromSequenceType<>(cntr, sequenceType);

        history.setCntr(boundedContainer);
        history.setExpensiveLoad(false);
        return history;
    }

    protected abstract ITypedGetFromIndex<T> createCtnr(SequencedFolder folder);
}
