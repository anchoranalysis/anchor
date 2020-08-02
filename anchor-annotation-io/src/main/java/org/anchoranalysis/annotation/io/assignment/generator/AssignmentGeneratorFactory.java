/*-
 * #%L
 * anchor-annotation-io
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

package org.anchoranalysis.annotation.io.assignment.generator;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.annotation.io.assignment.Assignment;
import org.anchoranalysis.image.stack.DisplayStack;
import io.vavr.Tuple2;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AssignmentGeneratorFactory {

    public static AssignmentGenerator createAssignmentGenerator(
            DisplayStack background,
            Assignment assignment,
            ColorPool colorPool,
            boolean useMIP,
            Tuple2<String, String> names,
            int outlineWidth,
            boolean appendNumberBrackets) {

        AssignmentGenerator generator =
                new AssignmentGenerator(background, assignment, colorPool, useMIP);

        setupNames(generator, assignment, appendNumberBrackets, names);

        generator.setOutlineWidth(outlineWidth);
        return generator;
    }

    private static void setupNames(
            AssignmentGenerator generator,
            Assignment assignment,
            boolean appendNumberBrackets,
            Tuple2<String, String> names) {
        generator.setLeftName(
                maybeAppendNumber(appendNumberBrackets, names._1(), assignment, true));
        generator.setRightName(
                maybeAppendNumber(appendNumberBrackets, names._2(), assignment, false));
    }

    private static String maybeAppendNumber(
            boolean doAppend, String mainString, Assignment assignment, boolean left) {
        if (doAppend) {
            return String.format("%s (%d)", mainString, assignment.numUnassigned(left));
        } else {
            return mainString;
        }
    }
}
