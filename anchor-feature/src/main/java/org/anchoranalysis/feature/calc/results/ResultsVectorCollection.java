/* (C)2020 */
package org.anchoranalysis.feature.calc.results;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ResultsVectorCollection implements Iterable<ResultsVector> {

    private List<ResultsVector> list = new ArrayList<>();

    public ResultsVectorCollection() {
        super();
    }

    public ResultsVectorCollection(ResultsVector rv) {
        super();
        list.add(rv);
    }

    public boolean add(ResultsVector e) {
        return list.add(e);
    }

    public int size() {
        return list.size();
    }

    public ResultsVector get(int index) {
        return list.get(index);
    }

    @Override
    public Iterator<ResultsVector> iterator() {
        return list.iterator();
    }
}
