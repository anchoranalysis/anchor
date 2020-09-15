package org.anchoranalysis.image.index;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import org.anchoranalysis.image.extent.box.BoundingBox;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SpatiallySeparate<T> {

    private RTree<T> tree;
    private Function<T,BoundingBox> extractBoundingBox;
    
    /**
     * Splits the collection of objects into spatially separate <i>clusters</i>.
     * 
     * <p>Any objects whose bounding-boxes intersect belong to the same cluster, but otherwise
     * not.
     * 
     * <p>This is similar to a simplified <a href="https://en.wikipedia.org/wiki/DBSCAN">DB Scan algorithm</a>.
     * 
     * @return a list of object-collections, each object-collection is guaranteed to be spatially separate from the others.
     */
    public List<List<T>> spatiallySeperate( Set<T> unprocessed ) {

        List<List<T>> out = new ArrayList<>();
        
        while(!unprocessed.isEmpty()) {
            
            T identifier = unprocessed.iterator().next();
            
            List<T> spatiallyConnected = new ArrayList<>();
            addSpatiallyConnected(spatiallyConnected, identifier, unprocessed);
            out.add(spatiallyConnected);
        }
        assert(unprocessed.isEmpty());
        return out;
    }
    
    private void addSpatiallyConnected( List<T> spatiallyConnected, T source, Set<T> unprocessed ) {

        unprocessed.remove(source);
        spatiallyConnected.add(source);
        
        List<T> queue = tree.intersectsWith( extractBoundingBox.apply(source) );
        
        while(!queue.isEmpty()) {
            T current = queue.remove(0);
            
            if (unprocessed.contains(current)) {
                
                unprocessed.remove(current);
                spatiallyConnected.add(current);
                
                queue.addAll( tree.intersectsWith( extractBoundingBox.apply(current) ) );
            }
        }
    }
}
