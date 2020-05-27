package org.anchoranalysis.image.io.objs.deserialize;

/*
 * #%L
 * anchor-io
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
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


import java.nio.file.Path;
import java.nio.file.Paths;

import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.image.chnl.Chnl;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.io.bean.rasterreader.RasterReader;
import org.anchoranalysis.image.io.rasterreader.OpenedRaster;
import org.anchoranalysis.image.objmask.ObjMask;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedByte;
import org.anchoranalysis.io.bean.deserializer.Deserializer;
import org.anchoranalysis.io.bean.deserializer.ObjectInputStreamDeserializer;
import org.anchoranalysis.io.deserializer.DeserializationFailedException;


/**
 * Deserializes an ObjMask stored in two parts
 *   1. A serialized BoundingBox somename.ser  (this filename is expected)
 *   2. A raster mask somename.tif
 *
 */
class ObjMaskDualDeserializer extends Deserializer<ObjMask> {

	private ObjectInputStreamDeserializer<BoundingBox> bboxDeserializer = new ObjectInputStreamDeserializer<>();
	private RasterReader rasterReader;
	
	public ObjMaskDualDeserializer(RasterReader rasterReader) {
		super();
		this.rasterReader = rasterReader;
	}

	@Override
	public ObjMask deserialize(Path filePath) throws DeserializationFailedException {
		
		Path tiffFilename = changeExtension(filePath.toAbsolutePath(), "ser", "tif");
		
		BoundingBox bbox = bboxDeserializer.deserialize(filePath);
		
		try (OpenedRaster or = rasterReader.openFile(tiffFilename)) {
			Stack stack = or.openCheckType(0, ProgressReporterNull.get(), VoxelDataTypeUnsignedByte.instance ).get(0);
			
			if (stack.getNumChnl()!=1) {
				throw new DeserializationFailedException("Raster file must have 1 channel exactly");
			}
			
			Chnl chnl = stack.getChnl(0);
			
			if (!chnl.getDimensions().getExtnt().equals(bbox.extent())) {
				throw new DeserializationFailedException(
					errorMessageMismatchingDims(bbox, chnl.getDimensions(), filePath)
				);
			}
			
			return new ObjMask( bbox, chnl.getVoxelBox().asByte() );
			
		} catch (RasterIOException e) {
			throw new DeserializationFailedException(e);
		}
	}
	
	private static String errorMessageMismatchingDims(BoundingBox bbox, ImageDim sd, Path filePath) {
		return String.format(
			"Dimensions of bounding box (%s) and raster (%s) do not match for file %s",
			bbox.extent(),
			sd.getExtnt(),
			filePath
		);
	}
	
	
	private static Path changeExtension( Path path, String oldExt, String newExt ) throws DeserializationFailedException {
		
		String oldExtUpcase = oldExt.toUpperCase();
		
		if (!path.toString().endsWith("." + oldExt) && !path.toString().endsWith("." + oldExtUpcase)) {
			throw new DeserializationFailedException("Files must have ." + oldExt + " extension");
		}
		
		// Changes extension into newExt
		String pathS = path.toString();
		pathS = pathS.substring(0, pathS.length()-3);
		pathS = pathS.concat(newExt);
		return Paths.get(pathS);
	}

}
