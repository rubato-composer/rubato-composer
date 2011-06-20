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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;

import org.rubato.audio.midi.MidiPlayer;
import org.rubato.base.AbstractRubette;
import org.rubato.base.Repository;
import org.rubato.base.Rubette;
import org.rubato.composer.RunInfo;
import org.rubato.composer.icons.Icons;
import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.Form;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

public class MidiFileOutRubette extends AbstractRubette {

    public MidiFileOutRubette() {
        setInCount(1);
        setOutCount(0);
    }
    
    
    public void run(RunInfo runInfo) {
        if (midiFile == null) {
            addError("No file has been set.");
        }
        else {
            Denotator score = getInput(0);
            if (score == null) {
                addError("Input denotator is null.");
                return;
            }
            if (!score.hasForm(scoreForm)) {
                addError("Input denotator is not of form \"Score\".");
                return;                
            }
            try {
                OutputStream outs = new BufferedOutputStream(new FileOutputStream(midiFile));
                midiPlayer = new MidiPlayer();
                midiPlayer.setScore(score);
                midiPlayer.writeSequence(outs);
                outs.flush();
                outs.close();
            }
            catch (IOException e) {
                addError("File %%1 could not be written to.", midiFile.getName());
            }
        }
    }


    public String getGroup() {
        return "Score";
    }

    
    public String getName() {
        return "MidiFileOut";
    }

    
    public Rubette duplicate() {
        MidiFileOutRubette newRubette = new MidiFileOutRubette();
        newRubette.midiFile = midiFile;
        return newRubette;
    }
    

    public boolean hasInfo() {
        return true;
    }
    
    
    public String getInfo() {
        if (midiFile == null) {
            return "File not set";
        }
        else {
            return getMidiFileBaseName();
        }
    }    

    
    public boolean hasProperties() {
        return true;
    }


    public JComponent getProperties() {
        if (properties == null) {
            properties = new JPanel();            
            properties.setLayout(new BorderLayout());            
            Box fileBox = new Box(BoxLayout.X_AXIS);
            fileBox.setBorder(new TitledBorder(BorderFactory.createEmptyBorder(), "MIDI output file"));
            
            fileNameField = new JTextField(getMidiFileName());
            fileNameField.setEditable(false);
            fileNameField.setOpaque(true);
            fileNameField.setBackground(Color.WHITE);
            fileBox.add(fileNameField);

            fileBox.add(Box.createHorizontalStrut(5));
            
            JButton browseButton = new JButton("Browse...");
            browseButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    browseFile(properties);
                } 
            });
            fileBox.add(browseButton);
            
            properties.add(fileBox);
            selectedFile = new File(getMidiFileName());
        }
        return properties;
    }


    public boolean applyProperties() {
        midiFile = selectedFile;
        return true;
    }


    public void revertProperties() {
        String fileName = " ";
        try {
            fileName = midiFile.getCanonicalPath();
        }
        catch (IOException e) {
        }
        fileNameField.setText(fileName);
    }

    
    public String getShortDescription() {
        return "Converts a denotator with form \"Score\" to a MIDI file";
    }

    
    public ImageIcon getIcon() {
        return icon;
    }
    

    public String getLongDescription() {
        return "The MidiFileOut Rubette converts"+
               " a denotator of form \"Score\" to MIDI"+
               " and writes it to a file.";
    }


    public String getInTip(int i) {
        return "Denotator of form \"Score\"";
    }

    
    public void toXML(XMLWriter writer) {
        writer.empty("File", "name", writer.toRelativePath(getMidiFileName()));
    }
    
    
    public Rubette fromXML(XMLReader reader, Element element) {
        Element child = XMLReader.getChild(element, "File");
        if (child != null) {
            String fileName = XMLReader.getStringAttribute(child, "name");
            MidiFileOutRubette newRubette = new MidiFileOutRubette();
            newRubette.setMidiFile(reader.toAbsolutePath(fileName));            
            return newRubette;
        }
        else {
            return null;
        }
    }

    
    private String getMidiFileName() {
        String fileName = "";
        if (midiFile != null) {
            try {
                fileName = midiFile.getCanonicalPath();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fileName;
    }
    
    
    private String getMidiFileBaseName() {
        String fileName = "";
        if (midiFile != null) {
            fileName = midiFile.getName();
        }
        return fileName;
    }
    
    
    private void setMidiFile(String fileName) {
        midiFile = new File(fileName);
    }
    
    
    protected void browseFile(JPanel props) {
        if (fileChooser == null) {
            createFileChooser();
        }
        fileChooser.setCurrentDirectory(currentDirectory);
        int res = fileChooser.showSaveDialog(props);
        if (res == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            currentDirectory = fileChooser.getCurrentDirectory();
            try {
                fileNameField.setText(selectedFile.getCanonicalPath());
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    
    private void createFileChooser() {
        fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileFilter() {
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().endsWith(".mid");
            }
            public String getDescription() {
                return "MIDI Files";
            }
        });        
    }

    
    private JTextField   fileNameField;
    private File         midiFile         = null;
    private File         selectedFile     = null;
    private File         currentDirectory = new File(".");
    private JFileChooser fileChooser      = null;
    private MidiPlayer   midiPlayer       = null;

    private static final Form scoreForm;
        
    protected JPanel properties = null;
    private static final ImageIcon icon;
    
    static {
        Repository rep = Repository.systemRepository();
        scoreForm = rep.getForm("Score");
        icon = Icons.loadIcon(Icons.class, "midiout.png");
    }
}
