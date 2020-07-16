/* (C)2020 */
package org.anchoranalysis.io.bean.root;

import java.nio.file.Path;

public class SplitPath {

    private Path root;
    private Path remainder;

    SplitPath() {}

    SplitPath(Path remainder, Path root) {
        super();
        this.remainder = remainder;
        this.root = root;
    }

    public Path getPath() {
        return remainder;
    }

    public void setPath(Path path) {
        this.remainder = path;
    }

    public Path getRoot() {
        return root;
    }

    public void setRoot(Path root) {
        this.root = root;
    }

    public Path combined() {
        return root.resolve(remainder);
    }
}
