package org.anchoranalysis.image.object.combine;

import java.util.stream.Stream;
import org.anchoranalysis.image.extent.box.BoundingBox;
import lombok.AccessLevel;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class BoundingBoxMerger {

    /**
     * Merges all the bounding boxes in a stream.
     *
     * @param stream a stream whose bounding-boxes are to be merged
     * @return a bounding-box just large enough to include all the bounding-boxes of the objects
     */
    public static BoundingBox mergeBoundingBoxes(Stream<BoundingBox> stream) {
        return stream // NOSONAR
                .reduce( // NOSONAR
                        (boundingBox, other) -> boundingBox.union().with(other))
                .get();
    }
}
