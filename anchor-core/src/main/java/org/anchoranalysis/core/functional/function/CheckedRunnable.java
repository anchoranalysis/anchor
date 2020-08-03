package org.anchoranalysis.core.functional.function;

@FunctionalInterface
public interface CheckedRunnable<E extends Exception> {
    public void run() throws E;
}