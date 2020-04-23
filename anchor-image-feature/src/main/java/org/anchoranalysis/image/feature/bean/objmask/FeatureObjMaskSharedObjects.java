package org.anchoranalysis.image.feature.bean.objmask;





import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.params.FeatureInputDescriptor;
import org.anchoranalysis.image.feature.bean.FeatureSharedObjs;
import org.anchoranalysis.image.feature.objmask.FeatureInputSingleObj;
import org.anchoranalysis.image.feature.objmask.FeatureInputSingleObjDescriptor;

/**
 * A feature that requires shared-objects during intialization
 * 
 * @author owen
 *
 */
public abstract class FeatureObjMaskSharedObjects extends FeatureSharedObjs<FeatureInputSingleObj> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public FeatureInputDescriptor paramType() throws FeatureCalcException {
		return FeatureInputSingleObjDescriptor.instance;
	}
}
