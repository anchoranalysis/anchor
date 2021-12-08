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
