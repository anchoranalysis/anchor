/* (C)2020 */
package org.anchoranalysis.image.outline.traverser.visitedpixels.combine;

/** A distance, and the two indices the point came from */
class DistanceIndexTwice {

    private int distance;
    private int indexHighest; // highest-level index
    private int indexLowest; // lowest-level index

    public DistanceIndexTwice(int distance, int indexHighest, int indexLowest) {
        super();
        this.distance = distance;
        this.indexHighest = indexHighest;
        this.indexLowest = indexLowest;
    }

    public int getDistance() {
        return distance;
    }

    public int getIndexHighest() {
        return indexHighest;
    }

    public int getIndexLowest() {
        return indexLowest;
    }
}
