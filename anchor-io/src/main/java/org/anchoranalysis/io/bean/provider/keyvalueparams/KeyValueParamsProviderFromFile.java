/* (C)2020 */
package org.anchoranalysis.io.bean.provider.keyvalueparams;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.shared.params.keyvalue.KeyValueParamsProvider;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.io.bean.input.InputManagerParams;
import org.anchoranalysis.io.bean.provider.file.FileProvider;
import org.anchoranalysis.io.error.FileProviderException;
import org.anchoranalysis.io.params.InputContextParams;

public class KeyValueParamsProviderFromFile extends KeyValueParamsProvider {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private FileProvider fileProvider;
    // END BEAN PROPERTIES

    @Override
    public KeyValueParams create() throws CreateException {
        try {
            Collection<File> files =
                    fileProvider.create(
                            new InputManagerParams(
                                    new InputContextParams(),
                                    ProgressReporterNull.get(),
                                    getLogger()));

            if (files.isEmpty()) {
                throw new CreateException("No files are provided");
            }

            if (files.size() > 1) {
                throw new CreateException("More than one file is provided");
            }

            Path filePath = files.iterator().next().toPath();
            return KeyValueParams.readFromFile(filePath);

        } catch (IOException e) {
            throw new CreateException(e);
        } catch (FileProviderException e) {
            throw new CreateException("Cannot find files", e);
        }
    }
}
