package org.anchoranalysis.anchor.mpp.mark.conic;

import org.anchoranalysis.mpp.mark.conic.Ellipsoid;
import org.anchoranalysis.spatial.orientation.Orientation3DEulerAngles;
import org.anchoranalysis.spatial.point.Point3d;
import org.junit.jupiter.api.Test;

/**
 * Tests {@Ellipsoid}.
 * 
 * @author Owen Feehan
 *
 */
class EllipsoidTest {
    
    @Test
    void checkVolume() {
        Ellipsoid mark = new Ellipsoid();
        mark.setMarksExplicit(new Point3d(5, 7, 4.5), new Orientation3DEulerAngles(1.5, 2.0, 0.4), new Point3d(5.6, 3.2, 4.1));

        VolumeTester.assertVolumeMatches(mark);
    }
}
