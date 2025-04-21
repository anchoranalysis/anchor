/*-
 * #%L
 * anchor-io-bioformats
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

package org.anchoranalysis.io.bioformats.bean;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.IFormatReader;
import loci.formats.ImageReader;
import loci.formats.UnknownFormatException;
import loci.formats.ome.OMEXMLMetadata;
import loci.formats.services.OMEXMLService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.time.ExecutionTimeRecorder;
import org.anchoranalysis.image.core.dimensions.OrientationChange;
import org.anchoranalysis.image.io.ImageIOException;
import org.anchoranalysis.image.io.bean.stack.reader.StackReaderOrientationCorrection;
import org.anchoranalysis.image.io.stack.CalculateOrientationChange;
import org.anchoranalysis.image.io.stack.input.OpenedImageFile;
import org.anchoranalysis.io.bioformats.bean.options.Default;
import org.anchoranalysis.io.bioformats.bean.options.ReadOptions;
import org.anchoranalysis.io.bioformats.metadata.ImageTimestampsAttributesFactory;

/**
 * Reads a stack using the <a href="https://www.openmicroscopy.org/bio-formats/">Bioformats</a>
 * library.
 *
 * <p>Note that EXIF orientation information is not processed when reading JPEGs or PNGs.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor
public class BioformatsReader extends StackReaderOrientationCorrection {

    // START BEAN PROPERTIES
    /** Options that influence how stack is read. */
    @BeanField @Getter @Setter private ReadOptions options = new Default();

    // END BEAN PROPERTIES

    /**
     * Create with particular options.
     *
     * @param options options that influence how stack is read.
     */
    public BioformatsReader(ReadOptions options) {
        this.options = options;
    }

    @Override
    public OpenedImageFile openFile(Path path, ExecutionTimeRecorder executionTimeRecorder)
            throws ImageIOException {
        return openFile(
                path,
                loggerForOrientation -> OrientationChange.KEEP_UNCHANGED,
                executionTimeRecorder);
    }

    @Override
    public OpenedImageFile openFile(
            Path filePath,
            CalculateOrientationChange orientationCorrection,
            ExecutionTimeRecorder executionTimeRecorder)
            throws ImageIOException {

        try {
            OMEXMLMetadata metadata = createMetadata();

            IFormatReader reader =
                    selectAndInitReaderWithMetadata(filePath, metadata, executionTimeRecorder);

            return new BioformatsOpenedRaster(
                    reader,
                    metadata,
                    options,
                    orientationCorrection,
                    () -> ImageTimestampsAttributesFactory.fromPath(filePath));
        } catch (UnknownFormatException e) {
            throw new ImageIOException("An unknown file format was used: " + filePath);
        } catch (Exception e) {
            throw new ImageIOException(
                    String.format(
                            "Failed to open image-file %s using %s",
                            filePath, BioformatsReader.class),
                    e);
        }
    }

    private IFormatReader selectAndInitReaderWithMetadata(
            Path filePath, OMEXMLMetadata metadata, ExecutionTimeRecorder executionTimeRecorder)
            throws Exception {
        try {
            IFormatReader reader =
                    executionTimeRecorder.recordExecutionTime(
                            "SelectAndInitReader", BioformatsReader::imageReader);
            reader.setMetadataStore(metadata);
            configureReaderForPyramidal(reader);
            executionTimeRecorder.recordExecutionTime(
                    "Assigning file-path to BioformatsReader",
                    () -> reader.setId(filePath.toString()));
            return reader;
        } catch (NoSuchMethodException
                | ClassNotFoundException
                | InstantiationException
                | IllegalAccessException
                | InvocationTargetException
                | IOException e) {
            throw new OperationFailedException(e);
        }
    }

    /**
     * Performs necessary configuration of the {@link IFormatReader} instructing it how to process
     * image pyramid files (as often occur with whole slide images).
     */
    private static void configureReaderForPyramidal(IFormatReader reader) {
        // For whole slide images, instruct NOT to show the different pyramid resolutions as
        // separate series
        // but rather express each pyramid (as a whole) as a series.
        // The getResolutionCount() and setResolution() methods can then be used for more
        // information about the pyramids
        // See https://docs.openmicroscopy.org/bio-formats/6.5.1/developers/wsi.html
        reader.setFlattenedResolutions(false);
    }

    /** The standard multiplexing image-reader from Bioformats. */
    private static IFormatReader imageReader() {
        ImageReader reader = new ImageReader();
        reader.setGroupFiles(false);
        return reader;
    }

    private static OMEXMLMetadata createMetadata() throws CreateException {
        try {
            ServiceFactory factory = new ServiceFactory();
            OMEXMLService service = factory.getInstance(OMEXMLService.class);
            return service.createOMEXMLMetadata();

        } catch (DependencyException | ServiceException e) {
            throw new CreateException(e);
        }
    }
}
