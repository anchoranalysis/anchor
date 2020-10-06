package org.anchoranalysis.test.image.rasterwriter;

import org.anchoranalysis.image.io.bean.rasterwriter.RasterWriter;
import org.anchoranalysis.test.image.DualComparerTemporaryFolder;
import org.junit.Before;
import org.junit.Rule;

/**
 * Base class for testing various implementations of {@link RasterWriter}.
 * 
 * <p>The extension passed as a parameter determines where the particular directory saved-rasters are saved to test against:
 * {@code src/test/resources/rasterWriter/formats/$EXTENSION}.
 *  
 * @author Owen Feehan
 *
 */
public abstract class RasterWriterTestBase {
        
    @Rule public DualComparerTemporaryFolder comparer;

    /** Performs the tests. */
    protected FourChannelStackTester tester;
    
    private final String extension;
    
    /** If true, then 3D stacks are also tested and saved, not just 2D stacks. */
    private final boolean include3D;
    
    /**
     * Creates for a particular extension
     * 
     * @param extension the extension (without a full stop) to be tested and written.
     * @param include3D If true, then 3D stacks are also tested and saved, not just 2D stacks.
     */
    public RasterWriterTestBase(String extension, boolean include3D) {
        super();
        this.extension = extension;
        this.include3D = include3D;
        this.comparer = new DualComparerTemporaryFolder("rasterWriter/formats/" + extension);
    }    
    
    @Before public void setup() {
        tester = new FourChannelStackTester(createWriter(), comparer, extension, "unsigned_8bit_", include3D);
    }    
    
    /** Creates the {@link RasterWriter} to be tested. */
    protected abstract RasterWriter createWriter();
    
}
