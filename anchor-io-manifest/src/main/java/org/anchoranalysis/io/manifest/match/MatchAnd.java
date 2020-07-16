/* (C)2020 */
package org.anchoranalysis.io.manifest.match;

import java.util.ArrayList;

public class MatchAnd<T> implements Match<T> {

    private ArrayList<Match<T>> conditions = new ArrayList<>();

    public MatchAnd() {}

    public MatchAnd(Match<T> condition1, Match<T> condition2) {
        conditions.add(condition1);
        conditions.add(condition2);
    }

    public void addCondition(Match<T> condition) {
        conditions.add(condition);
    }

    @Override
    public boolean matches(T obj) {

        for (Match<T> condition : conditions) {
            if (!condition.matches(obj)) {
                return false;
            }
        }
        return true;
    }
}
