/* (C)2020 */
package org.anchoranalysis.gui.videostats.internalframe.annotator.navigation;

// TODO rename this interface
public interface IToolPanelListenFramework {

    void addToolSwitchedListener(ToolSwitchedListener l);

    boolean isSelectedDelete();
}
