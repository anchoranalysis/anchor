/*-
 * #%L
 * anchor-image-bean
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

package org.anchoranalysis.image.bean.nonbean.spatial.arrange;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Owen Feehan
 * @param <T> item-type
 */
public class TableItemArrangement<T> {

    private List<List<T>> rows;
    private List<List<T>> columns;

    private int numberColumns;

    public TableItemArrangement(TableCreator<T> rasterIterator, int numberRows, int numberCols)
            throws TableItemException {

        this.numberColumns = numberCols;

        rows = new ArrayList<>();
        columns = new ArrayList<>();

        for (int row = 0; row < numberRows; row++) {
            for (int column = 0; column < numberCols; column++) {

                if (!rasterIterator.hasNext()) {
                    return;
                }

                T item = rasterIterator.createNext(row, column);
                assert (item != null);

                List<T> currentRows = getListOrAdd(rows, row);
                List<T> currentColumns = getListOrAdd(columns, column);

                currentRows.add(item);
                currentColumns.add(item);
            }
        }
    }
    
    /**
     * Is a particular cell occupied by an image?
     * 
     * @param rowIndex the row the cell lies on (zero-indexed).
     * @param columnIndex the column the cell lies on (zero-indexed).
     * @return true iff an image occupies the particular cell.
     */
    public boolean isCellUsed(int rowIndex, int columnIndex) {
        if (rowIndex >= rows.size()) {
            return false;
        } else {
        	return columnIndex < rows.get(rowIndex).size();
        }
    }

    public T get(int rowIndex, int columnIndex) {
        return rows.get(rowIndex).get(columnIndex);
    }

    public List<T> getRow(int index) {
        return rows.get(index);
    }

    public List<T> getColumn(int index) {
        return columns.get(index);
    }

    public int getNumberRowsUsed() {
        return rows.size();
    }

    public int getNumberColumnsUsed() {
        return columns.size();
    }

    public int getRowForListIndex(int index) {
        return index / numberColumns;
    }

    public int getColForListIndex(int index) {
        return index % numberColumns;
    }

    // Assumes we will only ever call get() on an index that is one more than
    //  than existing items, so add() is sufficient
    private List<T> getListOrAdd(List<List<T>> list, int index) {

        if (index >= list.size()) {
            List<T> current = new ArrayList<>();
            list.add(current);
            return current;
        } else {
            return list.get(index);
        }
    }
}
