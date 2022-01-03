package org.anchoranalysis.image.bean.spatial.arrange.tile;

import lombok.Getter;

/** All the sizes for a particular dimension, as well as the sum of the sizes. */
class SizesAcrossDimension {

    /** The sizes and corner points for the dimension. */
    private SizeAtPoint[] sizes;

    /** The sum of the sizes across the dimension. */
    @Getter private int sum;

    /** The current index, of the next element to be added. */
    private int index = 0;

    public SizesAcrossDimension(int numberElements) {
        sizes = new SizeAtPoint[numberElements];
    }

    /**
     * Adds a new element of a particular {@code size}.
     *
     * @param size the size of the element in the particular dimension.
     */
    public void add(int size) {
        sizes[index] = new SizeAtPoint(sum, size);
        sum += size;
        index++;
    }

    /**
     * Gets the size corresponding at a particular position.
     *
     * @param index the index of the element (zero-indexed).
     * @return the corresponding size.
     */
    public SizeAtPoint get(int index) {
        return sizes[index];
    }
}
