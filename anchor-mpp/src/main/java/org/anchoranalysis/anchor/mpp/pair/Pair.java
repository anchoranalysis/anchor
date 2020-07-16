/* (C)2020 */
package org.anchoranalysis.anchor.mpp.pair;

import org.anchoranalysis.anchor.overlay.id.Identifiable;

/**
 * Pair of marks
 *
 * @param <T> item-type
 */
public class Pair<T extends Identifiable> {

    private final T source;
    private final T destination;

    public Pair(T source, T destination) {
        super();

        if (source.getId() < destination.getId()) {
            this.source = source;
            this.destination = destination;
        } else {
            this.destination = source;
            this.source = destination;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object othero) {

        if (othero == null) {
            return false;
        }
        if (othero == this) {
            return true;
        }
        if (!(othero instanceof Pair)) {
            return false;
        }

        Pair<T> other = (Pair<T>) othero;
        return ((this.source.equals(other.source)) && (this.destination.equals(other.destination)));
    }

    @Override
    public int hashCode() {
        if (source == null || destination == null) {
            return 0;
        }

        return (source.getId() * 3) + destination.getId();
    }

    public T getSource() {
        return source;
    }

    public T getDestination() {
        return destination;
    }

    @Override
    public String toString() {
        return String.format("%d--%d", source.getId(), destination.getId());
    }
}
