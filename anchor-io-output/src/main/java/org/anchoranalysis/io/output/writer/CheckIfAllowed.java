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
import org.anchoranalysis.io.output.MultiLevelOutputEnabled;
import org.anchoranalysis.io.output.bean.rules.OutputEnabledRules;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.outputter.OutputterChecked;

/**
 * Only allows outputs, if the output-name is allowed in the {@link OutputterChecked}.
 * @author Owen Feehan
 *
 */
@RequiredArgsConstructor
public class CheckIfAllowed implements Writer {

    public static final int NUMBER_ELEMENTS_WRITTEN_NOT_ALLOWED = -2;
    
    // START REQUIRED ARGUMENTS
    /** The associated output-manager */
    private final MultiLevelOutputEnabled outputEnabled;

    /** If defined, execute before every operation */
    private final Optional<WriterExecuteBeforeEveryOperation> preop;

    /** The writer. */
    private final Writer writer;
    // END REQUIRED ARGUMENTS

    @Override
    public Optional<OutputterChecked> createSubdirectory(
            String outputName,
            ManifestFolderDescription manifestDescription,
            Optional<FolderWriteWithPath> manifestFolder)
            throws OutputWriteFailedException {

        if (!outputEnabled.isOutputEnabled(outputName)) {
            return Optional.empty();
        }

        maybeExecutePreop();

        return writer.createSubdirectory(outputName, manifestDescription, manifestFolder);
    }

    @Override
    public boolean writeSubdirectoryWithGenerator(String outputName, GenerateWritableItem<?> collectionGenerator)
            throws OutputWriteFailedException {

        if (!outputEnabled.isOutputEnabled(outputName)) {
            return false;
        }

        maybeExecutePreop();

        writer.writeSubdirectoryWithGenerator(outputName, collectionGenerator);
        
        return true;
    }

    @Override
    public int write(
            IndexableOutputNameStyle outputNameStyle,
            GenerateWritableItem<?> generator,
            String index)
            throws OutputWriteFailedException {

        if (!outputEnabled.isOutputEnabled(outputNameStyle.getOutputName())) {
            return NUMBER_ELEMENTS_WRITTEN_NOT_ALLOWED;
        }

        maybeExecutePreop();

        return writer.write(outputNameStyle, generator, index);
    }

    @Override
    public boolean write(String outputName, GenerateWritableItem<?> generator)
            throws OutputWriteFailedException {

        if (!outputEnabled.isOutputEnabled(outputName)) {
            return false;
        }

        maybeExecutePreop();

        writer.write(outputName, generator);
        
        return true;
    }

    @Override
    public Optional<Path> writeGenerateFilename(
            String outputName,
            String extension,
            Optional<ManifestDescription> manifestDescription) {

        if (!outputEnabled.isOutputEnabled(outputName)) {
            return Optional.empty();
        }

        maybeExecutePreop();

        return writer.writeGenerateFilename(
                outputName,
                extension,
                manifestDescription);
    }
        
    private void maybeExecutePreop() {
        preop.ifPresent(WriterExecuteBeforeEveryOperation::execute);;
    }
}
