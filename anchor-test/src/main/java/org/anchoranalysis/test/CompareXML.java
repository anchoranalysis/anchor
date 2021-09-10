package org.anchoranalysis.test;

import org.w3c.dom.Document;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.Diff;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Compares two XML documents to see if they are equal.
 * 
 * @author Owen Feehan
 *
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class CompareXML {

    /**
     * Are two XML-documents equal in content, ignoring whitespace and comments?
     *
     * <p>Note that both objects are normalized during the check, and their state changes
     * permanently.
     *
     * @param document1 first document
     * @param document2 second document
     * @return true if their contents match, false otherwise
     */
    public static boolean areDocumentsEqual(Document document1, Document document2) {
        return areBuildersEqual(Input.fromDocument(document1), Input.fromDocument(document2));
    }
        
    /** Are two {@link Input.Builder}s equal in content, ignoring whitespace and comments? */
    private static boolean areBuildersEqual(Input.Builder builder1, Input.Builder builder2) {
        Diff difference =
                DiffBuilder.compare(builder1)
                        .ignoreWhitespace()
                        .ignoreComments()
                        .withTest(builder2)
                        .build();
        return !difference.hasDifferences();
    }
}
