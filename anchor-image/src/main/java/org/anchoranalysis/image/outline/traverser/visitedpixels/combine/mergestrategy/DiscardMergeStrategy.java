/* (C)2020 */
package org.anchoranalysis.image.outline.traverser.visitedpixels.combine.mergestrategy;

class DiscardMergeStrategy extends MergeStrategy {

    public DiscardMergeStrategy(int cost) {
        super(cost);
    }

    @Override
    public void applyStrategy(MergeCandidate candidate) {
        // DO NOTHING
    }
}
