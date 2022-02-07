package org.anchoranalysis.core.color;

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

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.functional.FunctionalIterate;

/**
 * A list of colors, each corresponding to a particular index position.
 *
 * <p>Note that a color may be duplicated in more than one position in the list. There is no check
 * on uniqueness.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor
public class ColorList implements ColorIndex, Iterable<RGBColor> {

    private List<RGBColor> list = new ArrayList<>();

    /**
     * Create for one or more colors of type {@link Color}.
     *
     * @param colors the colors
     */
    public ColorList(Color... colors) {
        this();
        Arrays.stream(colors).forEach(this::add);
    }

    /**
     * Create for one or more colors of type {@link RGBColor}.
     *
     * @param colors the colors
     */
    public ColorList(RGBColor... colors) {
        this();
        Arrays.stream(colors).forEach(this::add);
    }

    /**
     * Create for one or more colors of type {@link RGBColor}.
     *
     * @param colors the colors
     */
    public ColorList(Stream<RGBColor> colors) {
        this.list = colors.collect(Collectors.toList());
    }

    @Override
    public Iterator<RGBColor> iterator() {
        return list.iterator();
    }

    @Override
    public RGBColor get(int index) {
        return list.get(index);
    }

    @Override
    public int numberUniqueColors() {
        return size();
    }

    /**
     * Randomize the order of the colors in the list.
     *
     * <p>This is a mutable operation, that will change the relationship between colors and indices.
     */
    public void shuffle() {
        Collections.shuffle(list);
    }

    /**
     * Creates a new list, containing the same elements as the current list.
     *
     * @return a newly created list, containing the same elements as the current list, in the same
     *     order.
     */
    public ColorList shallowCopy() {

        ColorList out = new ColorList();

        // We copy all the marks
        for (RGBColor color : this) {
            out.add(color);
        }

        return out;
    }

    /**
     * Creates a new list, containing the duplicates of same elements as the current list.
     *
     * @return a newly created list, containing duplicated elements from the current list, in the
     *     same order.
     */
    public ColorList deepCopy() {

        ColorList out = new ColorList();

        // We copy all the marks
        for (RGBColor color : this) {
            out.add(color.duplicate());
        }

        return out;
    }

    /**
     * The number of elements (colors) in the list.
     *
     * @return the size of the list.
     */
    public int size() {
        return list.size();
    }

    /**
     * Appends a {@link Color} to the list.
     *
     * <p>The color is added to the end of the list, so existing relationships between colors and
     * indices remains unchanged.
     *
     * @param color the color to add
     */
    public void add(Color color) {
        add(new RGBColor(color));
    }

    /**
     * Like {@link #add(Color)} but adds a {@link RGBColor}.
     *
     * @param color the color to add
     */
    public void add(RGBColor color) {
        list.add(color);
    }

    /**
     * Inserts a {@link Color} into the list, at a particular index.
     *
     * <p>Note that this likely changes the relationship between existing colors and indices.
     *
     * @param index the index to insert the color at.
     * @param color the color to insert.
     */
    public void add(int index, RGBColor color) {
        list.add(index, color);
    }

    /**
     * Appends {@link Color}s to the list.
     *
     * <p>The colors are added to the end of the list, so existing relationships between colors and
     * indices remains unchanged.
     *
     * @param colors the colors to add
     */
    public void addAll(ColorList colors) {
        list.addAll(colors.list);
    }

    /**
     * Append multiple entries for the same color to the end of the list.
     *
     * @param color the color to add multiple times
     * @param numberTimes how many times to add it.
     */
    public void addMultiple(RGBColor color, int numberTimes) {
        FunctionalIterate.repeat(numberTimes, () -> add(color));
    }

    /**
     * Remove an element at a particular index.
     *
     * @param index the index to remove at.
     * @return the removed element
     */
    public RGBColor remove(int index) {
        return list.remove(index);
    }

    /**
     * Exposes the underlying {@link List} in the data-structure.
     *
     * @return the list used internally.
     */
    public List<RGBColor> asList() {
        return list;
    }
}
