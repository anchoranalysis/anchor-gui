/*-
 * #%L
 * anchor-gui-frame
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

package org.anchoranalysis.gui.frame.details;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.index.IndexGettableSettable;
import org.anchoranalysis.core.index.container.BoundedRangeIncompleteDynamic;
import org.anchoranalysis.gui.displayupdate.DisplayUpdateRememberStack;
import org.anchoranalysis.gui.frame.details.canvas.ControllerAction;
import org.anchoranalysis.gui.frame.details.canvas.InitialSliderState;
import org.anchoranalysis.gui.frame.details.canvas.InternalFrameCanvas;
import org.anchoranalysis.gui.frame.details.canvas.controller.imageview.ControllerImageView;
import org.anchoranalysis.gui.image.frame.ISliderState;
import org.anchoranalysis.gui.retrieveelements.IRetrieveElements;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.image.extent.ImageDimensions;

// TODO refactor
public class InternalFrameWithDetailsTopPanel {

    private InternalFrameCanvas delegate;

    private JLabel detailsLabel;
    private JLabel rightLabel;
    private StringHelper stringConstructor;

    // We reference these so we can clean-up more easily than if they are stuck in buttons somewhere
    private ZoomInAction zoomInAction;
    private ZoomOutAction zoomOutAction;

    public InternalFrameCanvas getFrameCanvas() {
        return delegate;
    }

    public InternalFrameWithDetailsTopPanel(String frameName) {
        delegate = new InternalFrameCanvas(frameName);
    }

    public ISliderState init(
            BoundedRangeIncompleteDynamic indexBounds,
            IndexGettableSettable indexCntr,
            DisplayUpdateRememberStack stackProvider,
            InitialSliderState initialSliceState,
            IRetrieveElements elementRetriever,
            VideoStatsModuleGlobalParams mpg)
            throws InitException {

        setupTopPanel();

        ISliderState sliderState =
                delegate.init(
                        indexBounds,
                        indexCntr,
                        stackProvider,
                        initialSliceState,
                        elementRetriever,
                        mpg);

        stringConstructor = new StringHelper(delegate, sliderState);
        delegate.addMouseMotionListener(
                new UpdateMouseMovedLabel(stringConstructor, detailsLabel, delegate), false);

        StackChangedOrMouseExited stackChangedOrMouseExited =
                new StackChangedOrMouseExited(stringConstructor, detailsLabel);
        delegate.controllerAction().mouse().addMouseListener(stackChangedOrMouseExited, false);
        stackProvider.addChangeListener(stackChangedOrMouseExited);

        delegate.controllerAction()
                .frame()
                .addInternalFrameListener(new DisposeCanvasWhenFrameIsClosed());

        return sliderState;
    }

    public void setIndexSliderVisible(boolean visibility) {
        delegate.setIndexSliderVisible(visibility);
    }

    public ImageDimensions dimensions() {
        return delegate.dimensions();
    }

    public void flush() {
        delegate.flush();
    }

    public IRetrieveElements getElementRetriever() {
        return delegate.getElementRetriever();
    }

    // Detail generator
    public boolean addAdditionalDetails(GenerateExtraDetail arg0) {
        return stringConstructor.addAdditionalDetails(arg0);
    }

    public ControllerImageView controllerImageView() {
        return delegate.controllerImageView();
    }

    public ControllerPopupMenu controllerPopupMenu() {
        return delegate.controllerPopupMenu();
    }

    public ControllerAction controllerAction() {
        return delegate.controllerAction();
    }

    private class DisposeCanvasWhenFrameIsClosed extends InternalFrameAdapter {

        public DisposeCanvasWhenFrameIsClosed() {
            super();
        }

        @Override
        public void internalFrameClosed(InternalFrameEvent e) {
            zoomInAction.dispose();
            zoomOutAction.dispose();
            zoomInAction = null;
            zoomOutAction = null;
            stringConstructor = null;
            detailsLabel = null;
            rightLabel = null;
        }
    }

    // More configuration of top-panel
    private void setupTopPanelInner(JPanel topPanel, Font font) {
        zoomInAction = new ZoomInAction(delegate);
        zoomOutAction = new ZoomOutAction(delegate);

        JButton buttonZoomIn = new JButton(zoomInAction);
        JButton buttonZoomOut = new JButton(zoomOutAction);

        buttonZoomIn.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        buttonZoomOut.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        JPanel zoomPanel = new JPanel();
        zoomPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        zoomPanel.setLayout(new GridBagLayout());

        rightLabel = createLabel(font);
        rightLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));

        {
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weightx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;

            rightLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            zoomPanel.add(rightLabel, gbc);
        }

        {
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 1;
            gbc.gridy = 0;
            zoomPanel.add(buttonZoomIn, gbc);
        }

        {
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 2;
            gbc.gridy = 0;
            zoomPanel.add(buttonZoomOut, gbc);
        }

        topPanel.add(zoomPanel, BorderLayout.EAST);
    }

    private static JLabel createLabel(Font font) {
        // Let's set up the panel
        JLabel label = new JLabel(" ");
        label.setFont(font);
        return label;
    }

    // Refactor into separate class
    private void setupTopPanel() {
        JPanel topPanel = new JPanel();
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        topPanel.setLayout(new BorderLayout());

        Font font = new Font("Monospaced", Font.PLAIN, 10);
        detailsLabel = createLabel(font);
        topPanel.add(detailsLabel, BorderLayout.WEST);

        setupTopPanelInner(topPanel, font);

        delegate.controllerAction().order().setAsTopComponent(topPanel);
    }
}
