/* (C)2020 */
package org.anchoranalysis.image.io.input.series;

import java.util.Optional;
import java.util.Set;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.Operation;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.name.store.NamedProviderStore;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.io.chnl.ChnlGetter;
import org.anchoranalysis.image.stack.NamedImgStackCollection;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.stack.TimeSequence;

public interface NamedChnlCollectionForSeries extends ChnlGetter {

    Optional<Channel> getChnlOrNull(String chnlName, int t, ProgressReporter progressReporter)
            throws GetOperationFailedException;

    Set<String> chnlNames();

    int sizeT(ProgressReporter progressReporter) throws RasterIOException;

    ImageDimensions dimensions() throws RasterIOException;

    void addAsSeparateChnls(
            NamedImgStackCollection stackCollection, int t, ProgressReporter progressReporter)
            throws OperationFailedException;

    void addAsSeparateChnls(NamedProviderStore<TimeSequence> stackCollection, int t)
            throws OperationFailedException;

    Operation<Stack, OperationFailedException> allChnlsAsStack(int t);
}
