/*-
 * #%L
 * anchor-io-manifest
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
/* (C)2020 */
package org.anchoranalysis.io.manifest.folder;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.io.manifest.ManifestFolderDescription;
import org.anchoranalysis.io.manifest.file.FileWrite;
import org.anchoranalysis.io.manifest.match.FileWriteIndex;
import org.anchoranalysis.io.manifest.match.Match;
import org.anchoranalysis.io.manifest.operationrecorder.WriteOperationRecorder;
import org.anchoranalysis.io.manifest.sequencetype.SequenceType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.checkerframework.checker.nullness.qual.Nullable;

// A folder contains a list of subfolders, contained files varies by implementation
public abstract class FolderWrite implements SequencedFolder, WriteOperationRecorder, Serializable {

    /** */
    private static final long serialVersionUID = 1L;

    // Not Optional as it needs to be serialized
    @Nullable private FolderWrite parentFolder;

    private ArrayList<FolderWrite> folders = new ArrayList<>();

    private static Log log = LogFactory.getLog(FolderWrite.class);

    // Not Optional as it needs to be serialized
    @Nullable @Getter @Setter private ManifestFolderDescription manifestFolderDescription;

    public FolderWrite() {
        log.debug("New Folder Write: empty");
        parentFolder = null;
    }

    // Parent folder
    public FolderWrite(FolderWrite parentFolder) {
        super();
        log.debug("New Folder Write: " + parentFolder.getRelativePath());
        this.parentFolder = parentFolder;
    }

    public abstract Path getRelativePath();

    public Path calcPath() {
        if (parentFolder != null) {
            return parentFolder.calcPath().resolve(getRelativePath());
        } else {
            return getRelativePath();
        }
    }

    @Override
    public void findFileFromIndex(List<FileWrite> foundList, String index, boolean recursive) {
        FileWriteIndex match = new FileWriteIndex(index);
        findFile(foundList, match, true);
    }

    // Finds a folder a comparator matches
    public abstract void findFile(
            List<FileWrite> foundList, Match<FileWrite> match, boolean recursive);

    // Finds a folder a comparator matches
    public synchronized void findFolder(List<FolderWrite> foundList, Match<FolderWrite> match) {

        for (FolderWrite folder : folders) {

            if (match.matches(folder)) {
                foundList.add(folder);
            }

            if (folder == null) {
                continue;
            }
            folder.findFolder(foundList, match);
        }
    }

    @Override
    public synchronized WriteOperationRecorder writeFolder(
            Path relativeFolderPath,
            ManifestFolderDescription manifestFolderDescription,
            FolderWriteWithPath folder) {
        folder.setParentFolder(Optional.of(this));
        folder.setPath(relativeFolderPath);
        folder.setManifestFolderDescription(manifestFolderDescription);
        folders.add(folder);
        return folder;
    }

    protected Optional<FolderWrite> getParentFolder() {
        return Optional.ofNullable(parentFolder);
    }

    public void setParentFolder(Optional<FolderWrite> parentFolder) {
        this.parentFolder = parentFolder.orElse(null);
    }

    protected List<FolderWrite> getFolderList() {
        return folders;
    }

    public abstract List<FileWrite> fileList();

    @Override
    public SequenceType getAssociatedSequence() {
        return getManifestFolderDescription().getSequenceType();
    }
}
