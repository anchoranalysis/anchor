/* (C)2020 */
package org.anchoranalysis.core.relation;

public class LessThan implements RelationToValue {

    @Override
    public boolean isRelationToValueTrue(double valueFirst, double valueSecond) {
        return valueFirst < valueSecond;
    }
}
