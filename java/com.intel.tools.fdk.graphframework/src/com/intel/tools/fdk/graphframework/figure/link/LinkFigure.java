/* ============================================================================
 * INTEL CONFIDENTIAL
 *
 * Copyright 2016 Intel Corporation All Rights Reserved.
 *
 * The source code contained or described herein and all documents related to
 * the source code ("Material") are owned by Intel Corporation or its suppliers
 * or licensors. Title to the Material remains with Intel Corporation or its
 * suppliers and licensors. The Material contains trade secrets and proprietary
 * and confidential information of Intel or its suppliers and licensors. The
 * Material is protected by worldwide copyright and trade secret laws and
 * treaty provisions. No part of the Material may be used, copied, reproduced,
 * modified, published, uploaded, posted, transmitted, distributed, or
 * disclosed in any way without Intel's prior express written permission.
 *
 * No license under any patent, copyright, trade secret or other intellectual
 * property right is granted to or conferred upon you by disclosure or delivery
 * of the Materials, either expressly, by implication, inducement, estoppel or
 * otherwise. Any license under such intellectual property rights must be
 * express and approved by Intel in writing.
 * ============================================================================
 */
package com.intel.tools.fdk.graphframework.figure.link;

import org.eclipse.swt.graphics.Color;

import com.intel.tools.fdk.graphframework.figure.IGraphFigure;
import com.intel.tools.fdk.graphframework.figure.ghost.GhostLinkFigure;
import com.intel.tools.fdk.graphframework.graph.ILink;
import com.intel.tools.fdk.graphframework.graph.Style.IStyleListener;

/** Represent a graph link */
public class LinkFigure extends GhostLinkFigure implements IGraphFigure, IStyleListener {

    private final ILink link;

    /**
     * @param link
     *            the represented link
     * @param source
     *            the source anchor
     * @param target
     *            the target anchor
     */
    public LinkFigure(final ILink link, final LinkAnchor source, final LinkAnchor target) {
        super(source, target);
        this.link = link;
        setForegroundColor(link.getStyle().getForeground());
        this.link.getStyle().addListener(this);
    }

    @Override
    public void select() {
        setLineWidth(getLineWidth() + 1);
    }

    @Override
    public void unselect() {
        setLineWidth(getLineWidth() - 1);
    }

    /**
     * @return the link graph element associated to the figure
     */
    public ILink getLink() {
        return link;
    }

    @Override
    public void foregroundUpdated(final Color color) {
        setForegroundColor(link.getStyle().getForeground());
        invalidate();
    }

}