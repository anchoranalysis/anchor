/* (C)2020 */
package org.anchoranalysis.bean.init;

import java.util.Optional;
import lombok.Getter;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.init.params.BeanInitParams;
import org.anchoranalysis.bean.init.params.ParamsInitializer;
import org.anchoranalysis.bean.init.property.PropertyDefiner;
import org.anchoranalysis.bean.init.property.PropertyInitializer;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.core.log.Logger;

/**
 * A bean that must be initialized with some parameters before being used.
 *
 * @author Owen Feehan
 * @param <B> bean-family type
 * @param <P> initialization-parameters type
 */
public abstract class InitializableBean<B, P extends BeanInitParams> extends AnchorBean<B>
        implements ParamsInitializer<P> {

    private final PropertyInitializer<P> propertyInitializer;

    @Getter private final PropertyDefiner propertyDefiner;

    /** Has the bean been initialized yet? */
    private Optional<P> initializationParameters = Optional.empty();

    /** the logger */
    private Logger logger;

    protected InitializableBean(
            PropertyInitializer<P> propertyInitializer, PropertyDefiner propertyDefiner) {
        this.propertyInitializer = propertyInitializer;
        this.propertyDefiner = propertyDefiner;
    }

    // Dummy method, that children can optionally override
    @Override
    public void init(P params, Logger logger) throws InitException {
        this.initializationParameters = Optional.of(params);
        this.logger = logger;
        onInit(params);
    }

    /**
     * Called after initialization. An empty impelmentation is provided, to be overridden as needed
     * in the sub-classes.
     */
    public void onInit(P paramsInit) throws InitException {
        // Empty implementation to be replaced in sub-classes
    }

    /**
     * Initializes the bean and recursively all contained beans (who have compatible initialization
     * requirements)
     *
     * <p>The correct initialization parameters are found for each bean via the {@code pi}
     * parameter;
     *
     * @param pi the property-initializer to use
     * @param logger logger
     * @throws InitException if the initialization fails, including if correct
     *     initialization-parameters cannot be derived
     */
    public void initRecursiveWithInitializer(PropertyInitializer<?> pi, Logger logger)
            throws InitException {
        HelperInit.initRecursive(this, pi, logger);
    }

    /**
     * Inits this object, and all children objects, so long as they have P Once a Bean doesn't have
     * {@code P}, the children are not evaluated
     *
     * @param params init-params
     * @param logger logger
     * @throws InitException if the initialization fails
     */
    public void initRecursive(P params, Logger logger) throws InitException {
        propertyInitializer.setParam(params);
        HelperInit.initRecursive(this, propertyInitializer, logger);
    }

    public boolean isInitialized() {
        return initializationParameters.isPresent();
    }

    protected PropertyInitializer<P> getPropertyInitializer() {
        return propertyInitializer;
    }

    /** The logger */
    protected Logger getLogger() {
        return logger;
    }

    protected P getInitializationParameters() {
        return initializationParameters.orElseThrow(
                () ->
                        new AnchorFriendlyRuntimeException(
                                "No initialization-params as the been has not been initialized"));
    }
}
