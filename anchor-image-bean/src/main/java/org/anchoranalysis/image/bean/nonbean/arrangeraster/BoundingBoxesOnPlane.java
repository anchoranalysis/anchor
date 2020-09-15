/*-
 * #%L
 * anchor-image-bean
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

package org.anchoranalysis.image.bean.nonbean.arrangeraster;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.Accessors;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.box.BoundingBox;

/**
 * Describes a set of bounding boxes on top of a plane
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor
@Value
@Accessors(fluent = true)
public class BoundingBoxesOnPlane implements Iterable<BoundingBox> {

    // START REQUIRED ARGUMENTS
    private final Extent extent;
    // END REQUIRED ARGUMENTS

    private List<BoundingBox> list = new ArrayList<>();

    public BoundingBoxesOnPlane(Extent extent, BoundingBox box) {
        this(extent);
        add(box);
    }

    public void add(BoundingBox boundingBox) {
        list.add(boundingBox);
    }

    public BoundingBox get(int index) {
        return list.get(index);
    }

    public Iterator<BoundingBox> boxIterator() {
        return list.iterator();
    }

    @Override
    public Iterator<BoundingBox> iterator() {
        return boxIterator();
    }
}
