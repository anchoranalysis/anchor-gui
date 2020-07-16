/*-
 * #%L
 * anchor-gui-annotation
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
