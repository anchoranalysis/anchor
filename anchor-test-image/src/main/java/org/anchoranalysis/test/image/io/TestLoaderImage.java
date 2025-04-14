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
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.serialize.DeserializationFailedException;
import org.anchoranalysis.core.time.ExecutionTimeRecorderIgnore;
import org.anchoranalysis.image.core.channel.Channel;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.io.ImageIOException;
import org.anchoranalysis.image.io.bean.stack.reader.StackReader;
import org.anchoranalysis.image.io.object.input.ObjectCollectionReader;
import org.anchoranalysis.image.io.stack.input.OpenedImageFile;
import org.anchoranalysis.image.voxel.object.ObjectCollection;
import org.anchoranalysis.io.bioformats.ConfigureBioformatsLogging;
import org.anchoranalysis.test.LoggerFixture;
import org.anchoranalysis.test.TestDataLoadException;
import org.anchoranalysis.test.TestLoader;

/**
 * A test loader for image-related operations, providing methods to open and compare images and object collections.
 */
@RequiredArgsConstructor
public class TestLoaderImage {

    // START REQUIRED ARGUMENTS
    /** Delegate loader (for non image-related loading) */
    @Getter private final TestLoader loader;

    /** Reads rasters from filesystem */
    private final StackReader stackReader;
    // END REQUIRED ARGUMENTS

    private final Logger logger = LoggerFixture.suppressedLogger();

    /**
     * Constructs a TestLoaderImage with a given TestLoader.
     *
     * @param loader The TestLoader to use for non image-related loading.
     */
    public TestLoaderImage(TestLoader loader) {
        this.loader = loader;
        this.stackReader = BeanInstanceMapFixture.ensureStackReader();
    }

    /**
     * Opens a channel from a test path.
     *
     * @param testPath The test path to the image file.
     * @return The opened channel.
     */
    public Channel openChannelFromTestPath(String testPath) {
        return extractChannel(openStackFromTestPath(testPath));
    }

    /**
     * Opens a channel from a file path.
     *
     * @param filePath The file path to the image file.
     * @return The opened channel.
     */
    public Channel openChannelFromFilePath(Path filePath) {
        return extractChannel(openStackFromFilePath(filePath));
    }

    /**
     * Opens a stack from a test path.
     *
     * @param testPath The test path to the image file.
     * @return The opened stack.
     */
    public Stack openStackFromTestPath(String testPath) {
        ConfigureBioformatsLogging.instance().makeSureConfigured();

        Path filePath = loader.resolveTestPath(testPath);
        return openStackFromFilePath(filePath);
    }

    /**
     * Opens a stack from a file path.
     *
     * @param filePath The file path to the image file.
     * @return The opened stack.
     */
    public Stack openStackFromFilePath(Path filePath) {

        ConfigureBioformatsLogging.instance().makeSureConfigured();

        try (OpenedImageFile openedFile =
                stackReader.openFile(filePath, ExecutionTimeRecorderIgnore.instance())) {
            return openedFile.open(logger).getFrame(0);
        } catch (ImageIOException e) {
            throw new TestDataLoadException(e);
        }
    }

    /**
     * Compare two images that are located on the file system in the same test-folder
     *
     * @param path1 first-path to compare
     * @param path2 second-path to compare
     * @return true if the images are equal (every pixel is identical, and data-types are the same)
     * @throws FileNotFoundException if one or both of the files cannot be found
     */
    public boolean compareTwoImages(String path1, String path2) throws FileNotFoundException {
        return compareTwoImages(this, path1, this, path2, false);
    }

    /**
     * Compare two images that are located on the file system (possibly in different test folders)
     *
     * @param loader1 loader to use for path1
     * @param path1 first-path to compare
     * @param loader2 loader to use for path2
     * @param path2 second-path to compare
     * @param ignoreResolutionDifferences if true any differences in image-resolution are not
     *     considered
     * @return true if the images are equal (every pixel is identical, and data-types are the same)
     * @throws FileNotFoundException if one or both of the files cannot be found
     */
    public static boolean compareTwoImages(
            TestLoaderImage loader1,
            String path1,
            TestLoaderImage loader2,
            String path2,
            boolean ignoreResolutionDifferences)
            throws FileNotFoundException {

        if (!loader1.doesPathExist(path1)) {
            throw new FileNotFoundException(path1);
        }

        if (!loader2.doesPathExist(path2)) {
            throw new FileNotFoundException(path2);
        }

        Stack stackWritten = loader1.openStackFromTestPath(path1);

        Stack stackSaved = loader2.openStackFromTestPath(path2);

        return stackWritten.equalsDeep(stackSaved, !ignoreResolutionDifferences);
    }

    /**
     * Opens an object collection from a test path.
     *
     * @param testDirectoryPath The test path to the directory containing the object collection.
     * @return The opened object collection.
     */
    public ObjectCollection openObjectsFromTestPath(String testDirectoryPath) {
        Path filePath = loader.resolveTestPath(testDirectoryPath);
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
        BeanInstanceMapFixture.ensureStackReader();

        try {
            return ObjectCollectionReader.createFromPath(
                    folderPath, LoggerFixture.suppressedOperationContext());
        } catch (DeserializationFailedException e) {
            throw new TestDataLoadException(e);
        }
    }

    /**
     * Compare two obj-mask-collection that are located on the file system in the test-folder
     *
     * @param path1 first-path
     * @param path2 second-path
     * @return true if the object-mask-collection are equal (every object-pixel is identical)
     */
    public boolean compareTwoObjectCollections(String path1, String path2) {

        if (!loader.doesPathExist(path1)) {
            throw new TestDataLoadException(
                    String.format(
                            "The first-path cannot be found in the first test-loader: %s", path1));
        }

        if (!loader.doesPathExist(path2)) {
            throw new TestDataLoadException(
                    String.format(
                            "The second-path cannot be found in the second test-loader: %s",
                            path1));
        }

        ObjectCollection objectsWritten = openObjectsFromTestPath(path1);

        ObjectCollection objectsSaved = openObjectsFromTestPath(path2);

        return objectsWritten.equalsDeep(objectsSaved);
    }

    /**
     * Resolves a test path to an actual file system path.
     *
     * @param testPath The test path to resolve.
     * @return The resolved file system path.
     */
    public Path resolveTestPath(String testPath) {
        return loader.resolveTestPath(testPath);
    }

    /**
     * Checks if a test path exists.
     *
     * @param testFilePath The test path to check.
     * @return true if the path exists, false otherwise.
     */
    public boolean doesPathExist(String testFilePath) {
        return loader.doesPathExist(testFilePath);
    }

    private static Channel extractChannel(Stack stack) {
        if (stack.getNumberChannels() != 1) {
            throw new TestDataLoadException(
                    "Loading a stack which contains more than one channel, when only one channel is intended");
        }
        return stack.getChannel(0);
    }
}
