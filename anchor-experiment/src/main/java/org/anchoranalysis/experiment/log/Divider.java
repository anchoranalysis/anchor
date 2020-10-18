/*-
 * #%L
 * anchor-experiment
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.experiment.log;

import com.google.common.base.Strings;

/**
 * Creates a line of text that span a certain width with a repeated character, optionally containing
 * a label.
 *
 * @author Owen Feehan
 */
public class Divider {

    private static final int CHARACTERS_IN_BANNER = 80;

    /** The character that is repeated, as a string. */
    private final String characterToRepeat;

    /** A line of repeated characters that doesn't involve a label. */
    private final String withoutLabel;

    /** A banner using dashes. */
    public Divider() {
        this('-');
    }

    /**
     * A banner using a particular repeated character.
     *
     * @param characterToRepeat the character that is repeated
     */
    public Divider(char characterToRepeat) {
        this.characterToRepeat = String.valueOf(characterToRepeat);
        this.withoutLabel = repeatedCharacter(CHARACTERS_IN_BANNER);
    }

    /**
     * Creates a line of repeated dashes.
     *
     * @return a string of repeated dashes to total 80 chracters.
     */
    public String withoutLabel() {
        return withoutLabel;
    }

    /**
     * Creates a line of repeated dashes with a centered label of text.
     *
     * <p>The label of text is padded by one character of whitespace on either side.
     *
     * @param label the label
     * @return a string of repreated dashes including a label to total 80 characters
     */
    public String withLabel(String label) {
        int numberNeeded = CHARACTERS_IN_BANNER - label.length() - 2;
        int numberFirst = numberNeeded / 2;
        int numberSecond = numberNeeded - numberFirst;

        StringBuilder out = new StringBuilder();
        out.append(repeatedCharacter(numberFirst));
        out.append(" ");
        out.append(label);
        out.append(" ");
        out.append(repeatedCharacter(numberSecond));
        return out.toString();
    }

    private String repeatedCharacter(int numberRepeats) {
        return Strings.repeat(characterToRepeat, numberRepeats);
    }
}
