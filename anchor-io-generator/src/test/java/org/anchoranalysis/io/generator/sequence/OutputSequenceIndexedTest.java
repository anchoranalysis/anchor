/*-
 * #%L
 * anchor-io-generator
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
package org.anchoranalysis.io.generator.sequence;

import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.io.generator.sequence.pattern.OutputPattern;
import org.anchoranalysis.io.generator.sequence.pattern.OutputPatternStringSuffix;
import org.anchoranalysis.io.manifest.Manifest;
import org.anchoranalysis.io.manifest.sequencetype.SequenceType;
import org.anchoranalysis.io.manifest.sequencetype.StringsWithoutOrder;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.outputter.BindFailedException;
import org.anchoranalysis.io.output.outputter.OutputterChecked;
import org.anchoranalysis.test.io.output.OutputterCheckedFixture;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OutputSequenceIndexedTest {
    
    private static final String OUTPUT_NAME = "out";
    
    @Test
    public void testUniformFileTypes() throws OutputWriteFailedException, OperationFailedException {
        test(true);
    }
    
    @Test
    public void testNonUniformFileTypes() throws OutputWriteFailedException, OperationFailedException {
        test(false);
    }
    
    /**
     * Outputs a sequence with the generator configured with different types of file-types.
     * 
     * @param withUniformFileTypes if true, the generator provides certain file-types common to all.
     * 
     * @throws OutputWriteFailedException
     * @throws OperationFailedException
     */
    private static void test(boolean withUniformFileTypes) throws OutputWriteFailedException, OperationFailedException {
        Manifest manifest = new Manifest();
        
        SequenceType<String> sequenceType = new StringsWithoutOrder();

        BoundOutputter<Integer> outputter = createOutputter( new OutputPatternStringSuffix(OUTPUT_NAME, false), manifest, withUniformFileTypes);
        
        OutputSequenceIndexed<Integer, String> sequence = new OutputSequenceIndexed<>(
                outputter, sequenceType
        );
        
        sequence.add(4, "4");
        sequence.add(5, "6");
        sequence.add(9, "9");
        
        assertEquals(3, sequenceType.getNumberElements());
        assertEquals(1, manifest.getRootFolder().subdirectories().size());
        assertTrue(manifest.getRootFolder().subdirectories().get(0) instanceof IndexableSubdirectory);
        checkIndexableSubdirectory( (IndexableSubdirectory) manifest.getRootFolder().subdirectories().get(0));        
    }
    
    private static void checkIndexableSubdirectory(IndexableSubdirectory subdirectory) {
        assertTrue( subdirectory.getFileTypes().size()==1 );
        assertTrue( subdirectory.getFileTypes().iterator().next().getManifestDescription().equals(GeneratorFixture.MANIFEST_DESCRIPTION) );
        assertTrue( subdirectory.getOutputName().getOutputName().equals(OUTPUT_NAME) );
    }

    private static BoundOutputter<Integer> createOutputter(OutputPattern pattern, Manifest manifest, boolean includeFileTypes) throws OperationFailedException {

        try {
            OutputterChecked outputter = OutputterCheckedFixture.create(manifest);
            return new BoundOutputter<Integer>(outputter, pattern, GeneratorFixture.create(includeFileTypes) );
            
        } catch (BindFailedException e) {
            throw new OperationFailedException(e);
        }
    }
}
