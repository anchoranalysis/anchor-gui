/* (C)2020 */
package org.anchoranalysis.gui.videostats.dropdown;

public class NamedModule {
    private String title;
    private VideoStatsModuleCreatorAndAdder creatorAndAdder;

    // Distinguishes modules of the same type from each other
    private String shortTitle;

    public NamedModule(String title, VideoStatsModuleCreatorAndAdder creatorAndAdder) {
        this(title, creatorAndAdder, title);
    }

    public NamedModule(
            String title, VideoStatsModuleCreatorAndAdder creatorAndAdder, String shortTitle) {
        super();
        this.title = title;
        this.creatorAndAdder = creatorAndAdder;
        this.shortTitle = shortTitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public VideoStatsModuleCreatorAndAdder getCreator() {
        return creatorAndAdder;
    }

    public void setCreator(VideoStatsModuleCreatorAndAdder creator) {
        this.creatorAndAdder = creator;
    }

    public String getShortTitle() {
        return shortTitle;
    }

    public void setShortTitle(String shortTitle) {
        this.shortTitle = shortTitle;
    }
}
