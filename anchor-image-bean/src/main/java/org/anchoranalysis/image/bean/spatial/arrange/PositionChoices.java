package org.anchoranalysis.image.bean.spatial.arrange;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.ToIntFunction;

import org.anchoranalysis.image.bean.nonbean.spatial.arrange.ArrangeStackException;
import org.anchoranalysis.image.bean.nonbean.spatial.arrange.StackArrangement;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.spatial.box.Extent;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

/** 
 * The choices of text that may describe the position along a particular axis.
 *
 * <p>All choices are case insensitive.
 */
@AllArgsConstructor @RequiredArgsConstructor class PositionChoices {
	
	// START ARGUMENTS
	/** The text to describe the <b>minimum</b> position along the axis. */
	private final String textMin;
	
	/** The text to describe the <b>center</b> position along the axis. */
	private final String textCenter;
	
	/** The text to describe the <b>maximum</b> position along the axis. */
	private final String textMax;
	
	/** An additional alternative text to describe the <b>minimum</b> position along the axis, if one exists. */
	private Optional<String> textMinAlternative = Optional.empty();
	// END ARGUMENTS
	
	/**
	 * Calculates the position on a particular axis to locate the overlay.
	 * 
	 * @param fieldName the name of the field where the position was specified.
	 * @param fieldValue the value of the field.
	 * @param extractValue extracts a value on the particular axis from a {@link Extent}.
	 * @param arrangement the stacks to align with.
	 * @param overlaySize the size of the overlay (what is being aligned).
	 * @return the minimum corner on the particular axis to locate the overlay.
	 * @throws ArrangeStackException if an invalid value for a field was used.
	 */
    public int position(String fieldName, String fieldValue, ToIntFunction<Extent> extractValue, StackArrangement arrangement, Dimensions overlaySize) throws ArrangeStackException {

        if (fieldValue.equalsIgnoreCase(textMin) || matchesAlternative(fieldValue)) {
            return 0;
        } else if (fieldValue.equalsIgnoreCase(textCenter)) {
        	return (extractValue.applyAsInt(arrangement.extent()) - extractValue.applyAsInt(overlaySize.extent())) / 2;
        } else if (fieldValue.equalsIgnoreCase(textMax)) {
            return extractValue.applyAsInt(arrangement.extent()) - extractValue.applyAsInt(overlaySize.extent());
        } else {
        	String describeAllChoices = String.join(", ", allChoices());
            throw new ArrangeStackException( String.format("The string '%s' is an invalid value for field %s. Accept values are: %s", fieldValue, fieldName, describeAllChoices));
        }
    }
    
    /** All possible textual choices to describe a position. */
    private List<String> allChoices() {
    	if (textMinAlternative.isPresent()) {
    		return Arrays.asList(textMin, textCenter, textMax, textMinAlternative.get());
    	} else {
    		return Arrays.asList(textMin, textCenter, textMax);
    	}
    }
    
    /** If a alternative minimum text exists <b>and</b> it is identical to {@code alignText}. */
    private boolean matchesAlternative(String fieldValue) {
    	if (textMinAlternative.isPresent()) {
    		return fieldValue.equalsIgnoreCase(textMinAlternative.get());
    	} else {
    		return false;
    	}
    }
}