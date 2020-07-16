/* (C)2020 */
package org.anchoranalysis.core.relation;

public class GreaterThan implements RelationToValue {

    @Override
    public boolean isRelationToValueTrue(double valueFirst, double valueSecond) {
        return valueFirst > valueSecond;
    }
}
