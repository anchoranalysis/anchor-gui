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

package org.anchoranalysis.gui.interactivebrowser.browser;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import org.anchoranalysis.core.log.error.ErrorReporter;
import org.anchoranalysis.gui.IconFactory;

public class SplashScreenTime extends JWindow {

    private static final long serialVersionUID = -88085680922609983L;

    public SplashScreenTime(
            String resourcePath, Frame f, int waitTime, final ErrorReporter errorReporter) {
        super(f);

        IconFactory rf = new IconFactory();
        ImageIcon icon = rf.icon(resourcePath);

        JLabel l = new JLabel(icon);
        getContentPane().add(l, BorderLayout.CENTER);
        pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension labelSize = l.getPreferredSize();
        setLocation(
                screenSize.width / 2 - (labelSize.width / 2),
                screenSize.height / 2 - (labelSize.height / 2));
        addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        setVisible(false);
                        dispose();
                    }
                });
        final int pause = waitTime;
        final Runnable closerRunner =
                () -> {
                    setVisible(false);
                    dispose();
                };
        Runnable waitRunner =
                () -> {
                    try {
                        Thread.sleep(pause);
                        SwingUtilities.invokeAndWait(closerRunner);
                    } catch (InterruptedException e) {
                        // Restore interrupted state
                        Thread.currentThread().interrupt();
                    } catch (Exception e) {
                        errorReporter.recordError(SplashScreenTime.class, e);
                        // can catch InvocationTargetException
                    }
                };
        setVisible(true);
        Thread splashThread = new Thread(waitRunner, "SplashThread");
        splashThread.start();
    }
}
