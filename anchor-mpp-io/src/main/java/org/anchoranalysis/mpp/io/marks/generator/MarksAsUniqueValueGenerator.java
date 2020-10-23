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
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.object.properties.ObjectCollectionWithProperties;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.io.generator.raster.RasterGeneratorSelectFormat;
import org.anchoranalysis.image.io.generator.raster.object.collection.ObjectsAsUniqueValueGenerator;
import org.anchoranalysis.image.io.stack.StackWriteOptions;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.mpp.mark.MarkCollection;

public class MarksAsUniqueValueGenerator extends RasterGeneratorSelectFormat<MarkCollection> {

    private ObjectsAsUniqueValueGenerator delegate;
    private RegionMembershipWithFlags regionMembership;

    public MarksAsUniqueValueGenerator(
            Dimensions dimensions, RegionMembershipWithFlags regionMembership) {
        delegate = new ObjectsAsUniqueValueGenerator(dimensions);
        this.regionMembership = regionMembership;
    }

    @Override
    public Stack transform(MarkCollection element) throws OutputWriteFailedException {

        ObjectCollectionWithProperties objects =
                element.deriveObjects(delegate.dimensions(), this.regionMembership);
        return delegate.transform(objects.withoutProperties());
    }

    @Override
    public Optional<ManifestDescription> createManifestDescription() {
        return delegate.createManifestDescription();
    }

    @Override
    public StackWriteOptions guaranteedImageAttributes() {
        return delegate.guaranteedImageAttributes();
    }
}
