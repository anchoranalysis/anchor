/*-
 * #%L
 * anchor-mpp-io
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

package org.anchoranalysis.mpp.io.output;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.identifier.provider.NamedProviderGetException;
import org.anchoranalysis.core.identifier.provider.store.NamedProviderStore;
import org.anchoranalysis.image.bean.displayer.StackDisplayer;
import org.anchoranalysis.image.core.stack.DisplayStack;
import org.anchoranalysis.image.core.stack.Stack;

/** Utility class for creating background {@link DisplayStack}s. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BackgroundCreator {

    /**
     * Creates a background {@link DisplayStack} from a named stack in a collection.
     *
     * @param stackCollection the {@link NamedProviderStore} containing the stacks
     * @param backgroundStackName the name of the background stack in the collection
     * @param displayer the {@link StackDisplayer} used to derive the display stack
     * @return a new {@link DisplayStack} representing the background
     * @throws CreateException if the background stack cannot be retrieved or processed
     */
    public static DisplayStack createBackground(
            NamedProviderStore<Stack> stackCollection,
            String backgroundStackName,
            StackDisplayer displayer)
            throws CreateException {
        try {
            return displayer.deriveFrom(stackCollection.getException(backgroundStackName));
        } catch (NamedProviderGetException e) {
            throw new CreateException("Cannot get background display-stack with identifier: ", e);
        }
    }
}
