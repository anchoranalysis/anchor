/* (C)2020 */
package org.anchoranalysis.io.manifest.folder;

import java.util.List;
import org.anchoranalysis.io.manifest.file.FileWrite;
import org.anchoranalysis.io.manifest.sequencetype.SequenceType;

public interface SequencedFolder {

    void findFileFromIndex(List<FileWrite> foundList, String index, boolean recursive);

    SequenceType getAssociatedSequence();
}
