package org.anchoranalysis.image.bean.channel;

import java.util.Optional;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.image.core.channel.Channel;
import org.anchoranalysis.image.core.dimensions.Dimensions;

/**
 * A method of aggregating the voxels from many identically-sized {@link Channel}s into one.
 *
 * <p>The {@link Channel}s must also all share the same voxel-data-type, which also forms the
 * aggregated type.
 *
 * @author Owen Feehan
 */
public abstract class ChannelAggregator extends AnchorBean<ChannelAggregator> {

    /**
     * Adds a {@link Channel} to the aggregation.
     *
     * @param channel the channel to add.
     * @throws OperationFailedException if the dimensions do not match existing channels that were
     *     previously added.
     */
    public synchronized void addChannel(Channel channel) throws OperationFailedException {
        Optional<Dimensions> existing = existing();
        if (existing.isPresent() && !channel.dimensions().equals(existing.get())) {
            throw new OperationFailedException(
                    String.format(
                            "Dimensions of added-channel (%s) and aggregated-channel must be equal (%s)",
                            channel.dimensions(), existing));
        }

        addChannelAfterCheck(channel);
    }

    /**
     * Retrieve or create a {@link Channel} with containing the aggregated values.
     *
     * @return the channel, with newly created voxels, containing the mean-value of each voxel
     * @throws OperationFailedException if not channels have been addded, so no mean exists.
     */
    public Channel aggregatedChannel() throws OperationFailedException {

        if (!existing().isPresent()) {
            throw new OperationFailedException(
                    "No channels have been added, so cannot create aggregation");
        }

        return retrieveCreateAggregatedChannel();
    }

    /**
     * The {@link Dimensions} to use for the aggregation.
     *
     * @return the dimensions, if at least one call to {@link #addChannel(Channel)} has occurred,
     *     otherwise {@link Optional#empty()}.
     */
    protected abstract Optional<Dimensions> existing();

    /**
     * Adds a {@link Channel} to the aggregation - after checking {@code channel} has acceptable
     * dimensions.
     *
     * @param channel the channel to add, guaranteed to have identical dimensions to any previous
     *     call to {@link #addChannel(Channel)}.
     * @throws OperationFailedException if the dimensions do not match existing channels that were
     *     previously added.
     */
    protected abstract void addChannelAfterCheck(Channel channel) throws OperationFailedException;

    /**
     * Retrieve or create an aggregated-channel of type {@code outputType}.
     *
     * <p>This channel is the result of the aggregation operation.
     *
     * @return a {@link Channel}, either as already exists internally, or newly created.
     */
    protected abstract Channel retrieveCreateAggregatedChannel();
}
