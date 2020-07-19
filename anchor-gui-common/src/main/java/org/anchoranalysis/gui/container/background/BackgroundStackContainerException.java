package org.anchoranalysis.gui.container.background;

public class BackgroundStackContainerException extends Exception {
    
    public BackgroundStackContainerException(Throwable cause) {
        super(cause);
    }

    public BackgroundStackContainerException(String message) {
        super(message);
    }

    public BackgroundStackContainerException(String message, Throwable exc) {
        super(message, exc);
    }
}
