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
import org.anchoranalysis.bean.initializable.InitializableBean;
import org.anchoranalysis.bean.initializable.params.NullInitialization;
import org.anchoranalysis.core.exception.InitException;
import org.anchoranalysis.core.log.Logger;

/**
 * Tries to initialize a bean's property with particular (or derived) parameters
 *
 * <p><div> Specifically, two types of properties will be initialized:
 *
 * <ul>
 *   <li>Properties that have identical property-type to P (the current parameters)
 *   <li>Properties with another property-type that can be extracted from P (the current parameters)
 * </ul>
 *
 * </div>
 *
 * @author Owen Feehan
 * @param <P> init-param type
 */
public class PropertyInitializer<P> {

    private P param;
    private Class<?> initParamType;

    // Initially only NullParamsInit are extracted (as they can be applied irrespective of current
    // parameters)
    // Depending on the constructor, more extracters may be added to this list
    private List<ExtractFromParam<P, ?>> extracters = listExtractersWithDefault();

    /**
     * Simpler case where only the current-parameters can be initialized, but for no further
     * property-types.
     *
     * @param initParamType type of parameters to be propagated
     */
    public PropertyInitializer(Class<?> initParamType) {
        super();
        this.initParamType = initParamType;
    }

    /**
     * More complex-case where both current-parameters and other extracted property-types can be
     * initialized.
     *
     * @param initParamType type of parameters to be propagated
     * @param extractersToAdd extracters to used for other property-types that can be derived from
     *     current property-type
     */
    public PropertyInitializer(
            Class<?> initParamType, List<ExtractFromParam<P, ?>> extractersToAdd) {
        super();
        this.initParamType = initParamType;

        // Add any additional extracters
        this.extracters.addAll(extractersToAdd);
    }

    /** Sets the current parameters used for propagation */
    public void setParam(P param) {
        this.param = param;
    }

    /**
     * Applies the initialization to a property using the current or extracted params if possible.
     *
     * <p>It is essential that {@link #setParam(Object)} is called before this method
     *
     * @param propertyValue bean-property to be initialized (as a root)
     * @param parent the parent bean in which this property resides
     * @param logger logger for any errrors
     * @return true if some kind of initialization occurred with this property, false otherwise
     * @throws InitException
     */
    public boolean applyInitializationIfPossibleTo(
            Object propertyValue, Object parent, Logger logger) throws InitException {

        assert (param != null);

        // Properties with identical property-type to the current parameters
        if (initIdenticalParamTypes(propertyValue, parent, logger)) {
            return true;
        }

        // Properties with other property-types (whose parameters can be extracted from the current
        // parameters)
        return initExtractedParams(propertyValue, parent, logger);
    }

    public Class<?> getInitParamType() {
        return initParamType;
    }

    /**
     * Initializes properties that have an identical property-type as currently
     *
     * <p>This has the effect of recursively propagating the the initialization
     *
     * @param propertyValue
     * @param parent
     * @param logger
     * @return
     * @throws InitException
     */
    private boolean initIdenticalParamTypes(Object propertyValue, Object parent, Logger logger)
            throws InitException {
        return param != null
                && initMatchingPropertiesWith(
                        propertyValue, parent, logger, param.getClass(), param);
    }

    /**
     * Initializes properties that don't have the same property type, but another property-type that
     * can be extracted
     *
     * <p>The {{@link #extracters} defines what other property-types can be extracted and how to do
     * it
     *
     * @param propertyValue
     * @param parent
     * @param logger
     * @return
     * @throws InitException
     */
    private boolean initExtractedParams(Object propertyValue, Object parent, Logger logger)
            throws InitException {

        for (ExtractFromParam<P, ?> extract : extracters) {

            // We can always initialize anything that doesn't take parameters (i.e. takes
            // NullInitParams)
            if (extract.acceptsAsSource(param.getClass())
                    && initMatchingPropertiesWith(
                            propertyValue,
                            parent,
                            logger,
                            extract.getClassOfTarget(),
                            extract.extract(param))) {
                return true;
            }
        }

        return false;
    }

    private boolean initMatchingPropertiesWith(
            Object propertyValue,
            Object parent,
            Logger logger,
            Class<?> propertyClassToMatch,
            Object paramToInitWith)
            throws InitException {
        Optional<PropertyDefiner> pd = findPropertyThatDefines(propertyValue, propertyClassToMatch);
        if (pd.isPresent()) {
            pd.get().doInitFor(propertyValue, paramToInitWith, parent, logger);
            return true;
        }
        return false;
    }

    private Optional<PropertyDefiner> findPropertyThatDefines(
            Object propertyValue, Class<?> paramType) {

        if (propertyValue instanceof InitializableBean) {

            InitializableBean<?, ?> initBean = (InitializableBean<?, ?>) propertyValue;
            PropertyDefiner pd = initBean.getPropertyDefiner();

            if (pd.accepts(paramType)) {
                return Optional.of(pd);
            } else {
                return Optional.empty();
            }

        } else {
            return Optional.empty();
        }
    }

    private List<ExtractFromParam<P, ?>> listExtractersWithDefault() {
        List<ExtractFromParam<P, ?>> list = new ArrayList<>();
        list.add(nullExtracter());
        return list;
    }

    /**
     * A simple extractor used to apply {@link NullInitialization} to any property looking for this
     * property-type
     */
    private ExtractFromParam<P, NullInitialization> nullExtracter() {
        return new ExtractFromParam<>(NullInitialization.class, params -> NullInitialization.instance());
    }
}
