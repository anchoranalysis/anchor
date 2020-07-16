/* (C)2020 */
package org.anchoranalysis.io.bean.descriptivename;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.io.input.descriptivename.DescriptiveFile;

/**
 * Calculates the descriptive-name independently for each file
 *
 * @author Owen Feehan
 */
public abstract class DescriptiveNameFromFileIndependent extends DescriptiveNameFromFile {

    @Override
    public List<DescriptiveFile> descriptiveNamesFor(
            Collection<File> files, String elseName, Logger logger) {

        List<DescriptiveFile> out = new ArrayList<>();

        int i = 0;
        for (File f : files) {
            String descriptiveName = createDescriptiveNameOrElse(f, i++, elseName, logger);
            out.add(new DescriptiveFile(f, descriptiveName));
        }

        return out;
    }

    protected abstract String createDescriptiveName(File file, int index) throws CreateException;

    private String createDescriptiveNameOrElse(
            File file, int index, String elseName, Logger logger) {
        try {
            return createDescriptiveName(file, index);
        } catch (CreateException e) {

            String elseNameWithIndex = String.format("%s04%d", elseName, index);

            logger.errorReporter()
                    .recordError(
                            DescriptiveNameFromFileIndependent.class,
                            String.format(
                                    "Cannot create a descriptive-name for file %s and index %d. Using '%s' instead.",
                                    file.getPath(), index, elseNameWithIndex));
            return elseNameWithIndex;
        }
    }
}
