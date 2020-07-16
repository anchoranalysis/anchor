/* (C)2020 */
package org.anchoranalysis.image.bean.nonbean.arrangeraster;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Owen Feehan
 * @param <T> item-type
 */
public class TableItemArrangement<T> {

    private List<List<T>> rows;
    private List<List<T>> cols;

    private int numCols;

    /**
     * @author Owen Feehan
     * @param <T> item-type
     */
    public static interface TableCreator<T> {

        boolean hasNext();

        T createNext(int rowPos, int colPos) throws TableItemException;
    }

    // Assumes we will only ever call get() on an index that is one more than
    //  than existing items, so add() is sufficient
    private List<T> getListOrAdd(List<List<T>> list, int index) {

        if (index >= list.size()) {
            List<T> crntList = new ArrayList<>();
            list.add(crntList);
            return crntList;
        } else {
            return list.get(index);
        }
    }

    // Is the cell in use
    public boolean isCellUsed(int rowIndex, int colIndex) {
        if (rowIndex >= rows.size()) {
            return false;
        }
        return colIndex < rows.get(rowIndex).size();
    }

    public T get(int rowIndex, int colIndex) {
        return rows.get(rowIndex).get(colIndex);
    }

    public List<T> getRow(int index) {
        return rows.get(index);
    }

    public List<T> getCol(int index) {
        return cols.get(index);
    }

    public int getNumRowsUsed() {
        return rows.size();
    }

    public int getNumColsUsed() {
        return cols.size();
    }

    public int getRowForListIndex(int index) {
        return index / numCols;
    }

    public int getColForListIndex(int index) {
        return index % numCols;
    }

    public TableItemArrangement(TableCreator<T> rasterIterator, int numRows, int numCols)
            throws TableItemException {

        this.numCols = numCols;

        rows = new ArrayList<>();
        cols = new ArrayList<>();

        for (int crntRow = 0; crntRow < numRows; crntRow++) {
            for (int crntCol = 0; crntCol < numCols; crntCol++) {

                if (!rasterIterator.hasNext()) {
                    return;
                }

                T item = rasterIterator.createNext(crntRow, crntCol);
                assert (item != null);

                List<T> crntRowList = getListOrAdd(rows, crntRow);
                List<T> crntColList = getListOrAdd(cols, crntCol);

                crntRowList.add(item);
                crntColList.add(item);
            }
        }
    }
}
