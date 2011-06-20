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

package org.rubato.composer;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;

import org.rubato.base.Rubette;
import org.rubato.composer.network.JNetwork;


public class RubetteTransferHandler extends TransferHandler {

    protected Transferable createTransferable(JComponent c) {
        if (c instanceof JList) {
            RubetteInfo info = (RubetteInfo)((JList)c).getSelectedValue();
            if (info.isGroup()) {
                return null;
            }
            RubetteTransferable rt = new RubetteTransferable(info.getRubette());
            return rt;
        }

        return null;
    }
    

    public int getSourceActions(JComponent c) {
        return COPY;
    }

    
    public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
        if (comp instanceof JNetwork) {
            return transferFlavors.length > 0 && transferFlavors[0] == RubetteTransferable.rubetteFlavor;
        }
        else {
            return false;
        }
    }
    

    public boolean importData(JComponent comp, Transferable t) {
        if (canImport(comp, t.getTransferDataFlavors()) && comp instanceof JNetwork) {
            JNetwork jnetwork = (JNetwork)comp;
            try {
                Rubette rubette = (Rubette)t.getTransferData(RubetteTransferable.rubetteFlavor);
                jnetwork.getJComposer().addJRubette(jnetwork, rubette);
                return true;
            }
            catch (Exception e) {
                return false;
            }
        }
        return false;
    }
}