package org.anchoranalysis.image.init;

import org.anchoranalysis.bean.init.InitializableBean;
import org.anchoranalysis.bean.init.params.BeanInitParams;
import org.anchoranalysis.bean.init.property.PropertyInitializer;
import org.anchoranalysis.core.bridge.IObjectBridge;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.log.LogErrorReporter;

/**
 * A bridge that performs initialization of objects
 * 
 * @author Owen Feehan
 *
 * @param <S> source (bean) type
 * @param <T> destination type
 * @param <V> initialization-parameters type
 */
class InitBridge<
	S extends InitializableBean<?,V>,
	T,
	V extends BeanInitParams
> implements IObjectBridge<S,T,OperationFailedException> {

	private PropertyInitializer<?> pi;
	private LogErrorReporter logger;
	private IObjectBridge<S, T, CreateException> beanBridge;
	
	/**
	 * Constructor
	 * 
	 * @param pi used to initialize properties in the source-bean
	 * @param logger passed to initialized object
	 * @param beanBridge maps the bean to another type
	 */
	public InitBridge(
		PropertyInitializer<?> pi,
		LogErrorReporter logger,
		IObjectBridge<S, T, CreateException> beanBridge
	) {
		super();
		this.pi = pi;
		this.logger = logger;
		this.beanBridge = beanBridge;
	}
	
	@Override
	public T bridgeElement(S sourceObject) throws OperationFailedException {
		assert logger!=null;
		try {
			// Initialize
			sourceObject.initRecursiveWithInitializer(pi, logger);
			
			// Bridge the source to the destination
			return beanBridge.bridgeElement(sourceObject);
		} catch (InitException | CreateException e) {
			throw new OperationFailedException(e);
		}
	}
}
