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

import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.identifier.getter.IdentifierGetter;
import org.anchoranalysis.image.core.stack.DisplayStack;
import org.anchoranalysis.image.core.stack.RGBStack;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.io.stack.ConvertDisplayStackToRGB;
import org.anchoranalysis.image.io.stack.output.StackWriteAttributes;
import org.anchoranalysis.image.io.stack.output.StackWriteAttributesFactory;
import org.anchoranalysis.image.io.stack.output.generator.RasterGeneratorSelectFormat;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.mpp.io.marks.ColoredMarksWithDisplayStack;
import org.anchoranalysis.mpp.overlay.OverlayCollectionMarkFactory;
import org.anchoranalysis.overlay.Overlay;
import org.anchoranalysis.overlay.collection.ColoredOverlayCollection;
import org.anchoranalysis.overlay.writer.DrawOverlay;

@RequiredArgsConstructor
public abstract class MarksGeneratorBase
        extends RasterGeneratorSelectFormat<ColoredMarksWithDisplayStack> {

    // START REQUIRED FIELDS
    private final DrawOverlay writer;
    private final IdentifierGetter<Overlay> idGetter;
    private final RegionMembershipWithFlags regionMembership;
    // START REQUIRED FIELDS

    @Override
    public Stack transform(ColoredMarksWithDisplayStack element) throws OutputWriteFailedException {
        try {
            RGBStack stack = ConvertDisplayStackToRGB.convert(background(element.getStack()));

            ColoredOverlayCollection overlays =
                    OverlayCollectionMarkFactory.createColor(
                            element.getMarksColored(), regionMembership);

            writer.writeOverlays(overlays, stack, idGetter);

            return stack.asStack();

        } catch (OperationFailedException e) {
            throw new OutputWriteFailedException(e);
        }
    }

    @Override
    public StackWriteAttributes guaranteedImageAttributes() {
        return StackWriteAttributesFactory.rgbMaybe3D(false);
    }

    protected abstract DisplayStack background(DisplayStack stack) throws OperationFailedException;
}
