/*-
 * #%L
 * anchor-gui-mdi
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

package org.anchoranalysis.gui.videostats.frame;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.EventListenerList;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import org.anchoranalysis.core.log.error.ErrorReporter;
import org.anchoranalysis.gui.IconFactory;
import org.anchoranalysis.gui.marks.MarkDisplaySettings;
import org.anchoranalysis.gui.mdi.IArrangeFrames;
import org.anchoranalysis.gui.mdi.PartitionedFrameList;
import org.anchoranalysis.gui.mdi.WindowMenu;
import org.anchoranalysis.gui.mdi.action.SmartArrangeAction;
import org.anchoranalysis.gui.toolbar.VideoStatsToolbar;
import org.anchoranalysis.gui.videostats.ModuleEventRouter;
import org.anchoranalysis.gui.videostats.SubgroupRetriever;
import org.anchoranalysis.gui.videostats.VideoStatsDesktopPane;
import org.anchoranalysis.gui.videostats.action.changemarkdisplay.IChangeMarkDisplaySendable;
import org.anchoranalysis.gui.videostats.link.LinkedPropertiesAmongModules;
import org.anchoranalysis.gui.videostats.module.VideoStatsModule;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleClosedEvent;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleClosedListener;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleSubgroup;
import org.anchoranalysis.gui.videostats.threading.InteractiveThreadPool;

public class VideoStatsFrame extends JFrame implements IGetToolbar {

    private static final long serialVersionUID = -1715371432214364539L;
    private VideoStatsDesktopPane desktopPane;

    private ArrayList<IChangeMarkDisplaySendable> changeMarkDisplayUpdateList = new ArrayList<>();

    private PartitionedFrameList partitionedFrames = new PartitionedFrameList();

    private LinkedPropertiesAmongModules linkedProperties;

    private SubgroupRetriever subgroupRetriever = new SubgroupRetriever();

    private EventListenerList eventListenerList = new EventListenerList();

    private HashMap<JInternalFrame, VideoStatsModule> frameMap = new HashMap<>();

    private VideoStatsToolbar toolbar;

    private MarkDisplaySettings lastMarkDisplaySettings = new MarkDisplaySettings();

    private ModuleEventRouter moduleEventRouter;

    private InteractiveThreadPool threadPool =
            new InteractiveThreadPool(
                    new InteractiveThreadPool.AddProgressBarInternalFrame() {

                        @Override
                        public void addInternalFrame(JInternalFrame frame) {
                            desktopPane.addInternalFrame(frame);
                        }
                    });

    private JMenu fileMenu;
    private List<Action> listFileActions = new ArrayList<>();

    public VideoStatsFrame(String subtitle) {
        super("Anchor - " + subtitle);

        desktopPane = new VideoStatsDesktopPane();
        setIconImage(new IconFactory().icon("/appIcon/anchorLogo40.png").getImage());

        moduleEventRouter = new ModuleEventRouter(subgroupRetriever);

        toolbar = new VideoStatsToolbar();
    }

    public void initBeforeAddingFrames(ErrorReporter errorReporter) {

        setSize(1200, 800);
        // setLocation(100, 100);
        setLocationByPlatform(true);
        setVisible(true);
        setMinimumSize(new Dimension(800, 600));

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.getViewport().add(desktopPane);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.setMinimumSize(new Dimension(800, 600));

        setVisible(true);
        setContentPane(panel);

        // Graphics Environment
        setExtendedState(this.getExtendedState() | Frame.MAXIMIZED_BOTH);

        setupMenuBar(errorReporter);

        // We create our Toolbar

        toolbar.addWindowButtons(desktopPane, partitionedFrames);
        toolbar.addSeparator();
        addLinkToggleButtons();
        toolbar.addSeparator();
        toolbar.addDisplayToggleButtons(
                this.changeMarkDisplayUpdateList, this.lastMarkDisplaySettings);

        panel.add(toolbar.getDelegate(), BorderLayout.NORTH);

        addWindowListener(
                new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        ExitUtilities.exit();
                    }
                });
    }

    private void setupMenuBar(ErrorReporter errorReporter) {

        // MENU Bar
        JMenuBar menuBar = new JMenuBar();

        fileMenu = new JMenu("File");
        fileMenu.setMnemonic('f');

        for (Action a : listFileActions) {
            fileMenu.add(new JMenuItem(a));
        }

        if (listFileActions.size() > 0) {
            fileMenu.addSeparator();
        }

        fileMenu.add(new JMenuItem(new ExitAction()));

        WindowMenu windowMenu =
                new WindowMenu(
                        desktopPane,
                        new IArrangeFrames() {
                            @Override
                            public void arrange() {
                                smartArrange();
                            }
                        },
                        errorReporter);

        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic('h');
        helpMenu.add(new JButton(new AboutAction(this, errorReporter)));

        menuBar.add(fileMenu);
        menuBar.add(windowMenu);
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    private void addLinkToggleButtons() {

        this.linkedProperties =
                new LinkedPropertiesAmongModules(moduleEventRouter, subgroupRetriever);

        toolbar.addToggleButtonFromActionList(linkedProperties.createActionList());
    }

    private void smartArrange() {
        SmartArrangeAction.arrangeDefaultView(desktopPane, partitionedFrames);
    }

    public void showWithDefaultView() {
        setVisible(true);
        smartArrange();
    }

    private class ModuleClosedListener extends InternalFrameAdapter {

        @Override
        public void internalFrameClosed(InternalFrameEvent e) {
            super.internalFrameClosed(e);

            // We should remove the module from any open lists, by retrieving the module associated
            //   with the window
            VideoStatsModule module = frameMap.get(e.getSource());

            if (module.isFixedSize()) {
                partitionedFrames.getFixedFrames().remove(module.getComponent());
            } else {
                partitionedFrames.getDynamicFrames().remove(module.getComponent());
            }

            VideoStatsModuleSubgroup subgroup = subgroupRetriever.get(module);
            subgroup.removeModule(module);

            //			if (subgroup.size()==0) {
            //				// If there are no modules left in the subgroup we remove from the retriever
            //				subgroupRetriever.remove(module);
            //
            //			}

            subgroupRetriever.remove(module);

            linkedProperties.removeModule(module);

            moduleEventRouter.remove(module);

            module.triggerModuleClosedEvents();

            frameMap.remove(e.getSource());

            module.setComponent(null);

            // We trigger a ModuleClosedEvent for other objects to react to
            for (VideoStatsModuleClosedListener l :
                    eventListenerList.getListeners(VideoStatsModuleClosedListener.class)) {
                l.videoStatsModuleClosed(new VideoStatsModuleClosedEvent(this, module));
            }
        }
    }

    public synchronized void addVideoStatsModuleVisible(
            VideoStatsModule module, VideoStatsModuleSubgroup subgroup) {

        if (module == null) {
            return;
        }

        addVideoStatsModule(module, subgroup);
        module.getComponent().setVisible(true);
    }

    private synchronized void addVideoStatsModule(
            VideoStatsModule module, VideoStatsModuleSubgroup subgroup) {

        // If we have a null module, then there's nothing to add
        if (module == null) {
            return;
        }

        assert (subgroup != null);

        subgroupRetriever.add(module, subgroup);
        subgroup.addModule(module);

        subgroup.getDefaultModuleState().setMarkDisplaySettings(this.lastMarkDisplaySettings);

        module.getComponent().addInternalFrameListener(new ModuleClosedListener());

        if (module.isFixedSize()) {
            this.partitionedFrames.getFixedFrames().add(module.getComponent());
            // this.partitionedFrames.getDynamicFrames().add( module.getComponent() );
        } else {
            this.partitionedFrames.getDynamicFrames().add(module.getComponent());
        }

        if (module.getChangeMarkDisplaySendable() != null) {
            this.changeMarkDisplayUpdateList.add(module.getChangeMarkDisplaySendable());
        }

        linkedProperties.addModule(module);

        frameMap.put(module.getComponent(), module);
        moduleEventRouter.add(module);

        // We send our current default index

        module.getComponent().pack();

        // module.getComponent().setLocation(100, 100);

        desktopPane.addInternalFrame(module.getComponent());
        module.getComponent().toFront();
    }

    @Override
    public VideoStatsToolbar getToolbar() {
        return toolbar;
    }

    public int getLastFrameIndex() {
        return linkedProperties.getLastFrameIndex();
    }

    public void setFrameIndex(int index) {
        linkedProperties.setFrameIndex(index);
    }

    @Override
    public void paint(Graphics g) {

        super.paint(g);
    }

    public void addSeparator() {
        toolbar.addSeparator();
    }

    public synchronized void addVideoStatsModuleClosedListener(
            VideoStatsModuleClosedListener listener) {
        eventListenerList.add(VideoStatsModuleClosedListener.class, listener);
    }

    public List<Action> getListFileActions() {
        return listFileActions;
    }

    public InteractiveThreadPool getThreadPool() {
        return threadPool;
    }

    public void setThreadPool(InteractiveThreadPool threadPool) {
        this.threadPool = threadPool;
    }

    public JInternalFrame selectFrame(boolean forward) {
        return desktopPane.selectFrame(forward);
    }

    public MarkDisplaySettings getLastMarkDisplaySettings() {
        return lastMarkDisplaySettings;
    }
}
