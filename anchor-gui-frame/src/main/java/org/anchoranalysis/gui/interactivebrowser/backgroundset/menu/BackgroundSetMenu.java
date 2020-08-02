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

package org.anchoranalysis.gui.interactivebrowser.backgroundset.menu;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.gui.container.background.BackgroundStackContainerException;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.definition.ImageStackContainerFromName;

// A menu which allows changing of the background
class BackgroundSetMenu {

    private JMenu menu;

    @AllArgsConstructor
    public static class BackgroundChangeAction extends AbstractAction {

        private static final long serialVersionUID = -7238970062538672779L;

        private String backgroundSetName;
        private ImageStackContainerFromName stackCntrGetter;
        private IBackgroundSetter backgroundSetter;
        private ErrorReporter errorReporter;

        @Override
        public void actionPerformed(ActionEvent arg0) {

            try {
                backgroundSetter.setImageStackCntr(
                        stackCntrGetter.imageStackCntrFromName(backgroundSetName));
            } catch (BackgroundStackContainerException | SetOperationFailedException e) {
                errorReporter.recordError(BackgroundSetMenu.class, e);
                JOptionPane.showMessageDialog(
                        null, String.format("Background set '%s' failed", backgroundSetName));
            }
        }
    }

    private ShowMenuListener showMenuListener;

    private static class ShowMenuListener implements MenuListener {

        private IGetNames nameGetter;
        private ImageStackContainerFromName stackCntrFromName;
        private IBackgroundSetter backgroundSetter;
        private JMenu menu;
        private ErrorReporter errorReporter;

        public ShowMenuListener(
                JMenu menu,
                IBackgroundSetter backgroundSetter,
                IGetNames nameGetter,
                ImageStackContainerFromName stackCntrFromName,
                ErrorReporter errorReporter) {
            this.menu = menu;
            this.nameGetter = nameGetter;
            this.backgroundSetter = backgroundSetter;
            this.stackCntrFromName = stackCntrFromName;
            this.errorReporter = errorReporter;
        }

        private void addItems() {

            for (String name : nameGetter.names()) {
                menu.add(
                        new BackgroundChangeAction(
                                name, stackCntrFromName, backgroundSetter, errorReporter));
            }
        }

        public void setNameGetter(IGetNames nameGetter) {
            this.nameGetter = nameGetter;
        }

        public void setStackCntrFromName(ImageStackContainerFromName stackCntrFromName) {
            this.stackCntrFromName = stackCntrFromName;
        }

        @Override
        public void menuCanceled(MenuEvent arg0) {}

        @Override
        public void menuDeselected(MenuEvent arg0) {}

        @Override
        public void menuSelected(MenuEvent arg0) {
            menu.removeAll();
            addItems();
        }
    }

    public BackgroundSetMenu(
            final IGetNames nameGetter,
            ImageStackContainerFromName stackCntrFromName,
            IBackgroundSetter backgroundSetter,
            ErrorReporter errorReporter) {
        assert (backgroundSetter != null);
        menu = new JMenu("Background");
        showMenuListener =
                new ShowMenuListener(
                        menu, backgroundSetter, nameGetter, stackCntrFromName, errorReporter);
        menu.addMenuListener(showMenuListener);
    }

    public JMenu getMenu() {
        return menu;
    }

    public void update(IGetNames nameGetter, ImageStackContainerFromName stackCntrFromName) {
        showMenuListener.setNameGetter(nameGetter);
        showMenuListener.setStackCntrFromName(stackCntrFromName);
    }
}
