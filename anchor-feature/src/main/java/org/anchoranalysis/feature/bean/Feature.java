package org.anchoranalysis.feature.bean;

/*
 * #%L
 * anchor-feature
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


import java.io.Serializable;
import java.util.List;
import org.anchoranalysis.bean.annotation.AllowEmpty;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.error.BeanMisconfiguredException;
import org.anchoranalysis.bean.init.property.PropertyInitializer;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.cache.CacheSession;
import org.anchoranalysis.feature.cache.FeatureCacheDefinition;
import org.anchoranalysis.feature.cache.SimpleCacheDefinition;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.params.FeatureCalcParams;
import org.anchoranalysis.feature.init.FeatureInitParams;
import org.anchoranalysis.feature.init.IInitFeatures;

public abstract class Feature extends FeatureBase implements
		Serializable, IInitFeatures {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// START BEAN PROPERTIES
	@BeanField
	@AllowEmpty
	private String customName = "";
	// END BEAN PROPERTIES

	// Saved for debugging reasons ONLY
	// private FeatureInitParams paramsInit;

	private transient LogErrorReporter logger;

	private boolean hasBeenInit = false;
	
	private FeatureCacheDefinition cacheDefinition;
	
	private CacheSession cache;

	protected Feature() {
		super();
		setupCacheDefinition();
	}
	
	protected Feature( PropertyInitializer<FeatureInitParams> propertyInitializer ) {
		super( propertyInitializer );
		setupCacheDefinition();
	}
	
	private void setupCacheDefinition() {
		this.cacheDefinition = createCacheDefinition();
	}
	
	@Override
	public final String getBeanDscr() {
		String paramDscr = getParamDscr();

		if (paramDscr != "") {
			return String.format("%s(%s)", getBeanName(), getParamDscr());
		} else {
			return getBeanName();
		}
	}

	public String getDscrLong() {
		return getBeanDscr();
	}

	public String getCustomName() {
		return customName;
	}

	public void setCustomName(String customName) {
		this.customName = customName;
	}

	@Override
	public String getFriendlyName() {

		if (getCustomName() != null && !getCustomName().isEmpty()) {
			return getCustomName();
		} else {
			return getDscrLong();
		}
	}

	public String getDscrWithCustomName() {
		return getCustomName() != null && !getCustomName().isEmpty() ? getCustomName()
				+ ":  " + getBeanDscr()
				: getBeanDscr();
	}

	public double calcCheckInit(FeatureCalcParams params) throws FeatureCalcException {
		if (!hasBeenInit) {
			throw new FeatureCalcException(String.format(
					"The feature (%s) has not been initialized",
					this.toString()));
		}
	
		double ret = calc( params );
		
		assert( !Double.isNaN(ret) );
		return ret;
	}
	
	// Calculates a value for some parameters
	protected abstract double calc(FeatureCalcParams params) throws FeatureCalcException;

	/**
	 * Optionally transforms the parameters passed into this feature, before
	 * they are passed to a dependent feature
	 * 
	 * @param params
	 *            params passed to this feature
	 * @param dependentFeature
	 *            a dependent-feature
	 */
	public FeatureCalcParams transformParams(FeatureCalcParams params,
			Feature dependentFeature) throws FeatureCalcException {
		return params;
	}

	protected void duplicateHelper(Feature out) {
		out.customName = new String(customName);
	}
	
	/**
	 * Initialises the bean with important parameters needed for calculation.  Must be called (one-time) before feature calculations.
	 * 
	 * @param params parameters used for initialisation that are simply passed to beforeCalc()
	 * @param logger logger
	 * 
	 * When a feature requires particular additional-caches, these are extracted from allAdditionalCaches to create an ordered array
	 *   in the same order as returned by needsAdditionalCaches()
	 * @param logger the logger, saved and made available to the feature
	 */
	@Override
	public void init(
		FeatureInitParams params,
		FeatureBase parentFeature,
		LogErrorReporter logger
	) throws InitException {
				
		hasBeenInit = true;
		this.logger = logger;

		cache = this.cacheDefinition.rslv(parentFeature, params.getCache() );
		
		beforeCalc(	params,	cache );
	}
	
	protected FeatureCacheDefinition createCacheDefinition() {
		return new SimpleCacheDefinition(this);
	}
	

	/**
	 * Returns a list of Features that exist as bean-properties of this feature,
	 * either directly or in lists.
	 * 
	 * It does not recurse.
	 * 
	 * It ignores features that are referenced from elsewhere.
	 * 
	 * @return
	 * @throws CreateException
	 * @throws BeanMisconfiguredException
	 */
	public final FeatureList createListChildFeatures(boolean includeAdditionallyUsed)
			throws BeanMisconfiguredException {
		
		List<Feature> outUpcast = findChildrenOfClass( getOrCreateBeanFields(), Feature.class );

		FeatureList out = new FeatureList(outUpcast);

		if (includeAdditionallyUsed) {
			addAdditionallyUsedFeatures(out);
		}

		return out;
	}

	public String getParamDscr() {
		return describeChildBeans();
	}

	/**
	 * Adds other additionally-used features that aren't actually bean
	 * properties
	 * 
	 * NOTHING happens here. But it can be overriden by child classes
	 * appropriately
	 * 
	 * @param out a list to add these features to
	 *            
	 */
	public void addAdditionallyUsedFeatures(FeatureList out) {
	}

	// Dummy method, that children can optionally override
	public void beforeCalc(FeatureInitParams params, CacheSession cache) throws InitException {

	}

	protected LogErrorReporter getLogger() {
		return logger;
	}
	
	
	@Override
	public String toString() {
		return getFriendlyName();
	}

	@Override
	public FeatureCacheDefinition cacheDefinition() {
		return cacheDefinition;
	}

	protected CacheSession getCacheSession() {
		return cache;
	}
}
