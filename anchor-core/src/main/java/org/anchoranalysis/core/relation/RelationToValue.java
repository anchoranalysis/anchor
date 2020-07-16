/* (C)2020 */
package org.anchoranalysis.core.relation;

@FunctionalInterface
public interface RelationToValue {
    boolean isRelationToValueTrue(double valueFirst, double valueSecond);
}
