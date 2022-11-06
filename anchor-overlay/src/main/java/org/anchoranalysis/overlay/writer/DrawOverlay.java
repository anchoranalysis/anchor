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
import org.anchoranalysis.core.identifier.getter.IdentifyFromIteration;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.object.properties.ObjectWithProperties;
import org.anchoranalysis.image.core.stack.RGBStack;
import org.anchoranalysis.image.voxel.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.voxel.binary.values.BinaryValuesInt;
import org.anchoranalysis.overlay.Overlay;
import org.anchoranalysis.overlay.bean.DrawObject;
import org.anchoranalysis.overlay.collection.ColoredOverlayCollection;
import org.anchoranalysis.spatial.box.BoundingBox;

/**
 * Draws a {@Overlay} onto a {@link RGBStack}, including precalculated overlays.
 *
 * @author Owen Feehan
 */
public abstract class DrawOverlay {

    private static final String PROPERTY_COLOR_ID = "colorID";

    public abstract DrawObject getDrawObject();

    /**
     * Draw a collection of colored-overlays on top of a {@link RGBStack}.
     *
     * @param overlays the overlays to write, together with their associated color.
     * @param stack the image to write overlays onto.
     * @param idGetter gets an id from an {@link Overlay}.
     * @throws OperationFailedException if the operation cannot complete successfully.
     */
    public void drawOverlays(
            ColoredOverlayCollection overlays, RGBStack stack, IdentifierGetter<Overlay> idGetter)
            throws OperationFailedException {
        drawOverlays(
                overlays, stack.dimensions(), stack, idGetter, new BoundingBox(stack.extent()));
    }

    //
    // It's a two step process
    //   First we generate object-mask for the configuration
    //   Then these are written to the RGBMask
    //
    //   We split the steps in two, so that they can be potentially cached
    //
    private void drawOverlays(
            ColoredOverlayCollection overlays,
            Dimensions dimensions,
            RGBStack background,
            IdentifierGetter<Overlay> idGetter,
            BoundingBox boxContainer)
            throws OperationFailedException {

        try {
            List<PrecalculationOverlay> overlaysPreprocessed =
                    precalculate(overlays, this, dimensions, BinaryValuesInt.getDefault().asByte());

            drawPrecalculatedOverlays(
                    overlaysPreprocessed,
                    dimensions,
                    background,
                    createFromOverlays(
                            overlays, idGetter, new IdentifierFromProperty(PROPERTY_COLOR_ID)),
                    boxContainer);
        } catch (CreateException e) {
            throw new OperationFailedException(e);
        }
    }

    /**
     * Write precalculated overlays onto the {@link RGBStack}.
     *
     * @param precalculatedMasks
     * @param dimensions dimensions for the <i>entire</i> masks, not just those in the container.
     * @param background
     * @param attributes
     * @param restrictTo
     * @throws OperationFailedException
     */
    public abstract void drawPrecalculatedOverlays(
            List<PrecalculationOverlay> precalculatedMasks,
            Dimensions dimensions,
            RGBStack background,
            ObjectDrawAttributes attributes,
            BoundingBox restrictTo)
            throws OperationFailedException;

    public abstract void drawOverlaysIfIntersects(
            ColoredOverlayCollection overlays,
            RGBStack stack,
            IdentifierGetter<Overlay> identifierGetter,
            List<BoundingBox> intersectList)
            throws OperationFailedException;

    // Does computationally-intensive preprocessing (so it can be cached). Any object can be used,
    // but
    // there should be exactly one object
    //  per Mark in the marks, in the same order as the Marks is inputted
    public static List<PrecalculationOverlay> precalculate(
            ColoredOverlayCollection overlays,
            DrawOverlay drawOverlay,
            Dimensions dimensions,
            BinaryValuesByte bvOut)
            throws CreateException {

        IdentifyFromIteration<Overlay> colorIDGetter = new IdentifyFromIteration<>();

        return CheckedStream.mapToObj(
                        IntStream.range(0, overlays.size()),
                        CreateException.class,
                        index -> {
                            Overlay overlay = overlays.getOverlay(index);

                            ObjectWithProperties object =
                                    overlay.createObject(drawOverlay, dimensions, bvOut);
                            object.setProperty(
                                    PROPERTY_COLOR_ID, colorIDGetter.getIdentifier(overlay, index));

                            return createPrecalculation(drawOverlay, object, dimensions);
                        })
                .collect(Collectors.toList());
    }

    private static PrecalculationOverlay createPrecalculation(
            DrawOverlay drawOverlay, ObjectWithProperties object, Dimensions dimensions)
            throws CreateException {
        return drawOverlay.getDrawObject().precalculate(object, dimensions);
    }
    
    private static ObjectDrawAttributes createFromOverlays(
            ColoredOverlayCollection overlays,
            IdentifierGetter<Overlay> idGetter,
            IdentifierGetter<ObjectWithProperties> idGetterColor) {
    	IdentifierGetter<ObjectWithProperties> idGetterMask = new IdentifyDelegateToOverlays(idGetter, overlays, true);
        return new ObjectDrawAttributes(
                overlays.getColors(), idGetterMask, idGetterColor);
    }
}
