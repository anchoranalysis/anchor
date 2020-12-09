package org.anchoranalysis.image.core.dimensions.resize.suggestion;

import static org.junit.Assert.assertEquals;
import java.util.Optional;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.friendly.AnchorFriendlyRuntimeException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
class ImageResizeSuggestionHelper {
    
    public static void testScaleTo(String suggestionAsString, Optional<Integer> expectedWidth, Optional<Integer> expectedHeight, boolean expectedPreserveAspectRatio) throws SuggestionFormatException {
        try {
            assertScaleTo(test(suggestionAsString), expectedWidth, expectedHeight, expectedPreserveAspectRatio);
        } catch (CreateException e) {
            throw new AnchorFriendlyRuntimeException(e);
        }
    }
    
    public static void testScaleFactor(String suggestionAsString, float expectedScaleFactor) throws SuggestionFormatException {
        assertScaleFactor(test(suggestionAsString), expectedScaleFactor);
    }
    
    public static ImageResizeSuggestion test(String suggestionAsString) throws SuggestionFormatException {
        return ImageResizeSuggestionFactory.create(suggestionAsString);
    }
   
    private static void assertScaleTo(ImageResizeSuggestion suggestion, Optional<Integer> expectedWidth, Optional<Integer> expectedHeight, boolean expectPreserveAspectRatio) throws CreateException {
        assertEquals( new ScaleToSuggestion(expectedWidth, expectedHeight, expectPreserveAspectRatio), suggestion );
    }
    
    private static void assertScaleFactor(ImageResizeSuggestion suggestion, float expectedScaleFactor) {
        assertEquals( new ScaleFactorSuggestion(expectedScaleFactor), suggestion );
    }
}
