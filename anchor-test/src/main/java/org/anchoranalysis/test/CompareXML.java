/*-
 * #%L
 * anchor-test
 * %%
 * Copyright (C) 2010 - 2021 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
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
