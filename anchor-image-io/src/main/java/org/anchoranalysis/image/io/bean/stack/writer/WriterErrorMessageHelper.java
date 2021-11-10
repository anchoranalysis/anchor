package org.anchoranalysis.image.io.bean.stack.writer;

import java.nio.file.Path;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.image.io.ImageIOException;

/**
 * Helps create similar error messages among {@link StackWriter}s.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WriterErrorMessageHelper {

    /**
     * Creates a {@link ImageIOException} with an error message that image-writing failed.
     *
     * @param <T> the writer in which the writing failed.
     * @param writerClass class corresponding to {@code <T>}.
     * @param path the path that the image was been written to, when failure occurred.
     * @param cause the cause of failure.
     * @return an exception that can be thrown describing the failure.
     */
    public static <T extends StackWriter> ImageIOException generalWriteException(
            Class<T> writerClass, Path path, Exception cause) {
        String message =
                String.format(
                        "An error occurred writing an image to %s using the %s",
                        path, writerClass.getSimpleName());
        return new ImageIOException(message, cause);
    }
}
