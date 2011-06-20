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
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import org.rubato.base.Rubette;


public class RubetteTransferable implements Transferable {

    public static final String rubetteType = DataFlavor.javaJVMLocalObjectMimeType+";class=org.rubato.base.AbstractRubette"; //$NON-NLS-1$
    public static final DataFlavor rubetteFlavor;

    
    public RubetteTransferable(Rubette rubette) {
        super();
        this.rubette = rubette;
    }
    
    
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor == rubetteFlavor; 
    }
    

    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] { rubetteFlavor };
    }
    

    public Object getTransferData(DataFlavor flavor)
            throws UnsupportedFlavorException, IOException {
        if (flavor != rubetteFlavor) {
            throw new UnsupportedFlavorException(flavor);
        }
        return rubette;
    }
    
    
    private Rubette rubette;
    
    static {
        DataFlavor flavor = null;
        try {
            flavor = new DataFlavor(rubetteType);
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        rubetteFlavor = flavor;
    }
}
