/* ===========================================================
 * TradeManager : a application to trade strategies for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2011-2011, by Simon Allen and Contributors.
 *
 * Project Info:  org.trade
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * [Java is a trademark or registered trademark of Oracle, Inc.
 * in the United States and other countries.]
 *
 * (C) Copyright 2011-2011, by Simon Allen and Contributors.
 *
 * Original Author:  Simon Allen;
 * Contributor(s):   -;
 *
 * Changes
 * -------
 *
 */
package org.trade.ui.chart.renderer;

import org.jfree.chart.LegendItem;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.chart.text.TextUtils;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.chart.util.PublicCloneable;
import org.jfree.chart.util.SerialUtils;
import org.jfree.chart.util.ShapeUtils;
import org.jfree.data.xy.XYDataset;
import org.trade.core.valuetype.Side;
import org.trade.indicator.PivotDataset;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serial;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * A renderer that draws a small dot at each data point for an {@link XYPlot}.
 * The example shown here is generated by the <code>ScatterPlotDemo4.java</code>
 * program included in the JFreeChart demo collection: <br>
 * <br>
 * <img src="../../../../../images/XYDotRendererSample.png" alt=
 * "XYDotRendererSample.png" />
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class PivotRenderer extends AbstractXYItemRenderer implements XYItemRenderer, PublicCloneable {

    /**
     * For serialization.
     */
    @Serial
    private static final long serialVersionUID = -2764344339073566425L;

    final DateFormat TOOLTIP_DATE_FORMAT = new SimpleDateFormat("H:mma MM/dd/yy");
    /**
     * The default tip radius (in Java2D units).
     */
    public static final double DEFAULT_TIP_RADIUS = 10.0;

    /**
     * The default base radius (in Java2D units).
     */
    public static final double DEFAULT_BASE_RADIUS = 30.0;

    /**
     * The default label offset (in Java2D units).
     */
    public static final double DEFAULT_LABEL_OFFSET = 3.0;

    /**
     * The default arrow length (in Java2D units).
     */
    public static final double DEFAULT_ARROW_LENGTH = 5.0;

    /**
     * The default arrow width (in Java2D units).
     */
    public static final double DEFAULT_ARROW_WIDTH = 3.0;

    /**
     * The default font.
     */
    public static final Font DEFAULT_FONT = new Font("SansSerif", Font.PLAIN, 10);

    /**
     * The default paint.
     */
    public static final Paint DEFAULT_PAINT = Color.black;

    /**
     * The default text anchor.
     */
    public static final TextAnchor DEFAULT_TEXT_ANCHOR = TextAnchor.CENTER;

    /**
     * The default rotation anchor.
     */
    public static final TextAnchor DEFAULT_ROTATION_ANCHOR = TextAnchor.CENTER;

    /**
     * The default rotation angle.
     */
    public static final double DEFAULT_ROTATION_ANGLE = 0.0;

    /**
     * The dot width.
     */
    private int dotWidth;

    /**
     * The dot height.
     */
    private int dotHeight;

    /**
     * The shape that is used to represent an item in the legend.
     *
     * @since 1.0.7
     */
    private transient Shape legendShape;

    /**
     * Constructs a new renderer.
     */
    public PivotRenderer() {
        super();
        this.dotWidth = 10;
        this.dotHeight = 1;
        this.legendShape = new Rectangle2D.Double(-3.0, -3.0, 6.0, 6.0);
    }

    /**
     * Returns the dot width (the default value is 1).
     *
     * @return The dot width. * @see #setDotWidth(int)
     * @since 1.0.2
     */
    public int getDotWidth() {
        return this.dotWidth;
    }

    /**
     * Sets the dot width and sends a {@link RendererChangeEvent} to all
     * registered listeners.
     *
     * @param w the new width (must be greater than zero).
     * @throws IllegalArgumentException if <code>w</code> is less than one. * @see #getDotWidth()
     * @since 1.0.2
     */
    public void setDotWidth(int w) {
        if (w < 1) {
            throw new IllegalArgumentException("Requires w > 0.");
        }
        this.dotWidth = w;
        fireChangeEvent();
    }

    /**
     * Returns the dot height (the default value is 1).
     *
     * @return The dot height. * @see #setDotHeight(int)
     * @since 1.0.2
     */
    public int getDotHeight() {
        return this.dotHeight;
    }

    /**
     * Sets the dot height and sends a {@link RendererChangeEvent} to all
     * registered listeners.
     *
     * @param h the new height (must be greater than zero).
     * @throws IllegalArgumentException if <code>h</code> is less than one. * @see #getDotHeight()
     * @since 1.0.2
     */
    public void setDotHeight(int h) {
        if (h < 1) {
            throw new IllegalArgumentException("Requires h > 0.");
        }
        this.dotHeight = h;
        fireChangeEvent();
    }

    /**
     * Returns the shape used to represent an item in the legend.
     *
     * @return The legend shape (never <code>null</code>). * @see
     * #setLegendShape(Shape)
     * @since 1.0.7
     */
    public Shape getLegendShape() {
        return this.legendShape;
    }

    /**
     * Sets the shape used as a line in each legend item and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param shape the shape (<code>null</code> not permitted).
     * @see #getLegendShape()
     * @since 1.0.7
     */
    public void setLegendShape(Shape shape) {
        if (shape == null) {
            throw new IllegalArgumentException("Null 'shape' argument.");
        }
        this.legendShape = shape;
        fireChangeEvent();
    }

    /**
     * Draws the visual representation of a single data item.
     *
     * @param g2             the graphics device.
     * @param state          the renderer state.
     * @param dataArea       the area within which the data is being drawn.
     * @param info           collects information about the drawing.
     * @param plot           the plot (can be used to obtain standard color information
     *                       etc).
     * @param domainAxis     the domain (horizontal) axis.
     * @param rangeAxis      the range (vertical) axis.
     * @param dataset        the dataset.
     * @param series         the series index (zero-based).
     * @param item           the item index (zero-based).
     * @param crosshairState crosshair information for the plot (<code>null</code>
     *                       permitted).
     * @param pass           the pass index.
     * @see XYItemRenderer#drawItem(Graphics2D,
     * XYItemRendererState, Rectangle2D, PlotRenderingInfo, XYPlot,
     * ValueAxis, ValueAxis, XYDataset, int, int, CrosshairState, int)
     */
    public void drawItem(Graphics2D g2, XYItemRendererState state, Rectangle2D dataArea, PlotRenderingInfo info,
                         XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset, int series, int item,
                         CrosshairState crosshairState, int pass) {

        // do nothing if item is not visible
        if (!getItemVisible(series, item)) {
            return;
        }

        // get the data point...
        double x = dataset.getXValue(series, item);
        double y = dataset.getYValue(series, item);
        double adjx = (this.dotWidth - 1) / 2.0;
        double adjy = (this.dotHeight - 1) / 2.0;
        if (!Double.isNaN(y)) {
            RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
            RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
            double transX = domainAxis.valueToJava2D(x, dataArea, xAxisLocation) - adjx;
            double transY = rangeAxis.valueToJava2D(y, dataArea, yAxisLocation) - adjy;

            g2.setPaint(getItemPaint(series, item));
            PlotOrientation orientation = plot.getOrientation();
            if (orientation == PlotOrientation.HORIZONTAL) {
                g2.fillRect((int) transY, (int) transX, this.dotHeight, this.dotWidth);
            } else if (orientation == PlotOrientation.VERTICAL) {
                g2.fillRect((int) transX, (int) transY, this.dotWidth, this.dotHeight);
            }

            int domainAxisIndex = plot.getDomainAxisIndex(domainAxis);
            int rangeAxisIndex = plot.getRangeAxisIndex(rangeAxis);
            updateCrosshairValues(crosshairState, x, y, domainAxisIndex, transX, transY, orientation);

            PivotDataset pivotDataset = (PivotDataset) dataset;
            if (null != pivotDataset.getPivotSide(series, item)) {
                String ledgend = "Pivot";
                if (pivotDataset.getPivotSide(series, item).equals(Side.BOT)) {
                    drawPivotArrow(g2, plot, dataArea, domainAxis, rangeAxis, item, info, 45d, x,
                            pivotDataset.getPivotValue(series, item), ledgend);
                } else {
                    drawPivotArrow(g2, plot, dataArea, domainAxis, rangeAxis, item, info, -45d, x,
                            pivotDataset.getPivotValue(series, item), ledgend);
                }
            }
        }

    }

    /**
     * Draws the annotation.
     *
     * @param g2            the graphics device.
     * @param plot          the plot.
     * @param dataArea      the data area.
     * @param domainAxis    the domain axis.
     * @param rangeAxis     the range axis.
     * @param rendererIndex the renderer index.
     * @param info          the plot rendering info.
     * @param angle         double
     * @param x             double
     * @param y             double
     * @param ledgend       String
     */
    public void drawPivotArrow(Graphics2D g2, XYPlot plot, Rectangle2D dataArea, ValueAxis domainAxis,
                               ValueAxis rangeAxis, int rendererIndex, PlotRenderingInfo info, double angle, double x, double y,
                               String ledgend) {

        double tipRadius = DEFAULT_TIP_RADIUS;
        double baseRadius = DEFAULT_BASE_RADIUS;
        double arrowLength = DEFAULT_ARROW_LENGTH;
        double arrowWidth = DEFAULT_ARROW_WIDTH;
        double labelOffset = DEFAULT_LABEL_OFFSET;
        Font font = DEFAULT_FONT;
        Paint paint = DEFAULT_PAINT;
        boolean outlineVisible = false;
        Paint outlinePaint = Color.black;
        Stroke outlineStroke = new BasicStroke(0.5f);

        TextAnchor textAnchor = DEFAULT_TEXT_ANCHOR;
        TextAnchor rotationAnchor = DEFAULT_ROTATION_ANCHOR;
        double rotationAngle = DEFAULT_ROTATION_ANGLE;

        Stroke arrowStroke = new BasicStroke(1.0f);
        Paint arrowPaint = Color.black;

        PlotOrientation orientation = plot.getOrientation();
        RectangleEdge domainEdge = Plot.resolveDomainAxisLocation(plot.getDomainAxisLocation(), orientation);
        RectangleEdge rangeEdge = Plot.resolveRangeAxisLocation(plot.getRangeAxisLocation(), orientation);
        double j2DX = domainAxis.valueToJava2D(x, dataArea, domainEdge);
        double j2DY = rangeAxis.valueToJava2D(y, dataArea, rangeEdge);
        if (orientation == PlotOrientation.HORIZONTAL) {
            double temp = j2DX;
            j2DX = j2DY;
            j2DY = temp;
        }
        double startX = j2DX + (Math.cos(angle) * baseRadius);
        double startY = j2DY + (Math.sin(angle) * baseRadius);

        double endX = j2DX + (Math.cos(angle) * tipRadius);
        double endY = j2DY + (Math.sin(angle) * tipRadius);

        double arrowBaseX = endX + (Math.cos(angle) * arrowLength);
        double arrowBaseY = endY + (Math.sin(angle) * arrowLength);

        double arrowLeftX = arrowBaseX + (Math.cos(angle + (Math.PI / 2.0)) * arrowWidth);
        double arrowLeftY = arrowBaseY + (Math.sin(angle + (Math.PI / 2.0)) * arrowWidth);

        double arrowRightX = arrowBaseX - (Math.cos(angle + (Math.PI / 2.0)) * arrowWidth);
        double arrowRightY = arrowBaseY - (Math.sin(angle + (Math.PI / 2.0)) * arrowWidth);

        GeneralPath arrow = new GeneralPath();
        arrow.moveTo((float) endX, (float) endY);
        arrow.lineTo((float) arrowLeftX, (float) arrowLeftY);
        arrow.lineTo((float) arrowRightX, (float) arrowRightY);
        arrow.closePath();

        g2.setStroke(arrowStroke);
        g2.setPaint(arrowPaint);
        Line2D line = new Line2D.Double(startX, startY, endX, endY);
        g2.draw(line);
        g2.fill(arrow);

        // draw the label
        double labelX = j2DX + (Math.cos(angle) * (baseRadius + labelOffset));
        double labelY = j2DY + (Math.sin(angle) * (baseRadius + labelOffset));
        g2.setFont(font);
        Shape hotspot = TextUtils.calculateRotatedStringBounds(ledgend, g2, (float) labelX, (float) labelY,
                textAnchor, rotationAngle, rotationAnchor);
        g2.setPaint(paint);
        TextUtils.drawRotatedString(ledgend, g2, (float) labelX, (float) labelY, textAnchor, rotationAngle,
                rotationAnchor);
        if (outlineVisible) {
            g2.setStroke(outlineStroke);
            g2.setPaint(outlinePaint);
            g2.draw(hotspot);
        }

        // String toolTip = getToolTipText();
        // String url = getURL();
        // if (toolTip != null || url != null) {
        // addEntity(info, hotspot, rendererIndex, toolTip, url);
        // }

    }

    /**
     * Returns a legend item for the specified series.
     *
     * @param datasetIndex the dataset index (zero-based).
     * @param series       the series index (zero-based).
     * @return A legend item for the series (possibly <code>null</code>). * @see
     * org.jfree.chart.renderer.xy.XYItemRenderer#getLegendItem(int,
     * int)
     */
    public LegendItem getLegendItem(int datasetIndex, int series) {

        // if the renderer isn't assigned to a plot, then we don't have a
        // dataset...
        XYPlot plot = getPlot();
        if (plot == null) {
            return null;
        }

        XYDataset dataset = plot.getDataset(datasetIndex);
        if (dataset == null) {
            return null;
        }

        LegendItem result = null;
        if (getItemVisible(series, 0)) {
            String label = getLegendItemLabelGenerator().generateLabel(dataset, series);
            String description = label;
            String toolTipText = null;
            if (getLegendItemToolTipGenerator() != null) {
                toolTipText = getLegendItemToolTipGenerator().generateLabel(dataset, series);
            }
            String urlText = null;
            if (getLegendItemURLGenerator() != null) {
                urlText = getLegendItemURLGenerator().generateLabel(dataset, series);
            }
            Paint fillPaint = lookupSeriesPaint(series);
            result = new LegendItem(label, description, toolTipText, urlText, getLegendShape(), fillPaint);
            result.setLabelFont(lookupLegendTextFont(series));
            Paint labelPaint = lookupLegendTextPaint(series);
            if (labelPaint != null) {
                result.setLabelPaint(labelPaint);
            }
            result.setSeriesKey(dataset.getSeriesKey(series));
            result.setSeriesIndex(series);
            result.setDataset(dataset);
            result.setDatasetIndex(datasetIndex);
        }

        return result;

    }

    /**
     * Tests this renderer for equality with an arbitrary object. This method
     * returns <code>true</code> if and only if:
     *
     * <ul>
     * <li><code>obj</code> is not <code>null</code>;</li>
     * <li><code>obj</code> is an instance of <code>XYDotRenderer</code>;</li>
     * <li>both renderers have the same attribute values.
     * </ul>
     *
     * @param obj the object (<code>null</code> permitted).
     * @return A boolean.
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof PivotRenderer that)) {
            return false;
        }
        if (this.dotWidth != that.dotWidth) {
            return false;
        }
        if (this.dotHeight != that.dotHeight) {
            return false;
        }
        if (!ShapeUtils.equal(this.legendShape, that.legendShape)) {
            return false;
        }
        return super.equals(obj);
    }

    /**
     * Returns a clone of the renderer.
     *
     * @return A clone. * @throws CloneNotSupportedException if the renderer
     * cannot be cloned. * @see org.jfree.util.PublicCloneable#clone()
     */
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * Provides serialization support.
     *
     * @param stream the input stream.
     * @throws IOException if there is an I/O error. * @throws ClassNotFoundException if
     *                     there is a classpath problem.
     */
    @Serial
    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.legendShape = SerialUtils.readShape(stream);
    }

    /**
     * Provides serialization support.
     *
     * @param stream the output stream.
     * @throws IOException if there is an I/O error.
     */
    @Serial
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtils.writeShape(this.legendShape, stream);
    }

}
