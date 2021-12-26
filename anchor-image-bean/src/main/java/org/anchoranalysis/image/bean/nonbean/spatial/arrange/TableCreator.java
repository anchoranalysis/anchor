package org.anchoranalysis.image.bean.nonbean.spatial.arrange;

public interface TableCreator<T> {

    boolean hasNext();

    T createNext(int rowPos, int colPos) throws TableItemException;
}