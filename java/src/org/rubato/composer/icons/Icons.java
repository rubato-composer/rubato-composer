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

package org.rubato.composer.icons;

import java.net.URL;

import javax.swing.ImageIcon;

@SuppressWarnings("nls")
public class Icons {
    
    public final static ImageIcon emptyIcon; 
    public final static ImageIcon newIcon;
    public final static ImageIcon openIcon;
    public final static ImageIcon revertIcon;
    public final static ImageIcon addIcon;
    public final static ImageIcon saveIcon;
    public final static ImageIcon saveasIcon;
    public final static ImageIcon quitIcon;
    public final static ImageIcon newnetIcon;
    public final static ImageIcon closeIcon;
    public final static ImageIcon runIcon;
    public final static ImageIcon runContIcon;
    public final static ImageIcon stopIcon;
    public final static ImageIcon prefIcon;
    public final static ImageIcon rubatoIcon; 
    public final static ImageIcon splashIcon; 
    public final static ImageIcon denoIcon; 
    public final static ImageIcon formIcon; 
    public final static ImageIcon moduleIcon; 
    public final static ImageIcon morphIcon; 
    public final static ImageIcon limitIcon; 
    public final static ImageIcon colimitIcon; 
    public final static ImageIcon powerIcon; 
    public final static ImageIcon listIcon; 
    public final static ImageIcon simpleIcon; 
    public final static ImageIcon midiinIcon; 
    public final static ImageIcon midioutIcon; 
    public final static ImageIcon tdownIcon; 
    public final static ImageIcon trightIcon; 
    public final static ImageIcon schemeIcon; 
    public final static ImageIcon schemeEditIcon; 
    public final static ImageIcon browseIcon;
    public final static ImageIcon cutIcon;
    public final static ImageIcon copyIcon;
    public final static ImageIcon pasteIcon;

    
    public static ImageIcon loadIcon(Object obj, String name) {
        URL imageURL;
        imageURL = obj.getClass().getResource(name);
        return new ImageIcon(imageURL);
    }
    
    
    public static ImageIcon loadIcon(Class<?> cls, String name) {
        URL imageURL;
        imageURL = cls.getResource(name);
        return new ImageIcon(imageURL);
    }

    
    public static ImageIcon loadIcon(String name) {
        return loadIcon(Icons.class, name);
    }
    

    static {
        emptyIcon   = loadIcon("emptyicon.png");
        newIcon     = loadIcon("newicon.png");
        openIcon    = loadIcon("openicon.png");
        revertIcon  = loadIcon("reverticon.png");
        addIcon     = loadIcon("addicon.png");
        saveIcon    = loadIcon("saveicon.png");
        saveasIcon  = loadIcon("saveasicon.png");
        quitIcon    = loadIcon("quiticon.png");
        runIcon     = loadIcon("runicon.png");
        runContIcon = loadIcon("runconticon.png");
        stopIcon    = loadIcon("stopicon.png");
        newnetIcon  = loadIcon("newneticon.png");
        closeIcon   = loadIcon("closeicon.png");
        prefIcon    = loadIcon("preficon.png");
        rubatoIcon  = loadIcon("rubatoicon.png");
        splashIcon  = loadIcon("splash.png");
        denoIcon    = loadIcon("denotator.png");
        formIcon    = loadIcon("form.png");
        moduleIcon  = loadIcon("module.png");
        morphIcon   = loadIcon("morph.png");
        limitIcon   = loadIcon("limit.png");
        colimitIcon = loadIcon("colimit.png");
        powerIcon   = loadIcon("power.png");
        listIcon    = loadIcon("list.png");
        simpleIcon  = loadIcon("simple.png");
        midiinIcon  = loadIcon("midiin.png");
        midioutIcon = loadIcon("midiout.png");
        tdownIcon   = loadIcon("triandown.png");
        trightIcon  = loadIcon("trianright.png");
        schemeIcon  = loadIcon("schemeicon.png");
        schemeEditIcon  = loadIcon("schemeediticon.png");
        browseIcon  = loadIcon("browseicon.png");
        cutIcon     = loadIcon("cuticon.png");
        copyIcon    = loadIcon("copyicon.png");
        pasteIcon   = loadIcon("pasteicon.png");
    }
}
