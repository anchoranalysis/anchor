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

package org.anchoranalysis.image.bean.nonbean.spatial.arrange;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.Accessors;

import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.box.Extent;

/**
 * A list of {@link BoundingBox}es that indicate whether to locate corresponding {@link Stack}s on a unified larger image.
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor
@Value
@Accessors(fluent = true)
public class StackArrangement implements Iterable<BoundingBox> {

    // START REQUIRED ARGUMENTS
	/** The size of the larger image in which all the {@link BoundingBox}es in {@code list} must fully reside. */
    private final Extent extent;
    // END REQUIRED ARGUMENTS

    private List<BoundingBox> list = new ArrayList<>();

    /**
     * Create with a single {@link BoundingBox}.
     * 
     * @param extent the size of the larger image in which all the {@link BoundingBox}es in {@code list} must fully reside.
     * @param box the {@link BoundingBox}.
     */
    public StackArrangement(Extent extent, BoundingBox box) {
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
