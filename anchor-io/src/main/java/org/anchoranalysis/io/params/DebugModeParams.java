/* (C)2020 */
package org.anchoranalysis.io.params;

/**
 * An alternative mode of option where behaviours change.
 *
 * <p><div>e.g.
 *
 * <ul>
 *   <li>A single-file is executed only.
 *   <li>Output paths may change.
 *   <li>Logging output may change.
 * </ul>
 *
 * </div>
 *
 * @author Owen Feehan
 */
public class DebugModeParams {

    /** If null, this is disabled * */
    private String contains = null;

    /**
     * Constructor
     *
     * @param contains an optional string used to filter inputs. If NULL, then disabled.
     */
    public DebugModeParams(String contains) {
        super();
        this.contains = contains;
    }

    /** @return contains, or an empty string if null. */
    public String containsOrEmpty() {
        if (contains != null) {
            return contains;
        } else {
            return "";
        }
    }
}
