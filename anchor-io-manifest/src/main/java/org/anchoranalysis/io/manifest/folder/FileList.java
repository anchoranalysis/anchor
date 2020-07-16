/* (C)2020 */
package org.anchoranalysis.io.manifest.folder;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.file.FileWrite;
import org.anchoranalysis.io.manifest.match.Match;

class FileList implements Serializable {

    /** */
    private static final long serialVersionUID = 5858857164978822313L;

    private FolderWrite folder;
    private ArrayList<FileWrite> files;

    public FileList(FolderWrite folder) {
        super();
        this.folder = folder;
        this.files = new ArrayList<>();
    }

    // Finds a folder a comparator matches
    public void findFile(List<FileWrite> foundList, Match<FileWrite> match, boolean recursive) {

        for (FileWrite file : files) {

            if (match.matches(file)) {
                foundList.add(file);
            }
        }

        if (!recursive) {
            return;
        }

        for (FolderWrite f : this.folder.getFolderList()) {
            f.findFile(foundList, match, recursive);
        }
    }

    public void add(FileWrite fw) {
        this.files.add(fw);
    }

    public void write(
            String outputName,
            ManifestDescription manifestDescription,
            Path outfilePath,
            String index) {

        FileWrite fw = new FileWrite(folder);
        fw.setOutputName(outputName);
        fw.setFileName(outfilePath.toString());
        fw.setManifestDescription(manifestDescription);
        fw.setIndex(index);
        add(fw);
    }

    public List<FileWrite> getFileList() {
        return files;
    }
}
