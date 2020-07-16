/* (C)2020 */
package org.anchoranalysis.io.bean.provider.file.filter;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.io.bean.filepath.generator.FilePathGenerator;
import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.io.error.FileProviderException;

public class FilterForExistingFiles extends FilterFileProvider {

    // START BEAN PROPERTIES
    @BeanField
    private List<FilePathGenerator> listFilePathGenerator =
            new ArrayList<>(); // All files need to be present
    // END BEAN PROPERTIES

    @Override
    protected boolean isFileAccepted(File file, boolean debugMode) throws FileProviderException {

        try {
            for (FilePathGenerator fpg : listFilePathGenerator) {
                Path annotationPath = fpg.outFilePath(file.toPath(), debugMode);

                if (!annotationPath.toFile().exists()) {
                    return false;
                }
            }
            return true;

        } catch (AnchorIOException e) {
            throw new FileProviderException(e);
        }
    }

    public List<FilePathGenerator> getListFilePathGenerator() {
        return listFilePathGenerator;
    }

    public void setListFilePathGenerator(List<FilePathGenerator> listFilePathGenerator) {
        this.listFilePathGenerator = listFilePathGenerator;
    }
}
