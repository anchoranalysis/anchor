package org.anchoranalysis.io.manifest.sequencetype;

import org.anchoranalysis.core.index.container.BoundedRangeIncomplete;

/**
 * An incomplete range of discrete integers (like {@link BoundedRangeIncomplete} but with a string-representation for each index.
 * 
 * @author Owen Feehan
 *
 */
public interface IncompleteElementRange extends BoundedRangeIncomplete {
    
    /**
     * Returns the typed-index corresponding to the integer-index.
     * 
     * @param elementIndex which element (beginning at 0) to find an index for.
     * @return the index
     */
    public abstract String stringRepresentationForElement(int elementIndex);
}
