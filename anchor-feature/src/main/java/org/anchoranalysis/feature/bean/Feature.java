/*-
 * #%L
 * anchor-feature
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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

package org.anchoranalysis.feature.bean;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.AllowEmpty;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.error.BeanMisconfiguredException;
import org.anchoranalysis.bean.init.InitializableBean;
import org.anchoranalysis.bean.init.property.PropertyInitializer;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.bean.list.FeatureListFactory;
import org.anchoranalysis.feature.cache.SessionInput;
import org.anchoranalysis.feature.calc.FeatureCalculationException;
import org.anchoranalysis.feature.calc.FeatureInitParams;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * Feature that calculates a result (double) for some parameters
 *
 * <p>It should always be called in a context of a session, which first initializes the feature
 * before doing calculations.
 *
 * @author Owen Feehan
 * @param <T> input-type
 */
public abstract class Feature<T extends FeatureInput>
        extends InitializableBean<Feature<T>, FeatureInitParams> {

    // START BEAN PROPERTIES
    /**
     * An optional additional name that be associated with the feature (defaults to an empty string)
     */
    @BeanField @AllowEmpty @Getter @Setter private String customName = "";
    // END BEAN PROPERTIES

    protected Feature() {
        super(
                new PropertyInitializer<FeatureInitParams>(FeatureInitParams.class),
                new FeatureDefiner<>());
    }

    protected Feature(PropertyInitializer<FeatureInitParams> propertyInitializer) {
        super(propertyInitializer, new FeatureDefiner<>());
    }

    /**
     * Called after initialization. An empty implementation is provided, to be overridden as needed
     * in the sub-classes.
     */
    @Override
    public void onInit(FeatureInitParams paramsInit) throws InitException {
        super.onInit(paramsInit);
        beforeCalc(paramsInit);
    }

    /**
     * The class corresponding to feature input-type (i.e. the {@code T} template parameter).
     *
     * @return
     */
    public abstract Class<? extends FeatureInput> inputType();

    @Override
    public final String getBeanDscr() {
        String paramDscr = getParamDscr();
        if (!paramDscr.isEmpty()) {
            return String.format("%s(%s)", getBeanName(), paramDscr);
        } else {
            return getBeanName();
        }
    }

    public String getDscrLong() {
        return getBeanDscr();
    }

    /**
     * Duplicates the feature as per {@link #duplicateBean} but sets a particular custom-name
     *
     * @param customName the custom-name to set
     * @return a duplicated (deep copy of bean attributes) feature, identical to current feature,
     *     but with the specified custom-name
     */
    public Feature<T> duplicateChangeName(String customName) {
        Preconditions.checkNotNull(customName);
        Feature<T> duplicated = duplicateBean();
        duplicated.setCustomName(customName);
        return duplicated;
    }

    public String getFriendlyName() {
        if (!getCustomName().isEmpty()) {
            return getCustomName();
        } else {
            return getDscrLong();
        }
    }

    public String getDscrWithCustomName() {
        if (!getCustomName().isEmpty()) {
            return String.format("%s: %s", getCustomName(), getBeanDscr());
        } else {
            return getBeanDscr();
        }
    }

    public double calcCheckInit(SessionInput<T> input) throws FeatureCalculationException {
        if (!isInitialized()) {
            throw new FeatureCalculationException(
                    String.format("The feature (%s) has not been initialized", this.toString()));
        }

        return calc(input);
    }

    // Calculates a value for some parameters
    protected abstract double calc(SessionInput<T> input) throws FeatureCalculationException;

    protected void duplicateHelper(Feature<FeatureInput> out) {
        out.customName = customName;
    }

    /**
     * Returns a list of Features that exist as bean-properties of this feature, either directly or
     * in lists.
     *
     * <p>It does not recurse.
     *
     * <p>It ignores features that are referenced from elsewhere.
     *
     * @return
     * @throws BeanMisconfiguredException
     */
    public final FeatureList<FeatureInput> createListChildFeatures()
            throws BeanMisconfiguredException {

        return FeatureListFactory.wrapReuse(
                        findChildrenOfClass(getOrCreateBeanFields(), Feature.class));
    }

    public String getParamDscr() {
        return describeChildBeans();
    }

    /**
     * Dummy method, that children can optionally override
     *
     * @param paramsInit initialization parameters
     */
    protected void beforeCalc(FeatureInitParams paramsInit) throws InitException {
        // Does nothing. To be overridden in children if needed.
    }

    @Override
    public String toString() {
        return getFriendlyName();
    }

    /** Downcasts the feature */
    @SuppressWarnings("unchecked")
    public <S extends T> Feature<S> downcast() {
        return (Feature<S>) this;
    }
}
