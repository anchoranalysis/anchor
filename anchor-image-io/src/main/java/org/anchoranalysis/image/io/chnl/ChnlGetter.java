/* (C)2020 */
package org.anchoranalysis.image.io.chnl;

import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.image.channel.Channel;

public interface ChnlGetter {

    boolean hasChnl(String chnlName);

    Channel getChnl(String chnlName, int t, ProgressReporter progressReporter)
            throws GetOperationFailedException;
}
