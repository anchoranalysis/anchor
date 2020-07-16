/* (C)2020 */
package org.anchoranalysis.io.output.bean;

import java.util.Optional;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.io.bean.filepath.prefixer.PathWithDescription;
import org.anchoranalysis.io.error.FilePathPrefixerException;
import org.anchoranalysis.io.filepath.prefixer.FilePathPrefix;
import org.anchoranalysis.io.filepath.prefixer.FilePathPrefixerParams;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.output.bean.allowed.OutputAllowed;
import org.anchoranalysis.io.output.bound.BindFailedException;
import org.anchoranalysis.io.output.bound.BoundOutputManager;

/*
 *  Responsible for making decisions on where output goes and what form it takes
 *
 *  Is always associated with a particular infilePath through init()
 *   which may be changed as the program is executed.
 */
public abstract class OutputManager extends AnchorBean<OutputManager> {

    public abstract boolean isOutputAllowed(String outputName);

    /**
     * A second-level of OutputAllowed for a particular key, or NULL if none is defined for this key
     */
    public abstract OutputAllowed outputAllowedSecondLevel(String key);

    public abstract BoundOutputManager bindRootFolder(
            String expIdentifier,
            ManifestRecorder writeOperationRecorder,
            FilePathPrefixerParams context)
            throws BindFailedException;

    public abstract FilePathPrefix prefixForFile(
            PathWithDescription input,
            String expIdentifier,
            Optional<ManifestRecorder> manifestRecorder,
            Optional<ManifestRecorder> experimentalManifestRecorder,
            FilePathPrefixerParams context)
            throws FilePathPrefixerException;
}
