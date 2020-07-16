/* (C)2020 */
package org.anchoranalysis.io.bean.provider.file;

import java.io.File;
import java.util.Collection;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.io.bean.input.InputManagerParams;
import org.anchoranalysis.io.error.FileProviderException;

public abstract class FileProvider extends AnchorBean<FileProvider> {

    public abstract Collection<File> create(InputManagerParams params) throws FileProviderException;
}
