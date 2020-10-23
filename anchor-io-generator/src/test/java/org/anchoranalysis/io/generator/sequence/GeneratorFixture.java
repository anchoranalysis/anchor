package org.anchoranalysis.io.generator.sequence;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.stream.IntStream;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.io.generator.Generator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.file.FileType;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PACKAGE)
public class GeneratorFixture {

    public static final ManifestDescription MANIFEST_DESCRIPTION = new ManifestDescription("testType", "testFunction");

    public static final String EXTENSION_PREFIX1 = "first";
    public static final String EXTENSION_PREFIX2 = "second";
    
    /**
     * Creates returning a particular number of file-types that <i>always the same for each call to write</i>.
     * 
     * @param numberFileTypes the number of distinct file-types to return for each call to write
     */    
    public static Generator<Integer> create(int numberFileTypes) throws OperationFailedException {
        return createMockGenerator(EXTENSION_PREFIX1, numberFileTypes);
    }
    
    /**
     * Creates returning a particular number of file-types that <i>alternate for each call to write between two different arrays</i>.
     * 
     * @param numberFileTypes the number of distinct file-types to return for each call to write
     */
    public static Generator<Integer> createAlternatingFileTypes(int numberFileTypes) throws OperationFailedException {
        Generator<Integer> first = createMockGenerator(EXTENSION_PREFIX1, numberFileTypes );
        Generator<Integer> second = createMockGenerator(EXTENSION_PREFIX2, numberFileTypes );
        return new AlternatingGenerator<>(first, second);
    }
    
    private static Generator<Integer> createMockGenerator(String extensionPrefix, int numberFileTypes) throws OperationFailedException {
        return createMockGenerator( createFileTypes(extensionPrefix, numberFileTypes) );
    }
    
    private static Generator<Integer> createMockGenerator(FileType[] manifestFileTypes) throws OperationFailedException {
        try {
            @SuppressWarnings("unchecked")
            Generator<Integer> generator = mock(Generator.class);
            when(generator.write(any(),any(),any())).thenReturn(manifestFileTypes);
            when(generator.writeWithIndex(any(),any(),any(),any())).thenReturn(manifestFileTypes);
            return generator;
        } catch (OutputWriteFailedException e) {
            throw new OperationFailedException(e);
        }        
    }
    
    private static FileType[] createFileTypes(String extensionPrefix, int numberFileTypes) {
        return IntStream.range(0, numberFileTypes).mapToObj( index -> fileType(extensionPrefix, index)).toArray(FileType[]::new);
    }
    
    private static FileType fileType(String extensionPrefix, int index) {
        return new FileType(MANIFEST_DESCRIPTION, extensionPrefix + index);
    }
}
