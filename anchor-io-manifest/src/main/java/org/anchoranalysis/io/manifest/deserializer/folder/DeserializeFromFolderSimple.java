/* (C)2020 */
package org.anchoranalysis.io.manifest.deserializer.folder;

import org.anchoranalysis.core.index.ITypedGetFromIndex;
import org.anchoranalysis.io.bean.deserializer.Deserializer;
import org.anchoranalysis.io.manifest.deserializer.folder.sequenced.SequencedFolderDeserializer;
import org.anchoranalysis.io.manifest.folder.SequencedFolder;

public class DeserializeFromFolderSimple<T> extends DeserializeFromFolder<T> {

    private Deserializer<T> deserializer;

    public DeserializeFromFolderSimple(Deserializer<T> deserializer, SequencedFolder folder) {
        super(folder);
        this.deserializer = deserializer;
    }

    @Override
    protected ITypedGetFromIndex<T> createCtnr(SequencedFolder folder) {
        return new SequencedFolderDeserializer<>(folder, deserializer);
    }
}
