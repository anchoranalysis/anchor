/* (C)2020 */
package org.anchoranalysis.feature.io.csv.name;

import java.util.Optional;

/**
 * A name that that uniquely represents something, and can be split into two parts (a directory
 * part, and a file part)
 *
 * @author Owen
 */
public interface MultiName extends Iterable<String>, Comparable<MultiName> {

    /** The part of the name which is exposed as a directory */
    Optional<String> directoryPart();

    /** The part of the name which is exposed as a file (inside the directory-part) */
    String filePart();

    String toString();

    boolean equals(Object obj);
}
