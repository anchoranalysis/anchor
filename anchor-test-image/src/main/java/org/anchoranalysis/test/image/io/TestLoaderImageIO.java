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

package org.anchoranalysis.test.image.io;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import lombok.Getter;
import org.anchoranalysis.bean.xml.RegisterBeanFactories;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.io.bean.rasterreader.RasterReader;
import org.anchoranalysis.image.io.objects.ObjectCollectionReader;
import org.anchoranalysis.image.io.rasterreader.OpenedRaster;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.bioformats.ConfigureBioformatsLogging;
import org.anchoranalysis.io.deserializer.DeserializationFailedException;
import org.anchoranalysis.test.TestDataLoadException;
import org.anchoranalysis.test.TestLoader;

public class TestLoaderImageIO {

    /** Delegate loader (for non image-related loading) */
    @Getter private TestLoader testLoader;

    /** Reads rasters from filesystme */
    private RasterReader rasterReader;

    public TestLoaderImageIO(TestLoader testLoader) {
        this.testLoader = testLoader;

        TestReaderWriterUtilities.ensureRasterReader();
        rasterReader = RegisterBeanFactories.getDefaultInstances().get(RasterReader.class);
    }

    public Channel openChannelFromTestPath(String testPath) {
        return extractChannel(openStackFromTestPath(testPath));
    }

    public Channel openChannelFromFilePath(Path filePath) {
        return extractChannel(openStackFromFilePath(filePath));
    }

    public Stack openStackFromTestPath(String testPath) {
        ConfigureBioformatsLogging.instance().makeSureConfigured();

        Path filePath = testLoader.resolveTestPath(testPath);
        return openStackFromFilePath(filePath);
    }

    public Stack openStackFromFilePath(Path filePath) {

        ConfigureBioformatsLogging.instance().makeSureConfigured();

        try (OpenedRaster openedRaster = rasterReader.openFile(filePath)) {
            return openedRaster.open(0, ProgressReporterNull.get()).get(0);
        } catch (RasterIOException e) {
            throw new TestDataLoadException(e);
        }
    }

    /**
     * Compare two images that are located on the file system in the same test-folder
     *
     * @param path1 first-path to compare
     * @param path2 second-path to compare
     * @return TRUE if the images are equal (every pixel is identical, and data-types are the same)
     * @throws FileNotFoundException if one or both of the files cannot be found
     */
    public boolean compareTwoImages(String path1, String path2) throws FileNotFoundException {
        return compareTwoImages(this, path1, this, path2);
    }

    /**
     * Compare two images that are located on the file system (possibly in different test folders)
     *
     * @param loader1 loader to use for path1
     * @param path1 first-path to compare
     * @param loader2 loader to use for path2
     * @param path2 second-path to compare
     * @return TRUE if the images are equal (every pixel is identical, and data-types are the same)
     * @throws FileNotFoundException if one or both of the files cannot be found
     */
    public static boolean compareTwoImages(
            TestLoaderImageIO loader1, String path1, TestLoaderImageIO loader2, String path2)
            throws FileNotFoundException {

        if (!loader1.getTestLoader().doesPathExist(path1)) {
            throw new FileNotFoundException(path1);
        }

        if (!loader2.getTestLoader().doesPathExist(path2)) {
            throw new FileNotFoundException(path2);
        }

        Stack stackWritten = loader1.openStackFromTestPath(path1);

        Stack stackSaved = loader2.openStackFromTestPath(path2);

        return stackWritten.equals(stackSaved);
    }

    public ObjectCollection openObjectsFromTestPath(String testFolderPath) {
        Path filePath = testLoader.resolveTestPath(testFolderPath);
        return openObjectsFromFilePath(filePath);
    }

    /**
     * Opens an obj-mask-collection from a path to a file
     *
     * @param folderPath the path to a folder
     * @return an object-mask collection
     * @throws TestDataLoadException if data cannot be loaded
     */
    public ObjectCollection openObjectsFromFilePath(Path folderPath) {

        ConfigureBioformatsLogging.instance().makeSureConfigured();
        TestReaderWriterUtilities.ensureRasterReader();

        try {
            return ObjectCollectionReader.createFromPath(folderPath);
        } catch (DeserializationFailedException e) {
            throw new TestDataLoadException(e);
        }
    }

    /**
     * Compare two obj-mask-collection that are located on the file system in the test-folder
     *
     * @param path1 first-path
     * @param path2 second-path
     * @return TRUE if the object-mask-collection are equal (every object-pixel is identical)
     */
    public boolean compareTwoObjectCollections(String path1, String path2) {

        if (!getTestLoader().doesPathExist(path1)) {
            throw new TestDataLoadException(
                    String.format(
                            "The first-path cannot be found in the first test-loader: %s", path1));
        }

        if (!getTestLoader().doesPathExist(path2)) {
            throw new TestDataLoadException(
                    String.format(
                            "The second-path cannot be found in the second test-loader: %s",
                            path1));
        }

        ObjectCollection objectsWritten = openObjectsFromTestPath(path1);

        ObjectCollection objectsSaved = openObjectsFromTestPath(path2);

        return objectsWritten.equalsDeep(objectsSaved);
    }

    public Path resolveTestPath(String testPath) {
        return testLoader.resolveTestPath(testPath);
    }

    private static Channel extractChannel(Stack stack) {
        if (stack.getNumberChannels() != 1) {
            throw new TestDataLoadException(
                    "Loading a stack which contains more than one channel, when only one channel is intended");
        }
        return stack.getChannel(0);
    }
}
