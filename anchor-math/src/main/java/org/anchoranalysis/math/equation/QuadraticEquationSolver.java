/* (C)2020 */
package org.anchoranalysis.math.equation;

import org.anchoranalysis.core.error.OperationFailedException;

/** Solves a Quadratic Equation by finding non-complex roots */
public class QuadraticEquationSolver {

    private QuadraticEquationSolver() {}

    /** Roots (solution) of a quadratic equation */
    public static class QuadraticRoots {
        private double root1;
        private double root2;

        public QuadraticRoots(double root1, double root2) {
            super();
            this.root1 = root1;
            this.root2 = root2;
        }

        public double getRoot1() {
            return root1;
        }

        public void setRoot1(double root1) {
            this.root1 = root1;
        }

        public double getRoot2() {
            return root2;
        }

        public void setRoot2(double root2) {
            this.root2 = root2;
        }
    }

    /**
     * Solves a quadratic equation in form x^2 + b^x + c = 0
     *
     * <p>Assumes no complex roots
     *
     * @param a coefficient for x^2
     * @param b coefficient for x
     * @param c coefficient for constant term
     * @return simple roots
     * @throws OperationFailedException if the solution requires complex roots
     */
    public static QuadraticRoots solveQuadraticEquation(double a, double b, double c)
            throws OperationFailedException {

        double common = b * b - 4 * a * c;

        // As sometimes we get minor presumably "round-off" error we adjust
        if (common > -1e-3 && common < 0) {
            common = 0;
        }

        if (common < 0) {
            throw new OperationFailedException(
                    String.format("Complex roots returned: common=%f", common));
        }

        double commonSqrt = Math.sqrt(common);
        double div = 2 * a;

        double root1 = (-b + commonSqrt) / div;
        double root2 = (-b - commonSqrt) / div;

        return new QuadraticRoots(root1, root2);
    }
}
