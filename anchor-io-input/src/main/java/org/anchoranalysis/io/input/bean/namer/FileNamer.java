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

package org.anchoranalysis.io.input.bean.namer;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.io.input.InputReadFailedException;
import org.anchoranalysis.io.input.file.FileNamerContext;
import org.anchoranalysis.io.input.file.NamedFile;

/**
 * Associates a name (a compact unique identifier) with a file.
 *
 * <p>The operation can be processed on a single file or a collection of files.
 *
 * @author Owen Feehan
 */
public abstract class FileNamer extends AnchorBean<FileNamer> {

    /**
     * A name for a file.
     *
     * @param file the file to extract a name for.
     * @param context additional context for naming.
     * @return the file combined with an extracted name.
     */
    public NamedFile deriveName(File file, FileNamerContext context) {
        return deriveName(Arrays.asList(file), context).get(0);
    }

    /**
     * Derives a list of names (associated with each file) for some files.
     *
     * @param files the files to describe.
     * @param context additional context for naming.
     * @return a list of identical size and order to files, corresponding to the file the extracted
     *     name.
     */
    public abstract List<NamedFile> deriveName(List<File> files, FileNamerContext context);

    /**
     * Like {@link #deriveName(List, FileNamerContext)} but checks that the final list of
     * named-files all have unique names.
     *
     * @param files the files to describe.
     * @param context additional context for naming.
     * @return a list of identical size and order to files, corresponding to the file the extracted
     *     name.
     * @throws InputReadFailedException if more than one {@link NamedFile} have the same name
     */
    public List<NamedFile> deriveNameUnique(List<File> files, FileNamerContext context)
            throws InputReadFailedException {
        List<NamedFile> list = deriveName(files, context);
        checkUniqueness(list);
        checkNoPredicate(list, FileNamer::containsBackslash, "contain backslashes");
        checkNoPredicate(list, FileNamer::emptyString, "contain an empty string");
        return list;
    }

    private static void checkUniqueness(List<NamedFile> list) throws InputReadFailedException {
        Map<String, Long> countDescriptiveNames =
                list.stream()
                        .collect(
                                Collectors.groupingBy(
                                        NamedFile::getIdentifier, Collectors.counting()));

        for (Map.Entry<String, Long> entry : countDescriptiveNames.entrySet()) {
            if (entry.getValue() > 1) {
                throw new InputReadFailedException(
                        String.format(
                                "The extracted names are not unique for %s.%nThe following have the same name:%n%s",
                                entry.getKey(), keysWithName(entry.getKey(), list)));
            }
        }
    }

    private static void checkNoPredicate(
            List<NamedFile> list, Predicate<String> predicate, String dscr)
            throws InputReadFailedException {
        long numWithBackslashes =
                list.stream().filter(file -> predicate.test(file.getIdentifier())).count();

        if (numWithBackslashes > 0) {
            throw new InputReadFailedException(
                    String.format(
                            "The following names may not %s:%n%s",
                            dscr, keysWithNamePredicate(predicate, list)));
        }
    }

    /** For debugging if there is a non-uniqueness clash between two {@link NamedFile}s. */
    private static String keysWithName(String name, List<NamedFile> list) {
        return keysWithNamePredicate(file -> file.equals(name), list);
    }

    private static String keysWithNamePredicate(Predicate<String> predicate, List<NamedFile> list) {
        List<String> matches =
                list.stream()
                        .filter(file -> predicate.test(file.getIdentifier()))
                        .map(file -> file.getPath().toString())
                        .toList();

        return String.join(System.lineSeparator(), matches);
    }

    private static boolean containsBackslash(String value) {
        return value.contains("\\");
    }

    private static boolean emptyString(String value) {
        return value == null || value.isEmpty();
    }
}
