/* (C)2020 */
package org.anchoranalysis.io.input;

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Supplier;
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.core.cache.CachedOperation;
import org.anchoranalysis.io.bean.filepath.generator.FilePathGenerator;
import org.anchoranalysis.io.error.AnchorIOException;

public class OperationOutFilePath extends CachedOperation<Path, AnchorIOException> {

    private NamedBean<FilePathGenerator> ni;
    private Supplier<Optional<Path>> path;
    private boolean debugMode;

    public OperationOutFilePath(
            NamedBean<FilePathGenerator> ni, Supplier<Optional<Path>> path, boolean debugMode) {
        super();
        this.ni = ni;
        this.path = path;
        this.debugMode = debugMode;
    }

    @Override
    protected Path execute() throws AnchorIOException {
        return ni.getValue()
                .outFilePath(
                        path.get()
                                .orElseThrow(
                                        () ->
                                                new AnchorIOException(
                                                        "A binding-path must be associated with the input for this operation")),
                        debugMode)
                .toAbsolutePath()
                .normalize();
    }
}
