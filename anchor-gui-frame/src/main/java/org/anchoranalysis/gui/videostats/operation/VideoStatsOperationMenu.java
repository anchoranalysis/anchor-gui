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

package org.anchoranalysis.gui.videostats.operation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.MenuListener;
import org.anchoranalysis.gui.videostats.dropdown.DualMenuWrapper;

// A menu allowing modules to be called
public class VideoStatsOperationMenu {

    // One or other of these should be non-null
    private DualMenuWrapper menu;

    // The parent menu, in case we need to back to the root node
    @SuppressWarnings("unused")
    private VideoStatsOperationMenu parent;

    // A list of all operations added
    private List<VideoStatsOperationOrMenu> listOperations = new ArrayList<>();

    private VideoStatsOperation defaultOperation = null;

    // ROOT
    public VideoStatsOperationMenu(DualMenuWrapper menu) {
        super();
        this.menu = menu;
        this.parent = null;
    }

    // ROOT
    private VideoStatsOperationMenu(DualMenuWrapper menu, VideoStatsOperationMenu parent) {
        super();
        this.menu = menu;
        this.parent = parent;
    }

    public VideoStatsOperationMenu getOrCreateSubMenu(
            String name, boolean includeInListOperations) {
        VideoStatsOperationMenu menu = getSubMenu(name);
        if (menu != null) {
            return menu;
        }
        return createSubMenu(name, includeInListOperations);
    }

    // The only way to create a sub menu
    public VideoStatsOperationMenu createSubMenu(String name, boolean includeInListOperations) {

        JMenu menuNew = new JMenu(name);

        VideoStatsOperationMenu subMenuNew =
                new VideoStatsOperationMenu(new DualMenuWrapper(menuNew), this);
        this.menu.add(menuNew);

        if (includeInListOperations) {
            listOperations.add(new VideoStatsOperationOrMenu(subMenuNew));
        }

        return subMenuNew;
    }

    // Returns null if none foubd
    public VideoStatsOperationMenu getSubMenu(String name) {
        for (VideoStatsOperationOrMenu op : listOperations) {
            if (op.isMenu() && op.getMenu().getName().equals(name)) {
                return op.getMenu();
            }
        }
        return null;
    }

    public void addSeparator() {
        menu.addSeparator();
        listOperations.add(VideoStatsOperationOrMenu.createAsSeparator());
    }

    public void addAsDefault(VideoStatsOperation op) {
        this.defaultOperation = op;
        add(op);
    }

    public void add(VideoStatsOperation op) {

        JMenuItem item = new JMenuItem(op.getName());
        item.addActionListener(new ExecuteActionListener(op));

        listOperations.add(new VideoStatsOperationOrMenu(op));

        this.menu.add(item);
    }

    private static class ExecuteActionListener implements ActionListener {

        private VideoStatsOperation op;

        public ExecuteActionListener(VideoStatsOperation op) {
            super();
            this.op = op;
        }

        @Override
        public void actionPerformed(ActionEvent arg0) {
            op.execute(true);
        }
    }

    public void addMenuListener(MenuListener l) {
        menu.addMenuListener(l);
    }

    // Better if we made this a read-only iterator, but for now we leave it like this
    public List<VideoStatsOperationOrMenu> getListOperations() {
        return listOperations;
    }

    public String getName() {
        return menu.getText();
    }

    public void executeDefaultOperation() {
        if (defaultOperation != null) {
            defaultOperation.execute(true);
        }
    }
}
