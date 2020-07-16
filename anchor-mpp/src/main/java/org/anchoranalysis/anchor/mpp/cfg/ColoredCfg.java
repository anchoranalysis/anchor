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

package org.anchoranalysis.anchor.mpp.cfg;

import java.awt.Color;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.color.ColorList;
import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.core.idgetter.IDGetter;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDimensions;

public class ColoredCfg implements Iterable<Mark> {

    private Cfg cfg;
    private ColorList colorList;

    public ColoredCfg() {
        this(new Cfg(), new ColorList());
    }

    public ColoredCfg(Cfg cfg, ColorList colorList) {
        super();
        this.cfg = cfg;
        this.colorList = colorList;
    }

    // NB, this changes the IDs of the marks
    public ColoredCfg(Cfg cfg, ColorIndex colorIndex, IDGetter<Mark> colorIDGetter) {
        super();
        this.cfg = cfg;

        this.colorList = new ColorList();

        for (int i = 0; i < cfg.size(); i++) {
            Mark m = cfg.get(i);
            this.colorList.add(colorIndex.get(colorIDGetter.getID(m, i)));
        }
    }

    public ColoredCfg(Mark mark, RGBColor color) {
        super();
        this.cfg = new Cfg(mark);
        this.colorList = new ColorList(color);
    }

    public Cfg getCfg() {
        return cfg;
    }

    public ColorList getColorList() {
        return colorList;
    }

    public void add(Mark mark, Color color) {
        add(mark, new RGBColor(color));
    }

    public void addChangeID(Mark mark, Color color) {
        addChangeID(mark, new RGBColor(color));
    }

    public void addChangeID(Mark mark, RGBColor color) {
        cfg.add(mark);
        mark.setId(colorList.addWithIndex(color));
    }

    public void add(Mark mark, RGBColor color) {
        cfg.add(mark);
        colorList.add(color);
    }

    public void addAll(Cfg cfg, RGBColor color) {
        for (Mark mark : cfg) {
            add(mark, color);
        }
    }

    public void addAll(ColoredCfg cfg) {
        for (int i = 0; i < cfg.size(); i++) {
            add(cfg.getCfg().get(i), cfg.colorList.get(i));
        }
    }

    @Override
    public Iterator<Mark> iterator() {
        return cfg.iterator();
    }

    public final int size() {
        return cfg.size();
    }

    public ColoredCfg deepCopy() {

        ColoredCfg newCfg = new ColoredCfg();
        newCfg.cfg = cfg.deepCopy();
        newCfg.colorList = colorList.deepCopy();
        return newCfg;
    }

    public ColoredCfg shallowCopy() {

        ColoredCfg newCfg = new ColoredCfg();
        newCfg.cfg = cfg.shallowCopy();
        newCfg.colorList = colorList.shallowCopy();
        return newCfg;
    }

    public ColoredCfg createMerged(ColoredCfg toMerge) {

        ColoredCfg mergedNew = shallowCopy();

        Set<Mark> set = mergedNew.getCfg().createSet();

        for (int i = 0; i < toMerge.size(); i++) {
            Mark m = toMerge.getCfg().get(i);

            if (!set.contains(m)) {
                mergedNew.getCfg().add(m);
                mergedNew.getColorList().add(toMerge.getColorList().get(i));
            }
        }
        return mergedNew;
    }

    // Calculates mask
    public ColoredCfg subsetWhereBBoxIntersects(
            ImageDimensions bndScene, int regionID, List<BoundingBox> intersectList) {

        ColoredCfg intersectCfg = new ColoredCfg();
        for (int i = 0; i < getCfg().size(); i++) {
            Mark mark = getCfg().get(i);

            if (mark.bbox(bndScene, regionID).intersection().existsWithAny(intersectList)) {
                intersectCfg.add(mark.duplicate(), getColorList().get(i));
            }
        }
        return intersectCfg;
    }

    public void remove(int index) {
        colorList.remove(index);
        cfg.remove(index);
    }
}
