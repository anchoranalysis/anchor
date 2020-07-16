/* (C)2020 */
package org.anchoranalysis.io.bean.provider.file;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.OptionalBean;
import org.anchoranalysis.core.functional.FunctionalList;
import org.anchoranalysis.core.functional.FunctionalProgress;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.io.bean.input.InputManagerParams;
import org.anchoranalysis.io.error.FileProviderException;
import org.anchoranalysis.io.params.InputContextParams;

/**
 * A specific list of paths which form the input.
 *
 * <p>If no paths are specified in the bean, then can be read from the Input-Context
 *
 * <p>If none are available in the Input-Context, then either the fallback is called if it exists,
 * or an error is thrown
 *
 * @author Owen Feehan
 */
@NoArgsConstructor
public class SpecificPathList extends FileProvider {

    // START BEAN PROPERTIES
    /**
     * If specified, this forms the list of paths which is provided as input. If not, then the
     * input-context is asked. If still not, then the fallback.
     */
    @BeanField @OptionalBean @Getter @Setter private List<String> listPaths;

    /**
     * If no paths can be found either from listPaths or the input-context, then the fallback is
     * called if exists, otherwise an error is thrown
     */
    @BeanField @OptionalBean @Getter @Setter private FileProvider fallback;
    // END BEAN PROPERTIES

    public SpecificPathList(List<String> listPaths) {
        this.listPaths = listPaths;
    }

    /** Factory method for creating the class with an empty list of paths */
    public static SpecificPathList createWithEmptyList() {
        SpecificPathList out = new SpecificPathList();
        out.listPaths = new ArrayList<>();
        return out;
    }

    @Override
    public Collection<File> create(InputManagerParams params) throws FileProviderException {

        Optional<List<String>> selectedPaths = selectListPaths(params.getInputContext());

        if (selectedPaths.isPresent()) {
            return matchingFilesForList(selectedPaths.get(), params.getProgressReporter());
        } else if (fallback != null) {
            return fallback.create(params);
        } else {
            throw new FileProviderException("No input-paths are specified, nor a fallback");
        }
    }

    private Optional<List<String>> selectListPaths(InputContextParams inputContext) {
        if (listPaths != null) {
            return Optional.of(listPaths);
        } else {
            return inputContext.getInputPaths().map(SpecificPathList::stringFromPaths);
        }
    }

    private static List<String> stringFromPaths(List<Path> paths) {
        return FunctionalList.mapToList(paths, Path::toString);
    }

    private static Collection<File> matchingFilesForList(
            List<String> listPaths, ProgressReporter progressReporter) {
        return FunctionalProgress.mapList(listPaths, progressReporter, File::new);
    }
}
