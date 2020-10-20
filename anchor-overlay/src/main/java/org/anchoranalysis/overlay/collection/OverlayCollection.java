/*-
 * #%L
 * anchor-overlay
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

package org.anchoranalysis.overlay.collection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.IntPredicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.overlay.Overlay;
import org.anchoranalysis.overlay.writer.DrawOverlay;
import org.anchoranalysis.spatial.box.BoundingBox;

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
            set.add(ol.getIdentifier());
        }
        return set;
    }

    public List<BoundingBox> boxList(DrawOverlay drawOverlay, Dimensions dimensions) {

        List<BoundingBox> out = new ArrayList<>();

        for (Overlay ol : this) {
            BoundingBox box = ol.box(drawOverlay, dimensions);
            out.add(box);
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

    public OverlayCollection createSubset(IntPredicate predicateOnIndex) {

        OverlayCollection out = new OverlayCollection();

        // This our current
        for (Overlay overlay : this) {
            if (predicateOnIndex.test(overlay.getIdentifier())) {
                out.add(overlay);
            }
        }

        return out;
    }

    public List<Overlay> asList() {
        return delegate;
    }
}
