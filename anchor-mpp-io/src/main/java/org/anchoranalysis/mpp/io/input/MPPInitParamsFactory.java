package org.anchoranalysis.mpp.io.input;

import java.util.Optional;

import org.anchoranalysis.anchor.mpp.bean.MPPBean;
import org.anchoranalysis.anchor.mpp.bean.init.MPPInitParams;
import org.anchoranalysis.bean.define.Define;
import org.anchoranalysis.bean.init.property.PropertyInitializer;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.name.provider.INamedProvider;
import org.anchoranalysis.core.name.store.SharedObjects;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.experiment.task.ParametersBound;
import org.anchoranalysis.image.init.ImageInitParams;
import org.anchoranalysis.image.io.input.ImageInitParamsFactory;
import org.anchoranalysis.image.objmask.ObjMaskCollection;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.output.bound.BoundIOContext;

public class MPPInitParamsFactory {

	private MPPInitParamsFactory() {}
	
	public static MPPInitParams createFromInput( ParametersBound<? extends InputForMPPBean,?> params, Optional<Define> define ) throws CreateException {

		InputForMPPBean inputObject = params.getInputObject();
		
		try {
			MPPInitParams soMPP = create(
				params.context(),
				define
			);
			inputObject.addToSharedObjects( soMPP, soMPP.getImage() );
			return soMPP;
			
		} catch (OperationFailedException e) {
			throw new CreateException(e);
		}
	}
	
	public static MPPInitParams create(	BoundIOContext context, Optional<Define> define ) throws CreateException {
		
		SharedObjects so = new SharedObjects( context.getLogger() );
		
		ImageInitParams soImage = ImageInitParamsFactory.create( so, context.getModelDirectory() );
		MPPInitParams soMPP = new MPPInitParams(soImage, so);
		
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
		BoundIOContext params,
		Optional<Define> define,
		Optional<INamedProvider<Stack>> stacks,
		Optional<INamedProvider<ObjMaskCollection>> objs,
		Optional<KeyValueParams> keyValueParams
	) throws CreateException {
		
		try {
			MPPInitParams soMPP = create(params, define);
			
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
