/* (C)2020 */
package org.anchoranalysis.gui;

import javax.swing.ImageIcon;

// TODO make sure resources are located in the correct project
public class IconFactory {

    public ImageIcon icon(String resourcePath) {
        java.net.URL imageURL = IconFactory.class.getResource(resourcePath);
        ImageIcon icon = (imageURL != null) ? new ImageIcon(imageURL) : null;
        return icon;
    }
}
