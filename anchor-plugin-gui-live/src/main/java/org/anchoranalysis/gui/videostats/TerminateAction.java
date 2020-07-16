/* (C)2020 */
package org.anchoranalysis.gui.videostats;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import org.anchoranalysis.mpp.sgmn.bean.optscheme.termination.TriggerTerminationCondition;

public class TerminateAction extends AbstractAction {

    private static final long serialVersionUID = -6983742220291326020L;

    private TriggerTerminationCondition terminationCondition;

    public TerminateAction(TriggerTerminationCondition terminationCondition, ImageIcon icon) {
        super("", icon);
        this.terminationCondition = terminationCondition;
        // putValue( Action.SELECTED_KEY, true);

        putValue(SHORT_DESCRIPTION, "Terminate Early");
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        this.terminationCondition.trigger();
    }
}
