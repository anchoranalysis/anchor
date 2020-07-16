/* (C)2020 */
package org.anchoranalysis.anchor.overlay.collection;

import java.util.stream.IntStream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.anchor.overlay.object.OverlayObjectMask;
import org.anchoranalysis.core.idgetter.IDGetter;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.object.ObjectCollectionFactory;
import org.anchoranalysis.image.object.ObjectMask;

/**
 * Two-way factory.
 *
 * <p>Creation of {@link OverlayCollection} from marks Retrieval of marks back from {@link
 * OverlayCollection}s
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OverlayCollectionObjectFactory {

    public static OverlayCollection createWithoutColor(
            ObjectCollection objects, IDGetter<ObjectMask> idGetter) {
        return new OverlayCollection(
                IntStream.range(0, objects.size())
                        .mapToObj(
                                i -> {
                                    ObjectMask objectMask = objects.get(i);
                                    return new OverlayObjectMask(
                                            objectMask, idGetter.getID(objectMask, i));
                                }));
    }

    /** Creates objects from whatever Overlays are found in the collection * */
    public static ObjectCollection objectsFromOverlays(OverlayCollection overlays) {

        // Extract mask from any overlays that are object-mask overlays
        return ObjectCollectionFactory.filterAndMapFrom(
                overlays.asList(),
                overlay -> overlay instanceof OverlayObjectMask,
                overlay -> ((OverlayObjectMask) overlay).getObject().getMask());
    }
}
