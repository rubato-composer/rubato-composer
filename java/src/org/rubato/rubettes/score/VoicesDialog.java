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

package org.rubato.rubettes.score;

import static org.rubato.composer.Utilities.installEnterKey;
import static org.rubato.composer.Utilities.installEscapeKey;

import java.awt.BorderLayout;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.rubato.audio.midi.MidiPlayer;
import org.rubato.util.TextUtils;

final class VoicesDialog
        extends JDialog
        implements ActionListener, ItemListener, ListSelectionListener {
    
    public static int[] showDialog(JComponent comp, int[] voices) {
        VoicesDialog dialog = new VoicesDialog(comp, voices);
        dialog.setVisible(true);
        return dialog.result;
    }

    
    public VoicesDialog(JComponent comp, int[] vcs) {
        super(JOptionPane.getFrameForComponent(comp), "Assign voices", true);
        setLocationRelativeTo(comp);
        setLayout(new BorderLayout(0, 5));
        
        voices = new int[vcs.length];
        
        voiceSelect = new JComboBox();
        voiceSelect.setEditable(false);
        voiceSelect.setToolTipText("Select the voice to change");
        voiceSelect.addItemListener(this);
        add(voiceSelect, BorderLayout.NORTH);

        model = new DefaultListModel();
        instrumentList = new JList();
        instrumentList.setModel(model);
        instrumentList.addListSelectionListener(this);
        instrumentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fillInstrumentList();
        add(new JScrollPane(instrumentList), BorderLayout.CENTER);
        
        okButton = new JButton("Ok");
        okButton.addActionListener(this);
        add(okButton, BorderLayout.SOUTH);
        
        for (int i = 0; i < voices.length; i++) {
            voices[i] = vcs[i];
            voiceSelect.addItem(TextUtils.replaceStrings("Voice %1", i+1));
        }

        installEscapeKey(this);
        Action enterAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                okButton.doClick();
            }
        };         
        installEnterKey(this, enterAction);
        
        pack();
    }

    
    public int[] getVoices() {
        return voices;
    }
    
    
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == okButton) {
            result = voices;
            setVisible(false);
        }
    }
    
    
    public void itemStateChanged(ItemEvent e) {
        int i = voiceSelect.getSelectedIndex();
        int v = voices[i];
        v = Math.min(Math.max(0, v), Math.min(instruments.length, 128)-1);
        instrumentList.setSelectedIndex(v);
        instrumentList.ensureIndexIsVisible(v);
    }
    
    
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            int i = voiceSelect.getSelectedIndex();
            int v = instrumentList.getSelectedIndex();
            voices[i] = v;
        }
    }
    
    
    private void fillInstrumentList() {
        if (instruments == null) {
            instruments = MidiPlayer.getInstruments();
        }
        model.removeAllElements();
        for (int i = 0; i < Math.min(instruments.length, 128); i++) {
            model.addElement(instruments[i]);
        }
    }


    private JComboBox voiceSelect;
    protected JButton okButton;
    private JList     instrumentList;

    private DefaultListModel model;
    
    private static String[] instruments = null;
    
    private int[] voices;
    private int[] result = null;
}
