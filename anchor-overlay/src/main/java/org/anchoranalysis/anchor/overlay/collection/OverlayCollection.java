/* (C)2020 */
package org.anchoranalysis.anchor.overlay.collection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.anchoranalysis.anchor.overlay.Overlay;
import org.anchoranalysis.anchor.overlay.writer.DrawOverlay;
import org.anchoranalysis.core.index.IndicesSelection;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDimensions;

public class OverlayCollection implements Iterable<Overlay> {

    private List<Overlay> delegate;

    public OverlayCollection() {
        delegate = new ArrayList<>();
    }

    public OverlayCollection(Stream<Overlay> stream) {
        delegate = stream.collect(Collectors.toList());
    }

    @Override
    public Iterator<Overlay> iterator() {
        return delegate.iterator();
    }

    public Overlay get(int index) {
        return delegate.get(index);
    }

    public Overlay remove(int index) {
        return delegate.remove(index);
    }

    public int size() {
        return delegate.size();
    }

    public boolean add(Overlay e) {
        return delegate.add(e);
    }

    public boolean addAll(OverlayCollection c) {
        return delegate.addAll(c.delegate);
    }

    public Set<Integer> integerSet() {
        HashSet<Integer> set = new HashSet<>();
        for (Overlay ol : delegate) {
            set.add(ol.getId());
        }
        return set;
    }

    public List<BoundingBox> bboxList(DrawOverlay maskWriter, ImageDimensions dimensions) {

        List<BoundingBox> out = new ArrayList<>();

        for (Overlay ol : this) {
            BoundingBox bbox = ol.bbox(maskWriter, dimensions);
            out.add(bbox);
        }
        return out;
    }

    public OverlayCollection shallowCopy() {

        OverlayCollection out = new OverlayCollection();

        // We copy all the marks
        out.delegate = new ArrayList<>(this.delegate.size());
        for (Overlay ol : this.delegate) {
            out.delegate.add(ol);
        }

        return out;
    }

    // A hashmap of all the marks, using the Id as an index
    public Set<Overlay> createSet() {

        HashSet<Overlay> out = new HashSet<>();

        for (Overlay overlay : this) {
            out.add(overlay);
        }

        return out;
    }

    public OverlayCollection createMerged(OverlayCollection toMerge) {

        OverlayCollection mergedNew = shallowCopy();

        Set<Overlay> set = mergedNew.createSet();

        for (Overlay m : toMerge) {
            if (!set.contains(m)) {
                mergedNew.add(m);
            }
        }

        return mergedNew;
    }

    public OverlayCollection createSubset(IndicesSelection indices) {

        OverlayCollection out = new OverlayCollection();

        // This our current
        for (Overlay ol : this) {
            if (indices.contains(ol.getId())) {
                out.add(ol);
            }
        }

        return out;
    }

    public List<Overlay> asList() {
        return delegate;
    }
}
