package org.anchoranalysis.image.object;

import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;

/**
 * A helper class to create a map from input-objects to output-objects (labelled-objects with an operation applied)
 * 
 * @author Owen Feehan
 *
 */
@AllArgsConstructor class MapForLabelsCreator {
    
    /** a map from a label to the corresponding input-object */
    private Map<Integer,ObjectMask> input;
    
    /** the labelled-objects for each label in the sequence */
    private ObjectCollection labelled;
    
    /** an operation to apply after labelling, but before the object is placed in the map */
    private UnaryOperator<ObjectMask> operationAfterLabelling;
    
    /** Minimum label-value inclusive */
    private int minLabelInclusive;
    
    /**
     * Creates a map from the input-objects to output-objects (their derived labelled objects with an operation applied)
     *  
     * @return the newly created map
     */
    public Map<ObjectMask,ObjectMask> createMapForLabels() {

        Stream<Integer> indexStream = IntStream.range(0, labelled.size()).mapToObj(Integer::valueOf);

        return indexStream.collect( Collectors.toMap(this::inputObjectForLabel, this::outputObjectForIndex) );
    }

    private ObjectMask inputObjectForLabel(int index) {
        return input.get(minLabelInclusive+index);
    }
    
    private ObjectMask outputObjectForIndex(int index) {
        return operationAfterLabelling.apply(labelled.get(index) );
    }
}