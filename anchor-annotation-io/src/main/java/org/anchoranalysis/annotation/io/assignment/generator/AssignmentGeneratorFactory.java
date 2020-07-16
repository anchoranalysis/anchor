/* (C)2020 */
package org.anchoranalysis.annotation.io.assignment.generator;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.annotation.io.assignment.Assignment;
import org.anchoranalysis.image.stack.DisplayStack;
import org.apache.commons.lang3.tuple.Pair;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AssignmentGeneratorFactory {

    public static AssignmentGenerator createAssignmentGenerator(
            DisplayStack background,
            Assignment assignment,
            ColorPool colorPool,
            boolean useMIP,
            Pair<String, String> names,
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
            Pair<String, String> names) {
        generator.setLeftName(
                maybeAppendNumber(appendNumberBrackets, names.getLeft(), assignment, true));
        generator.setRightName(
                maybeAppendNumber(appendNumberBrackets, names.getRight(), assignment, false));
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
