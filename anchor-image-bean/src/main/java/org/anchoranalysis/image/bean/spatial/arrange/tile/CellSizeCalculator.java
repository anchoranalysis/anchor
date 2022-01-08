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

import java.util.function.IntFunction;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;
import org.anchoranalysis.core.exception.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.image.bean.nonbean.spatial.arrange.StackArrangement;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.box.Extent;
import org.anchoranalysis.spatial.point.Point3i;

/**
 * Calculates the size and positions of each cell in a {@code
 * TableItemArrangement<StackArrangement>}.
 *
 * @author Owen Feehan
 */
class CellSizeCalculator {

    /** The height of each respective row. */
    private SizesAcrossDimension rowHeights;

    /** The width of each respective column. */
    private SizesAcrossDimension columnWidths;

    /** The maximum coordinate encountered in the z-dimension. */
    private int maxZ;

    /**
     * Create from a particular tabular arrangement.
     *
     * @param table the tabular arrangement.
     */
    public CellSizeCalculator(ArrangementIndex table) {

        maxZ = 0;

        // Process all the rows
        rowHeights = processDimension(table.getNumberRows(), table::getRow, Extent::y);

        // Process all the columns
        columnWidths = processDimension(table.getNumberColumns(), table::getColumn, Extent::x);
    }

    /**
     * The position and size of a cell in the table.
     *
     * @param columnIndex the index of the column of the cell (zero-indexed).
     * @param rowIndex the index of the row of the cell (zero-indexed).
     * @return a bounding-box describing the corner of the cell, and its corresponding size.
     */
    public BoundingBox cell(int columnIndex, int rowIndex) {
        SizeAtPoint width = columnWidths.get(columnIndex);
        SizeAtPoint height = rowHeights.get(rowIndex);
        Point3i cornerPoint = new Point3i(width.getPoint(), height.getPoint(), 0);
        Extent cellSize = new Extent(width.getSize(), height.getSize(), maxZ);
        return BoundingBox.createReuse(cornerPoint, cellSize);
    }

    /**
     * Exposes the total width, height, depth of all cells as an {@link Extent}.
     *
     * @return the total size across the three dimensions as a newly created {@link Extent}.
     */
    public Extent total() {

        if (columnWidths.getSum() != 0 && rowHeights.getSum() != 0 && maxZ != 0) {
            return new Extent(columnWidths.getSum(), rowHeights.getSum(), maxZ);
        } else {
            throw new AnchorFriendlyRuntimeException(
                    String.format(
                            "At least one dimension is zero: %d, %d, %d",
                            columnWidths.getSum(), rowHeights.getSum(), maxZ));
        }
    }

    /**
     * Processes a single dimension, either a row or a column, to determine the position and size of
     * each cell.
     *
     * <p>The {@code maxZ} variable is updated with the maximum size encountered in the z-dimension.
     *
     * @param numberElements the total number of elements in the dimension (e.g. total number of
     *     rows or columns).
     * @param elementsForIndex extracts an element for a particular index.
     * @param extractSize extracts a size for the particular dimension.
     * @return a newly created {@link SizesAcrossDimension}, representing the sizes across the
     *     dimension.
     */
    private SizesAcrossDimension processDimension(
            int numberElements,
            IntFunction<Stream<StackArrangement>> elementsForIndex,
            ToIntFunction<Extent> extractSize) {
        SizesAcrossDimension sizes = new SizesAcrossDimension(numberElements);
        for (int i = 0; i < numberElements; i++) {
            Stream<StackArrangement> elements = elementsForIndex.apply(i);
            sizes.add(maxSizeForDimension(elements, extractSize));
        }
        return sizes;
    }

    /** Finds the maximum value of particular dimension of a cell. */
    private int maxSizeForDimension(
            Stream<StackArrangement> cells, ToIntFunction<Extent> extractDimension) {
        // Assumes dim are the same for all channels
        return cells.map(StackArrangement::extent)
                .mapToInt(extent -> extractDimensionAndUpdateZ(extent, extractDimension))
                .max()
                .getAsInt();
    }

    /**
     * Extracts a particular dimension from an {@link Extent} and simultaneously updates {@code
     * maxZ} if needed.
     */
    private int extractDimensionAndUpdateZ(Extent extent, ToIntFunction<Extent> extractDimension) {

        if (extent.z() > maxZ) {
            maxZ = extent.z();
        }

        return extractDimension.applyAsInt(extent);
    }
}
