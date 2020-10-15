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
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.PointConverter;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.dimensions.Dimensions;
import org.anchoranalysis.image.dimensions.Resolution;
import org.anchoranalysis.image.object.properties.ObjectWithProperties;
import org.anchoranalysis.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.overlay.OverlayProperties;

public abstract class MarkWithPosition extends Mark implements Serializable {

    /** */
    private static final long serialVersionUID = -6976277985708631268L;

    // TODO rename pos to position, but this will likely break annotations, so be careful
    // START mark state
    @Getter @Setter private Point3d pos;
    // END mark state

    // Constructor
    public MarkWithPosition() {
        super();
        this.pos = new Point3d();
    }

    // Copy constructor
    public MarkWithPosition(MarkWithPosition src) {
        super(src);
        this.pos = new Point3d(src.pos);
    }

    public String strPos() {
        return String.format("[%6.1f,%6.1f,%6.1f]", this.pos.x(), this.pos.y(), this.pos.z());
    }

    @Override
    public void scale(double scaleFactor) {
        scaleXYPoint(this.pos, scaleFactor);
    }

    public static void scaleXYPoint(Point3d point, double multFactor) {
        point.setX(point.x() * multFactor);
        point.setY(point.y() * multFactor);
    }

    @Override
    public Point3d centerPoint() {
        return getPos();
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
        return pos.equals(trgt.pos);
    }

    @Override
    public ObjectWithProperties deriveObject(
            Dimensions bndScene, RegionMembershipWithFlags rm, BinaryValuesByte bvOut) {

        ObjectWithProperties object = super.deriveObject(bndScene, rm, bvOut);
        object.setProperty(
                "midpointInt", calculateRelativePoint(pos, object.boundingBox().cornerMin()));
        return object;
    }

    @Override
    public OverlayProperties generateProperties(Optional<Resolution> resolution) {
        OverlayProperties op = super.generateProperties(resolution);

        int dimensions = numberDimensions();

        if (dimensions >= 1) {
            op.addDoubleAsString("Pos X", pos.x());
        }
        if (dimensions >= 2) {
            op.addDoubleAsString("Pos Y", pos.y());
        }
        if (dimensions >= 3) {
            op.addDoubleAsString("Pos Z", pos.z());
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
