/*-
 * #%L
 * anchor-test-image
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

package org.anchoranalysis.test.image;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import lombok.Getter;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.io.csv.comparer.CSVComparer;
import org.anchoranalysis.io.csv.reader.CSVReaderException;
import org.anchoranalysis.test.TestLoader;
import org.anchoranalysis.test.image.io.TestLoaderImageIO;

/**
 * Allows for comparison of objects that exist on different test loaders
 *
 * @author Owen Feehan
 */
public class DualComparer {

    @Getter private final TestLoader loader1;

    @Getter private final TestLoader loader2;

    private final TestLoaderImageIO loaderImage1;
    private final TestLoaderImageIO loaderImage2;

    public DualComparer(TestLoader loader1, TestLoader loader2) {
        super();
        this.loader1 = loader1;
        this.loader2 = loader2;
        this.loaderImage1 = new TestLoaderImageIO(loader1);
        this.loaderImage2 = new TestLoaderImageIO(loader2);
    }

    /**
     * Compare two images that have an identical path, but in two different test loaders
     *
     * @param path path to compare
     * @return true if the images are equal (every pixel is identical, and data-types are the same)
     * @throws FileNotFoundException if one or both of the files cannot be found
     */
    public boolean compareTwoImages(String path) throws FileNotFoundException {
        return TestLoaderImageIO.compareTwoImages(loaderImage1, path, loaderImage2, path);
    }

    /**
     * Compare two XML documents. They are compared by their DOM trees, but they need to be
     * identical for equality.
     *
     * @param path path to compare
     * @return true if the xml-documents are equal, fALSE otherwise
     */
    public boolean compareTwoXmlDocuments(String path) {
        return TestLoader.areXmlEqual(
                loader1.openXmlFromTestPath(path), loader2.openXmlFromTestPath(path));
    }

    /**
     * Compare two CSV files, ignoring the first numFirstColumnsToIgnore. They need to be exactly
     * identical, apart from these ignored columns.
     *
     * @param path path to compare
     * @param messageStream if non-equal, additional explanation messages are printed here
     * @return true if the csv-files are identical apart from the ignored columns, fALSE otherwise
     * @throws CSVReaderException if something goes wrong with csv I/O or a csv file is reject
     */
    public boolean compareTwoCsvFiles(String path, CSVComparer comparer, PrintStream messageStream)
            throws CSVReaderException {
        return comparer.areCsvFilesEqual(
                loaderImage1.resolveTestPath(path),
                loaderImage2.resolveTestPath(path),
                messageStream);
    }

    /**
     * Compare two object-mask-collections
     *
     * @param path path to compare
     * @throws IOException if something goes wrong with I/O
     */
    public boolean compareTwoObjectCollections(String path) throws IOException {
        ObjectCollection objects1 = loaderImage1.openObjectsFromTestPath(path);
        ObjectCollection objects2 = loaderImage2.openObjectsFromTestPath(path);
        return objects1.equalsDeep(objects2);
    }

    public boolean compareTwoSubdirectories(String path) {
        Path dir1 = loaderImage1.resolveTestPath(path);
        Path dir2 = loaderImage2.resolveTestPath(path);
        return DirectoriesComparer.areDirectoriesEqual(dir1, dir2);
    }
}
