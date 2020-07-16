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

import java.util.List;
import javax.swing.ImageIcon;
import org.anchoranalysis.gui.mark.MarkDisplaySettings;

public class ShowMidpointAction extends ChangeMarkDisplayToggleAction {

    private static final long serialVersionUID = -9105081219776936274L;

    public ShowMidpointAction(
            List<IChangeMarkDisplaySendable> updateList,
            MarkDisplaySettings lastMarkDisplaySettings,
            ImageIcon icon) {
        super(
                "Show Midpoint",
                icon,
                lastMarkDisplaySettings.isShowMidpoint(),
                updateList,
                lastMarkDisplaySettings);
    }

    @Override
    protected void changeMarkDisplay(IChangeMarkDisplaySendable sendable, boolean currentState) {

        getLastMarkDisplaySettings().setShowMidpoint(currentState);
        sendable.setShowMidpoint(currentState);
    }
}
