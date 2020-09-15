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
import org.anchoranalysis.core.idgetter.IDGetter;
import org.anchoranalysis.image.extent.Dimensions;
import org.anchoranalysis.image.extent.box.BoundingBox;

@AllArgsConstructor
public class ColoredMarks implements Iterable<Mark> {

    @Getter private MarkCollection marks;
    @Getter private ColorList colorList;

    public ColoredMarks() {
        this(new MarkCollection(), new ColorList());
    }

    // NB, this changes the IDs of the marks
    public ColoredMarks(MarkCollection marks, ColorIndex colorIndex, IDGetter<Mark> colorIDGetter) {
        super();
        this.marks = marks;

        this.colorList = new ColorList();

        for (int index = 0; index < marks.size(); index++) {
            int colorID = colorIDGetter.getID(marks.get(index), index);
            this.colorList.add(colorIndex.get(colorID));
        }
    }

    public ColoredMarks(Mark mark, RGBColor color) {
        super();
        this.marks = new MarkCollection(mark);
        this.colorList = new ColorList(color);
    }

    public void add(Mark mark, Color color) {
        add(mark, new RGBColor(color));
    }

    public void addChangeID(Mark mark, Color color) {
        addChangeID(mark, new RGBColor(color));
    }

    public void addChangeID(Mark mark, RGBColor color) {
        marks.add(mark);
        mark.setId(colorList.addWithIndex(color));
    }

    public void add(Mark mark, RGBColor color) {
        marks.add(mark);
        colorList.add(color);
    }

    public void addAll(MarkCollection marks, RGBColor color) {
        for (Mark mark : marks) {
            add(mark, color);
        }
    }

    public void addAll(ColoredMarks marks) {
        for (int i = 0; i < marks.size(); i++) {
            add(marks.getMarks().get(i), marks.colorList.get(i));
        }
    }

    @Override
    public Iterator<Mark> iterator() {
        return marks.iterator();
    }

    public final int size() {
        return marks.size();
    }

    public ColoredMarks deepCopy() {

        ColoredMarks out = new ColoredMarks();
        out.marks = marks.deepCopy();
        out.colorList = colorList.deepCopy();
        return out;
    }

    public ColoredMarks shallowCopy() {

        ColoredMarks out = new ColoredMarks();
        out.marks = marks.shallowCopy();
        out.colorList = colorList.shallowCopy();
        return out;
    }

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

    public ColoredMarks subsetWhereBBoxIntersects(
            Dimensions bndScene, int regionID, List<BoundingBox> intersectList) {

        ColoredMarks intersectingMarks = new ColoredMarks();
        for (int i = 0; i < getMarks().size(); i++) {
            Mark mark = getMarks().get(i);

            if (mark.box(bndScene, regionID).intersection().existsWithAny(intersectList)) {
                intersectingMarks.add(mark.duplicate(), getColorList().get(i));
            }
        }
        return intersectingMarks;
    }

    public void remove(int index) {
        colorList.remove(index);
        marks.remove(index);
    }
}
