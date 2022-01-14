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

package org.anchoranalysis.image.bean.spatial.arrange.overlay;

import java.util.Iterator;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.image.bean.nonbean.spatial.arrange.ArrangeStackException;
import org.anchoranalysis.image.bean.nonbean.spatial.arrange.StackArrangement;
import org.anchoranalysis.image.bean.spatial.arrange.Single;
import org.anchoranalysis.image.bean.spatial.arrange.StackArranger;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.box.Extent;
import org.anchoranalysis.spatial.point.Point3i;

/**
 * Overlays one image on the other.
 *
 * <p>
 *
 * <ul>
 *   <li><b>first</b> image passed is assumed to be the source.
 *   <li><b>second</b> image passed is assumed to be the overlay.
 * </ul>
 *
 * <p>We have no Z implemented yet, so we always overlay at z position 0.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor
@AllArgsConstructor
public class Overlay extends StackArranger {

    // START: strings used for position choices
    private static final String LEFT = "left";
    private static final String RIGHT = "right";
    private static final String TOP = "top";
    private static final String CENTER = "center";
    private static final String BOTTOM = "bottom";
    // END: strings used for position choices

    /**
     * The choice which will cause a single-sliced overlay to be duplicated across the z-dimension
     * to match the z-size onto which it is projected.
     */
    private static final String REPEAT = "repeat";

    private static final PositionChoices CHOICES_X = new PositionChoices(LEFT, CENTER, RIGHT);
    private static final PositionChoices CHOICES_Y = new PositionChoices(TOP, CENTER, BOTTOM);
    private static final PositionChoices CHOICES_Z =
            new PositionChoices(BOTTOM, CENTER, TOP, Optional.of(REPEAT));

    private static final Single SINGLE = new Single();

    // START BEAN PROPERTIES
    /**
     * Indicates how to align the image across the <b>X-axis</b> (i.e. horizontally): one of {@code
     * left, right, center}.
     */
    @BeanField @Getter @Setter private String alignX = LEFT;

    /**
     * Indicates how to align the image across the <b>Y-axis</b> (i.e. vertically): one of {@code
     * top, bottom, center}.
     */
    @BeanField @Getter @Setter private String alignY = TOP;

    /**
     * Indicates how to align the image across the <b>Z-axis</b>: one of {@code top, bottom, center,
     * repeat}.
     *
     * <p>{@code repeat} is a special-case where a single z-slice overlay will be duplicated across
     * the z-dimension of the stack onto which it is overlayed.
     */
    @BeanField @Getter @Setter private String alignZ = BOTTOM;
    // END BEAN PROPERTIES

    @Override
    public String describeBean() {
        return getBeanName();
    }

    @Override
    public StackArrangement arrangeStacks(Iterator<Extent> extents) throws ArrangeStackException {

        if (!extents.hasNext()) {
            throw new ArrangeStackException("No image in iterator for source");
        }

        StackArrangement arrangement = SINGLE.arrangeStacks(extents);

        if (!extents.hasNext()) {
            throw new ArrangeStackException("No image in iterator for overlay");
        }

        BoundingBox box = boxForOverlay(arrangement.extent(), extents.next());
        arrangement.add(box);
        return arrangement;
    }

    /** The bounding-box for the overlay, relative to the stack on which it will be projected. */
    private BoundingBox boxForOverlay(Extent enclosing, Extent overlay)
            throws ArrangeStackException {
        Extent extent = deriveExtent(enclosing, overlay);
        Point3i cornerMin = cornerMin(enclosing, overlay);
        return BoundingBox.createReuse(cornerMin, extent);
    }

    /** The minimum corner at which the overlay should be located in the output image. */
    private Point3i cornerMin(Extent enclosing, Extent overlay) throws ArrangeStackException {
        return new Point3i(
                CHOICES_X.position("alignX", alignX, Extent::x, enclosing, overlay),
                CHOICES_Y.position("alignY", alignY, Extent::y, enclosing, overlay),
                CHOICES_Z.position("alignZ", alignZ, Extent::z, enclosing, overlay));
    }

    /** Determines the size of the overlayed stack, as projected into the image. */
    private Extent deriveExtent(Extent enclosing, Extent overlay) throws ArrangeStackException {
        return new Extent(
                Math.min(overlay.x(), enclosing.x()),
                Math.min(overlay.y(), enclosing.y()),
                deriveZExtent(enclosing, overlay));
    }

    /**
     * Determines the size of the z-dimension of the overlayed stack, as projected into the image.
     */
    private int deriveZExtent(Extent enclosing, Extent overlay) throws ArrangeStackException {
        if (alignZ.equalsIgnoreCase(REPEAT)) {

            if (overlay.z() != 1) {
                throw new ArrangeStackException(
                        String.format(
                                "If alignZ is `repeat` then the overlay must have a single z-slice, but it has %d slices.",
                                overlay.z()));
            }

            return enclosing.z();
        } else {
            return Math.min(overlay.z(), enclosing.z());
        }
    }
}
