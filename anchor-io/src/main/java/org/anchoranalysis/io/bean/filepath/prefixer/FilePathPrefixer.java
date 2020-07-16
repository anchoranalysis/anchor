/* (C)2020 */
package org.anchoranalysis.io.bean.filepath.prefixer;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.error.BeanStrangeException;
import org.anchoranalysis.io.error.FilePathPrefixerException;
import org.anchoranalysis.io.filepath.prefixer.FilePathPrefix;
import org.anchoranalysis.io.filepath.prefixer.FilePathPrefixerParams;

public abstract class FilePathPrefixer extends AnchorBean<FilePathPrefixer> {

    /**
     * Provides a prefix which can be prepended to all output files. The prefix should be an
     * absolute path.
     *
     * @param input an input to derive a prefix from
     * @param experimentIdentifier an identifier for the experiment
     * @param outputContext output-context
     * @return a prefixer
     * @throws FilePathPrefixerException
     */
    public abstract FilePathPrefix outFilePrefix(
            PathWithDescription input, String experimentIdentifier, FilePathPrefixerParams context)
            throws FilePathPrefixerException;

    /**
     * Provides a prefix that becomes the root-folder. The prefix should be an absolute path.
     *
     * @param experimentIdentifier an identifier for the experiment
     * @param outputContext output-context
     * @return a prefixer
     * @throws FilePathPrefixerException
     */
    public abstract FilePathPrefix rootFolderPrefix(
            String experimentIdentifier, FilePathPrefixerParams context)
            throws FilePathPrefixerException;

    /**
     * Converts a relative-path to an absolute-path (relative to the file-path associated with this
     * current bean)
     *
     * <p>If there is no file-path associated with the current bean, then we throw an error if it is
     * a relative path, or otherwise it remains unchanged
     *
     * <p>If the pathToResolve is already absolute, then we return it as-is
     *
     * @param pathToResolve input-path that is relative
     * @return the converted path (relative to the localizedPath of the current file)
     * @throws IOException if it cannot be converted to a real-path
     */
    protected Path resolvePath(Path pathToResolve) {

        if (pathToResolve.isAbsolute()) {
            // It's okay we it's absolute path
            return pathToResolve;
        }

        // We have a relative path
        if (getLocalPath() == null) {
            throw new BeanStrangeException(
                    String.format(
                            "Cannot resolve relative-path: %s as there is no localPath for this bean",
                            pathToResolve));
        }

        assert !pathToResolve.isAbsolute();
        assert getLocalPath().isAbsolute();
        Path parent = getLocalPath().getParent();

        return parent.resolve(pathToResolve).normalize();
    }

    /** An absolute path to the prefix */
    protected Path resolvePath(String maybeRelativePath) {
        return resolvePath(Paths.get(maybeRelativePath));
    }
}
