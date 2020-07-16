/* (C)2020 */
package org.anchoranalysis.core.error.friendly;

/*-
 * #%L
 * anchor-core
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import java.io.IOException;
import java.io.Writer;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

/**
 * A string split by newlines
 *
 * @author Owen Feehan
 */
class SplitString {

    private String[] lines;

    /**
     * Constructor: create from an unsplit string
     *
     * @param unsplitStr a string with 0 or more lines
     */
    public SplitString(String unsplitStr) {
        lines = splitString(unsplitStr);
    }

    /**
     * Splits by newline
     *
     * @param unsplitStr string with 0 or more newline characters
     * @return an array with lines of the string (minus newlines)
     */
    private static String[] splitString(String unsplitStr) {
        return unsplitStr.split("\\r?\\n");
    }

    /**
     * Find the maximum-length of any single line
     *
     * @return the maximum length of any single line
     */
    public int maxLength() {

        assert (lines.length > 0);

        int max = Integer.MIN_VALUE;

        for (int i = 0; i < lines.length; i++) {
            int curLen = lines[i].length();

            if (curLen > max) {
                max = curLen;
            }
        }

        return max;
    }

    /**
     * Constructs a nicely formatted version of each line for outputting of the format
     *
     * <p>prefix+line[index]+suffix
     *
     * <p>for each index in the array. prefix+line[index] are forced to be of constant size by
     * appending whitespace.
     *
     * @param writer where the lines are written to
     * @param prefix a prefix placed before every line
     * @param suffix a suffix placed at the end of only the first line
     * @param wrapLengthPrefixPlusLine If length of prefix+line is < wrapLengthPrefixPlusLine, white
     *     space is appended. If length is greater, word-wrapping occurs. -1 disables
     * @throws IOException if an I/O error occurs
     */
    public void appendNicelyWrappedLines(
            Writer writer, String prefix, String suffix, int wrapLengthPrefixPlusLine)
            throws IOException {

        boolean doWrapping = wrapLengthPrefixPlusLine != -1;

        for (int i = 0; i < lines.length; i++) {

            String line = lines[i];
            if (i == 0) {
                // We have a suffix on the first line
                // We only need the wrapping if we the suffix
                appendNicelyWrappedLine(
                        writer,
                        line,
                        prefix,
                        suffix,
                        doWrapping,
                        doWrapping,
                        wrapLengthPrefixPlusLine);
            } else {
                // new line
                writer.append(System.lineSeparator());

                // But no suffix on any other line
                appendNicelyWrappedLine(
                        writer, line, prefix, "", doWrapping, false, wrapLengthPrefixPlusLine);
            }
        }
    }

    private void appendNicelyWrappedLine(
            Writer writer,
            String line,
            String prefix,
            String suffix,
            boolean wrapTooLongLines,
            boolean padTooShortFirstLine,
            int fixedSizeForWrappingPadding)
            throws IOException {

        // The message combined with its prepend
        String combinedMessage = prefix + line;

        // If our line than our wrapLength, we need to split up into smaller individual chunks
        if (wrapTooLongLines && combinedMessage.length() > fixedSizeForWrappingPadding) {

            appendTooLargeLineBySplitting(
                    writer,
                    line,
                    prefix,
                    suffix,
                    fixedSizeForWrappingPadding,
                    padTooShortFirstLine);
            return;
        }

        writer.write(combinedMessage);

        if (padTooShortFirstLine && combinedMessage.length() < fixedSizeForWrappingPadding) {
            // Add whitespace to give table-effect
            int whiteSpaceLength = fixedSizeForWrappingPadding - combinedMessage.length();
            writer.append(StringUtils.repeat(" ", whiteSpaceLength));
        }

        writer.append(suffix);
    }

    private void appendTooLargeLineBySplitting(
            Writer writer,
            String line,
            String prefix,
            String suffix,
            int fixedSizeForWrappingPadding,
            boolean padTooShortFirstLine)
            throws IOException {
        // What's the max size of our line alone
        int wrapLengthLineAlone = fixedSizeForWrappingPadding - prefix.length();
        String maybeNewlined =
                WordUtils.wrap(line, wrapLengthLineAlone, System.lineSeparator(), true);

        String[] linesSplit = splitString(maybeNewlined);

        for (int i = 0; i < linesSplit.length; i++) {
            String s = linesSplit[i];

            // Only allow the real suffix to be written out for the first-line, and that's the only
            // time we need to pad
            if (i == 0) {
                appendNicelyWrappedLine(
                        writer,
                        s,
                        prefix,
                        suffix,
                        false,
                        padTooShortFirstLine,
                        fixedSizeForWrappingPadding);
            } else {
                // add a new line
                writer.append(System.lineSeparator());
                appendNicelyWrappedLine(
                        writer, s, prefix, "", false, false, fixedSizeForWrappingPadding);
            }
        }
    }
}
