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

import org.anchoranalysis.core.exception.InitializeException;
import org.anchoranalysis.core.log.Logger;

/**
 * Assigns a parameter to a bean, if the bean accepts it.
 *
 * <p>It is called by {@link BeanInitializer} to assign the parameter to the properties of the bean
 * or its children.
 *
 * @author Owen Feehan
 */
public interface InitializationParameterAssigner {

    /**
     * Whether a particular type of initialization parameters is accepted?
     *
     * @param paramType the class of initialization parameters to be checked, whether it is accepted
     *     or not.
     * @return true iff {@code paramType} is an acceptable type for initialization parameters.
     */
    boolean accepts(Class<?> paramType);

    /**
     * Assigns the parameter to the bean.
     *
     * @param bean the bean
     * @param parameter the parameter to assign
     * @param parent any parent bean of {@code bean}
     * @param logger the logger
     * @throws InitializeException if the initialization cannot successfully complete.
     */
    void assignParameterToProperties(Object bean, Object parameter, Object parent, Logger logger)
            throws InitializeException;

    /**
     * A string describing which classes of initialization-parameters are accepted or not.
     *
     * @return a human readable string.
     */
    String describeAcceptedClasses();
}
