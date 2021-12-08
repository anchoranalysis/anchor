package org.anchoranalysis.image.inference.segment;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import org.anchoranalysis.core.cache.CachedSupplier;
import org.anchoranalysis.core.exception.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.image.voxel.object.ObjectMask;

/**
 * An {@link ObjectMask} that exists at multiple scales.
 *
 * <p>Specifically the scales are:
 *
 * <ul>
 *   <li>the scale associated with the input-image.
 *   <li>the scale associated with the segmentation (usually a scaled down version of the above).
 * </ul>
 *
 * @author Owen Feehan
 */
public class MultiScaleObject {

    /** Supplies an {@link ObjectMask} at the respective-scale, caching each call. */
    private final DualScale<CachedSupplier<ObjectMask, AnchorImpossibleSituationException>> objects;

    /**
     * Creates with a separate supplier of an {@link ObjectMask} for each respective scale.
     *
     * <p>The suppliers will be cached internally, so are guaranteed to be called maximally one
     * time.
     *
     * @param inputScale supplies an {@link ObjectMask} for the input-scale.
     * @param modelScale supplies an {@link ObjectMask} for the model-scale.
     */
    public MultiScaleObject(Supplier<ObjectMask> inputScale, Supplier<ObjectMask> modelScale) {
        this(new DualScale<>(inputScale, modelScale));
    }

    /**
     * Creates with suppliers of an {@link ObjectMask} for each respective scale, wrapped in a
     * {@link DualScale}.
     *
     * <p>The suppliers will be cached internally, so are guaranteed to be called maximally one
     * time.
     *
     * @param objects supplies a {@link ObjectMask} for each respective scale.
     */
    private MultiScaleObject(DualScale<Supplier<ObjectMask>> objects) {
        this.objects = objects.map(CachedSupplier::cache);
    }

    /**
     * Creates a new {@link MultiScaleObject} by extracting a {@link ObjectMask} from a {@link
     * DualScale}.
     *
     * <p>This extract occurs lazily, only when needed.
     *
     * @param <T> the element-type of the {@link DualScale}.
     * @param dual the {@link DualScale} the object is extracted from.
     * @param extractObject a function applied to the respective scale in {@link DualScale} to
     *     extract an {@link ObjectMask}.
     * @return a newly created {@link MultiScaleObject}, as per above.
     */
    public static <T> MultiScaleObject extractFrom(
            DualScale<T> dual, Function<T, ObjectMask> extractObject) {
        return new MultiScaleObject(dual.map(element -> () -> extractObject.apply(element)));
    }

    /**
     * Creates a new {@link MultiScaleObject} by extracting a {@link ObjectMask} from two {@link
     * DualScale}s.
     *
     * <p>This extract occurs lazily, only when needed.
     *
     * @param <T> the element-type of {@code dual1}.
     * @param <S> the element-type of {@code dual2}.
     * @param dual1 the <i>first</i> {@link DualScale} the object is extracted from.
     * @param dual2 the <i>second</i> {@link DualScale} the object is extracted from.
     * @param extractObject a function applied to the respective scale in the two {@link DualScale}s
     *     respectively to extract an {@link ObjectMask}.
     * @return a newly created {@link MultiScaleObject}, as per above.
     */
    public static <T, S> MultiScaleObject extractFrom(
            DualScale<T> dual1, DualScale<S> dual2, BiFunction<T, S, ObjectMask> extractObject) {
        return new MultiScaleObject(
                dual1.combine(dual2, (first, second) -> () -> extractObject.apply(first, second)));
    }

    /**
     * Gets the {@link ObjectMask} at the scale associated with the input-image.
     *
     * @return the object.
     */
    public ObjectMask getInputScale() {
        return objects.atInputScale().get();
    }

    /**
     * Gets the {@link ObjectMask} at the scale associated with the segmentation.
     *
     * @return the object.
     */
    public ObjectMask getModelScale() {
        return objects.atModelScale().get();
    }
}
