/* (C)2020 */
package org.anchoranalysis.io.manifest.folder;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.file.FileWrite;
import org.anchoranalysis.io.manifest.match.Match;

// A stub folder which means it represents an experiment applied to a particular file
public class ExperimentFileFolder extends FolderWriteWithPath {

    /** */
    private static final long serialVersionUID = 4923049916867897251L;

    @Override
    public void write(
            String outputName,
            ManifestDescription manifestDescription,
            Path outFilePath,
            String index) {
        // NOTHING TO DO
    }

    @Override
    public void findFile(List<FileWrite> foundList, Match<FileWrite> match, boolean recursive) {
        // NOTHING TO DO
    }

    @Override
    public List<FileWrite> fileList() {
        return new ArrayList<>();
    }
}
