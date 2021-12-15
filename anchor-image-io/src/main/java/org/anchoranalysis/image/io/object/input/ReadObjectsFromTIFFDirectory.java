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

package org.anchoranalysis.image.io.object.input;

import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.bean.xml.RegisterBeanFactories;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.format.NonImageFileFormat;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.serialize.DeserializationFailedException;
import org.anchoranalysis.core.serialize.Deserializer;
import org.anchoranalysis.core.time.OperationContext;
import org.anchoranalysis.image.io.bean.stack.reader.StackReader;
import org.anchoranalysis.image.voxel.object.ObjectCollection;
import org.anchoranalysis.image.voxel.object.ObjectCollectionFactory;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.io.manifest.directory.sequenced.SequencedDirectory;
import org.anchoranalysis.io.manifest.directory.sequenced.SequencedDirectoryDeserializer;
import org.anchoranalysis.io.manifest.directory.sequenced.SupplierAtIndex;
import org.anchoranalysis.io.manifest.sequencetype.SequenceTypeException;

class ReadObjectsFromTIFFDirectory implements Deserializer<ObjectCollection> {

    @Override
    public ObjectCollection deserialize(Path folderPath, OperationContext context)
            throws DeserializationFailedException {
        StackReader stackReader =
                RegisterBeanFactories.getDefaultInstances()
                        .getInstanceFor(StackReader.class)
                        .orElseThrow(
                                () ->
                                        new DeserializationFailedException(
                                                "No default StackReader is defined, as is required."));
        return readObjects(folderPath, stackReader, context);
    }

    private ObjectCollection readObjects(
            Path folderPath, StackReader stackReader, OperationContext context)
            throws DeserializationFailedException {

        try {
            String acceptFilter = "*" + NonImageFileFormat.SERIALIZED_BINARY.extensionWithPeriod();
            BoundsFromRange<ObjectMask> container =
                    deserializeFromDirectory(
                            new SerializedObjectsFromDirectory(
                                    folderPath, Optional.of(acceptFilter)),
                            new ObjectDualDeserializer(stackReader),
                            context);
            return createFromContainer(container);

        } catch (SequenceTypeException | CreateException e) {
            throw new DeserializationFailedException(e);
        }
    }

    private static ObjectCollection createFromContainer(BoundsFromRange<ObjectMask> container)
            throws CreateException {
        try {
            return ObjectCollectionFactory.mapFromRange(
                    container.getRange().getMinimumIndex(),
                    container.getRange().getMaximumIndex() + 1,
                    GetOperationFailedException.class,
                    container::get);

        } catch (GetOperationFailedException e) {
            throw new CreateException(e);
        }
    }

    private static <T> BoundsFromRange<T> deserializeFromDirectory(
            SequencedDirectory directory, Deserializer<T> deserializer, OperationContext context) {
        SupplierAtIndex<T> container =
                new SequencedDirectoryDeserializer<>(directory, deserializer, context);
        return new BoundsFromRange<>(container, directory.getAssociatedElementRange());
    }
}
