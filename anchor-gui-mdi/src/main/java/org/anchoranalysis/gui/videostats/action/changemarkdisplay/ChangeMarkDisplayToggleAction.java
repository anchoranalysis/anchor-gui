/*-
 * #%L
 * anchor-gui-mdi
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

package org.anchoranalysis.gui.videostats.action.changemarkdisplay;

import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import org.anchoranalysis.gui.marks.MarkDisplaySettings;

public abstract class ChangeMarkDisplayToggleAction extends AbstractAction {

    private static final long serialVersionUID = -4367824021242290791L;

    private List<IChangeMarkDisplaySendable> updateList;

    private MarkDisplaySettings lastMarkDisplaySettings;

    public ChangeMarkDisplayToggleAction(
            String title,
            ImageIcon icon,
            boolean defaultState,
            List<IChangeMarkDisplaySendable> updateList,
            MarkDisplaySettings lastMarkDisplaySettings) {
        super("", icon);
        this.updateList = updateList;
        this.lastMarkDisplaySettings = lastMarkDisplaySettings;
        putValue(Action.SELECTED_KEY, defaultState);
        putValue(SHORT_DESCRIPTION, title);
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {

        for (IChangeMarkDisplaySendable sendable : updateList) {
            changeMarkDisplay(sendable, (Boolean) getValue(Action.SELECTED_KEY));
        }
    }

    protected MarkDisplaySettings getLastMarkDisplaySettings() {
        return lastMarkDisplaySettings;
    }

    protected abstract void changeMarkDisplay(
            IChangeMarkDisplaySendable sendable, boolean currentState);
}
