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
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.anchoranalysis.core.error.OptionalOperationUnsupportedException;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.random.RandomNumberGenerator;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Dimensions;
import org.anchoranalysis.image.object.properties.ObjectCollectionWithProperties;
import org.anchoranalysis.image.object.properties.ObjectCollectionWithPropertiesFactory;
import org.anchoranalysis.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.mpp.bean.regionmap.RegionMembership;
import org.anchoranalysis.mpp.bean.regionmap.RegionMembershipWithFlags;

/**
 * A collection of marks, termed a <i>configuration</i> in marked-point-processes academic literature.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public final class MarkCollection implements Iterable<Mark>, Serializable {

    /** */
    private static final long serialVersionUID = 2398855316191681489L;

    @Getter private final List<Mark> marks;

    public MarkCollection() {
        this(new ArrayList<>());
    }

    public MarkCollection(Stream<Mark> stream) {
        this(stream.collect(Collectors.toList()));
    }

    public MarkCollection(Mark mark) {
        this();
        add(mark);
    }

    public MarkCollection shallowCopy() {
        return new MarkCollection(marks.stream());
    }

    public MarkCollection deepCopy() {
        return new MarkCollection(marks.stream().map(Mark::duplicate));
    }

    public boolean add(Mark arg0) {
        return marks.add(arg0);
    }

    public void addAll(MarkCollection marks) {
        for (Mark m : marks) {
            add(m);
        }
    }

    public boolean contains(Object arg0) {
        return marks.contains(arg0);
    }

    public final boolean isEmpty() {
        return marks.isEmpty();
    }

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

    public Mark remove(int index) {
        return marks.remove(index);
    }

    public void removeTwo(int index1, int index2) {

        int maxIndex = Math.max(index1, index2);
        int minIndex = Math.min(index1, index2);

        // Remove the second index first
        marks.remove(maxIndex);
        marks.remove(minIndex);
    }

    public final int randomIndex(RandomNumberGenerator randomNumberGenerator) {
        return randomNumberGenerator.sampleIntFromRange(size());
    }

    public Mark get(int index) {
        return marks.get(index);
    }

    public final Mark randomMark(RandomNumberGenerator randomNumberGenerator) {
        return marks.get(randomIndex(randomNumberGenerator));
    }

    public final void exchange(int index, Mark markToAssign) {
        marks.set(index, markToAssign);
    }

    // Inefficient, be careful with usage
    public int indexOf(Mark mark) {
        return marks.indexOf(mark);
    }

    public ObjectCollectionWithProperties deriveObjects(
            Dimensions dimensions, RegionMembershipWithFlags regionMembership) {

        return ObjectCollectionWithPropertiesFactory.filterAndMapFrom(
                marks,
                mark -> mark.numberRegions() > regionMembership.getRegionID(),
                mark ->
                        mark.deriveObject(
                                dimensions, regionMembership, BinaryValuesByte.getDefault()));
    }

    /**
     * Scales the mark in X and Y dimensions.
     * 
     * @param scaleFactor how much to scale by.
     * @throws OptionalOperationUnsupportedException if the type of mark used in the annotation does not supported scaling.
     */
    public void scaleXY(double scaleFactor) throws OptionalOperationUnsupportedException {

        for (Mark mark : marks) {
            mark.scale(scaleFactor);
        }
    }

    public MarkCollection marksAt(Point3d point, RegionMap regionMap, int regionID) {

        MarkCollection marksOut = new MarkCollection();

        RegionMembership rm = regionMap.membershipForIndex(regionID);
        byte flags = rm.flags();

        // We cycle through each item in the configuration
        for (Mark m : this) {

            byte membership = m.isPointInside(point);
            if (rm.isMemberFlag(membership, flags)) {
                marksOut.add(m);
            }
        }
        return marksOut;
    }

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

    // A hashmap of all the marks, using the Id as an index
    public Map<Integer, Mark> createIdHashMap() {

        HashMap<Integer, Mark> hashMap = new HashMap<>();

        for (Mark mark : this) {
            hashMap.put(mark.getId(), mark);
        }

        return hashMap;
    }

    public int[] createIdArr() {

        int[] idArr = new int[size()];

        int i = 0;
        for (Mark mark : this) {
            idArr[i++] = mark.getId();
        }

        return idArr;
    }

    // A hashmap of all the marks, using the Id as an index
    public Set<Mark> createSet() {

        HashSet<Mark> hashMap = new HashSet<>();

        for (Mark mark : this) {
            hashMap.add(mark);
        }

        return hashMap;
    }

    // A hashmap of all the marks, using the Id as an index, mapping to the id
    public Map<Mark, Integer> createHashMapToId() {

        HashMap<Mark, Integer> hashMap = new HashMap<>();

        for (int i = 0; i < size(); i++) {
            hashMap.put(get(i), i);
        }

        return hashMap;
    }

    public MarkCollection createMerged(MarkCollection toMerge) {

        MarkCollection mergedNew = shallowCopy();

        Set<Mark> set = mergedNew.createSet();

        for (Mark m : toMerge) {
            if (!set.contains(m)) {
                mergedNew.add(m);
            }
        }

        return mergedNew;
    }

    public List<BoundingBox> boxList(Dimensions bndScene, int regionID) {

        ArrayList<BoundingBox> list = new ArrayList<>();
        for (Mark m : this) {
            list.add(m.box(bndScene, regionID));
        }
        return list;
    }

    public Mark set(int index, Mark element) {
        return marks.set(index, element);
    }
}
