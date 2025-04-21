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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.time.OperationContext;
import org.anchoranalysis.image.bean.nonbean.spatial.align.PositionChoicesConstants;
import org.anchoranalysis.image.bean.nonbean.spatial.arrange.ArrangeStackException;
import org.anchoranalysis.image.bean.nonbean.spatial.arrange.StackArrangement;
import org.anchoranalysis.image.bean.spatial.arrange.Single;
import org.anchoranalysis.image.bean.spatial.arrange.StackArranger;
import org.anchoranalysis.image.bean.spatial.arrange.align.Align;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.box.Extent;

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

    private static final Single SINGLE = new Single();

    // START BEAN PROPERTIES
    /** Indicates how to align the image across the three axes. */
    @BeanField @Getter @Setter private Align align = new Align("left", "top", "bottom");

    // END BEAN PROPERTIES

    /**
     * Creates with alignment text for each axis.
     *
     * @param alignX indicates how to align the image across the <b>X-axis</b>: one of {@code top,
     *     bottom, center}.
     * @param alignY indicates how to align the image across the <b>Y-axis</b> (i.e. vertically):
     *     one of {@code top, bottom, center}.
     * @param alignZ indicates how to align the image across the <b>Z-axis</b>: one of {@code top,
     *     bottom, center, repeat}. See {@code alignZ}.
     */
    public Overlay(String alignX, String alignY, String alignZ) {
        this.align = new Align(alignX, alignY, alignZ);
    }

    @Override
    public String describeBean() {
        return getBeanName();
    }

    @Override
    public StackArrangement arrangeStacks(Iterator<Extent> extents, OperationContext context)
            throws ArrangeStackException {

        if (!extents.hasNext()) {
            throw new ArrangeStackException("No image in iterator for source");
        }

        StackArrangement arrangement = SINGLE.arrangeStacks(extents, context);

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
        try {

            if (align.getAlignZ().equalsIgnoreCase(PositionChoicesConstants.REPEAT)) {

                BoundingBox boxAligned = align.align(overlay.flattenZ(), enclosing.flattenZ());

                if (overlay.z() != 1) {
                    throw new ArrangeStackException(
                            String.format(
                                    "If alignZ is `repeat` then the overlay must have a single z-slice, but it has %d slices.",
                                    overlay.z()));
                }

                return boxAligned.changeExtent(
                        extentToChange -> extentToChange.duplicateChangeZ(enclosing.z()));
            } else {
                return align.align(overlay, enclosing);
            }
        } catch (OperationFailedException e) {
            throw new ArrangeStackException(
                    "Failed to align the overlay with the enclosing stack", e);
        }
    }
}
