/* (C)2020 */
package org.anchoranalysis.gui.plot.visualvm;

import java.util.Optional;
import javax.swing.JInternalFrame;
import org.anchoranalysis.core.property.IPropertyValueReceivable;
import org.anchoranalysis.core.property.IPropertyValueSendable;
import org.anchoranalysis.gui.plot.panel.ClickableGraphInstance;
import org.anchoranalysis.gui.plot.panel.GraphPanel;
import org.anchoranalysis.gui.videostats.IModuleCreatorDefaultState;
import org.anchoranalysis.gui.videostats.link.LinkModules;
import org.anchoranalysis.gui.videostats.module.VideoStatsModule;

public class InternalFrameGraphAsModule {

    private JInternalFrame frame;
    private ClickableGraphInstance graphInstance;

    public InternalFrameGraphAsModule(String title, ClickableGraphInstance graphInstance) {
        frame =
                new InternalFrameWithPanel(title, new GraphPanel(graphInstance).getPanel())
                        .getFrame();
        this.graphInstance = graphInstance;
    }

    public InternalFrameGraphAsModule(
            String title, ClickableGraphInstance graphInstance, JInternalFrame frame) {
        this.frame = frame;
        this.graphInstance = graphInstance;
    }

    public JInternalFrame getFrame() {
        return frame;
    }

    public IModuleCreatorDefaultState moduleCreator() {
        return defaultFrameState -> {
            VideoStatsModule module = new VideoStatsModule();

            module.setComponent(frame);

            // ISelectFrameSendable
            LinkModules link = new LinkModules(module);
            link.getFrameIndex()
                    .add(
                            maybeFrameReceiver(),
                            maybeFrameSendable(defaultFrameState.getLinkState().getFrameIndex()));

            return module;
        };
    }

    private Optional<IPropertyValueSendable<Integer>> maybeFrameSendable(int defaultIndex) {
        Optional<IPropertyValueSendable<Integer>> send = graphInstance.getSelectFrameSendable();
        send.ifPresent(s -> s.setPropertyValue(defaultIndex, false));
        return send;
    }

    private Optional<IPropertyValueReceivable<Integer>> maybeFrameReceiver() {
        return graphInstance.getSelectFrameReceivable();
    }
}
