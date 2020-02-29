package org.anchoranalysis.core.name.store.cachedgetter;

/*-
 * #%L
 * anchor-core
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import org.anchoranalysis.core.cache.ExecuteException;
import org.anchoranalysis.core.cache.Operation;
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
 */
public class ProfiledCachedGetter<T> extends CachedGetter<T> {

	private String name;
	private String storeDisplayName;
	private LogErrorReporter logErrorReporter;
	
	private static MeasuringSemaphoreExecutor semaphore = new MeasuringSemaphoreExecutor();
	
	private static final int STORE_DISPLAY_NAME_LENGTH = 30;
	private static final int NAME_LENGTH = 30;
	
	public ProfiledCachedGetter(Operation<T> getter, String name, String storeDisplayName, LogErrorReporter logErrorReporter) {
		super(getter);
		this.logErrorReporter = logErrorReporter;
		
		// We pad the display name to a fixed with
		this.name = StringUtils.rightPad( name, NAME_LENGTH );
		this.storeDisplayName = StringUtils.rightPad( storeDisplayName, STORE_DISPLAY_NAME_LENGTH );
	}
	
	@Override
	protected T execute() throws ExecuteException {
		return semaphore.execute(
			() -> super.execute(),
			name,
			storeDisplayName,
			logErrorReporter
		);
	}
}
