/* (C)2020 */
package org.anchoranalysis.image.stack.region.chnlconverter;

public enum ConversionPolicy {
    CHANGE_EXISTING_CHANNEL, // The old channel is converted into the new type if needed
    DO_NOT_CHANGE_EXISTING, // Never change the existing channel, make a new chnl... but only if the
    // type needs to be changed
    ALWAYS_NEW // Always make a new channel
}
