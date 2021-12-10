/*-
 * #%L
 * anchor-bean
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

package org.anchoranalysis.bean.initializable.property;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import org.anchoranalysis.bean.initializable.InitializableBean;
import org.anchoranalysis.bean.initializable.parameters.NullInitialization;
import org.anchoranalysis.core.exception.InitializeException;
import org.anchoranalysis.core.functional.OptionalFactory;
import org.anchoranalysis.core.log.Logger;

/**
 * Tries to initialize a bean and it's children with particular parameters, if possible.
 *
 * <p>If the passed parameters are not directly suitable for initialization, other parameters may
 * also be derived and used instead.
 *
 * <p><div>Specifically, two types of properties will be initialized:
 *
 * <ul>
 *   <li>Properties that have identical property-type to {@code P} (the current parameters).
 *   <li>Properties with another property-type that can be extracted from {@code P} (the current
 *       parameters).
 * </ul>
 *
 * </div>
 *
 * @author Owen Feehan
 * @param <P> init-param type
 */
public class BeanInitializer<P> {

    /** The parameter (or its derivatives) used for initialization. */
    private P initializationParam;

    /** Type of parameters to be propagated, corresponding to {@code P}. */
    @Getter private final Class<?> initializationType;

    /**
     * A list of possible alternative initialization parameter types that may be extracted from
     * {@code P}.
     *
     * <p>Additionally extracters may be added in constructor, depending on which constructor is
     * used.
     */
    private List<ExtractDerivedParameter<P, ?>> extracters = listExtractersWithDefault();

    /**
     * Simpler case where only the current-parameters can be initialized, but for no further
     * property-types.
     *
     * @param initializationType type of parameters to be propagated, corresponding to {@code <P>}.
     */
    public BeanInitializer(Class<?> initializationType) {
        this.initializationType = initializationType;
    }

    /**
     * More complex-case where both current-parameters and other extracted property-types can be
     * initialized.
     *
     * @param initializationType type of parameters to be propagated, corresponding to {@code <P>}.
     * @param extractersToAdd extracters to used for other property-types that can be derived from
     *     current property-type.
     */
    public BeanInitializer(
            Class<?> initializationType, List<ExtractDerivedParameter<P, ?>> extractersToAdd) {
        this.initializationType = initializationType;

        // Add any additional extracters
        this.extracters.addAll(extractersToAdd);
    }

    /**
     * Sets the current parameters used for propagation.
     *
     * @param param the parameters to be assigned, and to be propagated into initialized beans.
     */
    public void setParam(P param) {
        this.initializationParam = param;
    }

    /**
     * Applies the initialization to a bean if possible.
     *
     * <p>As a prerequisite, {@link #setParam(Object)} must be called before calling this method.
     *
     * <p>Initialization is possible if either the parameters set above, or derived parameters are
     * of acceptable type.
     *
     * @param bean the bean to be initialized.
     * @param parent the parent bean of {@code bean}.
     * @param logger logger for any errors.
     * @return true if some kind of initialization occurred with this property, false otherwise.
     * @throws InitializeException if initialization could not successfully complete.
     */
    public boolean applyInitializationIfPossibleTo(Object bean, Object parent, Logger logger)
            throws InitializeException {

        assert (initializationParam != null);

        // Properties with identical property-type to the current parameters
        if (initializeIdenticalParamTypes(bean, parent, logger)) {
            return true;
        }

        // Properties with other property-types (whose parameters can be extracted from the current
        // parameters)
        return initializeExtractedParameters(bean, parent, logger);
    }

    /**
     * Initializes properties that have an identical property-type as currently.
     *
     * <p>This has the effect of recursively propagating the the initialization-parameters.
     */
    private boolean initializeIdenticalParamTypes(Object bean, Object parent, Logger logger)
            throws InitializeException {
        return initializationParam != null
                && initializeMatchingPropertiesWith(
                        bean, parent, logger, initializationParam.getClass(), initializationParam);
    }

    /**
     * Initializes properties that don't have the same property type, but another property-type that
     * can be extracted.
     *
     * <p>The {{@link #extracters} defines what other property-types can be extracted and how to do
     * it.
     */
    private boolean initializeExtractedParameters(Object bean, Object parent, Logger logger)
            throws InitializeException {

        for (ExtractDerivedParameter<P, ?> extract : extracters) {

            Optional<?> extractedParam = extract.extractIfPossible(initializationParam);

            // We can always initialize anything that doesn't take parameters (i.e. takes
            // NullInitialization)
            if (extractedParam.isPresent()
                    && initializeMatchingPropertiesWith(
                            bean, parent, logger, extract.getTargetClass(), extractedParam.get())) {
                return true;
            }
        }

        return false;
    }

    private boolean initializeMatchingPropertiesWith(
            Object bean,
            Object parent,
            Logger logger,
            Class<?> propertyClassToMatch,
            Object paramToInitWith)
            throws InitializeException {
        Optional<InitializationParameterAssigner> property =
                areParametersAccepted(bean, propertyClassToMatch);
        if (property.isPresent()) {
            property.get().assignInitializationToProperties(bean, paramToInitWith, parent, logger);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Does the bean accept {@code paramType} for initialization?
     *
     * @return a {@link InitializationParameterAssigner} if it accepts them, otherwise {@link
     *     Optional#empty()}.
     */
    private Optional<InitializationParameterAssigner> areParametersAccepted(
            Object bean, Class<?> paramType) {

        if (bean instanceof InitializableBean) {

            InitializableBean<?, ?> initBean = (InitializableBean<?, ?>) bean;
            InitializationParameterAssigner initializer = initBean.getPropertyInitializer();
            return OptionalFactory.create(initializer.accepts(paramType), initializer);

        } else {
            return Optional.empty();
        }
    }

    /**
     * Creates a new list of extracters containing only an extracter for {@link NullInitialization}.
     */
    private List<ExtractDerivedParameter<P, ?>> listExtractersWithDefault() {
        List<ExtractDerivedParameter<P, ?>> list = new ArrayList<>();
        list.add(nullExtracter());
        return list;
    }

    /**
     * A simple extractor used to apply {@link NullInitialization} to any property looking for this
     * property-type.
     */
    private ExtractDerivedParameter<P, NullInitialization> nullExtracter() {
        return new ExtractDerivedParameter<>(
                NullInitialization.class, parameters -> NullInitialization.instance());
    }
}
