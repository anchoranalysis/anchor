/* (C)2020 */
package org.anchoranalysis.io.bean.file.matcher;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

interface WalkDirectory {

    public void walkDirectory(Path dir, List<File> listOut);
}
