/*-
 * #%L
 * anchor-mpp
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

package org.anchoranalysis.mpp.mark;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.anchoranalysis.core.exception.CheckedUnsupportedOperationException;
import org.anchoranalysis.core.random.RandomNumberGenerator;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.voxel.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.voxel.object.ObjectCollection;
import org.anchoranalysis.image.voxel.object.ObjectCollectionFactory;
import org.anchoranalysis.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.mpp.bean.regionmap.RegionMembership;
import org.anchoranalysis.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.point.Point3i;
import org.anchoranalysis.spatial.scale.ScaleFactor;

/**
 * An ordered collection of {@link Mark}s.
 *
 * <p>This is often termed a <i>configuration</i> in marked-point-processes academic literature.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public final class MarkCollection implements Iterable<Mark>, Serializable {

    private static final long serialVersionUID = 2398855316191681489L;

    /** The underlying {@link List} that stores the {@link Mark}s in the collection. */
    @Getter private final List<Mark> marks;

    /** Creates with no elements in the collection. */
    public MarkCollection() {
        this(new ArrayList<>());
    }

    /**
     * Creates from a stream of {@link Mark}s.
     *
     * @param stream the stream of marks to add to the collection.
     */
    public MarkCollection(Stream<Mark> stream) {
        this(stream.toList());
    }

    /**
     * Creates from a single {@link Mark}.
     *
     * @param mark the mark to add to the collection.
     */
    public MarkCollection(Mark mark) {
        this();
        add(mark);
    }

    /**
     * Creates a shallow copy of the collection.
     *
     * @return a new MarkCollection with the same marks.
     */
    public MarkCollection shallowCopy() {
        return new MarkCollection(marks.stream());
    }

    /**
     * Creates a deep copy of the collection.
     *
     * @return a new MarkCollection with duplicates of all marks.
     */
    public MarkCollection deepCopy() {
        return new MarkCollection(marks.stream().map(Mark::duplicate));
    }

    /**
     * Adds a mark to the collection.
     *
     * @param mark the mark to add.
     * @return true if the mark was added successfully.
     */
    public boolean add(Mark mark) {
        return marks.add(mark);
    }

    /**
     * Adds all marks from another collection to this collection.
     *
     * @param marks the collection of marks to add.
     */
    public void addAll(MarkCollection marks) {
        for (Mark m : marks) {
            add(m);
        }
    }

    /**
     * Checks if the collection contains a specific object.
     *
     * @param obj the object to check for.
     * @return true if the object is in the collection.
     */
    public boolean contains(Object obj) {
        return marks.contains(obj);
    }

    /**
     * Checks if the collection is empty.
     *
     * @return true if the collection contains no marks.
     */
    public final boolean isEmpty() {
        return marks.isEmpty();
    }

    /**
     * Returns the number of marks in the collection.
     *
     * @return the size of the collection.
     */
    public final int size() {
        return marks.size();
    }

    @Override
    public final Iterator<Mark> iterator() {
        return marks.iterator();
    }

    @Override
    public String toString() {

        String newLine = System.getProperty("line.separator");

        StringBuilder s = new StringBuilder("{");

        s.append(String.format("size=%d%n", marks.size()));

        for (Iterator<Mark> i = marks.iterator(); i.hasNext(); ) {
            s.append(i.next().toString());
            s.append(newLine);
        }

        s.append("}");
        s.append(newLine);

        return s.toString();
    }

    /**
     * Removes a mark at a specific index.
     *
     * @param index the index of the mark to remove.
     * @return the removed mark.
     */
    public Mark remove(int index) {
        return marks.remove(index);
    }

    /**
     * Removes two marks at specified indices.
     *
     * @param index1 the first index.
     * @param index2 the second index.
     */
    public void removeTwo(int index1, int index2) {
        int maxIndex = Math.max(index1, index2);
        int minIndex = Math.min(index1, index2);
        marks.remove(maxIndex);
        marks.remove(minIndex);
    }

    /**
     * Generates a random index within the collection.
     *
     * @param randomNumberGenerator the random number generator to use.
     * @return a random index.
     */
    public final int randomIndex(RandomNumberGenerator randomNumberGenerator) {
        return randomNumberGenerator.sampleIntFromRange(size());
    }

    /**
     * Gets a mark at a specific index.
     *
     * @param index the index of the mark to get.
     * @return the mark at the specified index.
     */
    public Mark get(int index) {
        return marks.get(index);
    }

    /**
     * Gets a random mark from the collection.
     *
     * @param randomNumberGenerator the random number generator to use.
     * @return a randomly selected mark.
     */
    public final Mark randomMark(RandomNumberGenerator randomNumberGenerator) {
        return marks.get(randomIndex(randomNumberGenerator));
    }

    /**
     * Replaces the mark at a specific index.
     *
     * @param index the index to replace.
     * @param markToAssign the new mark to assign.
     */
    public final void exchange(int index, Mark markToAssign) {
        marks.set(index, markToAssign);
    }

    /**
     * Finds the index of a specific mark in the collection.
     *
     * @param mark the mark to find.
     * @return the index of the mark, or -1 if not found.
     */
    public int indexOf(Mark mark) {
        return marks.indexOf(mark);
    }

    /**
     * Derives objects from the marks in the collection.
     *
     * @param dimensions the dimensions to use.
     * @param regionMembership the region membership to consider.
     * @return a collection of derived objects.
     */
    public ObjectCollection deriveObjects(
            Dimensions dimensions, RegionMembershipWithFlags regionMembership) {
        return ObjectCollectionFactory.filterAndMapFrom(
                marks,
                mark -> mark.numberRegions() > regionMembership.getRegionID(),
                mark ->
                        mark.deriveObject(
                                dimensions, regionMembership, BinaryValuesByte.getDefault()));
    }

    /**
     * Scales all marks in the collection in X and Y dimensions.
     *
     * @param scaleFactor how much to scale by.
     * @throws CheckedUnsupportedOperationException if scaling is not supported for any mark.
     */
    public void scaleXY(ScaleFactor scaleFactor) throws CheckedUnsupportedOperationException {
        for (Mark mark : marks) {
            mark.scale(scaleFactor);
        }
    }

    /**
     * Finds marks at a specific point that belong to a specific region.
     *
     * @param point the point to check.
     * @param regionMap the region map to use.
     * @param regionID the ID of the region to consider.
     * @return a new MarkCollection containing the matching marks.
     */
    public MarkCollection marksAt(Point3i point, RegionMap regionMap, int regionID) {

        MarkCollection marksOut = new MarkCollection();

        RegionMembership region = regionMap.membershipForIndex(regionID);
        byte flags = region.flags();

        // We cycle through each item in the configuration
        for (Mark mark : this) {

            byte membership = mark.isPointInside(point);
            if (region.isMemberFlag(membership, flags)) {
                marksOut.add(mark);
            }
        }
        return marksOut;
    }

    /**
     * Checks if this collection is deeply equal to another.
     *
     * @param other the other collection to compare with.
     * @return true if the collections are deeply equal.
     */
    public boolean equalsDeep(MarkCollection other) {

        // Size
        if (size() != other.size()) {
            return false;
        }

        int i = 0;
        for (Mark m : this) {

            if (!m.equalsDeep(other.get(i++))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Creates a map of marks indexed by their IDs.
     *
     * @return a Map where keys are mark IDs and values are the corresponding marks.
     */
    public Map<Integer, Mark> createIdHashMap() {

        HashMap<Integer, Mark> hashMap = new HashMap<>();

        for (Mark mark : this) {
            hashMap.put(mark.getIdentifier(), mark);
        }

        return hashMap;
    }

    /**
     * Creates an array of mark IDs.
     *
     * @return an array containing the IDs of all marks in the collection.
     */
    public int[] createIdArr() {

        int[] idArr = new int[size()];

        int i = 0;
        for (Mark mark : this) {
            idArr[i++] = mark.getIdentifier();
        }

        return idArr;
    }

    /**
     * Creates a set of all marks in the collection.
     *
     * @return a Set containing all marks in the collection.
     */
    public Set<Mark> createSet() {

        HashSet<Mark> hashMap = new HashSet<>();

        for (Mark mark : this) {
            hashMap.add(mark);
        }

        return hashMap;
    }

    /**
     * Creates a map of marks to their indices in the collection.
     *
     * @return a Map where keys are marks and values are their indices.
     */
    public Map<Mark, Integer> createHashMapToId() {

        HashMap<Mark, Integer> hashMap = new HashMap<>();

        for (int i = 0; i < size(); i++) {
            hashMap.put(get(i), i);
        }

        return hashMap;
    }

    /**
     * Creates a new collection by merging this collection with another.
     *
     * @param toMerge the collection to merge with.
     * @return a new MarkCollection containing all unique marks from both collections.
     */
    public MarkCollection createMerged(MarkCollection toMerge) {

        MarkCollection mergedNew = shallowCopy();

        Set<Mark> set = mergedNew.createSet();

        for (Mark mark : toMerge) {
            if (!set.contains(mark)) {
                mergedNew.add(mark);
            }
        }

        return mergedNew;
    }

    /**
     * Creates a list of bounding boxes for all marks in the collection.
     *
     * @param dimensions the dimensions to use.
     * @param regionID the ID of the region to consider.
     * @return a List of BoundingBox objects.
     */
    public List<BoundingBox> boxList(Dimensions dimensions, int regionID) {

        ArrayList<BoundingBox> list = new ArrayList<>();
        for (Mark m : this) {
            list.add(m.box(dimensions, regionID));
        }
        return list;
    }

    /**
     * Replaces the mark at a specific index.
     *
     * @param index the index to replace.
     * @param element the new mark to set.
     * @return the mark previously at the specified position.
     */
    public Mark set(int index, Mark element) {
        return marks.set(index, element);
    }
}
