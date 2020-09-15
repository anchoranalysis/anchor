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
package org.anchoranalysis.image.extent.box;

import com.google.common.base.Preconditions;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.anchoranalysis.core.functional.FunctionalList;
import org.anchoranalysis.image.object.combine.BoundingBoxMerger;

/**
 * One or more elements, each with an individual bounding-box, and collectively with a bounding-box
 * that fully contains them all.
 *
 * @author Owen Feehan
 * @param <T> type of element in the collection
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Accessors(fluent = true)
public class BoundedList<T> {

    /**
     * The collection of element with bounding-boxes.
     *
     * <p>This collection should not be altered after the constructor (treated immutably).
     */
    @Getter private final List<T> list;

    /** A bounding-box that must contain all elements in the collection */
    @Getter private final BoundingBox boundingBox;

    /**
     * Extracts a bounding box from an element. The operation is assumed to involve negligible
     * computational cost.
     */
    private final Function<T, BoundingBox> extractBoundingBox;

    /**
     * Creates for a single element, using its current bounding-box.
     *
     * @param element the single element
     * @param extractBoundingBox extracts a bounding box from an element. The operation is assumed
     *     to involve no computational cost.
     */
    public static <T> BoundedList<T> createSingle(
            T element, Function<T, BoundingBox> extractBoundingBox) {
        return new BoundedList<>(
                Arrays.asList(element), extractBoundingBox.apply(element), extractBoundingBox);
    }

    /**
     * Creates for a list, minimally fitting a bounding-box around all elements
     *
     * @param list the list
     * @param extractBoundingBox extracts a bounding box from an element. The operation is assumed
     *     to involve no computational cost.
     */
    public static <T> BoundedList<T> createFromList(
            List<T> list, Function<T, BoundingBox> extractBoundingBox) {
        Preconditions.checkArgument(!list.isEmpty());
        BoundingBox mergedBox =
                BoundingBoxMerger.mergeBoundingBoxes(list.stream().map(extractBoundingBox));
        return new BoundedList<>(list, mergedBox, extractBoundingBox);
    }

    /**
     * Assigns a new containing bounding-box.
     *
     * <p>The new box must contain the existing box.
     *
     * @param boxToAssign the new bounding-box to assign
     * @return newly-created with the same list but a different bounding-box
     */
    public BoundedList<T> assignBoundingBox(BoundingBox boxToAssign) {
        Preconditions.checkArgument(boxToAssign.contains().box(boundingBox));
        return new BoundedList<>(list, boxToAssign, extractBoundingBox);
    }

    /**
     * Assigns a new containing bounding-box and maps each individual element.
     *
     * <p>The new box must contain the existing box.
     *
     * @param boxToAssign the new bounding-box to assign
     * @parma mappingFunction applied to each element of the list to generate new element
     * @return newly-created with the same list but a different bounding-box
     */
    public BoundedList<T> assignBoundingBoxAndMap(
            BoundingBox boxToAssign, UnaryOperator<T> mappingFunction) {
        return new BoundedList<>(
                FunctionalList.mapToList(list, mappingFunction), boxToAssign, extractBoundingBox);
    }

    /**
     * Adds elements without changing the bounding-box
     *
     * <p>The operation is <i>immutable</i>.
     *
     * @param elementsToAdd elements to add (unchanged)
     * @return a newly created {@link BoundedList} with existing and added elements and the same
     *     bounding-box
     */
    public BoundedList<T> addObjectsNoBoundingBoxChange(Collection<T> elementsToAdd) {
        list.addAll(elementsToAdd);
        return new BoundedList<>(list, boundingBox, extractBoundingBox);
    }

    /** The number of elements */
    public int size() {
        return list.size();
    }

    /** Gets a particular element */
    public T get(int index) {
        return list.get(index);
    }

    /** A stream of elements in the list. */
    public Stream<T> stream() {
        return list.stream();
    }
}
