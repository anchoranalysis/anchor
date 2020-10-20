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

import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.identifier.getter.IdentifierGetter;
import org.anchoranalysis.image.core.stack.DisplayStack;
import org.anchoranalysis.image.io.bean.object.draw.Flatten;
import org.anchoranalysis.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.overlay.Overlay;
import org.anchoranalysis.overlay.bean.DrawObject;
import org.anchoranalysis.overlay.writer.DrawOverlay;

public class MarksFlattenedGenerator extends MarksGeneratorBase {

    // We cache the last background, and background MIP
    private DisplayStack cachedBackground;
    private DisplayStack cachedBackgroundMIP;

    public MarksFlattenedGenerator(
            DrawObject drawObject,
            IdentifierGetter<Overlay> idGetter,
            RegionMembershipWithFlags regionMembership) {
        super(createWriter(drawObject), idGetter, regionMembership);
    }

    @Override
    protected DisplayStack background(DisplayStack stack) throws OperationFailedException {
        // We avoid repeating the same calculation using a cache
        if (stack != cachedBackground) {
            cachedBackground = stack;
            cachedBackgroundMIP = stack.maximumIntensityProjection();
        }

        return cachedBackgroundMIP;
    }

    private static DrawOverlay createWriter(DrawObject drawObject) {
        return new SimpleOverlayWriter(new Flatten(drawObject));
    }
}
