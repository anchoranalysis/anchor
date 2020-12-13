/*-
 * #%L
 * anchor-image-bean
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

package org.anchoranalysis.image.bean.nonbean.init;

import lombok.AllArgsConstructor;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.define.Define;
import org.anchoranalysis.bean.initializable.InitializableBean;
import org.anchoranalysis.bean.initializable.params.BeanInitParams;
import org.anchoranalysis.bean.initializable.property.PropertyInitializer;
import org.anchoranalysis.bean.provider.Provider;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.functional.checked.CheckedFunction;
import org.anchoranalysis.core.identifier.provider.store.NamedProviderStore;
import org.anchoranalysis.core.log.Logger;

/**
 * Helps populates a {@link NamedProviderStore} from the contents of a {@link Define}.
 *
 * <p>Objects can be added directly (no initialization) or with initialization.
 *
 * @author Owen Feehan
 * @param <V> initialization-parameters for provider
 */
@AllArgsConstructor
public class PopulateStoreFromDefine<V extends BeanInitParams> {

    /** Define source for objects. */
    private Define define;

    /** Used to intitialize the properties of objects added with initialization. */
    private PropertyInitializer<?> propertyInitializer;

    /** Passed to objects added with initialization. */
    private Logger logger;

    /**
     * Copies objects of a particular class from the define WITHOUT doing any initialization
     *
     * @param <S> type of objects
     * @param defineClass class to identify objects in Define
     * @param destination where to copy to
     * @throws OperationFailedException
     */
    public <S extends AnchorBean<S>> void copyWithoutInit(
            Class<?> defineClass, NamedProviderStore<S> destination)
            throws OperationFailedException {
        StoreAdderHelper.addPreserveName(define, defineClass, destination, nameUnchangedBridge());
    }

    /**
     * Copies objects of a particular class from the define AND initializes
     *
     * @param <S> type of objects
     * @param defineClass class to identify objects in Define
     * @param destination where to copy to
     * @throws OperationFailedException
     */
    public <S extends InitializableBean<S, V>> void copyInit(
            Class<?> defineClass, NamedProviderStore<S> destination)
            throws OperationFailedException {

        // Initializes and returns the input
        CheckedFunction<S, S, OperationFailedException> bridge =
                new InitBridge<>(propertyInitializer, logger, nameUnchangedBridge());

        StoreAdderHelper.addPreserveName(define, defineClass, destination, bridge);
    }

    /**
     * Copies objects of a particular class from the define AND initializes as a provider
     *
     * <p>Specifically, each object will be lazily initialized once when first retrieved from the
     * store.
     *
     * @param <S> type of provider-objects
     * @param <T> type of objects created by the provider
     * @param defineClass class to identify objects in Define
     * @param destination where to copy to
     * @return the provider-bridge created for the initialization
     * @throws OperationFailedException
     */
    public <S extends InitializableBean<?, V> & Provider<T>, T>
            CheckedFunction<S, T, OperationFailedException> copyProvider(
                    Class<?> defineClass, NamedProviderStore<T> destination)
                    throws OperationFailedException {

        InitBridge<S, T, V> bridge =
                new InitBridge<>(
                        propertyInitializer,
                        logger,
                        s -> s.create() // NOSONAR Initializes and then gets what's provided
                        );

        StoreAdderHelper.addPreserveName(define, defineClass, destination, bridge);

        return bridge;
    }

    private static <S, E extends Exception> CheckedFunction<S, S, E> nameUnchangedBridge() {
        return name -> name;
    }
}
