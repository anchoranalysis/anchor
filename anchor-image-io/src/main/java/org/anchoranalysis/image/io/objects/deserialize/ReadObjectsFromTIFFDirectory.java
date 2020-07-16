/* (C)2020 */
package org.anchoranalysis.image.io.objects.deserialize;

import java.nio.file.Path;
import org.anchoranalysis.bean.xml.RegisterBeanFactories;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.image.io.bean.rasterreader.RasterReader;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.object.ObjectCollectionFactory;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.io.bean.deserializer.Deserializer;
import org.anchoranalysis.io.deserializer.DeserializationFailedException;
import org.anchoranalysis.io.manifest.deserializer.folder.DeserializeFromFolder;
import org.anchoranalysis.io.manifest.deserializer.folder.DeserializeFromFolderSimple;
import org.anchoranalysis.io.manifest.deserializer.folder.LoadContainer;
import org.anchoranalysis.io.manifest.sequencetype.SequenceTypeException;
import org.anchoranalysis.io.manifest.serialized.SerializedObjectSetFolderSource;

class ReadObjectsFromTIFFDirectory implements Deserializer<ObjectCollection> {

    @Override
    public ObjectCollection deserialize(Path folderPath) throws DeserializationFailedException {
        return readWithRaster(
                folderPath, RegisterBeanFactories.getDefaultInstances().get(RasterReader.class));
    }

    private ObjectCollection readWithRaster(Path folderPath, RasterReader rasterReader)
            throws DeserializationFailedException {

        try {
            DeserializeFromFolder<ObjectMask> deserializeFolder =
                    new DeserializeFromFolderSimple<>(
                            new ObjectDualDeserializer(rasterReader),
                            new SerializedObjectSetFolderSource(folderPath, "*.ser"));

            return createFromLoadContainer(deserializeFolder.create());

        } catch (SequenceTypeException | CreateException e) {
            throw new DeserializationFailedException(e);
        }
    }

    private static ObjectCollection createFromLoadContainer(LoadContainer<ObjectMask> lc)
            throws CreateException {
        try {
            return ObjectCollectionFactory.mapFromRange(
                    lc.getCntr().getMinimumIndex(),
                    lc.getCntr().getMaximumIndex() + 1,
                    GetOperationFailedException.class,
                    index -> lc.getCntr().get(index));

        } catch (GetOperationFailedException e) {
            throw new CreateException(e);
        }
    }
}
