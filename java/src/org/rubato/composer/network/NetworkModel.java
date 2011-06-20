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

package org.rubato.composer.network;

import static org.rubato.xml.XMLConstants.*;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.rubato.base.Rubette;
import org.rubato.composer.JComposer;
import org.rubato.composer.RubetteManager;
import org.rubato.composer.notes.NoteModel;
import org.rubato.composer.rubette.Link;
import org.rubato.composer.rubette.RubetteModel;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

public class NetworkModel {

    public NetworkModel(JNetwork jnetwork, String name) {
        this.jnetwork = jnetwork;
        this.name = name;
    }
    
    
    public NetworkModel(String name) {
        this.jnetwork = null;
        this.name = name;
    }
    
    
    public JNetwork getJNetwork() {
        return jnetwork;
    }
    
    
    public String getName() {
        return name;
    }
    
    
    public void setName(String name) {
        this.name = name;
    }
    
    
    public String getInfo() {
        return info;
    }
    
    
    public void setInfo(String info) {
        this.info = info;
    }
    
    
    public void addRubette(RubetteModel rubette) {
        rubettes.add(rubette);
    }
    
    
    public void removeRubette(RubetteModel rubette) {
        rubettes.remove(rubette);
    }
    
    
    public ArrayList<RubetteModel> getRubettes() {
        return rubettes;
    }
    
    
    public void computeDependencyTree() {
        computeRootsAndCoroots();
        computeDependents();
        computeDependencies();
    }
    
    
    public ArrayList<RubetteModel> getDependents() {
        return dependents;
    }
    
    
    private void computeRootsAndCoroots() {
        roots.clear();
        coroots.clear();
        for (RubetteModel rubette : rubettes) {
            if (rubette.getInLinkCount() == 0) {
                roots.add(rubette);
            }
            else if (rubette.getOutLinkCount() == 0) {
                coroots.add(rubette);
            }
        }        
    }
    
    
    private void computeDependents() {
        dependents.clear();
        for (RubetteModel model : roots) {
            computeDependents(model, dependents);
            dependents.add(model);
        }
        // reverse list
        Collections.reverse(dependents);
    }
    
    
    private void computeDependents(RubetteModel model, ArrayList<RubetteModel> list) {
        for (RubetteModel rmodel : model.getFirstDependents()) {
            computeDependents(rmodel, list);
            if (!(list.contains(rmodel))) {
                list.add(rmodel);
            }
        }
    }
    
    
    private void computeDependencies() {
        dependencies.clear();
        for (RubetteModel model : coroots) {
            computeDependencies(model, dependencies);
            dependencies.add(model);
        }
        // reverse list
        Collections.reverse(dependencies);
    }
    
    
    private void computeDependencies(RubetteModel model, ArrayList<RubetteModel> list) {
        for (RubetteModel rmodel : model.getFirstDependencies()) {
            computeDependencies(rmodel, list);
            if (!(list.contains(rmodel))) {
                list.add(rmodel);
            }
        }
    }
    
    
    public void addNote(NoteModel note) {
        notes.add(note);
    }
    
    
    public void removeNote(NoteModel note) {
        notes.remove(note);
    }
    
    
    public ArrayList<NoteModel> getNotes() {
        return notes;
    }
    
    
    public void toXML(XMLWriter writer) {
        writer.openBlock(NETWORK, NAME_ATTR, getName());
        int i = 0;
        for (RubetteModel rmodel : rubettes) {
            String cls = rmodel.getRubette().getClass().getCanonicalName();
            String n = rmodel.getName();
            rmodel.setSerial(i);
            Point pt = rmodel.getLocation();
            Object[] attrs = new Object[6+((pt!=null)?4:0)];
            attrs[0] = NAME_ATTR;
            attrs[1] = n;
            attrs[2] = CLASS_ATTR;
            attrs[3] = cls;
            attrs[4] = SERIAL_ATTR;
            attrs[5] = i;
            if (pt != null) {
                attrs[6] = X_ATTR;
                attrs[7] = pt.x;
                attrs[8] = Y_ATTR;
                attrs[9] = pt.y;
            }
            writer.openBlock(RUBETTE, attrs);
            rmodel.toXML(writer);
            writer.closeBlock();
            i++;
        }
        for (RubetteModel rmodel : rubettes) {
            for (int j = 0; j < rmodel.getInLinkCount(); j++) {
                Link link = rmodel.getInLink(j);
                link.toXML(writer);
            }
        }
        for (NoteModel note : notes) {
            note.toXML(writer);
        }
        writer.closeBlock();
    }
    
    
    public static NetworkModel fromXML(XMLReader reader, Element networkElement) {
        String name = networkElement.getAttribute(NAME_ATTR).trim();
        if (name.length() == 0) {
            reader.setError(Messages.getString("NetworkModel.missingattr"), NETWORK, NAME_ATTR); //$NON-NLS-1$
            return null;
        }
        else {
            NetworkModel networkModel = new NetworkModel(name);
            RubetteManager manager = RubetteManager.getManager();
            HashMap<Integer,RubetteModel> rubetteModelMap = new HashMap<Integer,RubetteModel>();
            
            // read rubettes
            Element child = XMLReader.getChild(networkElement, RUBETTE);
            while (child != null) {
                String rubName = child.getAttribute(NAME_ATTR);
                String rubClass = child.getAttribute(CLASS_ATTR);
                int serial = XMLReader.getIntAttribute(child, SERIAL_ATTR, 0);
                int x = XMLReader.getIntAttribute(child, X_ATTR, 0);
                int y = XMLReader.getIntAttribute(child, Y_ATTR, 0);
                Rubette rubette = manager.getRubetteByClassName(rubClass);
                if (rubette == null) {
                    reader.setError(Messages.getString("NetworkModel.classnotavailable"), rubClass); //$NON-NLS-1$
                }
                else {
                    rubette = rubette.fromXML(reader, child);
                    if (rubette != null) {
                        RubetteModel rmodel = new RubetteModel(rubette, name);
                        rmodel.setName(rubName);
                        rmodel.setLocation(new Point(x, y));
                        rubetteModelMap.put(serial, rmodel);
                        networkModel.addRubette(rmodel);
                    }
                }
                child = XMLReader.getNextSibling(child, RUBETTE);
            }
            
            if (reader.hasError()) {
                return null;
            }

            // read links
            child = XMLReader.getChild(networkElement, LINK);
            while (child != null) {
                int src = XMLReader.getIntAttribute(child, SRC_ATTR, 0);
                int srcPos = XMLReader.getIntAttribute(child, SRCPOS_ATTR, 0);
                int dest = XMLReader.getIntAttribute(child, DEST_ATTR, 0);
                int destPos = XMLReader.getIntAttribute(child, DESTPOS_ATTR, 0);
                int type = XMLReader.getIntAttribute(child, TYPE_ATTR, 0);
                RubetteModel srcModel = rubetteModelMap.get(src);
                RubetteModel destModel = rubetteModelMap.get(dest);
                if (srcModel != null && destModel != null) {
                    Link link = new Link(srcModel, srcPos, destModel, destPos);
                    link.setType(type);
                    srcModel.addOutLink(link);
                    destModel.setInLink(link);
                }
                else {
                    reader.setError(Messages.getString("NetworkModel.cannotlink"), //$NON-NLS-1$
                                    srcModel == null?"unknown":srcModel.getName(), srcPos,
                                    destModel== null?"unknown":destModel.getName(), destPos);
                }
                child = XMLReader.getNextSibling(child, LINK);
            }

            // read notes
            child = XMLReader.getChild(networkElement, NOTE);
            while (child != null) {
                NoteModel noteModel = NoteModel.fromXML(reader, child);
                networkModel.addNote(noteModel);
                child = XMLReader.getNextSibling(child, NOTE);
            }
            
            return networkModel;
        }
    }
    
    
    public NetworkModel newInstance() {
        ArrayList<RubetteModel> newRubettes = new ArrayList<RubetteModel>(rubettes.size());
        for (int i = 0; i < rubettes.size(); i++) {
            RubetteModel rmodel = rubettes.get(i);
            rmodel.setSerial(i);
            newRubettes.add(rmodel.newInstance());
        }
        for (int i = 0; i < rubettes.size(); i++) {
            RubetteModel rmodel = rubettes.get(i);
            for (int j = 0; j < rmodel.getInLinkCount(); j++) {
                Link link = rmodel.getInLink(j);
                int src = link.getSrcModel().getSerial();
                RubetteModel srcModel = newRubettes.get(src);
                int srcPos = link.getSrcPos();
                int dest = link.getDestModel().getSerial();
                RubetteModel destModel = newRubettes.get(dest);
                int destPos = link.getDestPos();
                Link newLink = new Link(srcModel, srcPos, destModel, destPos);
                newLink.setType(link.getType());
                srcModel.addOutLink(newLink);
                destModel.setInLink(newLink);
            }
        }
        
        ArrayList<NoteModel> newNotes = new ArrayList<NoteModel>(notes.size());
        for (NoteModel nmodel : notes) {
            newNotes.add(nmodel.newInstance());
        }
        
        NetworkModel newModel = new NetworkModel(getName());
        newModel.rubettes = newRubettes;
        newModel.notes = newNotes;
        newModel.computeDependencyTree();
        return newModel;
    }
    
    
    public JNetwork createJNetwork(JComposer jcomposer) {
        JNetwork newNetwork = new JNetwork(jcomposer);
        newNetwork.setModel(this);
        jnetwork = newNetwork;
        return newNetwork;
    }
    
    
    public JNetwork createJMacroRubetteView(JComposer jComposer, JNetwork jNetwork) {
        JNetwork newNetwork = new JMacroRubetteView(jComposer, jNetwork);
        newNetwork.setModel(this);
        return newNetwork;
    }

    
    public String toString() {
        StringBuilder buf = new StringBuilder(30);
        buf.append("NetworkModel["); //$NON-NLS-1$
        buf.append(getName());
        for (RubetteModel rubette : rubettes) {
            buf.append(","); //$NON-NLS-1$
            buf.append(rubette);
        }
        buf.append("]"); //$NON-NLS-1$
        return buf.toString();
    }       
    
    
    private JNetwork  jnetwork;
    private String    name;
    private String    info = ""; //$NON-NLS-1$
    private ArrayList<RubetteModel> rubettes = new ArrayList<RubetteModel>();
    private ArrayList<RubetteModel> roots = new ArrayList<RubetteModel>();
    private ArrayList<RubetteModel> coroots = new ArrayList<RubetteModel>();
    private ArrayList<RubetteModel> dependents = new ArrayList<RubetteModel>(100);
    private ArrayList<RubetteModel> dependencies = new ArrayList<RubetteModel>(100);
    private ArrayList<NoteModel> notes = new ArrayList<NoteModel>();
}
