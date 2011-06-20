/*
 * Copyright (C) 2006 Florian Thalmann
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
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.rubato.base.AbstractRubette;
import org.rubato.base.Rubette;
import org.rubato.composer.RunInfo;
import org.rubato.composer.Utilities;
import org.rubato.composer.components.JStatusline;
import org.rubato.composer.icons.Icons;
import org.rubato.math.yoneda.PowerDenotator;
import org.rubato.rubettes.util.NoteGenerator;
import org.rubato.rubettes.util.MacroNoteGenerator;
import org.rubato.rubettes.util.ScaleMap;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

/**
 * A rubette for generating preset and custom scales. The output is a Score denotator with
 * all scale notes with pitches between 0 and 127. Their onset is 0, their duration 1.
 * 
 * @author Florian Thalmann
 */
public class ScaleRubette extends AbstractRubette implements ChangeListener, ActionListener {
	
	private final int MAX_NUMBER_OF_NOTES = 12;
	private final int MIN_PITCH = 0;
	private final int MAX_PITCH = 127;
	
	private int numberOfNotes;
	private double root;
	private int preset;
	private double[] intervals;
	
	private ScaleMap scaleMap;
	private NoteGenerator noteGenerator;
	
	private JPanel propertiesPanel;
	private JPanel northPanel;
	private JSlider numberOfNotesSlider;
	private JTextField rootTextField;
	private JComboBox presetsComboBox;
	private JPanel presetsPanel;
	private JPanel intervalsPanel;
	private JTextField[] intervalTextFields;
	private JStatusline statusline;
	
	private static final ImageIcon icon = Icons.loadIcon(ScaleRubette.class, "scaleicon.png");
	
	public ScaleRubette() {
		this.setInCount(0);
		this.setOutCount(1);
		this.scaleMap = new ScaleMap();
		this.noteGenerator = new MacroNoteGenerator();
		this.numberOfNotes = 7;
		this.preset = 1;
		this.root = 60;
		this.intervals = this.scaleMap.get(this.getPresetName());
		this.statusline = new JStatusline();
		this.statusline.setText("");
	}
	
	private String getPresetName() {
		return this.scaleMap.getScaleNames(this.numberOfNotes)[this.preset].toString();
	}

	@Override
	public String getName() {
		return "Scale";
	}

	@Override
	public Rubette newInstance() {
		return new ScaleRubette();
	}

    @Override
    public Rubette duplicate() {
        // TODO: This must be correctly implemented
        return newInstance();
    }
    
	@Override
	public void run(RunInfo arg0) {
		this.setOutput(0, this.generateScale());
	}
	
	protected PowerDenotator generateScale() {
		HashSet<Double> pitches = new HashSet<Double>();
		this.addHigherPitchesTo(pitches);
		this.addLowerPitchesTo(pitches);
		return this.noteGenerator.createSimpleMelody(0, pitches.toArray(new Double[]{}));
	}
	
	private void addHigherPitchesTo(HashSet<Double> pitches) {
		double currentPitch = this.root;
		for (int i = 0; currentPitch <= this.MAX_PITCH; i++) {
			if (currentPitch >= this.MIN_PITCH) {
				pitches.add(new Double(currentPitch));
			}
			currentPitch += this.intervals[i%this.numberOfNotes];
		}
	}
	
	private void addLowerPitchesTo(HashSet<Double> pitches) {
		int lastIndex = this.numberOfNotes-1;
		double currentPitch = this.root-this.intervals[lastIndex];
		for (int i = lastIndex-1; currentPitch >= this.MIN_PITCH; i--) {
			if (currentPitch <= this.MAX_PITCH) {
				pitches.add(new Double(currentPitch));
			}
			int currentIndex = i%this.numberOfNotes;
			if (currentIndex < 0) {
				currentIndex = currentIndex + this.numberOfNotes;
			}
			currentPitch -= this.intervals[currentIndex];
		}
	}
	
    public boolean hasProperties() {
        return true;
    }

    public JComponent getProperties() {
        if (this.propertiesPanel == null) {
            this.propertiesPanel = new JPanel();            
            this.propertiesPanel.setLayout(new BorderLayout());
            
            this.northPanel = new JPanel();
            this.northPanel.setLayout(new GridLayout(2,1));
            
            JPanel sliderPanel = this.createTitledBorderPanel("Number of notes");
            this.numberOfNotesSlider = new JSlider(1, this.MAX_NUMBER_OF_NOTES, this.numberOfNotes);
            this.numberOfNotesSlider.setMinorTickSpacing(1);
            this.numberOfNotesSlider.setMajorTickSpacing(6);
            this.numberOfNotesSlider.setSnapToTicks(true);
            this.numberOfNotesSlider.setPaintTicks(true);
            this.numberOfNotesSlider.setPaintLabels(true);
            this.numberOfNotesSlider.addChangeListener(this);
            sliderPanel.add(numberOfNotesSlider);
            this.northPanel.add(sliderPanel);
            
            JPanel scalePanel = new JPanel();
            scalePanel.setLayout(new GridLayout(2, 1));
            JPanel rootPanel = this.createTitledBorderPanel("Root note (MIDI pitch, c'=60)");
            this.rootTextField = new JTextField(new Double(this.root).toString(), 5);
            rootPanel.add(this.rootTextField);
            scalePanel.add(rootPanel);
            this.presetsPanel = this.createTitledBorderPanel("Preset or custom scale");
            scalePanel.add(this.presetsPanel);
            this.northPanel.add(scalePanel);
            
            this.propertiesPanel.add(this.northPanel, BorderLayout.NORTH);
            
            this.intervalsPanel = this.createTitledBorderPanel("Intervals in semitones");
            
            this.intervalTextFields = new JTextField[this.MAX_NUMBER_OF_NOTES];
            for (int i = 0; i < this.intervalTextFields.length; i++) {
            	this.intervalTextFields[i] = new JTextField(5);
            	this.intervalTextFields[i].addActionListener(this);
            }
            this.propertiesPanel.add(this.intervalsPanel, BorderLayout.CENTER);
            
            this.updatePresetsComboBoxAndIntervalsPanel(this.numberOfNotes);
            this.presetsComboBox.setSelectedIndex(this.preset);
			
			this.propertiesPanel.add(this.statusline, BorderLayout.SOUTH);
			this.revertProperties();
        }
        return this.propertiesPanel;
    }
    
    private JPanel createTitledBorderPanel(String text) {
    	JPanel panel = new JPanel();
    	panel.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(), text));
    	return panel;
    }
    
    private void updatePresetsComboBoxAndIntervalsPanel(int currentNumberOfNotes) {
    	this.updatePresetsComboBox(currentNumberOfNotes);
    	
    	this.intervalsPanel.removeAll();
    	int numberOfRows = (int) Math.ceil(((double) currentNumberOfNotes) / 4);
    	this.intervalsPanel.setLayout(new GridLayout(numberOfRows, 4));
    	for (int i = 0; i < currentNumberOfNotes; i++) {
    		this.intervalsPanel.add(this.intervalTextFields[i]);
    	}
    	this.intervalsPanel.repaint();
    	this.updateIntervals();
    	
    	this.packPropertiesWindow();
    }
    
    private void updatePresetsComboBox(int currentNumberOfNotes) {
    	int previousIndex = 0;
    	if (this.presetsComboBox != null) {
    		previousIndex = this.presetsComboBox.getSelectedIndex();
    	}
    	this.presetsPanel.removeAll();
    	this.presetsComboBox = new JComboBox(this.scaleMap.getScaleNames(currentNumberOfNotes));
    	this.presetsComboBox.addActionListener(this);
    	//preferably select the item with the same index as the previously selected 
    	int itemCount = this.presetsComboBox.getItemCount(); 
    	if (itemCount > previousIndex) {
    		this.presetsComboBox.setSelectedIndex(previousIndex);
    	} else {
    		this.presetsComboBox.setSelectedIndex(itemCount-1);
    	}
    	this.presetsPanel.add(this.presetsComboBox);
    }
    
    private void updateIntervals() {
    	double[] newIntervals = this.scaleMap.get(this.presetsComboBox.getSelectedItem());
    	//"custom" also returns null, therefore no update
    	if (newIntervals != null) {
    		for (int i = 0; i < newIntervals.length; i++) {
    			this.intervalTextFields[i].setText(new Double(newIntervals[i]).toString());
    		}
    	}
    }
    
    public boolean applyProperties() {
    	int newNumberOfNotes = this.numberOfNotesSlider.getValue();
    	double newRoot = 0;
    	try {
    		newRoot = Double.parseDouble(this.rootTextField.getText());
    	} catch (NumberFormatException e) {
    		this.setStatuslineText("Root note is not a valid number");
        	return false;
    	}
    	
    	double[] newIntervals = new double[newNumberOfNotes];
        for (int i = 0; i < newNumberOfNotes; i++) {
        	try {
        		newIntervals[i] = Double.parseDouble(this.intervalTextFields[i].getText());
        		if (newIntervals[i] < 1) {
        			this.setStatuslineText("Interval #" + (i+1) + " is not greater than one");
            		return false;
        		}
        	} catch (NumberFormatException e) {
        		this.setStatuslineText("Interval #" + (i+1) + " is not a valid number");
        		return false;
        	}
        }
        this.numberOfNotes = newNumberOfNotes;
        this.root = newRoot;
        this.preset = this.presetsComboBox.getSelectedIndex();
        this.intervals = newIntervals;
        this.statusline.setText("");
        this.revertProperties(); //fields are getting updating
        return true;
    }
    
    public void revertProperties() {
    	this.numberOfNotesSlider.setValue(this.numberOfNotes);
    	this.rootTextField.setText(new Double(this.root).toString());
    	for (int i = 0; i < this.numberOfNotes; i++) {
    		this.intervalTextFields[i].setText(new Double(this.intervals[i]).toString());
    	}
    	//odd trick for obtaining loaded presets...
    	this.updatePresetsComboBoxAndIntervalsPanel(this.numberOfNotes);
    	this.presetsComboBox.setSelectedIndex(this.preset);
    	this.updatePresetsComboBoxAndIntervalsPanel(this.numberOfNotes);
    }
    
    protected void setTempRootNote(double root) {
    	this.rootTextField.setText(new Double(root).toString());
    }
    
    protected void setTempPreset(int preset) {
    	this.presetsComboBox.setSelectedIndex(preset);
    }
    
    /*
     * sets a text to the statusline located in the south of the properties window
     */
    protected void setStatuslineText(String text) {
    	this.statusline.setText(text);
    }
    
    /**
     * Returns the ScaleMap used by this rubette.
     */
    public ScaleMap getScaleMap() {
    	return this.scaleMap;
    }
    
    /**
     * Returns the current text in the statusline of this rubette's properties window.
     */
    public String getStatuslineText() {
    	return this.statusline.getText();
    }
    
    /**
	 * Processes all events coming from the numberOfNotes slider.
	 */
    public void stateChanged(ChangeEvent event) {
    	if (event.getSource() == this.numberOfNotesSlider) {
    		this.updatePresetsComboBoxAndIntervalsPanel(this.numberOfNotesSlider.getValue());
    	}
    }
    
    /**
	 * Processes all events coming from the presets combo box.
	 */
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		if (src == this.presetsComboBox) {
			this.updateIntervals();
		} else if (src.getClass().equals(JTextField.class)) {
			//some interval changed -> select "custom"
			this.presetsComboBox.setSelectedIndex(0);
		}
	}
    
    /*
	 * packs this properties window
	 */
	private void packPropertiesWindow() {
		Window propertiesWindow = Utilities.getWindow(this.propertiesPanel);
		if (propertiesWindow != null) {
			propertiesWindow.pack();
		}
	}
	
	/**
     * Returns the fact that MorphingRubette belongs to the core rubettes
     */
    public String getGroup() {
        return "Score";
    }
	
	public boolean hasInfo() {
        return true;
    }
    
    public String getInfo() {
    	String status = this.statusline.getText();
        if (!status.equals("") && !status.equals("Info")) {
            return status;
        } else if (this.presetsComboBox != null) {
        	return this.presetsComboBox.getSelectedItem().toString();
        } else return this.getPresetName();
    }
    
    public String getShortDescription() {
        return "Creates a scale for the whole midi range";
    }

    public String getLongDescription() {
        return "Creates a repetitive musical scale for the whole midi range.";        
    }
    
    public String getInTip(int i) {
        return null;
    }
    
    public String getOutTip(int i) {
        return "Output score denotator";
    }
    
    public ImageIcon getIcon() {
        return icon;
    }
    
    private static final String SCALE = "Scale";
    private static final String NUMBER_OF_NOTES = "numberOfNotes";
    private static final String ROOT = "rootNote";
    private static final String PRESET = "preset";
    private static final String INTERVAL = "interval";
    private static final String SEMITONES = "semitones";
    
    @Override
    public void toXML(XMLWriter writer) {
    	writer.openBlock(SCALE,
			NUMBER_OF_NOTES, this.numberOfNotes,
			ROOT, this.root,
			PRESET, this.preset);
    		if (this.preset == 0) {
    			for (int i = 0; i < this.intervals.length; i++) {
    				writer.empty(INTERVAL, SEMITONES, this.intervals[i]);
    			}
    		}
    	writer.closeBlock();
	}
	
	@Override
	public Rubette fromXML(XMLReader reader, Element element) {
		ScaleRubette loadedRubette = new ScaleRubette();
		loadedRubette.getProperties();
		Element child = XMLReader.getChild(element, SCALE);
		int numberOfNotes = XMLReader.getIntAttribute(child, NUMBER_OF_NOTES, 7);
		loadedRubette.numberOfNotes = numberOfNotes;
		loadedRubette.root = XMLReader.getRealAttribute(child, ROOT, 60);
		int loadedPreset = XMLReader.getIntAttribute(child, PRESET, 0);
		loadedRubette.preset = loadedPreset;
		if (loadedPreset == 0) {
			double[] intervals = new double[numberOfNotes];
			int i = 0;
			Element nextSibling = XMLReader.getChild(child, INTERVAL);
			while (nextSibling != null) {
				intervals[i] = XMLReader.getRealAttribute(nextSibling, SEMITONES, 1);
				nextSibling = XMLReader.getNextSibling(nextSibling, INTERVAL);
				i++;
			}
			loadedRubette.intervals = intervals;
		}
		loadedRubette.revertProperties();
		return loadedRubette;
	}

}
