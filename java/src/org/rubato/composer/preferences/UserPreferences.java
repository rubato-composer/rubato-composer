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

package org.rubato.composer.preferences;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.File;
import java.util.LinkedList;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public final class UserPreferences {
        
    public static UserPreferences getUserPreferences() {
        return userPreferences;
    }
    
    
    public void save() {
        try {
            preferences.flush();
        }
        catch (BackingStoreException e) {}
    }

    
    public void setMaximized(boolean b) {
        preferences.putBoolean(PREF_WINDOW_MAXIMIZED, b);
    }
    
    
    public boolean getMaximized() {
        return preferences.getBoolean(PREF_WINDOW_MAXIMIZED, false);
    }
    
    
    public boolean getGeometrySaved() {
        return preferences.getBoolean(PREF_WINDOW_SAVEGEO, false);
    }
    
    
    public void setGeometrySaved(boolean b) {
        preferences.putBoolean(PREF_WINDOW_SAVEGEO, b);
    }
    
    
    public void setGeometry(int x, int y, int width, int height) {
        preferences.putInt(PREF_WINDOW_X, x);        
        preferences.putInt(PREF_WINDOW_Y, y);        
        preferences.putInt(PREF_WINDOW_WIDTH, width);        
        preferences.putInt(PREF_WINDOW_HEIGHT, height);        
    }
    
    
    public Rectangle getGeometry() {
        int x = preferences.getInt(PREF_WINDOW_X, -1);
        int y = preferences.getInt(PREF_WINDOW_Y, -1);
        int w = preferences.getInt(PREF_WINDOW_WIDTH, -1);
        int h = preferences.getInt(PREF_WINDOW_HEIGHT, -1);
        return new Rectangle(x, y, w, h);
    }
    
    
    public void setLinkType(int type) {
        preferences.putInt(PREF_LINKS_ZIGZAG, type);
    }
    

    public int getLinkType() {
        return preferences.getInt(PREF_LINKS_ZIGZAG, 2);
    }
    
    
    public void setAskBeforeLeaving(boolean b) {
        preferences.putBoolean(PREF_WINDOW_ASK, b);
    }


    public boolean getAskBeforeLeaving() {
        return preferences.getBoolean(PREF_WINDOW_ASK, true);
    }

    
    public void setDefaultQuantization(int q) {
        q = Math.abs(q);
        if (q == 0) {
            q = 1920;
        }
        preferences.putInt(PREF_DEFAULT_QUANT, q);
    }
    
    
    public int getDefaultQuantization() {
        int q = preferences.getInt(PREF_DEFAULT_QUANT, 1920);
        q = Math.abs(q);
        if (q == 0) {
            q = 1920;
        }
        return q;
    }
    
    
    public void setCurrentDirectory(File file) {
        String path = "."; //$NON-NLS-1$
        if (file != null && file.isDirectory()) {
            path = file.getAbsolutePath();
        }
        preferences.put(PREF_DIR_CURRENT, path);
    }
    
    
    public File getCurrentDirectory() {
        String path = preferences.get(PREF_DIR_CURRENT, "."); //$NON-NLS-1$
        File file = new File(path);
        if (file.isDirectory() && file.canRead()) {
            return file;
        }
        else {
            return new File("."); //$NON-NLS-1$
        }
    }
    
    
    public Color getEntryErrorColor() {
        return new Color(1.0f, 0.6f, 0.0f);
    }
    
    
    public LinkedList<File> getRecentFiles() {
        LinkedList<File> files = new LinkedList<File>();
        int i = 0;
        String key = "RecentFile"+i;
        String file = preferences.get(key, "");
        while (!file.equals("")) {
            files.add(new File(file));
            i++;
            key = "RecentFile"+i;
            file = preferences.get(key, "");
        }
        return files;
    }
    
    
    public void setRecentFiles(LinkedList<File> files) {
        int i = 0;
        for (File file : files) {
            preferences.put("RecentFile"+i, file.getPath());
            i++;
        }
        preferences.put("RecentFile"+i, "");
    }
    
    
    public void setShowProgress(boolean b) {
        preferences.putBoolean(PREF_SHOW_PROGRESS, b);
    }
    
    
    public boolean getShowProgress() {
        return preferences.getBoolean(PREF_SHOW_PROGRESS, true);
    }
    
    
    // private
    
    private Preferences preferences;
    
    private UserPreferences() {
        preferences = Preferences.userNodeForPackage(UserPreferences.class);         
    }
    
    private static UserPreferences userPreferences = new UserPreferences();
    
    private static final String PREF_WINDOW_MAXIMIZED = "window.maximized"; //$NON-NLS-1$
    private static final String PREF_WINDOW_ASK       = "window.ask"; //$NON-NLS-1$
    private static final String PREF_WINDOW_X         = "window.x"; //$NON-NLS-1$
    private static final String PREF_WINDOW_Y         = "window.y"; //$NON-NLS-1$
    private static final String PREF_WINDOW_WIDTH     = "window.width"; //$NON-NLS-1$
    private static final String PREF_WINDOW_HEIGHT    = "window.height"; //$NON-NLS-1$
    private static final String PREF_WINDOW_SAVEGEO   = "window.saveGeometry"; //$NON-NLS-1$
    private static final String PREF_LINKS_ZIGZAG     = "links.zigzag"; //$NON-NLS-1$
    private static final String PREF_DIR_CURRENT      = "dir.current"; //$NON-NLS-1$
    private static final String PREF_DEFAULT_QUANT    = "rational.quant"; //$NON-NLS-1$
    private static final String PREF_SHOW_PROGRESS    = "run.showprogress"; //$NON-NLS-1$
}
