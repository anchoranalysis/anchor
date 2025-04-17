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

package org.anchoranalysis.mpp.bean.points;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.SkipInit;
import org.anchoranalysis.bean.xml.exception.ProvisionFailedException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.mpp.bean.points.fitter.InsufficientPointsException;
import org.anchoranalysis.mpp.bean.points.fitter.PointsFitter;
import org.anchoranalysis.mpp.bean.points.fitter.PointsFitterException;
import org.anchoranalysis.mpp.bean.provider.SingleMarkProvider;
import org.anchoranalysis.mpp.mark.Mark;
import org.anchoranalysis.mpp.mark.MarkCollection;
import org.anchoranalysis.mpp.mark.points.PointList;
import org.anchoranalysis.spatial.point.Point3f;
import org.anchoranalysis.spatial.point.PointConverter;

/** Creates a mark by fitting it to a set of points extracted from other marks. */
public class CreateMarkFromPoints extends AnchorBean<CreateMarkFromPoints> {

    // START BEAN PROPERTIES
    /** Provides the mark to be fitted. */
    @BeanField @SkipInit @Getter @Setter private SingleMarkProvider markProvider;

    /** Fits the provided mark to the extracted points. */
    @BeanField @SkipInit @Getter @Setter private PointsFitter pointsFitter;

    /** Minimum number of points required to create a mark. */
    @BeanField @Getter @Setter private int minNumPoints = 20;

    /** Whether to throw an exception when there are insufficient points. */
    @BeanField @Getter @Setter private boolean throwExceptionForInsufficientPoints = false;
    // END BEAN PROPERTIES

    /**
     * Extracts points from marks, creates a new mark, and fits this mark to the extracted points.
     *
     * @param marks a collection of marks containing PointLists
     * @param dimensions the dimensions of the space in which the marks exist
     * @return an Optional containing the fitted mark, or empty if fitting was not possible
     * @throws OperationFailedException if the operation fails
     */
    public Optional<Mark> fitMarkToPointsFromMarks(MarkCollection marks, Dimensions dimensions)
            throws OperationFailedException {
        try {
            Mark mark =
                    markProvider
                            .get()
                            .orElseThrow(
                                    () ->
                                            new OperationFailedException(
                                                    "A mark is required for this operation"));

            List<Point3f> points = extractPointsFromMarks(marks);

            if (points.size() >= minNumPoints) {
                return fitPoints(mark, points, dimensions);
            } else {
                return maybeThrowInsufficientPointsException(points);
            }
        } catch (ProvisionFailedException e) {
            throw new OperationFailedException(e);
        }
    }

    /**
     * Fits points to a mark using the pointsFitter.
     *
     * @param mark the mark to fit
     * @param points the points to fit the mark to
     * @param dimensions the dimensions of the space
     * @return an Optional containing the fitted mark, or empty if fitting was not possible
     * @throws OperationFailedException if the operation fails
     */
    private Optional<Mark> fitPoints(Mark mark, List<Point3f> points, Dimensions dimensions)
            throws OperationFailedException {
        try {
            pointsFitter.fit(points, mark, dimensions);
            return Optional.of(mark);
        } catch (InsufficientPointsException e) {
            return maybeThrowInsufficientPointsException(points);
        } catch (PointsFitterException e) {
            throw new OperationFailedException(e);
        }
    }

    /**
     * Throws an exception if configured to do so when there are insufficient points.
     *
     * @param points the list of points
     * @return an empty Optional if no exception is thrown
     * @throws OperationFailedException if throwExceptionForInsufficientPoints is true
     */
    private Optional<Mark> maybeThrowInsufficientPointsException(List<Point3f> points)
            throws OperationFailedException {
        if (throwExceptionForInsufficientPoints) {
            throw new OperationFailedException(
                    String.format(
                            "There are an insufficient number of points to successfully create a mark (number_pts=%d, min_number=%d)",
                            points.size(), minNumPoints));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Extracts points from all marks in a MarkCollection.
     *
     * @param marks the collection of marks to extract points from
     * @return a list of extracted points
     * @throws OperationFailedException if any mark is not a PointList
     */
    private static List<Point3f> extractPointsFromMarks(MarkCollection marks)
            throws OperationFailedException {
        List<Point3f> out = new ArrayList<>();

        for (Mark m : marks) {
            if (m instanceof PointList) {
                addPointsFrom((PointList) m, out);
            } else {
                throw new OperationFailedException(
                        String.format(
                                "At least one Mark in the marks is not a PointList, rather a %s",
                                m.getClass()));
            }
        }

        return out;
    }

    /**
     * Adds points from a PointList mark to a list of points.
     *
     * @param mark the PointList mark to extract points from
     * @param points the list to add the extracted points to
     */
    private static void addPointsFrom(PointList mark, List<Point3f> points) {
        points.addAll(PointConverter.convert3dTo3f(mark.getPoints()));
    }
}
