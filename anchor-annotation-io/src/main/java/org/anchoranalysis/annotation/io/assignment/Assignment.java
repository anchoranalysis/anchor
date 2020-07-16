/* (C)2020 */
package org.anchoranalysis.annotation.io.assignment;

import java.util.List;
import org.anchoranalysis.core.text.TypedValue;
import org.anchoranalysis.image.object.ObjectMask;

public interface Assignment {

    int numPaired();

    int numUnassigned(boolean left);

    List<ObjectMask> getListPaired(boolean left);

    List<ObjectMask> getListUnassigned(boolean left);

    List<String> createStatisticsHeaderNames();

    List<TypedValue> createStatistics();
}
