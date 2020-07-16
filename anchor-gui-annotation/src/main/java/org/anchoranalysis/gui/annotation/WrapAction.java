/* (C)2020 */
package org.anchoranalysis.gui.annotation;

import java.awt.event.ActionEvent;
import java.util.function.Consumer;
import javax.swing.AbstractAction;

public class WrapAction extends AbstractAction {

    /** */
    private static final long serialVersionUID = 1L;

    private Consumer<ActionEvent> consumer;

    public WrapAction(Consumer<ActionEvent> consumer) {
        super();
        this.consumer = consumer;
    }

    public WrapAction(String name, Consumer<ActionEvent> consumer) {
        super(name);
        this.consumer = consumer;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        consumer.accept(e);
    }
}
