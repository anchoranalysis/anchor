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


import org.anchoranalysis.bean.annotation.AllowEmpty;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.error.BeanMisconfiguredException;
import org.anchoranalysis.bean.init.property.PropertyInitializer;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.bean.list.FeatureListFactory;
import org.anchoranalysis.feature.cache.SessionInput;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.FeatureInitParams;
import org.anchoranalysis.feature.calc.InitializableFeature;
import org.anchoranalysis.feature.input.FeatureInput;


/**
 * Feature that calculates a result (double) for some parameters
 * 
 * <p>It should always be called in a context of a session, which first initializes the feature before doing calculations.</p>
 * 
 * @author owen
 *
 * @param <T> input-type 
 */
public abstract class Feature<T extends FeatureInput> extends FeatureBase<T> implements InitializableFeature<T> {

	// START BEAN PROPERTIES
	@BeanField
	@AllowEmpty
	private String customName = "";
	// END BEAN PROPERTIES

	private transient LogErrorReporter logger;

	private boolean hasBeenInit = false;

	protected Feature() {
		super();
	}
	
	protected Feature(PropertyInitializer<FeatureInitParams> propertyInitializer) {
		super(propertyInitializer);
	}
	
	@Override
	public final String getBeanDscr() {
		String paramDscr = getParamDscr();

		if (!paramDscr.isEmpty()) {
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
	
	/**
	 * Duplicates the feature as per {@link duplicateBean} but sets a particular custom-name
	 * 
	 * @param customName the custom-name to set
	 * @return a duplicated (deep copy of bean attributes) feature, identical to current feature, but with the specified custom-name
	 */
	public Feature<T> duplicateChangeName(String customName) {
		Feature<T> duplicated = duplicateBean();
		duplicated.setCustomName(customName);
		return duplicated;
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

	public double calcCheckInit(SessionInput<T> input) throws FeatureCalcException {
		if (!hasBeenInit) {
			throw new FeatureCalcException(String.format(
					"The feature (%s) has not been initialized",
					this.toString()));
		}
	
		double ret = calc( input );
		
		assert( !Double.isNaN(ret) );
		assert( Double.isFinite(ret) );
		
		return ret;
	}
	
	// Calculates a value for some parameters
	protected abstract double calc(SessionInput<T> input) throws FeatureCalcException;

	protected void duplicateHelper(Feature<FeatureInput> out) {
		out.customName = new String(customName);
	}
	
	/**
	 * Initializes the bean with important parameters needed for calculation.  Must be called (one-time) before feature calculations.
	 * 
	 * @param params parameters used for initialization that are simply passed to beforeCalc()
	 * @param logger logger
	 * 
	 * @param logger the logger, saved and made available to the feature
	 */
	@Override
	public void init(
		FeatureInitParams params,
		FeatureBase<T> parentFeature,
		LogErrorReporter logger
	) throws InitException {
				
		hasBeenInit = true;
		this.logger = logger;
		beforeCalc( );
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
	public final FeatureList<FeatureInput> createListChildFeatures(boolean includeAdditionallyUsed)
			throws BeanMisconfiguredException {

		FeatureList<FeatureInput> out = FeatureListFactory.wrapReuse(
			findChildrenOfClass( getOrCreateBeanFields(), Feature.class )
		);

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
	public void addAdditionallyUsedFeatures(FeatureList<FeatureInput> out) {
	}

	/** Dummy method, that children can optionally override */
	public void beforeCalc() throws InitException {
		// Does nothing. To be overridden in children if needed.
	}

	protected LogErrorReporter getLogger() {
		return logger;
	}
	
	
	@Override
	public String toString() {
		return getFriendlyName();
	}
	
	/** Upcasts the feature to FeatureCalcParams */
	@SuppressWarnings("unchecked")
	public Feature<FeatureInput> upcast() {
		return (Feature<FeatureInput>) this;
	}
	
	/** Downcasts the feature  */
	@SuppressWarnings("unchecked")
	public <S extends T> Feature<S> downcast() {
		return (Feature<S>) this;
	}
}
