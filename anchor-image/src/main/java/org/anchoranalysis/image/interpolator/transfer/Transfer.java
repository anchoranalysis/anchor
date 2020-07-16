/* (C)2020 */
package org.anchoranalysis.image.interpolator.transfer;

import org.anchoranalysis.image.interpolator.Interpolator;

public interface Transfer {

    void assignSlice(int z);

    void transferCopyTo(int z);

    void transferTo(int z, Interpolator interpolator);
}
