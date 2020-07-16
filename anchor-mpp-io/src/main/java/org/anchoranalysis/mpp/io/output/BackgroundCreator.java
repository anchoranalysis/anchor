/* (C)2020 */
package org.anchoranalysis.mpp.io.output;

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.core.name.store.NamedProviderStore;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.image.stack.Stack;

public class BackgroundCreator {

    private BackgroundCreator() {}

    public static DisplayStack createBackground(
            NamedProviderStore<Stack> stackCollection, String backgroundStackName)
            throws CreateException {
        try {
            return DisplayStack.create(stackCollection.getException(backgroundStackName));
        } catch (NamedProviderGetException e) {
            throw new CreateException("Cannot create background display-stack", e);
        }
    }
}
