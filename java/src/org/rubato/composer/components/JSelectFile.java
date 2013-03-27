/*
 * Copyright (C) 2013 Florian Thalmann
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

import static org.rubato.composer.Utilities.makeTitledBorder;

import java.awt.BorderLayout;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

import javax.swing.*;
import javax.swing.event.EventListenerList;

import org.rubato.util.CustomFileFilter;


public class JSelectFile extends JPanel implements ActionListener {
	
	private JLabelField fileLabelField;
	private JButton browseButton;
	private JFileChooser fileChooser;
	private CustomFileFilter fileFilter;
	
	//TODO: do we need label??
	private String label;
	private boolean saving;
	private File currentDirectory;
	private File selectedFile;
	
	public JSelectFile(String[] allowedExtensions, boolean saving) {
        this.fileFilter = new CustomFileFilter(false, allowedExtensions);
        this.fileChooser = new JFileChooser();
    	this.fileChooser.setFileFilter(this.fileFilter);  
    	this.currentDirectory = new File(".");
    	this.saving = saving;
        createLayout();
    }
    
    
    public JSelectFile(String[] allowedExtensions, boolean saving, String label) {
    	this(allowedExtensions, saving);  
        this.label = label;
        createLayout();
    }
    
    
    public File getFile() {
        return this.selectedFile;
    }
    
    
    public void setFile(File file) {
        this.selectedFile = file;
        this.updateFileChooserAndLabel();
    }
    
    
    public void clear() {
        this.selectedFile = null;
        this.updateFileChooserAndLabel();
    }
    
    
    public void disableBorder() {
        this.setBorder(null);
    }
    
    
    private void createLayout() {
        this.setLayout(new BorderLayout(3, 0));        
        if (this.label != null) {
        	this.setBorder(makeTitledBorder(this.label));
        } else {
        	this.setBorder(makeTitledBorder("File (" + this.fileFilter.getDescription() + "):"));
        }
        
        this.fileLabelField = new JLabelField();
        this.add(this.fileLabelField, BorderLayout.CENTER);
        
        Box buttonBox = Box.createHorizontalBox();
        buttonBox.add(Box.createHorizontalStrut(5));
        this.browseButton = new JButton("Browse...");
        this.browseButton.setToolTipText("Browse file system");
        this.browseButton.addActionListener(this);
        this.add(this.browseButton, BorderLayout.EAST);
    }

    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == this.browseButton) {
        	this.browseFile(this);
        }
    }
    
    protected void browseFile(JPanel host) {
        this.fileChooser.setCurrentDirectory(this.currentDirectory);
        int returnState;
        if (this.saving) {
        	returnState = this.fileChooser.showSaveDialog(host);
        } else {
        	returnState = this.fileChooser.showOpenDialog(host);
        }
        if (returnState == JFileChooser.APPROVE_OPTION) {
            this.selectedFile = this.fileChooser.getSelectedFile();
            this.currentDirectory = this.fileChooser.getCurrentDirectory();
            this.updateFileLabelField();
        }
        fireActionEvent();
    }
    
    
    private void updateFileChooserAndLabel() {
        this.fileChooser.setSelectedFile(this.selectedFile);
        this.updateFileLabelField();
    }
    
    
    private void updateFileLabelField() {
    	if (this.selectedFile != null) {
    		try {
    			this.fileLabelField.setText(this.selectedFile.getCanonicalPath());
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    }
    

    private ActionEvent       actionEvent  = null;
    private EventListenerList listenerList = new EventListenerList();
    private final static String EMPTY_STRING = "";

    public void addActionListener(ActionListener l) {
        listenerList.add(ActionListener.class, l);
    }

    
    public void removeActionListener(ActionListener l) {
        listenerList.remove(ActionListener.class, l);
    }


    protected void fireActionEvent() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==ActionListener.class) {
                if (actionEvent == null) {
                    actionEvent = new ActionEvent(this, 0, EMPTY_STRING);
                }
                ((ActionListener)listeners[i+1]).actionPerformed(actionEvent);
            }
        }
    }
    
}