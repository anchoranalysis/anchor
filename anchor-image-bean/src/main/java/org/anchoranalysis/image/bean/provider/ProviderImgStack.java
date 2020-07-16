/* (C)2020 */
package org.anchoranalysis.image.bean.provider;

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.stack.Stack;

// Provides multiple channels
public interface ProviderImgStack {

    Stack createStack() throws CreateException;
}
