/* (C)2020 */
package org.anchoranalysis.io.manifest.file;

import java.io.Serializable;
import java.nio.file.Path;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.folder.FolderWrite;

public class FileWrite implements Serializable {

    /** */
    private static final long serialVersionUID = 5796355859093885433L;

    private FolderWrite parentFolder;

    private String fileName;
    private String outputName;
    private ManifestDescription manifestDescription;
    private String index;

    public FileWrite() {}

    public FileWrite(FolderWrite parentFolder) {
        super();
        this.parentFolder = parentFolder;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getOutputName() {
        return outputName;
    }

    public void setOutputName(String outputName) {
        this.outputName = outputName;
    }

    public ManifestDescription getManifestDescription() {
        return manifestDescription;
    }

    public void setManifestDescription(ManifestDescription manifestDescription) {
        this.manifestDescription = manifestDescription;
    }

    public Path calcPath() {
        return parentFolder.calcPath().resolve(fileName);
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public void setIndex(int index) {
        this.index = Integer.toString(index);
    }

    public FolderWrite getParentFolder() {
        return parentFolder;
    }

    public void setParentFolder(FolderWrite parentFolder) {
        this.parentFolder = parentFolder;
    }
}
