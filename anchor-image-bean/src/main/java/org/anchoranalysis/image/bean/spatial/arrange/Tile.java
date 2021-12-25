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

package org.anchoranalysis.image.bean.spatial.arrange;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.Positive;
import org.anchoranalysis.image.bean.nonbean.spatial.arrange.ArrangeStackException;
import org.anchoranalysis.image.bean.nonbean.spatial.arrange.StackArrangement;
import org.anchoranalysis.image.bean.nonbean.spatial.arrange.TableCreator;
import org.anchoranalysis.image.bean.nonbean.spatial.arrange.TableItemArrangement;
import org.anchoranalysis.image.bean.nonbean.spatial.arrange.TableItemException;
import org.anchoranalysis.image.core.stack.RGBStack;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.box.Extent;
import org.anchoranalysis.spatial.point.Point3i;

/**
 * A higher-level aggregate structure that arranges other {@link StackArranger}s in a tabular pattern.
 * 
 * <p>The table is defined by a number of rows and columns.
 * 
 * @author Owen Feehan
 *
 */
public class Tile extends StackArranger {

    @AllArgsConstructor
    private class CreateTable implements TableCreator<StackArrangement> {

        private final Iterator<RGBStack> iterator;

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public StackArrangement createNext(int rowPos, int colPos) throws TableItemException {
            try {
                return createArrangeRasterForItem(rowPos, colPos)
                        .arrangeStacks(iterator);
            } catch (ArrangeStackException e) {
                throw new TableItemException(e);
            }
        }

        private StackArranger createArrangeRasterForItem(int row, int column) {

            // This can be made more efficient by using a lookup table for the cells.
            // But as there should be relatively few exceptions, we just always loop through the list.
        	
            if (cells != null) {
            	for( Cell cell : cells) {
            		Optional<StackArranger> arrangeStack = cell.ifPositionMatches(row, column);
            		if (arrangeStack.isPresent()) {
                		return arrangeStack.get();
                	}
            	}
            }

            // If there's no explicit cell definition
            return cellDefault;
        }
    }
    
    // START BEAN PROPERTIES
    /**
     * The number of <i>rows</i> to use in the table produced when tiling.
     *
     * <p>If this value is set to {@code -1} it is automatically set.
     */
    @BeanField @Positive @Getter @Setter private int numberRows = -1;

    /**
     * The number of <i>columns</i> to use in the table produced when tiling.
     *
     * <p>If this value is set to {@code -1} it is automatically set.
     */
    @BeanField @Positive @Getter @Setter private int numberColumns = -1;

    /**
     * Defines the corresponding {@link StackArranger} for each individual cell in table.
     * 
     * <p>Each cell should be specified zero or one times, via a reference to the corresponding row and column.
     * 
     * <p>If a particular cell is unspecified, {@code cellDefault} is used.
     */
    @BeanField @Getter @Setter private List<Cell> cells = new ArrayList<>();

    /**
     * Used to define an individual cell, if no specific entry is found in {@code cells} for a particular cell. 
     */
    @BeanField @Getter @Setter private StackArranger cellDefault = new Single();
    // END BEAN PROPERTIES
    
    @Override
    protected StackArrangement arrangeStacks(final Iterator<RGBStack> stacks)
            throws ArrangeStackException {

        try {
            TableItemArrangement<StackArrangement> table =
                    new TableItemArrangement<>(
                            new CreateTable(stacks), numberRows, numberColumns);

            return createSet(table, new MaxWidthHeight(table));

        } catch (TableItemException e) {
            throw new ArrangeStackException(e);
        }
    }

    private static void addShifted(
            Iterable<BoundingBox> src, StackArrangement dest, int shiftX, int shiftY) {

        // We now loop through each item in the cell, and add to our output set with
        //   the correct offset
        for (BoundingBox box : src) {

            Point3i cornerMin = new Point3i(box.cornerMin());
            cornerMin.incrementX(shiftX);
            cornerMin.incrementY(shiftY);

            dest.add(new BoundingBox(cornerMin, box.extent()));
        }
    }

    private static StackArrangement createSet(
            TableItemArrangement<StackArrangement> table, MaxWidthHeight maxWidthHeight) {

        StackArrangement set =
                new StackArrangement(
                        new Extent(
                                maxWidthHeight.getTotalWidth(),
                                maxWidthHeight.getTotalHeight(),
                                maxWidthHeight.getMaxZ()));

        // We iterator over every cell in the table
        for (int rowPos = 0; rowPos < table.getNumberRowsUsed(); rowPos++) {
            for (int colPos = 0; colPos < table.getNumberColumnsUsed(); colPos++) {

                if (!table.isCellUsed(rowPos, colPos)) {
                    break;
                }

                StackArrangement boxSet = table.get(rowPos, colPos);

                int rowHeight = maxWidthHeight.getMaxHeightForRow(rowPos);
                int colWidth = maxWidthHeight.getMaxWidthForCol(colPos);

                int rowX = maxWidthHeight.sumWidthBeforeCol(colPos);
                int rowY = maxWidthHeight.sumHeightBeforeRow(rowPos);

                int x = rowX + ((colWidth - boxSet.extent().x()) / 2); // We center
                int y = rowY + ((rowHeight - boxSet.extent().y()) / 2); // We center

                addShifted(boxSet, set, x, y);
            }
        }
        return set;
    }
}
