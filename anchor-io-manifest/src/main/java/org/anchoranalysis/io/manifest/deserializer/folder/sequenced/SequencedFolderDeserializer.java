/* (C)2020 */
package org.anchoranalysis.io.manifest.deserializer.folder.sequenced;

import java.nio.file.Path;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.io.bean.deserializer.Deserializer;
import org.anchoranalysis.io.deserializer.DeserializationFailedException;
import org.anchoranalysis.io.manifest.folder.SequencedFolder;

public class SequencedFolderDeserializer<T> extends SequencedFolderCntrCreator<T> {

    private Deserializer<T> deserializer;

    public SequencedFolderDeserializer(SequencedFolder rootFolder, Deserializer<T> deserializer) {
        super(rootFolder);
        this.deserializer = deserializer;
    }

    @Override
    protected T createFromFilePath(Path path) throws CreateException {
        try {
            return deserializer.deserialize(path);
        } catch (DeserializationFailedException e) {
            throw new CreateException(e);
        }
    }
}
