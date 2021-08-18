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
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.core.exception.OptionalOperationUnsupportedException;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.dimensions.Resolution;
import org.anchoranalysis.image.core.object.properties.ObjectWithProperties;
import org.anchoranalysis.image.voxel.binary.values.BinaryValuesByte;
import org.anchoranalysis.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.overlay.OverlayProperties;
import org.anchoranalysis.spatial.point.Point3d;
import org.anchoranalysis.spatial.point.Point3i;
import org.anchoranalysis.spatial.point.PointConverter;
import org.anchoranalysis.spatial.point.ReadableTuple3i;
import org.anchoranalysis.spatial.scale.ScaleFactor;

public abstract class MarkWithPosition extends Mark implements Serializable {

    /** */
    private static final long serialVersionUID = -6976277985708631268L;

    // START mark state
    @Getter @Setter private Point3d position;
    // END mark state

    // Constructor
    protected MarkWithPosition() {
        this.position = new Point3d();
    }

    // Copy constructor
    protected MarkWithPosition(MarkWithPosition src) {
        super(src);
        this.position = new Point3d(src.position);
    }

    public String strPos() {
        return String.format(
                "[%6.1f,%6.1f,%6.1f]", this.position.x(), this.position.y(), this.position.z());
    }

    @Override
    public void scale(ScaleFactor scaleFactor) throws OptionalOperationUnsupportedException {
        scaleFactor.scale(position);
    }

    @Override
    public Point3d centerPoint() {
        return getPosition();
    }

    // Checks if two marks are equal by comparing all attributes
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

    @Override
    public ObjectWithProperties deriveObject(
            Dimensions bndScene, RegionMembershipWithFlags rm, BinaryValuesByte bvOut) {

        ObjectWithProperties object = super.deriveObject(bndScene, rm, bvOut);
        object.setProperty(
                "midpointInt", calculateRelativePoint(position, object.boundingBox().cornerMin()));
        return object;
    }

    @Override
    public OverlayProperties generateProperties(Optional<Resolution> resolution) {
        OverlayProperties op = super.generateProperties(resolution);

        int dimensions = numberDimensions();

        if (dimensions >= 1) {
            op.addDoubleAsString("Pos X", position.x());
        }
        if (dimensions >= 2) {
            op.addDoubleAsString("Pos Y", position.y());
        }
        if (dimensions >= 3) {
            op.addDoubleAsString("Pos Z", position.z());
        }
        return op;
    }

    /** Calculates a relative-point from pointGlobal to pointBase */
    private static Point3i calculateRelativePoint(Point3d pointGlobal, ReadableTuple3i pointBase) {
        Point3i pointOut = PointConverter.intFromDoubleFloor(pointGlobal);
        pointOut.subtract(pointBase);
        return pointOut;
    }
}
