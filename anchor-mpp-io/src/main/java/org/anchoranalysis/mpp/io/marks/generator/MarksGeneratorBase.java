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

import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.identifier.getter.IdentifierGetter;
import org.anchoranalysis.image.core.stack.DisplayStack;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.core.stack.rgb.RGBStack;
import org.anchoranalysis.image.io.generator.raster.RasterGeneratorSelectFormat;
import org.anchoranalysis.image.io.stack.ConvertDisplayStackToRGB;
import org.anchoranalysis.image.io.stack.StackWriteOptions;
import org.anchoranalysis.image.io.stack.StackWriteOptionsFactory;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.mpp.io.marks.ColoredMarksWithDisplayStack;
import org.anchoranalysis.mpp.overlay.OverlayCollectionMarkFactory;
import org.anchoranalysis.overlay.Overlay;
import org.anchoranalysis.overlay.collection.ColoredOverlayCollection;
import org.anchoranalysis.overlay.writer.DrawOverlay;

public abstract class MarksGeneratorBase
        extends RasterGeneratorSelectFormat<ColoredMarksWithDisplayStack> {

    @Getter @Setter private String manifestDescriptionFunction = "marks";

    private DrawOverlay writer;
    private IdentifierGetter<Overlay> idGetter;
    private RegionMembershipWithFlags regionMembership;

    public MarksGeneratorBase(
            DrawOverlay writer,
            IdentifierGetter<Overlay> idGetter,
            RegionMembershipWithFlags regionMembership) {
        super();
        this.writer = writer;
        this.idGetter = idGetter;
        this.regionMembership = regionMembership;
    }

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
    public Optional<ManifestDescription> createManifestDescription() {
        return Optional.of(new ManifestDescription("raster", manifestDescriptionFunction));
    }

    @Override
    public StackWriteOptions guaranteedImageAttributes() {
        return StackWriteOptionsFactory.rgbMaybe3D();
    }

    protected abstract DisplayStack background(DisplayStack stack) throws OperationFailedException;
}
