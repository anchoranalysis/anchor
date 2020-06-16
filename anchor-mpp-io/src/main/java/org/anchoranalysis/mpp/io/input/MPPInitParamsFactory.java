package org.anchoranalysis.mpp.io.input;

/*-
 * #%L
 * anchor-mpp-io
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan
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

import java.util.Optional;

import org.anchoranalysis.anchor.mpp.bean.MPPBean;
import org.anchoranalysis.anchor.mpp.bean.init.MPPInitParams;
import org.anchoranalysis.bean.define.Define;
import org.anchoranalysis.bean.init.property.PropertyInitializer;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.name.store.SharedObjects;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.image.init.ImageInitParams;
import org.anchoranalysis.image.io.input.ImageInitParamsFactory;
import org.anchoranalysis.image.objectmask.ObjectMaskCollection;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.output.bound.BoundIOContext;

public class MPPInitParamsFactory {

	private MPPInitParamsFactory() {}
		
	public static MPPInitParams create(	BoundIOContext context, Optional<Define> define, Optional<? extends InputForMPPBean> input ) throws CreateException {
		
		SharedObjects so = new SharedObjects( context.getLogger() );
		
		ImageInitParams soImage = ImageInitParamsFactory.create( so, context.getModelDirectory() );
		MPPInitParams soMPP = new MPPInitParams(soImage, so);
		
		if (input.isPresent()) {
			try {
				input.get().addToSharedObjects(soMPP, soImage);
			} catch (OperationFailedException e) {
				throw new CreateException(e);
			}
		}
		
		if (define.isPresent()) {
			try {
				// Tries to initialize any properties (of type MPPInitParams) found in the NamedDefinitions
				PropertyInitializer<MPPInitParams> pi = MPPBean.initializerForMPPBeans();
				pi.setParam(soMPP);
				soMPP.populate(
					pi,
					define.get(),
					context.getLogger()
				);

			} catch (OperationFailedException e) {
				throw new CreateException(e);
			}
		}
		
		return soMPP;
	}
	
	public static MPPInitParams createFromExistingCollections(
		BoundIOContext context,
		Optional<Define> define,
		Optional<NamedProvider<Stack>> stacks,
		Optional<NamedProvider<ObjectMaskCollection>> objs,
		Optional<KeyValueParams> keyValueParams
	) throws CreateException {
		
		try {
			MPPInitParams soMPP = create(
				context,
				define,
				Optional.empty()
			);
			
			ImageInitParams soImage = soMPP.getImage();
			
			if (stacks.isPresent()) {
				soImage.copyStackCollectionFrom(stacks.get());
			}
			
			if (objs.isPresent()) {
				soMPP.getImage().copyObjMaskCollectionFrom(objs.get());
			}
			
			if (keyValueParams.isPresent()) {
				soImage.addToKeyValueParamsCollection("input_params", keyValueParams.get());
			}
			
			return soMPP;
			
		} catch (OperationFailedException e) {
			throw new CreateException(e);
		}
	}
}
