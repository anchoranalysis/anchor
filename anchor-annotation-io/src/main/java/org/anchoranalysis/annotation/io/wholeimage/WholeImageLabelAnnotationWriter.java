/* (C)2020 */
package org.anchoranalysis.annotation.io.wholeimage;

import java.io.IOException;
import java.nio.file.Path;
import org.anchoranalysis.annotation.io.AnnotationWriter;
import org.anchoranalysis.annotation.io.WriterUtilities;
import org.anchoranalysis.annotation.wholeimage.WholeImageLabelAnnotation;
import org.anchoranalysis.io.generator.text.WriteStringToFile;

public class WholeImageLabelAnnotationWriter
        implements AnnotationWriter<WholeImageLabelAnnotation> {

    @Override
    public void write(WholeImageLabelAnnotation annotation, Path path) throws IOException {

        WriterUtilities.createNecessaryDirectories(path);

        WriteStringToFile.apply(annotation.getLabel(), path);
    }
}
