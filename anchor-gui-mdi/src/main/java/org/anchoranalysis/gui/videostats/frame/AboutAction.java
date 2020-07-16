/* (C)2020 */
package org.anchoranalysis.gui.videostats.frame;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
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
