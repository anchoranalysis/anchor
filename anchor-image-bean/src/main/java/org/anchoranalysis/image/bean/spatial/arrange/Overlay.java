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

import java.util.Iterator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.image.bean.nonbean.spatial.arrange.ArrangeStackException;
import org.anchoranalysis.image.bean.nonbean.spatial.arrange.BoundingBoxesOnPlane;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.stack.RGBStack;
import org.anchoranalysis.spatial.Extent;
import org.anchoranalysis.spatial.box.BoundingBox;
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
public class Overlay extends ArrangeStackBean {

    // START BEAN PROPERTIES

    // left, right, center
    @BeanField @Getter @Setter private String horizontalAlign = "left";

    // top, bottom, center
    @BeanField @Getter @Setter private String verticalAlign = "top";

    // top, bottom, center
    @BeanField @Getter @Setter private String zAlign = "top";

    // END BEAN PROPERTIES

    @Override
    public String describeBean() {
        return getBeanName();
    }

    private int positionHorizontal(BoundingBoxesOnPlane boxSet, Dimensions dimensions) {

        if (horizontalAlign.equalsIgnoreCase("left")) {
            return 0;
        } else if (horizontalAlign.equalsIgnoreCase("right")) {
            return boxSet.extent().x() - dimensions.x();
        } else {
            return (boxSet.extent().x() - dimensions.x()) / 2;
        }
    }

    private int positionVertical(BoundingBoxesOnPlane boxSet, Dimensions dimensions) {

        if (verticalAlign.equalsIgnoreCase("top")) {
            return 0;
        } else if (verticalAlign.equalsIgnoreCase("bottom")) {
            return boxSet.extent().y() - dimensions.y();
        } else {
            return (boxSet.extent().y() - dimensions.y()) / 2;
        }
    }

    private int positionZ(BoundingBoxesOnPlane boxSet, Dimensions dimensions) {

        if (zAlign.equalsIgnoreCase("bottom") || zAlign.equalsIgnoreCase("repeat")) {
            return 0;
        } else if (zAlign.equalsIgnoreCase("top")) {
            return boxSet.extent().z() - dimensions.z();
        } else {
            return (boxSet.extent().z() - dimensions.z()) / 2;
        }
    }

    @Override
    public BoundingBoxesOnPlane createBoundingBoxesOnPlane(Iterator<RGBStack> rasterIterator)
            throws ArrangeStackException {

        if (!rasterIterator.hasNext()) {
            throw new ArrangeStackException("No image in iterator for source");
        }

        Single sr = new Single();
        BoundingBoxesOnPlane boxSet = sr.createBoundingBoxesOnPlane(rasterIterator);

        if (!rasterIterator.hasNext()) {
            throw new ArrangeStackException("No image in iterator for overlay");
        }

        RGBStack overlayImg = rasterIterator.next();

        Extent overlayE = deriveExtent(overlayImg.channelAt(0).extent(), boxSet.extent());

        int hPos = positionHorizontal(boxSet, overlayImg.dimensions());
        int vPos = positionVertical(boxSet, overlayImg.dimensions());
        int zPos = positionZ(boxSet, overlayImg.dimensions());

        boxSet.add(new BoundingBox(new Point3i(hPos, vPos, zPos), overlayE));
        return boxSet;
    }

    private Extent deriveExtent(Extent overlay, Extent box) {
        return new Extent(
                Math.min(overlay.x(), box.x()),
                Math.min(overlay.y(), box.y()),
                zAlign.equalsIgnoreCase("repeat") || (overlay.z() > box.z())
                        ? box.z()
                        : overlay.z());
    }
}
