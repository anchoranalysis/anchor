/* (C)2020 */
package org.anchoranalysis.image.io.bean.channel.map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.BeanField;

@AllArgsConstructor
public class ImgChnlMapEntry extends AnchorBean<ImgChnlMapEntry> {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private String name;

    @BeanField @Getter @Setter private int index;
    // END BEAN PROPERTIES
}
