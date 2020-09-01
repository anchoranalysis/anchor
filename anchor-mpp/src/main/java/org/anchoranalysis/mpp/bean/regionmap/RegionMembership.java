/*-
 * #%L
 * anchor-mpp
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

package org.anchoranalysis.mpp.bean.regionmap;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.friendly.AnchorFriendlyRuntimeException;

/** @author Owen Feehan */
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public abstract class RegionMembership extends AnchorBean<RegionMembership> {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private boolean bit0 = false;

    @BeanField @Getter @Setter private boolean bit1 = false;

    @BeanField @Getter @Setter private boolean bit2 = false;

    @BeanField @Getter @Setter private boolean bit3 = false;

    @BeanField @Getter @Setter private boolean bit4 = false;

    @BeanField @Getter @Setter private boolean bit5 = false;

    @BeanField @Getter @Setter private boolean bit6 = false;

    @BeanField @Getter @Setter private boolean bit7 = false;
    // END BEAN PROPERTIES

    public RegionMembership(int index) {
        switch (index) {
            case 0:
                bit0 = true;
                break;
            case 1:
                bit1 = true;
                break;
            case 2:
                bit2 = true;
                break;
            case 3:
                bit3 = true;
                break;
            case 4:
                bit4 = true;
                break;
            case 5:
                bit5 = true;
                break;
            case 6:
                bit6 = true;
                break;
            case 7:
                bit7 = true;
                break;
            default:
                throw new AnchorFriendlyRuntimeException(
                        String.format("Index %d is not supported", index));
        }
    }

    public byte flags() {
        byte membership = 0;

        if (bit0) {
            membership =
                    RegionMembershipUtilities.setAsMemberFlag(
                            membership, RegionMembershipUtilities.flagForRegion(0));
        }
        if (bit1) {
            membership =
                    RegionMembershipUtilities.setAsMemberFlag(
                            membership, RegionMembershipUtilities.flagForRegion(1));
        }
        if (bit2) {
            membership =
                    RegionMembershipUtilities.setAsMemberFlag(
                            membership, RegionMembershipUtilities.flagForRegion(2));
        }
        if (bit3) {
            membership =
                    RegionMembershipUtilities.setAsMemberFlag(
                            membership, RegionMembershipUtilities.flagForRegion(3));
        }
        if (bit4) {
            membership =
                    RegionMembershipUtilities.setAsMemberFlag(
                            membership, RegionMembershipUtilities.flagForRegion(4));
        }
        if (bit5) {
            membership =
                    RegionMembershipUtilities.setAsMemberFlag(
                            membership, RegionMembershipUtilities.flagForRegion(5));
        }
        if (bit6) {
            membership =
                    RegionMembershipUtilities.setAsMemberFlag(
                            membership, RegionMembershipUtilities.flagForRegion(6));
        }
        if (bit7) {
            membership =
                    RegionMembershipUtilities.setAsMemberFlag(
                            membership, RegionMembershipUtilities.flagForRegion(7));
        }
        return membership;
    }

    public abstract boolean isMemberFlag(byte membership, byte flag);

    @Override
    public String toString() {
        return String.format(
                "%s%s%s%s%s%s%s%s",
                bitAsString(bit0),
                bitAsString(bit1),
                bitAsString(bit2),
                bitAsString(bit3),
                bitAsString(bit4),
                bitAsString(bit5),
                bitAsString(bit6),
                bitAsString(bit7));
    }

    private static String bitAsString(boolean bit) {
        if (bit) {
            return "1";
        } else {
            return "0";
        }
    }
}
