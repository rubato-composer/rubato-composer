/*
 * Copyright (C) 2007 Gérard Milmeister
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
import java.util.List;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.filechooser.FileFilter;

import org.rubato.base.AbstractRubette;
import org.rubato.base.Repository;
import org.rubato.base.Rubette;
import org.rubato.composer.RunInfo;
import org.rubato.composer.Utilities;
import org.rubato.math.arith.Rational;
import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.Form;
import org.rubato.xml.XMLConstants;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

public final class ScoreToCsoundRubette extends AbstractRubette {

    public ScoreToCsoundRubette() {
        setInCount(1);
        setOutCount(0);
    }
    

    public void run(RunInfo runInfo) {
        if (scoFile == null || getScoFileBaseName().length() == 0) {
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
                PrintStream ps = new PrintStream(new BufferedOutputStream(new FileOutputStream(scoFile)));
                writeCsoundScore(ps, Note.scoreToNotes(score));
                ps.flush();
                ps.close();
            }
            catch (IOException e) {
            	e.printStackTrace();
                addError("File %%1 could not be written to.", scoFile.getName());
            }
        }
    }

    
    public String getGroup() {
        return "Score";
    }
    

    public String getName() {
        return "ScoreToCsound";
    }

    
    public Rubette newInstance() {
        return new ScoreToCsoundRubette();
    }


    public Rubette duplicate() {
        ScoreToCsoundRubette newRubette = new ScoreToCsoundRubette();
        if (scoFile != null) {
            newRubette.setScoFile(getScoFileName());            
        }
        newRubette.timeScale = timeScale;
        newRubette.a4freq    = a4freq;
        newRubette.prolog    = new String(prolog);
        
        return newRubette;
    }

    
    public boolean hasInfo() {
        return true;
    }
    
    
    public String getInfo() {
        if (scoFile == null || getScoFileName().length() == 0) {
            return "File not set";
        }
        else {
            return getScoFileBaseName();
        }
    }    

    
    public boolean hasProperties() {
        return true;
    }
    
    
    public JComponent getProperties() {
        if (properties == null) {
            properties = new JPanel();            
            properties.setLayout(new BorderLayout());
            Box vBox = new Box(BoxLayout.Y_AXIS);
            Box fileBox = new Box(BoxLayout.X_AXIS);
            fileBox.setBorder(Utilities.makeTitledBorder("Score file"));
            
            fileNameField = new JTextField(getScoFileName());
            fileNameField.setEditable(false);
            fileNameField.setOpaque(true);
            fileNameField.setBackground(Color.WHITE);
            fileNameField.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
            fileBox.add(fileNameField);
            fileBox.add(Box.createHorizontalStrut(5));
            
            JButton browseButton = new JButton("Browse...");
            browseButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    browseFile(properties);
                } 
            });
            fileBox.add(browseButton);

            vBox.add(fileBox);

            timeScaleField = new JTextField();
            timeScaleField.setText(Double.toString(timeScale));
            Box timeScaleBox = Box.createHorizontalBox();
            timeScaleBox.add(timeScaleField);
            timeScaleBox.setBorder(Utilities.makeTitledBorder("Timescale (seconds per quarter)"));
            vBox.add(timeScaleBox);
            
            freqField = new JTextField();
            freqField.setText(Double.toString(a4freq));
            Box freqBox = Box.createHorizontalBox();
            freqBox.add(freqField);
            freqBox.setBorder(Utilities.makeTitledBorder("Frequency of A4 (MIDI key 69)"));
            vBox.add(freqBox);            
            
            prologTextArea = new JTextArea(5, 0);
            prologTextArea.setText(prolog);
            JScrollPane prologScrollPane = new JScrollPane(prologTextArea);
            Box prologScrollBox = Box.createHorizontalBox();
            prologScrollBox.add(prologScrollPane);
            prologScrollBox.setBorder(Utilities.makeTitledBorder("Prolog"));
            vBox.add(prologScrollBox);
            
            properties.add(vBox);
            
            selectedFile = new File(getScoFileName());
        }
        return properties;
    }


    public boolean applyProperties() {
        scoFile = selectedFile;
        prolog = prologTextArea.getText().trim();
        
        try {
            timeScale = Math.max(Double.parseDouble(timeScaleField.getText()), 0.0);
        }
        catch (NumberFormatException e) {
            timeScale = 2.0;
        }
        timeScaleField.setText(Double.toString(timeScale));
        
        try {
            a4freq = Math.max(Double.parseDouble(freqField.getText()), 1.0);
        }
        catch (NumberFormatException e) {
            a4freq = 440.0;
        }
        freqField.setText(Double.toString(a4freq));
        
        return true;
    }


    public void revertProperties() {
        String fileName = " ";
        try {
            fileName = scoFile.getCanonicalPath();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        fileNameField.setText(fileName);
        timeScaleField.setText(Double.toString(timeScale));
        freqField.setText(Double.toString(timeScale));
        prologTextArea.setText(prolog);
    }

    
    public String getShortDescription() {
        return "Converts a denotator with form \"Score\" to a Csound score file";
    }

    
    public ImageIcon getIcon() {
        return icon;
    }
    

    public String getLongDescription() {
        return "The ScoreToCsound Rubette converts"+
               " a denotator of form \"Score\" to a"+
               " Csound score file.";
    }


    public String getInTip(int i) {
        return "Denotator of form \"Score\"";
    }

    
    static final String FILE      = "File";
    static final String PROLOG    = "Prolog";
    static final String TIMESCALE = "TimeScale";
    static final String FREQ      = "Freq";
    
    public void toXML(XMLWriter writer) {
        writer.empty(FILE, XMLConstants.NAME_ATTR, writer.toRelativePath(getScoFileName()));
        writer.empty(TIMESCALE, XMLConstants.VALUE_ATTR, timeScale);
        writer.empty(FREQ, XMLConstants.VALUE_ATTR, a4freq);
        writer.openBlock(PROLOG);
        writer.writeTextNode(prolog);
        writer.closeBlock();
    }

    
    public Rubette fromXML(XMLReader reader, Element element) {
        ScoreToCsoundRubette newRubette = new ScoreToCsoundRubette();
        Element child = XMLReader.getChild(element, FILE);
        if (child != null) {
            String fileName = XMLReader.getStringAttribute(child, XMLConstants.NAME_ATTR);
            newRubette.setScoFile(reader.toAbsolutePath(fileName));            
        }
        child = XMLReader.getChild(element, TIMESCALE);
        if (child != null) {
            newRubette.timeScale = Math.max(XMLReader.getRealAttribute(child, XMLConstants.VALUE_ATTR, 0.5), 0.0);
        }
        child = XMLReader.getChild(element, FREQ);
        if (child != null) {
            newRubette.a4freq = Math.max(XMLReader.getRealAttribute(child, XMLConstants.VALUE_ATTR, 440.0), 1.0);
        }
        child = XMLReader.getChild(element, PROLOG);
        if (child != null) {
            newRubette.prolog = XMLReader.getText(child).trim();
        }
        return newRubette;
    }
    

    private String getScoFileName() {
        String fileName = "";
        if (scoFile != null) {
            try {
                fileName = scoFile.getCanonicalPath();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fileName;
    }
    
    
    private String getScoFileBaseName() {
        String fileName = "";
        if (scoFile != null) {
            fileName = scoFile.getName();
        }
        return fileName;
    }
    
    
    private void setScoFile(String fileName) {
        scoFile = new File(fileName);
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
                return f.isDirectory() || f.getName().endsWith(".sco");
            }
            public String getDescription() {
                return "Csound Score Files";
            }
        });        
    }

    
    private void writeCsoundScore(PrintStream writer, List<Note> notes) {
        writer.println("; Generated by Rubato Composer");
        writer.println("; Prolog");
        writer.println(prolog);
        writer.println("; Score");
        writer.format(";inst   start        dur          amp          freq\n");
        writer.format(";p1     p2           p3           p4           p5\n");
        for (Note note : notes) {
            writer.format("i%-3d %12.7f %12.7f %14.9f %12.7f\n",
                          note.voice,
                          makeStart(note.onset),
                          makeDuration(note.duration),
                          makeAmplitude(note.loudness),
                          makeFrequency(note.pitch));
        }
    }
    
    
    private double makeStart(double onset) {
        return onset*timeScale;   
    }
    
    
    private double makeDuration(double dur) {
        return dur*timeScale;
    }
    
    
    private double makeAmplitude(int loudness) {
        return Math.max((double)loudness/(double)127, 0);
    }
    
    
    private double makeFrequency(Rational pitch) {        
        return a4freq*Math.pow(ff,pitch.doubleValue()-69);
    }
    
    
    private JTextField   fileNameField;
    private JTextArea    prologTextArea;
    private JTextField   timeScaleField;
    private JTextField   freqField;
    
    private File         scoFile          = null;
    private File         selectedFile     = null;
    private File         currentDirectory = new File(".");
    private JFileChooser fileChooser      = null;
    private String       prolog           = ""; 
    private double       a4freq           = 440.0;
    private double       timeScale        = 0.5;
    private final double ff               = Math.pow(2.0, 1.0/12.0);

    private static final Form scoreForm;
        
    protected JPanel properties = null;
    private static final ImageIcon icon = null;
    
    static {
        Repository rep = Repository.systemRepository();
        scoreForm = rep.getForm("Score");
//        icon = Icons.loadIcon(Icons.class, "csound.png");
    }
}
