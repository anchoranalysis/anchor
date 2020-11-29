package org.anchoranalysis.core.index.bounded;

/*
 * #%L
 * anchor-core
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
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

import javax.swing.event.EventListenerList;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.bounded.BoundChangeEvent.BoundType;

public class SingleContainer<T> implements BoundedIndexContainer<T> {

    private T item = null;
    private int index;
    private boolean loopAround = false;

    private EventListenerList eventListenerList = new EventListenerList();

    public SingleContainer(boolean loopAround) {
        this.loopAround = loopAround;
    }

    public SingleContainer(T item, int index, boolean loopAround) {
        setItem(item, index);
        this.loopAround = loopAround;
    }

    // Index is irrelevant, we always return the same item
    @Override
    public T get(int index) throws GetOperationFailedException {
        return item;
    }

    public T getItem() {
        return item;
    }

    public void setItem(T item, int index) {

        // If the item is the same as the existing item, there is no change, and no stateChange
        //  events are triggered
        if (item == this.item && index == this.index) {
            return;
        }

        this.item = item;
        this.index = index;

        for (BoundChangeListener cl : eventListenerList.getListeners(BoundChangeListener.class)) {
            cl.boundChanged(new BoundChangeEvent(this, BoundType.MINIMUM));
            cl.boundChanged(new BoundChangeEvent(this, BoundType.MAXIMUM));
        }
    }

    @Override
    public void addBoundChangeListener(BoundChangeListener cl) {
        eventListenerList.add(BoundChangeListener.class, cl);
    }

    @Override
    public int getMinimumIndex() {
        return index;
    }

    @Override
    public int getMaximumIndex() { // NOSONAR
        return index;
    }

    @Override
    public int nextIndex(int index) {
        if (loopAround) {
            return index;
        } else {
            return -1;
        }
    }

    @Override
    public int previousIndex(int index) {
        if (loopAround) {
            return index;
        } else {
            return -1;
        }
    }

    @Override
    public int previousEqualIndex(int index) {
        if (index == this.index) {
            return index;
        } else {
            if (loopAround) {
                return index;
            } else {
                return -1;
            }
        }
    }
}
