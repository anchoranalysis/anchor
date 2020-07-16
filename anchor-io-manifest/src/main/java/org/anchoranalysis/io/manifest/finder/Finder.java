/* (C)2020 */
package org.anchoranalysis.io.manifest.finder;

import org.anchoranalysis.io.manifest.ManifestRecorder;

public interface Finder {

    // true, if there was a successful match, false otherwise
    boolean doFind(ManifestRecorder manifestRecorder);

    // doFind must be called first
    // Does the object exist?
    boolean exists();
}
