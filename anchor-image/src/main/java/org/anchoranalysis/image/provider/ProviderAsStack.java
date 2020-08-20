package org.anchoranalysis.image.provider;

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.stack.Stack;

/** Provides a stack */
public interface ProviderAsStack {

    /** Creates from the image-bean in the form of a stack */
    Stack createAsStack() throws CreateException;
}
