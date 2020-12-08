package org.anchoranalysis.image.core.dimensions.resize.suggestion;

import static org.anchoranalysis.image.core.dimensions.resize.suggestion.ImageResizeSuggestionHelper.*;

import java.util.Optional;
import org.junit.Test;

/**
 * Tests {@link ImageResizeSuggestionFactory}.
 * 
 * @author Owen Feehan
 *
 */
public class ImageResizeSuggestionFactoryTest {

    /** Tests a suggestion with <b>both width and height</b> that <b>does not</b> preserve aspect ratio. */ 
    @Test
    public void testBothNoPreserve() throws SuggestionFormatException {
        testScaleTo("67x43", Optional.of(67), Optional.of(43), false);
    }
    
    /** Tests a suggestion with <b>both width and height</b> that <b>does</b> preserve aspect ratio. */
    @Test
    public void testBothPreserve() throws SuggestionFormatException {
        testScaleTo("31x92+", Optional.of(31), Optional.of(92), true);
    }
    
    /** Tests a suggestion with <b>width only</b> that <b>does</b> preserve aspect ratio. */
    @Test
    public void testWidthOnly() throws SuggestionFormatException {
        testScaleTo("31x", Optional.of(31), Optional.empty(), true);
    }
    
    /** Tests a suggestion with <b>width only</b> that <b>does</b> preserve aspect ratio - and contains a trailing plus. */
    @Test
    public void testWidthOnlyWithPlis() throws SuggestionFormatException {
        testScaleTo("31x+", Optional.of(31), Optional.empty(), true);
    }
    
    /** Tests a suggestion with <b>width only</b> that <b>does</b> preserve aspect ratio. */
    @Test
    public void testHeightOnly() throws SuggestionFormatException {
        testScaleTo("x78", Optional.empty(), Optional.of(78), true);
    }
    
    /** Tests a suggestion with <b>both width and height</b>, one of which is a zero, which is not allowed. */ 
    @Test(expected = SuggestionFormatException.class)
    public void testBothWithZero() throws SuggestionFormatException {
        test("67x0");
    }
    
    /** Tests a suggestion with <b>both width and height</b>, one of which is negative, which is not allowed. */ 
    @Test(expected = SuggestionFormatException.class)
    public void testBothWithNegative() throws SuggestionFormatException {
        test("-4x20");
    }
    
    /** Tests a <b>integer</b> scale-factor. */
    @Test
    public void testScaleFactorInteger() throws SuggestionFormatException {
        testScaleFactor("7", 7);
    }
    
    /** Tests a <b>floating-point</b> scale-factor. */
    @Test
    public void testScaleFactorFloat() throws SuggestionFormatException {
        testScaleFactor("0.33", 0.33f);
    }
    
    /** Tests a <b>zero-valued</b> scale-factor. */
    @Test(expected = SuggestionFormatException.class)
    public void testScaleFactorWithZero() throws SuggestionFormatException {
        test("0");
    }
    
    /** Tests a <b>negative-valued</b> scale-factor. */
    @Test(expected = SuggestionFormatException.class)
    public void testScaleFactorWithNegative() throws SuggestionFormatException {
        test("-8.9");
    }
    
    /** Tests an empty string. */
    @Test(expected = SuggestionFormatException.class)
    public void testEmpty() throws SuggestionFormatException {
        test("");
    }
    
    /** Tests non-numeric text. */
    @Test(expected = SuggestionFormatException.class)
    public void testNonNumericText() throws SuggestionFormatException {
        test("fooBar");
    }
}
