/*-
 * #%L
 * anchor-plugin-opencv
 * %%
 * Copyright (C) 2010 - 2021 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.plugin.opencv.bean.object.segment.decode.instance;

import java.nio.file.Path;
import java.util.List;
import org.anchoranalysis.core.exception.InitializeException;
import org.anchoranalysis.core.time.ExecutionTimeRecorderIgnore;
import org.anchoranalysis.image.bean.nonbean.error.SegmentationFailedException;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.inference.bean.segment.instance.SegmentStackIntoObjectsPooled;
import org.anchoranalysis.image.inference.segment.SegmentedObjects;
import org.anchoranalysis.image.io.ImageInitializationFactory;
import org.anchoranalysis.io.output.outputter.InputOutputContext;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.test.TestLoader;
import org.anchoranalysis.test.image.InputOutputContextFixture;
import org.anchoranalysis.test.image.WriteIntoDirectory;
import org.anchoranalysis.test.image.load.CarImageLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Base class for testing implementations of {@link SegmentStackIntoObjectsPooled}.
 *
 * @author Owen Feehan
 */
public abstract class InstanceSegmentationTestBase {

    /** The segmentation implementation to test. */
    private SegmentStackIntoObjectsPooled<?> segmenter;

    @TempDir Path temporaryDirectory;

    private WriteIntoDirectory writer;

    private CarImageLoader loader = new CarImageLoader();

    @BeforeEach
    void setup() throws InitializeException {
        writer = new WriteIntoDirectory(temporaryDirectory, false);
        segmenter = createSegmenter();
        initSegmenter(segmenter);
    }

    @Test
    void testRGB() throws SegmentationFailedException {
        assertExpectedSegmentation(stackRGB(), expectedBoxesRGB(), "rgb");
    }

    @Test
    void testGrayscale8Bit() throws SegmentationFailedException {
        assertExpectedSegmentation(stackGrayscale(), expectedBoxesGrayscale(), "grayscale");
    }

    /** Creates the segmentation implementation to be tested. */
    protected abstract SegmentStackIntoObjectsPooled<?> createSegmenter();

    /** The RGB stack that is tested. */
    protected Stack stackRGB() {
        return loader.carRGB();
    }

    /** The grayscale stack that is tested. */
    protected Stack stackGrayscale() {
        return loader.carGrayscale8Bit();
    }

    /**
     * The bounding-boxes of the objects expected from the <i>RGB</i> stack as a segmentation
     * result.
     */
    protected abstract List<BoundingBox> expectedBoxesRGB();

    /**
     * The bounding-boxes of the objects expected from the <i>grayscale</i> stack as a segmentation
     * result.
     */
    protected abstract List<BoundingBox> expectedBoxesGrayscale();

    private void assertExpectedSegmentation(
            Stack stack, List<BoundingBox> expectedBoxes, String suffix)
            throws SegmentationFailedException {
        SegmentedObjects segmentResults =
                segmenter.segment(stack, ExecutionTimeRecorderIgnore.instance());
        writer.writeObjects("objects_" + suffix, segmentResults.asObjects(), stackRGB());
        ExpectedBoxesChecker.assertExpectedBoxes(segmentResults.asObjects(), expectedBoxes);
    }

    private static void initSegmenter(SegmentStackIntoObjectsPooled<?> segmenter)
            throws InitializeException {
        Path root = TestLoader.createFromMavenWorkingDirectory().getRoot();
        InputOutputContext context = InputOutputContextFixture.withSuppressedLogger(root);
        segmenter.initializeRecursive(
                ImageInitializationFactory.create(context), context.getLogger());
    }
}
