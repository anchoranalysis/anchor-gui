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

package org.anchoranalysis.gui.videostats.dropdown;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPopupMenu;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.gui.file.opened.IOpenedFileGUI;
import org.anchoranalysis.gui.reassign.JDropdownButton;
import org.anchoranalysis.gui.videostats.operation.VideoStatsOperationFromCreatorAndAdder;
import org.anchoranalysis.gui.videostats.operation.VideoStatsOperationMenu;
import org.anchoranalysis.gui.videostats.threading.InteractiveThreadPool;

public class VideoStatsModuleDropdown implements IOpenedFileGUI {

    // private static Log log = LogFactory.getLog(VideoStatsModuleDropdown.class);

    private int numItems = 0;

    private JDropdownButton dropdownButton;
    private VideoStatsOperationMenu rootMenu;

    public VideoStatsModuleDropdown(String name, ImageIcon icon) {
        this.dropdownButton = new JDropdownButton(name, icon);
        this.rootMenu = new VideoStatsOperationMenu(new DualMenuWrapper(dropdownButton.getPopup()));
    }

    public JDropdownButton getDropdownButton() {
        return dropdownButton;
    }

    // We always treat the first module as the return module
    public VideoStatsModuleCreatorAndAdder addNamedModules(
            NamedModule[] modulesToAdd, InteractiveThreadPool threadPool, Logger logErrorRepoter) {

        VideoStatsModuleCreatorAndAdder ret = null;

        for (NamedModule moduleToAdd : modulesToAdd) {

            VideoStatsOperationFromCreatorAndAdder vso =
                    new VideoStatsOperationFromCreatorAndAdder(
                            moduleToAdd.getTitle(),
                            moduleToAdd.getCreator(),
                            threadPool,
                            logErrorRepoter);

            rootMenu.add(vso);

            numItems++;

            if (ret == null) {
                ret = moduleToAdd.getCreator();
            }
        }

        return ret;
    }

    // We always treat the first module as the return module
    public VideoStatsModuleCreatorAndAdder addNamedModules(
            VideoStatsOperationMenu menu,
            NamedModule[] modulesToAdd,
            InteractiveThreadPool threadPool,
            Logger logger,
            boolean useShortNames) {

        VideoStatsModuleCreatorAndAdder ret = null;

        for (NamedModule moduleToAdd : modulesToAdd) {

            if (useShortNames) {
                menu.add(
                        new VideoStatsOperationFromCreatorAndAdder(
                                moduleToAdd.getShortTitle(),
                                moduleToAdd.getCreator(),
                                threadPool,
                                logger));
            } else {
                menu.add(
                        new VideoStatsOperationFromCreatorAndAdder(
                                moduleToAdd.getTitle(),
                                moduleToAdd.getCreator(),
                                threadPool,
                                logger));
            }
            numItems++;

            if (ret == null) {
                ret = moduleToAdd.getCreator();
            }
        }

        return ret;
    }

    public int getNumItems() {
        return numItems;
    }

    @Override
    public JPopupMenu getPopup() {
        return dropdownButton.getPopup();
    }

    @Override
    public VideoStatsOperationMenu getRootMenu() {
        return rootMenu;
    }

    @Override
    public JButton getButton() {
        return dropdownButton.getButton();
    }

    @Override
    public void executeDefaultOperation() {
        rootMenu.executeDefaultOperation();
    }
}
