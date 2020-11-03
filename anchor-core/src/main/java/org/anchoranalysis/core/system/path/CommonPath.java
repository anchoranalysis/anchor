package org.anchoranalysis.core.system.path;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.anchoranalysis.core.functional.FunctionalList;
import com.owenfeehan.pathpatternfinder.commonpath.FindCommonPathElements;
import com.owenfeehan.pathpatternfinder.commonpath.PathElements;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Finds the common root directory of a set of paths.
 * 
 * <p>This is derived from an example on <a href="http://rosettacode.org/wiki/Find_common_directory_path#Java">Rosetta Code</a>.
 * @author Owen Feehan
 *
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class CommonPath {
    
    public static Optional<Path> commonPath(Collection<File> files) {
        List<Path> paths = FunctionalList.mapToList(files, File::toPath);
        return FindCommonPathElements.findForFilePaths( (Iterable<Path>) paths).map(PathElements::toPath);
    }
}
