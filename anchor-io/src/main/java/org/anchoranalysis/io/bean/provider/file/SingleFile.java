/* (C)2020 */
package org.anchoranalysis.io.bean.provider.file;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.io.bean.input.InputManagerParams;
import org.anchoranalysis.io.params.InputContextParams;

@NoArgsConstructor
public class SingleFile extends FileProviderWithDirectory {

    // START BEAN PROPERTIES
    @BeanField @Getter private String path;
    // END BEAN PROPERTIES

    // Optionally changes the directory of the path
    private Path directory;

    public SingleFile(String path) {
        this.path = path;
    }

    @Override
    public Collection<File> matchingFilesForDirectory(Path directory, InputManagerParams params) {

        File file = new File(path);

        if (hasDirectory()) {
            file = directory.resolve(file.getName()).toFile();
        }

        return Collections.singletonList(file);
    }

    public static String replaceBackslashes(String str) {
        return str.replace('\\', '/');
    }

    public void setPath(String path) {
        // Make everything a forward slash
        this.path = replaceBackslashes(path);
    }

    private boolean hasDirectory() {
        return directory != null;
    }

    @Override
    public Path getDirectoryAsPath(InputContextParams inputContext) {

        if (hasDirectory()) {
            return directory;
        } else {
            // We infer the directory if it isn't set
            return Paths.get(path).getParent();
        }
    }
}
