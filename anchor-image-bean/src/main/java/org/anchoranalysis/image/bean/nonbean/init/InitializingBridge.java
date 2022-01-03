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
import org.anchoranalysis.bean.initializable.InitializableBean;
import org.anchoranalysis.bean.initializable.parameters.BeanInitialization;
import org.anchoranalysis.bean.initializable.property.BeanInitializer;
import org.anchoranalysis.bean.xml.exception.ProvisionFailedException;
import org.anchoranalysis.core.exception.InitializeException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.functional.checked.CheckedFunction;
import org.anchoranalysis.core.log.Logger;

/**
 * A bridge that performs initialization of objects.
 *
 * @author Owen Feehan
 * @param <S> source (bean) type
 * @param <T> destination type
 * @param <V> initialization-parameters type
 */
@AllArgsConstructor
class InitializingBridge<S extends InitializableBean<?, V>, T, V extends BeanInitialization>
        implements CheckedFunction<S, T, OperationFailedException> {

    /** Used to initialize properties in the source-bean. */
    private final BeanInitializer<?> initializer;

    /** The logger passed to the beans that are initialized. */
    private final Logger logger;

    /** Maps the source-bean to a destination-bean. */
    private final CheckedFunction<S, T, ProvisionFailedException> beanBridge;

    @Override
    public T apply(S sourceObject) throws OperationFailedException {
        assert logger != null;
        try {
            // Initialize
            sourceObject.initRecursiveWithInitializer(initializer, logger);

            // Bridge the source to the destination
            return beanBridge.apply(sourceObject);
        } catch (InitializeException | ProvisionFailedException e) {
            throw new OperationFailedException(e);
        }
    }
}
