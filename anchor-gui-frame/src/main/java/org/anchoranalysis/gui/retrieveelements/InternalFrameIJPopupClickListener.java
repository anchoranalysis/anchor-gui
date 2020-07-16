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

package org.anchoranalysis.gui.retrieveelements;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.gui.frame.details.ControllerPopupMenu;

public class InternalFrameIJPopupClickListener extends MouseAdapter {

    private ExportPopupParams popUpParams;
    private IRetrieveElements retriever;
    private List<JMenu> additionalMenuList = new ArrayList<>();
    private ErrorReporter errorReporter;
    private boolean retrieveElementsInPopupEnabled = true;

    private ControllerPopupMenu controllerPopupMenu;

    public InternalFrameIJPopupClickListener(
            ExportPopupParams popUpParams,
            IRetrieveElements retriever,
            ErrorReporter errorReporter) {
        this.popUpParams = popUpParams;
        this.retriever = retriever;
        this.errorReporter = errorReporter;
        assert (popUpParams != null);

        controllerPopupMenu =
                new ControllerPopupMenu() {

                    @Override
                    public void addAdditionalMenu(JMenu menu) {
                        additionalMenuList.add(menu);
                    }

                    @Override
                    public void setRetrieveElementsInPopupEnabled(boolean r) {
                        retrieveElementsInPopupEnabled = r;
                    }
                };
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger()) doPop(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) doPop(e);
    }

    private void doPop(MouseEvent e) {

        JPopupMenu popUp = new JPopupMenu();

        boolean menuAdded = false;

        if (retrieveElementsInPopupEnabled) {
            ExportSubMenu exportSubMenu = new ExportSubMenu(popUpParams, errorReporter);
            RetrieveElements re = retriever.retrieveElements();
            re.addToPopUp(exportSubMenu);
            popUp.add(exportSubMenu.getMenu());
            menuAdded = true;
        }

        for (JMenu menu : additionalMenuList) {
            popUp.add(menu);
            menuAdded = true;
        }

        if (menuAdded) {
            popUp.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    public ControllerPopupMenu controllerPopupMenu() {
        return controllerPopupMenu;
    }
}
