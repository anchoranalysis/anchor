package org.anchoranalysis.io.input.path.matcher;

import java.nio.file.Path;
import java.util.function.Predicate;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * A {@code Predicate<Path} for both a file and a directory.
 *  
 * @author Owen Feehan
 *
 */
@AllArgsConstructor @Value
public class DualPathPredicates {
    
    /** Only accepts files where the predicate returns true */
    private Predicate<Path> file;

    /** Only accepts any containing directories where the predicate returns true */
    private Predicate<Path> directory;
}
