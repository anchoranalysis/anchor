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

package org.anchoranalysis.bean.initializable;

import java.util.Optional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.initializable.parameters.BeanInitialization;
import org.anchoranalysis.bean.initializable.property.BeanInitializer;
import org.anchoranalysis.bean.initializable.property.InitializationParameterAssigner;
import org.anchoranalysis.core.exception.InitializeException;
import org.anchoranalysis.core.log.Logger;

/**
 * A bean that must be initialized with some parameters before being used.
 *
 * @author Owen Feehan
 * @param <B> bean-family type
 * @param <P> initialization-parameters type
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class InitializableBean<B, P extends BeanInitialization> extends AnchorBean<B> {

    /** Initializes a bean with another parameter. */
    private final BeanInitializer<P> beanInitializer;

    /** Assigns a parameter, if possible, to a bean - as used by {@code bean initializer}. */
    @Getter private final InitializationParameterAssigner propertyInitializer;

    /** Has the bean been initialized yet? */
    private Optional<P> initialization = Optional.empty();

    /** the logger */
    private Logger logger;

    /**
     * Initializes the bean with the necessary parameters.
     *
     * <p>This method should always be called <b>once</b> before using the other methods of the
     * bean.
     *
     * @param parameters parameters to initialize with.
     * @param logger a logger for events.
     * @throws InitializeException if initialization cannot complete successfully.
     */
    public void initialize(P parameters, Logger logger) throws InitializeException {
        this.initialization = Optional.of(parameters);
        this.logger = logger;
        onInitialization(parameters);
    }

    /**
     * Called after initialization. An empty implementation is provided, to be overridden as needed
     * in the sub-classes.
     *
     * @param initialization parameters used for initialization.
     * @throws InitializeException if initialization does not successfully complete.
     */
    public void onInitialization(P initialization) throws InitializeException {
        // Empty implementation to be replaced in sub-classes
    }

    /**
     * Initializes the bean and recursively all contained beans (who have compatible initialization
     * requirements)
     *
     * @param initializer the property-initializer to use.
     * @param logger logger.
     * @throws InitializeException if the initialization fails, including if correct
     *     initialization-parameters cannot be derived.
     */
    public void initRecursiveWithInitializer(BeanInitializer<?> initializer, Logger logger)
            throws InitializeException {
        HelperInit.initializeRecursive(this, initializer, logger);
    }

    /**
     * Initializes this object, and all children objects, so long as they have {@code P} once a Bean
     * doesn't have {@code P}, the children are not evaluated.
     *
     * @param parameters the parameters to initialize with.
     * @param logger logger.
     * @throws InitializeException if the initialization fails.
     */
    public void initializeRecursive(P parameters, Logger logger) throws InitializeException {
        beanInitializer.setParam(parameters);
        HelperInit.initializeRecursive(this, beanInitializer, logger);
    }

    /**
     * Has the been already been initialized via a call to {@link #initialize(BeanInitialization,
     * Logger)} or similar.
     *
     * @return true iff the bean has been initialized.
     */
    public boolean isInitialized() {
        return initialization.isPresent();
    }

    /**
     * The logger.
     *
     * @return the logger.
     */
    protected Logger getLogger() {
        return logger;
    }

    /**
     * The parameters used for initialization.
     *
     * @return the parameters passed for initialization.
     * @throws InitializeException if the bean has not been initialized.
     */
    protected P getInitialization() throws InitializeException {
        return initialization.orElseThrow(
                () ->
                        new InitializeException(
                                "No initialization-parameters exist as the been has not been initialized"));
    }
}
