package org.anchoranalysis.image.extent.rtree;

import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.anchoranalysis.image.extent.box.BoundingBox;

@AllArgsConstructor
/**
 * An R-Tree coupled with a set, both of whose elements are maintained as identical across
 * add/remove operations.
 */
class RTreeWithSet<T> {

    /** The r-tree for elements */
    private final RTree<T> tree;

    /** A set containing identical elements to the r-tree. */
    private final Set<T> set;

    public boolean contains(T element) {
        return set.contains(element);
    }

    public boolean isEmpty() {
        return set.isEmpty();
    }

    public T arbitraryElement() {
        return set.iterator().next();
    }

    public void remove(T element, BoundingBox box) {
        set.remove(element);
        tree.delete(box, element);
    }

    public List<T> intersectsWith(BoundingBox box) {
        return tree.intersectsWith(box);
    }
}
