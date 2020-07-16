/* (C)2020 */
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
import org.anchoranalysis.core.error.reporter.ErrorReporter;
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
