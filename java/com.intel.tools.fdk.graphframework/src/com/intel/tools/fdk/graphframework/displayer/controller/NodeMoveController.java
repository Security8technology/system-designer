/*
 * Copyright (C) 2013-2017 Intel Corporation
 *
 * This Program is subject to the terms of the Eclipse Public License, v. 1.0.
 * If a copy of the license was not distributed with this file,
 * you can obtain one at <http://www.eclipse.org/legal/epl-v10.html>
 *
 * SPDX-License-Identifier: EPL-1.0
 */
package com.intel.tools.fdk.graphframework.displayer.controller;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.GhostImageFigure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

import com.intel.tools.fdk.graphframework.displayer.GraphDisplayer;
import com.intel.tools.fdk.graphframework.figure.IGraphFigure;
import com.intel.tools.fdk.graphframework.figure.node.GroupBodyFigure;
import com.intel.tools.fdk.graphframework.figure.node.LeafBodyFigure;

/**
 * Controller allowing to move a figure which is in the content layer.</br>
 * Only Node Figure of the {@link GraphDisplayer} will be moved.
 */
public class NodeMoveController {

    private static final TypeTreeSearch NODE_BODY_SEARCHER = new TypeTreeSearch(LeafBodyFigure.class);
    private static final TypeTreeSearch GROUP_BODY_SEARCHER = new TypeTreeSearch(GroupBodyFigure.class);

    /** Ghost alpha value in range 0-255 */
    private static final int GHOST_ALPHA = 128;

    public interface FigureMoveListener {
        /** This event is fired when the movement has ended (once the mouse is released) */
        void figureMoved(IFigure figure, Point destination);
    }

    /** The clicked figure which is moving */
    private IFigure movedFigure;
    /** The Ghost displayed on the feedback layer during the move */
    private GhostImageFigure ghost;
    /** The figure really moved during drag (can be movingGhost or movedFigure) */
    private IFigure movingFigure;
    /** Offset between the mouse click and the clicked Figure */
    private final Dimension offset = new Dimension(0, 0);
    /** Indicate if the ghost is visible or not */
    private boolean ghostVisible = false;
    /** Snap figure to grid */
    private boolean snapToGrid;

    private final List<FigureMoveListener> listeners = new ArrayList<>();

    /**
     * @param displayer
     *            the displayer which will allow component move
     */
    public NodeMoveController(final GraphDisplayer displayer) {
        displayer.getContentLayer().addMouseListener(new MouseListener.Stub() {
            @Override
            public void mouseReleased(final MouseEvent event) {
                if (movedFigure != null) {
                    displayer.getFeedbackLayer().remove(ghost);
                    final Point destination = new Point(event.x, event.y);
                    final Rectangle figureBounds = movingFigure.getBounds().getCopy();

                    if (snapToGrid) {
                        final Point adjustedTopLeft = adjustToGrid();
                        destination.setLocation(adjustedTopLeft);
                        figureBounds.setLocation(adjustedTopLeft);
                    }

                    fireFigureMoved(movedFigure, destination);
                    movedFigure.setBounds(figureBounds);

                    // Reset state
                    offset.setWidth(0);
                    offset.setHeight(0);

                    // Remove the ghost
                    displayer.getContentLayer().repaint();
                    movedFigure = null;
                }
            }

            @Override
            public void mousePressed(final MouseEvent event) {
                movedFigure = displayer.getContentLayer().findFigureAt(event.getLocation().x, event.getLocation().y,
                        NODE_BODY_SEARCHER);
                if (movedFigure == null) {
                    movedFigure = displayer.getContentLayer().findFigureAt(event.getLocation().x, event.getLocation().y,
                            GROUP_BODY_SEARCHER);
                }
                if (movedFigure != null) {
                    ghost = new GhostImageFigure(movedFigure, GHOST_ALPHA, null);
                    ghost.setVisible(ghostVisible);

                    // We have something to move, let's consume the event
                    event.consume();
                    ghost.setBounds(movedFigure.getBounds().getCopy());
                    displayer.getFeedbackLayer().add(ghost);

                    // Get the click position
                    offset.setWidth(event.x - movedFigure.getBounds().x);
                    offset.setHeight(event.y - movedFigure.getBounds().y);
                    movingFigure = ghostVisible ? ghost : movedFigure;
                }
            }
        });
        displayer.getContentLayer().addMouseMotionListener(new MouseMotionListener.Stub() {
            @Override
            public void mouseDragged(final MouseEvent event) {
                if (movedFigure != null) {
                    final Rectangle bounds = movingFigure.getBounds().getCopy();
                    bounds.setLocation(event.x - offset.width(), event.y - offset.height());
                    movingFigure.setBounds(bounds);
                }
            }
        });
    }

    /**
     * @param visible
     *            true if the ghost should be moved and visible, false if the ghost should be hidden and static
     */
    public void setGhostVisible(final boolean visible) {
        this.ghostVisible = visible;
    }

    /**
     * @param snapToGrid
     *            if true, figured moved position is adjusted to snap to grid.
     */
    public void setSnapToGrid(final boolean snapToGrid) {
        this.snapToGrid = snapToGrid;
    }

    private void fireFigureMoved(final IFigure figure, final Point destination) {
        listeners.forEach(listener -> listener.figureMoved(figure, destination));
    }

    public void addFigureMoveListener(final FigureMoveListener listener) {
        listeners.add(listener);
    }

    public void removeFigureMoveListener(final FigureMoveListener listener) {
        listeners.remove(listener);
    }

    /**
     * Calculate adjusted point based on @SIZE_UNIT@ as grid unit to snap to grid.
     *
     * @return snapped point object.
     */
    private Point adjustToGrid() {
        return new Point(
                (int) ((Math.round(
                        movingFigure.getBounds().getTopLeft().x / (double) IGraphFigure.SIZE_UNIT))
                        * IGraphFigure.SIZE_UNIT),
                (int) ((Math.round(
                        movingFigure.getBounds().getTopLeft().y / (double) IGraphFigure.SIZE_UNIT))
                        * IGraphFigure.SIZE_UNIT));
    }

}
