package org.anchoranalysis.image.io.objs.deserialize;

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

import org.anchoranalysis.bean.xml.RegisterBeanFactories;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.image.io.bean.rasterreader.RasterReader;
import org.anchoranalysis.image.objmask.ObjMask;
import org.anchoranalysis.image.objmask.ObjMaskCollection;
import org.anchoranalysis.io.bean.deserializer.Deserializer;
import org.anchoranalysis.io.deserializer.DeserializationFailedException;
import org.anchoranalysis.io.manifest.deserializer.folder.DeserializeFromFolder;
import org.anchoranalysis.io.manifest.deserializer.folder.DeserializeFromFolderSimple;
import org.anchoranalysis.io.manifest.deserializer.folder.LoadContainer;
import org.anchoranalysis.io.manifest.sequencetype.SequenceTypeException;
import org.anchoranalysis.io.manifest.serialized.SerializedObjectSetFolderSource;

class ReadObjsFromTIFFDirectory extends Deserializer<ObjMaskCollection> {
	
	@Override
	public ObjMaskCollection deserialize(Path folderPath) throws DeserializationFailedException {
		RasterReader rasterReader = RegisterBeanFactories.getDefaultInstances().get(RasterReader.class);
		return readWithRaster(folderPath, rasterReader);
	}
	
	private ObjMaskCollection readWithRaster( Path folderPath, RasterReader rasterReader ) throws DeserializationFailedException {
		
		try {
			SerializedObjectSetFolderSource folderSource = new SerializedObjectSetFolderSource(folderPath,"*.ser");
			
			DeserializeFromFolder<ObjMask> deserializeFolder = new DeserializeFromFolderSimple<ObjMask>(
				new ObjMaskDualDeserializer(rasterReader),
				folderSource
			);
			
			return createFromLoadContainer( deserializeFolder.create() );	
			
		} catch (SequenceTypeException | CreateException e) {
			throw new DeserializationFailedException(e);
		}
	}
	
	private static ObjMaskCollection createFromLoadContainer( LoadContainer<ObjMask> lc ) throws CreateException {
		ObjMaskCollection omc = new ObjMaskCollection();
		try {
			for( int i=lc.getCntr().getMinimumIndex(); i<=lc.getCntr().getMaximumIndex(); i++ ) {
				omc.add( lc.getCntr().get(i) );
			}
		} catch (GetOperationFailedException e) {
			throw new CreateException(e);
		}
		
		return omc;	
	}
}
