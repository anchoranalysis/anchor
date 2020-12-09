package org.anchoranalysis.image.core.dimensions.resize.suggestion;

import java.util.Optional;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.functional.OptionalUtilities;
import lombok.AllArgsConstructor;

/**
 * Extracts a {@link ScaleToSuggestion} or {@link ScaleToSuggestion} from an array of strings.
 * 
 * @author Owen Feehan
  */
@AllArgsConstructor
class SuggestionFromArray {

    private final String[] array;
    
    /**
     * Extracts a suggestion with <b>both</b> a width and a height.
     * 
     * @param preserveAspectRatio whether to preserve the aspect ratio
     * @return the created suggestion
     * @throws SuggestionFormatException if any array elements is not a positive integer
     */
    public ScaleToSuggestion extractBoth(boolean preserveAspectRatio) throws SuggestionFormatException {
        return extract(Optional.of(0), Optional.of(1), preserveAspectRatio);
    }
        
    /**
     * Extracts a suggestion with <b>either</b> a width or a height.
     * 
     * @param multiplexWidth if true, the width is extracted, otherwise the height
     * @return the created suggestion
     * @throws SuggestionFormatException if any array elements is not a positive integer
     */
    public ScaleToSuggestion extractOne(boolean multiplexWidth) throws SuggestionFormatException {
        Optional<Integer> widthPosition = OptionalUtilities.createFromFlag(multiplexWidth, 0);
        Optional<Integer> heightPosition = OptionalUtilities.createFromFlag(!multiplexWidth, 0);
        return extract(widthPosition, heightPosition, true);
    }
    
    public ScaleFactorSuggestion extractScaleFactor() throws SuggestionFormatException {
        return new ScaleFactorSuggestion( extractFloat(0) );
    }
    
    /** Creates a {@link ScaleToSuggestion} by extracting particular positions for width and height (or not at all). */
    private ScaleToSuggestion extract(Optional<Integer> widthPosition, Optional<Integer> heightPosition, boolean preserveAspectRatio) throws SuggestionFormatException {
        try {
            return new ScaleToSuggestion( extractIntegerOptional(widthPosition), extractIntegerOptional(heightPosition), preserveAspectRatio);
        } catch (CreateException e) {
            throw new SuggestionFormatException("Neither a width nor a height is specified");
        }
    }
    
    /** Like {@link #extractInteger(int)} but takes an {@link Optional} as an parameter. */
    private Optional<Integer> extractIntegerOptional(Optional<Integer> position) throws SuggestionFormatException {
        return OptionalUtilities.map(position, this::extractInteger);
    }
    
    /** Extracts an integer at a particular position in the array. */
    private int extractInteger(int position) throws SuggestionFormatException {
        String string = array[position]; // NOSONAR
        int value = Integer.parseInt(string);
        checkValue(value);
        return value;  
    }
    
    /** Extracts a float at a particular position in the array. */
    private float extractFloat(int position) throws SuggestionFormatException {
        String string = array[position]; // NOSONAR
        float value = Float.parseFloat(string);
        checkValue(value);
        return value;  
    }
    
    /** Checks that value is positive. */
    private static void checkValue(float value) throws SuggestionFormatException {
        if (value == 0) {
            throw new SuggestionFormatException("An image resize suggestion must not specify a 0 as width or height");
        }
        if (value < 0) {
            throw new SuggestionFormatException("An image resize suggestion must not specify a negative-number as width or height");
        }
    }
}
