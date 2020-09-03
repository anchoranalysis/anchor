package org.anchoranalysis.io.bioformats.bean;

import static org.junit.Assert.assertEquals;
import java.io.File;
import java.nio.file.Path;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.io.bean.rasterreader.RasterReader;
import org.anchoranalysis.image.io.rasterreader.OpenedRaster;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.stack.TimeSequence;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.test.TestLoader;
import lombok.AllArgsConstructor;

/**
 * The location of an image and some expectations that should be asserted.
 * 
 * <p>The expectations are:
 * <ul>
 * <li>the voxel-data-type
 * <li>the number of channels
 * <li>the count of intensity values equal to a particular value.
 * </ul>
 * 
 * @author Owen Feehan
 *
 */
@AllArgsConstructor class ExpectedImage {
    
    private static final String IMAGE_DIRECTORY = "images";

    /** The file extension of the iamge */
    private String extension;
    
    /** The filename (without extension) of the image somewhere in the images/ directory */
    private String fileNameWithoutExtension;
    
    /** The expected count of voxels with intensity=={@code intensityValueToCount} in the first channel. */
    private int expectedCount;
    
    /** The expected number of channels */
    private int expectedNumberChannels;
    
    /** The expected data-type of voxels */
    private VoxelDataType expectedDataType;
        
    /** Which intensity value to count */
    private int intensityValueToCount;
    
    public void openAndAssert(RasterReader rasterReader, TestLoader loader) throws RasterIOException {
        Stack stack = openStackFromReader(rasterReader, loader);
        assertEqualsPrefix("voxel data type", expectedDataType, stack.getChannel(0).getVoxelDataType() );
        assertEqualsPrefix("number channels", expectedNumberChannels, stack.getNumberChannels() );
        assertEqualsPrefix("count of voxels==" + intensityValueToCount, expectedCount, stack.getChannel(0).voxelsEqualTo(intensityValueToCount).count());
    }
    
    private Stack openStackFromReader(RasterReader reader, TestLoader loader) throws RasterIOException {
        
        Path path = loader.resolveTestPath( relativePath() );
        
        OpenedRaster openedRaster = reader.openFile(path);
        TimeSequence timeSequence = openedRaster.open(0, ProgressReporterNull.get());
        return timeSequence.get(0);
    }
    
    private void assertEqualsPrefix(String message, int expected, int actual) {
        assertEquals(fileNameWithoutExtension + " " + message, expected, actual);
    }
    
    private void assertEqualsPrefix(String message, Object expected, Object actual) {
        assertEquals(fileNameWithoutExtension + " " + message, expected, actual);
    }
    
    private String relativePath() {
        StringBuilder builder = new StringBuilder();
        builder.append(IMAGE_DIRECTORY);
        builder.append(File.separator);
        builder.append(extension);
        builder.append(File.separator);
        builder.append(fileNameWithoutExtension);
        builder.append(".");
        builder.append(extension);
        return builder.toString();
    }
}