/* (C)2020 */
package org.anchoranalysis.io.bean.provider.file.filter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.io.bean.input.InputManagerParams;
import org.anchoranalysis.io.bean.provider.file.FileProvider;
import org.anchoranalysis.io.error.FileProviderException;

public abstract class FilterFileProvider extends FileProvider {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private FileProvider fileProvider;
    // END BEAN PROPERTIES

    @Override
    public Collection<File> create(InputManagerParams params) throws FileProviderException {

        Collection<File> filesIn = fileProvider.create(params);

        List<File> filesOut = new ArrayList<>();

        for (File f : filesIn) {

            if (isFileAccepted(f, params.isDebugModeActivated())) {
                filesOut.add(f);
            }
        }

        return filesOut;
    }

    protected abstract boolean isFileAccepted(File file, boolean debugMode)
            throws FileProviderException;
}
