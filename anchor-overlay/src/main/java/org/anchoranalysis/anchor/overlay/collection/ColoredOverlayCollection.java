/* (C)2020 */
package org.anchoranalysis.anchor.overlay.collection;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.anchoranalysis.anchor.overlay.Overlay;
import org.anchoranalysis.anchor.overlay.writer.DrawOverlay;
import org.anchoranalysis.core.color.ColorList;
import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.core.index.IndicesSelection;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDimensions;

@AllArgsConstructor
public class ColoredOverlayCollection implements Iterable<Overlay> {

    @Getter private OverlayCollection overlays;
    private ColorList colors;

    public ColoredOverlayCollection() {
        overlays = new OverlayCollection();
        colors = new ColorList();
    }

    public boolean add(Overlay e, RGBColor color) {
        colors.add(color);
        return overlays.add(e);
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        for (int i = 0; i < overlays.size(); i++) {
            RGBColor col = colors.get(i);
            Overlay ol = overlays.get(i);
            sb.append(String.format("col=%s\tol=%s%n", col, ol));
        }
        sb.append("}\n");
        return sb.toString();
    }

    public ColorList getColorList() {
        return colors;
    }

    public ColoredOverlayCollection createSubsetFromIDs(IndicesSelection indices) {

        ColoredOverlayCollection out = new ColoredOverlayCollection();

        // This our current
        for (int i = 0; i < size(); i++) {
            Overlay overlay = get(i);

            if (indices.contains(overlay.getId())) {
                out.add(overlay, getColorList().get(i));
            }
        }

        return out;
    }

    // TODO - make more efficient using RTrees
    public ColoredOverlayCollection subsetWhereBBoxIntersects(
            ImageDimensions bndScene, DrawOverlay maskWriter, List<BoundingBox> intersectList) {

        ColoredOverlayCollection out = new ColoredOverlayCollection();

        for (int i = 0; i < size(); i++) {

            Overlay overlay = get(i);

            if (overlay.bbox(maskWriter, bndScene).intersection().existsWithAny(intersectList)) {
                out.add(overlay, getColor(i));
            }
        }
        return out;
    }

    // Everything from the two Cfgs which isn't in the intersection
    public static OverlayCollection createIntersectionComplement(
            ColoredOverlayCollection cfg1, ColoredOverlayCollection cfg2) {

        OverlayCollection out = new OverlayCollection();

        if (cfg2 == null) {
            out.addAll(cfg1.withoutColor());
            return out;
        }

        Set<Overlay> set1 = cfg1.createSet();
        Set<Overlay> set2 = cfg2.createSet();

        for (Overlay m : cfg1) {
            if (!set2.contains(m)) {
                out.add(m);
            }
        }

        for (Overlay m : cfg2) {
            if (!set1.contains(m)) {
                out.add(m);
            }
        }

        return out;
    }

    public OverlayCollection withoutColor() {
        return overlays;
    }

    public List<BoundingBox> bboxList(DrawOverlay maskWriter, ImageDimensions dim) {
        return overlays.bboxList(maskWriter, dim);
    }

    public Set<Overlay> createSet() {
        return overlays.createSet();
    }
}
