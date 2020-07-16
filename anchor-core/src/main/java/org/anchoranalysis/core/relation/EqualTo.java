/* (C)2020 */
package org.anchoranalysis.core.relation;

import org.anchoranalysis.core.arithmetic.DoubleUtilities;

public class EqualTo implements RelationToValue {

    @Override
    public boolean isRelationToValueTrue(double valueFirst, double valueSecond) {
        return DoubleUtilities.areEqual(valueFirst, valueSecond);
    }
}
