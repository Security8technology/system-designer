/*
 * Copyright (C) 2013-2017 Intel Corporation
 *
 * This Program is subject to the terms of the Eclipse Public License, v. 1.0.
 * If a copy of the license was not distributed with this file,
 * you can obtain one at <http://www.eclipse.org/legal/epl-v10.html>
 *
 * SPDX-License-Identifier: EPL-1.0
 */
package com.intel.tools.fdk.graphframework.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.intel.tools.fdk.graphframework.displayer.controller.ModelSelectionController;
import com.intel.tools.fdk.graphframework.displayer.controller.ModelSelectionController.IModelSelectionListener;
import com.intel.tools.fdk.graphframework.graph.IGraph;
import com.intel.tools.fdk.graphframework.graph.IGraphElement;
import com.intel.tools.fdk.graphframework.graph.IGroup;
import com.intel.tools.fdk.graphframework.graph.ILeaf;
import com.intel.tools.fdk.graphframework.graph.ILink;
import com.intel.tools.fdk.graphframework.graph.IPin;
import com.intel.tools.utils.IntelPalette;

public class PropertiesPane extends ScrolledComposite {

    private IGraphUIProvider uiProvider;
    private final Composite containerComposite;
    private Object lastSelectedObject = null;

    public PropertiesPane(final Composite parent, final int style, final IGraphUIProvider uiProvider,
            final ModelSelectionController selectionController) {
        super(parent, style | SWT.V_SCROLL);
        setModelSelectionController(selectionController);
        setGraphUIProvider(uiProvider);

        final GridLayout gridLayout = new GridLayout(1, false);
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        setLayout(gridLayout);

        setBackground(IntelPalette.WHITE);
        setBackgroundMode(SWT.INHERIT_FORCE);

        containerComposite = new Composite(this, SWT.NONE);
        setContent(containerComposite);
        setExpandHorizontal(true);

        containerComposite.setBackground(IntelPalette.WHITE);
        containerComposite.setBackgroundMode(SWT.INHERIT_FORCE);

        addListener(SWT.Activate, new Listener() {
            @Override
            public void handleEvent(final Event e) {
                setFocus();
            }
        });
        forceFocus();
    }

    /**
     * Sets the provider for graph items UI.
     *
     * @param uiProvider
     *            The UI provider.
     */
    public void setGraphUIProvider(final IGraphUIProvider uiProvider) {
        this.uiProvider = uiProvider;
    }

    public void setModelSelectionController(final ModelSelectionController selectionController) {
        selectionController.addModelSelectionReleaseListener(new IModelSelectionListener() {
            @Override
            public void graphSelected(final IGraph graph) {
                createUI(graph);
            }

            @Override
            public void groupSelected(final IGroup group) {
                createUI(group);
            }

            @Override
            public void leafSelected(final ILeaf leaf) {
                createUI(leaf);
            }

            @Override
            public void pinSelected(final IPin pin) {
                createUI(pin);
            }

            @Override
            public void linkSelected(final ILink link) {
                createUI(link);
            }

        });
    }

    protected <T extends IGraphElement> void createUI(final T obj) {
        // We execute the UI build in an async UI thread as it may take little time to be sure
        // Any current UI job is finished.
        containerComposite.getDisplay().asyncExec(new Runnable() {

            @Override
            public void run() {
                setRedraw(false);
                if (uiProvider != null && lastSelectedObject != obj) {
                    resetUI();
                    if (obj instanceof ILink) {
                        uiProvider.createUI(containerComposite, (ILink) obj);
                    } else if (obj instanceof ILeaf) {
                        uiProvider.createUI(containerComposite, (ILeaf) obj);
                    } else if (obj instanceof IGroup) {
                        uiProvider.createUI(containerComposite, (IGroup) obj);
                    } else if (obj instanceof IGraph) {
                        uiProvider.createUI(containerComposite, (IGraph) obj);
                    }
                    lastSelectedObject = obj;
                }
                relayout();
                setRedraw(true);
            }

        });
    }

    protected void resetUI() {
        for (final Control control : containerComposite.getChildren()) {
            control.dispose();
        }
    }

    protected void relayout() {
        containerComposite.pack();
        layout();
    }
}
