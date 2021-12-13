/*-
 * #%L
 * anchor-image-inference
 * %%
 * Copyright (C) 2010 - 2021 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.image.inference.segment;

import java.util.function.BiFunction;
import java.util.function.Function;
import lombok.AllArgsConstructor;

/**
 * Stores two versions of the same object, representing two different scales.
 *
 * <p>This is a <a href="https://en.wikipedia.org/wiki/Monad_(functional_programming)">monad</a>
 * that provides a framework for applying a chain of operations on both scales.
 *
 * @author Owen Feehan
 * @param <T> element-type to store.
 */
@AllArgsConstructor
public class DualScale<T> {

    /** The element scaled to match the size of the input-image. */
    private T inputScale;

    /** The element scaled to match the size of the input for model inference. */
    private T modelScale;

    /**
     * The element scaled to match the size of the input-image.
     *
     * @return the element, at the requested scale.
     */
    public T atInputScale() {
        return inputScale;
    }

    /**
     * The element scaled to match the size of the input for model inference.
     *
     * @return the element, at the requested scale.
     */
    public T atModelScale() {
        return modelScale;
    }

    /**
     * Create a derived {@link DualScale} by applying an <b>identical</b> mapping to each element.
     *
     * @param <S> the target element-type after the mapping.
     * @param mappingFunction maps an instance of type {@code T} to type {@code S}.
     * @return a newly created {@link DualScale} containing the mapped instances.
     */
    public <S> DualScale<S> map(Function<T, S> mappingFunction) {
        return new DualScale<>(
                mappingFunction.apply(inputScale), mappingFunction.apply(modelScale));
    }

    /**
     * Create a derived {@link DualScale} by combining the respective elements of this instance with
     * another.
     *
     * @param <S> the target element-type after combining.
     * @param <U> the element-type of {@code other}.
     * @param other the other {@link DualScale} to combine with.
     * @param combineFunction combines the respective elements from the two {@link DualScale}s to
     *     form an element in the derived instance.
     * @return a newly created {@link DualScale} containing the respectiuve outputs from {@code
     *     combineFunction}.
     */
    public <S, U> DualScale<S> combine(DualScale<U> other, BiFunction<T, U, S> combineFunction) {
        return new DualScale<>(
                combineFunction.apply(inputScale, other.inputScale),
                combineFunction.apply(modelScale, other.modelScale));
    }

    /**
     * Apply a {@link BiFunction} to both elements.
     *
     * @param <R> the return type of the {@link BiFunction}.
     * @param function the function to apply, to the input-scale and model-scale elements,
     *     respectively.
     * @return the result of applying the {@code function} to the respective elements.
     */
    public <R> R apply(BiFunction<T, T, R> function) {
        return function.apply(inputScale, modelScale);
    }
}
