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

import com.google.common.io.Files;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import lombok.Getter;
import org.anchoranalysis.image.voxel.object.ObjectCollection;
import org.anchoranalysis.io.input.csv.CSVReaderException;
import org.anchoranalysis.test.TestLoader;
import org.anchoranalysis.test.image.csv.CSVComparer;
import org.anchoranalysis.test.image.io.TestLoaderImage;
import org.apache.commons.io.FileUtils;

/**
 * Allows for comparison of objects that exist on different test loaders
 *
 * @author Owen Feehan
 */
public class DualComparer {

    @Getter private final TestLoader loader1;

    @Getter private final TestLoader loader2;

    private final TestLoaderImage loaderImage1;
    private final TestLoaderImage loaderImage2;

    public DualComparer(TestLoader loader1, TestLoader loader2) {
        super();
        this.loader1 = loader1;
        this.loader2 = loader2;
        this.loaderImage1 = new TestLoaderImage(loader1);
        this.loaderImage2 = new TestLoaderImage(loader2);
    }

    /**
     * Compare two images that have an identical path, but in two different test loaders
     *
     * @param path relative-path (compared to root of both loaders) of files to compare
     * @return true if the images are equal (every pixel is identical, and data-types are the same)
     * @throws FileNotFoundException if one or both of the files cannot be found
     */
    public boolean compareTwoImages(String path) throws FileNotFoundException {
        return TestLoaderImage.compareTwoImages(loaderImage1, path, loaderImage2, path, false);
    }

    /**
     * Compare two images with different paths, but in two different test loaders
     *
     * @param path1 relative-path (compared to root of first loader) of first image
     * @param path2 relative-path (compared to root of second loader) of second image
     * @param ignoreResolutionDifferences if true any differences in image-resolution are not
     *     considered
     * @return true if the images are equal (every pixel is identical, and data-types are the same)
     * @throws FileNotFoundException if one or both of the files cannot be found
     */
    public boolean compareTwoImages(String path1, String path2, boolean ignoreResolutionDifferences)
            throws FileNotFoundException {
        return TestLoaderImage.compareTwoImages(
                loaderImage1, path1, loaderImage2, path2, ignoreResolutionDifferences);
    }

    /**
     * Compare two XML documents. They are compared by their DOM trees, but they need to be
     * identical for equality.
     *
     * @param path relative-path (compared to root of both loaders) of files to compare
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
     * @param path relative-path (compared to root of both loaders) of files to compare
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
     * Compare two object-mask-collections.
     *
     * @param path relative-path (compared to root of both loaders) of files to compare
     * @return true if both paths return object-collections that are voxelwise identical.
     * @throws IOException if something goes wrong with I/O
     */
    public boolean compareTwoObjectCollections(String path) throws IOException {
        ObjectCollection objects1 = loaderImage1.openObjectsFromTestPath(path);
        ObjectCollection objects2 = loaderImage2.openObjectsFromTestPath(path);
        return objects1.equalsDeep(objects2);
    }

    /**
     * Compare two binary-files.
     *
     * @param path relative-path (compared to root of both loaders) of files to compare
     * @return true if both paths have binary-files that are bytewise identical
     * @throws IOException if something goes wrong with I/O
     */
    public boolean compareTwoBinaryFiles(String path) throws IOException {
        return Files.equal(
                loaderImage1.resolveTestPath(path).toFile(),
                loaderImage2.resolveTestPath(path).toFile());
    }

    public boolean compareTwoSubdirectories(String path) {
        Path dir1 = loaderImage1.resolveTestPath(path);
        Path dir2 = loaderImage2.resolveTestPath(path);
        return DirectoriesComparer.areDirectoriesEqual(dir1, dir2);
    }

    /**
     * Copies a file from its path in the first loader, to its path in the second loader.
     *
     * <p>Any existing file is replaced.
     *
     * @param path relative-path (compared to root of both loaders) of files to copy
     * @throws IOException if copyign fails
     */
    public void copyFromPath1ToPath2(String path) throws IOException {
        FileUtils.copyFile(
                loaderImage1.resolveTestPath(path).toFile(),
                loaderImage2.resolveTestPath(path).toFile());
    }
}
