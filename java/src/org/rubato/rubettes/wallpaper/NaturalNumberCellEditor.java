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

package org.rubato.rubettes.wallpaper;

import java.awt.Color;

import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import org.rubato.composer.preferences.UserPreferences;

public class NaturalNumberCellEditor
        extends DefaultCellEditor
        implements CaretListener {

    public NaturalNumberCellEditor(JWallpaperDimensionsTable table) {
        super(new JTextField());
        this.textField = (JTextField)getComponent();
        this.textField.addCaretListener(this);
        this.bgColor = this.textField.getBackground();
    }
    
    public void caretUpdate(CaretEvent e) {
        checkValue();
    }
    
    public void up() {
        textField.setText(Integer.toString(getValue()+1));
    }
    
    public void down() {
        textField.setText(Integer.toString(Math.max(getValue()-1, 0)));
    }

    public int getValue() {
        int i = 0;
        try {
            i = Math.max(Integer.parseInt(textField.getText()), 0);
        }
        catch (NumberFormatException e) {}
        return i;
    }
    
    private void checkValue() {
        textField.setBackground(bgColor);
        try {
            int i = Integer.parseInt(textField.getText());
            if (i < 0) {
                textField.setBackground(errorColor);
            }
        }
        catch (NumberFormatException e) {
            textField.setBackground(errorColor);
        }
    }
    
    private JTextField textField;
    private Color      bgColor;
    private Color      errorColor = UserPreferences.getUserPreferences().getEntryErrorColor();
}
