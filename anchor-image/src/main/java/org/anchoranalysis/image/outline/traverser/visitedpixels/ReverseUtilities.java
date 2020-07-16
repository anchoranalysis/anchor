/* (C)2020 */
package org.anchoranalysis.image.outline.traverser.visitedpixels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.geometry.Point3i;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReverseUtilities {

    public static List<Point3i> reversedList(List<Point3i> list) {
        List<Point3i> copy = new ArrayList<>(list);
        Collections.reverse(copy);
        return copy;
    }
}
