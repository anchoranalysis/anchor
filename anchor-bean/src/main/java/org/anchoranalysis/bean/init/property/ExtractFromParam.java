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

package org.anchoranalysis.bean.init.property;

import java.util.Optional;
import java.util.function.Function;

/**
 * Extracts on type of parameter from another
 *
 * @author Owen Feehan
 * @param <S> source-type
 * @param <T> destination-type
 */
public class ExtractFromParam<S, T> {

    private Class<?> classOfTarget;
    private Function<S, T> funcToExtract;
    private Optional<Class<?>> baseClassOfSource;

    /**
     * Constructor
     *
     * @param classOfTarget the class of the target type
     * @param funcToExtract a function to extract the target from the source
     */
    public ExtractFromParam(Class<?> classOfTarget, Function<S, T> funcToExtract) {
        this.classOfTarget = classOfTarget;
        this.funcToExtract = funcToExtract;
        this.baseClassOfSource = Optional.empty();
    }

    /**
     * Constructor
     *
     * @param classOfTarget the class of the target type
     * @param funcToExtract a function to extract the target from the source
     * @param baseClassOfSource a class the source-param must be assignable to. It will be checked
     *     by reflection.
     */
    public ExtractFromParam(
            Class<?> classOfTarget, Function<S, T> funcToExtract, Class<?> baseClassOfSource) {
        this.classOfTarget = classOfTarget;
        this.funcToExtract = funcToExtract;
        this.baseClassOfSource = Optional.of(baseClassOfSource);
    }

    public T extract(S params) {
        return funcToExtract.apply(params);
    }

    /** Are the source-parameters acceptable for extraction? * */
    public boolean acceptsAsSource(Class<?> classOfSource) {

        if (!baseClassOfSource.isPresent()) {
            return true;
        }

        return baseClassOfSource.get().isAssignableFrom(classOfSource.getClass());
    }

    public Class<?> getClassOfTarget() {
        return classOfTarget;
    }
}
