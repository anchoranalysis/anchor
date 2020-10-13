/*-
 * #%L
 * anchor-image-io
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

package org.anchoranalysis.image.io.objects;

import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.image.io.generator.raster.object.ObjectWithBoundingBoxGenerator;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.io.generator.Generator;
import org.anchoranalysis.io.generator.GeneratorBridge;
import org.anchoranalysis.io.generator.collection.CollectionGenerator;

/**
 * Writes each object as a raster-image in a directory.
 *
 * <p>Writes the corner information as a binary-serialized file in the directory
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RasterDirectoryObjectsGenerator {

    public static Generator<ObjectCollection> create() {

        // Creates a raster for each object inside a a directory
        Generator<ObjectMask> objectGenerator =
                new ObjectWithBoundingBoxGenerator(Optional.empty());

        // Creates a subfolder for each List of objects passed to the generator
        // We must use a list as it is required to be of type Collection<T> where T is the type
        // being iterated
        // We don't specify a sceneres as we don't know what images they belong to
        CollectionGenerator<ObjectMask> listGenerator =
                new CollectionGenerator<>(objectGenerator, "objs");

        // Finally we expose the list-generator as an ObjectCollection generator externally
        return GeneratorBridge.createOneToOne(listGenerator, ObjectCollection::asList);
    }
}
