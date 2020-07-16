/* (C)2020 */
package org.anchoranalysis.image.feature.session.merged;

public final class MergedPairsInclude {

    private boolean includeFirst;
    private boolean includeSecond;
    private boolean includeMerged;

    public MergedPairsInclude() {
        this(true, true, true);
    }

    public MergedPairsInclude(boolean includeFirst, boolean includeSecond, boolean includeMerged) {
        super();
        this.includeFirst = includeFirst;
        this.includeSecond = includeSecond;
        this.includeMerged = includeMerged;
    }

    public boolean includeFirstOrSecond() {
        return includeFirst || includeSecond;
    }

    public boolean includeFirst() {
        return includeFirst;
    }

    public boolean includeSecond() {
        return includeSecond;
    }

    public boolean includeMerged() {
        return includeMerged;
    }
}
