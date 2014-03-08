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

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.awt.event.*;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.*;

import org.rubato.base.Rubette;
import org.rubato.composer.*;
import org.rubato.composer.components.JMenuTitleItem;
import org.rubato.composer.dialogs.JNewMacroRubetteDialog;
import org.rubato.composer.notes.JNote;
import org.rubato.composer.notes.NoteModel;
import org.rubato.composer.preferences.UserPreferences;
import org.rubato.composer.rubette.*;
import org.rubato.rubettes.builtin.MacroInputRubette;
import org.rubato.rubettes.builtin.MacroOutputRubette;
import org.rubato.rubettes.builtin.MacroRubette;
import org.rubato.xml.XMLWriter;

public class JNetwork extends JLayeredPane
        implements MouseListener, MouseMotionListener, Scrollable {

    public JNetwork(JComposer jcomposer) {
        this.jcomposer = jcomposer;
        
        changeObservable.addObserver(jcomposer);
        
        setOpaque(true);
        setBackground(backgroundColor);
        setDoubleBuffered(true);
        setLayout(null);
        
        addMouseListener(this);
        addMouseMotionListener(this);

        getInputMap().put(KeyStroke.getKeyStroke("ctrl D"), "duplicate"); //$NON-NLS-1$ //$NON-NLS-2$
        getActionMap().put("duplicate", new AbstractAction () { //$NON-NLS-1$
            public void actionPerformed(ActionEvent e) {
                if (selected != null) {
                    selected.duplicate();
                }
            }
        });

        getInputMap().put(KeyStroke.getKeyStroke("DELETE"), "remove"); //$NON-NLS-1$ //$NON-NLS-2$
        getActionMap().put("remove", new AbstractAction () { //$NON-NLS-1$
            public void actionPerformed(ActionEvent e) {
                if (selected != null) {
                    selected.removeRubette();
                }
            }
        });

        getInputMap().put(KeyStroke.getKeyStroke("ctrl E"), "passthrough"); //$NON-NLS-1$ //$NON-NLS-2$
        getActionMap().put("passthrough", new AbstractAction () { //$NON-NLS-1$
            public void actionPerformed(ActionEvent e) {
                if (selected != null) {
                    selected.togglePassThrough();
                }
            }
        });

        getInputMap().put(KeyStroke.getKeyStroke("HOME"), "tofront"); //$NON-NLS-1$
        getActionMap().put("tofront", new AbstractAction () { //$NON-NLS-1$
            public void actionPerformed(ActionEvent e) {
                if (selected != null) {
                    selected.toFront();
                }
            }
        });

        getInputMap().put(KeyStroke.getKeyStroke("END"), "toback"); //$NON-NLS-1$
        getActionMap().put("toback", new AbstractAction () { //$NON-NLS-1$
            public void actionPerformed(ActionEvent e) {
                if (selected != null) {
                    selected.toBack();
                }
            }
        });

        setTransferHandler(new RubetteTransferHandler());

        dropTarget = new DropTarget(this, DnDConstants.ACTION_COPY, new DropTargetAdapter() {
            public void drop(DropTargetDropEvent dtde) {
                if (dtde.getTransferable().isDataFlavorSupported(RubetteTransferable.rubetteFlavor)) {
                    dtde.acceptDrop(dtde.getDropAction());
                    dtde.dropComplete(true);
                }
                else if (uri != null) {
                    dtde.dropComplete(true);
                    if (getJComposer().loseProject()) {
                        getJComposer().open(new File(uri));
                    }
                }
                else {
                    dtde.dropComplete(false);
                }
                rubette = null;
                jrubette = null;
                uri = null;
            }

            public void dragEnter(DropTargetDragEvent dtde) {
                Transferable transferable = dtde.getTransferable(); 
                if (transferable.isDataFlavorSupported(RubetteTransferable.rubetteFlavor)) {
                    try {
                        Rubette r = (Rubette)transferable.getTransferData(RubetteTransferable.rubetteFlavor);
                        if (r != rubette) {
                            rubette = r;
                            jrubette = getJComposer().getJRubetteList().createJRubette(rubette);
                        }
                        addRubette(jrubette, dtde.getLocation());
                    }
                    catch (Exception e) {
                        dtde.rejectDrag();
                    }
                }
                else if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                    try {
                        String s = (String)transferable.getTransferData(DataFlavor.stringFlavor);
                        uri = new URI(s.trim());
                        if (!(uri.getScheme().equals("file") && uri.getPath().endsWith(".rbo"))) {
                            uri = null;
                            dtde.rejectDrag();
                        }
                    }
                    catch (Exception e) {
                        dtde.rejectDrag();
                    }
                }
                else {
                    dtde.rejectDrag();
                }
            }
            
            public void dragExit(DropTargetEvent dte) {
                if (jrubette != null) {
                    removeRubette(jrubette);
                }
                uri = null;
            }
            
            public void dragOver(DropTargetDragEvent dtde) {
                if (jrubette != null) {
                    jrubette.moveJRubette(dtde.getLocation());
                }
            }            
            
            private JRubette jrubette = null;
            private Rubette  rubette  = null;
            private URI      uri      = null;
        });
    }
    
    
    public void setModel(NetworkModel model) {
        dispose();
        this.model = model;
        ArrayList<RubetteModel> rlist = model.getRubettes();
        for (RubetteModel rmodel : rlist) {
            JRubette jrubette = new JRubette(rmodel);
            rmodel.setJRubette(jrubette);
            jrubette.addMouseListener(this);
            rubettes.add(0, jrubette);
        }
        for (RubetteModel rmodel : rlist) {
            for (JLink jlink : rmodel.getJRubette().makeLinks()) {
                addLink(jlink);
            }
        }
        for (NoteModel nmodel : model.getNotes()) {
            JNote jnote = nmodel.createJNote();
            notes.add(jnote);
        }
        revalidate();
        refresh();
    }


    public NetworkModel getModel() {
        return model;
    }

    
    public JComposer getJComposer() {
        return jcomposer;
    }
    
    
    public String getName() {
        return model.getName();
    }
    
    
    public boolean isRunning() {
        return jcomposer.isRunning();
    }
    
    
    public void dispose() {
        LinkedList<JRubette> list = new LinkedList<JRubette>(rubettes);
        for (JRubette rubette : list) {
            rubette.removeRubette();
        }
    }
    
    
    public boolean isEmpty() {
        return notes.isEmpty() && rubettes.isEmpty();
    }
    
    
    public NetworkClip copy() {
        if (selection == null) {
            return null;
        }
        else {
            return new NetworkClip(selection);
        }
    }
    
    
    public void paste(NetworkClip clip) {
        clip.paste(this);
    }
    
    
    //
    // Rubettes
    //
    
    public boolean canAdd(JRubette jrubette) {
        return true;
    }
    

    public void addRubette(JRubette jrubette) {
        Point position = null;
        addRubette(jrubette, position);
    }


    public void addRubette(JRubette jrubette, Point position) {
        if (!canAdd(jrubette)) {
            return;
        }
        jrubette.addMouseListener(this);
        // put the new rubette on top of all others
        rubettes.add(0, jrubette);
        model.addRubette(jrubette.getModel());
        if (position != null) { jrubette.moveJRubette(position); }
        setChanged();
        refresh();
        jcomposer.setStatusInfo(Messages.getString("JNetwork.rubetteadded"), jrubette.getRubetteName()); //$NON-NLS-1$
    }


    public void removeRubette(JRubette jrubette) {
        for (JLink jlink : jrubette.getLinks()) {
            removeLink(jlink);
        }
        
        rubettes.remove(jrubette);
        model.removeRubette(jrubette.getModel());
        if (jrubette.getRubette() instanceof MacroRubette) {
            MacroRubette nrubette = (MacroRubette)jrubette.getRubette();
            jcomposer.removeJNetworkForModel(nrubette.getNetworkModel());
        }
        jcomposer.removeProblemsFor(jrubette);
        jcomposer.setStatusInfo(Messages.getString("JNetwork.rubetteremoved"), jrubette.getModel().getName()); //$NON-NLS-1$
        setChanged();
        clearSelection();
        refresh();
    }

    
    public void toFront(JRubette jrubette) {
        rubettes.remove(jrubette);
        rubettes.add(0, jrubette);
        setChanged();
        refresh();
    }
    
    
    public void toBack(JRubette jrubette) {
        rubettes.remove(jrubette);
        rubettes.add(rubettes.size(), jrubette);
        setChanged();
        refresh();
    }
    
    
    public ArrayList<JRubette> getJRubettes() {
        return rubettes;
    }
    
    //
    // Links
    //
    
    public void addLink(JLink jlink) {
        links.add(jlink);
        setChanged();
        refresh();
    }


    public void setDragLink(JLink link) {
        dragLink = link;
        refresh();
    }


    public void finishDragLink(Point point) {
        dragLink = null;
        Connector destConnector = null;
        Iterator<JRubette> iter = rubettes.iterator();
        while (iter.hasNext() && destConnector == null) {
            destConnector = iter.next().getConnector(point);
        }
        if (srcConnector != null && destConnector != null) {
            makeLink(srcConnector, destConnector);
        }
        srcConnector = null;
        destConnector = null;
        refresh();
    }


    public boolean linkDragging() {
        return dragLink != null;
    }


    public void setSrcConnector(Connector src) {
        srcConnector = src;
    }


    public void makeLink(Connector src, Connector dest) {
        if (src.getType() != dest.getType()) {
            if (src.getType() == Connector.INPUT) {
                Connector tmp = src;
                src = dest;
                dest = tmp;
            }
            makeLink(src.getJRubette(), src.getPos(), dest.getJRubette(), dest.getPos());
        }
        else {
            String s = (src.getType() == Connector.INPUT)?"input":"output"; //$NON-NLS-1$ //$NON-NLS-2$
            jcomposer.setStatusError(Messages.getString("JNetwork.cannotlink"), s); //$NON-NLS-1$
        }
    }


    protected void makeLink(JRubette src, int srcPos, JRubette dest, int destPos) {
        JLink jlink = new JLink(new Link(src.getModel(), srcPos, dest.getModel(), destPos));
        jlink.setType(UserPreferences.getUserPreferences().getLinkType());        
        jlink.setSrc(src, srcPos);
        jlink.setDest(dest, destPos);
        if (src.canLink(src, srcPos, dest, destPos) &&
            dest.canLink(src, srcPos, dest, destPos)) {
            src.addOutLink(jlink, srcPos);
            dest.setInLink(jlink, destPos);
            addLink(jlink);
            jcomposer.setStatusInfo(Messages.getString("JNetwork.linkadded"), //$NON-NLS-1$
                                    src, srcPos, dest, destPos);
        }
        else {
            jcomposer.setStatusError(Messages.getString("JNetwork.cannotmakelink"), //$NON-NLS-1$
                                     src, srcPos, dest, destPos);
        }
    }


    public void removeLink(JLink link) {
        link.detach();
        links.remove(link);
        jcomposer.setStatusInfo(Messages.getString("JNetwork.linkremoved"), //$NON-NLS-1$
                                link.getSrc(), link.getSrcPos(), link.getDest(), link.getDestPos());
        setChanged();
        refresh();
    }


    public JLink getLinkAt(Point point) {
        for (JLink jlink : links) {
            if (jlink.isNear(point)) {
                return jlink;
            }
        }
        return null;
    }

    
    public void highlight(JRubette jrubette, boolean b) {
        if (b == false) {
            highlighted = null;
            jrubette.highlight(false);
        }
        else if (jrubette == highlighted) {
            return;
        }
        else {
            if (highlighted != null) {
                highlighted.highlight(false);
            }
            highlighted = jrubette;
            jrubette.highlight(true);
        }
    }
    
    
    public void setSelected(JRubette jrubette) {
        selected = jrubette;
    }
    

    public void computeDependencyTree() {
        model.computeDependencyTree();
    }
    
    
    public void refresh() {
        refreshLayout();
        computeDependencyTree();
    }

    
    protected void refreshLayout() {
        int maxWidth = 0;
        int maxHeight = 0;
        
        removeAll();
        
        if (dragLink != null) {
            add(dragLink);
        }
        
        int i = 0;
        
        for (JLink jlink : links) {
            add(jlink, i);
        }
        
        for (JNote jnote : notes) {
            int x = jnote.getX()+jnote.getWidth();
            int y = jnote.getY()+jnote.getHeight();
            if (x > maxWidth) maxWidth = x;
            if (y > maxHeight) maxHeight = y;
            add(jnote, i++);
        }
        
        for (JRubette jrubette : rubettes) {
            int x = jrubette.getX()+jrubette.getWidth();
            int y = jrubette.getY()+jrubette.getHeight();
            if (x > maxWidth) maxWidth = x;
            if (y > maxHeight) maxHeight = y;
            add(jrubette, i++);
        }
        
        changeSize(maxWidth, maxHeight);
        
        if (getParent() instanceof JViewport) {
            getJViewport().revalidate();
        }
        repaint();        
    }
    

    public void mouseClicked(MouseEvent e) {
        if (!isRunning()) { 
            if (!e.isConsumed()) {
                clearSelection();
            }
        }
    }


    public void mousePressed(MouseEvent e) {
        if (isRunning()) { return; }
        if (!e.isConsumed()) {
            if (e.getButton() == MouseEvent.BUTTON3 || e.isControlDown()) { //control-click for mac...
            	// open popup menu
                JLink jlink = getLinkAt(e.getPoint());
                if (jlink != null) {
                    JPopupMenu popup = getLinkPopup(jlink);
                    popup.show(this, e.getX(), e.getY());
                }
                else {
                    JPopupMenu popup = getNetworkPopup(e.getX(), e.getY());
                    popup.show(this, e.getX(), e.getY());                    
                }
            }
            else if (e.getButton() == MouseEvent.BUTTON2) {
                // pan
                panning = true;
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                savedViewPos.x = getJViewport().getViewPosition().x;
                savedViewPos.y = getJViewport().getViewPosition().y;
                e.translatePoint(-savedViewPos.x, -savedViewPos.y);
                savedX = e.getX();
                savedY = e.getY();
            }
            else if (e.getButton() == MouseEvent.BUTTON1) {
                // make selection
                selecting = true;
                setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                savedX = e.getX();
                savedY = e.getY();
                selectionRectangle = new JSelectionRectangle();
                add(selectionRectangle);
                selectionRectangle.set(savedX, savedY, savedX, savedY);
            }
        }
    }

    
    public void mouseReleased(MouseEvent e) {
        if (panning) {
            panning = false;
            setCursor(Cursor.getDefaultCursor());
        }
        if (selecting) {
            selecting = false;
            setCursor(Cursor.getDefaultCursor());
            remove(selectionRectangle);
            selection = new ArrayList<JRubette>();
            for (JRubette r : rubettes) {
                if (selectionRectangle.intersects(r)) {
                    selection.add(r);
                }
            }
            selectionRectangle = null;
            repaint();
        }
    }

    
    public void mouseMoved(MouseEvent e) {}
    

    public void mouseEntered(MouseEvent e) {
        requestFocusInWindow();
    }


    public void mouseExited(MouseEvent e) {}


    public void mouseDragged(MouseEvent e) {
        if (panning) {
            int x = getJViewport().getViewPosition().x;
            int y = getJViewport().getViewPosition().y;
            e.translatePoint(-x, -y);
            pan(savedViewPos.x+savedX-e.getX(), savedViewPos.y+savedY-e.getY());
        }
        if (selecting) {
            selectionRectangle.set(savedX, savedY, e.getX(), e.getY());
            for (JRubette r : rubettes) {
                r.setInSelection(selectionRectangle.intersects(r));
            }
        }
    }

    
    public Dimension getPreferredSize() {
        return currentSize;
    }


    public Dimension getMinimumSize() {
        return MINIMUM_SIZE;
    }


    public boolean getScrollableTracksViewportHeight() {
        return false;
    }


    public boolean getScrollableTracksViewportWidth() {
        return false;
    }


    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 10;
    }


    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 10;
    }


    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }


    public void changeSize(int width, int height) {
        if (width == getWidth() && height == getHeight()) {
            return;
        }
        if (width < PREFERRED_SIZE.width) {
            width = PREFERRED_SIZE.width;
        }
        if (height < PREFERRED_SIZE.height) {
            height = PREFERRED_SIZE.height;
        }
        currentSize.width = width;
        currentSize.height = height;
    }


    private JPopupMenu getLinkPopup(final JLink jlink) {
        JPopupMenu popup = new JPopupMenu();
        JMenuItem item = new JMenuItem(Messages.getString("JNetwork.removelink")); //$NON-NLS-1$
        item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    removeLink(jlink);
                }
            });
        popup.add(item);
        item = new JMenuItem(Messages.getString("JNetwork.diagonal")); //$NON-NLS-1$
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jlink.setType(JLink.LINE);
            }
        });        
        popup.add(item);
        item = new JMenuItem(Messages.getString("JNetwork.zigzag")); //$NON-NLS-1$
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jlink.setType(JLink.ZIGZAG);
            }
        });        
        popup.add(item);
        item = new JMenuItem(Messages.getString("JNetwork.curved")); //$NON-NLS-1$
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jlink.setType(JLink.CURVE);
            }
        });        
        popup.add(item);
        return popup;
    }


    protected JPopupMenu getNetworkPopup(final int x, final int y) {
        final JPopupMenu popup = new JPopupMenu();
        JMenuItem item;
        item = new JMenuTitleItem(model.getName());
        popup.add(item);
        popup.addSeparator();
        item = new JMenuItem(Messages.getString("JNetwork.rename")); //$NON-NLS-1$
        item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    renameNetwork();
                }
            });
        popup.add(item);
        item = new JMenuItem(Messages.getString("JNetwork.discard")); //$NON-NLS-1$
        item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    removeNetwork();
                }
            });
        popup.add(item);
        item = new JMenuItem(Messages.getString("JNetwork.createnote")); //$NON-NLS-1$
        item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    createNote(x, y);
                }
            });
        popup.add(item);
        item = new JMenuItem(Messages.getString("JNetwork.createfromnet")); //$NON-NLS-1$
        item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    createRubette();
                }
            });
        popup.add(item);
        return popup;
    }
    
    
    protected void removeNetwork() {
        jcomposer.removeJNetwork(this);        
    }
    
    
    protected void renameNetwork() {
        Frame frame = JOptionPane.getFrameForComponent(this);
        String s = JOptionPane.showInputDialog(frame, ENTERNAME_MSG);
        if (s != null) {
            s = s.trim();
            if (s.length() > 0 && s.length() <= 32) {
                jcomposer.renameJNetwork(this, s);
                for (JRubette rubette : rubettes) {
                    rubette.refresh();
                }
                return;
            }
            else {
                jcomposer.showErrorDialog(NAMEOFNETWORK_ERROR);
            }
        }
    }
    
    
    protected void createNote(final int x, final int y) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JNote jnote = new JNote(x, y);
                notes.add(jnote);
                model.addNote(jnote.getModel());
                setChanged();
                refreshLayout();
            }
        });
    }
    
    
    public void removeNote(JNote jnote) {
        model.removeNote(jnote.getModel());
        notes.remove(jnote);
        setChanged();
        refreshLayout();
    }
    
    
    private void pan(int x, int y) {
        if (x < 0) { x = 0; }
        if (y < 0) { y = 0; }
        newViewPos.x = x;
        newViewPos.y = y;
        getJViewport().setViewPosition(newViewPos);
    }
    
    
    private JViewport getJViewport() {
        return (JViewport)getParent();
    }
    
    
    /**
     * Creates a new MacroRubette from this JNetwork.
     */
    protected void createRubette() {
        Frame frame = JOptionPane.getFrameForComponent(this);
        MacroRubette rubette = new MacroRubette();
        NetworkModel newNetworkModel = model.newInstance();
            
        // check network
        ArrayList<RubetteModel> rbts = newNetworkModel.getRubettes();
        int inCount = 0;
        int outCount = 0;
        for (RubetteModel rmodel : rbts) {
            Rubette arubette = rmodel.getRubette();
            if (arubette instanceof MacroInputRubette) {
                inCount++;
            }
            else if (arubette instanceof MacroOutputRubette) {
                outCount++;
            }
        }
        if (inCount > 1) {
            jcomposer.showErrorDialog(Messages.getString("JNetwork.inrubetteerror")); //$NON-NLS-1$
            return;
        }
        if (outCount > 1) {
            jcomposer.showErrorDialog(Messages.getString("JNetwork.outrubetteerror")); //$NON-NLS-1$
            return;
        }

        JNewMacroRubetteDialog dialog = new JNewMacroRubetteDialog(frame);
        dialog.setLocation(frame.getX()+frame.getWidth()/2, frame.getY()+frame.getHeight()/2);
        dialog.setVisible(true);        
        if (dialog.isOk()) {
            String name = dialog.getName().trim();
            if (name.length() == 0) {
                jcomposer.showErrorDialog(NAMENOTEMPTY_ERROR);
            }
            else if (jcomposer.getRubetteManager().hasRubetteByName(name)) {
                jcomposer.showErrorDialog(NAMEALREADYEXISTS_ERROR, name);
            }
            else {
                newNetworkModel.setName(name);
                rubette.setNetworkModel(newNetworkModel);
                rubette.setName(name);
                rubette.setInfo(dialog.getInfo());
                rubette.setShortDescription(dialog.getShortDescription());
                rubette.setLongDescription(dialog.getLongDescription());
                jcomposer.getRubetteManager().addRubette(rubette);
            }
            setChanged();
        }
        dialog.dispose();
    }
    
    
    public void setChanged() {
        changeObservable.change();
    }

    
    public void toXML(XMLWriter writer) {
        model.toXML(writer);
    }
    
    
    public String toString() {
        return model.getName();
    }

    
    //
    // Selections
    //
    
    public void clearSelection() {
        for (JRubette r : rubettes) {
            r.setInSelection(false);
        }
        selection = null;
        selectionRectangle = null;
    }
    
    
    public void cutSelection() {
        if (selection != null) {
            for (JRubette r : selection) {
                removeRubette(r);
            }
        }
    }
    
    
    public ArrayList<JRubette> getSelection() {
        return selection;
    }

    
    public void toggleSelection(JRubette rubette) {
        if (selection == null) {
            selection = new ArrayList<JRubette>();
        }
        if (selection.remove(rubette)) {
            rubette.setInSelection(false);
        }
        else {
            selection.add(rubette);
            rubette.setInSelection(true);
        }
    }
    
    
    public class JSelectionRectangle extends JPanel {
        
        public void set(int x0, int y0, int x1, int y1) {
            rect.x      = (x0 < x1)?x0:x1;
            rect.y      = (y0 < y1)?y0:y1;
            rect.width  = Math.abs(x1-x0)+1;
            rect.height = Math.abs(y1-y0)+1;
            setBounds(rect);
        }
        
        public boolean intersects(JRubette r) {
            return rect.intersects(r.getBounds());
        }
        
        protected void paintComponent(Graphics g) {
            g.setColor(fillColor);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(lineColor);
            g.drawRect(0, 0, getWidth()-1, getHeight()-1);
        }
        
        private Rectangle rect = new Rectangle();
        
        private Color fillColor = new Color(255, 250, 205);        
        private Color lineColor = new Color(205, 201, 165);        
    }
    

    protected NetworkModel      model        = null;
    // Link back to the containing JComposer
    private JComposer           jcomposer    = null;
    private ArrayList<JRubette> rubettes     = new ArrayList<JRubette>();
    private ArrayList<JLink>    links        = new ArrayList<JLink>();
    private JLink               dragLink     = null;
    private Connector           srcConnector = null;
    protected ArrayList<JNote>  notes        = new ArrayList<JNote>();

    private   JRubette highlighted = null;
    protected JRubette selected    = null;
    
    private boolean       panning = false;
    private boolean       selecting = false;
    private int           savedX, savedY;
    private Point         savedViewPos = new Point();
    private Point         newViewPos   = new Point();
    
    private JSelectionRectangle selectionRectangle = null;
    private ArrayList<JRubette> selection = null;
    
    @SuppressWarnings("unused")
    private DropTarget dropTarget;
    private ChangeObservable changeObservable = new ChangeObservable();

    private final static Color backgroundColor = Color.WHITE;

    private final static Dimension PREFERRED_SIZE = new Dimension(1500, 1500);
    private final static Dimension MINIMUM_SIZE = new Dimension(200, 200);

    private Dimension currentSize = PREFERRED_SIZE;
    
    private static final String NAMENOTEMPTY_ERROR      = Messages.getString("JNetwork.namenotempty"); //$NON-NLS-1$
    private static final String NAMEALREADYEXISTS_ERROR = Messages.getString("JNetwork.alreadyexists"); //$NON-NLS-1$
    private static final String NAMEOFNETWORK_ERROR     = Messages.getString("JNetwork.nameofnetwork"); //$NON-NLS-1$
    private static final String ENTERNAME_MSG           = Messages.getString("JNetwork.entername"); //$NON-NLS-1$
}