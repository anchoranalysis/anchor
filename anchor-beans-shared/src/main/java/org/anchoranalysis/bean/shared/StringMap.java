/* (C)2020 */
package org.anchoranalysis.bean.shared;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.anchoranalysis.bean.AnchorBean;

public class StringMap extends AnchorBean<StringMap> {

    // START BEAN PROPERTIES
    private List<StringMapItem> list;
    // END BEAN PROPERTIES

    public Map<String, String> create() {

        HashMap<String, String> map = new HashMap<>();

        for (StringMapItem mapping : list) {
            map.put(mapping.getSource(), mapping.getTarget());
        }

        return map;
    }

    public List<StringMapItem> getList() {
        return list;
    }

    public void setList(List<StringMapItem> list) {
        this.list = list;
    }
}
