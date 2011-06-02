package org.weasis.core.ui.graphic;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.weasis.core.api.gui.util.DecFormater;
import org.weasis.core.api.gui.util.GeomUtil;
import org.weasis.core.api.image.util.Unit;
import org.weasis.core.api.media.data.ImageElement;
import org.weasis.core.ui.Messages;

public class PerpendicularLineGraphic extends AbstractDragGraphic {

    public static final Icon ICON = new ImageIcon(
        PerpendicularLineGraphic.class.getResource("/icon/22x22/draw-perpendicular.png")); //$NON-NLS-1$

    private Stroke strokeDecorator;

    public PerpendicularLineGraphic(float lineThickness, Color paintColor, boolean labelVisible) {
        super(4, paintColor, lineThickness, labelVisible);
    }

    @Override
    public Icon getIcon() {
        return ICON;
    }

    @Override
    public String getUIName() {
        return Messages.getString("MeasureToolBar.perpendicular"); //$NON-NLS-1$
    }

    @Override
    protected void updateStroke() {
        super.updateStroke();
        strokeDecorator =
            new BasicStroke(lineThickness, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f,
                new float[] { 5.0f, 5.0f }, 0f);
    }

    @Override
    protected int moveAndResizeOnDrawing(int handlePointIndex, int deltaX, int deltaY, MouseEvent mouseEvent) {
        if (handlePointIndex == -1) {
            handlePointIndex = super.moveAndResizeOnDrawing(handlePointIndex, deltaX, deltaY, mouseEvent);
        } else {
            if (!isGraphicComplete()) {
                handlePointList.get(handlePointIndex).setLocation(mouseEvent.getPoint());

                if (handlePointList.size() >= 3) {
                    Point2D A = handlePointList.get(0);
                    Point2D B = handlePointList.get(1);
                    Point2D C = handlePointList.get(2);

                    while (handlePointList.size() < handlePointTotalNumber)
                        handlePointList.add(new Point.Double());

                    Point2D D = handlePointList.get(3);
                    D.setLocation(GeomUtil.getPerpendicularPointToLine(A, B, C));
                }
            } else {
                Point2D A = handlePointList.get(0);
                Point2D B = handlePointList.get(1);
                Point2D C = handlePointList.get(2);
                Point2D D = handlePointList.get(3);

                if (handlePointIndex == 0 || handlePointIndex == 1) {
                    double theta = GeomUtil.getAngleRad(A, B);
                    handlePointList.get(handlePointIndex).setLocation(mouseEvent.getPoint());
                    theta -= GeomUtil.getAngleRad(A, B);

                    Point2D anchor = (handlePointIndex == 0) ? B : A;
                    AffineTransform transform = AffineTransform.getRotateInstance(theta, anchor.getX(), anchor.getY());

                    transform.transform(C, C);
                    transform.transform(D, D);
                } else if (handlePointIndex == 2) {
                    handlePointList.get(handlePointIndex).setLocation(mouseEvent.getPoint());
                    D.setLocation(GeomUtil.getPerpendicularPointToLine(A, B, C));
                } else if (handlePointIndex == 3) {
                    double tx = D.getX();
                    double ty = D.getY();
                    D.setLocation(GeomUtil.getPerpendicularPointToLine(A, B, mouseEvent.getPoint()));
                    tx -= D.getX();
                    ty -= D.getY();
                    AffineTransform.getTranslateInstance(-tx, -ty).transform(C, C);
                }
            }

        }
        return handlePointIndex;
    }

    @Override
    protected void updateShapeOnDrawing(MouseEvent mouseEvent) {

        if (handlePointList.size() >= 1) {
            Point2D A = handlePointList.get(0);

            if (handlePointList.size() >= 2) {
                Point2D B = handlePointList.get(1);

                AdvancedShape newShape = new AdvancedShape(3);

                if (!A.equals(B)) {
                    GeneralPath generalpath = new GeneralPath(Path2D.WIND_NON_ZERO, handlePointList.size() / 2);
                    newShape.addShape(generalpath);
                    generalpath.moveTo(A.getX(), A.getY());
                    generalpath.lineTo(B.getX(), B.getY());

                    if (handlePointList.size() >= 3) {
                        Point2D C = handlePointList.get(2);

                        if (handlePointList.size() == 4) {
                            Point2D D = handlePointList.get(3);

                            if (!C.equals(D)) {
                                generalpath.moveTo(C.getX(), C.getY());
                                generalpath.lineTo(D.getX(), D.getY());

                                String label = "";
                                label = getRealDistanceLabel(getImageElement(mouseEvent), C, D);
                                setLabel(new String[] { label }, getDefaultView2d(mouseEvent));

                                // Check if D is outside of AB segment
                                if (Math.signum(GeomUtil.getAngleDeg(D, A)) == Math.signum(GeomUtil.getAngleDeg(D, B))) {
                                    Point2D E = D.distance(A) < D.distance(B) ? A : B;
                                    if (!D.equals(E))
                                        newShape.addShape(new Line2D.Double(D, E), strokeDecorator);
                                }

                                double cornerLength = 10;
                                double dMin =
                                    (2.0 / 3.0) * Math.min(D.distance(C), Math.max(D.distance(A), D.distance(B)));
                                double scalingMin = cornerLength / dMin;

                                Point2D F = GeomUtil.getMidPoint(A, B);
                                if (!D.equals(C) && !F.equals(D))
                                    newShape.addInvShape(GeomUtil.getCornerShape(F, D, C, cornerLength),
                                        (Point2D) D.clone(), scalingMin);
                            }
                        }
                    }
                }
                setShape(newShape, mouseEvent);
            }
        }
    }

    @Override
    public List<MeasureItem> getMeasurements(ImageElement imageElement, boolean releaseEvent) {

        return null;
    }

    protected String getRealDistanceLabel(ImageElement image, Point2D A, Point2D B) {
        String label = "";
        if (image != null) {
            AffineTransform rescale = AffineTransform.getScaleInstance(image.getPixelSize(), image.getPixelSize());

            Point2D At = rescale.transform(A, null);
            Point2D Bt = rescale.transform(B, null);

            Unit unit = image.getPixelSpacingUnit();
            label = "Dist : " + DecFormater.twoDecimal(At.distance(Bt)) + " " + unit.getAbbreviation();
        }
        return label;
    }
}
