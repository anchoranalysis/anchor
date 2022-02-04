package org.anchoranalysis.io.input.bean.path;

import java.nio.file.Path;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.BeanInstanceMap;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.exception.BeanMisconfiguredException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.index.range.IndexRangeNegative;
import org.anchoranalysis.io.input.path.DerivePathException;
import org.anchoranalysis.io.input.path.ExtractPathElementRange;

/**
 * Constructs a {@link Path} from a sub-range of the name-elements of the {@link Path}.
 *
 * <p>The name-elements are a split of the {@link Path} by the directory-separator. See the {@link
 * Path} Javadoc.
 *
 * <p>All indices begin at 0 (for the first element), and can also accept negative-indices which
 * count backwards from the end.
 *
 * <p>e.g. -1 is the last element; -2 is the second-last element.
 *
 * @author Owen Feehan
 */
public class NameElementRange extends DerivePath {

    // START BEAN PROPERTIES
    /**
     * The index of the first element (inclusive) for the range.
     *
     * <p>Zero-indexed. It can be negative, in which it counts backwards from the end. See class
     * description.
     */
    @BeanField @Getter @Setter private int indexStart = 0;

    /**
     * The index of the last element (inclusive) for the range.
     *
     * <p>Zero-indexed. It can be negative, in which it counts backwards from the end. See class
     * description.
     */
    @BeanField @Getter @Setter private int indexEnd = -2;
    // END BEAN PROPERTIES

    private IndexRangeNegative indexRange;

    @Override
    public void checkMisconfigured(BeanInstanceMap defaultInstances)
            throws BeanMisconfiguredException {
        super.checkMisconfigured(defaultInstances);
        try {
            this.indexRange = new IndexRangeNegative(indexStart, indexEnd);
        } catch (OperationFailedException e) {
            throw new BeanMisconfiguredException("The indices are not correctly ordered", e);
        }
    }

    @Override
    public Path deriveFrom(Path source, boolean debugMode) throws DerivePathException {
        return ExtractPathElementRange.extract(source, indexRange);
    }
}
