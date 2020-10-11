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
import org.anchoranalysis.image.io.bean.stack.StackReader;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.object.factory.ObjectCollectionFactory;
import org.anchoranalysis.io.manifest.deserializer.DeserializationFailedException;
import org.anchoranalysis.io.manifest.deserializer.Deserializer;
import org.anchoranalysis.io.manifest.deserializer.folder.DeserializeFromFolder;
import org.anchoranalysis.io.manifest.deserializer.folder.DeserializeFromFolderSimple;
import org.anchoranalysis.io.manifest.deserializer.folder.LoadContainer;
import org.anchoranalysis.io.manifest.sequencetype.SequenceTypeException;

class ReadObjectsFromTIFFDirectory implements Deserializer<ObjectCollection> {

    @Override
    public ObjectCollection deserialize(Path folderPath) throws DeserializationFailedException {
        return readWithRaster(
                folderPath, RegisterBeanFactories.getDefaultInstances().get(StackReader.class));
    }

    private ObjectCollection readWithRaster(Path folderPath, StackReader stackReader)
            throws DeserializationFailedException {

        try {
            DeserializeFromFolder<ObjectMask> deserializeFolder =
                    new DeserializeFromFolderSimple<>(
                            new ObjectDualDeserializer(stackReader),
                            new SerializedObjectSetFolderSource(folderPath, Optional.of("*.ser")));

            return createFromLoadContainer(deserializeFolder.create());

        } catch (SequenceTypeException | CreateException e) {
            throw new DeserializationFailedException(e);
        }
    }

    private static ObjectCollection createFromLoadContainer(LoadContainer<ObjectMask> lc)
            throws CreateException {
        try {
            return ObjectCollectionFactory.mapFromRange(
                    lc.getContainer().getMinimumIndex(),
                    lc.getContainer().getMaximumIndex() + 1,
                    GetOperationFailedException.class,
                    index -> lc.getContainer().get(index));

        } catch (GetOperationFailedException e) {
            throw new CreateException(e);
        }
    }
}
