package org.anchoranalysis.io.input.path;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.index.range.IndexRangeNegative;

/**
 * Extracts a range of elements from a {@link Path}.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExtractPathElementRange {

    /**
     * Extracts a sub-path from a {@link Path} by only retaining a range of elements.
     *
     * <p>The name-elements are a split of the {@link Path} by the directory-separator. See the
     * {@link Path} Javadoc.
     *
     * <p>All indices begin at 0 (for the first element), and can also accept negative-indices which
     * count backwards from the end.
     *
     * <p>e.g. -1 is the last element; -2 is the second-last element.
     *
     * @param path the path to find a sub-path for.
     * @param range which elements to include or not.
     * @return a newly created {@link Path} containing only the elements of {@code path} in the
     *     sub-range.
     * @throws DerivePathException if the range contains invalid indices.
     */
    public static Path extract(Path path, IndexRangeNegative range) throws DerivePathException {
        try {
            List<Path> elementsSubrange = range.extract(path.getNameCount(), path::getName);
            return getFile(elementsSubrange).toPath();
        } catch (OperationFailedException e) {
            throw new DerivePathException(
                    "Cannot extract a subrange of elements from the path: " + path, e);
        }
    }

    /** Construct a file from a collection of name elements. */
    private static File getFile(Collection<Path> elements) {
        File file = null;
        for (final Path name : elements) {
            if (file == null) {
                file = new File(name.toString());
            } else {
                file = new File(file, name.toString());
            }
        }
        return file;
    }
}
