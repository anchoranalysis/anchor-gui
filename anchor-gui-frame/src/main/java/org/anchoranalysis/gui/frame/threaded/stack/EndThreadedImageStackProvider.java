/* (C)2020 */
package org.anchoranalysis.gui.frame.threaded.stack;

import org.anchoranalysis.gui.videostats.module.VideoStatsModuleClosedEvent;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleClosedListener;

class EndThreadedImageStackProvider implements VideoStatsModuleClosedListener {

    private IThreadedProducer producer;

    public EndThreadedImageStackProvider(IThreadedProducer producer) {
        super();
        this.producer = producer;
    }

    @Override
    public void videoStatsModuleClosed(VideoStatsModuleClosedEvent evt) {
        producer.dispose();
    }
}
