package org.anchoranalysis.core.log.error;

/**
 * Logs error messages into a {@link String} via {@link StringBuilder}.
 *
 * @author Owen Feehan
 */
public class ErrorReporterIntoString extends ErrorReporterBase {

    /**
     * Creates for a particular {@link StringBuilder}.
     *
     * @param builder the builder that is appended to with messages.
     */
    public ErrorReporterIntoString(StringBuilder builder) {
        super(message -> appendWithLineSeperator(builder, message));
    }

    /** Appends a message the builder with an additional line-separator character. */
    private static void appendWithLineSeperator(StringBuilder builder, String message) {
        builder.append(message);
        builder.append(System.lineSeparator());
    }
}
