/* (C)2020 */
package org.anchoranalysis.core.name.provider;

import org.anchoranalysis.core.error.combinable.AnchorCombinableException;
import org.anchoranalysis.core.index.GetOperationFailedException;

public class NamedProviderGetException extends AnchorCombinableException {

    /** */
    private static final long serialVersionUID = 1L;

    private NamedProviderGetException(String key, Throwable cause) {
        super(key, cause);
    }

    public static NamedProviderGetException nonExistingItem(String key) {
        return new NamedProviderGetException(
                key,
                new GetOperationFailedException(
                        String.format("The item '%s' doesn't exist!", key)));
    }

    public static NamedProviderGetException nonExistingItem(String key, String storeName) {
        return new NamedProviderGetException(
                key,
                new GetOperationFailedException(
                        String.format("The item '%s' doesn't exist in %s!", key, storeName)));
    }

    public static NamedProviderGetException wrap(String key, Throwable cause) {
        return new NamedProviderGetException(key, cause);
    }

    @Override
    protected boolean canExceptionBeCombined(Throwable exc) {
        return exc instanceof NamedProviderGetException;
    }

    @Override
    protected boolean canExceptionBeSkipped(Throwable exc) {
        return !(exc instanceof NamedProviderGetException);
    }

    @Override
    public Throwable summarize() {
        return super.combineDscrsRecursively("at ", "\n-> ");
    }

    @Override
    protected String createMessageForDscr(String dscr) {
        return dscr;
    }
}
