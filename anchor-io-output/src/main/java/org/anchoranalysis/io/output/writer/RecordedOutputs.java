package org.anchoranalysis.io.output.writer;

import java.util.HashSet;
import java.util.Set;

/**
 * Outputs recorded from {@link RecordOutputNames}.
 * 
 * <p>Three separate sets are recorded:
 * <ul>
 * <li>outputs that are written as they were allowed.
 * <li>outputs that not written as they are <b>not</b> allowed.
 * </ul>
 *  
 * @author Owen Feehan
 *
 */
public class RecordedOutputs {

    private Set<String> namesAllowed = new HashSet<>();
    private Set<String> namesNotAllowed = new HashSet<>();
    
    public void add(String outputName, boolean allowed) {
        if (allowed) {
            namesAllowed.add(outputName);
        } else {
            namesNotAllowed.add(outputName);
        }
    }
}
