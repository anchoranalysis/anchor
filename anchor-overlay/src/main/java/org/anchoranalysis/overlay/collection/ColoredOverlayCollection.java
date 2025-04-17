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
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.anchoranalysis.core.color.ColorList;
import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.overlay.Overlay;
import org.anchoranalysis.overlay.writer.DrawOverlay;
import org.anchoranalysis.spatial.box.BoundingBox;

/**
 * Like a {@link OverlayCollection} but additionally associates a color with each overlay.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public class ColoredOverlayCollection implements Iterable<Overlay> {

    /** The collection of overlays, each element corresponding to {@code colors}. */
    @Getter private OverlayCollection overlays;

    /** The list of colors, each element corresponding to {@code overlays}. */
    @Getter private ColorList colors;

    /** Create an empty collection. */
    public ColoredOverlayCollection() {
        overlays = new OverlayCollection();
        colors = new ColorList();
    }

    /**
     * Append an overlay and its respective color to the end of the list..
     *
     * @param overlay the overlay to append.
     * @param color the corresponding color of the overlay.
     */
    public void add(Overlay overlay, RGBColor color) {
        colors.add(color);
        overlays.add(overlay);
    }

    @Override
    public Iterator<Overlay> iterator() {
        return overlays.iterator();
    }

    /**
     * The total number of elements in the list.
     *
     * @return the total number of elements.
     */
    public int size() {
        return overlays.size();
    }

    /**
     * Access a particular {@link Overlay} in the collection by index.
     *
     * @param index the index (starting at 0).
     * @return the respective element at index {@code index}.
     * @throws IndexOutOfBoundsException if the index is out of range ({@code index &lt; 0 || index
     *     &gt;= size()})
     */
    public Overlay getOverlay(int index) {
        return overlays.get(index);
    }

    /**
     * Access a particular color in the collection by index.
     *
     * @param index the index (starting at 0).
     * @return the respective element at index {@code index}.
     * @throws IndexOutOfBoundsException if the index is out of range ({@code index &lt; 0 || index
     *     &gt;= size()})
     */
    public RGBColor getColor(int index) {
        return colors.get(index);
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

    /**
     * Find overlays whose bounding-boxes intersect with any of the boxes in {@code
     * toIntersectWith}.
     *
     * @param scene the size of the image in which all bounding-boxes must fully fit inside.
     * @param drawOverlay what draws the overlays on the image, and thus determines the bounding-box
     *     of an overlay.
     * @param toIntersectWith the list of boxes against which elements are searched for any
     *     intersection.
     * @return a newly created {@link ColoredOverlayCollection} containing the elements (uncopied)
     *     which match the criteria. This may be empty if no elements match the criteria.
     */
    public ColoredOverlayCollection subsetWhereBoxIntersects(
            Dimensions scene, DrawOverlay drawOverlay, List<BoundingBox> toIntersectWith) {

        // TODO - make more efficient using RTrees

        ColoredOverlayCollection out = new ColoredOverlayCollection();

        for (int i = 0; i < size(); i++) {

            Overlay overlay = getOverlay(i);

            if (overlay.box(drawOverlay, scene).intersection().existsWithAny(toIntersectWith)) {
                out.add(overlay, getColor(i));
            }
        }
        return out;
    }
}
