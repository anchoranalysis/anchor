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
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.experimental.Accessors;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.box.Extent;

/**
 * A list of {@link BoundingBox}es that indicate where to locate corresponding {@link Stack}s on a
 * unified larger image.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
@Value
@Accessors(fluent = true)
public class StackArrangement implements Iterable<BoundingBoxEnclosed> {

    // START REQUIRED ARGUMENTS
    /**
     * The size of the larger image in which all the {@link BoundingBox}es in {@code list} must
     * fully reside.
     */
    private final Extent extent;
    // END REQUIRED ARGUMENTS

    /** Where to locate a respective image on a larger image. */
    private final List<BoundingBoxEnclosed> boxes;

    /**
     * Create with the overall {@link Extent} and no boxes.
     *
     * @param extent the size of the larger image in which all the {@link BoundingBox}es in {@code
     *     list} must fully reside.
     */
    public StackArrangement(Extent extent) {
        this.extent = extent;
        this.boxes = new ArrayList<>();
    }

    /**
     * Create with a single {@link BoundingBox}.
     *
     * @param extent the size of the larger image in which all the {@link BoundingBox}es in {@code
     *     list} must fully reside.
     * @param box the {@link BoundingBox}.
     */
    public StackArrangement(Extent extent, BoundingBox box) {
        this(extent);
        add(box);
    }

    /**
     * Adds a new {@link BoundingBox} to the arrangement, with no padding.
     *
     * @param box the {@link BoundingBox} in which the image should be placed.
     */
    public void add(BoundingBox box) {
        boxes.add(new BoundingBoxEnclosed(box));
    }

    /**
     * Adds a new {@link BoundingBoxEnclosed} to the arrangement.
     *
     * @param box the {@link BoundingBox} in which the image should be placed, together with any
     *     enclosing box (e.g. with padding).
     */
    public void add(BoundingBoxEnclosed box) {
        boxes.add(box);
    }

    /**
     * Gets the {@link BoundingBox} corresponding to a particular index.
     *
     * @param index the index.
     * @return the corresponding {@link BoundingBox} in the arrangement, and any enclosing
     *     bounding-box.
     * @throws IndexOutOfBoundsException if the index is out of range.
     */
    public BoundingBoxEnclosed get(int index) {
        return boxes.get(index);
    }

    @Override
    public Iterator<BoundingBoxEnclosed> iterator() {
        return boxes.iterator();
    }
}
