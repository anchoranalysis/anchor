/* (C)2020 */
package org.anchoranalysis.anchor.mpp.mark;

/** A method to quickly calculate overlap approximately */
@FunctionalInterface
public interface QuickOverlapCalculation {

    /**
     * A quick (computationally-efficient) test to see if we can reject the possibility of overlap
     *
     * @param mark the other mark to assess overlap with
     * @param regionID the region to check for overlap
     * @return true if there's definitely no overlap, false if there is maybe overlap or not
     */
    boolean noOverlapWith(Mark mark, int regionID);
}
