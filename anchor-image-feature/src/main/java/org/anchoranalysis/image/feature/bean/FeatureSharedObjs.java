package org.anchoranalysis.image.feature.bean;

import java.util.Arrays;
import java.util.List;

import org.anchoranalysis.bean.init.property.ExtractFromParam;
import org.anchoranalysis.bean.init.property.PropertyInitializer;
import org.anchoranalysis.feature.bean.FeatureCastInitParams;
import org.anchoranalysis.feature.calc.FeatureInitParams;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.image.feature.init.FeatureInitParamsSharedObjs;
import org.anchoranalysis.image.init.ImageInitParams;


/**
 * A feature that depends on SharedObjects being passed during the paramter-initialization
 * 
 * @author owen
 *
 * @param <S> init-params params type
 * @param <T> feature input type
 */
public abstract class FeatureSharedObjs<T extends FeatureInput> extends FeatureCastInitParams<FeatureInitParamsSharedObjs, T> {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected FeatureSharedObjs() {
		super(
			FeatureInitParamsSharedObjs.class,
			new PropertyInitializer<>(	FeatureInitParams.class, paramExtracters() )
		);
	}
	
	private static List<ExtractFromParam<FeatureInitParams,?>> paramExtracters() {
		return Arrays.asList(
			new ExtractFromParam<>(
				ImageInitParams.class,
				p -> extractImageInitParams(p),
				FeatureInitParamsSharedObjs.class
			)
		);
	}
		
	private static ImageInitParams extractImageInitParams(FeatureInitParams params) {
		FeatureInitParamsSharedObjs paramCast = (FeatureInitParamsSharedObjs) params;
		return paramCast.getSharedObjects();
	}
}
