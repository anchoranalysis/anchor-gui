/* (C)2020 */
package org.anchoranalysis.gui.videostats.dropdown.addoverlays;

import javax.swing.JFrame;
import org.anchoranalysis.anchor.overlay.collection.OverlayCollection;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.event.IRoutableReceivable;
import org.anchoranalysis.core.property.change.PropertyValueChangeEvent;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.gui.videostats.link.LinkModules;
import org.anchoranalysis.gui.videostats.module.VideoStatsModule;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleSubgroup;
import org.anchoranalysis.gui.videostats.threading.InteractiveThreadPool;

public class AdderAddOverlaysWithStack implements IAddVideoStatsModule {

    private final IAddVideoStatsModule delegate;
    private final InteractiveThreadPool threadPool;
    private final ErrorReporter errorReporter;

    public AdderAddOverlaysWithStack(
            IAddVideoStatsModule adder,
            InteractiveThreadPool threadPool,
            ErrorReporter errorReporter) {
        super();
        this.delegate = adder;
        this.errorReporter = errorReporter;
        this.threadPool = threadPool;
    }

    @Override
    public void addVideoStatsModule(VideoStatsModule module) {

        LinkModules link = new LinkModules(module);

        // If we have an OVERLAYERS event, but don't already have a OVERLAYS_WITH_STACK we plug the
        // gap
        //  by adding a stack
        if (link.getOverlays().exists()
                && !link.getOverlaysWithStack().exists()
                && module.getNrgStackGetter() != null) {
            IRoutableReceivable<PropertyValueChangeEvent<OverlayCollection>> rec =
                    link.getOverlays().getReceivable();
            if (rec != null) {

                link.getOverlaysWithStack()
                        .add(
                                new OverlayCollectionWithStackAdaptorRouted(
                                        rec,
                                        module.getNrgStackGetter(),
                                        threadPool,
                                        errorReporter));
            }

        } else {
            System.out.printf("addVideoStatsModule without OVERLAYS_WITH_STACK%n");
        }

        delegate.addVideoStatsModule(module);
    }

    @Override
    public VideoStatsModuleSubgroup getSubgroup() {
        return delegate.getSubgroup();
    }

    @Override
    public JFrame getParentFrame() {
        return delegate.getParentFrame();
    }

    @Override
    public IAddVideoStatsModule createChild() {
        return delegate.createChild();
    }
}
