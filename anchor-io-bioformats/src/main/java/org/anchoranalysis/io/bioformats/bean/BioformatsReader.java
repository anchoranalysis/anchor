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
import loci.formats.FormatException;
import loci.formats.IFormatReader;
import loci.formats.ImageReader;
import loci.formats.UnknownFormatException;
import loci.formats.ome.OMEXMLMetadata;
import loci.formats.services.OMEXMLService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.AllowEmpty;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.io.bean.rasterreader.RasterReader;
import org.anchoranalysis.image.io.rasterreader.OpenedRaster;
import org.anchoranalysis.io.bioformats.BioformatsOpenedRaster;
import org.anchoranalysis.io.bioformats.bean.options.Default;
import org.anchoranalysis.io.bioformats.bean.options.ReadOptions;

@NoArgsConstructor
public class BioformatsReader extends RasterReader {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private ReadOptions options = new Default();

    /** If non-empty forces usage of a particular bioformats plugin */
    @BeanField @AllowEmpty @Getter @Setter private String forceReader = "";
    // END BEAN PROPERTIES

    public BioformatsReader(ReadOptions options) {
        this.options = options;
    }

    @Override
    public OpenedRaster openFile(Path filePath) throws RasterIOException {

        try {
            IFormatReader r = selectAndInitReader();

            OMEXMLMetadata metadata = createMetadata();
            r.setMetadataStore(metadata);
            r.setId(filePath.toString());

            return new BioformatsOpenedRaster(r, metadata, options);
        } catch (UnknownFormatException e) {
            throw new RasterIOException("An unknown file format was used");
        } catch (FormatException
                | IOException
                | CreateException
                | NoSuchMethodException
                | ClassNotFoundException
                | InstantiationException
                | IllegalAccessException
                | IllegalArgumentException
                | InvocationTargetException
                | SecurityException e) {
            throw new RasterIOException(e);
        }
    }

    private IFormatReader selectAndInitReader()
            throws ClassNotFoundException, InstantiationException, IllegalAccessException,
                    InvocationTargetException, NoSuchMethodException {

        if (!forceReader.isEmpty()) {
            return (IFormatReader) Class.forName(forceReader).getConstructor().newInstance();
        } else {
            return imageReader();
        }
    }

    /* The standard multiplexing image-reader for bioformats */
    private static IFormatReader imageReader() {
        ImageReader r = new ImageReader();
        r.setGroupFiles(false);
        return r;
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
