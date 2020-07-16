/* (C)2020 */
package org.anchoranalysis.annotation.io.wholeimage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.annotation.io.AnnotationReader;
import org.anchoranalysis.annotation.wholeimage.WholeImageLabelAnnotation;
import org.anchoranalysis.io.error.AnchorIOException;

public class WholeImageLabelAnnotationReader
        implements AnnotationReader<WholeImageLabelAnnotation> {

    @Override
    public Optional<WholeImageLabelAnnotation> read(Path path) throws AnchorIOException {

        try (FileReader fileReader = new FileReader(path.toFile())) {
            return Optional.of(readFromFile(fileReader));
        } catch (IOException e) {
            throw new AnchorIOException("A failure opening the annotation file for reading");
        }
    }

    private static WholeImageLabelAnnotation readFromFile(FileReader fileReader)
            throws AnchorIOException {
        try (BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            String label = bufferedReader.readLine();

            if (hasAnotherLine(bufferedReader)) {
                // Something went wrong as we are not at the End Of File
                throw new AnchorIOException(
                        "We expect the a whole-image label to be in a text file with a single line only");
            }

            return new WholeImageLabelAnnotation(label);
        } catch (IOException e) {
            throw new AnchorIOException(
                    "A failure occurred reading a line from the annotation file");
        }
    }

    private static boolean hasAnotherLine(BufferedReader bufferedReader) throws IOException {
        String nextLine = bufferedReader.readLine();
        return nextLine != null;
    }
}
