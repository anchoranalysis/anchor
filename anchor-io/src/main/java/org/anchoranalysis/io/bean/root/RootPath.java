/* (C)2020 */
package org.anchoranalysis.io.bean.root;

import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.io.filepath.prefixer.PathDifferenceFromBase;

/**
 * Defines a *root path* i.e. a directory in which files are read/written during analysis
 *
 * <p>Analysis scripts may select different root-paths depending on how they are executed (during
 * debugging, locally/server)
 *
 * <p>The name of a root must not be unique, but the combination of all fields should be unique i.e.
 * several roots can have the same name, but should vary in their other settings
 *
 * @author Owen Feehan
 */
@EqualsAndHashCode(callSuper = false)
public class RootPath extends AnchorBean<RootPath> {

    // START PROPERTIES
    @BeanField @Getter @Setter private String name;

    /*** A path on a filesystem to the directory, that defines the root */
    @BeanField @Getter @Setter private String path;

    /*** If TRUE this root is preferred, when executing a job in debugging mode */
    @BeanField @Getter @Setter private boolean debug = false;

    // END PROPERTIES

    /**
     * It splits the *root* portion of the path from the remainder
     *
     * @param path path to split
     * @return the split-path
     * @throws AnchorIOException if the path cannot be matched against the root
     */
    public SplitPath split(Path path) throws AnchorIOException {

        SplitPath out = new SplitPath();

        Path rootPath = asPath();

        // We get the difference of what is left, or else an exception is thrown if it cannot match
        PathDifferenceFromBase diff = PathDifferenceFromBase.differenceFrom(rootPath, path);
        out.setRoot(rootPath);
        out.setPath(diff.combined());
        return out;
    }

    public Path asPath() {
        return Paths.get(path);
    }
}
