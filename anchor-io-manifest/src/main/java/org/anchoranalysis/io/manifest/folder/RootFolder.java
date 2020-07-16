/* (C)2020 */
package org.anchoranalysis.io.manifest.folder;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.List;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.file.FileWrite;
import org.anchoranalysis.io.manifest.match.Match;

public class RootFolder extends FolderWrite implements Serializable {

    /** */
    private static final long serialVersionUID = -939826653076766321L;

    private final FileList delegate;

    // We don't want to serialize this, as its temporary state (and an error will be thrown as
    // WindowsPath is not serializable)
    private final transient Path rootPath;

    public RootFolder(Path rootPath) {
        super();
        this.rootPath = rootPath;
        delegate = new FileList(this);
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
    public Path getRelativePath() {
        return rootPath;
    }

    @Override
    public void findFile(List<FileWrite> foundList, Match<FileWrite> match, boolean recursive) {
        delegate.findFile(foundList, match, recursive);
    }

    @Override
    public List<FileWrite> fileList() {
        return delegate.getFileList();
    }

    public void add(FileWrite file) {
        delegate.add(file);
    }

    public void add(FolderWrite folder) {
        getFolderList().add(folder);
    }
}
