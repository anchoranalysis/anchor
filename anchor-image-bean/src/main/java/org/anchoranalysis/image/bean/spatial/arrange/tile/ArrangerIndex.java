package org.anchoranalysis.image.bean.spatial.arrange.tile;

import java.util.List;
import java.util.function.ToIntFunction;
import org.anchoranalysis.image.bean.nonbean.spatial.arrange.ArrangeStackException;
import org.anchoranalysis.image.bean.spatial.arrange.StackArranger;
import org.anchoranalysis.spatial.box.Extent;

/**
 * A {@link StackArranger} for each cell in a table.
 *
 * <p><>A specific {@link StackArranger} may be defined for each cell, otherwise {@code cellDefault}
 * is used.
 *
 * @author Owen Feehan
 */
class ArrangerIndex {

    /**
     * Defines the corresponding {@link StackArranger} for an individual cell in table.
     *
     * <p>It is indexed by first rows and then columns.
     */
    private final Cell[][] index;

    /** Defines any cell, when no specific entry is found in {@code cells} for a particular cell. */
    private final StackArranger cellDefault;

    /**
     * Create from a list of {@link Cell}s.
     *
     * @param cells defines the corresponding {@link StackArranger} for an individual cell in table.
     * @param cellDefault defines any cell, when no specific entry is found in {@code cells} for a
     *     particular cell.
     * @param tableSize the table size, in columns (x-dimension) and rows (y-dimension).
     * @throws ArrangeStackException if a particular cell is referenced more than once in {@code
     *     cells} or contains an invalid index.
     */
    public ArrangerIndex(List<Cell> cells, StackArranger cellDefault, Extent tableSize)
            throws ArrangeStackException {

        index = new Cell[tableSize.y()][tableSize.x()];

        for (Cell cell : cells) {
            checkDimension(cell, tableSize, Cell::getRow, Extent::y, "row");
            checkDimension(cell, tableSize, Cell::getColumn, Extent::x, "column");

            if (index[cell.getRow()][cell.getColumn()] == null) {
                index[cell.getRow()][cell.getColumn()] = cell;
            } else {
                throw new ArrangeStackException(
                        String.format(
                                "Duplicated cell entries exist for the cell with column=%d and row=%d",
                                cell.getColumn(), cell.getRow()));
            }
        }
        this.cellDefault = cellDefault;
    }

    /**
     * Gets a {@link StackArranger} corresponding to a particular cell in a table.
     *
     * @param column the index of the column in the table (zero-indexed).
     * @param row the index of the row in the table (zero-indexed).
     * @return the corresponding {@link StackArranger}.
     */
    public StackArranger getForCell(int column, int row) {
        Cell cell = index[row][column];

        if (cell != null) {
            return cell.getArrange();
        } else {
            // If there's no explicit cell definition
            return cellDefault;
        }
    }

    /**
     * Checks that the cell coordinates along a particular dimension lie within the expected range.
     */
    private static void checkDimension(
            Cell cell,
            Extent tableSize,
            ToIntFunction<Cell> fromCell,
            ToIntFunction<Extent> fromExtent,
            String entityName)
            throws ArrangeStackException {
        int dimensionValue = fromCell.applyAsInt(cell);
        if (dimensionValue < 0 || dimensionValue >= fromExtent.applyAsInt(tableSize)) {
            throw new ArrangeStackException(
                    String.format(
                            "A cell exists with an invalid %s: %d", entityName, dimensionValue));
        }
    }
}
