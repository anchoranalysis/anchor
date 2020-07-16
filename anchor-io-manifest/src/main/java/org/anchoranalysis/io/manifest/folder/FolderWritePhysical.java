/* (C)2020 */
package org.anchoranalysis.io.manifest.folder;

import java.nio.file.Path;
import java.util.List;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.file.FileWrite;
import org.anchoranalysis.io.manifest.match.Match;

public class FolderWritePhysical extends FolderWriteWithPath {

    /** */
    private static final long serialVersionUID = 8992970758732036941L;

    private FileList delegate = new FileList(this);

    public FolderWritePhysical() {
        super();
    }

    // Finds a folder a comparator matches
    @Override
    public void findFile(List<FileWrite> foundList, Match<FileWrite> match, boolean recursive) {
        delegate.findFile(foundList, match, recursive);
    }

    public void add(FileWrite fw) {
        delegate.add(fw);
    }

    @Override
    public void write(
            String outputName,
            ManifestDescription manifestDescription,
            Path outFilePath,
            String index) {
        delegate.write(outputName, manifestDescription, outFilePath, index);
    }

    @Override
    public List<FileWrite> fileList() {
        return delegate.getFileList();
    }
}
