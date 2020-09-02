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
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.gui.container.background.BackgroundStackContainerException;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.definition.ImageStackContainerFromName;

// A menu which allows changing of the background
class BackgroundSetMenu {

    @Getter private JMenu menu;

    public static class BackgroundChangeAction extends AbstractAction {

        private static final long serialVersionUID = -7238970062538672779L;

        private final String backgroundSetName;
        private final transient ImageStackContainerFromName stackContainerGetter;
        private final transient BackgroundSetter backgroundSetter;
        private final transient ErrorReporter errorReporter;

        public BackgroundChangeAction(String backgroundSetName,
                ImageStackContainerFromName stackContainerGetter, BackgroundSetter backgroundSetter,
                ErrorReporter errorReporter) {
            super(backgroundSetName);
            this.backgroundSetName = backgroundSetName;
            this.stackContainerGetter = stackContainerGetter;
            this.backgroundSetter = backgroundSetter;
            this.errorReporter = errorReporter;
        }
        
        @Override
        public void actionPerformed(ActionEvent arg0) {

            try {
                backgroundSetter.setImageStackContainer(
                        stackContainerGetter.get(backgroundSetName));
            } catch (BackgroundStackContainerException | SetOperationFailedException e) {
                errorReporter.recordError(BackgroundSetMenu.class, e);
                JOptionPane.showMessageDialog(
                        null, String.format("Background set '%s' failed", backgroundSetName));
            }
        }
    }

    private ShowMenuListener showMenuListener;

    @AllArgsConstructor
    private static class ShowMenuListener implements MenuListener {

        private JMenu menu;
        private BackgroundSetter backgroundSetter;
        @Setter private IGetNames nameGetter;
        @Setter private ImageStackContainerFromName stackContainerFromName;
        private ErrorReporter errorReporter;

        private void addItems() {

            for (String name : nameGetter.names()) {
                menu.add(
                        new BackgroundChangeAction(
                                name, stackContainerFromName, backgroundSetter, errorReporter));
            }
        }

        @Override
        public void menuCanceled(MenuEvent arg0) {
            // NOTHING TO DO
        }

        @Override
        public void menuDeselected(MenuEvent arg0) {  
         // NOTHING TO DO
        }

        @Override
        public void menuSelected(MenuEvent arg0) {
            menu.removeAll();
            addItems();
        }
    }

    public BackgroundSetMenu(
            final IGetNames nameGetter,
            ImageStackContainerFromName stackContainerFromName,
            BackgroundSetter backgroundSetter,
            ErrorReporter errorReporter) {
        menu = new JMenu("Background");
        showMenuListener =
                new ShowMenuListener(
                        menu, backgroundSetter, nameGetter, stackContainerFromName, errorReporter);
        menu.addMenuListener(showMenuListener);
    }

    public void update(IGetNames nameGetter, ImageStackContainerFromName stackCntrFromName) {
        showMenuListener.setNameGetter(nameGetter);
        showMenuListener.setStackContainerFromName(stackCntrFromName);
    }
}
