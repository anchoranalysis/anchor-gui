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
package org.anchoranalysis.gui.mdi;



import javax.swing.DefaultDesktopManager;
import javax.swing.JComponent;

import java.awt.Insets;
import java.awt.Dimension;

import javax.swing.JScrollPane;
import javax.swing.JInternalFrame;
import javax.swing.JViewport;

  private static final long serialVersionUID = 4323947782956971576L;

  private MDIDesktopPane desktop;

  public MDIDesktopManager(MDIDesktopPane desktop) {
    this.desktop = desktop;
  }

  @Override
public void endResizingFrame(JComponent f) {
    super.endResizingFrame(f);
    resizeDesktop();
  }

  @Override
public void endDraggingFrame(JComponent f) {
    super.endDraggingFrame(f);

    resizeDesktop();
  }

  public void setNormalSize() {
    JScrollPane scrollPane = getScrollPane();
    int x = 0;
    int y = 0;
    Insets scrollInsets = getScrollPaneInsets();

    if (scrollPane != null) {
      Dimension d = scrollPane.getVisibleRect().getSize();

      if (scrollPane.getBorder() != null) {
        d.setSize(d.getWidth() - scrollInsets.left - scrollInsets.right,
        d.getHeight() - scrollInsets.top - scrollInsets.bottom);
      }

      d.setSize(d.getWidth() - 20, d.getHeight() - 20);

      desktop.setAllSize(x,y);

      scrollPane.invalidate();
      scrollPane.validate();
    }
  }

  private Insets getScrollPaneInsets() {
    JScrollPane scrollPane = getScrollPane();

    if(scrollPane == null) {
      return new Insets(0,0,0,0);
    }
    else {
      return getScrollPane().getBorder().getBorderInsets(scrollPane);
    }
  }

  private JScrollPane getScrollPane() {
    if(desktop.getParent() instanceof JViewport) {
      JViewport viewPort = (JViewport)desktop.getParent();

      if(viewPort.getParent() instanceof JScrollPane) {
        return (JScrollPane)viewPort.getParent();
      }
    }

    return null;
  }

  protected void resizeDesktop() {
    int x = 0;
    int y = 0;
    JScrollPane scrollPane = getScrollPane();
    Insets scrollInsets = getScrollPaneInsets();

    if(scrollPane != null) {
      JInternalFrame allFrames[] = desktop.getAllFrames();

      for(int i = 0; i < allFrames.length; i++) {
        if(allFrames[i].getX()+allFrames[i].getWidth()>x) {
          x = allFrames[i].getX() + allFrames[i].getWidth();
        }

        if(allFrames[i].getY()+allFrames[i].getHeight()>y) {
          y = allFrames[i].getY() + allFrames[i].getHeight();
        }
      }

      Dimension d = scrollPane.getVisibleRect().getSize();

      if(scrollPane.getBorder() != null) {
        d.setSize(d.getWidth() - scrollInsets.left - scrollInsets.right,
        d.getHeight() - scrollInsets.top - scrollInsets.bottom);
      }

      if (x <= d.getWidth()) x = ((int)d.getWidth()) - 20;

      if (y <= d.getHeight()) y = ((int)d.getHeight()) - 20;

      desktop.setAllSize(x,y);

      scrollPane.invalidate();
      scrollPane.validate();
    }
  }

}
