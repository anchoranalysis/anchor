/* (C)2020 */
package org.anchoranalysis.io.manifest.serialized;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.io.bean.file.matcher.MatchGlob;
import org.anchoranalysis.io.bean.input.InputManagerParams;
import org.anchoranalysis.io.bean.provider.file.SearchDirectory;
import org.anchoranalysis.io.error.FileProviderException;
import org.anchoranalysis.io.manifest.file.FileWrite;
import org.anchoranalysis.io.manifest.folder.FolderWritePhysical;
import org.anchoranalysis.io.manifest.folder.SequencedFolder;
import org.anchoranalysis.io.manifest.sequencetype.IncrementalSequenceType;
import org.anchoranalysis.io.manifest.sequencetype.SequenceType;
import org.anchoranalysis.io.manifest.sequencetype.SequenceTypeException;
import org.anchoranalysis.io.params.InputContextParams;

public class SerializedObjectSetFolderSource implements SequencedFolder {

    private HashMap<String, FileWrite> mapFileWrite = new HashMap<>();
    private IncrementalSequenceType incrSequenceType;

    // Constructor
    public SerializedObjectSetFolderSource(Path folderPath) throws SequenceTypeException {
        this(folderPath, null);
    }

    // Constructor
    public SerializedObjectSetFolderSource(Path folderPath, String acceptFilter)
            throws SequenceTypeException {
        super();

        SearchDirectory fileSet = createFileSet(folderPath, acceptFilter);

        incrSequenceType = new IncrementalSequenceType();
        int i = 0;

        FolderWritePhysical fwp = new FolderWritePhysical();
        fwp.setPath(folderPath);
        fwp.setParentFolder(Optional.empty());

        try {
            Collection<File> files =
                    fileSet.create(
                            new InputManagerParams(
                                    new InputContextParams(),
                                    ProgressReporterNull.get(),
                                    null // HACK: Can be safely set to null as
                                    // fileSet.setIgnoreHidden(false);	// NOSONAR
                                    ));

            for (File file : files) {

                String iStr = Integer.toString(i);

                FileWrite fw = new FileWrite(fwp);
                fw.setFileName(file.getName());
                fw.setIndex(iStr);
                fw.setOutputName("serializedObjectSetFolder");
                fw.setManifestDescription(null);

                mapFileWrite.put(iStr, fw);

                incrSequenceType.update(String.valueOf(i));

                i++;
            }
        } catch (FileProviderException e) {
            throw new SequenceTypeException(e);
        }
    }

    @Override
    public SequenceType getAssociatedSequence() {
        return incrSequenceType;
    }

    @Override
    public void findFileFromIndex(List<FileWrite> foundList, String index, boolean recursive) {

        FileWrite fw = mapFileWrite.get(index);

        if (fw != null) {
            foundList.add(fw);
        }
    }

    // AcceptFilter can be null in which case, it is ignored
    private SearchDirectory createFileSet(Path folderPath, String acceptFilter) {

        // We use fileSets so as to be expansible with the future
        SearchDirectory fileSet = new SearchDirectory();
        fileSet.setDirectory(folderPath.toString());
        fileSet.setIgnoreHidden(false);

        if (acceptFilter != null) {
            fileSet.setMatcher(new MatchGlob(acceptFilter));
        }

        return fileSet;
    }
}
