/* (C)2020 */
package org.anchoranalysis.gui.cfgnrgtable;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

// Copies from web. For copying a string onto both internal and external clipboard
//  On internal clipboard it can stay as a String, on external clipboard it is changed appropriately
class StringTransferable implements Transferable, ClipboardOwner {

    @SuppressWarnings("deprecation")
    private static final DataFlavor plainTextFlavor = DataFlavor.plainTextFlavor;

    private static final DataFlavor localStringFlavor = DataFlavor.stringFlavor;

    private static final DataFlavor[] flavors = {
        StringTransferable.plainTextFlavor, StringTransferable.localStringFlavor
    };

    private static final List<DataFlavor> flavorList = Arrays.asList(flavors);

    private String string;

    public StringTransferable(String string) {
        super();
        this.string = string;
    }

    public synchronized DataFlavor[] getTransferDataFlavors() {
        return flavors;
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return (flavorList.contains(flavor));
    }

    public synchronized Object getTransferData(DataFlavor flavor)
            throws UnsupportedFlavorException, IOException {

        if (flavor.equals(StringTransferable.plainTextFlavor)) {
            String charset = flavor.getParameter("charset").trim();
            if (charset.equalsIgnoreCase("unicode")) {
                // System.out.println("returning unicode charset");
                // uppercase U in Unicode here!
                return new ByteArrayInputStream(this.string.getBytes("Unicode"));
            } else {
                // System.out.println("returning latin-1 charset");
                return new ByteArrayInputStream(this.string.getBytes("iso8859-1"));
            }
        } else if (StringTransferable.localStringFlavor.equals(flavor)) {
            return this.string;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }

    @Override
    public void lostOwnership(Clipboard arg0, Transferable arg1) {}
}
