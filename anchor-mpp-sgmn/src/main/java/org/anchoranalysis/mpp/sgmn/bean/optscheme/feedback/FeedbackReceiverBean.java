/* (C)2020 */
package org.anchoranalysis.mpp.sgmn.bean.optscheme.feedback;

import org.anchoranalysis.anchor.mpp.bean.MPPBean;
import org.anchoranalysis.mpp.sgmn.optscheme.feedback.FeedbackReceiver;

public abstract class FeedbackReceiverBean<T> extends MPPBean<FeedbackReceiverBean<T>>
        implements FeedbackReceiver<T> {}
