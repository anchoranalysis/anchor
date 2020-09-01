/*-
 * #%L
 * anchor-io-output
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

package org.anchoranalysis.io.output.writer;

import java.nio.file.Path;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.ManifestFolderDescription;
import org.anchoranalysis.io.manifest.folder.FolderWriteWithPath;
import org.anchoranalysis.io.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.namestyle.OutputNameStyle;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.bound.BoundOutputManager;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

@RequiredArgsConstructor
public class CheckIfAllowed implements Writer {

    // START REQUIRED ARGUMENTS
    /** The associated output-manager */
    private final BoundOutputManager outputManager;

    /** Execute before every operation */
    private final WriterExecuteBeforeEveryOperation preop;

    private final Writer writer;
    // END REQUIRED ARGUMENTS

    @Override
    public Optional<BoundOutputManager> bindAsSubdirectory(
            String outputName,
            ManifestFolderDescription manifestDescription,
            Optional<FolderWriteWithPath> folder)
            throws OutputWriteFailedException {

        if (!outputManager.isOutputAllowed(outputName)) {
            return Optional.empty();
        }

        preop.execute();

        return writer.bindAsSubdirectory(outputName, manifestDescription, folder);
    }

    @Override
    public void writeSubfolder(String outputName, GenerateWritableItem<?> collectionGenerator)
            throws OutputWriteFailedException {

        if (!outputManager.isOutputAllowed(outputName)) {
            return;
        }

        preop.execute();

        writer.writeSubfolder(outputName, collectionGenerator);
    }

    @Override
    public int write(
            IndexableOutputNameStyle outputNameStyle,
            GenerateWritableItem<?> generator,
            String index)
            throws OutputWriteFailedException {

        if (!outputManager.isOutputAllowed(outputNameStyle.getOutputName())) {
            return -1;
        }

        preop.execute();

        return writer.write(outputNameStyle, generator, index);
    }

    @Override
    public void write(OutputNameStyle outputNameStyle, GenerateWritableItem<?> generator)
            throws OutputWriteFailedException {

        if (!outputManager.isOutputAllowed(outputNameStyle.getOutputName())) return;

        preop.execute();

        writer.write(outputNameStyle, generator);
    }

    @Override
    public Optional<Path> writeGenerateFilename(
            String outputName,
            String extension,
            Optional<ManifestDescription> manifestDescription,
            String outputNamePrefix,
            String outputNameSuffix,
            String index) {

        if (!outputManager.isOutputAllowed(outputName)) {
            return Optional.empty();
        }

        preop.execute();

        return writer.writeGenerateFilename(
                outputName,
                extension,
                manifestDescription,
                outputNamePrefix,
                outputNameSuffix,
                index);
    }

    @Override
    public OutputWriteSettings getOutputWriteSettings() {
        return outputManager.getOutputWriteSettings();
    }
}
