/* (C)2020 */
package org.anchoranalysis.image.outline.traverser.contiguouspath;

/** A distance, and the index the point came from */
public class DistanceIndex {

    private int distance;
    private int index;

    public DistanceIndex(int distance, int index) {
        super();
        this.distance = distance;
        this.index = index;
    }

    public int getDistance() {
        return distance;
    }

    public int getIndex() {
        return index;
    }
}
