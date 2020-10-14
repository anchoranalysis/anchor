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

package org.anchoranalysis.image.io.objects.deserialize;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.io.bean.files.provider.SearchDirectory;
import org.anchoranalysis.io.bean.input.InputManagerParams;
import org.anchoranalysis.io.bean.path.matcher.MatchGlob;
import org.anchoranalysis.io.exception.FilesProviderException;
import org.anchoranalysis.io.input.InputContextParams;
import org.anchoranalysis.io.manifest.directory.DirectoryWritePhysical;
import org.anchoranalysis.io.manifest.directory.sequenced.SequencedDirectory;
import org.anchoranalysis.io.manifest.file.FileWrite;
import org.anchoranalysis.io.manifest.sequencetype.IncompleteElementRange;
import org.anchoranalysis.io.manifest.sequencetype.IncrementingIntegers;
import org.anchoranalysis.io.manifest.sequencetype.SequenceType;
import org.anchoranalysis.io.manifest.sequencetype.SequenceTypeException;

class SerializedObjectSetFolderSource implements SequencedDirectory {

    private Map<String, FileWrite> mapFileWrite = new HashMap<>();
    private SequenceType<Integer> sequenceType;

    // Constructor
    public SerializedObjectSetFolderSource(Path directory, Optional<String> acceptFilter)
            throws SequenceTypeException {
        super();

        SearchDirectory fileSet = createFileSet(directory, acceptFilter);

        sequenceType = new IncrementingIntegers();
        int i = 0;

        DirectoryWritePhysical fwp = new DirectoryWritePhysical(directory);

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

                FileWrite fileWrite = new FileWrite(fwp);
                fileWrite.setFileName(file.getName());
                fileWrite.setIndex(iStr);
                fileWrite.setOutputName("serializedObjectSetFolder");
                fileWrite.setManifestDescription(null);

                mapFileWrite.put(iStr, fileWrite);

                sequenceType.update(i);

                i++;
            }
        } catch (FilesProviderException e) {
            throw new SequenceTypeException(e);
        }
    }

    @Override
    public IncompleteElementRange getAssociatedElementRange() {
        return sequenceType.elementRange();
    }

    @Override
    public void findFileFromIndex(List<FileWrite> foundList, String index, boolean recursive) {

        FileWrite fw = mapFileWrite.get(index);

        if (fw != null) {
            foundList.add(fw);
        }
    }

    // AcceptFilter can be null in which case, it is ignored
    private static SearchDirectory createFileSet(Path folderPath, Optional<String> acceptFilter) {

        // We use fileSets so as to be expansible with the future
        SearchDirectory fileSet = new SearchDirectory();
        fileSet.setDirectory(folderPath.toString());
        fileSet.setIgnoreHidden(false);

        acceptFilter.ifPresent( filter ->
            fileSet.setMatcher(new MatchGlob(filter))
        );

        return fileSet;
    }
}
