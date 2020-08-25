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
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.image.extent.Dimensions;
import org.anchoranalysis.image.io.generator.raster.RasterGenerator;
import org.anchoranalysis.image.io.generator.raster.object.collection.ObjectsAsUniqueValueGenerator;
import org.anchoranalysis.image.object.properties.ObjectCollectionWithProperties;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.generator.Generator;
import org.anchoranalysis.io.generator.IterableGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.mpp.mark.MarkCollection;

public class MarksAsUniqueValueGenerator extends RasterGenerator
        implements IterableGenerator<MarkCollection> {

    private ObjectsAsUniqueValueGenerator delegate;
    private MarkCollection marks;
    private RegionMembershipWithFlags rm;

    public MarksAsUniqueValueGenerator(
            Dimensions dimensions, RegionMembershipWithFlags rm) {
        delegate = new ObjectsAsUniqueValueGenerator(dimensions);
        this.rm = rm;
    }

    public MarksAsUniqueValueGenerator(
            Dimensions dimensions, RegionMembershipWithFlags rm, MarkCollection marks) {
        this(dimensions, rm);
        this.marks = marks;
    }

    @Override
    public boolean isRGB() {
        return delegate.isRGB();
    }

    @Override
    public Stack generate() throws OutputWriteFailedException {

        ObjectCollectionWithProperties objects =
                marks.deriveObjects(delegate.dimensions(), this.rm);
        try {
            delegate.setIterableElement(objects.withoutProperties());
        } catch (SetOperationFailedException e) {
            throw new OutputWriteFailedException(e);
        }
        return delegate.generate();
    }

    @Override
    public Optional<ManifestDescription> createManifestDescription() {
        return delegate.createManifestDescription();
    }

    @Override
    public MarkCollection getIterableElement() {
        return marks;
    }

    @Override
    public void setIterableElement(MarkCollection element) throws SetOperationFailedException {
        this.marks = element;
    }

    @Override
    public void start() throws OutputWriteFailedException {
        delegate.start();
    }

    @Override
    public void end() throws OutputWriteFailedException {
        delegate.end();
    }

    @Override
    public Generator getGenerator() {
        return this;
    }
}
