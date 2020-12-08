package org.anchoranalysis.image.core.dimensions.resize.suggestion;

import static org.junit.Assert.assertEquals;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
class ImageResizeSuggestionHelper {
    
    public static void testScaleTo(String suggestionAsString, Optional<Integer> expectedWidth, Optional<Integer> expectedHeight, boolean expectedPreserveAspectRatio) throws SuggestionFormatException {
        assertScaleTo(test(suggestionAsString), expectedWidth, expectedHeight, expectedPreserveAspectRatio);
    }
    
    public static void testScaleFactor(String suggestionAsString, float expectedScaleFactor) throws SuggestionFormatException {
        assertScaleFactor(test(suggestionAsString), expectedScaleFactor);
    }
    
    public static ImageResizeSuggestion test(String suggestionAsString) throws SuggestionFormatException {
        return ImageResizeSuggestionFactory.create(suggestionAsString);
    }
   
    private static void assertScaleTo(ImageResizeSuggestion suggestion, Optional<Integer> expectedWidth, Optional<Integer> expectedHeight, boolean expectPreserveAspectRatio) {
        assertEquals( new ScaleToSuggestion(expectedWidth, expectedHeight, expectPreserveAspectRatio), suggestion );
    }
    
    private static void assertScaleFactor(ImageResizeSuggestion suggestion, float expectedScaleFactor) {
        assertEquals( new ScaleFactorSuggestion(expectedScaleFactor), suggestion );
    }
}
