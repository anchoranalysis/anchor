package org.anchoranalysis.io.output.writer;

import java.util.Set;
import java.util.TreeSet;

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

    // TreeSet is used to ensure alphabetical ordering
    
    /**
     * Names of outputs that have been allowed and written.
     */
    private Set<String> namesAllowed = new TreeSet<>();
    
    /**
     * Names of outputs that were not allowed and therefore not written.
     */
    private Set<String> namesNotAllowed = new TreeSet<>();
    
    /**
     * Adds a new output-name to the set of recorded names.
     * 
     * @param outputName the output-name
     * @param allowed where the output was allowed or not
     */
    public void add(String outputName, boolean allowed) {
        if (allowed) {
            namesAllowed.add(outputName);
        } else {
            namesNotAllowed.add(outputName);
        }
    }
    
    /**
     * Number of output-names that were allowed.
     *  
     * @return the number of names
     */
    public int numberAllowed() {
        return namesAllowed.size();
    }

    /**
     * Number of output-names that were not allowed.
     *  
     * @return the number of names
     */
    public int numberNotAllowed() {
        return namesNotAllowed.size();
    }
    
    /**
     * If no output-names exist.
     * 
     * @return true iff no output-names have been recorded.
     */
    public boolean isEmpty() {
        return namesAllowed.isEmpty() && namesNotAllowed.isEmpty();
    }
    
    /**
     * A comma-separated string of all output-names that were <b>allowed</b>.
     * 
     * <p>The strings are separated by a comma and a space, and are alphabetically-ordered.
     * 
     * @return the output-names as a string
     */
    public String summarizeAllowed() {
        return summarizeNames(namesAllowed);
    }

    /**
     * A comma-separated string of all output-names that were <b>not allowed</b>.
     * 
     * <p>The strings are separated by a comma and a space, and are alphabetically-ordered.
     * 
     * @return the output-names as a string
     */
    public String summarizeNotAllowed() {
        return summarizeNames(namesNotAllowed);
    }
    
    private static String summarizeNames(Set<String> names) {
        return String.join(", ", names);
    }
}
