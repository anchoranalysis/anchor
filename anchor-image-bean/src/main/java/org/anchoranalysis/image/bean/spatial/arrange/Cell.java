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

import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.NonNegative;
import org.anchoranalysis.image.core.stack.RGBStack;

/**
 * Indicates how to arrange one or more {@link RGBStack}s in a cell in a table.
 * 
 * <p>See {@link Tile} for the higher-level structure that uses this entity.
 * 
 * @author Owen Feehan
 *
 */
public class Cell extends AnchorBean<Cell> {

    // START BEAN PROPERTIES
	/** How to arrange any {@link RGBStack}s in this particular cell. */
    @BeanField @Getter @Setter private StackArranger arrange;

    /** The row in the table that the cell refers to (zero-indexed). */
    @BeanField @NonNegative @Getter @Setter private int row;

    /** The column in the table that the cell refers to (zero-indexed). */
    @BeanField @NonNegative @Getter @Setter private int column;
    // END BEAN PROPERTIES
    
    /**
     * Returns the object's {@code arrange} if {@code rowToMatch} and {@code columnToMatch} are identical to the current cell.
     *  
     * @param rowToMatch the index of the row to be matched (zero-indexed).
     * @param columnToMatch the index of the column to be matched (zero-indexed).
     * @return the value of {@code arrange} if the positions match, otherwise {@link Optional#empty()}.
     */
    public Optional<StackArranger> ifPositionMatches(int rowToMatch, int columnToMatch) {
    	if (row == rowToMatch && column == columnToMatch) {
            return Optional.of(arrange);
        } else {
        	return Optional.empty();
        }
    }
}
