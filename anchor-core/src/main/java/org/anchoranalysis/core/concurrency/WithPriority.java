package org.anchoranalysis.core.concurrency;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
@AllArgsConstructor public class WithPriority<T> implements Comparable<WithPriority<T>> {

    private T model;
    private boolean gpu;
    
    public T get() {
        return model;
    }
    
    public boolean isGPU() {
        return gpu;
    }

    /** Orders so that {@code hasPriority==true} has higher priority in queue to {@code hasPriority==false} */
    @Override
    public int compareTo(WithPriority<T> other) {
        return Boolean.compare(other.gpu, gpu);
    }
}