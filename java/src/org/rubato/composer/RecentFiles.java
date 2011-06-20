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

package org.rubato.composer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.LinkedList;

import javax.swing.*;

import org.rubato.composer.preferences.UserPreferences;

public class RecentFiles implements ActionListener {

    public RecentFiles(JMenu menu) {
        this.menu = menu;
        clearItem = new JMenuItem("Clear");   
        clearItem.addActionListener(this);
        preferences = UserPreferences.getUserPreferences();
        actionEvent = new ActionEvent(this, 0, "open");
        load();
        updateMenu();
    }
    
    
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == clearItem) {
            clear();
        }
    }
    
    
    public File getSelectedFile() {
        return selectedFile;
    }
    
    
    public void activate(File file) {
        FileAction fileAction = new FileAction(file);
        recentFiles.remove(fileAction);
        recentFiles.add(0, fileAction);
        updateMenu();
    }


    public void setAction(Action action) {
        this.action = action;
    }
    
    
    private void save() {
        LinkedList<File> files = new LinkedList<File>();
        for (FileAction fileAction : recentFiles) {
            files.add(fileAction.file);
        }
        preferences.setRecentFiles(files);
    }
    
    
    private void load() {
        recentFiles = new LinkedList<FileAction>();
        LinkedList<File> files = preferences.getRecentFiles();        
        if (files != null) {
            for (File file : files) {
                recentFiles.add(new FileAction(file));
            }
        }
        
    }
    
    
    private void updateMenu() {
        while (recentFiles.size() > MAX_FILES) {
            recentFiles.remove(MAX_FILES);
        }
        if (hasFiles()) {
            menu.removeAll();
            for (FileAction fileAction : recentFiles) {
                JMenuItem subMenuItem = new JMenuItem(fileAction);
                menu.add(subMenuItem);
            }
            menu.addSeparator();
            menu.add(clearItem);
            menu.setEnabled(true);
        }
        else {
            menu.removeAll();
            menu.setEnabled(false);
        }
        save();
    }
    
    
    public void clear() {
        recentFiles.clear();
        updateMenu();
    }
    
    
    private boolean hasFiles() {
        return recentFiles.size() > 0;
    }

    
    private class FileAction extends AbstractAction {

        public FileAction(File file) {
            super(file.getPath());
            this.file = file;
        }
        
        public void actionPerformed(ActionEvent e) {
            if (action != null) {
                selectedFile = file;
                action.actionPerformed(actionEvent);
            }
        }
        
        public boolean equals(Object obj) {
            return obj instanceof FileAction && file.equals(((FileAction)obj).file);
        }
        
        public String toString() {
            return file.toString();
        }
        
        public File file;
    }
    
    
    private final JMenu           menu;
    private final JMenuItem       clearItem;
    private final UserPreferences preferences;
    
    protected final ActionEvent     actionEvent;

    private LinkedList<FileAction> recentFiles;
    
    protected Action action       = null;
    protected File   selectedFile = null;
    
    private final static int MAX_FILES = 10;
}
