/* (C)2020 */
package org.anchoranalysis.image.outline.traverser.visitedpixels.combine.mergestrategy;

public abstract class MergeStrategy {

    // A cost of how undesirable (the higher the more undesirable) this merge is
    private int cost;

    protected MergeStrategy(int cost) {
        super();
        this.cost = cost;
    }

    public abstract void applyStrategy(MergeCandidate candidate);

    public int getCost() {
        return cost;
    }
}
