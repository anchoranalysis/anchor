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

package org.anchoranalysis.image.bean.arrangeraster;

import java.util.Iterator;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.bean.nonbean.arrangeraster.ArrangeRasterException;
import org.anchoranalysis.image.bean.nonbean.arrangeraster.BoundingBoxesOnPlane;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.stack.rgb.RGBStack;

// Overlays one image on the other
// FIRST image passed is assumed to be the source
// SECOND image passed is assumed to be the overlay
// We have no Z implemented yet, so we always overlay at z position 0
public class ArrangeRasterOverlay extends ArrangeRasterBean {

    // START BEAN PROPERTIES

    // left, right, center
    @BeanField @Getter @Setter private String horizontalAlign = "left";

    // top, bottom, center
    @BeanField @Getter @Setter private String verticalAlign = "top";

    // top, bottom, center
    @BeanField @Getter @Setter private String zAlign = "top";

    // END BEAN PROPERTIES

    @Override
    public String getBeanDscr() {
        return getBeanName();
    }

    private int calcHorizontalPos(BoundingBoxesOnPlane bboxSet, ImageDimensions dimensions) {

        if (horizontalAlign.equalsIgnoreCase("left")) {
            return 0;
        } else if (horizontalAlign.equalsIgnoreCase("right")) {
            return bboxSet.extent().x() - dimensions.x();
        } else {
            return (bboxSet.extent().x() - dimensions.x()) / 2;
        }
    }

    private int calcVerticalPos(BoundingBoxesOnPlane bboxSet, ImageDimensions dimensions) {

        if (verticalAlign.equalsIgnoreCase("top")) {
            return 0;
        } else if (verticalAlign.equalsIgnoreCase("bottom")) {
            return bboxSet.extent().y() - dimensions.y();
        } else {
            return (bboxSet.extent().y() - dimensions.y()) / 2;
        }
    }

    private int calcZPos(BoundingBoxesOnPlane bboxSet, ImageDimensions dimensions) {

        if (zAlign.equalsIgnoreCase("bottom") || zAlign.equalsIgnoreCase("repeat")) {
            return 0;
        } else if (zAlign.equalsIgnoreCase("top")) {
            return bboxSet.extent().z() - dimensions.z();
        } else {
            return (bboxSet.extent().z() - dimensions.z()) / 2;
        }
    }

    @Override
    public BoundingBoxesOnPlane createBoundingBoxesOnPlane(Iterator<RGBStack> rasterIterator)
            throws ArrangeRasterException {

        if (!rasterIterator.hasNext()) {
            throw new ArrangeRasterException("No image in iterator for source");
        }

        SingleRaster sr = new SingleRaster();
        BoundingBoxesOnPlane bboxSet = sr.createBoundingBoxesOnPlane(rasterIterator);

        if (!rasterIterator.hasNext()) {
            throw new ArrangeRasterException("No image in iterator for overlay");
        }

        RGBStack overlayImg = rasterIterator.next();

        Extent overlayE =
                deriveExtent(
                        overlayImg.channelAt(0).dimensions().extent(), bboxSet.extent());

        int hPos = calcHorizontalPos(bboxSet, overlayImg.dimensions());
        int vPos = calcVerticalPos(bboxSet, overlayImg.dimensions());
        int zPos = calcZPos(bboxSet, overlayImg.dimensions());

        bboxSet.add(new BoundingBox(new Point3i(hPos, vPos, zPos), overlayE));
        return bboxSet;
    }

    private Extent deriveExtent(Extent overlay, Extent bbox) {
        return new Extent(
                Math.min(overlay.x(), bbox.x()),
                Math.min(overlay.y(), bbox.y()),
                zAlign.equalsIgnoreCase("repeat") || (overlay.z() > bbox.z())
                        ? bbox.z()
                        : overlay.z());
    }
}
