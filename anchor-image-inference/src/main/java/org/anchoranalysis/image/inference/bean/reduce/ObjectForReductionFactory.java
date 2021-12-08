package org.anchoranalysis.image.inference.bean.reduce;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.image.inference.segment.LabelledWithConfidence;
import org.anchoranalysis.image.voxel.object.ObjectMask;

/**
 * Utility functions for creating one or more {@link ObjectForReduction}s.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ObjectForReductionFactory {

    /**
     * Creates a list of {@link ObjectForReduction}s from a corresponding list of labelled {@link
     * ObjectMask}s.
     *
     * <p>The index in {@code elements} is associated with the corresponding {@link
     * ObjectForReduction}.
     *
     * @param list the list to populate from.
     * @return a newly-created list, derived from {@code list}.
     */
    public static List<ObjectForReduction> populateFromList(
            List<LabelledWithConfidence<ObjectMask>> list) {
        return IntStream.range(0, list.size())
                .mapToObj(index -> new ObjectForReduction(list.get(index), index))
                .collect(Collectors.toList());
    }
}
