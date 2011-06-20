/*
 * Copyright (C) 2005 Gérard Milmeister
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of version 2 of the GNU General Public
 * License as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */

package org.rubato.composer.components;

import java.awt.event.MouseEvent;

import javax.swing.*;

public class JMenuTitleItem extends JMenuItem {

    public JMenuTitleItem(String title) {
        super(title);
    }
    
    protected void processMouseEvent(MouseEvent e) { /* do nothing */ }
    
    public void processMouseEvent(MouseEvent e, 
                                  MenuElement[] path,
                                  MenuSelectionManager manager) { /* do nothing */ }
    
    protected void processMouseMotionEvent(MouseEvent e) { /* do nothing */ }

    public void setArmed(boolean b) {
        getModel().setArmed(false);
    }
}
