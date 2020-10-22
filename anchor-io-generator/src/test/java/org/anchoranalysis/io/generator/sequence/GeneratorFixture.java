package org.anchoranalysis.io.generator.sequence;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.Optional;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.functional.OptionalUtilities;
import org.anchoranalysis.io.generator.Generator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.file.FileType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PACKAGE)
public class GeneratorFixture {

    public static final ManifestDescription MANIFEST_DESCRIPTION = new ManifestDescription("testType", "testFunction");
    
    public static Generator<Integer> create(boolean includeFileTypes) throws OperationFailedException {
        
        Optional<FileType[]> manifestFileTypes = OptionalUtilities.createFromFlag(includeFileTypes, GeneratorFixture::fileTypes);
        
        @SuppressWarnings("unchecked")
        Generator<Integer> generator = mock(Generator.class);
        when(generator.getFileTypes(any())).thenReturn(manifestFileTypes);
        return generator;
    }
    
    private static FileType[] fileTypes() {
        return new FileType[]{
                new FileType(MANIFEST_DESCRIPTION, "test")
        };
    }
}
