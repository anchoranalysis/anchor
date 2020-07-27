package org.anchoranalysis.feature.calc;

import org.anchoranalysis.core.error.friendly.AnchorFriendlyCheckedException;

/**
 * A feature-calculation exception that occurs in a particular named feature, or else a general message of failure.
 * 
 * @author Owen Feehan
 *
 */
public class NamedFeatureCalculationException extends AnchorFriendlyCheckedException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /** The message without any key identifier */
    private final String messageWithoutKey;
    
    /**
     * Constructor - when a general failure message that doesn't pertain to any particular feature.
     * 
     * @param message
     */
    public NamedFeatureCalculationException(String message) {
        super(message);
        this.messageWithoutKey = message;
    }
    
    /**
     * Constructor - when a calculation error occurs associated with the only pertinent feature.
     * 
     * @param message
     */
    public NamedFeatureCalculationException(Exception exception) {
        this(exception.toString());
    }
    
    /**
     * Constructor - when a particular named feature failed in some way
     * 
     * @param featureName a name to describe the feature whose calculation failed
     * @param message the reason for failure
     */
    public NamedFeatureCalculationException(String featureName, String message) {
        super(
          String.format(
             "An error occurred when calculating feature '%s': %s",
             featureName,
             message
          )      
        );
        messageWithoutKey = message;
    }
    
    /** Ignores the name in the exception */
    public FeatureCalculationException dropKey() {
        return new FeatureCalculationException(messageWithoutKey);
    }
}
