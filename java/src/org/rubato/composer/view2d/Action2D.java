/*
 * Copyright (C) 2006 Gérard Milmeister
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

package org.rubato.composer.view2d;

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

public interface Action2D {

    public Cursor getCursor();
    
    public void mouseClicked(MouseEvent e);
    
    public void mousePressed(MouseEvent e);
    
    public void mouseDragged(MouseEvent e);
    
    public void mouseReleased(MouseEvent e);
    
    public void draw(Graphics2D g);
}
