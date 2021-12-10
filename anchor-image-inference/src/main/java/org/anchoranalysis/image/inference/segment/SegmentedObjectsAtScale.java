package org.anchoranalysis.image.inference.segment;

import java.util.List;
import java.util.function.Function;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.core.functional.FunctionalList;
import org.anchoranalysis.image.core.object.properties.ObjectCollectionWithProperties;
import org.anchoranalysis.image.core.stack.DisplayStack;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.voxel.object.ObjectCollection;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.spatial.box.Extent;

/**
 * Exposes a particular set of segmented-objects at a particular scale.
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor
@Accessors(fluent = true)
public class SegmentedObjectsAtScale {

    /** START: REQUIRED ARGUMENTS. */
    /**
     * A list containing {@link MultiScaleObject} from which we derive other representations at a
     * specific scale.
     */
    private final List<LabelledWithConfidence<MultiScaleObject>> source;

    /**
     * Extracts an {@link ObjectMask} at the desired specific scale from a {@link MultiScaleObject}
     * in {@code source}.
     */
    private final Function<MultiScaleObject, ObjectMask> extractObject;

    /** The background image associated with this particular scale. */
    @Getter private final Stack background;
    /** END: REQUIRED ARGUMENTS. */

    // START: memoized alternative representations of source at at specific scale.
    private ObjectCollection objects;

    private ObjectCollectionWithProperties objectsWithProperties;
    private List<WithConfidence<ObjectMask>> listWithoutLabels;
    private List<LabelledWithConfidence<ObjectMask>> listWithLabels;
    // END: memoized alternative representations.

    /** Memoized alternative representation of the background as a {@link DisplayStack}. */
    private DisplayStack backgroundDisplayStack;

    /**
     * Create a {@link List} of <i>all</i> contained objects, <i>including</i> confidence.
     *
     * @return a newly created {@link List} that reuses the existing {@link ObjectMask} stored in
     *     the structure.
     */
    public List<LabelledWithConfidence<ObjectMask>> listWithLabels() {
        if (listWithLabels == null) {
            this.listWithLabels =
                    FunctionalList.mapToList(
                            source,
                            multi ->
                                    new LabelledWithConfidence<>(
                                            extractObject.apply(multi.getElement()),
                                            multi.getConfidence(),
                                            multi.getLabel()));
        }
        return listWithLabels;
    }

    /**
     * Create a {@link List} of <i>all</i> contained objects, <i>including</i> confidence.
     *
     * @return a newly created {@link List} that reuses the existing {@link ObjectMask} stored in
     *     the structure.
     */
    public List<WithConfidence<ObjectMask>> listWithoutLabels() {
        if (listWithoutLabels == null) {
            this.listWithoutLabels =
                    FunctionalList.mapToList(
                            source,
                            multi ->
                                    new WithConfidence<>(
                                            extractObject.apply(multi.getElement()),
                                            multi.getConfidence()));
        }
        return listWithoutLabels;
    }

    /**
     * Create a {@link ObjectCollection} of <i>all</i> contained objects, <i>excluding</i>
     * confidence.
     *
     * @return {@link ObjectCollection} derived from {@code source} and cached.
     */
    public ObjectCollection objects() {
        if (objects == null) {
            objects = objectsFromList(listWithoutLabels());
        }
        return objects;
    }

    /**
     * Like {@link #objects()} but instead creates a {@link ObjectCollectionWithProperties}
     * containing empty properties.
     *
     * @return an {@link ObjectCollection} derived from {@link #objects()} and cached.
     */
    public ObjectCollectionWithProperties objectsWithProperties() {
        if (objectsWithProperties == null) {
            objectsWithProperties = new ObjectCollectionWithProperties(objects());
        }
        return objectsWithProperties;
    }

    /**
     * Like {@link #background()} but instead creates a {@link DisplayStack}.
     *
     * @return a {@link DisplayStack} derived from {@link #background()} and cached.
     */
    public DisplayStack backgroundDisplayStack() {
        if (backgroundDisplayStack == null) {
            try {
                backgroundDisplayStack = DisplayStack.create(background.extractUpToThreeChannels());
            } catch (CreateException e) {
                throw new AnchorImpossibleSituationException();
            }
        }
        return backgroundDisplayStack;
    }

    /**
     * The width and height and depth of the background image, associated with the objects.
     *
     * <p>i.e. the size of each of the three possible dimensions.
     *
     * <p>All objects should reside exclusively within this space.
     *
     * @return the extent.
     */
    public Extent extent() {
        return background.extent();
    }

    /**
     * A count of the total number of elements in each representation.
     *
     * @return the total number of elements, valid for all representations.
     */
    public int size() {
        return source.size();
    }

    /** Derives a {@link ObjectCollection} from a {@code List<WithConfidence<ObjectMask>>} */
    private static ObjectCollection objectsFromList(List<WithConfidence<ObjectMask>> list) {
        return new ObjectCollection(list.stream().map(WithConfidence::getElement));
    }
}