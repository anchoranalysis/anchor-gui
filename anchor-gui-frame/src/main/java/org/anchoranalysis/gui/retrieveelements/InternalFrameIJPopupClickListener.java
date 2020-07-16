/* (C)2020 */
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
