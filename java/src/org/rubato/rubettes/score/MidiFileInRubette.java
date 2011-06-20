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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

import javax.sound.midi.InvalidMidiDataException;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import org.rubato.audio.midi.MidiReader;
import org.rubato.base.AbstractRubette;
import org.rubato.base.Rubette;
import org.rubato.composer.RunInfo;
import org.rubato.composer.icons.Icons;
import org.rubato.math.yoneda.Denotator;
import static org.rubato.xml.XMLConstants.*;

import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

public class MidiFileInRubette extends AbstractRubette {

    public MidiFileInRubette() {
        setInCount(0);
        setOutCount(1);
    }

    
    public void run(RunInfo runInfo) {
        if (midiFile == null) {
            if (score == null)
                addError("No file has been set.");
            else
                addError("No file has been set. Using stored denotator.");
        }
        else if (!midiFile.canRead()) {
            if (score == null)
                addError("File %%1 could not be read.", midiFile.getName());
            else
                addError("File %%1 could not be read. Using stored denotator.", midiFile.getName());
        }
        else if (midiFile.lastModified() > lastModified) {
            lastModified = midiFile.lastModified();
            score = midiToScore(midiFile);
        }
        setOutput(0, score);
    }

    
    private Denotator midiToScore(File file) {
        Denotator scr = null;
        try {
            InputStream is = new BufferedInputStream(new FileInputStream(midiFile));
            midiReader = new MidiReader(is);
            midiReader.setTempoFactor(tempoFactor);
            scr = midiReader.getDenotator();
        }
        catch (FileNotFoundException e) {
            addError("File %%1 not found.", midiFile.getName());
        }
        catch (IOException e) {
            addError("File %%1 could not be read.", midiFile.getName());
        }
        catch (InvalidMidiDataException e) {
            addError("File %%1 contains invalid MIDI data.", midiFile.getName());
        }
        return scr;
    }

    
    public String getGroup() {
        return "Score";
    }

    
    public String getName() {
        return "MidiFileIn";
    }

    
    public Rubette duplicate() {
        MidiFileInRubette newRubette = new MidiFileInRubette();
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
            
            JPanel panel = new JPanel();
            GridBagLayout layout = new GridBagLayout();
            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(5, 5, 5, 5); 
            panel.setLayout(layout);

            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridwidth = 1;
            c.weightx = 0.0;
            JLabel fileLabel = new JLabel("MIDI input file: ");
            layout.setConstraints(fileLabel, c);
            panel.add(fileLabel);

            c.weightx = 1.0;
            c.gridwidth = GridBagConstraints.REMAINDER;
            layout.setConstraints(fileBox, c);
            panel.add(fileBox);

            c.gridwidth = 1;
            c.weightx = 0.0;
            JLabel factorLabel = new JLabel("Tempo factor: ");
            layout.setConstraints(factorLabel, c);
            panel.add(factorLabel);
            
            c.gridwidth = GridBagConstraints.REMAINDER;
            c.weightx = 1.0;
            factorField = new JTextField(Double.toString(tempoFactor));
            layout.setConstraints(factorField, c);
            panel.add(factorField);
            
            c.gridwidth = 1;
            c.weightx = 0.0;
            JLabel storeLabel = new JLabel("Store denotator: ");
            layout.setConstraints(storeLabel, c);
            panel.add(storeLabel);
            
            c.gridwidth = GridBagConstraints.REMAINDER;
            c.weightx = 1.0;
            storeField = new JCheckBox();
            storeField.setSelected(storeDenotator);
            layout.setConstraints(storeField, c);
            panel.add(storeField);

            properties.add(panel, BorderLayout.CENTER);
            
            selectedFile = new File(getMidiFileName());
        }
        return properties;
    }


    public boolean applyProperties() {
        boolean changed = false;
        
        try {
            double f = Double.parseDouble(factorField.getText());
            changed = f != getTempoFactor();
            setTempoFactor(f);
        }
        catch (NumberFormatException e) {
            factorField.setText(Double.toString(getTempoFactor()));
        }
        
        if (midiFile == null || !midiFile.equals(selectedFile)) {
            midiFile = selectedFile;
            score = null;
            lastModified = midiFile.lastModified();
            changed = true;
        }

        storeDenotator = storeField.isSelected();
        
        if (changed) {
            score = midiToScore(midiFile);
            setOutput(0, score);
            return score != null;
        }
        else {
            return true;
        }
    }


    public void revertProperties() {
        String fileName = " ";
        try {
            
            fileName = midiFile.getCanonicalPath();            
        }
        catch (IOException e) {}
        fileNameField.setText(fileName);
        factorField.setText(Double.toString(getTempoFactor()));
        storeField.setSelected(storeDenotator);
    }

    
    public String getShortDescription() {
        return "Converts a MIDI file to a denotator with form \"Score\"";
    }

    
    public ImageIcon getIcon() {
        return icon;
    }
    

    public String getLongDescription() {
        return "The MidiFileIn Rubette reads in a MIDI file"+
               " and converts it to a denotator of form \"Score\".";
    }


    public String getOutTip(int i) {
        return "Denotator of form \"Score\"";
    }

    
    private static final String FILE = "File";
    private static final String TEMPO_FACTOR = "TempoFactor";
    private static final String STORE_DENOTATOR = "StoreDenotator";
    
    public void toXML(XMLWriter writer) {
        writer.empty(FILE, NAME_ATTR, writer.toRelativePath(getMidiFileName()));
        writer.empty(TEMPO_FACTOR, VALUE_ATTR, getTempoFactor());
        writer.empty(STORE_DENOTATOR, VALUE_ATTR, storeDenotator?TRUE_VALUE:FALSE_VALUE);
        if (storeDenotator && score != null) score.toXML(writer);
    }
    
    
    public Rubette fromXML(XMLReader reader, Element element) {
        String fileName = "";
        double f = 1.0;
        Denotator score = null;
        boolean store = false;
        Element child = XMLReader.getChild(element, FILE);
        if (child != null) {
            fileName = XMLReader.getStringAttribute(child, NAME_ATTR);
        }
        child = XMLReader.getNextSibling(child, TEMPO_FACTOR);
        if (child != null) {
            f = XMLReader.getRealAttribute(child, VALUE_ATTR, 1.0);
        }
        child = XMLReader.getNextSibling(child, STORE_DENOTATOR);
        if (child != null) {
            if (TRUE_VALUE.equals(child.getAttribute(VALUE_ATTR))) store = true;
        }
        child = XMLReader.getNextSibling(child, DENOTATOR);
        if (child != null) {
            score = reader.parseDenotator(child);
        }
        MidiFileInRubette newRubette = new MidiFileInRubette();
        newRubette.setMidiFile(reader.toAbsolutePath(fileName));
        newRubette.setTempoFactor(f);
        newRubette.setScore(score);
        newRubette.setStoreDenotator(store);
        return newRubette;
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
    
    
    public void setMidiFile(String fileName) {
        midiFile = new File(fileName);
    }
    
    
    public void setTempoFactor(double f) {
        tempoFactor = f;
    }
    
    
    public double getTempoFactor() {
        return tempoFactor;
    }
    
    
    public void setScore(Denotator score) {
        this.score = score;
    }
    
    
    public Denotator getScore() {
        return this.score;
    }

    
    public void setStoreDenotator(boolean storeDenotator) {
        this.storeDenotator = storeDenotator;
    }
    
    
    public boolean isStoreDenotator() {
        return this.storeDenotator;
    }
    
    
    protected void browseFile(JPanel props) {
        if (fileChooser == null) {
            createFileChooser();
        }
        fileChooser.setCurrentDirectory(currentDirectory);
        int res = fileChooser.showOpenDialog(props);
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
    private JTextField   factorField;
    private JCheckBox    storeField;
    private File         midiFile         = null;
    private long         lastModified     = 0;
    private File         selectedFile     = null;
    private File         currentDirectory = new File(".");
    private JFileChooser fileChooser      = null;
    private MidiReader   midiReader       = null;
    private Denotator    score            = null;
    private double       tempoFactor      = 1.0;
    private boolean      storeDenotator   = true;
        
    protected JPanel properties = null;
    private static final ImageIcon icon;
    
    static {
        icon = Icons.loadIcon(Icons.class, "midiin.png");
    }
}
