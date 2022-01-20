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

package org.anchoranalysis.image.bean.spatial.arrange.tile;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import org.anchoranalysis.core.time.OperationContext;
import org.anchoranalysis.image.bean.nonbean.spatial.arrange.ArrangeStackException;
import org.anchoranalysis.image.bean.nonbean.spatial.arrange.StackArrangement;
import org.anchoranalysis.image.bean.spatial.arrange.StackArranger;
import org.anchoranalysis.spatial.box.Extent;

/**
 * A {@link StackArrangement} for each cell in a table.
 *
 * <p>All cells must have a single associated {@link StackArrangement}.
 *
 * @author Owen Feehan
 */
class ArrangementIndex {

    private List<List<StackArrangement>> rows;
    private List<List<StackArrangement>> columns;

    /**
     * Create from a {@code Iterator<RGBStack>}.
     *
     * <p>If the iterator has no more stacks available, the table ends without processing further
     * entities.
     *
     * @param extents the respective sizes to arrange.
     * @param arrangers a {@link StackArranger} for each cell in the table.
     * @param tableSize the table size, in columns (x-dimension) and rows (y-dimension).
     * @param context objects for the operation.
     * @throws ArrangeStackException if the {@code stacks} and {@code arrangers} do not match
     *     expectations, or otherwise an error occurs.
     */
    public ArrangementIndex(
            Iterator<Extent> extents,
            ArrangerIndex arrangers,
            Extent tableSize,
            OperationContext context)
            throws ArrangeStackException {

        rows = new ArrayList<>();
        columns = new ArrayList<>();

        for (int row = 0; row < tableSize.y(); row++) {
            for (int column = 0; column < tableSize.x(); column++) {

                if (!extents.hasNext()) {
                    // Exit early if there are no more stacks to iterate.
                    return;
                }

                StackArrangement item =
                        arrangers.getForCell(column, row).arrangeStacks(extents, context);

                List<StackArrangement> currentRows = getListOrAdd(rows, row);
                List<StackArrangement> currentColumns = getListOrAdd(columns, column);

                currentRows.add(item);
                currentColumns.add(item);
            }
        }
    }

    /**
     * Is a particular cell occupied by an image?
     *
     * @param columnIndex the column the cell lies on (zero-indexed).
     * @param rowIndex the row the cell lies on (zero-indexed).
     * @return true iff an image occupies the particular cell.
     */
    public boolean isCellUsed(int columnIndex, int rowIndex) {
        if (rowIndex >= rows.size()) {
            return false;
        } else {
            return columnIndex < rows.get(rowIndex).size();
        }
    }

    public StackArrangement get(int columnIndex, int rowIndex) {
        return rows.get(rowIndex).get(columnIndex);
    }

    /**
     * Get all {@link StackArrangement} for a particular <i>row</i> of cells.
     *
     * @param index the index of the row (zero-indexed).
     * @return all the {@link StackArrangement}s associated with the row.
     */
    public Stream<StackArrangement> getRow(int index) {
        return rows.get(index).stream();
    }

    /**
     * Get all {@link StackArrangement} for a particular <i>column</i> of cells.
     *
     * @param index the index of the column (zero-indexed).
     * @return all the {@link StackArrangement}s associated with the column.
     */
    public Stream<StackArrangement> getColumn(int index) {
        return columns.get(index).stream();
    }

    /**
     * The number of rows in the table.
     *
     * @return the number of rows.
     */
    public int getNumberRows() {
        return rows.size();
    }

    /**
     * The number of columns in the table.
     *
     * @return the number of columns.
     */
    public int getNumberColumns() {
        return columns.size();
    }

    private static <T> List<T> getListOrAdd(List<List<T>> list, int index) {
        // Assumes we will only ever call get() on an index that is one more than
        //  than existing items, so add() is sufficient

        if (index >= list.size()) {
            List<T> current = new ArrayList<>();
            list.add(current);
            return current;
        } else {
            return list.get(index);
        }
    }
}
