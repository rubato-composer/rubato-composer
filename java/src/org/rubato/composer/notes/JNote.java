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

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.Border;

import org.rubato.composer.network.JNetwork;
import org.rubato.xml.XMLWriter;

public class JNote
        extends JPanel
        implements MouseListener, MouseMotionListener {
    
    public JNote(int x, int y) {
        createContent(x, y, DEFAULT_SIZE);
        setLocation(x, y);
        model = new NoteModel(this, titleField.getText(), textArea.getText());
    }

    
    public JNote(NoteModel model) {
        this.model = model;
        createContent(model.getX(), model.getY(), model.getSize());
        if (model.getTitle() != null) {
            titleField.setText(model.getTitle());
            titleField.setVisible(true);
        }
        textArea.setText(model.getText());
        setForegroundColor(new Color(model.getFg()));
        setBackgroundColor(new Color(model.getBg()));
        model.setJNote(this);
    }
    
    
    public NoteModel getModel() {
        return model;
    }
    
    
    public String getTitle() {
        if (titleField.isVisible()) {
            return titleField.getText();
        }
        else {
            return null;
        }
    }

    
    public void setTitle(String s) {
        titleField.setText(s);
    }
    
    
    public String getText() {
        return textArea.getText();
    }

    
    public void setText(String s) {
        textArea.setText(s);
    }
    
    
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        g.drawLine(getWidth()-9, getHeight()-3, getWidth()-3, getHeight()-3);
        g.drawLine(getWidth()-3, getHeight()-3, getWidth()-3, getHeight()-9);
        g.drawLine(getWidth()-7, getHeight()-5, getWidth()-5, getHeight()-5);
        g.drawLine(getWidth()-5, getHeight()-5, getWidth()-5, getHeight()-7);
    }
    
    
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            if (e.getX() > getWidth()-10 && e.getY() > getHeight()-10) {
                // start resizing JNote
                lastPosX = getSize().width-e.getX();
                lastPosY = getSize().height-e.getY();
                resizing = true;
                setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
            }
            else {
                // start moving JNote
                e.translatePoint(getX(), getY());
                lastMouseX = e.getX();
                lastMouseY = e.getY();
                lastPosX = getX();
                lastPosY = getY();
                moving = true;
                setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            }
            e.consume();
        }
        else if (e.getButton() == MouseEvent.BUTTON3) {
            // popup menu for JNote
            JPopupMenu menu = createPopupMenu();
            menu.show(this, e.getX(), e.getY());
            e.consume();            
        }
    }

    
    public void mouseReleased(MouseEvent e) {
        moving = false;
        resizing = false;
        setCursor(Cursor.getDefaultCursor());
        ((JNetwork)getParent()).refresh();
    }

    
    public void mouseDragged(MouseEvent e) {
        if (moving) {
            e.translatePoint(getX(), getY());
            setLocation(lastPosX+e.getX()-lastMouseX, lastPosY+e.getY()-lastMouseY);
        }
        else if (resizing) {
            setSize(e.getX()+lastPosX, e.getY()+lastPosY);
            revalidate();
        }
    }


    public void mouseClicked(MouseEvent e) {}

    public void mouseEntered(MouseEvent e) {}

    public void mouseExited(MouseEvent e) {}

    public void mouseMoved(MouseEvent e) {
        if (e.getX() > getWidth()-10 && e.getY() > getHeight()-10) {
            setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));        
        }
        else {
            setCursor(Cursor.getDefaultCursor());            
        }
    }


    public void toXML(XMLWriter writer) {
        model.toXML(writer);
    }
    
    
    private JPopupMenu createPopupMenu() {
        JPopupMenu popup = new JPopupMenu();
        JMenuItem item;
        item = new JMenuItem(Messages.getString("JNote.remove")); //$NON-NLS-1$
        item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    removeJNote();
                }
            });
        popup.add(item);
        if (titleField.isVisible()) {
            item = new JMenuItem(Messages.getString("JNote.removetitle")); //$NON-NLS-1$
            item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    removeTitle();
                }
            });
        }
        else {
            item = new JMenuItem(Messages.getString("JNote.addtitle")); //$NON-NLS-1$
            item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    addTitle();
                }
            });
        }
        popup.add(item);
        item = new JMenuItem(Messages.getString("JNote.bgcolor")); //$NON-NLS-1$
        item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    changeBackgroundColor();
                }
            });
        popup.add(item);
        item = new JMenuItem(Messages.getString("JNote.fgcolor")); //$NON-NLS-1$
        item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    changeForegroundColor();
                }
            });
        popup.add(item);
        return popup;    
    }
    
    
    protected void removeTitle() {
        titleField.setVisible(false);
        validate();
    }
    
    
    protected void addTitle() {
        titleField.setVisible(true);
        validate();
    }
    
    
    protected void removeJNote() {
        ((JNetwork)getParent()).removeNote(this);
    }
    
    
    public void setForegroundColor(Color color) {
        fgColor = color;
        titleField.setForeground(fgColor);
        textArea.setForeground(fgColor);
        repaint();
    }
    
    
    public Color getForegroundColor() {
        return fgColor;
    }
    
    
    protected void changeForegroundColor() {
        if (colorChooser == null) {
            colorChooser = new JColorChooser();
        }
        Color color = JColorChooser.showDialog(this, Messages.getString("JNote.chosefgcolor"), fgColor); //$NON-NLS-1$
        if (color != null) {
            setForegroundColor(color);
        }        
    }
    
    
    public void setBackgroundColor(Color color) {
        bgColor = color;
        setBackground(bgColor);
        titleField.setBackground(bgColor);
        textArea.setBackground(bgColor);
        repaint();
    }
    

    public Color getBackgroundColor() {
        return bgColor;
    }
    
    
    protected void changeBackgroundColor() {
        if (colorChooser == null) {
            colorChooser = new JColorChooser();
        }
        Color color = JColorChooser.showDialog(this, Messages.getString("JNote.chosebgcolor"), fgColor); //$NON-NLS-1$
        if (color != null) {
            setBackgroundColor(color);
        }        
    }
    
    
    private void createContent(int x, int y, Dimension size) {
        setLayout(new BorderLayout(0, 5));
        setOpaque(true);
        setBorder(BorderFactory.createCompoundBorder(lineBorder, marginBorder));
        setBackground(bgColor);
        
        titleField = new JTextField();
        titleField.setBackground(bgColor);
        titleField.setForeground(fgColor);
        titleField.setFont(titleFont);
        titleField.setBorder(emptyBorder);
        titleField.setHorizontalAlignment(SwingConstants.CENTER);
        titleField.setVisible(false);
        add(titleField, BorderLayout.NORTH);

        textArea = new JTextArea();
        textArea.setBackground(bgColor);
        textArea.setForeground(fgColor);
        textArea.setBorder(emptyBorder);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(emptyBorder);
        add(scrollPane, BorderLayout.CENTER);
        
        setLocation(x, y);
        setSize(size);
        repaint();
        revalidate();
        addMouseListener(this);
        addMouseMotionListener(this);        
    }
    
    
    private NoteModel  model;
    private JTextArea  textArea;
    private JTextField titleField;
    private int        lastPosX, lastPosY, lastMouseX, lastMouseY;
    private boolean    moving = false;
    private boolean    resizing = false;
    private Color      fgColor = DEFAULT_FOREGROUND;
    private Color      bgColor = DEFAULT_BACKGROUND;

    private JColorChooser colorChooser = null;

    private final static Color     DEFAULT_BACKGROUND = Color.YELLOW;
    private final static Color     DEFAULT_FOREGROUND = Color.BLACK;
    private final static Dimension DEFAULT_SIZE = new Dimension(180, 120);
    private final static Border    emptyBorder = BorderFactory.createEmptyBorder();
    private final static Border    marginBorder = BorderFactory.createEmptyBorder(10, 10, 10, 10);
    private final static Border    lineBorder = BorderFactory.createLineBorder(Color.BLACK);
    private final static Font      titleFont = new Font("sans", Font.BOLD, 16); //$NON-NLS-1$
}
