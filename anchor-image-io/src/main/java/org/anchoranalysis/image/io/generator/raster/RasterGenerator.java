/*-
 * #%L
 * anchor-image-io
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

package org.anchoranalysis.image.io.generator.raster;

import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.io.stack.StackWriteOptions;
import org.anchoranalysis.io.generator.TransformingGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.file.FileType;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.output.namestyle.OutputNameStyle;
import org.anchoranalysis.io.output.outputter.OutputterChecked;

/** 
 * Transfroms an entity to a {@link Stack} and writes it to the file-system.
 * 
 * @author Owen Feehan
 */
public abstract class RasterGenerator<T> implements TransformingGenerator<T,Stack> {

    // A fallback manifest-description if none is supplied by the generator
    private static final ManifestDescription MANIFEST_DESCRIPTION_FALLBACK = new ManifestDescription("raster", "unknown");
    
    @Override
    public FileType[] write(T element, OutputNameStyle outputNameStyle, OutputterChecked outputter)
            throws OutputWriteFailedException {
        return writeInternal(
                element,
                outputNameStyle.getFilenameWithoutExtension(),
                outputNameStyle.getOutputName(),
                "",
                outputter);
    }

    /** As only a single-file is involved, this methods delegates to a simpler virtual method. */
    @Override
    public FileType[] writeWithIndex(
            T element,
            String index,
            IndexableOutputNameStyle outputNameStyle,
            OutputterChecked outputter)
            throws OutputWriteFailedException {

        return writeInternal(
                element,
                outputNameStyle.getFilenameWithoutExtension(index),
                outputNameStyle.getOutputName(),
                index,
                outputter);
    }
   
    /**
     * Is the image being created RGB?
     * 
     * @return
     */
    public abstract boolean isRGB();
        
    public abstract StackWriteOptions writeOptions();
    
    public abstract Optional<ManifestDescription> createManifestDescription();
    
    protected abstract void writeToFile(
            T element, OutputWriteSettings outputWriteSettings, Path filePath)
            throws OutputWriteFailedException;

    protected abstract String selectFileExtension(OutputWriteSettings outputWriteSettings) throws OperationFailedException;
    
    private FileType[] writeInternal(
            T element,
            String filenameWithoutExtension,
            String outputName,
            String index,
            OutputterChecked outputter)
            throws OutputWriteFailedException {

        try {
            String extension = selectFileExtension(outputter.getSettings());
            
            Path pathToWriteTo =
                    outputter.makeOutputPath(
                            filenameWithoutExtension, extension);

            // First write to the file system, and then write to the operation-recorder.
            writeToFile(element, outputter.getSettings(), pathToWriteTo);

            return writeToManifest(outputName, index, outputter, pathToWriteTo, extension);
            
        } catch (OperationFailedException e) {
            throw new OutputWriteFailedException(e);
        }
    }
    
    /** Writes to the manifest, and creates an array of the file-types written. */
    private FileType[] writeToManifest(String outputName,
            String index,
            OutputterChecked outputter, Path pathToWriteTo, String extension) {
        Optional<ManifestDescription> manifestDescription = createManifestDescription(); 
        
        manifestDescription.ifPresent(
                        description ->
                                outputter.writeFileToOperationRecorder(
                                        outputName, pathToWriteTo, description, index));
        
        return createFileTypeArray(manifestDescription.orElse(MANIFEST_DESCRIPTION_FALLBACK), extension);
        
    }
    
    /**
     * Creates a new {@link FileType} wrapped in a single-item array.
     */
    private static FileType[] createFileTypeArray(ManifestDescription description, String extension) {
        return new FileType[] { new FileType(description, extension) };
    }
}
