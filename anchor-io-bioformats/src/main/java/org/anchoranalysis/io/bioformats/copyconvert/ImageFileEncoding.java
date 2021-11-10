package org.anchoranalysis.io.bioformats.copyconvert;

import lombok.Value;

/**
 * How values are encoded into an image-file.
 *
 * @author Owen Feehan
 */
@Value
public class ImageFileEncoding {

    /**
     * Whether the source bytes encoded as RGB (i.e. with the three colors coded into a 4 bytes
     * coding per voxel).
     */
    private boolean rgb;

    /**
     * Whether the channels are interleaved
     *
     * <p>Interleaving means that voxels from successive channels are directly adjacent. Otherwise,
     * each channel's voxels are contiguous.
     */
    private boolean interleaved;

    /**
     * The total number of channels found in any one buffer.
     *
     * <p>For an RGB image, this is by definition three.
     */
    private int numberChannelsPerArray;

    /**
     * When RGB-encoded and non-interleaved, the source array is considered 1 channel (not three).
     *
     * <p>When not RGB encoded, it describes the number of interleaved channels present.
     *
     * @return the number of channels as per above.
     */
    public int numberDistinctChannelsSource() {
        return (rgb && !interleaved) ? numberChannelsPerArray / 3 : numberChannelsPerArray;
    }
}
