/*-
 * #%L
 * anchor-image-bean
 * %%
 * Copyright (C) 2010 - 2022 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.image.bean.spatial.tile;

import static org.anchoranalysis.image.bean.spatial.arrange.ColoredDualStackTester.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.anchoranalysis.image.bean.nonbean.spatial.arrange.ArrangeStackException;
import org.anchoranalysis.image.bean.spatial.arrange.ColoredDualStackTester;
import org.anchoranalysis.image.bean.spatial.arrange.DualStacks;
import org.anchoranalysis.image.bean.spatial.arrange.overlay.Overlay;
import org.anchoranalysis.image.bean.spatial.arrange.tile.Cell;
import org.anchoranalysis.image.bean.spatial.arrange.tile.Tile;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.box.Extent;
import org.anchoranalysis.spatial.point.Point3i;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link Tile}.
 *
 * @author Owen Feehan
 */
class TileTest {

    /**
     * Arranges the big and small stacks in a <b>horizontal row</b> - with fully defined number of
     * rows and columns.
     */
    @Test
    void testHorizontal() throws ArrangeStackException {
        BoundingBox box =
                BoundingBox.createReuse(
                        new Point3i(DualStacks.SIZE_BIG.x(), 0, 0),
                        new Extent(
                                DualStacks.SIZE_SMALL.x(),
                                DualStacks.SIZE_BIG.y(),
                                DualStacks.SIZE_BIG.z()));

        testTwoCells(new Tile(2, 1), "leftCell_", "rightCell_", box);
    }

    /** Arranges a single cell that overlays the big and small stacks in a single cell. */
    @Test
    void testSingleCellOverlay() throws ArrangeStackException {
        Tile tile = createWithCellEntry(0, 0);
        testSingleCell(tile, CYAN, MAGENTA, CYAN, false);
    }

    /**
     * Arranges the big and small stacks in a <b>vertical column</b> - with fully defined number of
     * rows and columns.
     */
    @Test
    void testVertical() throws ArrangeStackException {
        BoundingBox box =
                BoundingBox.createReuse(
                        new Point3i(0, DualStacks.SIZE_BIG.y(), 0),
                        new Extent(
                                DualStacks.SIZE_BIG.y(),
                                DualStacks.SIZE_SMALL.y(),
                                DualStacks.SIZE_BIG.z()));

        testTwoCells(new Tile(1, 2), "topCell_", "bottomCell_", box);
    }

    /** Tests with an overlay with multiple z-slices. */
    @Test
    void testInvalidCell() throws ArrangeStackException {
        assertThrows(
                ArrangeStackException.class,
                () -> ColoredDualStackTester.combine(createWithCellEntry(0, 1), false));
    }

    /**
     * Creates with a single-cell-sized table, and an {@link Overlay} entry for a particular cell.
     */
    private static Tile createWithCellEntry(int rowInCell, int columnInCell) {
        Tile tile = new Tile(1, 1);
        tile.getCells().add(new Cell(new Overlay(), rowInCell, columnInCell));
        return tile;
    }
}
