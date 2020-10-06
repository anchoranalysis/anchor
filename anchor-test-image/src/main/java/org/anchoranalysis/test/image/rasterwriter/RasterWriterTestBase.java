package org.anchoranalysis.test.image.rasterwriter;

import java.util.Optional;
import org.anchoranalysis.core.functional.OptionalUtilities;
import org.anchoranalysis.image.io.bean.rasterwriter.RasterWriter;
import org.anchoranalysis.test.image.DualComparer;
import org.anchoranalysis.test.image.DualComparerFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

/**
 * Base class for testing various implementations of {@link RasterWriter}.
 * 
 * <p>The extension passed as a parameter determines where the particular directory saved-rasters are saved to test against:
 * {@code src/test/resources/rasterWriter/formats/$EXTENSION}.
 * 
 * <p>Two types of comparison are optionally possible:
 * <ul>
 * <li>Bytewise comparison, where the exact bytes on the file-system must be identical to the saved raster.
 * <li>Voxelwise comparison, where the voxel intensity-values must be identical to the saved-raster.
 * </ul>
 *  
 * @author Owen Feehan
 *
 */
public abstract class RasterWriterTestBase {
        
    @Rule public TemporaryFolder folder = new TemporaryFolder();

    /** Performs the tests. */
    protected FourChannelStackTester tester;
    
    private final String extension;
    
    /** If true, then 3D stacks are also tested and saved, not just 2D stacks. */
    private final boolean include3D;
    
    /** Iff true, a bytewise comparison occurs between the saved-file and the newly created file. */
    private final boolean bytewiseCompare;

    /** Iff defined, a voxel-wise comparison occurs with the saved-rasters from a different extension. */
    private final Optional<String> extensionVoxelwiseCompare;
    
    /**
     * Creates for a particular extension
     * 
     * @param extension the extension (without a full stop) to be tested and written.
     * @param include3D If true, then 3D stacks are also tested and saved, not just 2D stacks.
     * @param bytewiseCompare iff true, a bytewise comparison occurs between the saved-file and the newly created file.
     * @param extensionVoxelwiseCompare iff defined, a voxel-wise comparison occurs with the saved-rasters from a different extension. 
     */
    public RasterWriterTestBase(String extension, boolean include3D, boolean bytewiseCompare, Optional<String> extensionVoxelwiseCompare) {
        super();
        this.extension = extension;
        this.include3D = include3D;
        this.bytewiseCompare = bytewiseCompare;
        this.extensionVoxelwiseCompare = extensionVoxelwiseCompare;
    }    
    
    @Before public void setup() {
        tester = new FourChannelStackTester(
                createWriter(),
                folder.getRoot().toPath(),
                maybeCreateBytewiseComparer(),
                maybeCreateVoxelwiseComparer(),
                extension,
                "unsigned_8bit_",
                include3D
        );
    }    
    
    /** Creates the {@link RasterWriter} to be tested. */
    protected abstract RasterWriter createWriter();
    
    private Optional<DualComparer> maybeCreateBytewiseComparer() {
        return OptionalUtilities.createFromFlag(bytewiseCompare, () ->
            createComparer(extension)
        );
    }
    
    private Optional<DualComparerWithExtension> maybeCreateVoxelwiseComparer() {
        return extensionVoxelwiseCompare.map( extensionForComparer -> new DualComparerWithExtension( createComparer(extensionForComparer), extensionForComparer) );
    }
    
    private DualComparer createComparer(String extensionForComparer) {
        return DualComparerFactory.compareTemporaryFolderToTest(folder, Optional.empty(), "rasterWriter/formats/" + extensionForComparer);
    }
    
}
