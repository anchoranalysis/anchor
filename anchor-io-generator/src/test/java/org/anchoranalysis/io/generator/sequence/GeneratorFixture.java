package org.anchoranalysis.io.generator.sequence;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.Optional;
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

    /**
     * Creates returning a particular number of file-types
     * 
     * @param numberFileTypes the number of distinct file-types to return for each call to write
     */    
    public static Generator<Integer> create(int numberFileTypes) throws OperationFailedException {
        
        Optional<FileType[]> manifestFileTypes = Optional.of( createFileTypes(numberFileTypes) );
        
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
    
    private static FileType[] createFileTypes(int numberFileTypes) {
        return IntStream.range(0, numberFileTypes).mapToObj( index -> fileType(index)).toArray(FileType[]::new);
    }
    
    private static FileType fileType(int index) {
        return new FileType(MANIFEST_DESCRIPTION, "extension" + index);
    }
}
