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
import org.anchoranalysis.bean.exception.BeanMisconfiguredException;
import org.anchoranalysis.bean.initializable.InitializableBean;
import org.anchoranalysis.bean.initializable.property.BeanInitializer;
import org.anchoranalysis.core.exception.InitializeException;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.bean.list.FeatureListFactory;
import org.anchoranalysis.feature.calculate.FeatureCalculationException;
import org.anchoranalysis.feature.calculate.FeatureCalculationInput;
import org.anchoranalysis.feature.initialization.FeatureInitialization;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * Base class for a measurement that calculates a result (double) given <i>input</i> parameters.
 *
 * <p>This is a key base class in the <a href="http://www.anchoranalysis.org">Anchor</a> framework,
 * and many algorithms and procedures use a {@link Feature} on particular input-types to calculate
 * measurements in a flexible way, and often to use these measurements in a machine-learning
 * algorithm.
 *
 * <p>See <a href="https://en.wikipedia.org/wiki/Feature_(machine_learning)">Feature (Machine
 * learning) on Wikipedia</a> for general background on the concept of a <i>feature</i> in Machine
 * Learning.
 *
 * @author Owen Feehan
 * @param <T> input-type from which a measurement is calculated.
 */
public abstract class Feature<T extends FeatureInput>
        extends InitializableBean<Feature<T>, FeatureInitialization> {

    // START BEAN PROPERTIES
    /**
     * An optional additional name that be associated with the feature (defaults to an empty
     * string).
     */
    @BeanField @AllowEmpty @Getter @Setter private String customName = "";
    // END BEAN PROPERTIES

    /** Creates with the default initializer. */
    protected Feature() {
        super(new BeanInitializer<>(FeatureInitialization.class), new FeatureAssigner<>());
    }

    /**
     * Creates with a custom initializer.
     *
     * @param propertyInitializer the custom initializer.
     */
    protected Feature(BeanInitializer<FeatureInitialization> propertyInitializer) {
        super(propertyInitializer, new FeatureAssigner<>());
    }

    /**
     * Called after initialization.
     *
     * <p>An empty implementation is provided, to be overridden as needed in the sub-classes.
     */
    @Override
    public void onInitialization(FeatureInitialization initialization) throws InitializeException {
        super.onInitialization(initialization);
        beforeCalc(initialization);
    }

    /**
     * The class corresponding to feature input-type.
     *
     * <p>i.e. corresponding to the {@code T} template parameter.
     *
     * @return the class.
     */
    public abstract Class<? extends FeatureInput> inputType();

    @Override
    public final String describeBean() {
        String paramDscr = describeParameters();
        if (!paramDscr.isEmpty()) {
            return String.format("%s(%s)", getBeanName(), paramDscr);
        } else {
            return getBeanName();
        }
    }

    /**
     * A long human-readable description of the feature and some or all of its parameterization.
     *
     * @return the description.
     */
    public String descriptionLong() {
        return describeBean();
    }

    /**
     * Duplicates the feature as per {@link #duplicateBean} but sets a particular custom-name.
     *
     * @param customName the custom-name to set.
     * @return a duplicated (deep copy of bean attributes) feature, identical to current feature,
     *     but with the specified custom-name.
     */
    public Feature<T> duplicateChangeName(String customName) {
        Preconditions.checkNotNull(customName);
        Feature<T> duplicated = duplicateBean();
        duplicated.setCustomName(customName);
        return duplicated;
    }

    /**
     * A user-friendly human-readable name for the {@link Feature}.
     *
     * <p>If a custom-name has been assigned, this is returned, otherwise the {@link
     * #descriptionLong()}.
     *
     * @return the user-friendly human-readable name.
     */
    public String getFriendlyName() {
        if (!getCustomName().isEmpty()) {
            return getCustomName();
        } else {
            return descriptionLong();
        }
    }

    /**
     * Returns a list of Features that exist as bean-properties of this feature, either directly or
     * in lists.
     *
     * <p>It does not recurse.
     *
     * <p>It ignores features that are referenced from elsewhere.
     *
     * @return the list of features.
     * @throws BeanMisconfiguredException if the feature-beans are not structured as expected.
     */
    public final FeatureList<FeatureInput> createListChildFeatures()
            throws BeanMisconfiguredException {
        return FeatureListFactory.wrapReuse(findFieldsOfClass(Feature.class));
    }

    /**
     * A human-readable description of the parameterization of the bean.
     *
     * @return the description.
     */
    public String describeParameters() {
        return describeChildren();
    }

    @Override
    public String toString() {
        return getFriendlyName();
    }

    /**
     * Casts the feature to having a different input-type.
     *
     * <p>Note that no active compile-type check occurs, so be careful that this is used
     * appropriately.
     *
     * @param <S> the type to cast to, which should be a sub-type of the existing-type.
     * @return the same instance, cast as having a different input-type.
     */
    @SuppressWarnings("unchecked")
    public <S extends T> Feature<S> castAs() {
        return (Feature<S>) this;
    }

    /**
     * Calculates the result of a feature and throws an exception if the feature has not been
     * initialized.
     *
     * <p>It is not recommended to directly use this method to calculate a feature's value, but
     * rather to:
     *
     * <ul>
     *   <li><b>Externally</b>, for code outside a {@link Feature}'s implementation, please use the
     *       <b>feature-session</b> classes, who will perform the calculation using caching to
     *       reduce redundant computation.
     *   <li><b>Internally</b>, for code inside a {@link Feature}'s implementation, please call
     *       {@link FeatureCalculationInput#calculate(Feature)} and similar methods, who will
     *       back-reference the session above, to take advantage of the caching.
     * </ul>
     *
     * @param input the input to the calculation.
     * @return the feature-value corresponding to {@code input} for this feature.
     * @throws FeatureCalculationException if the feature has not been initialized.
     */
    public double calculateCheckInitialized(FeatureCalculationInput<T> input)
            throws FeatureCalculationException {
        if (!isInitialized()) {
            throw new FeatureCalculationException(
                    String.format("The feature (%s) has not been initialized", this.toString()));
        }

        return calculate(input);
    }

    /**
     * Dummy method, that children can optionally override.
     *
     * @param initialization initialization parameters.
     * @throws InitializeException if initialization cannot complete successfully.
     */
    protected void beforeCalc(FeatureInitialization initialization) throws InitializeException {
        // Does nothing. To be overridden in children if needed.
    }

    /**
     * Calculates a value for some input.
     *
     * @param input the input to the calculation.
     * @return the result of the calculation.
     * @throws FeatureCalculationException if the calculation cannot successfully complete.
     */
    protected abstract double calculate(FeatureCalculationInput<T> input)
            throws FeatureCalculationException;

    /**
     * Copies fields in this (base) class to {@code target}.
     *
     * <p>This is intended to be called by sub-classes to help when duplicating.
     *
     * @param target the object fields are assigned to.
     */
    protected void assignTo(Feature<FeatureInput> target) {
        target.customName = customName;
    }
}
