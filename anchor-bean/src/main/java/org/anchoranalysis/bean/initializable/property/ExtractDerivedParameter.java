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

import java.util.Optional;
import java.util.function.Function;
import lombok.Getter;

/**
 * Extracts one type of parameter from another, if possible.
 *
 * @author Owen Feehan
 * @param <S> type of parameter to extract from (source)
 * @param <T> type of parameter that is maybe derived (target)
 */
public class ExtractDerivedParameter<S, T> {

    /** The class of the target type {@code T}. */
    @Getter private Class<?> targetClass;

    private Function<S, T> extractionFunction;
    private Optional<Class<?>> sourceBaseClass;

    /**
     * Creates with only a {@code targetClass}.
     *
     * @param targetClass the class of the target type.
     * @param extractionFunction a function to extract the target from the source.
     */
    public ExtractDerivedParameter(Class<?> targetClass, Function<S, T> extractionFunction) {
        this.targetClass = targetClass;
        this.extractionFunction = extractionFunction;
        this.sourceBaseClass = Optional.empty();
    }

    /**
     * Creates with a {@code classOfTarget} and a {@code baseClassOfSource}.
     *
     * @param targetClass the class of the target type.
     * @param extractionFunction a function to extract the target from the source.
     * @param sourceBaseClass a class the source-param must be assignable to. It will be checked by
     *     reflection.
     */
    public ExtractDerivedParameter(
            Class<?> targetClass, Function<S, T> extractionFunction, Class<?> sourceBaseClass) {
        this.targetClass = targetClass;
        this.extractionFunction = extractionFunction;
        this.sourceBaseClass = Optional.of(sourceBaseClass);
    }

    /**
     * Extracts a derived parameter if possible from {@code parameter}.
     *
     * @param parameter the parameter to maybe derive an extracted parameter from.
     * @return the extracted parameter or {@link Optional#empty} if it is not possible.
     */
    public Optional<T> extractIfPossible(S parameter) {
        if (accepts(parameter.getClass())) {
            return Optional.of(extract(parameter));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Are a particular class of parameters suitable for extraction?
     *
     * @param paramClass the class of the parameter type.
     * @return true if {@code paramClass} is suitable to extract a derived parameter from.
     */
    private boolean accepts(Class<?> paramClass) {
        if (sourceBaseClass.isPresent()) {
            return sourceBaseClass.get().isAssignableFrom(paramClass.getClass());
        } else {
            return true;
        }
    }

    /**
     * Extracts the derived parameter.
     *
     * <p>This should only be called after checking with {@link #accepts(Class)} that {@code
     * parameter} has an acceptable class type.
     *
     * @param parameter to extract from (the source parameter).
     * @return the derived parameter.
     */
    private T extract(S parameter) {
        return extractionFunction.apply(parameter);
    }
}
