/* (C)2020 */
package org.anchoranalysis.test.image;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.error.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.feature.nrg.NRGStack;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.IncorrectImageSizeException;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.test.image.ChnlFixture.IntensityFunction;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NRGStackFixture {

    public static NRGStackWithParams create(boolean big, boolean do3D) {

        Extent size = muxExtent(big, do3D);

        try {
            Stack stack = new Stack();
            addChnl(stack, size, ChnlFixture::sumMod);
            addChnl(stack, size, ChnlFixture::diffMod);
            addChnl(stack, size, ChnlFixture::multMod);

            NRGStack nrgStack = new NRGStack(stack);
            return new NRGStackWithParams(nrgStack);

        } catch (IncorrectImageSizeException e) {
            throw new AnchorImpossibleSituationException();
        }
    }

    private static Extent muxExtent(boolean big, boolean do3D) {
        if (do3D) {
            return big ? ChnlFixture.LARGE_3D : ChnlFixture.MEDIUM_3D;
        } else {
            return big ? ChnlFixture.LARGE_2D : ChnlFixture.MEDIUM_2D;
        }
    }

    private static void addChnl(Stack stack, Extent size, IntensityFunction intensityFunction)
            throws IncorrectImageSizeException {
        stack.addChnl(ChnlFixture.createChnl(size, intensityFunction));
    }
}
