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

import static org.rubato.composer.Utilities.installEscapeKey;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.border.Border;

import org.rubato.base.Rubette;
import org.rubato.composer.JComposer;
import org.rubato.composer.JRubetteList;
import org.rubato.composer.components.JMenuTitleItem;
import org.rubato.composer.network.JNetwork;
import org.rubato.composer.network.JMacroRubetteView;
import org.rubato.composer.network.NetworkModel;
import org.rubato.composer.preferences.UserPreferences;
import org.rubato.rubettes.builtin.MacroInputRubette;
import org.rubato.rubettes.builtin.MacroOutputRubette;
import org.rubato.rubettes.builtin.MacroRubette;


public final class JRubette extends JPanel
        implements MouseListener, MouseMotionListener {


    public JRubette(int x, int y, Rubette rubette, String name) {
        this(x, y, new RubetteModel(rubette, name), name);        
    }
    
    
    public JRubette(RubetteModel rubetteModel) {
        this(rubetteModel.getLocation().x, rubetteModel.getLocation().y, rubetteModel, rubetteModel.getName());
    }
    
    
    public JRubette(int x, int y, RubetteModel rubetteModel, String name) {
        model = rubetteModel;
        rubetteModel.setJRubette(this);

        setLayout(null);
        setOpaque(false);

        int wy = MARGIN_TOP+5;

        if (model.getIcon() == null) {
            nameLabel = new JLabel(model.getName(), SwingConstants.CENTER);
        }
        else {
            nameLabel = new JLabel(model.getName(), model.getIcon(), SwingConstants.CENTER);            
        }
        nameLabel.setBounds(MARGIN_LEFT+5, wy, size.width-MARGIN_RIGHT-MARGIN_LEFT-10, nameLabel.getPreferredSize().height);
        add(nameLabel);

        wy += nameLabel.getHeight()+5;

        if (model.hasInfo()) {
            infoLabel = new JLabel("", SwingConstants.CENTER); //$NON-NLS-1$
            infoLabel.setText(getInfo());
            infoLabel.setBounds(MARGIN_LEFT+5, wy, size.width-MARGIN_RIGHT-MARGIN_LEFT-10, infoLabel.getPreferredSize().height);
            add(infoLabel);
            wy += infoLabel.getHeight()+5;
        }

        if (model.hasProperties()) {
            JButton propertiesButton = new JButton(Messages.getString("JRubette.properties")); //$NON-NLS-1$
            propertiesButton.setToolTipText(Messages.getString("JRubette.propertiestooltip")); //$NON-NLS-1$
            propertiesButton.setBounds(MARGIN_LEFT+5, wy, size.width-MARGIN_RIGHT-MARGIN_LEFT-10, propertiesButton.getPreferredSize().height);
            propertiesButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    openProperties();
                }
            });
            add(propertiesButton);
            wy += propertiesButton.getHeight()+5;
        }

        if (model.hasView()) {
            JButton viewButton = new JButton(Messages.getString("JRubette.view")); //$NON-NLS-1$
            viewButton.setToolTipText(Messages.getString("JRubette.viewtooltip")); //$NON-NLS-1$
            viewButton.setBounds(MARGIN_LEFT+5, wy, size.width-MARGIN_RIGHT-MARGIN_LEFT-10, viewButton.getPreferredSize().height);
            viewButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    openView();
                }
            });
            add(viewButton);
            wy += viewButton.getHeight()+5;
        }
        
        if (model.getRubette() instanceof MacroRubette) {
            JButton openButton = new JButton(Messages.getString("JRubette.open")); //$NON-NLS-1$
            openButton.setToolTipText(Messages.getString("JRubette.opentooltip")); //$NON-NLS-1$
            openButton.setBounds(MARGIN_LEFT+5, wy, size.width-MARGIN_RIGHT-MARGIN_LEFT-10, openButton.getPreferredSize().height);
            openButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    openNetwork();
                }
            });
            add(openButton);
            wy += openButton.getHeight()+5;            
        }

        size.height = wy+MARGIN_BOTTOM;

        setBounds(x, y, size.width, size.height);

        setInConnectors(model.getInCount());
        setOutConnectors(model.getOutCount());

        addMouseListener(this);
        addMouseMotionListener(this);

        setToolTipText(""); //$NON-NLS-1$
    }


    public RubetteModel getModel() {
        return model;
    }

    
    public Rubette getRubette() {
        return model.getRubette();
    }

    
    public String getRubetteName() {
        return model.getName();
    }
    
    
    protected boolean isRunning() {
        return getJNetwork().isRunning();
    }
    

    protected void openProperties() {
        if (!canChangeProperties()) {
            return;
        }
        if (propertiesDialog == null) {
            JComponent comp = model.getRubette().getProperties();
            Frame frame = JOptionPane.getFrameForComponent(this);
            propertiesDialog = getPropertiesFrame(frame, comp);
            propertiesDialog.setLocationRelativeTo(this);
        }
        propertiesDialog.setVisible(true);
        refresh();
    }


    protected void openView() {
        if (viewDialog == null) {
            JComponent comp = model.getRubette().getView();
            Frame frame = JOptionPane.getFrameForComponent(this);
            viewDialog = getViewFrame(frame, comp);
            viewDialog.setLocationRelativeTo(this);
        }
        viewDialog.setVisible(true);
        viewDialog.pack();
        refresh();
    }
    
    
    private JDialog getViewFrame(Frame frame, JComponent comp) {
        final JDialog dialog = new JDialog(frame);
        dialog.setLayout(new BorderLayout());
        comp.setBorder(emptyBorder);
        dialog.add(comp, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 1, 5, 5));
        buttonPanel.setBorder(emptyBorder);
        
        JButton hideButton = new JButton(Messages.getString("JRubette.hide")); //$NON-NLS-1$
        hideButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.setVisible(false);
            } 
        });
        hideButton.setToolTipText("Hide view window");
        buttonPanel.add(hideButton);
        
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.pack();
        dialog.setLocationRelativeTo(comp);
        return dialog;
    }


    private JDialog getPropertiesFrame(Frame frame, JComponent comp) {
        final JDialog dialog = new JDialog(frame);
        dialog.setLayout(new BorderLayout());
        comp.setBorder(emptyBorder);
        dialog.add(comp, BorderLayout.CENTER);
        
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(1,3,5,5));
        buttonsPanel.setBorder(emptyBorder);
        
        JButton hideButton = new JButton(Messages.getString("JRubette.hide")); //$NON-NLS-1$
        hideButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.setVisible(false);
            } 
        });
        hideButton.setToolTipText("Hide properties window");
        buttonsPanel.add(hideButton);
        
        JButton revertButton = new JButton(Messages.getString("JRubette.revert")); //$NON-NLS-1$
        revertButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                model.getRubette().revertProperties();
            } 
        });
        revertButton.setToolTipText("Revert properties to previous values");
        buttonsPanel.add(revertButton);
        
        JButton applyButton = new JButton(Messages.getString("JRubette.apply")); //$NON-NLS-1$
        applyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                applyProperties(false);
            } 
        });
        applyButton.setToolTipText("Apply properties");
        buttonsPanel.add(applyButton);
        
        JButton applyAndHideButton = new JButton(Messages.getString("JRubette.applyandhide")); //$NON-NLS-1$
        applyAndHideButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                applyProperties(true);
            } 
        });
        applyAndHideButton.setToolTipText("Apply properties and hide window");
        buttonsPanel.add(applyAndHideButton);

        dialog.getRootPane().setDefaultButton(applyAndHideButton);
        
        dialog.add(buttonsPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(comp);

        installEscapeKey(dialog);
        
        return dialog;
    }

    
    private boolean canChangeProperties() {
        if (getJNetwork() instanceof JMacroRubetteView) {
            Rubette rubette = getModel().getRubette();
            if (rubette instanceof MacroInputRubette) {
                getJNetwork().getJComposer().showErrorDialog(Messages.getString("JRubette.cannotchangeinprop")); //$NON-NLS-1$
                return false;
            }
            if (rubette instanceof MacroOutputRubette) {
                getJNetwork().getJComposer().showErrorDialog(Messages.getString("JRubette.cannotchangeoutprop"));  //$NON-NLS-1$
                return false;
            }
        }
        return true;
    }

    
    private void resizeInConnectors(int n) {
        Connector[] newInConnectors = new Connector[n];
        if (n > inConnectorCount) {
            for (int i = 0; i < inConnectorCount; i++) {
                newInConnectors[i] = inConnectors[i];
            }
            for (int i = inConnectorCount; i < n; i++) {
                newInConnectors[i] = new Connector(this, i, null, Connector.UNCONNECTED, Connector.INPUT);
            }
        }
        else if (n < inConnectorCount) {
            for (int i = 0; i < n; i++) {
                newInConnectors[i] = inConnectors[i];
            }
            for (int i = n; i < inConnectorCount; i++) {
                List<JLink> linkList = inConnectors[i].getLinks();
                JLink[] links = new JLink[linkList.size()];
                for (int j = 0; j < links.length; j++) {
                    links[j] = linkList.get(j);
                }
                for (int j = 0; j < links.length; j++) {
                    getJNetwork().removeLink(links[j]);
                }
            }
        }
        inConnectorCount = n;
        inConnectors = newInConnectors;
        if (inConnectorCount > 0) {
            int inConnWidth = getWidth()/inConnectorCount;
            for (int i = 0; i < inConnectorCount; i++) {
                Rectangle r = new Rectangle(i*inConnWidth-CONNECTOR_WIDTH/2+inConnWidth/2,
                                            0,
                                            CONNECTOR_WIDTH,
                                            MARGIN_TOP);
                inConnectors[i].setRectangle(r);
                inConnectors[i].refresh();
            }
        }
        model.resizeInputs();
    }


    private void resizeOutConnectors(int n) {
        Connector[] newOutConnectors = new Connector[n];
        if (n > outConnectorCount) {
            for (int i = 0; i < outConnectorCount; i++) {
                newOutConnectors[i] = outConnectors[i];
            }
            for (int i = outConnectorCount; i < n; i++) {
                newOutConnectors[i] = new Connector(this, i, null, Connector.UNCONNECTED, Connector.OUTPUT);
            }
        }
        else if (n < outConnectorCount) {
            for (int i = 0; i < n; i++) {
                newOutConnectors[i] = outConnectors[i];
            }
            for (int i = n; i < outConnectorCount; i++) {
                List<JLink> linkList = outConnectors[i].getLinks();
                JLink[] links = new JLink[linkList.size()];
                for (int j = 0; j < links.length; j++) {
                    links[j] = linkList.get(j);
                }
                for (int j = 0; j < links.length; j++) {
                    getJNetwork().removeLink(links[j]);
                }
            }
        }
        outConnectorCount = n;
        outConnectors = newOutConnectors;
        if (outConnectorCount > 0) {
            int outConnWidth = getWidth()/outConnectorCount;
            for (int i = 0; i < outConnectorCount; i++) {
                Rectangle r = new Rectangle(i*outConnWidth-CONNECTOR_WIDTH/2+outConnWidth/2,
                                            getHeight()-MARGIN_BOTTOM-1,
                                            CONNECTOR_WIDTH,
                                            MARGIN_BOTTOM);
                outConnectors[i].setRectangle(r);
                outConnectors[i].refresh();
            }
        }
    }

    
    private void setInConnectors(int n) {
        inConnectorCount = n;
        inConnectors = new Connector[n];
        if (n > 0) {
            int inConnWidth = getWidth()/inConnectorCount;
            for (int i = 0; i < inConnectorCount; i++) {
                Rectangle r = new Rectangle(i*inConnWidth-CONNECTOR_WIDTH/2+inConnWidth/2,
                                            0,
                                            CONNECTOR_WIDTH,
                                            MARGIN_TOP);
                inConnectors[i] = new Connector(this, i, r, Connector.UNCONNECTED, Connector.INPUT);
            }
        }
        if (n == 0) {
            getModel().setPassThrough(false);
            repaint();
        }
    }


    private void setOutConnectors(int n) {
        outConnectorCount = n;
        outConnectors = new Connector[n];
        if (n > 0) {
            int outConnWidth = getWidth()/outConnectorCount;
            for (int j = 0; j < outConnectorCount; j++) {
                Rectangle r = new Rectangle(j*outConnWidth-CONNECTOR_WIDTH/2+outConnWidth/2,
                                            getHeight()-MARGIN_BOTTOM-1,
                                            CONNECTOR_WIDTH,
                                            MARGIN_BOTTOM);
                outConnectors[j] = new Connector(this, j, r, Connector.UNCONNECTED, Connector.OUTPUT);
            }
        }
        if (n == 0) {
            getModel().setPassThrough(false);
            repaint();
        }
    }


    protected Connector inConnector(int x, int y) {
        for (int i = 0; i < inConnectorCount; i++) {
            if (inConnectors[i].contains(x, y)) {
                return inConnectors[i];
            }
        }
        for (int i = 0; i < outConnectorCount; i++) {
            if (outConnectors[i].contains(x, y)) {
                return outConnectors[i];
            }
        }
        return null;
    }


    public Connector getConnector(Point point) {
        return inConnector(point.x-getX(), point.y-getY());
    }


    public void setInLink(JLink link, int i) {
        inConnectors[i].addLink(link);
        model.setInLink(link.getLink());
    }


    public void addOutLink(JLink link, int i) {
        outConnectors[i].addLink(link);
        model.addOutLink(link.getLink());
        runPartially();
    }


    public void removeLink(JLink link) {
        for (int i = 0; i < inConnectorCount; i++) {
            inConnectors[i].removeLink(link);
        }
        for (int i = 0; i < outConnectorCount; i++) {
            outConnectors[i].removeLink(link);
        }
    }
    
    
    public List<JLink> makeLinks() {
        LinkedList<JLink> jlinkList = new LinkedList<JLink>();
        for (int i = 0; i < model.getInLinkCount(); i++) {
            Link link = model.getInLink(i);
            JRubette src = link.getSrcModel().getJRubette();
            int srcPos = link.getSrcPos();
            int destPos = link.getDestPos();
            JLink jlink = new JLink(link);
            jlink.setSrc(src, srcPos);
            jlink.setDest(this, destPos);
            src.outConnectors[srcPos].addLink(jlink);
            inConnectors[destPos].addLink(jlink);
            jlinkList.add(jlink);
        }
        return jlinkList;
    }
    
    
    public List<JLink> getLinks() {
        LinkedList<JLink> list = new LinkedList<JLink>();
        for (int i = 0; i < inConnectorCount; i++) {
            list.addAll(inConnectors[i].getLinks());
        }
        for (int i = 0; i < outConnectorCount; i++) {
            list.addAll(outConnectors[i].getLinks());
        }
        return list;
    }
    

    public boolean canLink(JRubette src, int srcPos, JRubette dest, int destPos) {
        if (src == dest) {
            return false;
        }
        else if (this == src) {
            return true;
        }
        else if (this == dest) {
            if (inConnectors[destPos].getLinkCount() > 0) {
                return false;
            }
            else {
                return true;
            }
        }
        return true;

    }


    public void moveJRubette(int x, int y) {
        setBounds(x, y, size.width, size.height);
        for (int i = 0; i < inConnectorCount; i++) {
            inConnectors[i].refresh();
        }
        for (int i = 0; i < outConnectorCount; i++) {
            outConnectors[i].refresh();
        }
    }

    
    public void moveRelative(int dx, int dy) {
        moveJRubette(getX()+dx, getY()+dy);
    }
    

    public void moveJRubette(Point pt) {
        moveJRubette(pt.x, pt.y);
    }

    
    public void highlight(boolean b) {
        if (highlighted != b) {
            highlighted = b;
            refresh();
        }
    }
    
    
    public void setInSelection(boolean b) {
        if (inselection != b) {
            inselection = b;
            refresh();
        }
    }
    
    
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;
        g2d.setColor(Color.BLACK);

        for (int i = 0; i < inConnectorCount; i++) {
            inConnectors[i].paint(g2d);
        }
        for (int j = 0; j < outConnectorCount; j++) {
            outConnectors[j].paint(g2d);
        }
        
        // background color
        g2d.setColor(getModel().isPassThrough()?passthroughColor:backgroundColor);
        g2d.setColor(inselection?inselectionColor:g2d.getColor());
        int x = MARGIN_LEFT;
        int y = MARGIN_TOP;
        g2d.fillRect(MARGIN_LEFT,
                     MARGIN_TOP,
                     getWidth()-MARGIN_RIGHT-MARGIN_LEFT-1,
                     getHeight()-MARGIN_TOP-MARGIN_BOTTOM);
        
        // light color
        g2d.setColor(getModel().isPassThrough()?passthroughLightColor:backgroundLightColor);
        g2d.setColor(inselection?inselectionLightColor:g2d.getColor());
        g2d.drawLine(x, y, getWidth()-MARGIN_RIGHT-2, y);
        g2d.drawLine(x, y+1, getWidth()-MARGIN_RIGHT-2, y+1);
        g2d.drawLine(x, y, x, getHeight()-MARGIN_BOTTOM-1);
        g2d.drawLine(x+1, y, x+1, getHeight()-MARGIN_BOTTOM-1);
        x = getWidth()-MARGIN_RIGHT-1;
        
        // dark color
        g2d.setColor(getModel().isPassThrough()?passthroughDarkColor:backgroundDarkColor);
        g2d.setColor(inselection?inselectionDarkColor:g2d.getColor());
        g2d.drawLine(x-1, y, x-1, getHeight()-MARGIN_BOTTOM-1);
        g2d.drawLine(x-2, y+1, x-2, getHeight()-MARGIN_BOTTOM-1);
        x = MARGIN_LEFT;
        y = getHeight()-MARGIN_BOTTOM-1;
        g2d.drawLine(x, y, getWidth()-MARGIN_RIGHT-2, y);
        g2d.drawLine(x+1, y-1, getWidth()-MARGIN_RIGHT-2, y-1);
        
        if (highlighted) {
            g2d.setColor(Color.RED);
            g2d.drawRect(MARGIN_LEFT,
                         MARGIN_TOP,
                         getWidth()-MARGIN_RIGHT-MARGIN_LEFT-2,
                         getHeight()-MARGIN_TOP-MARGIN_BOTTOM-1);
            g2d.drawRect(MARGIN_LEFT+1,
                         MARGIN_TOP+1,
                         getWidth()-MARGIN_RIGHT-MARGIN_LEFT-4,
                         getHeight()-MARGIN_TOP-MARGIN_BOTTOM-3);
        }
        if (selected) {
            g2d.setColor(Color.BLACK);
            g2d.drawRect(MARGIN_LEFT,
                         MARGIN_TOP,
                         getWidth()-MARGIN_RIGHT-MARGIN_LEFT-2,
                         getHeight()-MARGIN_TOP-MARGIN_BOTTOM-1);
        }
    }


    public void mouseClicked(MouseEvent e) {
        if (isRunning()) { return; }        
        if (e.getButton() == MouseEvent.BUTTON1) {
            if (e.getModifiersEx() == KeyEvent.CTRL_DOWN_MASK) {
                getJNetwork().toggleSelection(this);
            }
            else {
                if (e.getClickCount() > 1) {
                    if (nameLabel.getBounds().contains(e.getPoint())) {
                        rename();
                    }
                }
            }
            e.consume();
        }
    }


    public void mousePressed(MouseEvent e) {
        if (isRunning()) { 
            return;
        }        
        else if (e.getButton() == MouseEvent.BUTTON1) {
            // either start moving or dragging a new connector
            Connector connector = inConnector(e.getX(), e.getY());
            if (connector != null) {
                // start dragging a new connector
                e.translatePoint(getX(), getY());
                draglink = new JLink(null);
                draglink.setType(UserPreferences.getUserPreferences().getLinkType());
                draglink.moveSrc(connector.getConnectPoint());
                draglink.moveDest(connector.getConnectPoint());
                draglinkType = connector.getType();
                getJNetwork().setDragLink(draglink);
                getJNetwork().setSrcConnector(connector);
                setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
            }
            else {
                // start moving JRubette
                e.translatePoint(getX(), getY());
                lastX = lastMouseX = e.getX();
                lastY = lastMouseY = e.getY();
                lastPosX = getX();
                lastPosY = getY();
                setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                moving = true;
            }
            e.consume();
        }
        else if (e.getButton() == MouseEvent.BUTTON3) {
            // popup menu for JRubette
            JPopupMenu menu = createPopupMenu();
            menu.show(this, e.getX(), e.getY());
            e.consume();
        }
    }


    public void mouseReleased(MouseEvent e) {
        if (moving) {
            moving = false;
            setChanged();
            getJNetwork().refresh();
        }
        if (draglink != null) {
            e.translatePoint(getX(), getY());
            getJNetwork().finishDragLink(e.getPoint());
        }
        setCursor(Cursor.getDefaultCursor());
    }


    public void mouseEntered(MouseEvent e) {
        getJNetwork().setSelected(this);
        selected = true;        
        repaint();
    }

    
    public void mouseExited(MouseEvent e) {        
        getJNetwork().setSelected(null);
        selected = false;
        repaint();
    }


    public void mouseDragged(MouseEvent e) {
        if (moving) {
            e.translatePoint(getX(), getY());
            moveJRubette(lastPosX+e.getX()-lastMouseX, lastPosY+e.getY()-lastMouseY);
            if (inselection) {
                ArrayList<JRubette> selection = getJNetwork().getSelection();
                if (selection != null) {
                    for (JRubette r : selection) {
                        if (r != this) { r.moveRelative(e.getX()-lastX, e.getY()-lastY); }
                    }
                }
            }
            lastX = e.getX();
            lastY = e.getY();
        }
        else if (draglink != null) {
            e.translatePoint(getX(), getY());
            if (draglinkType == Connector.OUTPUT) {
                draglink.moveDest(e.getPoint());
            }
            else {
                draglink.moveSrc(e.getPoint());
            }
        }
    }


    public void mouseMoved(MouseEvent e) {}

    
    public String getToolTipText(MouseEvent e) {
      Connector connector = inConnector(e.getX(), e.getY());
      if (connector != null) {
          if (connector.getType() == Connector.INPUT) {
              return model.getInTip(connector.getPos());
          }
          else {
              return model.getOutTip(connector.getPos());
          }
      }
      else {
          return model.getName()+": "+model.getShortDescription(); //$NON-NLS-1$
      }
    }
    

    public Dimension getPreferredSize() {
        return size;
    }


    public Dimension getMinimumsize() {
        return size;
    }


    public Dimension getMaximumsize() {
        return size;
    }

    
    private JPopupMenu createPopupMenu() {
        JPopupMenu popup = new JPopupMenu();
        JMenuItem item;
        item = new JMenuTitleItem(getRubetteName());
        popup.add(item);
        popup.addSeparator();
        item = new JMenuItem(Messages.getString("JRubette.rename")); //$NON-NLS-1$
        item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    rename();
                }
            });
        popup.add(item);
        item = new JMenuItem(Messages.getString("JRubette.remove")); //$NON-NLS-1$
        item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    removeRubette();
                }
            });
        popup.add(item);
        item = new JMenuItem("Duplicate"); //$NON-NLS-1$
        item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    duplicate();
                }
            });
        popup.add(item);
        item = new JMenuItem("Move to Front"); //$NON-NLS-1$
        item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    toFront();
                }
            });
        popup.add(item);
        item = new JMenuItem("Move to Back"); //$NON-NLS-1$
        item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    toBack();
                }
            });
        popup.add(item);
        if (getModel().canPassThrough()) {
            item = new JMenuItem("Pass Through"); //$NON-NLS-1$
            item.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        togglePassThrough();
                    }
                });
            popup.add(item);
        }
        if (getModel().getRubette() instanceof MacroRubette) {
            item = new JMenuItem(Messages.getString("JRubette.open")); //$NON-NLS-1$
            item.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        openNetwork();
                    }
                });
            popup.add(item);
        }
        return popup;    
    }
    
    
    public void runPartially() {
        computeDependents();
        getJNetwork().getJComposer().startPartialRun(dependents);
    }
    
    
    public void removeRubette() {
        getJNetwork().setSelected(null);
        closeDialogs();
        getJNetwork().removeRubette(this);
    }
    
    
    public void duplicate() {
        JRubetteList rubetteList = getJNetwork().getJComposer().getJRubetteList();
        JRubette jrubette = rubetteList.duplicate(getRubette());
        getJNetwork().addRubette(jrubette, new Point(getX()+30, getY()+30));
    }
    
    
    public void togglePassThrough() {
        if (getModel().canPassThrough()) {
            getModel().togglePassThrough();
            repaint();
            runPartially();
        }
    }

    
    public void toFront() {
        getJNetwork().toFront(this);
    }
    
    
    public void toBack() {
        getJNetwork().toBack(this);
    }
    
    
    public void closeDialogs() {
        if (viewDialog != null) {
            viewDialog.dispose();
            viewDialog = null;
        }
        if (propertiesDialog != null) {
            propertiesDialog.dispose();
            propertiesDialog = null;
        }        
    }
    
    
    protected void rename() {
        if (renameField == null) {
            renameField = new JTextField();
            renameField.setBounds(nameLabel.getBounds());
            renameField.addFocusListener(new FocusAdapter() {
                public void focusLost(FocusEvent e) {
                    finishRename();
                }
            });
            renameField.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    finishRename();
                }
            });
        }
        renameField.setText(model.getName().trim());
        remove(nameLabel);        
        add(renameField);
        renameField.grabFocus();
    }

    
    protected void finishRename() {
        String s = renameField.getText().trim();
        remove(renameField);
        add(nameLabel);
        if (s.length() > 0 && s.length() <= 32) {
            model.setName(s);
            setChanged();
            refresh();
            getJNetwork().getJComposer().refreshNetworks();
        }        
    }
    
    
    protected void openNetwork() {
        MacroRubette nrubette = (MacroRubette)getRubette();
        NetworkModel networkModel = nrubette.getNetworkModel();
        JComposer jcomposer = getJNetwork().getJComposer();
        jcomposer.addJMacroRubetteView(networkModel, getJNetwork());
    }
    

    public void refresh() {
        nameLabel.setText(model.getName());
        if (viewDialog != null) {
            viewDialog.setTitle(getJNetwork().getName()+" - "+ //$NON-NLS-1$
                                model.getName()+" - "+ //$NON-NLS-1$
                                Messages.getString("JRubette.viewtitle")); //$NON-NLS-1$
        }
        if (propertiesDialog != null) {
            propertiesDialog.setTitle(getJNetwork().getName()+" - "+ //$NON-NLS-1$
                                      model.getName()+" - "+ //$NON-NLS-1$
                                      Messages.getString("JRubette.propertiestitle")); //$NON-NLS-1$
        }
        if (model.hasInfo()) {
            infoLabel.setText(getInfo());
        }
        if (inConnectorCount != model.getRubette().getInCount()) {
            resizeInConnectors(model.getRubette().getInCount());
        }
        if (outConnectorCount != model.getRubette().getOutCount()) {
            resizeOutConnectors(model.getRubette().getOutCount());
        }
        repaint();
    }
    
    
    private void setChanged() {
        getJNetwork().setChanged();
    }
    
    
    private JNetwork getJNetwork() {
        return (JNetwork)getParent();
    }
    
    
    private String getInfo() {
        String s = model.getInfo();
        return (s == null || s.length() == 0)?Messages.getString("JRubette.info"):s;  //$NON-NLS-1$
    }
    
    
    private void computeDependents() {
        dependents.clear();
        computeDependents(model, dependents);
        dependents.add(model);
        Collections.reverse(dependents);
    }

    
    private void computeDependents(RubetteModel rubetteModel, ArrayList<RubetteModel> list) {
        for (RubetteModel rmodel : rubetteModel.getFirstDependents()) {
            computeDependents(rmodel, list);
            if (!(list.contains(rmodel))) {
                list.add(rmodel);
            }
        }
    }
    
    
    protected void applyProperties(boolean hide) {
        if (!isRunning()) {
            if (model.getRubette().applyProperties()) {
                if (hide) {
                    propertiesDialog.setVisible(false);
                }
                setChanged();
                refresh();
                runPartially();
            }
        }
    }

    
    public String toString() {
        return model.getName();
    }
    

    private int lastMouseX = -1;
    private int lastMouseY = -1;
    private int lastPosX   = -1;
    private int lastPosY   = -1;
    private int lastX      = -1;
    private int lastY      = -1;
    
    private boolean      moving       = false;
    private JLink        draglink     = null;
    private int          draglinkType = 0;
    private boolean      highlighted  = false;
    private boolean      selected     = false;
    private boolean      inselection  = false;

    protected RubetteModel model            = null;
    private JLabel         nameLabel        = null;
    private JLabel         infoLabel        = null;
    private JDialog        viewDialog       = null;
    private JDialog        propertiesDialog = null;
    private JTextField     renameField      = null;

    private int          inConnectorCount  = 0;
    private int          outConnectorCount = 0;
    private Connector    inConnectors[];
    private Connector    outConnectors[];

    private ArrayList<RubetteModel> dependents = new ArrayList<RubetteModel>(100);
    
    // dimensions of box
    
    private static final int MARGIN_TOP      = 8;
    private static final int MARGIN_BOTTOM   = 8;
    private static final int MARGIN_LEFT     = 0;
    private static final int MARGIN_RIGHT    = 0;
    private static final int CONNECTOR_WIDTH = 8;
    private static final int TWIDTH           = 120;
    private static final int THEIGHT          = 130;

    private Dimension size = new Dimension(TWIDTH, THEIGHT);

    // colors
    
    private static final Color backgroundColor      = new Color(255, 228, 181);
    private static final Color backgroundLightColor = new Color(255, 242, 198);
    private static final Color backgroundDarkColor  = new Color(241, 205, 155);

    private static final Color passthroughColor      = new Color(255, 130,  71);
    private static final Color passthroughLightColor = new Color(255, 152, 111);
    private static final Color passthroughDarkColor  = new Color(241, 120,  60);

    private static final Color inselectionColor      = new Color(159, 121, 238);
    private static final Color inselectionLightColor = new Color(171, 130, 255);
    private static final Color inselectionDarkColor  = new Color(137, 104, 205);

    private static final Border emptyBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
}
