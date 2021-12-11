package org.anchoranalysis.anchor.mpp.mark.conic;

import static org.junit.Assert.assertEquals;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.mpp.mark.Mark;
import org.anchoranalysis.mpp.mark.MarkToObjectConverter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Checks that the volume of an object is similar to the number of voxels in an object representation.
 * @author Owen Feehan
 *
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
class VolumeTester {

    private static final double RATIO_TOLERANCE_VOLUME = 0.02;
    
    private static final MarkToObjectConverter CONVERTER = new MarkToObjectConverter( new Dimensions(30,40,50) );

    /** 
     * Establishes that the number of voxels in the object-representation of a mark, is almost identical to it's calculated volume.
     *
     * @param mark the mark to check.
     */
    public static void assertVolumeMatches(Mark mark) {
        assertEqualsRatioTolerance(numberVoxelsFromObject(mark), mark.volume(0));
    }
    
    /** Allows a tolerance, as a % of the expected value. */
    private static void assertEqualsRatioTolerance(double expected, double actual) {
        assertEquals(expected, actual, expected * RATIO_TOLERANCE_VOLUME);
    }
    
    /** Gets the number of voxels from an object-representation of a {@link Mark}. */
    private static int numberVoxelsFromObject(Mark mark) {
        return CONVERTER.convert(mark).numberVoxelsOn();
    }
}
