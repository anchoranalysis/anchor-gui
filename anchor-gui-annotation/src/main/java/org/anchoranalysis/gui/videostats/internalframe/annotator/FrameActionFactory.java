/* (C)2020 */
package org.anchoranalysis.gui.videostats.internalframe.annotator;

import com.google.common.base.Supplier;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import javax.swing.JInternalFrame;
import org.anchoranalysis.gui.annotation.WrapAction;

public class FrameActionFactory {

    public static ActionListener listener(Runnable exec) {
        return new WrapAction(e -> exec.run());
    }

    public static ActionListener listenerCloseFrame(JInternalFrame frame, Runnable exec) {
        return listener(
                () -> {
                    exec.run();
                    closeFrame(frame);
                });
    }

    public static AbstractAction actionCloseFrame(
            JInternalFrame frame, String name, Supplier<Boolean> exec) {
        return action(
                name,
                () -> {
                    if (exec.get()) {
                        closeFrame(frame);
                    }
                });
    }

    private static AbstractAction action(String name, Runnable exec) {
        return new WrapAction(name, e -> exec.run());
    }

    private static void closeFrame(JInternalFrame frame) {
        frame.setVisible(false);
        frame.dispose();
    }
}
