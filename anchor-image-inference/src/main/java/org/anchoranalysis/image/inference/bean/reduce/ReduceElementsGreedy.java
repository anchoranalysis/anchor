/*-
 * #%L
 * anchor-plugin-image
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
package org.anchoranalysis.image.inference.bean.reduce;

import java.util.List;
import org.anchoranalysis.image.inference.bean.segment.reduce.ReduceElements;
import org.anchoranalysis.image.inference.segment.LabelledWithConfidence;
import org.anchoranalysis.image.inference.segment.ReductionOutcome;
import org.anchoranalysis.image.voxel.object.ObjectMask;

/**
 * Reduces the number or spatial-extent of elements by favouring higher-confidence elements over
 * lower-confidence elements.
 *
 * <p>Elements can be removed entirely, or can have other operations executed (e.g. removing
 * overlapping voxels).
 *
 * <ol>
 *   <li>Select highest-confidence proposal, remove from list, add to output.
 *   <li>Compare this proposal with remaining proposals, and process any overlapping elements
 *       (removing or otherwise altering).
 *   <li>If there are remaining proposals in the queue, goto Step 1.
 * </ol>
 *
 * @author Owen Feehan
 */
public abstract class ReduceElementsGreedy extends ReduceElements<ObjectMask> {

    @Override
    public ReductionOutcome<LabelledWithConfidence<ObjectMask>> reduce(
            List<LabelledWithConfidence<ObjectMask>> elements) {

        /** Tracks which objects overlap with other objects, updated as merges/deletions occur. */
        ReduceObjectsGraph graph = new ReduceObjectsGraph(elements);

        ReductionOutcome<LabelledWithConfidence<ObjectMask>> outcome = new ReductionOutcome<>();

        while (!graph.isEmpty()) {

            ObjectForReduction highestConfidence = graph.peek();

            if (!findIntersectionsAndProcess(highestConfidence, graph)) {
                // Then no intersections exist, so we can remove the element fron te graph
                addVertexToOutcome(graph.poll(), outcome);
            }
        }

        return outcome;
    }

    /**
     * Whether to include another (possibly-overlapping with {@code source}) element in processing?
     *
     * @param source the element we are currently processing, for which we search for possibly
     *     overlapping element
     * @param other another object which we are considering to include in processing (in conjunction
     *     with {@code source}).
     * @return true iff {@code other} should be included in processing.
     */
    protected abstract boolean shouldObjectsBeProcessed(ObjectMask source, ObjectMask other);

    /**
     * Processes two objects.
     *
     * @param source the element that is being processed as the <i>source</i>, the object with
     *     higher confidence.
     * @param overlapping the element that is deemed to overlap with the {@code sourceElement} about
     *     which a decision is to be made.
     * @param graph the graph relating objects to each other, and determining the priority for
     *     reduction.
     * @return true if the {@code source} object was altered during processing, false if it was not.
     */
    protected abstract boolean processObjects(
            ObjectForReduction source, ObjectForReduction overlapping, ReduceObjectsGraph graph);

    /**
     * Finds any overlapping objects and processes them accordingly (possibly removing or merging
     * them).
     *
     * @return true if the proposal was altered during processing, false otherwise.
     */
    private boolean findIntersectionsAndProcess(
            ObjectForReduction proposal, ReduceObjectsGraph graph) {

        List<ObjectForReduction> intersecting = graph.adjacentVerticesOutgoing(proposal);
        for (ObjectForReduction other : intersecting) {

            if (shouldObjectsBeProcessed(proposal.getElement(), other.getElement())
                    && processObjects(proposal, other, graph)) {
                // When a change has occurred to the proposal, then we abandon processing of
                // overlaps and requery the priority-queue (in case things have changed).
                return true;
            }
        }
        return false;
    }

    /**
     * Adds a vertex so that it exists in the {@link ReductionOutcome}, either retained, or
     * newly-added.
     */
    private static void addVertexToOutcome(
            ObjectForReduction vertex,
            ReductionOutcome<LabelledWithConfidence<ObjectMask>> outcome) {
        if (vertex.getIndex() == ObjectForReduction.NEWLY_ADDED) {
            outcome.addNewlyAdded(vertex.getLabelled());
        } else {
            outcome.addIndexToRetain(vertex.getIndex());
        }
    }
}
