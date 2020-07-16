/* (C)2020 */
package org.anchoranalysis.core.index;

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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.anchoranalysis.core.property.change.PropertyValueChangeEvent;
import org.anchoranalysis.core.property.change.PropertyValueChangeListener;
import org.apache.commons.lang.ArrayUtils;

public class IndicesSelection {

    private HashSet<Integer> currentSelectionSet = new HashSet<>();
    private int[] currentSelection;
    private List<PropertyValueChangeListener<IntArray>> eventListeners = new ArrayList<>();
    private boolean enabled = true;

    public IndicesSelection() {
        this.currentSelection = new int[] {};
    }

    public IndicesSelection(int[] ids) {
        setCurrentSelection(ids);
    }

    public IndicesSelection(Collection<Integer> ids) {
        setCurrentSelection(ids);
    }

    // Does not copy id array
    public synchronized void setCurrentSelection(int[] ids) {

        if (!enabled) {
            return;
        }

        if (equalsInt(ids)) {
            return;
        }

        currentSelection = ArrayUtils.clone(ids);

        currentSelectionSet.clear();
        for (int i : ids) {
            currentSelectionSet.add(i);
        }

        onIndicesChanged();
    }

    public synchronized void setCurrentSelection(Collection<Integer> ids) {

        if (!enabled) {
            return;
        }

        int[] idArr = IndicesUtils.intArrayFromCollection(ids);
        setCurrentSelection(idArr);
    }

    /**
     * Equals an array of integer ids?
     *
     * @param ids an array of integers to compare
     * @return true of the id-numbers exactly (including order) match the current-selection
     */
    public boolean equalsInt(int[] ids) {
        return Arrays.equals(ids, currentSelection);
    }

    public synchronized int[] getCurrentSelection() {
        return currentSelection;
    }

    public synchronized boolean contains(int i) {
        return currentSelectionSet.contains(i);
    }

    public synchronized boolean anyFoundIn(int[] idArr) {

        HashSet<Integer> selectionSet = new HashSet<>();
        for (int i : idArr) {
            selectionSet.add(i);
        }

        for (int id : currentSelection) {

            if (selectionSet.contains(id)) {
                return true;
            }
        }
        return false;
    }

    public synchronized boolean allFoundIn(int[] idArr) {

        HashSet<Integer> selectionSet = new HashSet<>();
        for (int i : idArr) {
            selectionSet.add(i);
        }

        for (int id : currentSelection) {

            if (!selectionSet.contains(id)) {
                return false;
            }
        }
        return true;
    }

    private void onIndicesChanged() {

        if (!enabled) {
            return;
        }

        for (PropertyValueChangeListener<IntArray> l : eventListeners) {
            l.propertyValueChanged(
                    new PropertyValueChangeEvent<>(this, new IntArray(currentSelection), false));
        }
    }

    public synchronized boolean isEnabled() {
        return enabled;
    }

    public synchronized void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEmpty() {
        return currentSelection.length == 0;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder("[");

        boolean first = true;
        for (int id : currentSelection) {

            if (first) {
                first = false;

            } else {
                sb.append(", ");
            }

            sb.append(Integer.toString(id));
        }

        sb.append("]");
        return sb.toString();
    }

    private static class IntegerHistogram {

        private HashMap<Integer, Integer> map = new HashMap<>();

        public void add(int id) {

            Integer i = map.get(id);
            if (i == null) {
                i = Integer.valueOf(0);
            }
            map.put(id, ++i);
        }

        public int get(int id) {
            return map.get(id);
        }

        public Set<Integer> keys() {
            return map.keySet();
        }
    }

    // 1 occurrences, include in output list
    // 2 or more occurrences, do not include in output list
    public static int[] mergedList(int[] list1, int[] list2) {

        IntegerHistogram intHist = new IntegerHistogram();
        for (int id : list1) {
            intHist.add(id);
        }

        for (int id : list2) {
            intHist.add(id);
        }

        List<Integer> list = new ArrayList<>();
        for (int id : intHist.keys()) {

            if (intHist.get(id) == 1) {
                list.add(id);
            }
        }

        return IndicesUtils.intArrayFromCollection(list);
    }

    public void addListener(PropertyValueChangeListener<IntArray> l) {
        eventListeners.add(l);
    }

    public void removeListener(PropertyValueChangeListener<IntArray> l) {
        eventListeners.remove(l);
    }
}
