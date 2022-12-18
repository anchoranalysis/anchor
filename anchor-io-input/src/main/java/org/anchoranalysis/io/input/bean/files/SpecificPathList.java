/*-
 * #%L
 * anchor-io
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

package org.anchoranalysis.io.input.bean.files;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.OptionalBean;
import org.anchoranalysis.bean.primitive.StringList;
import org.anchoranalysis.core.functional.FunctionalList;
import org.anchoranalysis.core.system.path.CommonPath;
import org.anchoranalysis.io.input.InputContextParameters;
import org.anchoranalysis.io.input.bean.InputManagerParameters;
import org.anchoranalysis.io.input.file.FilesProviderException;

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
public class SpecificPathList extends FilesProvider {

    // START BEAN PROPERTIES
    /**
     * If specified, this forms the list of paths which is provided as input.
     *
     * <p>If not, then the input-context is asked. If still not, then the fallback.
     *
     * <p>If a list is specified, than the input-directory is derived from the maximally common root
     * of all the files, if it exists.
     */
    @BeanField @OptionalBean @Getter @Setter private StringList listPaths;

    /**
     * If no paths can be found either from listPaths or the input-context, then the fallback is
     * called if exists, otherwise an error is thrown
     */
    @BeanField @OptionalBean @Getter @Setter private FilesProvider fallback;
    // END BEAN PROPERTIES

    /**
     * Creates by reusing an existing list.
     *
     * @param listPaths the list of paths to reuse.
     */
    public SpecificPathList(List<String> listPaths) {
        this.listPaths = new StringList(listPaths);
    }

    /**
     * Creates by reusing an existing list.
     *
     * @param listPaths the list of paths to reuse.
     */
    public SpecificPathList(StringList listPaths) {
        this.listPaths = listPaths;
    }

    /**
     * Factory method for creating the class with an empty list of paths.
     *
     * @return a newly created list that is empty.
     */
    public static SpecificPathList createWithEmptyList() {
        SpecificPathList out = new SpecificPathList();
        out.listPaths = new StringList();
        return out;
    }

    @Override
    public List<File> create(InputManagerParameters parameters) throws FilesProviderException {

        Optional<List<String>> selectedPaths = selectListPaths(parameters.getInputContext());

        if (selectedPaths.isPresent()) {
            return matchingFilesForList(selectedPaths.get());
        } else if (fallback != null) {
            return fallback.create(parameters);
        } else {
            throw exceptionIfUnspecified();
        }
    }

    @Override
    public Optional<Path> rootDirectory(InputContextParameters inputContext)
            throws FilesProviderException {

        Optional<List<String>> selectedPaths = selectListPaths(inputContext);

        if (selectedPaths.isPresent()) {
            return CommonPath.fromStrings(selectedPaths.get());
        } else if (fallback != null) {
            return fallback.rootDirectory(inputContext);
        } else {
            throw exceptionIfUnspecified();
        }
    }

    private Optional<List<String>> selectListPaths(InputContextParameters inputContext) {
        if (listPaths != null) {
            return Optional.of(listPaths.asList());
        } else {
            return inputContext.getInputPaths().map(SpecificPathList::stringFromPaths);
        }
    }

    private static FilesProviderException exceptionIfUnspecified() {
        return new FilesProviderException("No input-paths are specified, nor a fallback");
    }

    private static List<String> stringFromPaths(List<Path> paths) {
        return FunctionalList.mapToList(paths, Path::toString);
    }

    private static List<File> matchingFilesForList(List<String> listPaths) {
        return FunctionalList.mapToList(listPaths, File::new);
    }
}
