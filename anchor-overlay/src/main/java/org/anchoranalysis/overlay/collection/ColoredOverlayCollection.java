/*-
 * #%L
 * anchor-overlay
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

package org.anchoranalysis.overlay.collection;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.IntPredicate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.anchoranalysis.core.color.ColorList;
import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.overlay.Overlay;
import org.anchoranalysis.overlay.writer.DrawOverlay;
import org.anchoranalysis.spatial.box.BoundingBox;

@AllArgsConstructor
public class ColoredOverlayCollection implements Iterable<Overlay> {

    @Getter private OverlayCollection overlays;
    @Getter private ColorList colors;

    public ColoredOverlayCollection() {
        overlays = new OverlayCollection();
        colors = new ColorList();
    }

    public boolean add(Overlay overlay, RGBColor color) {
        colors.add(color);
        return overlays.add(overlay);
    }

    @Override
    public Iterator<Overlay> iterator() {
        return overlays.iterator();
    }

    public int size() {
        return overlays.size();
    }

    public Overlay remove(int index) {
        colors.remove(index);
        return overlays.remove(index);
    }

    public Overlay get(int index) {
        return overlays.get(index);
    }

    public RGBColor getColor(int index) {
        return colors.get(index);
    }

    public OverlayCollection withoutColor() {
        return overlays;
    }

    public List<BoundingBox> boxList(DrawOverlay drawOverlay, Dimensions dim) {
        return overlays.boxList(drawOverlay, dim);
    }

    public Set<Overlay> createSet() {
        return overlays.createSet();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{\n");
        for (int i = 0; i < overlays.size(); i++) {
            RGBColor color = colors.get(i);
            Overlay overlay = overlays.get(i);
            builder.append(String.format("col=%s\tol=%s%n", color, overlay));
        }
        builder.append("}\n");
        return builder.toString();
    }

    public ColoredOverlayCollection createSubsetFromIDs(IntPredicate predicateOnIndex) {

        ColoredOverlayCollection out = new ColoredOverlayCollection();

        // This our current
        for (int i = 0; i < size(); i++) {
            Overlay overlay = get(i);

            if (predicateOnIndex.test(overlay.getIdentifier())) {
                out.add(overlay, getColors().get(i));
            }
        }

        return out;
    }

    // TODO - make more efficient using RTrees
    public ColoredOverlayCollection subsetWhereBBoxIntersects(
            Dimensions scene, DrawOverlay drawOverlay, List<BoundingBox> toIntersectWith) {

        ColoredOverlayCollection out = new ColoredOverlayCollection();

        for (int i = 0; i < size(); i++) {

            Overlay overlay = get(i);

            if (overlay.box(drawOverlay, scene).intersection().existsWithAny(toIntersectWith)) {
                out.add(overlay, getColor(i));
            }
        }
        return out;
    }

    // Everything from the two Markss which isn't in the intersection
    public static OverlayCollection createIntersectionComplement(
            ColoredOverlayCollection overlays1, ColoredOverlayCollection overlays2) {

        OverlayCollection out = new OverlayCollection();

        if (overlays2 == null) {
            out.addAll(overlays1.withoutColor());
            return out;
        }

        Set<Overlay> set1 = overlays1.createSet();
        Set<Overlay> set2 = overlays2.createSet();

        for (Overlay overlay : overlays1) {
            if (!set2.contains(overlay)) {
                out.add(overlay);
            }
        }

        for (Overlay overlay : overlays2) {
            if (!set1.contains(overlay)) {
                out.add(overlay);
            }
        }

        return out;
    }
}
