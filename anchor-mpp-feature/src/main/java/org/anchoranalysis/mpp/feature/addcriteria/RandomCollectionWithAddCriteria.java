/*-
 * #%L
 * anchor-mpp-feature
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

package org.anchoranalysis.mpp.feature.addcriteria;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.InitializeException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.exception.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.core.functional.OptionalUtilities;
import org.anchoranalysis.core.graph.GraphWithPayload;
import org.anchoranalysis.core.graph.TypedEdge;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.random.RandomNumberGenerator;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.calculate.FeatureInitialization;
import org.anchoranalysis.feature.energy.EnergyStack;
import org.anchoranalysis.feature.session.FeatureSession;
import org.anchoranalysis.feature.session.calculator.multi.FeatureCalculatorMulti;
import org.anchoranalysis.feature.shared.SharedFeatures;
import org.anchoranalysis.mpp.feature.input.FeatureInputPairMemo;
import org.anchoranalysis.mpp.feature.mark.MemoList;
import org.anchoranalysis.mpp.mark.Mark;
import org.anchoranalysis.mpp.mark.MarkCollection;
import org.anchoranalysis.mpp.mark.UpdateMarkSetException;
import org.anchoranalysis.mpp.mark.voxelized.memo.MemoForIndex;
import org.anchoranalysis.mpp.mark.voxelized.memo.VoxelizedMarkMemo;
import org.anchoranalysis.mpp.pair.RandomCollection;

/**
 * An implementation of a {@link RandomCollection} that uses {@link AddCriteria}
 *
 * <p>Note: this is not a valid-bean on its own, as there is no default public constructor So if we
 * use it in BeanXML, we must sub-class it with such a constructor. However, it is a valid
 * (non-bean) class on its own.
 *
 * <p>However, it is useful to keep the class non-abstract, as when the copy methods (shallowCopy,
 * deepCopy etc.) are called, we can instantiate an instance of this class
 *
 * @author Owen Feehan
 * @param <T> type of the pair
 */
public class RandomCollectionWithAddCriteria<T> extends RandomCollection<T> {

    private GraphWithPayload<Mark, T> graph;

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private Class<?> pairTypeClass;

    @BeanField @Getter @Setter private AddCriteria<T> addCriteria;
    // END BEAN PROPERTIES

    private boolean hasInit = false;
    private EnergyStack energyStack;
    private Logger logger;
    private SharedFeatures sharedFeatures;

    public RandomCollectionWithAddCriteria(Class<?> pairTypeClass) {
        this.pairTypeClass = pairTypeClass;
        graph = new GraphWithPayload<>(true);
    }

    public RandomCollectionWithAddCriteria<T> shallowCopy() {
        RandomCollectionWithAddCriteria<T> out =
                new RandomCollectionWithAddCriteria<>(this.pairTypeClass);
        out.graph = this.graph.shallowCopy();
        out.addCriteria = this.addCriteria;
        out.energyStack = this.energyStack;
        out.hasInit = this.hasInit;
        out.logger = this.logger;
        out.sharedFeatures = this.sharedFeatures;
        return out;
    }

    public RandomCollectionWithAddCriteria<T> deepCopy() {
        RandomCollectionWithAddCriteria<T> out =
                new RandomCollectionWithAddCriteria<>(this.pairTypeClass);
        out.graph = this.graph.shallowCopy();
        out.addCriteria = this.addCriteria;
        out.energyStack = energyStack;
        out.hasInit = this.hasInit;
        out.logger = this.logger;
        out.sharedFeatures = this.sharedFeatures;
        return out;
    }

    @Override
    public void initUpdatableMarks(
            MemoForIndex marks, EnergyStack stack, Logger logger, SharedFeatures sharedFeatures)
            throws InitializeException {
        this.logger = logger;
        this.sharedFeatures = sharedFeatures;

        try {
            this.graph = new GraphWithPayload<>(true);

            // Add all marks as vertices
            for (int i = 0; i < marks.size(); i++) {
                graph.addVertex(marks.getMemoForIndex(i).getMark());
            }

            Optional<FeatureList<FeatureInputPairMemo>> features =
                    addCriteria.orderedListOfFeatures();

            Optional<FeatureCalculatorMulti<FeatureInputPairMemo>> session =
                    OptionalUtilities.map(
                            features,
                            f ->
                                    FeatureSession.with(
                                            f,
                                            new FeatureInitialization(stack.getParameters()),
                                            sharedFeatures,
                                            logger));

            initGraph(marks, stack, session);

            this.hasInit = true;
            this.energyStack = stack;

        } catch (CreateException e) {
            throw new InitializeException(e);
        }
    }

    @Override
    public void add(MemoForIndex marksExisting, VoxelizedMarkMemo newMark)
            throws UpdateMarkSetException {
        checkInit();
        try {
            this.graph.addVertex(newMark.getMark());
            calculatePairsForMark(marksExisting, newMark, energyStack);
        } catch (CreateException e) {
            throw new UpdateMarkSetException(e);
        }
    }

    @Override
    public void exchange(
            MemoForIndex memo,
            VoxelizedMarkMemo oldMark,
            int indexOldMark,
            VoxelizedMarkMemo newMark)
            throws UpdateMarkSetException {
        checkInit();

        // We need to make a copy of the list, so we can perform the removal operation after the add
        MemoList memoList = new MemoList();
        memoList.addAll(memo);

        remove(memo, oldMark);

        memoList.remove(indexOldMark);

        add(memoList, newMark);
    }

    @Override
    public void remove(MemoForIndex marksExisting, VoxelizedMarkMemo mark)
            throws UpdateMarkSetException {
        checkInit();
        try {
            this.graph.removeVertex(mark.getMark());
        } catch (OperationFailedException e) {
            throw new UpdateMarkSetException(e);
        }
    }

    // START DELEGATES

    // Each pair appears twice
    public Set<T> createPairsUnique() {
        HashSet<T> setOut = new HashSet<>();
        for (TypedEdge<Mark, T> pair : pairsMaybeDuplicates()) {
            setOut.add(pair.getPayload());
        }
        return setOut;
    }

    public Collection<TypedEdge<Mark, T>> getPairsFor(Mark mark) {
        return graph.outgoingEdgesFor(mark);
    }

    public boolean containsMark(Mark mark) {
        return graph.containsVertex(mark);
    }

    public Collection<Mark> getMarks() {
        return graph.vertices();
    }

    public boolean isMarksSpan(MarkCollection marks) {

        for (int i = 0; i < marks.size(); i++) {
            if (!containsMark(marks.get(i))) {
                return false;
            }
        }

        for (Mark m : getMarks()) {
            if (marks.indexOf(m) == -1) {
                return false;
            }
        }

        return true;
    }

    // As pairs can be duplicated, this is not strictly UNIFORM sampling, but usually each item
    // appears twice
    //  it's more efficient to sample from this, than to always insist upon uniqueness, as this
    // involves
    //  creating a HashMap each query (due to the implementation)
    @Override
    public T sampleRandomPairNonUniform(RandomNumberGenerator randomNumberGenerator) {

        int count = this.graph.edgesMaybeDuplicates().size();

        if (count == 0) {
            throw new AnchorFriendlyRuntimeException("No edges exist to sample from");
        }

        // Pick an element from the existing configuration
        int index = randomNumberGenerator.sampleIntFromRange(count);

        int i = 0;
        for (TypedEdge<Mark, T> edge : pairsMaybeDuplicates()) {
            if (i++ == index) {
                return edge.getPayload();
            }
        }

        throw new AnchorFriendlyRuntimeException("Invalid index chosen for randomPair");
    }

    private void initGraph(
            MemoForIndex marks,
            EnergyStack stack,
            Optional<FeatureCalculatorMulti<FeatureInputPairMemo>> session)
            throws CreateException {
        // Some energy components need to be calculated individually
        for (int i = 0; i < marks.size(); i++) {

            VoxelizedMarkMemo srcMark = marks.getMemoForIndex(i);

            for (int j = 0; j < i; j++) {

                VoxelizedMarkMemo destMark = marks.getMemoForIndex(j);

                addCriteria
                        .generateEdge(srcMark, destMark, stack, session, stack.dimensions().z() > 1)
                        .ifPresent(
                                pair -> graph.addEdge(srcMark.getMark(), destMark.getMark(), pair));
            }
        }
    }

    private void calculatePairsForMark(
            MemoForIndex pxlMarkMemoList, VoxelizedMarkMemo newMark, EnergyStack energyStack)
            throws CreateException {

        Optional<FeatureCalculatorMulti<FeatureInputPairMemo>> session;

        try {
            session =
                    OptionalUtilities.map(
                            addCriteria.orderedListOfFeatures(),
                            f ->
                                    FeatureSession.with(
                                            f,
                                            new FeatureInitialization(energyStack.getParameters()),
                                            sharedFeatures,
                                            logger));
        } catch (InitializeException e) {
            throw new CreateException(e);
        }

        // We calculate how the new mark interacts with all the other marks
        for (int i = 0; i < pxlMarkMemoList.size(); i++) {

            VoxelizedMarkMemo otherMark = pxlMarkMemoList.getMemoForIndex(i);
            if (!otherMark.getMark().equals(newMark.getMark())) {
                addCriteria
                        .generateEdge(
                                otherMark,
                                newMark,
                                energyStack,
                                session,
                                energyStack.dimensions().z() > 1)
                        .ifPresent(
                                pair ->
                                        this.graph.addEdge(
                                                otherMark.getMark(), newMark.getMark(), pair));
            }
        }
    }

    // Each edge can appear many times
    private Collection<TypedEdge<Mark, T>> pairsMaybeDuplicates() {
        return graph.edgesMaybeDuplicates();
    }

    private void checkInit() throws UpdateMarkSetException {
        if (!hasInit) {
            throw new UpdateMarkSetException("object has not been initialized");
        }
    }
}
