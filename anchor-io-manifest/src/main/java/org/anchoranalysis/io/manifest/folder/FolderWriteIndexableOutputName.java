/* (C)2020 */
package org.anchoranalysis.io.manifest.folder;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.file.FileType;
import org.anchoranalysis.io.manifest.file.FileWrite;
import org.anchoranalysis.io.manifest.match.Match;
import org.anchoranalysis.io.manifest.sequencetype.SequenceType;
import org.anchoranalysis.io.namestyle.IndexableOutputNameStyle;

public class FolderWriteIndexableOutputName extends FolderWriteWithPath {

    /** */
    private static final long serialVersionUID = -8404795823155555672L;

    private IndexableOutputNameStyle outputName;

    private ArrayList<FileType> template;

    // Constructor
    public FolderWriteIndexableOutputName(IndexableOutputNameStyle outputName) {
        super();
        this.outputName = outputName;
        this.template = new ArrayList<>();
    }

    public void addFileType(FileType fileType) {
        this.template.add(fileType);
    }

    // Every time a file is written, we do a check to ensure the outputName
    //   and manifestDescription and path matches one of our templates, otherwise
    //   we throw an Exception as something is wrong
    @Override
    public void write(
            String outputName,
            ManifestDescription manifestDescription,
            Path outFilePath,
            String index) {
        // CURRENTLY - we do no check
    }

    // We apply the match to each element in our sequence type, if the folder
    //   has no SequenceType then something is wrong and we throw an exception
    @Override
    public void findFile(List<FileWrite> foundList, Match<FileWrite> match, boolean recursive) {
        SequenceType sequenceType = getManifestFolderDescription().getSequenceType();

        int i = sequenceType.getMinimumIndex();
        do {
            // We loop through each file type
            for (FileType fileType : template) {
                FileWrite virtualFile = genFile(sequenceType.indexStr(i), fileType);
                if (match.matches(virtualFile)) {
                    foundList.add(virtualFile);
                }
            }

            i = sequenceType.nextIndex(i);

        } while (i != -1);
    }

    private FileWrite genFile(String index, FileType fileType) {

        FileWrite fw = new FileWrite();
        fw.setFileName(outputName.getPhysicalName(index) + "." + fileType.getFileExtension());
        fw.setOutputName(outputName.getOutputName());
        fw.setManifestDescription(fileType.getManifestDescription());
        fw.setIndex(index);
        fw.setParentFolder(this);
        return fw;
    }

    @Override
    public List<FileWrite> fileList() {
        throw new UnsupportedOperationException();
    }
}
