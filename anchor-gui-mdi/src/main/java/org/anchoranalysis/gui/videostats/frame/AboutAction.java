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

package org.anchoranalysis.gui.videostats.frame;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.anchoranalysis.core.log.error.ErrorReporter;
import org.anchoranalysis.gui.interactivebrowser.browser.SplashScreenTime;

class AboutAction extends AbstractAction {

    private static final long serialVersionUID = 6108379470565819605L;

    private Frame frame;
    private ErrorReporter errorReporter;

    public AboutAction(Frame frame, ErrorReporter errorReporter) {
        super("About");
        // putValue(MNEMONIC_KEY, "x");

        this.frame = frame;
        this.errorReporter = errorReporter;
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {

        // We also do a splash screen - almost indefinitely
        new SplashScreenTime("/appSplash/anchor_splash.png", frame, 1500000, errorReporter);
    }
}
