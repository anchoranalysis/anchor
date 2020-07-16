/* (C)2020 */
package org.anchoranalysis.image.bean.arrangeraster;

import java.util.Iterator;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.bean.nonbean.arrangeraster.ArrangeRasterException;
import org.anchoranalysis.image.bean.nonbean.arrangeraster.BBoxSetOnPlane;
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

    private int calcHorizontalPos(BBoxSetOnPlane bboxSet, ImageDimensions dimensions) {

        if (horizontalAlign.equalsIgnoreCase("left")) {
            return 0;
        } else if (horizontalAlign.equalsIgnoreCase("right")) {
            return bboxSet.getExtent().getX() - dimensions.getX();
        } else {
            return (bboxSet.getExtent().getX() - dimensions.getX()) / 2;
        }
    }

    private int calcVerticalPos(BBoxSetOnPlane bboxSet, ImageDimensions dimensions) {

        if (verticalAlign.equalsIgnoreCase("top")) {
            return 0;
        } else if (verticalAlign.equalsIgnoreCase("bottom")) {
            return bboxSet.getExtent().getY() - dimensions.getY();
        } else {
            return (bboxSet.getExtent().getY() - dimensions.getY()) / 2;
        }
    }

    private int calcZPos(BBoxSetOnPlane bboxSet, ImageDimensions dimensions) {

        if (zAlign.equalsIgnoreCase("bottom") || zAlign.equalsIgnoreCase("repeat")) {
            return 0;
        } else if (zAlign.equalsIgnoreCase("top")) {
            return bboxSet.getExtent().getZ() - dimensions.getZ();
        } else {
            return (bboxSet.getExtent().getZ() - dimensions.getZ()) / 2;
        }
    }

    @Override
    public BBoxSetOnPlane createBBoxSetOnPlane(Iterator<RGBStack> rasterIterator)
            throws ArrangeRasterException {

        if (!rasterIterator.hasNext()) {
            throw new ArrangeRasterException("No image in iterator for source");
        }

        SingleRaster sr = new SingleRaster();
        BBoxSetOnPlane bboxSet = sr.createBBoxSetOnPlane(rasterIterator);

        if (!rasterIterator.hasNext()) {
            throw new ArrangeRasterException("No image in iterator for overlay");
        }

        RGBStack overlayImg = rasterIterator.next();

        Extent overlayE =
                deriveExtent(
                        overlayImg.getChnl(0).getDimensions().getExtent(), bboxSet.getExtent());

        int hPos = calcHorizontalPos(bboxSet, overlayImg.getDimensions());
        int vPos = calcVerticalPos(bboxSet, overlayImg.getDimensions());
        int zPos = calcZPos(bboxSet, overlayImg.getDimensions());

        bboxSet.add(new BoundingBox(new Point3i(hPos, vPos, zPos), overlayE));
        return bboxSet;
    }

    private Extent deriveExtent(Extent overlay, Extent bbox) {
        return new Extent(
                Math.min(overlay.getX(), bbox.getX()),
                Math.min(overlay.getY(), bbox.getY()),
                zAlign.equalsIgnoreCase("repeat") || (overlay.getZ() > bbox.getZ())
                        ? bbox.getZ()
                        : overlay.getZ());
    }
}
