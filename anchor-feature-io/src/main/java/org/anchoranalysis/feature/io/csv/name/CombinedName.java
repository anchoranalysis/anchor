/* (C)2020 */
package org.anchoranalysis.feature.io.csv.name;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Optional;

/**
 * A name which is unique when a primaryName is combined with a secondaryName
 *
 * <p>In some cases (e.g. when grouping) the primaryName has relevance.
 *
 * @author Owen
 */
public class CombinedName implements MultiName {

    private static final String SEPARATOR = "/";

    /** The key used for aggregating */
    private String directoryPart;

    /** All the names not used in aggregating joined togther */
    private String filePart;

    /**
     * All names combined together in a single string with a forward slash as seperator.
     *
     * <p>The primaryName is used for aggregating. The rest are not. primaryName/secondaryName
     */
    private String allTogether;

    public CombinedName(String directoryPart, String filePart) {
        this.directoryPart = directoryPart;
        this.filePart = filePart;
        this.allTogether = String.join(SEPARATOR, directoryPart, filePart);
    }

    @Override
    public int hashCode() {
        return allTogether.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CombinedName) {
            CombinedName objCast = (CombinedName) obj;
            return allTogether.equals(objCast.allTogether);
        } else {
            return false;
        }
    }

    @Override
    public Optional<String> directoryPart() {
        // The primary name is used for aggregation
        return Optional.of(directoryPart);
    }

    @Override
    public Iterator<String> iterator() {
        return Arrays.asList(directoryPart, filePart).iterator();
    }

    @Override
    public String filePart() {
        return filePart;
    }

    @Override
    public String toString() {
        return allTogether;
    }

    @Override
    public int compareTo(MultiName other) {

        if (other instanceof CombinedName) {

            CombinedName otherCast = (CombinedName) other;

            int cmp = directoryPart.compareTo(otherCast.directoryPart);

            if (cmp != 0) {
                return cmp;
            }

            return filePart.compareTo(otherCast.filePart);
        } else {
            return 1;
        }
    }
}
