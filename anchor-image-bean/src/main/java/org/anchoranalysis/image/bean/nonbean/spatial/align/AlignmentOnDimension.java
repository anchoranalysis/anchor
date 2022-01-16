package org.anchoranalysis.image.bean.nonbean.spatial.align;

/**
 * How the alignment will occur for a particular dimension.
 *
 * @author Owen Feehan
 */
public enum AlignmentOnDimension {
    /** Aligns to the <b>minimum</b> position along the axis. */
    MIN {

        @Override
        public int align(int larger, int smaller, int disconsiderLeft) {
            return disconsiderLeft;
        }
    },

    /** Aligns to the <b>center</b> position along the axis. */
    CENTER {

        @Override
        public int align(int larger, int smaller, int disconsiderLeft) {
            int centered = (larger - disconsiderLeft - smaller) / 2;
            return disconsiderLeft + centered;
        }
    },

    /** Aligns to the <b>maximum</b> position along the axis. */
    MAX {

        @Override
        public int align(int larger, int smaller, int disconsiderLeft) {
            return Math.max(disconsiderLeft, larger - smaller);
        }
    };

    /**
     * Determine the minimum value after alignment.
     *
     * @param larger the larger space which is aligned with, disconsidering the first {@code
     *     disconsiderLeft} of it.
     * @param smaller the smaller space to align.
     * @param disconsiderLeft how much of {@code larger} to disconsider, left-most.
     * @return the minimum value of the smaller space after alignment.
     */
    public abstract int align(int larger, int smaller, int disconsiderLeft);
}
