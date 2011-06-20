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

package org.rubato.composer.rubette;

import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ImageIcon;

import org.rubato.base.Rubette;
import org.rubato.rubettes.builtin.MacroRubette;
import org.rubato.xml.XMLWriter;

public class RubetteModel {

    public RubetteModel(Rubette rubette, String name) {
        assert(rubette != null);
        rubette.setModel(this);
        this.jrubette = null;
        this.rubette = rubette;
        this.name = name;
        inputs = new Link[rubette.getInCount()];
    }

    
    public Rubette getRubette() {
        return rubette;
    }
    
    
    public JRubette getJRubette() {
        return jrubette;
    }
    
    
    public void setJRubette(JRubette jrubette) {
        this.jrubette = jrubette;
    }
    
    
    public ImageIcon getIcon() {
        return rubette.getIcon();
    }
    
    
    public boolean hasInfo() {
        return rubette.hasInfo();        
    }
    
    
    public boolean hasProperties() {
        return rubette.hasProperties();
    }
    
    
    public boolean hasView() {
        return rubette.hasView();
    }
    
    
    public int getInCount() {
        return rubette.getInCount();
    }
    
    
    public void resizeInputs() {
        Link[] newInputs = new Link[rubette.getInCount()];
        for (int i = 0; i < Math.min(inputs.length, newInputs.length); i++) {
            newInputs[i] = inputs[i];            
        }
        inputs = newInputs;
    }

    
    public int getOutCount() {
        return rubette.getOutCount();
    }
    
    //
    // Links
    //
    
    public void setInLink(Link link) {
        inLinks.add(link);
        inputs[link.getDestPos()] = link;
    }
    
    
    public Link getInLink(int i) {
        return inputs[i];
    }

    
    public ArrayList<Link> getInLinks() {
        return inLinks;
    }
    
    
    public ArrayList<Link> getOutLinks() {
        return outLinks;
    }

    
    public void addOutLink(Link link) {
        outLinks.add(link);        
    }
    
    
    public void removeInLink(Link link) {
        inLinks.remove(link);
        inputs[link.getDestPos()] = null;
    }
    
    
    public void removeOutLink(Link link) {
        outLinks.remove(link);
    }
    
    
    public String getInTip(int i) {
        String s = rubette.getInTip(i);
        return (s != null)?s:"Input #"+i; //$NON-NLS-1$
    }
    
    
    public String getOutTip(int i) {
        String s = rubette.getOutTip(i);
        return (s != null)?s:"Output #"+i; //$NON-NLS-1$
    }

    
    public String getName() {
        return name;
    }

    
    public String getShortDescription() {
        return rubette.getShortDescription();
    }

    
    public void setName(String name) {
        this.name = name;
        if (rubette instanceof MacroRubette) {
            ((MacroRubette)rubette).setName(name);
        }
    }
    
    
    public String getInfo() {
        return rubette.getInfo();
    }


    public int getInLinkCount() {
        return inLinks.size();
    }
    

    public int getOutLinkCount() {
        return outLinks.size();
    }

    
    public List<RubetteModel> getDependents() {
        return dependents;
    }
    
    
    public List<RubetteModel> getFirstDependents() {
        List<RubetteModel> alldependents = new LinkedList<RubetteModel>();
        for (Link link : outLinks) {
            alldependents.add(link.getDestModel());
        }
        return alldependents;
    }
    
    
    public List<RubetteModel> getDependencies() {
        return dependencies;
    }
    
    
    public List<RubetteModel> getFirstDependencies() {
        List<RubetteModel> alldependencies = new LinkedList<RubetteModel>();
        for (Link link : inLinks) {
            alldependencies.add(link.getSrcModel());
        }
        return alldependencies;
    }
    
    
    public void computeDependents() {
        dependents.clear();
        for (Link link : outLinks) {
            RubetteModel dest = link.getDestModel();
            dependents.add(dest);
            for (RubetteModel model : dest.getDependents()) {
                if (!dependents.contains(model)) {
                    dependents.add(model);
                }
            }
        }
    }

    
    public void computeDependencies() {
        dependencies.clear();
        for (Link link : inLinks) {
            RubetteModel src = link.getSrcModel();
            dependencies.add(src);
            for (RubetteModel model : src.getDependencies()) {
                if (!dependencies.contains(model)) {
                    dependencies.add(model);
                }
            }
        }
    }
    
    
    public RubetteModel duplicate() {
        RubetteModel newModel = new RubetteModel(rubette.duplicate(), name);
        newModel.setLocation(getLocation());
        return newModel;
    }
    
    
    public void toXML(XMLWriter writer) {
        rubette.toXML(writer);
    }
    
    
    public int getSerial() {
        return serial;
    }
    
    
    public void setSerial(int i) {
        serial = i;
    }

    
    public Point getLocation() {
        if (jrubette != null) {
            location = jrubette.getLocation();
        }
        return location;
    }

    
    public void setLocation(Point pt) {
        location = pt;
    }
    
    
    public void togglePassThrough() {
        passthrough = !passthrough;
    }
    
    
    public void setPassThrough(boolean p) {
        passthrough = p;
    }
    
    
    public boolean isPassThrough() {
        return passthrough;
    }
    
    
    public boolean canPassThrough() {
        return getInCount() > 0 && getOutCount() > 0;
    }
    
    
    public RubetteModel newInstance() {
        RubetteModel newModel = new RubetteModel(rubette.duplicate(), name);
        newModel.setLocation(getLocation());
        return newModel;
    }
    
    
    public String toString() {
        return "RubetteModel["+getName()+"]"; //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    
    private JRubette   jrubette;
    private String     name;
    private ArrayList<Link> inLinks = new ArrayList<Link>();
    private ArrayList<Link> outLinks = new ArrayList<Link>();
    private Link[]     inputs;
    private Rubette    rubette;
    private int        serial;
    private Point      location;
    private boolean    passthrough = false;
    
    private LinkedList<RubetteModel> dependencies = new LinkedList<RubetteModel>();
    private LinkedList<RubetteModel> dependents = new LinkedList<RubetteModel>();
}
