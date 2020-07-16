/* (C)2020 */
package org.anchoranalysis.image.io.stack;

import org.anchoranalysis.core.error.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.IncorrectImageSizeException;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.image.stack.rgb.RGBStack;

public class ConvertDisplayStackToRGB {

    private ConvertDisplayStackToRGB() {}

    public static RGBStack convert(DisplayStack background) {

        try {
            if (background.getNumChnl() == 1) {
                return new RGBStack(
                        background.createChnlDuplicate(0),
                        background.createChnlDuplicate(0),
                        background.createChnlDuplicate(0));
            } else if (background.getNumChnl() == 3) {
                return new RGBStack(background.createImgStackDuplicate());
            } else {
                throw new AnchorImpossibleSituationException();
            }
        } catch (IncorrectImageSizeException e) {
            throw new AnchorImpossibleSituationException();
        }
    }

    public static RGBStack convertCropped(DisplayStack background, BoundingBox bbox) {

        try {
            if (background.getNumChnl() == 1) {
                Channel chnl = background.createChnlDuplicateForBBox(0, bbox);
                return new RGBStack(chnl, chnl.duplicate(), chnl.duplicate());
            } else if (background.getNumChnl() == 3) {
                return new RGBStack(
                        background.createChnlDuplicateForBBox(0, bbox),
                        background.createChnlDuplicateForBBox(1, bbox),
                        background.createChnlDuplicateForBBox(2, bbox));
            } else {
                throw new AnchorImpossibleSituationException();
            }
        } catch (IncorrectImageSizeException e) {
            // This should not be possible
            throw new AnchorImpossibleSituationException();
        }
    }
}
