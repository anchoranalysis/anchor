/* (C)2020 */
package org.anchoranalysis.experiment.task;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** Fake shared-state to use to indicate none exists (for type safety) */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NoSharedState {

    public static final NoSharedState INSTANCE = new NoSharedState();
}
