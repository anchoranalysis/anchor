/*-
 * #%L
 * anchor-mpp-io
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

package org.anchoranalysis.mpp.io.marks.generator;

import java.util.List;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.identifier.getter.IdentifierGetter;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.stack.RGBStack;
import org.anchoranalysis.overlay.Overlay;
import org.anchoranalysis.overlay.bean.DrawObject;
import org.anchoranalysis.overlay.collection.ColoredOverlayCollection;
import org.anchoranalysis.overlay.writer.DrawOverlay;
import org.anchoranalysis.overlay.writer.ObjectDrawAttributes;
import org.anchoranalysis.overlay.writer.PrecalculationOverlay;
import org.anchoranalysis.spatial.box.BoundingBox;

/**
 * Converts a configuration to a set of object-masks, using a simple {@link DrawObject} for all
 * objects.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public class SimpleOverlayWriter extends DrawOverlay {

    private final DrawObject drawObject;

    @Override
    public void writePrecalculatedOverlays(
            List<PrecalculationOverlay> precalculatedMasks,
            Dimensions dimensions,
            RGBStack background,
            ObjectDrawAttributes attributes,
            BoundingBox restrictTo)
            throws OperationFailedException {

        for (int i = 0; i < precalculatedMasks.size(); i++) {
            precalculatedMasks.get(i).writePrecalculatedMask(background, attributes, i, restrictTo);
        }
    }

    @Override
    public void writeOverlaysIfIntersects(
            ColoredOverlayCollection overlays,
            RGBStack stack,
            IdentifierGetter<Overlay> idGetter,
            List<BoundingBox> intersectList)
            throws OperationFailedException {

        writeOverlays(
                overlays.subsetWhereBBoxIntersects(stack.dimensions(), this, intersectList),
                stack,
                idGetter);
    }

    @Override
    public DrawObject getDrawObject() {
        return drawObject;
    }
}
