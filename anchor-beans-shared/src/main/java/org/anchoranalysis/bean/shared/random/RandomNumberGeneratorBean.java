/* (C)2020 */
package org.anchoranalysis.bean.shared.random;

import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.core.random.RandomNumberGenerator;

public abstract class RandomNumberGeneratorBean extends AnchorBean<RandomNumberGeneratorBean> {

    public abstract RandomNumberGenerator create();
}
