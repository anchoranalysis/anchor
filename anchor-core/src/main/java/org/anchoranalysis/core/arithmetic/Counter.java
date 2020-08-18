package org.anchoranalysis.core.arithmetic;

import lombok.Getter;

/**
 * A simple counter that increments
 * 
 * <p>This is useful when an object on the heap is required to track counting
 * 
 * @author Owen Feehan
 *
 */
public class Counter {

    @Getter private int count = 0;
    
    public void increment() {
        count++;
    }
    
    public void decrement() {
        count--;
    }
}
