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

package org.anchoranalysis.image.io.objects.deserialize;

import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.bean.xml.RegisterBeanFactories;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.GetterFromIndex;
import org.anchoranalysis.core.index.container.BoundedIndexContainer;
import org.anchoranalysis.core.index.container.BoundsFromRange;
import org.anchoranalysis.core.serialize.DeserializationFailedException;
import org.anchoranalysis.core.serialize.Deserializer;
import org.anchoranalysis.image.io.bean.stack.StackReader;
import org.anchoranalysis.image.voxel.object.ObjectCollection;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.image.voxel.object.factory.ObjectCollectionFactory;
import org.anchoranalysis.io.manifest.directory.sequenced.SequencedDirectory;
import org.anchoranalysis.io.manifest.directory.sequenced.SequencedDirectoryDeserializer;
import org.anchoranalysis.io.manifest.sequencetype.SequenceTypeException;

class ReadObjectsFromTIFFDirectory implements Deserializer<ObjectCollection> {

    @Override
    public ObjectCollection deserialize(Path folderPath) throws DeserializationFailedException {
        return readObjects(
                folderPath, RegisterBeanFactories.getDefaultInstances().get(StackReader.class));
    }

    private ObjectCollection readObjects(Path folderPath, StackReader stackReader)
            throws DeserializationFailedException {

        try {
            BoundedIndexContainer<ObjectMask> container = deserializeFromDirectory(
                    new SerializedObjectsFromDirectory(folderPath, Optional.of("*.ser")),
                    new ObjectDualDeserializer(stackReader)
            ); 
            return createFromContainer(container);

        } catch (SequenceTypeException | CreateException e) {
            throw new DeserializationFailedException(e);
        }
    }

    private static ObjectCollection createFromContainer(BoundedIndexContainer<ObjectMask> container)
            throws CreateException {
        try {
            return ObjectCollectionFactory.mapFromRange(
                    container.getMinimumIndex(),
                    container.getMaximumIndex() + 1,
                    GetOperationFailedException.class,
                    container::get);

        } catch (GetOperationFailedException e) {
            throw new CreateException(e);
        }
    }
    
    private static <T> BoundedIndexContainer<T> deserializeFromDirectory(SequencedDirectory directory, Deserializer<T> deserializer) {
        GetterFromIndex<T> container = new SequencedDirectoryDeserializer<>(directory, deserializer);
        return new BoundsFromRange<>(container, directory.getAssociatedElementRange());
    }
}
