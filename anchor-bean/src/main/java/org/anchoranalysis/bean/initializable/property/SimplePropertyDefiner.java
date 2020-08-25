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
import org.anchoranalysis.bean.initializable.params.ParamsInitializer;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.log.Logger;

/**
 * @author Owen Feehan
 * @param <P> param type
 */
@RequiredArgsConstructor
public class SimplePropertyDefiner<P> implements PropertyDefiner {

    private final Class<?> paramTypeMatch;

    @Override
    public boolean accepts(Class<?> paramType) {
        return paramTypeMatch.isAssignableFrom(paramType);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void doInitFor(Object propertyValue, Object param, Object parent, Logger logger)
            throws InitException {

        if (!(propertyValue instanceof ParamsInitializer)) {
            throw new InitException(
                    String.format(
                            "propertyValue is not an instance of %s, as is required for this property-definer",
                            ParamsInitializer.class.getSimpleName()));
        }

        if (!paramTypeMatch.isAssignableFrom(param.getClass())) {
            throw new InitException(
                    String.format(
                            "param is not the same class or a subclass of %s, as is required for this property-definer",
                            paramTypeMatch.getSimpleName()));
        }

        ParamsInitializer<P> propertyValueCast = (ParamsInitializer<P>) propertyValue;
        propertyValueCast.init((P) param, logger);
    }

    @Override
    public String toString() {
        return String.format("simpleProp=%s", paramTypeMatch);
    }

    @Override
    public String describeAcceptedClasses() {
        return paramTypeMatch.getSimpleName();
    }
}
