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

package org.anchoranalysis.mpp.overlay;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.mpp.mark.ColoredMarks;
import org.anchoranalysis.mpp.mark.Mark;
import org.anchoranalysis.mpp.mark.MarkCollection;
import org.anchoranalysis.overlay.Overlay;
import org.anchoranalysis.overlay.collection.ColoredOverlayCollection;
import org.anchoranalysis.overlay.collection.OverlayCollection;

/**
 * Two-way factory.
 *
 * <p>Creation of OverlayCollection from marks Retrieval of marks back from OverlayCollections
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OverlayCollectionMarkFactory {

    public static OverlayCollection createWithoutColor(
            MarkCollection marks, RegionMembershipWithFlags regionMembership) {
        OverlayCollection out = new OverlayCollection();

        for (int index = 0; index < marks.size(); index++) {
            Mark mark = marks.get(index);
            out.add(new OverlayMark(mark, regionMembership));
        }

        return out;
    }

    public static ColoredOverlayCollection createColor(
            ColoredMarks marks, RegionMembershipWithFlags regionMembership) {
        return createColor(marks.getMarks(), marks.getColorList(), regionMembership);
    }

    private static ColoredOverlayCollection createColor(
            MarkCollection marks,
            ColorIndex colorIndex,
            RegionMembershipWithFlags regionMembership) {

        ColoredOverlayCollection out = new ColoredOverlayCollection();

        for (int i = 0; i < marks.size(); i++) {
            out.add(new OverlayMark(marks.get(i), regionMembership), colorIndex.get(i));
        }
        return out;
    }

    // Creates a marks from whatever Overlays are found in the collection
    public static MarkCollection marksFromOverlays(OverlayCollection overlays) {
        MarkCollection out = new MarkCollection();

        for (int i = 0; i < overlays.size(); i++) {
            Overlay overlay = overlays.get(i);

            if (overlay instanceof OverlayMark) {
                OverlayMark overlayMark = (OverlayMark) overlay;
                out.add(overlayMark.getMark());
            }
        }

        return out;
    }

    // Creates a marks from whatever Overlays are found in the collection
    public static ColoredMarks marksFromOverlays(ColoredOverlayCollection overlays) {
        ColoredMarks out = new ColoredMarks();

        for (int i = 0; i < overlays.size(); i++) {
            Overlay overlay = overlays.get(i);

            RGBColor col = overlays.getColor(i);

            if (overlay instanceof OverlayMark) {
                OverlayMark overlayMark = (OverlayMark) overlay;
                out.add(overlayMark.getMark(), col);
            }
        }

        return out;
    }
}
