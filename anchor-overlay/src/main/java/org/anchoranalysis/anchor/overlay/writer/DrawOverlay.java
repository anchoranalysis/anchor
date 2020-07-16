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
/* (C)2020 */
package org.anchoranalysis.anchor.overlay.writer;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.anchoranalysis.anchor.overlay.Overlay;
import org.anchoranalysis.anchor.overlay.bean.DrawObject;
import org.anchoranalysis.anchor.overlay.collection.ColoredOverlayCollection;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.CheckedStream;
import org.anchoranalysis.core.idgetter.IDGetter;
import org.anchoranalysis.core.idgetter.IDGetterIter;
import org.anchoranalysis.image.binary.values.BinaryValues;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.object.properties.IDGetterObjectWithProperties;
import org.anchoranalysis.image.object.properties.ObjectWithProperties;
import org.anchoranalysis.image.stack.rgb.RGBStack;

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
     * @param oc overlays
     * @param stack overlays are written onto this stack
     * @param idGetter
     * @param factory
     * @throws OperationFailedException
     */
    public void writeOverlays(
            ColoredOverlayCollection oc, RGBStack stack, IDGetter<Overlay> idGetter)
            throws OperationFailedException {
        writeOverlays(
                oc,
                stack.getDimensions(),
                stack,
                idGetter,
                new BoundingBox(stack.getDimensions().getExtent()));
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
            ImageDimensions dimensions,
            RGBStack background,
            IDGetter<Overlay> idGetter,
            BoundingBox bboxContainer)
            throws OperationFailedException {

        try {
            List<PrecalcOverlay> masksPreprocessed =
                    precalculate(
                            overlays, this, dimensions, BinaryValues.getDefault().createByte());

            // TODO, can't we read the color directly from the cfg in some way?
            writePrecalculatedOverlays(
                    masksPreprocessed,
                    dimensions,
                    background,
                    ObjectDrawAttributesFactory.createFromOverlays(
                            overlays, idGetter, new IDGetterObjectWithProperties("colorID")),
                    bboxContainer);
        } catch (CreateException e) {
            throw new OperationFailedException(e);
        }
    }

    // dim should be for the ENTIRE cfg, not just the bit in bboxContainer
    public abstract void writePrecalculatedOverlays(
            List<PrecalcOverlay> precalculatedMasks,
            ImageDimensions dimensions,
            RGBStack background,
            ObjectDrawAttributes attributes,
            BoundingBox restrictTo)
            throws OperationFailedException;

    public abstract void writeOverlaysIfIntersects(
            ColoredOverlayCollection overlays,
            RGBStack stack,
            IDGetter<Overlay> idGetter,
            List<BoundingBox> intersectList)
            throws OperationFailedException;

    // Does computationally-intensive preprocessing (so it can be cached). Any object can be used,
    // but
    // there should be exactly one object
    //  per Mark in the cfg, in the same order as the Cfg is inputted
    public static List<PrecalcOverlay> precalculate(
            ColoredOverlayCollection coc,
            DrawOverlay maskWriter,
            ImageDimensions dimensions,
            BinaryValuesByte bvOut)
            throws CreateException {

        IDGetterIter<Overlay> colorIDGetter = new IDGetterIter<>();

        return CheckedStream.mapToObjWithException(
                        IntStream.range(0, coc.size()),
                        CreateException.class,
                        index -> {
                            Overlay overlay = coc.get(index);

                            ObjectWithProperties object =
                                    overlay.createObject(maskWriter, dimensions, bvOut);
                            object.setProperty("colorID", colorIDGetter.getID(overlay, index));

                            return createPrecalc(maskWriter, object, dimensions);
                        })
                .collect(Collectors.toList());
    }

    public static PrecalcOverlay createPrecalc(
            DrawOverlay maskWriter, ObjectWithProperties om, ImageDimensions dimensions)
            throws CreateException {
        return maskWriter.getDrawObject().precalculate(om, dimensions);
    }
}
