package org.anchoranalysis.image.bean.channel;

import java.util.Optional;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.image.bean.nonbean.ConsistentChannelChecker;
import org.anchoranalysis.image.core.channel.Channel;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.dimensions.Resolution; // NOSONAR

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
     * When true the resolution is no longer considered when comparing added channels to any
     * existing channel.
     */
    private boolean ignoreResolution = false;

    private ConsistentChannelChecker checker = new ConsistentChannelChecker();

    /**
     * Adds a {@link Channel} to the aggregation.
     *
     * @param channel the channel to add.
     * @param logger the logger to output warning messages to.
     * @throws OperationFailedException if the dimensions do not match existing channels that were
     *     previously added.
     */
    public synchronized void addChannel(Channel channel, Logger logger)
            throws OperationFailedException {
        Optional<Dimensions> existing = existingDimensions();

        checker.checkChannelType(channel);

        if (existing.isPresent() && !areDimensionsEqual(channel.dimensions(), existing.get())) {

            if (existing.get().extent().equals(channel.dimensions().extent())) {
                // If the sizes are the same but the resolutions differ, we stop comparing
                // resolution
                logger.messageLogger()
                        .logFormatted(
                                "Dropping image-resolution as it is not consistent between images: existing %s versus %s to add",
                                existing.get().resolution(), channel.resolution());
                ignoreResolution = true;
            } else {

                throw new OperationFailedException(
                        String.format(
                                "Sizes of added-channel (%s) and aggregated-channel must be equal (%s)",
                                channel.dimensions().extent(), existing.get().extent()));
            }
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

        if (!existingDimensions().isPresent()) {
            throw new OperationFailedException(
                    "No channels have been added, so cannot create aggregation");
        }

        return retrieveCreateAggregatedChannel();
    }

    /**
     * The {@link Dimensions} to use for the aggregation.
     *
     * @return the dimensions, if at least one call to {@link #addChannel(Channel, Logger)} has
     *     occurred, otherwise {@link Optional#empty()}.
     */
    protected abstract Optional<Dimensions> existingDimensions();

    /**
     * Adds a {@link Channel} to the aggregation - after checking {@code channel} has acceptable
     * dimensions.
     *
     * @param channel the channel to add, guaranteed to have identical dimensions to any previous
     *     call to {@link #addChannel(Channel, Logger)}.
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

    /**
     * Removes the {@link Resolution} component in {@link Dimensions}.
     *
     * @param dimensions to maybe remove resolution from.
     * @return {@code dimensions} unchanged when {@code ignoreResolution==false}, otherwise {@code
     *     dimensions} without any resolution specified.
     */
    protected Dimensions maybeDropResolution(Dimensions dimensions) {
        if (ignoreResolution) {
            return dimensions.duplicateChangeResolution(Optional.empty());
        } else {
            return dimensions;
        }
    }

    /**
     * Are two {@link Dimensions}, either considering or disconsidering the {@link Resolution}
     * depending on {@code ignoreResolution}.
     */
    private boolean areDimensionsEqual(Dimensions existing, Dimensions toAdd) {
        if (ignoreResolution) {
            return existing.extent().equals(toAdd.extent());
        } else {
            return existing.equals(toAdd);
        }
    }
}
