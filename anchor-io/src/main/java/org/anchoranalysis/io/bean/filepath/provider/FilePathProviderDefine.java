/* (C)2020 */
package org.anchoranalysis.io.bean.filepath.provider;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.io.bean.input.InputManagerParams;
import org.anchoranalysis.io.bean.provider.file.FileProvider;
import org.anchoranalysis.io.error.FileProviderException;
import org.anchoranalysis.io.params.InputContextParams;

public class FilePathProviderDefine extends FilePathProvider {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private FileProvider fileProvider;
    // END BEAN PROPERTIES

    @Override
    public Path create() throws CreateException {

        Collection<File> files;
        try {
            files =
                    fileProvider.create(
                            new InputManagerParams(
                                    new InputContextParams(),
                                    ProgressReporterNull.get(),
                                    getLogger()));
        } catch (FileProviderException e) {
            throw new CreateException("Cannot find files", e);
        }

        if (files.size() != 1) {
            throw new CreateException(
                    String.format(
                            "fileProvider must return one file. Instead, it returned %d files.",
                            files.size()));
        }

        return files.iterator().next().toPath();
    }
}
