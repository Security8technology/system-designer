/*
 * Copyright (C) 2013-2017 Intel Corporation
 *
 * This Program is subject to the terms of the Eclipse Public License, v. 1.0.
 * If a copy of the license was not distributed with this file,
 * you can obtain one at <http://www.eclipse.org/legal/epl-v10.html>
 *
 * SPDX-License-Identifier: EPL-1.0
 */
package com.intel.tools.fdk.graphframework.figure.ghost;

import org.eclipse.draw2d.Ellipse;
import org.eclipse.draw2d.PolylineShape;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.graphics.Color;

import com.intel.tools.fdk.graphframework.figure.IGraphFigure;
import com.intel.tools.fdk.graphframework.figure.pin.ArrowFigure;
import com.intel.tools.utils.IntelPalette;

/** Represent a graph pin with no relation with a graph element */
public class GhostPinFigure extends PolylineShape {

    private static final int LINE_WIDTH = 4;

    /** Pin arrow height in {@link IGraphFigure#SIZE_UNIT} */
    private static final int ARROW_SIZE = IGraphFigure.SIZE_UNIT;
    /** Pin connector height in {@link IGraphFigure#SIZE_UNIT} */
    private static final int CONNECTOR_SIZE = IGraphFigure.SIZE_UNIT / 2 + 4;
    private static final int SELECTION_PADDING = 1;

    private final ArrowFigure arrow = new ArrowFigure(ARROW_SIZE, LINE_WIDTH);
    private final Ellipse connector = new Ellipse();

    /**
     * @param pin
     *            the pin graph element represented by this figure
     */
    public GhostPinFigure() {
        setSize(getDesiredWidth(), getDesiredHeight());
        setColor(IntelPalette.GREY);
        setLineWidth(LINE_WIDTH);

        add(arrow);
        add(connector);

        connector.setLineWidth(2);      // That's a workaround for
        connector.setOutline(false);    // a draw2d bug when rendering ellipses
        connector.setAntialias(1);
        connector.setSize(CONNECTOR_SIZE, CONNECTOR_SIZE);
    }

    /**
     * Retrieves the position of the connector center where link will be connected.
     *
     * @return the connector center location in absolute coordinates
     */
    public Point getConnectorCenterLocation() {
        final Point center = connector.getBounds().getCenter();
        translateToAbsolute(center);
        return center;
    }

    private int getDesiredWidth() {
        return ARROW_SIZE + CONNECTOR_SIZE + SELECTION_PADDING * 2;
    }

    private int getDesiredHeight() {
        return Math.max(ARROW_SIZE, CONNECTOR_SIZE) + SELECTION_PADDING * 2;
    }

    protected ArrowFigure getArrow() {
        return arrow;
    }

    protected Ellipse getConnector() {
        return connector;
    }

    public void setColor(final Color color) {
        arrow.setForegroundColor(color);
        arrow.setBackgroundColor(color);
        connector.setBackgroundColor(color);
        connector.setForegroundColor(color);
    }

    /**
     * Layout the figure to represent an output
     *
     * The figure will look like: o>
     */
    protected void setupOutputLayout() {
        // Center the arrow on pin height and put it on the left side of the connector (but still touching it)
        getArrow().setLocation(new Point(SELECTION_PADDING, (getDesiredHeight() - getArrow().getBounds().height) / 2));
        // Center the connector on pin height and put it on the right
        getConnector().setLocation(new Point(
                getDesiredWidth() - getConnector().getBounds().width - SELECTION_PADDING,
                (getDesiredHeight() - getConnector().getBounds().height) / 2));
    }

    /**
     * Layout the figure to represent an input
     *
     * The figure will look like: >o
     */
    protected void setupInputLayout() {
        // Center the connector on pin height and put it on the left
        getConnector().setLocation(new Point(SELECTION_PADDING,
                (getDesiredHeight() - getConnector().getBounds().height) / 2));
        // Center the arrow on pin height and put it on the right
        getArrow().setLocation(new Point(getDesiredWidth() - getArrow().getBounds().width - SELECTION_PADDING,
                (getDesiredHeight() - getArrow().getBounds().height) / 2));
    }

}
