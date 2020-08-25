/*-
 * #%L
 * anchor-overlay
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

package org.anchoranalysis.overlay.collection;

import java.util.stream.IntStream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.idgetter.IDGetter;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.object.ObjectCollectionFactory;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.overlay.object.OverlayObjectMask;

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

        // Extract objects from any overlays that are object-mask overlays
        return ObjectCollectionFactory.filterAndMapFrom(
                overlays.asList(),
                overlay -> overlay instanceof OverlayObjectMask,
                overlay -> ((OverlayObjectMask) overlay).getObject().withoutProperties());
    }
}
