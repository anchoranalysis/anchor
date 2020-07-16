/* (C)2020 */
package org.anchoranalysis.core.index.container;

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

import java.util.ArrayList;
import java.util.Collections;
import javax.swing.event.EventListenerList;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.IIndexGetter;
import org.anchoranalysis.core.index.SimpleIndex;
import org.anchoranalysis.core.index.container.BoundChangeEvent.BoundType;

public class ArrayListContainer<T extends IIndexGetter & Comparable<IIndexGetter>>
        implements BoundedIndexContainer<T> {

    // The sorted elements
    private ArrayList<T> elements = new ArrayList<>();

    private EventListenerList eventListenerList = new EventListenerList();

    private int min = -1;

    private int max = -1;

    @Override
    public T get(int index) throws GetOperationFailedException {
        // We find the closest element that is equal to or less than our index
        return elements.get(getNearestElementIndexPrevious(index));
    }

    // We find the closest element that is equal to or less than our index
    private int getNearestElementIndexPrevious(int index) {

        // We find the closest element that is equal to or less than our index

        // Let's do a binary search tree for our item
        int res = Collections.binarySearch(elements, new SimpleIndex(index));

        if (res >= 0) {
            // We have an exact item
            return res;
        } else {
            // We do not have an exact item
            // instead res=(-(insertion point) - 1) is returned
            // i.e. where insertion point is index of the first element greater than the key
            //   or list.size(), if all elements in the list are less than the specified key.
            int insertionPoint = (res + 1) * -1;

            // We are looking below our minimum, so return the minimum
            if (insertionPoint == 0) {
                return 0;
            }

            // We are looking above our maximum, so return the maximum
            if (insertionPoint == elements.size()) {
                return elements.size() - 1;
            }

            // Otherwise return the greatest item less than our own
            return insertionPoint - 1;
        }
    }

    // We find the closest element that is equal to or less than our index
    private int getNearestElementIndexNext(int index) {

        // We find the closest element that is equal to or less than our index

        // Let's do a binary search tree for our item
        int res = Collections.binarySearch(elements, new SimpleIndex(index));

        if (res >= 0) {
            // We have an exact item
            return res;
        } else {
            // We do not have an exact item
            // instead res=(-(insertion point) - 1) is returned
            // i.e. where insertion point is index of the first element greater than the key
            //   or list.size(), if all elements in the list are less than the specified key.
            int insertionPoint = (res + 1) * -1;

            // We are looking below our minimum, so return the minimum
            if (insertionPoint == 0) {
                return 0;
            }

            // We are looking above our maximum, so return the maximum
            if (insertionPoint == elements.size()) {
                return elements.size() - 1;
            }

            // Otherwise return the greatest item less than our own
            return insertionPoint;
        }
    }

    // We are only allow add items in ascending order
    public void add(T item) {

        if (!elements.isEmpty()) {
            // Check that its greater than the final element
            T lastElement = elements.get(elements.size() - 1);

            if (lastElement.getIndex() > item.getIndex()) {
                throw new IllegalArgumentException(
                        "Adding item that is less than the last element");
            }
        }

        elements.add(item);

        if (min == -1) {
            setMinimumIndex(item.getIndex());
        }

        setMaximumIndex(item.getIndex());
    }

    @Override
    public int getMinimumIndex() {
        return this.min;
    }

    @Override
    public int getMaximumIndex() {
        return this.max;
    }

    @Override
    public void addBoundChangeListener(BoundChangeListener cl) {
        eventListenerList.add(BoundChangeListener.class, cl);
    }

    private void setMinimumIndex(int min) {

        this.min = min;

        for (BoundChangeListener cl : eventListenerList.getListeners(BoundChangeListener.class)) {
            cl.boundChanged(new BoundChangeEvent(this, BoundType.MINIMUM));
        }
    }

    public void setMaximumIndex(int max) {

        if (max <= getMaximumIndex()) {
            return;
        }
        this.max = max;

        for (BoundChangeListener cl : eventListenerList.getListeners(BoundChangeListener.class)) {
            cl.boundChanged(new BoundChangeEvent(this, BoundType.MAXIMUM));
        }
    }

    @Override
    public int nextIndex(int index) {
        int elementIndex = getNearestElementIndexNext(index + 1);
        return elements.get(elementIndex).getIndex();
    }

    @Override
    public int previousIndex(int index) {
        int elementIndex = getNearestElementIndexPrevious(index - 1);
        return elements.get(elementIndex).getIndex();
    }

    @Override
    public int previousEqualIndex(int index) {
        int elementIndex = getNearestElementIndexPrevious(index);
        return elements.get(elementIndex).getIndex();
    }
}
