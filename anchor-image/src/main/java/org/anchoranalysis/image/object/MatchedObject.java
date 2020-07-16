/* (C)2020 */
package org.anchoranalysis.image.object;

import lombok.Value;

/**
 * An object with associated matches.
 *
 * @author Owen Feehan
 */
@Value
public final class MatchedObject {

    /** The source object (which is matched against others) */
    private final ObjectMask source;

    /** The matches associated with the source-object */
    private final ObjectCollection matches;

    public int numMatches() {
        return this.matches.size();
    }
}
