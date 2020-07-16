/* (C)2020 */
package org.anchoranalysis.gui.videostats.operation;

import java.util.Optional;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleCreatorAndAdder;
import org.anchoranalysis.gui.videostats.operation.combine.IVideoStatsOperationCombine;
import org.anchoranalysis.gui.videostats.threading.InteractiveThreadPool;

public class VideoStatsOperationFromCreatorAndAdder implements VideoStatsOperation {

    private String name;
    private VideoStatsModuleCreatorAndAdder creatorAndAdder;
    private InteractiveThreadPool threadPool;
    private Logger logger;

    public VideoStatsOperationFromCreatorAndAdder(
            String name,
            VideoStatsModuleCreatorAndAdder creatorAndAdder,
            InteractiveThreadPool threadPool,
            Logger logger) {
        this.name = name;
        this.creatorAndAdder = creatorAndAdder;
        this.threadPool = threadPool;
        this.logger = logger;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void execute(boolean withMessages) {
        creatorAndAdder.createVideoStatsModuleForAdder(threadPool, null, logger);
    }

    @Override
    public Optional<IVideoStatsOperationCombine> getCombiner() {
        return creatorAndAdder.getCreator().getCombiner();
    }
}
