/* (C)2020 */
package org.anchoranalysis.image.io.input;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.image.io.RasterIOException;

/**
 * One part of a NamedChnlsInput that can be combined with others
 *
 * @author Owen Feehan
 */
public abstract class NamedChnlsInputPart extends NamedChnlsInput {

    public abstract boolean hasChnl(String chnlName) throws RasterIOException;

    public abstract List<Path> pathForBindingForAllChannels() throws GetOperationFailedException;

    public abstract File getFile();
}
