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

package org.anchoranalysis.anchor.mpp.bean.points;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.anchor.mpp.bean.points.fitter.InsufficientPointsException;
import org.anchoranalysis.anchor.mpp.bean.points.fitter.PointsFitter;
import org.anchoranalysis.anchor.mpp.bean.points.fitter.PointsFitterException;
import org.anchoranalysis.anchor.mpp.bean.provider.SingleMarkProvider;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.mark.MarkCollection;
import org.anchoranalysis.anchor.mpp.mark.points.PointList;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.SkipInit;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.Point3f;
import org.anchoranalysis.core.geometry.PointConverter;
import org.anchoranalysis.image.extent.Dimensions;

public class CreateMarkFromPoints extends AnchorBean<CreateMarkFromPoints> {

    // START BEAN PROPERTIES
    @BeanField @SkipInit @Getter @Setter private SingleMarkProvider markProvider;

    // NOTE no init occurs of pointsFitter
    @BeanField @SkipInit @Getter @Setter private PointsFitter pointsFitter;

    // Below this we don't bother outputting a feature, instead output featureElse
    @BeanField @Getter @Setter private int minNumPoints = 20;

    @BeanField @Getter @Setter private boolean throwExceptionForInsufficientPoints = false;
    // END BEAN PROPERTIES

    /**
     * Extract points from a marks, creates a new mark from markProvider and then fits this mark the
     * extracted points
     *
     * @param marks a marks containing MarkPointLists as points
     * @param dimensions
     * @return
     * @throws OperationFailedException
     */
    public Optional<Mark> fitMarkToPointsFromMarks(MarkCollection marks, Dimensions dimensions)
            throws OperationFailedException {

        try {
            Mark mark =
                    markProvider
                            .create()
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

        } catch (CreateException e) {
            throw new OperationFailedException(e);
        }
    }

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
     * Given a Marks whose marks are all MarkPointList, extract the points from all marks
     *
     * @param marks
     * @return
     */
    private static List<Point3f> extractPointsFromMarks(MarkCollection marks) throws OperationFailedException {

        List<Point3f> out = new ArrayList<>();

        for (Mark m : marks) {

            if (m instanceof PointList) {
                addPointsFrom((PointList) m, out);
            } else {
                throw new OperationFailedException(
                        String.format(
                                "At least one Mark in the marks is not a MarkPointList, rather a %s",
                                m.getClass()));
            }
        }

        return out;
    }

    private static void addPointsFrom(PointList mark, List<Point3f> points) {
        points.addAll(PointConverter.convert3dTo3f(mark.getPoints()));
    }
}
