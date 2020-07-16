/* (C)2020 */
package org.anchoranalysis.io.bean.provider.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.progress.ProgressReporterMultiple;
import org.anchoranalysis.io.bean.input.InputManagerParams;
import org.anchoranalysis.io.error.FileProviderException;

/** Lists all directories to a certain depth */
public class DirectoryDepth extends FileProviderWithDirectoryString {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private int exactDepth = 0;
    // END BEAN PROPERTIES

    @Override
    public Collection<File> matchingFilesForDirectory(Path directory, InputManagerParams params)
            throws FileProviderException {

        String[] filesDir = directory.toFile().list();

        if (filesDir == null) {
            throw new FileProviderException(
                    String.format("Path %s is not valid. Cannot enumerate directory.", directory));
        }

        int numFiles = filesDir.length;

        try (ProgressReporterMultiple prm =
                new ProgressReporterMultiple(params.getProgressReporter(), numFiles)) {
            WalkToDepth walkTo = new WalkToDepth(exactDepth, prm);
            return walkTo.findDirs(directory.toFile());
        } catch (IOException e) {
            throw new FileProviderException(e);
        }
    }
}
