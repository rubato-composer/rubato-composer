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

package org.rubato.composer;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.border.Border;

import org.rubato.base.Rubette;
import org.rubato.composer.icons.Icons;

class RubetteListModel extends DefaultListModel {
    
    public RubetteListModel() {
        super();
        groups.put("Core", true); //$NON-NLS-1$
    }
    
    
    public void addRubette(Rubette rubette) {
        rubetteInfos.add(new RubetteInfo(rubette));
        String group = rubette.getGroup();
        if (!groups.containsKey(group)) {
            groups.put(group, false);
        }
        refresh();
    }

    
    public void removeRubette(Rubette rubette) {
        rubetteInfos.remove(new RubetteInfo(rubette));
        refresh();
    }
    
    
    public void toggleGroup(String group) {
        if (groups.containsKey(group)) {
            groups.put(group, !groups.get(group));
            refresh();
        }
    }

    
    public void refresh() {
        ArrayList<RubetteInfo> infoList = new ArrayList<RubetteInfo>();
        infoList.addAll(rubetteInfos);
        for (String group : groups.keySet()) {
            infoList.add(new RubetteInfo(group));
        }
        Collections.sort(infoList);
        removeAllElements();
        for (RubetteInfo info : infoList) {
            if (info.isGroup() || groups.get(info.getGroup())) {
                addElement(info);
            }
        }
    }
    
    
    public ListCellRenderer getListCellRenderer() {
        return new RubetteCellRenderer();
    }

    
    private ArrayList<RubetteInfo> rubetteInfos = new ArrayList<RubetteInfo>();

    protected HashMap<String,Boolean> groups = new HashMap<String,Boolean>();
    
    
    protected class RubetteCellRenderer extends JLabel implements ListCellRenderer {

        public Component getListCellRendererComponent(JList list,
                                                      Object value,
                                                      int index,
                                                      boolean isSelected,
                                                      boolean cellHasFocus) {
            RubetteInfo info = (RubetteInfo)value;
            if (info.isGroup()) {
                setText(info.getGroup());
                if (groups.get(info.getGroup())) {
                    setIcon(Icons.tdownIcon);
                }
                else {
                    setIcon(Icons.trightIcon);
                }
                setBackground(groupBg);
                setForeground(groupFg);
                setBorder(groupBorder);
                setEnabled(list.isEnabled());
                setFont(list.getFont());
                setOpaque(true);
                return this;
            }

            Rubette rubette = ((RubetteInfo)value).getRubette();
            setText(rubette.getName());
            ImageIcon icon = rubette.getIcon();
            if (icon == null) { icon = Icons.emptyIcon; }
            setIcon(icon);
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            }
            else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            setBorder(emptyBorder);
            setEnabled(list.isEnabled());
            setFont(list.getFont());
            setOpaque(true);
            return this;
        }
        
        private final Border emptyBorder = BorderFactory.createEmptyBorder();
        private final Border groupBorder = BorderFactory.createEmptyBorder(2, 0, 2, 0);
    }

    protected final static Color groupBg = Color.GRAY;
    protected final static Color groupFg = Color.WHITE;
}


final class RubetteInfo implements Comparable<RubetteInfo> {
    
    public RubetteInfo(Rubette rubette) {
        this.rubette = rubette;
        this.name = rubette.getName();
        this.group = rubette.getGroup();
    }
    
    
    public RubetteInfo(String group) {
        this.rubette = null;
        this.name = ""; //$NON-NLS-1$
        this.group = group;
    }
    
    public Rubette getRubette() {
        return rubette;
    }
    
    
    public String getGroup() {
        return group;
    }
    
    
    public boolean isGroup() {
        return rubette == null;
    }
    
    
    public String toString() {
        return name;
    }
    
    
    public boolean equals(Object o) {
        if (o instanceof RubetteInfo) {
            RubetteInfo ri = (RubetteInfo)o;
            return group.equals(ri.group) && name.equals(ri.name) && rubette.equals(ri.rubette);
        }
        return false;
    }
    
    
    public int compareTo(RubetteInfo ri) {
        int c = group.compareTo(ri.group);
        if (c == 0) {
            c = name.compareTo(ri.name);
        }
        return c;
    }

    
    private String  group;
    private String  name;
    private Rubette rubette;
}
