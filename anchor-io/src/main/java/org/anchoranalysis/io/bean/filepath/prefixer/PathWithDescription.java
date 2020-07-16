/* (C)2020 */
package org.anchoranalysis.io.bean.filepath.prefixer;

import java.nio.file.Path;

/**
 * A path and associated descriptive name
 *
 * @author Owen Feehan
 */
public class PathWithDescription {

    private Path path;
    private String descriptiveName;

    public PathWithDescription(Path path, String descriptiveName) {
        super();
        this.path = path;
        this.descriptiveName = descriptiveName;
    }

    public Path getPath() {
        return path;
    }

    public String getDescriptiveName() {
        return descriptiveName;
    }
}
