/*-
 * #%L
 * anchor-mpp
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

package org.anchoranalysis.mpp.mark;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.core.exception.CheckedUnsupportedOperationException;
import org.anchoranalysis.spatial.point.Point3d;
import org.anchoranalysis.spatial.scale.ScaleFactor;

/** An abstract base class for marks that have a position in 3D space. */
public abstract class MarkWithPosition extends Mark implements Serializable {

    private static final long serialVersionUID = -6976277985708631268L;

    /** The position of the mark in 3D space. */
    @Getter @Setter private Point3d position;

    /** Constructs a new MarkWithPosition with a default position at the origin. */
    protected MarkWithPosition() {
        this.position = new Point3d();
    }

    /**
     * Copy constructor for MarkWithPosition.
     *
     * @param src the MarkWithPosition to copy
     */
    protected MarkWithPosition(MarkWithPosition src) {
        super(src);
        this.position = new Point3d(src.position);
    }

    /**
     * Describes the position of the mark as a string.
     *
     * @return the string.
     */
    protected String positionString() {
        return String.format(
                "[%6.1f,%6.1f,%6.1f]", this.position.x(), this.position.y(), this.position.z());
    }

    @Override
    public void scale(ScaleFactor scaleFactor) throws CheckedUnsupportedOperationException {
        scaleFactor.scale(position);
    }

    @Override
    public Point3d centerPoint() {
        return getPosition();
    }

    /**
     * Checks if this mark is equal to another mark by comparing all attributes.
     *
     * @param m the mark to compare with
     * @return true if the marks are equal, false otherwise
     */
    @Override
    public boolean equalsDeep(Mark m) {
        if (!super.equalsDeep(m)) {
            return false;
        }

        if (!(m instanceof MarkWithPosition)) {
            return false;
        }

        MarkWithPosition trgt = (MarkWithPosition) m;
        return position.equals(trgt.position);
    }

    // Overridden methods are left without individual JavaDoc comments as requested
}
