/* (C)2020 */
package org.anchoranalysis.io.manifest.deserializer.folder;

import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.ITypedGetFromIndex;
import org.anchoranalysis.core.index.container.BoundChangeListener;
import org.anchoranalysis.core.index.container.BoundedIndexContainer;
import org.anchoranalysis.io.manifest.sequencetype.SequenceType;

public class BoundsFromSequenceType<T> implements ITypedGetFromIndex<T>, BoundedIndexContainer<T> {

    private ITypedGetFromIndex<T> typedIndexGetter;

    private SequenceType sequenceType;

    public BoundsFromSequenceType(
            ITypedGetFromIndex<T> typedIndexGetter, SequenceType sequenceType) {
        super();
        this.typedIndexGetter = typedIndexGetter;
        this.sequenceType = sequenceType;
    }

    @Override
    public T get(int index) throws GetOperationFailedException {
        return typedIndexGetter.get(index);
    }

    @Override
    public void addBoundChangeListener(BoundChangeListener cl) {
        // ASSUME STATIC
    }

    @Override
    public int nextIndex(int index) {
        return sequenceType.nextIndex(index);
    }

    @Override
    public int previousIndex(int index) {
        return sequenceType.previousIndex(index);
    }

    @Override
    public int previousEqualIndex(int index) {
        return sequenceType.previousEqualIndex(index);
    }

    @Override
    public int getMinimumIndex() {
        return sequenceType.getMinimumIndex();
    }

    @Override
    public int getMaximumIndex() {
        return sequenceType.getMaximumIndex();
    }
}
