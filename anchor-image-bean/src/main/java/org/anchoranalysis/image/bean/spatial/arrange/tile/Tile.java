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

import com.github.davidmoten.guavamini.Preconditions;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.UnaryOperator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.Positive;
import org.anchoranalysis.image.bean.nonbean.spatial.arrange.ArrangeStackException;
import org.anchoranalysis.image.bean.nonbean.spatial.arrange.StackArrangement;
import org.anchoranalysis.image.bean.spatial.arrange.Single;
import org.anchoranalysis.image.bean.spatial.arrange.StackArranger;
import org.anchoranalysis.image.bean.spatial.arrange.align.BoxAligner;
import org.anchoranalysis.image.bean.spatial.arrange.align.Center;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.box.Extent;

/**
 * A higher-level aggregate structure that arranges other {@link StackArranger}s in a tabular
 * pattern.
 *
 * <p>The table is defined by a number of rows and columns.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor
public class Tile extends StackArranger {

    // START BEAN PROPERTIES
    /** The number of <i>columns</i> to use in the table produced when tiling. */
    @BeanField @Positive @Getter @Setter private int numberColumns = 1;

    /** The number of <i>rows</i> to use in the table produced when tiling. */
    @BeanField @Positive @Getter @Setter private int numberRows = 1;

    /**
     * Defines the corresponding {@link StackArranger} for an individual cell in table.
     *
     * <p>Each cell should be specified zero or one times, via a reference to the corresponding row
     * and column.
     *
     * <p>If a particular cell is unspecified, {@code cellDefault} is used.
     */
    @BeanField @Getter @Setter private List<Cell> cells = new ArrayList<>();

    /**
     * Used to define an individual cell, when no specific entry is found in {@code cells} for a
     * particular cell.
     */
    @BeanField @Getter @Setter private StackArranger cellDefault = new Single();

    /** How to align a smaller image inside a larger cell. */
    @BeanField @Getter @Setter private BoxAligner aligner = new Center();
    // END BEAN PROPERTIES

    /**
     * Creates for a particular number of columns and rows.
     *
     * @param numberColumns the number of <i>columns</i> to use in the table produced when tiling.
     * @param numberRows the number of <i>rows</i> to use in the table produced when tiling.
     */
    public Tile(int numberColumns, int numberRows) {
        Preconditions.checkArgument(numberColumns > 0);
        Preconditions.checkArgument(numberRows > 0);
        this.numberColumns = numberColumns;
        this.numberRows = numberRows;
    }

    @Override
    public StackArrangement arrangeStacks(final Iterator<Extent> extents)
            throws ArrangeStackException {

        Extent tableSize = new Extent(numberColumns, numberRows, 1);

        ArrangerIndex arrangers = new ArrangerIndex(cells, cellDefault, tableSize);

        ArrangementIndex table = new ArrangementIndex(extents, arrangers, tableSize);

        return createArrangement(table, new CellSizeCalculator(table));
    }

    private StackArrangement createArrangement(
            ArrangementIndex table, CellSizeCalculator cellSizes) {

        StackArrangement arrangement = new StackArrangement(cellSizes.total());

        // We iterator over every cell in the table
        for (int row = 0; row < table.getNumberRows(); row++) {
            for (int column = 0; column < table.getNumberColumns(); column++) {

                if (table.isCellUsed(column, row)) {

                    StackArrangement stacksInCell = table.get(column, row);

                    BoundingBox cell = cellSizes.cell(column, row);
                    addAll(stacksInCell, arrangement, box -> aligner.align(box, cell));
                }
            }
        }
        return arrangement;
    }

    /**
     * Add all {@link BoundingBox}es in {@code source} to {@code destination} after two additional
     * shifts.
     */
    private static void addAll(
            Iterable<BoundingBox> source,
            StackArrangement destination,
            UnaryOperator<BoundingBox> mapBox) {

        // We now loop through each item in the cell, and add to our output set with
        //   the correct offset
        for (BoundingBox box : source) {
            assert (destination.extent().contains(box));
            assert (!box.extent().anyDimensionIsLargerThan(box.extent()));
            destination.add(mapBox.apply(box));
        }
    }
}
