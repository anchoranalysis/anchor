package org.anchoranalysis.image.io.generator.raster;

/*-
 * #%L
 * anchor-image-io
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.io.bean.rasterwriter.RasterWriter;
import org.anchoranalysis.io.bean.output.OutputWriteSettings;

public class RasterWriterUtilities {

	/**
	 * Gets the default raster-writer associated with outputWriteSettings
	 * 
	 * @param outputWriteSettings
	 * @return a writer (always non-null)
	 * @throws RasterIOException if a writer doesn't exist
	 */
	public static RasterWriter getDefaultRasterWriter(OutputWriteSettings outputWriteSettings) throws RasterIOException {
		RasterWriter defaultWriter = (RasterWriter) outputWriteSettings.getWriterInstance(RasterWriter.class);
		if (defaultWriter==null) {
			throw new RasterIOException("No default rasterWriter has been set");
		}
		return defaultWriter;
	}
	
	public static String getDefaultRasterFileExtension(OutputWriteSettings outputWriteSettings) {
		RasterWriter defaultWriter = (RasterWriter) outputWriteSettings.getWriterInstance(RasterWriter.class);
		if (defaultWriter==null) {
			return ".unknown";
		}
		return defaultWriter.dfltExt();
	}
}
