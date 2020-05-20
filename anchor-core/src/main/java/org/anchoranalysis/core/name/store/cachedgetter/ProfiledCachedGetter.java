package org.anchoranalysis.core.name.store.cachedgetter;

import org.anchoranalysis.core.cache.WrapOperationAsCached;
import org.anchoranalysis.core.functional.Operation;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.apache.commons.lang.StringUtils;

/**
 * Does some profiling on get requests from the cache
 * 
 * <p>We HACK using static variables to monitor calls to different stores, so as to do a form of profiling.</p>
 * <p>As it is recurisvely called during the evaluation process, we subtract
//   the time spent in other 'execute' methods from the main one</p>
 * 
 * @author owen
 *
 * @param <T>
 * @param <E> exception thrown by operation
 */
public class ProfiledCachedGetter<T, E extends Throwable> extends WrapOperationAsCached<T,E> {

	private String name;
	private String storeDisplayName;
	private LogErrorReporter logErrorReporter;
	
	private static MeasuringSemaphoreExecutor<Throwable> semaphore = new MeasuringSemaphoreExecutor<>();
	
	private static final int STORE_DISPLAY_NAME_LENGTH = 30;
	private static final int NAME_LENGTH = 30;
	
	public ProfiledCachedGetter(Operation<T,E> getter, String name, String storeDisplayName, LogErrorReporter logErrorReporter) {
		super(getter);
		this.logErrorReporter = logErrorReporter;
		
		// We pad the display name to a fixed with
		this.name = StringUtils.rightPad( name, NAME_LENGTH );
		this.storeDisplayName = StringUtils.rightPad( storeDisplayName, STORE_DISPLAY_NAME_LENGTH );
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected T execute() throws E {
		// As a hack, we assume the exception type is always the same between calls to ProfiledCachedGetter
		// This may not be valid in practice. TODO consider an alternative means to profile.
		return ((MeasuringSemaphoreExecutor<E>) semaphore).execute(
			() -> super.execute(),
			name,
			storeDisplayName,
			logErrorReporter
		);
	}
}
