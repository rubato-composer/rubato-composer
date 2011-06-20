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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.event.EventListenerList;

import org.rubato.math.module.*;

public abstract class JSimpleEntry extends JPanel {

    public static JSimpleEntry make(Module module) {
        if (module instanceof ZFreeModule ||
            module instanceof RFreeModule ||
            module instanceof ZnFreeModule ||
            module instanceof QFreeModule ||
            module instanceof CFreeModule ||
            module instanceof ZStringFreeModule ||
            module instanceof RStringFreeModule ||
            module instanceof QStringFreeModule ||
            module instanceof ZnStringFreeModule ||
            module instanceof PolynomialRing ||
            module instanceof ModularPolynomialRing) {
            return new JSimpleNumberEntry(module);
        }
        else if (module instanceof ProductFreeModule) {
            return new JSimpleProductEntry((ProductFreeModule)module);
        }
        else {
            return null;
        }
    }
    
    public abstract void clear();
    
    public abstract boolean valueIsValid();
    
    public abstract ModuleElement getValue();

    public abstract void setValue(ModuleElement element);
    
    public void addActionListener(ActionListener l) {
        listenerList.add(ActionListener.class, l);
    }
    
    public void removeActionListener(ActionListener l) {
        listenerList.remove(ActionListener.class, l);
    }

    protected void fireActionEvent() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i] == ActionListener.class) {
                if (actionEvent == null) {
                    actionEvent = new ActionEvent(this, 0, ""); //$NON-NLS-1$
                }
                ((ActionListener)listeners[i+1]).actionPerformed(actionEvent);
            }
        }
    }

    private EventListenerList listenerList = new EventListenerList();
    private ActionEvent actionEvent = null;
}
