/*-
 * #%L
 * anchor-image
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

package org.anchoranalysis.image.core.object.seed;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.image.voxel.object.ObjectCollection;
import org.anchoranalysis.image.voxel.object.ObjectCollectionFactory;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.spatial.Extent;

public class SeedCollection implements Iterable<Seed> {

    private List<Seed> delegate = new ArrayList<>();

    public SeedCollection duplicate() {
        SeedCollection out = new SeedCollection();
        for (Seed seed : this) {
            out.delegate.add(seed.duplicate());
        }
        return out;
    }

    public void scaleXY(double scale, Extent extent) throws OperationFailedException {
        for (Seed seed : this) {
            seed.scaleXY(scale, extent);
        }
    }

    public ObjectCollection deriveObjects() {
        return ObjectCollectionFactory.mapFrom(delegate, Seed::deriveObject);
    }

    public void flattenZ() {

        for (Seed seed : delegate) {
            seed.flattenZ();
        }
    }

    public void growToZ(int sz) {

        for (Seed seed : delegate) {
            seed.growToZ(sz);
        }
    }

    public void add(Seed element) {
        delegate.add(element);
    }

    public Seed get(int index) {
        return delegate.get(index);
    }

    @Override
    public Iterator<Seed> iterator() {
        return delegate.iterator();
    }

    public int lastIndexOf(Object o) {
        return delegate.lastIndexOf(o);
    }

    public Seed set(int index, Seed element) {
        return delegate.set(index, element);
    }

    public int size() {
        return delegate.size();
    }

    public boolean doSeedsIntersectWithContainingMask(ObjectMask objectContaining) {

        for (int i = 0; i < delegate.size(); i++) {

            ObjectMask object = delegate.get(i).deriveObject();

            if (!object.hasIntersectingVoxels(objectContaining)) {
                return false;
            }
        }
        return true;
    }

    public boolean doSeedsIntersect() {

        for (int i = 0; i < delegate.size(); i++) {

            Seed s = delegate.get(i);

            ObjectMask objectS = s.deriveObject();

            for (int j = 0; j < i; j++) {

                Seed t = delegate.get(j);

                ObjectMask objectT = t.deriveObject();

                if (objectS.hasIntersectingVoxels(objectT)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean verifySeedsAreInside(Extent extent) {
        for (Seed seed : this) {

            ObjectMask object = seed.deriveObject();

            if (!extent.contains(object.boundingBox())) {
                return false;
            }
        }
        return true;
    }

    public boolean isEmpty() {
        return delegate.isEmpty();
    }
}
