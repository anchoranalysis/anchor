/* (C)2020 */
package org.anchoranalysis.image.bean.nonbean.arrangeraster;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;

// Describes a set of bounding boxes on top of a plane
public class BBoxSetOnPlane implements Iterable<BoundingBox> {

    private Extent extent;

    private List<BoundingBox> list = new ArrayList<>();

    public BBoxSetOnPlane(Extent extent) {
        super();
        this.extent = extent;
    }

    public BBoxSetOnPlane(Extent extent, BoundingBox bbox) {
        this(extent);
        add(bbox);
    }

    public void add(BoundingBox obj) {
        list.add(obj);
    }

    public BoundingBox get(int index) {
        return list.get(index);
    }

    public Iterator<BoundingBox> bboxIterator() {
        return list.iterator();
    }

    @Override
    public Iterator<BoundingBox> iterator() {
        return bboxIterator();
    }

    public Extent getExtent() {
        return extent;
    }
}
