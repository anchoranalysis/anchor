/*-
 * #%L
 * anchor-bean
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

package org.anchoranalysis.bean.initializable.property;

import lombok.RequiredArgsConstructor;
import org.anchoranalysis.bean.initializable.InitializableBean;
import org.anchoranalysis.bean.initializable.parameters.BeanInitialization;
import org.anchoranalysis.core.exception.InitializeException;
import org.anchoranalysis.core.log.Logger;

/**
 * Allows assignment of parameters if they are are of type {@code paramTypeMatch} or subclass of it.
 *
 * @author Owen Feehan
 * @param <P> param type
 */
@RequiredArgsConstructor
public class AssignerMatchClass<P extends BeanInitialization>
        implements InitializationParameterAssigner {

    private final Class<?> paramTypeMatch;

    @Override
    public boolean accepts(Class<?> paramType) {
        return paramTypeMatch.isAssignableFrom(paramType);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void assignInitializationToProperties(
            Object bean, Object initialization, Object parent, Logger logger)
            throws InitializeException {

        if (!(bean instanceof InitializableBean)) {
            throw new InitializeException(
                    String.format(
                            "propertyValue is not an instance of %s, as is required for this property-definer",
                            InitializableBean.class.getSimpleName()));
        }

        if (!paramTypeMatch.isAssignableFrom(initialization.getClass())) {
            throw new InitializeException(
                    String.format(
                            "param is not the same class or a subclass of %s, as is required for this property-definer",
                            paramTypeMatch.getSimpleName()));
        }

        InitializableBean<?, P> propertyValueCast = (InitializableBean<?, P>) bean;
        propertyValueCast.initialize((P) initialization, logger);
    }

    @Override
    public String toString() {
        return String.format("assignableFrom=%s", paramTypeMatch);
    }

    @Override
    public String describeAcceptedClasses() {
        return paramTypeMatch.getSimpleName();
    }
}
