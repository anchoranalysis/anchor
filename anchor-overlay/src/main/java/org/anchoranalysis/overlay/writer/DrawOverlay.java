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

import lombok.AllArgsConstructor;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.identifier.getter.IdentifierGetter;
import org.anchoranalysis.core.identifier.getter.IdentifyFromIteration;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.object.properties.ObjectWithProperties;
import org.anchoranalysis.image.core.stack.RGBStack;
import org.anchoranalysis.image.voxel.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.overlay.Overlay;
import org.anchoranalysis.overlay.bean.DrawObject;
import org.anchoranalysis.overlay.collection.ColoredOverlayCollection;
import org.anchoranalysis.spatial.box.BoundingBox;

/**
 * Draws an ovleray onto a {@link RGBStack}
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public class DrawOverlay {

    /**
     * The binary-values to use when converting an {@link Overlay} to a {@link
     * ObjectMaskWithProperties}.
     */
    private static final BinaryValuesByte BINARY_VALUES_OBJECT_MASK = BinaryValuesByte.getDefault();

    /** The means of getting an ID from a {@link Overlay} to establish a color. */
    private static final IdentifyFromIteration<Overlay> IDENTIFIER_GETTER_COLOR =
            new IdentifyFromIteration<>();

    private static final String PROPERTY_COLOR_ID = "colorID";

    /** How the overlay is drawn after being converted to a {@link ObjectMask}. */
    private final DrawObject drawObject;

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
        BoundingBox boxContainer = new BoundingBox(stack.extent());
        try {
            ObjectDrawAttributes attributes =
                    deriveAttributes(
                            overlays, idGetter, new IdentifierFromProperty(PROPERTY_COLOR_ID));

            Dimensions dimensions = stack.dimensions();

            for (int i = 0; i < overlays.size(); i++) {
                ObjectWithProperties object = extractOverlayAsObject(overlays, i, dimensions);
                drawObject.drawSingle(object, stack, attributes, i, boxContainer);
            }
        } catch (CreateException e) {
            throw new OperationFailedException(e);
        }
    }

    /**
     * Extracts a {@link Overlay} at position {@code index} in {@code overlays} and converts into a
     * {@link ObjectMaskWithProperties} including an identifier.
     */
    private static ObjectWithProperties extractOverlayAsObject(
            ColoredOverlayCollection overlays, int index, Dimensions dimensions)
            throws CreateException {
        Overlay overlay = overlays.getOverlay(index);
        ObjectWithProperties object = overlay.createObject(dimensions, BINARY_VALUES_OBJECT_MASK);
        object.setProperty(
                PROPERTY_COLOR_ID, IDENTIFIER_GETTER_COLOR.getIdentifier(overlay, index));
        return object;
    }

    /**
     * Derives {@link ObjectDrawAttributes} from a {@link ColoredOverlayCollection} getting
     * appropriate identifiers.
     */
    private static ObjectDrawAttributes deriveAttributes(
            ColoredOverlayCollection overlays,
            IdentifierGetter<Overlay> identiferOverlay,
            IdentifierGetter<ObjectWithProperties> idenitifierColor) {
        IdentifierGetter<ObjectWithProperties> idGetterMask =
                new IdentifyDelegateToOverlays(identiferOverlay, overlays, true);
        return new ObjectDrawAttributes(overlays.getColors(), idGetterMask, idenitifierColor);
    }
}
