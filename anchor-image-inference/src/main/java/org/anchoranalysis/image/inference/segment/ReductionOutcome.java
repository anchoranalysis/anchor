package org.anchoranalysis.image.inference.segment;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.functional.FunctionalList;

/**
 * The result of a reduction operation.
 *
 * <p>The reduction operation takes an <i>input list</i> of elements, and creates an <i>output
 * list</i>.
 *
 * <p>The <i>output list</i> contains, and combination of:
 *
 * <ul>
 *   <li><b>retained elements</b> from the input-list (a subset), each identified by its index in
 *       the input list.
 *   <li><b>newly-added elements</b>, that didn't exist in the input-list (typically a merging of
 *       elements in the input-list, who are not otherwise retained).
 * </ul>
 *
 * @author Owen Feehan
 * @param <T> element-type that is reduced / added.
 */
@RequiredArgsConstructor
@AllArgsConstructor
public class ReductionOutcome<T> {

    // START REQUIRED ARGUMENTS
    /**
     * The indices of elements in the list that are retained after the reduction operation.
     * Zero-indexed.
     */
    private final List<Integer> indicesRetained;
    // END REQUIRED ARGUMENTS

    private List<T> added = new ArrayList<>();

    /** Create with an empty list of retained-indices. */
    public ReductionOutcome() {
        this.indicesRetained = new ArrayList<>();
    }

    /**
     * Adds an index to be retained after reduction.
     *
     * @param index an index identifying an element in the input list for reduction, that should be
     *     retained in the output.
     */
    public void addIndexToRetain(int index) {
        indicesRetained.add(index);
    }

    /**
     * Adds a newly-added element, that didn't exist in the input list for reduction, but should
     * exist in the output list.
     *
     * @param toAdd the element to add.
     */
    public void addNewlyAdded(T toAdd) {
        this.added.add(toAdd);
    }

    /**
     * Generates a list of elements that exist after the reduction.
     *
     * @param input the list of elements that was passed as an input to reduce.
     * @return a newly created list, containing retained elements, and newly-added elements.
     */
    public List<T> listAfter(List<T> input) {
        List<T> out = new ArrayList<>();
        for (int index : indicesRetained) {
            out.add(input.get(index));
        }
        out.addAll(added);
        return out;
    }

    /**
     * The total number of elements after reduction.
     *
     * @return the total number of elements, summing both those retained and those newly added.
     */
    public int sizeAfter() {
        return added.size() + indicesRetained.size();
    }

    /**
     * Creates a new {@link ReductionOutcome} where both the retained-indices and the newly-added
     * elements may be mapped.
     *
     * @param <S> the type of the newly-added elements after the mapping.
     * @param mapIndex maps a given index in the input-list to a new index in the output-list.
     * @param mapAdded maps a given newly-added object in the input-list to corresponding element in
     *     the output-list.
     * @return a newly created {@link ReductionOutcome} containing the result of the mappings, as
     *     described above.
     */
    public <S> ReductionOutcome<S> map(UnaryOperator<Integer> mapIndex, Function<T, S> mapAdded) {
        List<Integer> indicesMapped = FunctionalList.mapToList(indicesRetained, mapIndex);
        List<S> addedMapped = FunctionalList.mapToList(added, mapAdded);
        return new ReductionOutcome<>(indicesMapped, addedMapped);
    }
}
