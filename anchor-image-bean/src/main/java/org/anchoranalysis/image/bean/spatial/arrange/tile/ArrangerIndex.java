package org.anchoranalysis.image.bean.spatial.arrange.tile;

import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.anchoranalysis.image.bean.spatial.arrange.StackArranger;

/**
 * A {@link StackArranger} for each cell in a table.
 *
 * <p><>A specific {@link StackArranger} may be defined for each cell, otherwise {@code cellDefault}
 * is used.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
class ArrangerIndex {

    // START REQUIRED ARGUMENTS
    /**
     * Defines the corresponding {@link StackArranger} for each individual cell in table.
     *
     * <p>Each cell should be specified zero or one times, via a reference to the corresponding row
     * and column.
     *
     * <p>If a particular cell is unspecified, {@code cellDefault} is used.
     */
    private final List<Cell> cells;

    /**
     * Used to define an individual cell, when no specific entry is found in {@code cells} for a
     * particular cell.
     */
    private final StackArranger cellDefault;
    // END REQUIRED ARGUMENTS

    /**
     * Gets a {@link StackArranger} corresponding to a particular cell in a table.
     *
     * @param row the index of the row in the table (zero-indexed).
     * @param column the index of the column in the table (zero-indexed).
     * @return the corresponding {@link StackArranger}.
     */
    public StackArranger getForCell(int row, int column) {

        // This can be made more efficient by using a lookup table for the cells.
        // But as there should be relatively few exceptions, we just always loop through the
        // list.
        for (Cell cell : cells) {
            Optional<StackArranger> arrangeStack = cell.ifPositionMatches(row, column);
            if (arrangeStack.isPresent()) {
                return arrangeStack.get();
            }
        }

        // If there's no explicit cell definition
        return cellDefault;
    }
}
