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

import java.awt.Color;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.color.ColorList;
import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.core.identifier.getter.IdentifierGetter;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.spatial.box.BoundingBox;

/**
 * A collection of marks, each associated with a color.
 */
@AllArgsConstructor
public class ColoredMarks implements Iterable<Mark> {

    @Getter private MarkCollection marks;
    @Getter private ColorList colorList;

    /**
     * Creates an empty ColoredMarks instance.
     */
    public ColoredMarks() {
        this(new MarkCollection(), new ColorList());
    }

    /**
     * Creates a ColoredMarks instance from a MarkCollection and assigns colors based on a ColorIndex.
     * Note: This constructor changes the IDs of the marks.
     *
     * @param marks the collection of marks
     * @param colorIndex the color index to use for assigning colors
     * @param colorIDGetter a function to get color IDs for marks
     */
    public ColoredMarks(
            MarkCollection marks, ColorIndex colorIndex, IdentifierGetter<Mark> colorIDGetter) {
        super();
        this.marks = marks;
        this.colorList = new ColorList();

        for (int index = 0; index < marks.size(); index++) {
            int colorID = colorIDGetter.getIdentifier(marks.get(index), index);
            this.colorList.add(colorIndex.get(colorID));
        }
    }

    /**
     * Creates a ColoredMarks instance with a single mark and color.
     *
     * @param mark the mark to add
     * @param color the color for the mark
     */
    public ColoredMarks(Mark mark, RGBColor color) {
        super();
        this.marks = new MarkCollection(mark);
        this.colorList = new ColorList(color);
    }

    /**
     * Adds a mark with an associated color.
     *
     * @param mark the mark to add
     * @param color the color for the mark
     */
    public void add(Mark mark, Color color) {
        add(mark, new RGBColor(color));
    }

    /**
     * Adds a mark, changes its ID, and associates it with a color.
     *
     * @param mark the mark to add
     * @param color the color for the mark
     */
    public void addChangeID(Mark mark, Color color) {
        addChangeID(mark, new RGBColor(color));
    }

    /**
     * Adds a mark, changes its ID, and associates it with an RGBColor.
     *
     * @param mark the mark to add
     * @param color the RGBColor for the mark
     */
    public void addChangeID(Mark mark, RGBColor color) {
        marks.add(mark);
        colorList.add(color);
        mark.setId(colorList.size() - 1);
    }

    /**
     * Adds a mark with an associated RGBColor.
     *
     * @param mark the mark to add
     * @param color the RGBColor for the mark
     */
    public void add(Mark mark, RGBColor color) {
        marks.add(mark);
        colorList.add(color);
    }

    /**
     * Adds all marks from a MarkCollection with the same color.
     *
     * @param marks the MarkCollection to add from
     * @param color the color for all added marks
     */
    public void addAll(MarkCollection marks, RGBColor color) {
        for (Mark mark : marks) {
            add(mark, color);
        }
    }

    /**
     * Adds all marks and colors from another ColoredMarks instance.
     *
     * @param marks the ColoredMarks to add from
     */
    public void addAll(ColoredMarks marks) {
        for (int i = 0; i < marks.size(); i++) {
            add(marks.getMarks().get(i), marks.colorList.get(i));
        }
    }

    @Override
    public Iterator<Mark> iterator() {
        return marks.iterator();
    }

    /**
     * Returns the number of marks in the collection.
     *
     * @return the size of the collection
     */
    public final int size() {
        return marks.size();
    }

    /**
     * Creates a deep copy of this ColoredMarks instance.
     *
     * @return a new ColoredMarks instance with copied marks and colors
     */
    public ColoredMarks deepCopy() {
        ColoredMarks out = new ColoredMarks();
        out.marks = marks.deepCopy();
        out.colorList = colorList.deepCopy();
        return out;
    }

    /**
     * Creates a shallow copy of this ColoredMarks instance.
     *
     * @return a new ColoredMarks instance with the same marks and colors
     */
    public ColoredMarks shallowCopy() {
        ColoredMarks out = new ColoredMarks();
        out.marks = marks.shallowCopy();
        out.colorList = colorList.shallowCopy();
        return out;
    }

    /**
     * Merges this ColoredMarks with another, avoiding duplicates.
     *
     * @param toMerge the ColoredMarks to merge with
     * @return a new ColoredMarks instance with merged marks and colors
     */
    public ColoredMarks createMerged(ColoredMarks toMerge) {
        ColoredMarks mergedNew = shallowCopy();
        Set<Mark> set = mergedNew.getMarks().createSet();

        for (int i = 0; i < toMerge.size(); i++) {
            Mark m = toMerge.getMarks().get(i);
            if (!set.contains(m)) {
                mergedNew.getMarks().add(m);
                mergedNew.getColorList().add(toMerge.getColorList().get(i));
            }
        }
        return mergedNew;
    }

    /**
     * Creates a subset of marks whose bounding boxes intersect with given boxes.
     *
     * @param dimensions the dimensions to use for bounding box calculations
     * @param regionID the region ID to use for bounding box calculations
     * @param intersectList the list of bounding boxes to check for intersection
     * @return a new ColoredMarks instance with the intersecting marks
     */
    public ColoredMarks subsetWhereBBoxIntersects(
            Dimensions dimensions, int regionID, List<BoundingBox> intersectList) {
        ColoredMarks intersectingMarks = new ColoredMarks();
        for (int i = 0; i < getMarks().size(); i++) {
            Mark mark = getMarks().get(i);
            if (mark.box(dimensions, regionID).intersection().existsWithAny(intersectList)) {
                intersectingMarks.add(mark.duplicate(), getColorList().get(i));
            }
        }
        return intersectingMarks;
    }

    /**
     * Removes a mark and its associated color at the specified index.
     *
     * @param index the index of the mark to remove
     */
    public void remove(int index) {
        colorList.remove(index);
        marks.remove(index);
    }
}