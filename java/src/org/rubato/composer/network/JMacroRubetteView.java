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

package org.rubato.composer.network;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.rubato.base.Rubette;
import org.rubato.composer.JComposer;
import org.rubato.composer.components.JMenuTitleItem;
import org.rubato.composer.rubette.JRubette;
import org.rubato.rubettes.builtin.MacroInputRubette;
import org.rubato.rubettes.builtin.MacroOutputRubette;
import org.rubato.rubettes.builtin.MacroRubette;
import org.rubato.xml.XMLWriter;

public class JMacroRubetteView extends JNetwork {

    public JMacroRubetteView(JComposer jcomposer, JNetwork jnetwork) {
        super(jcomposer);
        this.jnetwork = jnetwork;
    }
    
    
    public String getName() {
        return getJNetworkName()+"/"+getModel().getName(); //$NON-NLS-1$
    }
    
    
    private String getJNetworkName() {
        return (jnetwork == null)?"proto":jnetwork.getName(); //$NON-NLS-1$
    }
    
    
    public void dispose() {
        for (JRubette jrubette : getJRubettes()) {
            // close any open views depending on this one
            jrubette.closeDialogs();
            if (jrubette.getRubette() instanceof MacroRubette) {
                NetworkModel networkModel = ((MacroRubette)jrubette.getRubette()).getNetworkModel();
                getJComposer().removeJNetworkForModel(networkModel);
            }
        }
    }
    
    
    public boolean canAdd(JRubette jrubette) {
        Rubette rubette = jrubette.getRubette();
        if (rubette instanceof MacroInputRubette) {
            getJComposer().showErrorDialog(NOTADDINPUT_ERROR);
            return false;
        }
        if (rubette instanceof MacroOutputRubette) {
            getJComposer().showErrorDialog(NOTADDOUTPUT_ERROR);
            return false;
        }
        return true;
    }
    

    protected JPopupMenu getNetworkPopup(final int x, final int y) {
        final JPopupMenu popup = new JPopupMenu();
        JMenuItem item;
        item = new JMenuTitleItem(getModel().getName());
        popup.add(item);
        popup.addSeparator();
        item = new JMenuItem(CLOSENETWORK);
        item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    removeNetwork();
                }
            });
        popup.add(item);
        item = new JMenuItem(CREATENOTE);
        item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    createNote(x, y);
                }
            });
        popup.add(item);
        return popup;
    }    

    
    public void removeRubette(JRubette jrubette) {
        if (jrubette.getModel().getRubette() instanceof MacroInputRubette) {
            getJComposer().showErrorDialog(MACROINPUT_ERROR);
        }
        else if (jrubette.getModel().getRubette() instanceof MacroOutputRubette) {
            getJComposer().showErrorDialog(MACROOUTPUT_ERROR);
        }
        else {
            super.removeRubette(jrubette);
        }
    }

        
    public void toXML(XMLWriter writer) {}
    
    
    private JNetwork jnetwork;
    
    private static final String MACROINPUT_ERROR  = Messages.getString("JMacroRubetteView.macroinputerror"); //$NON-NLS-1$
    private static final String MACROOUTPUT_ERROR = Messages.getString("JMacroRubetteView.macrooutputerror"); //$NON-NLS-1$
    private static final String NOTADDINPUT_ERROR   = Messages.getString("JMacroRubetteView.notaddinputerror"); //$NON-NLS-1$
    private static final String NOTADDOUTPUT_ERROR  = Messages.getString("JMacroRubetteView.notaddoutputerror");     //$NON-NLS-1$
    private static final String CREATENOTE          = Messages.getString("JMacroRubetteView.createnote"); //$NON-NLS-1$
    private static final String CLOSENETWORK        = Messages.getString("JMacroRubetteView.closenetwork"); //$NON-NLS-1$
}
