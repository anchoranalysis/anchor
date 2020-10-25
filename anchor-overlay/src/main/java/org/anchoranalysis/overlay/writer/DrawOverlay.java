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

package org.anchoranalysis.overlay.writer;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.functional.CheckedStream;
import org.anchoranalysis.core.identifier.getter.IdentifierGetter;
import org.anchoranalysis.core.identifier.getter.IdentifyByIteration;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.object.properties.IdentifierByProperty;
import org.anchoranalysis.image.core.object.properties.ObjectWithProperties;
import org.anchoranalysis.image.core.stack.RGBStack;
import org.anchoranalysis.image.voxel.binary.values.BinaryValues;
import org.anchoranalysis.image.voxel.binary.values.BinaryValuesByte;
import org.anchoranalysis.overlay.Overlay;
import org.anchoranalysis.overlay.bean.DrawObject;
import org.anchoranalysis.overlay.collection.ColoredOverlayCollection;
import org.anchoranalysis.spatial.box.BoundingBox;

/**
 * Draws an overlay onto a RGB-stack, including precalculated overlays.
 *
 * @author Owen Feehan
 */
public abstract class DrawOverlay {

    public abstract DrawObject getDrawObject();

    /**
     * Writes a collection of colored-overlays to the background
     *
     * @param overlays overlays
     * @param stack overlays are written onto this stack
     * @param idGetter
     * @throws OperationFailedException
     */
    public void writeOverlays(
            ColoredOverlayCollection overlays, RGBStack stack, IdentifierGetter<Overlay> idGetter)
            throws OperationFailedException {
        writeOverlays(
                overlays, stack.dimensions(), stack, idGetter, new BoundingBox(stack.extent()));
    }

    //
    // It's a two step process
    //   First we generate object-mask for the configuration
    //   Then these are written to the RGBMask
    //
    //   We split the steps in two, so that they can be potentially cached
    //
    private void writeOverlays(
            ColoredOverlayCollection overlays,
            Dimensions dimensions,
            RGBStack background,
            IdentifierGetter<Overlay> idGetter,
            BoundingBox boxContainer)
            throws OperationFailedException {

        try {
            List<PrecalculationOverlay> overlaysPreprocessed =
                    precalculate(
                            overlays, this, dimensions, BinaryValues.getDefault().createByte());

            // TODO, can't we read the color directly from the marks in some way?
            writePrecalculatedOverlays(
                    overlaysPreprocessed,
                    dimensions,
                    background,
                    ObjectDrawAttributesFactory.createFromOverlays(
                            overlays, idGetter, new IdentifierByProperty("colorID")),
                    boxContainer);
        } catch (CreateException e) {
            throw new OperationFailedException(e);
        }
    }

    // dim should be for the ENTIRE marks, not just the bit in boxContainer
    public abstract void writePrecalculatedOverlays(
            List<PrecalculationOverlay> precalculatedMasks,
            Dimensions dimensions,
            RGBStack background,
            ObjectDrawAttributes attributes,
            BoundingBox restrictTo)
            throws OperationFailedException;

    public abstract void writeOverlaysIfIntersects(
            ColoredOverlayCollection overlays,
            RGBStack stack,
            IdentifierGetter<Overlay> idGetter,
            List<BoundingBox> intersectList)
            throws OperationFailedException;

    // Does computationally-intensive preprocessing (so it can be cached). Any object can be used,
    // but
    // there should be exactly one object
    //  per Mark in the marks, in the same order as the Marks is inputted
    public static List<PrecalculationOverlay> precalculate(
            ColoredOverlayCollection coc,
            DrawOverlay drawOverlay,
            Dimensions dimensions,
            BinaryValuesByte bvOut)
            throws CreateException {

        IdentifyByIteration<Overlay> colorIDGetter = new IdentifyByIteration<>();

        return CheckedStream.mapToObj(
                        IntStream.range(0, coc.size()),
                        CreateException.class,
                        index -> {
                            Overlay overlay = coc.get(index);

                            ObjectWithProperties object =
                                    overlay.createObject(drawOverlay, dimensions, bvOut);
                            object.setProperty(
                                    "colorID", colorIDGetter.getIdentifier(overlay, index));

                            return createPrecalc(drawOverlay, object, dimensions);
                        })
                .collect(Collectors.toList());
    }

    public static PrecalculationOverlay createPrecalc(
            DrawOverlay drawOverlay, ObjectWithProperties object, Dimensions dimensions)
            throws CreateException {
        return drawOverlay.getDrawObject().precalculate(object, dimensions);
    }
}
