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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.OptionalBean;
import org.anchoranalysis.bean.annotation.Positive;
import org.anchoranalysis.image.bean.nonbean.spatial.arrange.ArrangeStack;
import org.anchoranalysis.image.bean.nonbean.spatial.arrange.ArrangeStackException;
import org.anchoranalysis.image.bean.nonbean.spatial.arrange.BoundingBoxesOnPlane;
import org.anchoranalysis.image.bean.nonbean.spatial.arrange.TableItemArrangement;
import org.anchoranalysis.image.bean.nonbean.spatial.arrange.TableItemException;
import org.anchoranalysis.image.core.stack.rgb.RGBStack;
import org.anchoranalysis.spatial.extent.Extent;
import org.anchoranalysis.spatial.extent.box.BoundingBox;
import org.anchoranalysis.spatial.point.Point3i;

public class Tile extends ArrangeStackBean {

    // START BEAN PROPERTIES
    @BeanField @Positive @Getter @Setter private int numRows = -1;

    @BeanField @Positive @Getter @Setter private int numCols = -1;

    @BeanField @OptionalBean @Getter @Setter private List<Cell> cells = new ArrayList<>();

    @BeanField @Getter @Setter private ArrangeStackBean cellDefault = new Single();
    // END BEAN PROPERTIES

    @AllArgsConstructor
    private class CreateTable implements TableItemArrangement.TableCreator<BoundingBoxesOnPlane> {

        private final Iterator<RGBStack> iterator;

        // We can make this more efficient by using a lookup table for the cells
        // But as there should be relatively few exceptions, we just always loop
        //   through the list
        private ArrangeStack createArrangeRasterForItem(int rowPos, int colPos) {

            if (cells != null) {
                for (Cell cell : cells) {
                    if (cell.getRow() == rowPos && cell.getCol() == colPos) {
                        assert (cell.getArrange() != null);
                        return cell.getArrange();
                    }
                }
            }

            // If there's no explicit cell definition
            return cellDefault;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public BoundingBoxesOnPlane createNext(int rowPos, int colPos) throws TableItemException {
            try {
                return createArrangeRasterForItem(rowPos, colPos)
                        .createBoundingBoxesOnPlane(iterator);
            } catch (ArrangeStackException e) {
                throw new TableItemException(e);
            }
        }
    }

    private static void addShifted(
            Iterable<BoundingBox> src, BoundingBoxesOnPlane dest, int shiftX, int shiftY) {

        // We now loop through each item in the cell, and add to our output set with
        //   the correct offset
        for (BoundingBox box : src) {

            Point3i cornerMin = new Point3i(box.cornerMin());
            cornerMin.incrementX(shiftX);
            cornerMin.incrementY(shiftY);

            dest.add(new BoundingBox(cornerMin, box.extent()));
        }
    }

    private static BoundingBoxesOnPlane createSet(
            TableItemArrangement<BoundingBoxesOnPlane> table, MaxWidthHeight maxWidthHeight) {

        BoundingBoxesOnPlane set =
                new BoundingBoxesOnPlane(
                        new Extent(
                                maxWidthHeight.getTotalWidth(),
                                maxWidthHeight.getTotalHeight(),
                                maxWidthHeight.getMaxZ()));

        // We iterator over every cell in the table
        for (int rowPos = 0; rowPos < table.getNumRowsUsed(); rowPos++) {
            for (int colPos = 0; colPos < table.getNumColsUsed(); colPos++) {

                if (!table.isCellUsed(rowPos, colPos)) {
                    break;
                }

                BoundingBoxesOnPlane boxSet = table.get(rowPos, colPos);

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

    @Override
    public BoundingBoxesOnPlane createBoundingBoxesOnPlane(final Iterator<RGBStack> rasterIterator)
            throws ArrangeStackException {

        try {
            TableItemArrangement<BoundingBoxesOnPlane> table =
                    new TableItemArrangement<>(new CreateTable(rasterIterator), numRows, numCols);

            return createSet(table, new MaxWidthHeight(table));

        } catch (TableItemException e) {
            throw new ArrangeStackException(e);
        }
    }
}
