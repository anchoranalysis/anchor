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

package org.anchoranalysis.image.io.object.input;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.anchoranalysis.core.progress.ProgressIgnore;
import org.anchoranalysis.io.input.InputContextParams;
import org.anchoranalysis.io.input.bean.InputManagerParams;
import org.anchoranalysis.io.input.bean.files.SearchDirectory;
import org.anchoranalysis.io.input.bean.path.matcher.MatchGlob;
import org.anchoranalysis.io.input.file.FilesProviderException;
import org.anchoranalysis.io.manifest.directory.Subdirectory;
import org.anchoranalysis.io.manifest.directory.sequenced.SequencedDirectory;
import org.anchoranalysis.io.manifest.file.OutputtedFile;
import org.anchoranalysis.io.manifest.sequencetype.IncompleteElementRange;
import org.anchoranalysis.io.manifest.sequencetype.IncrementingIntegers;
import org.anchoranalysis.io.manifest.sequencetype.SequenceType;
import org.anchoranalysis.io.manifest.sequencetype.SequenceTypeException;

class SerializedObjectsFromDirectory implements SequencedDirectory {

    private Map<String, OutputtedFile> mapFileWrite = new HashMap<>();
    private SequenceType<Integer> sequenceType;

    // Constructor
    public SerializedObjectsFromDirectory(Path directoryPath, Optional<String> acceptFilter)
            throws SequenceTypeException {

        SearchDirectory fileSet = createFileSet(directoryPath, acceptFilter);

        sequenceType = new IncrementingIntegers();

        Subdirectory directoryInManifest = new Subdirectory(directoryPath);

        try {
            Collection<File> files =
                    fileSet.create(
                            new InputManagerParams(
                                    new InputContextParams(),
                                    ProgressIgnore.get(),
                                    null // HACK: Can be safely set to null as
                                    // fileSet.setIgnoreHidden(false);	// NOSONAR
                                    ));

            int index = 0;
            for (File file : files) {
                processFile(file, index, directoryInManifest);
                index++;
            }
        } catch (FilesProviderException e) {
            throw new SequenceTypeException(e);
        }
    }

    private void processFile(File file, int index, Subdirectory directoryInManifest)
            throws SequenceTypeException {
        String indexAsString = Integer.toString(index);

        OutputtedFile fileWrite =
                new OutputtedFile(
                        directoryInManifest,
                        file.getName(),
                        "serializedObjectSetFolder",
                        indexAsString,
                        Optional.empty());

        mapFileWrite.put(indexAsString, fileWrite);

        sequenceType.update(index);
    }

    @Override
    public IncompleteElementRange getAssociatedElementRange() {
        return sequenceType.elementRange();
    }

    @Override
    public void findFileFromIndex(List<OutputtedFile> foundList, String index, boolean recursive) {

        OutputtedFile outputtedFile = mapFileWrite.get(index);

        if (outputtedFile != null) {
            foundList.add(outputtedFile);
        }
    }

    // AcceptFilter can be null in which case, it is ignored
    private static SearchDirectory createFileSet(Path folderPath, Optional<String> acceptFilter) {

        // We use fileSets so as to be expansible with the future
        SearchDirectory fileSet = new SearchDirectory();
        fileSet.setDirectory(folderPath.toString());
        fileSet.setIgnoreHidden(false);

        acceptFilter.ifPresent(filter -> fileSet.setMatcher(new MatchGlob(filter)));

        return fileSet;
    }
}
