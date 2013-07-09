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

package org.rubato.rubettes.wallpaper;

import static org.rubato.xml.XMLConstants.FORM;
import static org.rubato.xml.XMLConstants.MODULEMORPHISM;
import static org.rubato.xml.XMLConstants.VALUE_ATTR;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import org.rubato.base.*;
import org.rubato.composer.RunInfo;
import org.rubato.composer.Utilities;
import org.rubato.composer.components.JSelectForm;
import org.rubato.composer.components.JStatusline;
import org.rubato.math.module.morphism.*;
import org.rubato.math.yoneda.*;
import org.rubato.rubettes.bigbang.model.TransformationPaths;
import org.rubato.rubettes.util.ArbitraryDenotatorMapper;
import org.rubato.rubettes.util.DenotatorPath;
import org.rubato.rubettes.util.SimpleFormFinder;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

/**
 * A rubette that creates a wallpaper using an input power denotator and a number of morphisms.
 * 
 * @author Florian Thalmann
 */
public class WallpaperRubette extends AbstractRubette implements ActionListener {
	
	private PowerForm inputForm = null;
	private SimpleFormFinder simpleFormFinder;
    
    private JPanel properties = null;
    private JSelectForm selectForm = null;
    private JButton	viewMorphismsButton = null;
	private JButton	addMorphismButton = null;
	private JButton removeMorphismButton = null;
	private JButton morphismUpButton = null;
	private JButton morphismDownButton = null;
	private JWallpaperDimensionsTable morphismsTable = null;
    private JStatusline statusline = null;
    
	/**
	 * Defines one input and one output. Initializes instance variables.
	 */
    public void init() {
        setInCount(1);
        setOutCount(1);
        this.morphismsTable = new JWallpaperDimensionsTable(this);
        this.morphismsTable.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        this.simpleFormFinder = new SimpleFormFinder(null);
    }

    /**
     * Called when the Rubette is run. Calculates and sets the output denotator, if there is an
     * input denotator.
     */
    public void run(RunInfo runInfo) {
        PowerDenotator input = (PowerDenotator) this.getInput(0);
        if (this.inputForm == null) {
            this.addError("Input form not set.");
        } else if (input == null) {
            this.addError("Input denotator not set.");
        } else {
        	try {
        		List<ModuleMorphism> morphisms = new ArrayList<ModuleMorphism>(this.morphismsTable.getMorphisms());
        		PowerDenotator output = this.getUnitedMappedDenotators(input, morphisms);
        		this.setOutput(0, output);
        	} catch (RubatoException e) { 
        		e.printStackTrace();
        		this.addError("Error while running.");
        	}
        }
    }
	
    /*
     * calls getUnitedMappedDenotators with all current morphisms in the table. Used for testing
     * only. 
     */
	protected PowerDenotator getUnitedMappedDenotators(PowerDenotator input) throws RubatoException {
		return this.getUnitedMappedDenotators(input, this.morphismsTable.getMorphisms());
	}
	
	/*
	 * returns the wallpaper denotator, i.e. a PowerDenotator of the same form as the input
	 * denotator with set union of all mappings of the input denotator's elements. 
	 * Recursive method!
	 */
	private PowerDenotator getUnitedMappedDenotators(PowerDenotator input, List<ModuleMorphism> morphisms) throws RubatoException {
		//remove first morphism from the list and get its range
		ModuleMorphism currentMorphism = morphisms.remove(0);
		int currentRangeFrom = this.morphismsTable.getRangeFrom(currentMorphism);
		int currentRangeTo = this.morphismsTable.getRangeTo(currentMorphism);
		//prepare the output denotator (same form as the input denotator)
		PowerDenotator output = (PowerDenotator) input.getPowerForm().createDefaultDenotator();
		PowerDenotator mappedDenotator = input;
		
		//Map using remaining morphisms in the list first (current morphism is dominant)
		if (morphisms.size() > 0) {
			mappedDenotator = this.getUnitedMappedDenotators(mappedDenotator, morphisms);
		}
		
		//Map using current morphism until 'from' position is reached
		if (currentRangeFrom >= 0) {
			for (int i = 0; i < currentRangeFrom; i++) {
				mappedDenotator = this.mapDenotator(mappedDenotator, currentMorphism);
			}
		} else {
			for (int i = 0; i > currentRangeFrom; i--) {
				//NEGATIV MAPPEN!
				mappedDenotator = this.mapDenotator(mappedDenotator, currentMorphism);
			}
		}
		//add the mapped denotator at 'from' position to the output 
		output = output.union(mappedDenotator);
		
		//Map until 'to' position is reached and add all denotators to the output
		if (currentRangeFrom < currentRangeTo) {
			for (int i = currentRangeFrom; i < currentRangeTo; i++) {
				mappedDenotator = this.mapDenotator(mappedDenotator, currentMorphism);
				output = output.union(mappedDenotator);
			}
		} else {
			for (int i = currentRangeFrom; i > currentRangeTo; i--) {
				//NEGATIV MAPPEN!
				mappedDenotator = this.mapDenotator(mappedDenotator, currentMorphism);
				output = output.union(mappedDenotator);
			}
		}
		return output;
	}
	
	protected PowerDenotator mapDenotator(PowerDenotator input, ModuleMorphism morphism) throws RubatoException {
		TransformationPaths paths = this.createTransformationPaths(morphism);
		ArbitraryDenotatorMapper mapper = new ArbitraryDenotatorMapper(morphism, paths);
		return mapper.getMappedPowerDenotator(input);
	}
	
	private TransformationPaths createTransformationPaths(ModuleMorphism morphism) {
		List<List<Integer>> listPaths = this.morphismsTable.getCoordinates(morphism);
		TransformationPaths transformationPaths = new TransformationPaths();
		int domDim = morphism.getDomain().getDimension();
		int codomDim = morphism.getCodomain().getDimension();
		for (int i = 0; i < domDim; i++) {
			transformationPaths.addSinglePathToNewDomainDimension(new DenotatorPath(this.inputForm, listPaths.get(i)));
		}
		for (int j = domDim; j < domDim+codomDim; j++) {
			transformationPaths.addSinglePathToNewCodomainDimension(new DenotatorPath(this.inputForm, listPaths.get(j)));
		}
		return transformationPaths;
	}
    
	/**
	 * Creates a standard instance of WallpaperRubette 
	 */
    public Rubette newInstance() {
        WallpaperRubette rubette = new WallpaperRubette();
        rubette.init();
        return rubette;
    }

    @Override
    public Rubette duplicate() {
        // TODO: This must be correctly implemented
        return newInstance();
    }

    /**
     * Returns the fact that WallpaperRubette belongs to the core rubettes
     */
    public String getGroup() {
        return RubatoConstants.CORE_GROUP;
    }

    /**
     * Returns the name of this rubette
     */
    public String getName() {
        return "Wallpaper";
    }

    /**
     * Returns the fact that this rubette has properties
     */
    public boolean hasProperties() {
        return true;
    }

    /**
     * Returns the properties panel for wallpaper specification
     */
    public JComponent getProperties() {
        if (this.properties == null) {
            this.properties = new JPanel();            
            this.properties.setLayout(new BorderLayout());
			
            this.selectForm = new JSelectForm(Repository.systemRepository(), Form.POWER);
            this.selectForm.setBorder(Utilities.makeTitledBorder("Input form"));
            this.selectForm.addActionListener(this);
            this.properties.add(this.selectForm, BorderLayout.NORTH);
			
			JPanel morphismButtonsPanel = new JPanel();
			morphismButtonsPanel.setLayout(new GridLayout(1, 4, 5, 5));
			//this.viewMorphismsButton = new JButton("View");
			//this.viewMorphismsButton.addActionListener(this);
            
			this.addMorphismButton = new JButton("Add");
			this.addMorphismButton.addActionListener(this);
            this.addMorphismButton.setToolTipText("Add a new morphism");
            
			this.removeMorphismButton = new JButton("Remove");
			this.removeMorphismButton.addActionListener(this);
            this.removeMorphismButton.setToolTipText("Remove selected morphism");
            
			this.morphismUpButton = new JButton("Up");
			this.morphismUpButton.addActionListener(this);
            this.morphismUpButton.setToolTipText("Move up selected morphism");
            
			this.morphismDownButton = new JButton("Down");
			this.morphismDownButton.addActionListener(this);
            this.morphismDownButton.setToolTipText("Move down selected morphism");
            
			//morphismButtonsPanel.add(this.viewMorphismsButton);
			morphismButtonsPanel.add(this.addMorphismButton);
			morphismButtonsPanel.add(this.removeMorphismButton);
			morphismButtonsPanel.add(this.morphismUpButton);
			morphismButtonsPanel.add(this.morphismDownButton);
			
			JPanel morphismsPanel = new JPanel();
			morphismsPanel.setLayout(new BorderLayout(5, 10));
			morphismsPanel.setBorder(Utilities.makeTitledBorder("Wallpaper dimensions"));
			morphismsPanel.setMinimumSize(new Dimension(0, 50));
			morphismsPanel.add(morphismButtonsPanel, BorderLayout.NORTH);
			morphismsPanel.add(this.morphismsTable, BorderLayout.CENTER);
			
			this.properties.add(morphismsPanel, BorderLayout.CENTER);
			
			this.statusline = new JStatusline();
			this.properties.add(this.statusline, BorderLayout.SOUTH);
			this.revertProperties();
        }
        return this.properties;
    }
    
    /**
     * Applies the changes of the morphisms table and the rubette properties
     */
    public boolean applyProperties() {
        PowerForm form = (PowerForm) this.selectForm.getForm();
        
        if (form != null && this.morphismsTable.getMorphismCount() > 0) {
        	List<SimpleForm> simpleForms = this.simpleFormFinder.getSimpleForms();
			if (simpleForms != null && simpleForms.size() > 0) {
				this.setInputForm(form);
			} else {
				this.setStatuslineText("The selected input form contains no subforms of type SimpleForm");
				return false;
			}
			boolean applied = this.morphismsTable.applyChanges();
			if (applied) {
				this.setStatuslineText("");
			}
			return applied;
		} else {
            this.setStatuslineText("Input data incomplete");
			return false;
        }
    }
    
    /*
     * sets a text to the statusline located in the south of the properties window
     */
    protected void setStatuslineText(String text) {
    	this.statusline.setText(text);
    }

    /*
     * sets this input form and updates properties window and internal variables
     */
    protected void setInputForm(PowerForm form) {
		this.inputForm = form;
		if (this.selectForm != null) {
			this.selectForm.setForm(form);
		}
		this.simpleFormFinder.setParentForm(form);
	}
	
    /*
     * returns the input form currently selected in the properties window (not applied)
     */
	protected PowerForm getTempInputForm() {
		if (this.selectForm != null) {
			return (PowerForm)this.selectForm.getForm();
		} else {
			return this.inputForm;
		}
	}
	
	/**
	 * Returns all SimpleForms located in this input form (dynamically changed).
	 * Not all forms yet, though...
	 * @return the list of all simple type subforms
	 */
	public List<SimpleForm> getSimpleForms() {
		return this.simpleFormFinder.getSimpleForms();
	}
	
	/**
	 * Returns all paths belonging to the simple forms returned by getSimpleForms().
	 * @return the list of paths
	 */
	public List<List<Integer>> getSimpleFormPaths() {
		return this.simpleFormFinder.getSimpleFormPaths();
	}

	/**
	 * Reverts this properties and the changes to this morphism table. 
	 */
    public void revertProperties() {
        this.selectForm.setForm(this.inputForm);
		this.morphismsTable.revertChanges();
    }
    
    /*
     * returns the frame where the properties panel is shown
     */
	protected Frame getPropertiesFrame() {
		return JOptionPane.getFrameForComponent(this.properties);
	}
	
	public boolean hasView() {
		return true;
	}
	
	public JComponent getView() {
		return this.morphismsTable.getMorphismsView();
	}
    
    /*
     * adds a morphism to this morphisms table. method used by fromXML() and and the test methods.
     */
    protected void addMorphism(ModuleMorphism morphism, int rangeFrom, int rangeTo, List<List<Integer>> coordinates) {
    	this.morphismsTable.addMorphism(morphism, rangeFrom, rangeTo, coordinates);
    	this.morphismsTable.applyChanges();
    }
    
    /**
     * Processes the events comming from the selectForm and the buttons in the properties window.
     */
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == this.selectForm) {
        	this.simpleFormFinder.setParentForm((PowerForm)this.selectForm.getForm());
            this.morphismsTable.resetSelectedCoordinates();
        } else if (src == this.viewMorphismsButton) {
        	//this.morphismsTable.viewMorphisms();
		} else if (src == this.addMorphismButton) {
        	this.morphismsTable.addMorphism();
        	this.packPropertiesWindow();
		} else if (src == this.removeMorphismButton) {
			this.morphismsTable.removeSelectedRows();
			this.packPropertiesWindow();
		} else if (src == this.morphismUpButton) {
			this.morphismsTable.moveSelectedRowsUp();
		} else if (src == this.morphismDownButton) {
			this.morphismsTable.moveSelectedRowsDown();
		}
    }
	
	/*
	 * packs this properties window
	 */
	private void packPropertiesWindow() {
		Window propertiesWindow = Utilities.getWindow(this.properties);
		if (propertiesWindow != null) {
			propertiesWindow.pack();
		}
	}
	
	/**
	 * Returns the table with all morphisms and their characteristics for the wallpaper
	 * @return this morphisms table
	 */
	public JWallpaperDimensionsTable getMorphismsTable() {
		return this.morphismsTable;
	}

	public boolean hasInfo() {
        return true;
    }
    
    public String getInfo() {
        if (this.inputForm == null) {
            return "Input form not set";
        } else {
        	return inputForm.getNameString() + ": " + inputForm.getTypeString() + "\n" 
        		+ this.morphismsTable.getMorphismCount() + " dimensions";
        }
    }
    
    public String getShortDescription() {
        return "Creates a wallpaper using a number of morphisms";
    }

    public String getLongDescription() {
        return "Creates a wallpaper from an input power denotator and a number of morphisms.";        
    }
    
    public String getInTip(int i) {
        return "Input power denotator";
    }
    
    public String getOutTip(int i) {
        return "Output power denotator";
    }

    private static final String MORPHISM = "Morphism";
	private static final String R_FROM = "rangeFrom";
	private static final String R_TO = "rangeTo";
	private static final String PATH = "ElementPath";
	private static final String INT = "Integer";
    
    public void toXML(XMLWriter writer) {
        if (this.inputForm != null) {
            writer.writeFormRef(this.inputForm);
			for (int i = 0; i < this.morphismsTable.getMorphismCount(); i++) {
				ModuleMorphism currentMorphism = this.morphismsTable.getMorphism(i);
				writer.openBlock(MORPHISM,
					R_FROM, this.morphismsTable.getRangeFrom(currentMorphism),
					R_TO, this.morphismsTable.getRangeTo(currentMorphism));
				currentMorphism.toXML(writer);
				List<List<Integer>> currentElementPaths
					= this.morphismsTable.getCoordinates(currentMorphism);
				for (int j = 0; j < currentElementPaths.size(); j++) {
					List<Integer> currentElementPath = currentElementPaths.get(j);
					writer.openBlock(PATH);
					for (int k = 0; k < currentElementPath.size(); k++) {
						writer.empty(INT, VALUE_ATTR, currentElementPath.get(k));
					}
					writer.closeBlock();
				}
				writer.closeBlock();
			}
        }
    }
    
    public Rubette fromXML(XMLReader reader, Element element) {
		WallpaperRubette loadedRubette = new WallpaperRubette();
        loadedRubette.init();
		Element child = XMLReader.getChild(element, FORM);
		loadedRubette.setInputForm((PowerForm)reader.parseAndResolveForm(child));
		
		child = XMLReader.getNextSibling(child, MORPHISM);
		while (child != null) {
			int currentFrom = XMLReader.getIntAttribute(child, R_FROM, 0);
			int currentTo = XMLReader.getIntAttribute(child, R_TO, 1);
			Element grandChild = XMLReader.getChild(child, MODULEMORPHISM);
			ModuleMorphism currentMorphism = reader.parseModuleMorphism(grandChild);
			List<List<Integer>> currentElementPaths = new ArrayList<List<Integer>>();
			grandChild = XMLReader.getNextSibling(grandChild, PATH);
			while (grandChild != null) {
				List<Integer> currentElementPath = new ArrayList<Integer>();
				Element greatGrandChild = XMLReader.getChild(grandChild, INT);
				while (greatGrandChild != null) {
					int currentInt = XMLReader.getIntAttribute(greatGrandChild, VALUE_ATTR, 0);
					currentElementPath.add(new Integer(currentInt));
					greatGrandChild = XMLReader.getNextSibling(greatGrandChild, INT);
				}
				currentElementPaths.add(currentElementPath);
				grandChild = XMLReader.getNextSibling(grandChild, PATH);
			}
			loadedRubette.addMorphism(currentMorphism, currentFrom, currentTo, currentElementPaths);
			child = XMLReader.getNextSibling(child, MORPHISM);
		}
		return loadedRubette;
    }

}
