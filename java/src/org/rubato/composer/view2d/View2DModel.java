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

package org.rubato.composer.view2d;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.rubato.math.module.Module;
import org.rubato.math.yoneda.*;

public class View2DModel {

    public View2DModel(Form form) {
        windowConfig = new WindowConfig();
        setForm(form);
    }
    
    
    public void setDenotators(ArrayList<Denotator> denotators) {
        this.denotators = denotators;
    }
    
    
    public ArrayList<Denotator> getDenotators() {
        return denotators;
    }
    
    
    public void setWindowConfig(WindowConfig windowConfig) {
        this.windowConfig = windowConfig;
    }
    
    
    public WindowConfig getWindowConfig() {
        return windowConfig;
    }
    
    
    public void setLimits(double xmin, double xmax, double ymin, double ymax) {
        windowConfig.setLimits(xmin, xmax, ymin, ymax);
    }
    
    
    public void zoom(double fx, double fy) {
        windowConfig.zoom(fx, fy);
    }
    
    
    public int worldToScreenX(double x) {
        return windowConfig.worldToScreenX(x);
    }
    
    
    public int worldToScreenY(double y) {
        return windowConfig.worldToScreenY(y);
    }
    
    
    public double screenToWorldX(int x) {
        return windowConfig.screenToWorldX(x);
    }
    
    
    public double screenToWorldY(int y) {
        return windowConfig.screenToWorldY(y);
    }
    
    
    public void setScreenSize(int width, int height) {
        windowConfig.setSize(width, height);
    }
    
    
    public void setForm(Form form) {
        if (form instanceof PowerForm || form instanceof ListForm) { 
            this.form = form;
            this.baseForm = form.getForm(0);
        }
    }
    
    
    public Form getForm() {
        return form;
    }
    

    public Form getBaseForm() {
        return baseForm;
    }

    
    public void setXAxis(Module module, int path[]) {
        setXModule(module);
        setXPath(path);
    }
    
    
    public void unsetXAxis() {
        setXModule(null);
        setXPath(null);
    }
    
    
    public void setYAxis(Module module, int path[]) {
        setYModule(module);
        setYPath(path);
    }
    
    
    public void unsetYAxis() {
        setYModule(null);
        setYPath(null);
    }
    
    
    public boolean axesDefined() {
        return xModule != null && yModule != null;
    }
    
    
    public void setXModule(Module module) {
        xModule = module;
    }
    
    
    public Module getXModule() {
        return xModule;
    }
    
    
    public void setYModule(Module module) {
        yModule = module;
    }
    
    
    public Module getYModule() {
        return yModule;
    }
    
    
    public void setXPath(int[] path) {
        xPath = path;
    }
    
    
    public int[] getXPath() {
        return xPath;
    }
    
    
    public void setYPath(int[] path) {
        yPath = path;
    }
    
    
    public int[] getYPath() {
        return yPath;
    }
    
    
    public Selection getNewSelection() {
        Selection selection = new Selection(windowConfig);
        if (selections == null) {
            selections = new LinkedList<Selection>();
        }
        selections.add(selection);
        return selection;
    }

    
    public Selection getSelectionAt(int screenX, int screenY) {
        if (selections != null) {
            for (Selection s : selections) {
                if (s.contains(screenX, screenY)) {
                    return s;
                }
            }
        }
        return null;
    }
    
    
    public List<Selection> getSelections() {
        return selections;
    }

    
    public void removeSelection(Selection selection) {
        selections.remove(selection);
    }
    
    
    public boolean hasSelections() {
        return selections != null;
    }
    
    
    public boolean selectionsContain(Point point) {
        if (selections != null) {
            for (Selection s : selections) {
                if (s.contains(point)) {
                    return true;
                }
            }
            return false;
        }
        else {
            return true;
        }
    }
    
    
    private WindowConfig windowConfig = null;
    private ArrayList<Denotator> denotators = null;
    
    private Form form = null;
    private Form baseForm = null;
    
    private Module xModule = null;
    private int[]  xPath   = null;
    private Module yModule = null;
    private int[]  yPath   = null;
    
    private List<Selection> selections = null;
}
