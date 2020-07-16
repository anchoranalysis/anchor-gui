/* (C)2020 */
package org.anchoranalysis.gui.videostats.operation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.anchoranalysis.gui.videostats.operation.combine.IVideoStatsOperationCombine;

public class VideoStatsOperationSequence implements VideoStatsOperation {

    private List<VideoStatsOperation> delegate = new ArrayList<>();

    private String name;

    public VideoStatsOperationSequence(String name) {
        super();
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public void add(VideoStatsOperation operation) {
        delegate.add(operation);
    }

    public void addAll(List<VideoStatsOperation> list) {
        delegate.addAll(list);
    }

    public List<VideoStatsOperation> getList() {
        return delegate;
    }

    @Override
    public void execute(boolean withMessages) {

        for (VideoStatsOperation op : delegate) {
            op.execute(withMessages);
        }
    }

    @Override
    public Optional<IVideoStatsOperationCombine> getCombiner() {
        return Optional.empty();
    }
}
