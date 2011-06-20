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

package org.rubato.composer.notes;

import static org.rubato.xml.XMLConstants.NOTE;
import static org.rubato.xml.XMLConstants.X_ATTR;
import static org.rubato.xml.XMLConstants.Y_ATTR;

import java.awt.Dimension;

import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

public class NoteModel {

    public NoteModel(JNote jnote, String title, String text) {
        this.jnote = jnote;
        this.title = title;
        this.text  = text;
    }    
    

    public NoteModel(int x, int y, int w, int h, String title, String text, int fg, int bg) {
        this.jnote = null;
        this.x     = x;
        this.y     = y;
        this.w     = w;
        this.h     = h;
        this.title = title;
        this.text  = text;
        this.fg    = fg;
        this.bg    = bg;
    }
    
    
    public void setJNote(JNote jnote) {
        this.jnote = jnote;
    }

    
    public int getX() {
        return x;
    } 

    
    public int getY() {
        return y;
    } 


    public Dimension getSize() {
        return new Dimension(w, h);
    } 

    
    public String getTitle() {
        return title;
    }
    

    public void setTitle(String s) {
        title = s;
    }

    
    public String getText() {
        return text;
    }
    

    public void setText(String s) {
        text = s;
    }
    
    
    public int getFg() {
        return fg;
    }


    public int getBg() {
        return bg;
    }
    

    public JNote createJNote() {
        return new JNote(this);
    }
    
    
    private final static String FG_ATTR = "fg"; //$NON-NLS-1$
    private final static String BG_ATTR = "bg"; //$NON-NLS-1$
    private final static String W_ATTR  = "w"; //$NON-NLS-1$
    private final static String H_ATTR  = "h"; //$NON-NLS-1$
    private final static String TITLE   = "Title"; //$NON-NLS-1$
    private final static String TEXT    = "Text"; //$NON-NLS-1$

    
    public void toXML(XMLWriter writer) {
        writer.openBlock(NOTE, X_ATTR, jnote.getX(), Y_ATTR, jnote.getY(),
                               W_ATTR, jnote.getWidth(), H_ATTR, jnote.getHeight(),
                               FG_ATTR, jnote.getForegroundColor().getRGB(),
                               BG_ATTR, jnote.getBackgroundColor().getRGB());
        if (jnote.getTitle() != null) {
            writer.openInline(TITLE);
            writer.writeTextNode(jnote.getTitle());
            writer.closeInline();
        }
        writer.openBlock(TEXT);
        writer.writeTextNode(jnote.getText());
        writer.closeBlock();
        writer.closeBlock();
    }
    
    
    public static NoteModel fromXML(XMLReader reader, Element noteElement) {
        int x = XMLReader.getIntAttribute(noteElement, X_ATTR, 0);
        int y = XMLReader.getIntAttribute(noteElement, Y_ATTR, 0);
        int w = XMLReader.getIntAttribute(noteElement, W_ATTR, 180);
        int h = XMLReader.getIntAttribute(noteElement, H_ATTR, 120);
        int fg = XMLReader.getIntAttribute(noteElement, FG_ATTR, 0);
        int bg = XMLReader.getIntAttribute(noteElement, BG_ATTR, -1);
        
        String title = null;
        Element child = XMLReader.getChild(noteElement, TITLE);
        if (child != null) {
            title = XMLReader.getText(child).trim(); 
        }
        
        String text = ""; //$NON-NLS-1$
        child = XMLReader.getChild(noteElement, TEXT);
        if (child != null) {
            text = XMLReader.getText(child);            
        }
        return new NoteModel(x, y, w, h, title, text, fg, bg);
    }
    
    
    public NoteModel newInstance() {
        if (jnote != null) {
            x     = jnote.getX();
            y     = jnote.getY();
            w     = jnote.getWidth();
            h     = jnote.getHeight();
            title = jnote.getTitle();
            text  = jnote.getText();
            bg    = jnote.getBackgroundColor().getRGB();
            fg    = jnote.getForegroundColor().getRGB();
        }
        NoteModel newNote = new NoteModel(x, y, w, h, title, text, fg, bg);
        return newNote;
    }
    
    
    private JNote  jnote = null;
    private String title = null;
    private String text  = null;
    private int    x     = 0;
    private int    y     = 0;
    private int    w     = 180;
    private int    h     = 120;
    private int    fg    = 0;
    private int    bg    = -1;
}
