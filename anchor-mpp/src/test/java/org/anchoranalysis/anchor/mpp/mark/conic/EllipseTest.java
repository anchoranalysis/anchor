package org.anchoranalysis.anchor.mpp.mark.conic;

import org.anchoranalysis.mpp.mark.conic.Ellipse;
import org.anchoranalysis.spatial.orientation.Orientation2D;
import org.anchoranalysis.spatial.point.Point2d;
import org.anchoranalysis.spatial.point.Point3d;
import org.junit.jupiter.api.Test;

/**
 * Tests {@Ellipse}.
 *
 * @author Owen Feehan
 */
class EllipseTest {

    @Test
    void checkVolume() {
        Ellipse mark = new Ellipse();
        mark.setMarksExplicit(new Point3d(5, 7, 0), new Orientation2D(1.5), new Point2d(5.6, 3.2));

        VolumeTester.assertVolumeMatches(mark);
    }
}
