package org.anchoranalysis.image.io.objs;

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

import java.nio.file.Path;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.image.objmask.ObjMaskCollection;
import org.anchoranalysis.io.bean.output.OutputWriteSettings;
import org.anchoranalysis.io.generator.Generator;
import org.anchoranalysis.io.generator.IterableGenerator;
import org.anchoranalysis.io.generator.SingleFileTypeGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.OutputWriteFailedException;

import ch.systemsx.cisd.hdf5.HDF5Factory;
import ch.systemsx.cisd.hdf5.IHDF5Writer;

/**
 * Writes an object-mask-collection to a HDF5 file
 * 
 * @author FEEHANO
 *
 */
class GeneratorHDF5 extends SingleFileTypeGenerator implements IterableGenerator<ObjMaskCollection> {

	private ObjMaskCollection item;
	private boolean compressed;

	public GeneratorHDF5(boolean compressed) {
		super();
		this.compressed = compressed;
	}

	@Override
	public void start() throws OutputWriteFailedException {
	}

	@Override
	public void end() throws OutputWriteFailedException {
		
	}

	@Override
	public Generator getGenerator() {
		return this;
	}

	@Override
	public void writeToFile(OutputWriteSettings outputWriteSettings, Path filePath)
			throws OutputWriteFailedException {
		// Write a HDF file
		try {
			writeObjMaskCollection( getIterableElement(), filePath );

		} catch (OperationFailedException e) {
			throw new OutputWriteFailedException(e);
		}
	}
	
	private void writeObjMaskCollection( ObjMaskCollection objs, Path filePath ) throws OperationFailedException {

		IHDF5Writer writer = HDF5Factory.open( filePath.toString() );
		
		addObjsSizeAttr(writer, objs);
		
		try {
			for( int i=0; i<objs.size(); i++ ) {
				
				String datasetId = PathUtilities.pathForObj(i);
				
				new ObjMaskHDF5Writer(
					objs.get(i),
					datasetId,
					writer,
					compressed
				).apply();
			}
			
		} finally {
			writer.close();
		}
		
	}
	
	// Adds an attribute with the total number of objects, so it can be quickly queried
	//  from the HDF5 without parsing all the datasets
	private void addObjsSizeAttr(IHDF5Writer writer, ObjMaskCollection objs) {
		writer.uint32().setAttr("/", "numberObjects", objs.size() );		
	}

	@Override
	public String getFileExtension(OutputWriteSettings outputWriteSettings) {
		return "h5";
	}

	@Override
	public ManifestDescription createManifestDescription() {
		return new ManifestDescription("hdf5", "objMaskCollection");
	}
	
	@Override
	public ObjMaskCollection getIterableElement() {
		return item;
	}

	@Override
	public void setIterableElement(ObjMaskCollection element) throws SetOperationFailedException {
		this.item = element;
	}
}
