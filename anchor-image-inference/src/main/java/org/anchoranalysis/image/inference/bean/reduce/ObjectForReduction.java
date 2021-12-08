package org.anchoranalysis.image.inference.bean.reduce;

import lombok.AllArgsConstructor;
import lombok.Value;
import org.anchoranalysis.image.inference.segment.LabelledWithConfidence;
import org.anchoranalysis.image.voxel.object.ObjectMask;

/** An object that can be reduced. */
@AllArgsConstructor
@Value
public class ObjectForReduction implements Comparable<ObjectForReduction> {

    /**
     * A special index value to indicate that an object has been newly-added.
     *
     * <p>Typically indices are non-negative, reflecting the position their position in the input
     * list (as passed for reduction).
     */
    public static final int NEWLY_ADDED = -1;

    /** The underlying labelled {@link ObjectMask} for input to a reduction algorithm. */
    private LabelledWithConfidence<ObjectMask> labelled;

    /**
     * The index of the element in the original list for reduction, or {@link NEWLY_ADDED} if newly
     * added.
     */
    private int index;

    /**
     * Creates with a {@link ObjectMask} that is considered newly-added.
     *
     * @param labelled a labelled newly-added object.
     */
    public ObjectForReduction(LabelledWithConfidence<ObjectMask> labelled) {
        this.labelled = labelled;
        this.index = NEWLY_ADDED;
    }

    /**
     * The underlying {@link ObjectMask} with whom a confidence and label is associated.
     *
     * @return the element to be passed as an input to a reduction algorithm.
     */
    public ObjectMask getElement() {
        return labelled.getElement();
    }

    /**
     * The label associated with the element.
     *
     * @return the label.
     */
    public String getLabel() {
        return labelled.getLabel();
    }

    /**
     * The associated confidence.
     *
     * @return the confidence.
     */
    public double getConfidence() {
        return labelled.getConfidence();
    }

    @Override
    public int compareTo(ObjectForReduction other) {
        return labelled.compareTo(other.labelled);
    }
}
