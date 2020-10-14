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

package org.anchoranalysis.io.input.bean.descriptivename;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.io.input.InputReadFailedException;
import org.anchoranalysis.io.input.files.NamedFile;

/**
 * Associates a name (a compact unique identifier) with a file.
 * 
 * <p>The operation can be procesed on a single file or a collection of files.
 * 
 * @author Owen Feehan
 *
 */
public abstract class FileNamer extends AnchorBean<FileNamer> {

    private static final String DEFAULT_ELSE_NAME = "unknownName";

    /**
     * A name for a file
     *
     * @param file the file to extract a name for
     * @param elseName a fallback name to use if something goes wrong
     * @return the file combined with an extracted name
     */
    public NamedFile deriveName(File file, String elseName, Logger logger) {
        return deriveName(Arrays.asList(file), elseName, logger).get(0);
    }
    
    
    /** 
     * Like {@link #deriveNameUnique(Collection, String, Logger)} but with a default for {@code elseName}.
     *
     * @param files the files to describe
     * @param logger the logger
     * @return a list of identical size and order to files, corresponding to the file the extracted name
     * @throws InputReadFailedException if more than one {@link NamedFile} have the same name
     */
    public List<NamedFile> deriveNameUnique(
            Collection<File> files, Logger logger) throws InputReadFailedException {
        return deriveNameUnique(files, DEFAULT_ELSE_NAME, logger);
    }

    /**
     * Derives a list of names (associated with each file) for some files.
     *
     * @param files the files to describe
     * @param elseName a string to use if an error occurs extracting a particular name (used as a
     *     prefix with an index)
     * @param logger the logger
     * @return a list of identical size and order to files, corresponding to the file the extracted name
     */
    public abstract List<NamedFile> deriveName(
            Collection<File> files, String elseName, Logger logger);
    
    /**
     * Like {@link #deriveName(Collection, String, Logger)} but checks that the final list of named-files all have unique
     * names
     * 
     * @param files the files to describe
     * @param elseName a string to use if an error occurs extracting a particular name (used as a
     *     prefix with an index)
     * @param logger the logger
     * @return a list of identical size and order to files, corresponding to the file the extracted name
     * @throws InputReadFailedException if more than one {@link NamedFile} have the same name
     */
    public List<NamedFile> deriveNameUnique(
            Collection<File> files, String elseName, Logger logger) throws InputReadFailedException {
        List<NamedFile> list = deriveName(files, elseName, logger);
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
                                        NamedFile::getName,
                                        Collectors.counting()));

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
                list.stream().filter(file -> predicate.test(file.getName())).count();

        if (numWithBackslashes > 0) {
            throw new InputReadFailedException(
                    String.format(
                            "The following names may not %s:%n%s",
                            dscr, keysWithNamePredicate(predicate, list)));
        }
    }

    /** For debugging if there is a non-uniqueness clash between two {@link NamedFile}s. */
    private static String keysWithName(
            String name, List<NamedFile> list) {
        return keysWithNamePredicate(file -> file.equals(name), list);
    }

    private static String keysWithNamePredicate(
            Predicate<String> predicate, List<NamedFile> list) {
        List<String> matches =
                list.stream()
                        .filter(file -> predicate.test(file.getName()))
                        .map(file -> file.getPath().toString())
                        .collect(Collectors.toList());

        return String.join(System.lineSeparator(), matches);
    }

    private static boolean containsBackslash(String value) {
        return value.contains("\\");
    }

    private static boolean emptyString(String value) {
        return value == null || value.isEmpty();
    }
}
