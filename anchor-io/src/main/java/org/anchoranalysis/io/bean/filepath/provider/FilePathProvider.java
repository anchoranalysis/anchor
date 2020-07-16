/* (C)2020 */
package org.anchoranalysis.io.bean.filepath.provider;

import java.nio.file.Path;
import org.anchoranalysis.bean.Provider;
import org.anchoranalysis.bean.shared.params.ParamsBean;
import org.anchoranalysis.core.error.CreateException;

public abstract class FilePathProvider extends ParamsBean<FilePathProvider>
        implements Provider<Path> {

    @Override
    public abstract Path create() throws CreateException;
}
