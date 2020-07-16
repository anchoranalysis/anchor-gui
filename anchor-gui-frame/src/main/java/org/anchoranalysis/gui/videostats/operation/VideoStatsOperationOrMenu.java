/* (C)2020 */
package org.anchoranalysis.gui.videostats.operation;

public class VideoStatsOperationOrMenu {

    private boolean separator = false;

    private VideoStatsOperation operation = null;
    private VideoStatsOperationMenu menu = null;

    // This constructor means it's a separator
    private VideoStatsOperationOrMenu() {}

    public static VideoStatsOperationOrMenu createAsSeparator() {
        VideoStatsOperationOrMenu out = new VideoStatsOperationOrMenu();
        out.separator = true;
        return out;
    }

    public VideoStatsOperationOrMenu(VideoStatsOperationMenu menu) {
        super();
        this.menu = menu;
    }

    public VideoStatsOperationOrMenu(VideoStatsOperation operation) {
        super();
        this.operation = operation;
    }

    public boolean isOperation() {
        return operation != null;
    }

    public boolean isMenu() {
        return menu != null;
    }

    public VideoStatsOperation getOperation() {
        return operation;
    }

    public VideoStatsOperationMenu getMenu() {
        return menu;
    }

    public boolean isSeparator() {
        return separator;
    }
}
