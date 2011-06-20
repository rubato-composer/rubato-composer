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

package org.rubato.rubettes.select2d;

import static org.rubato.composer.Utilities.makeTitledBorder;
import static org.rubato.xml.XMLConstants.VALUE_ATTR;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import org.rubato.base.RubatoException;
import org.rubato.composer.components.JFormTree;
import org.rubato.composer.icons.Icons;
import org.rubato.composer.view2d.*;
import org.rubato.composer.view2d.Point;
import org.rubato.math.module.*;
import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.Form;
import org.rubato.util.DoubleConverter;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

class Select2DPanel
        extends JPanel
        implements ActionListener {

    public Select2DPanel(View2DModel model) {
        this.model = model;
        createLayout();
    }

    
    public Select2DPanel(Form form) {
        model = new View2DModel(form);
        createLayout();
    }

    
    public void setDenotators(ArrayList<Denotator> denotators) {
        model.setDenotators(denotators);
        sliderPanel.setVisible(false);
        if (denotators != null && denotators.size() > 0) {
            Module address = denotators.get(0).getAddress();
            sliderPanel.removeAll();
            elementSlider = JElementSlider.make(address);
            if (elementSlider != null) {
                elementSlider.addActionListener(this);
                sliderPanel.add(elementSlider);
                sliderPanel.setVisible(true);
            }            
        }
        refillView();
    }

    
    public boolean hasSelections() {
        return model.hasSelections();
    }
    
    
    public boolean contains(int i) {
        return select2DView.isSelected(i);
    }
    
    
    public void setXPath(int[] path) {
        if (path != null) {
            xAxisTree.setSelectedPath(path);
            model.unsetXAxis();
            Module module = xAxisTree.getSelectedModule();
            if (module != null) {
                model.setXAxis(module, xAxisTree.getSelectedPath());
            }
            refillView();
        }
    }
    
    
    public void setYPath(int[] path) {
        if (path != null) {
            yAxisTree.setSelectedPath(path);
            model.unsetYAxis();
            Module module = yAxisTree.getSelectedModule();
            if (module != null) {
                model.setYAxis(module, yAxisTree.getSelectedPath());
            }
            refillView();
        }
    }
    
    
    private void createLayout() {
        setLayout(new BorderLayout());
        
        add(createActionBox(), BorderLayout.NORTH);
        
        JScrollPane scrollPane; 
        JPanel axisPanel = new JPanel();
        axisPanel.setLayout(new GridLayout(2, 1, 5, 5));
        
        xAxisTree = new JFormTree(model.getBaseForm(), true);
        xAxisTree.addActionListener(this);
        scrollPane = new JScrollPane(xAxisTree);
        scrollPane.setBorder(makeTitledBorder(XAXISTREE_TITLE));
        axisPanel.add(scrollPane);
        
        yAxisTree = new JFormTree(model.getBaseForm(), true);
        yAxisTree.addActionListener(this);
        scrollPane = new JScrollPane(yAxisTree);
        scrollPane.setBorder(makeTitledBorder(YAXISTREE_TITLE));
        axisPanel.add(scrollPane);
        
        axisPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        axisPanel.setPreferredSize(new Dimension(220, 0));
        add(axisPanel, BorderLayout.WEST);
        
        select2DView = new View2D(model);
        select2DView.setBorder(BorderFactory.createEtchedBorder());
        select2DView.addActionListener(this);
        add(select2DView, BorderLayout.CENTER);
        
        JPanel bottomBox = new JPanel();
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        bottomBox.setLayout(layout);
        
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 5, 5); 
        
        c.gridwidth = 1;
        c.weightx = 0.5;
        JComponent pointerBox = createPointerBox();
        layout.setConstraints(pointerBox, c);
        bottomBox.add(pointerBox);

        c.weightx = 1.0;
        c.gridwidth = GridBagConstraints.REMAINDER;
        JComponent extentBox = createExtentBox();
        layout.setConstraints(extentBox, c);
        bottomBox.add(extentBox);

        c.gridwidth = GridBagConstraints.REMAINDER;
        sliderPanel = new JPanel();
        sliderPanel.setBorder(makeTitledBorder("Address element"));
        sliderPanel.setLayout(new BorderLayout());
        sliderPanel.setVisible(false);
        elementSlider = null;
        layout.setConstraints(sliderPanel, c);
        bottomBox.add(sliderPanel);
        //sliderPanel.setVisible(false);
        
        add(bottomBox, BorderLayout.SOUTH);
        
        newSelectionAction    = new NewSelectionAction(select2DView);
        selectSelectionAction = new SelectSelectionAction(select2DView);
        removePointAction     = new RemovePointAction(select2DView);
        addPointAction        = new AddPointAction(select2DView);
        moveAction            = new MoveAction(select2DView);
        identifyAction        = new IdentifyAction(select2DView);
        windowZoomAction      = new WindowZoomAction(select2DView);
        
        select2DView.setAction(newSelectionAction);
        updateFields();
    }

    
    private JComponent createActionBox() {
        actionToolBar = new JToolBar();
        ButtonGroup group = new ButtonGroup();
        
        Action action = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                selectAction();
            }
        };
        
        newSelectionButton = new JToggleButton(NEWSEL_ICON);
        newSelectionButton.setToolTipText(NEWSEL_TIP);
        newSelectionButton.addActionListener(action);
        group.add(newSelectionButton);
        actionToolBar.add(newSelectionButton);
        
        selectSelectionButton = new JToggleButton(SELSEL_ICON);
        selectSelectionButton.setToolTipText(SELSEL_TIP);
        selectSelectionButton.addActionListener(action);
        group.add(selectSelectionButton);
        actionToolBar.add(selectSelectionButton);
        
        addPointButton = new JToggleButton(ADD_ICON);
        addPointButton.setToolTipText(ADD_TIP);
        addPointButton.addActionListener(action);
        group.add(addPointButton);
        actionToolBar.add(addPointButton);

        removePointButton = new JToggleButton(REMOVE_ICON);
        removePointButton.setToolTipText(REMOVE_TIP);
        removePointButton.addActionListener(action);
        group.add(removePointButton);
        actionToolBar.add(removePointButton);

        moveButton = new JToggleButton(MOVE_ICON);
        moveButton.setToolTipText(MOVE_TIP);
        moveButton.addActionListener(action);
        group.add(moveButton);
        actionToolBar.add(moveButton);

        identifyButton = new JToggleButton(IDENTIFY_ICON);
        identifyButton.setToolTipText(IDENTIFY_TIP);
        identifyButton.addActionListener(action);
        group.add(identifyButton);
        actionToolBar.add(identifyButton);
        
        windowZoomButton = new JToggleButton(ZOOM_ICON);
        windowZoomButton.setToolTipText(ZOOM_TIP);
        windowZoomButton.addActionListener(action);
        group.add(windowZoomButton);
        actionToolBar.add(windowZoomButton);
        
        newSelectionButton.setSelected(true);

        actionToolBar.addSeparator();
        
        autoZoomButton = new JButton(AUTO_ZOOM_ICON);
        autoZoomButton.setToolTipText(AUTO_ZOOM_TIP);
        autoZoomButton.addActionListener(this);
        actionToolBar.add(autoZoomButton);

        return actionToolBar;
    }
    
    
    private JComponent createExtentBox() {
        JLabel label;
        JPanel extentBox = new JPanel();
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        extentBox.setLayout(layout);
        
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 5, 5); 
        
        c.gridwidth = 1;
        c.weightx = 0.0;
        label = new JLabel(" x min:"); //$NON-NLS-1$
        layout.setConstraints(label, c);
        extentBox.add(label);
        
        c.weightx = 1.0;
        xminField = new JTextField();
        xminField.setEditable(false);
        layout.setConstraints(xminField, c);
        extentBox.add(xminField);
        
        c.weightx = 0.0;
        label = new JLabel(" x max:"); //$NON-NLS-1$
        layout.setConstraints(label, c);
        extentBox.add(label);
        
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weightx = 1.0;
        xmaxField = new JTextField();
        xmaxField.setEditable(false);
        layout.setConstraints(xmaxField, c);
        extentBox.add(xmaxField);
        
        c.gridwidth = 1;
        c.weightx = 0.0;
        label = new JLabel(" y min:"); //$NON-NLS-1$
        layout.setConstraints(label, c);
        extentBox.add(label);
        
        c.weightx = 1.0;
        yminField = new JTextField();
        yminField.setEditable(false);
        layout.setConstraints(yminField, c);
        extentBox.add(yminField);
        
        c.weightx = 0.0;
        label = new JLabel(" y max:"); //$NON-NLS-1$
        layout.setConstraints(label, c);
        extentBox.add(label);
        
        c.weightx = 1.0;
        ymaxField = new JTextField();
        ymaxField.setEditable(false);
        layout.setConstraints(ymaxField, c);
        extentBox.add(ymaxField);
        
        extentBox.setBorder(makeTitledBorder(EXTENT_TITLE));
        
        return extentBox;
    }
    
    
    private JComponent createPointerBox() {
        JLabel label;
        JPanel pointerBox = new JPanel();
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        pointerBox.setLayout(layout);
        
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 5, 5); 
        
        c.gridwidth = 1;
        c.weightx = 0.0;
        label = new JLabel(" x:"); //$NON-NLS-1$
        layout.setConstraints(label, c);
        pointerBox.add(label);
        
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weightx = 1.0;
        curXField = new JTextField();
        curXField.setEditable(false);
        layout.setConstraints(curXField, c);
        pointerBox.add(curXField);
        
        c.gridwidth = 1;
        c.weightx = 0.0;
        label = new JLabel(" y:"); //$NON-NLS-1$
        layout.setConstraints(label, c);
        pointerBox.add(label);
        
        c.weightx = 1.0;
        curYField = new JTextField();
        curYField.setEditable(false);
        layout.setConstraints(curYField, c);
        pointerBox.add(curYField);

        pointerBox.setBorder(makeTitledBorder(POINTER_TITLE));
        
        return pointerBox;
    }
    
    
    public void selectAction() {
        if (newSelectionButton.isSelected()) {
            select2DView.setAction(newSelectionAction);
        }
        else if (selectSelectionButton.isSelected()) {
            select2DView.setAction(selectSelectionAction);
        }
        else if (addPointButton.isSelected()) {
            select2DView.setAction(addPointAction);
        }
        else if (removePointButton.isSelected()) {
            select2DView.setAction(removePointAction);
        }
        else if (moveButton.isSelected()) {
            select2DView.setAction(moveAction);                
        }
        else if (identifyButton.isSelected()) {
            select2DView.setAction(identifyAction);
        }
        else if (windowZoomButton.isSelected()) {
            select2DView.setAction(windowZoomAction);            
        }
    }
    
    
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == xAxisTree) {
            model.unsetXAxis();
            Module module = xAxisTree.getSelectedModule();
            if (module != null) {
                model.setXAxis(module, xAxisTree.getSelectedPath());
            }
            refillView();
        }
        else if (src == yAxisTree) {
            model.unsetYAxis();
            Module module = yAxisTree.getSelectedModule();
            if (module != null) {
                model.setYAxis(module, yAxisTree.getSelectedPath());
            }
            refillView();
        }
        else if (src == select2DView) {
            if (e.getActionCommand().equals("newselection")) {
                addPointButton.doClick();
            }
            else {
                // window of the select view has changed
                updateFields();
            }
        }
        else if (src == autoZoomButton) {
            select2DView.zoomAll();
        }
        else if (src == elementSlider) {
            refillView();
        }
    }
    
    
    private void updateFields() {
        WindowConfig config = model.getWindowConfig();
        int xPrecision = precision(config.x_min, config.x_max);
        int yPrecision = precision(config.y_min, config.y_max);
        xFormat.setMaximumFractionDigits(xPrecision);
        yFormat.setMaximumFractionDigits(yPrecision);
        xminField.setText(xFormat.format(config.x_min));
        xmaxField.setText(xFormat.format(config.x_max));
        yminField.setText(yFormat.format(config.y_min));
        ymaxField.setText(yFormat.format(config.y_max));
        curXField.setText(xFormat.format(select2DView.getPointerX()));
        curYField.setText(yFormat.format(select2DView.getPointerY()));
    }

    private NumberFormat xFormat = NumberFormat.getInstance();
    private NumberFormat yFormat = NumberFormat.getInstance();
    
    private int precision(double a, double b) {
        double x = Math.abs(a-b);
        int i = 0;
        while (x < 1000.0) {
            i++;
            x *= 10.0;
        }
        return i;
    }
    
    
    private boolean axesDefined() {
        return model.axesDefined();
    }
    

    private void refillView() {
        clearView();
        if (axesDefined() && model.getDenotators() != null) {
            try {
                DoubleConverter xconvert = DoubleConverter.makeDoubleConverter(model.getXModule());
                DoubleConverter yconvert = DoubleConverter.makeDoubleConverter(model.getYModule());
                if (xconvert == null || yconvert == null) {
                    clearView();
                    return;
                }
                if (elementSlider == null) {
                    // element slider cannot be shown
                    for (Denotator d : model.getDenotators()) {
                        ModuleElement xElement = d.getElement(model.getXPath());
                        ModuleElement yElement = d.getElement(model.getYPath());
                        double x = xconvert.toDouble(xElement);
                        double y = yconvert.toDouble(yElement);
                        select2DView.addPoint(x, y, d);
                    }
                }
                else {
                    // create points evaluated at address
                    ModuleElement element = elementSlider.getElement();
                    for (Denotator d : model.getDenotators()) {
                        ModuleElement xElement = d.getModuleMorphism(model.getXPath()).map(element);
                        ModuleElement yElement = d.getModuleMorphism(model.getYPath()).map(element);
                        double x = xconvert.toDouble(xElement);
                        double y = yconvert.toDouble(yElement);
                        select2DView.addPoint(x, y, d);
                    }
                }
                select2DView.recalcScreenCoords();
                select2DView.repaint();
            }
            catch (RubatoException e) {
                clearView();
                return;
            }
        }
    }
    
    
    public Dimension getPreferredSize() {
        return PREFERRED_SIZE;
    }

    
    private void clearView() {
        select2DView.removeAllPoints();
        repaint();
    }
    
    
    private static String pathToString(int[] path) {
        StringBuilder buf = new StringBuilder(20);
        if (path.length > 0) {
            buf.append(path[0]);
            for (int i = 1; i < path.length; i++) {
                buf.append(","); //$NON-NLS-1$
                buf.append(path[i]);
            }
        }
        return buf.toString();
    }
    
    
    private static int[] stringToPath(String pathString) {
        String p = pathString.trim();
        if (p.length() == 0) {
            return new int[0];
        }
        String[] strings = pathString.trim().split(","); //$NON-NLS-1$
        int[] path = new int[strings.length];
        for (int i = 0; i < path.length; i++) {
            try {
                path[i] = Integer.parseInt(strings[i]);
            }
            catch (NumberFormatException e) {
                return null;
            }
        }
        return path;
    }
    
    
    public Select2DPanel duplicate() {
        WindowConfig  config   = model.getWindowConfig();
        View2DModel   newModel = new View2DModel(model.getForm());
        Select2DPanel newPanel = new Select2DPanel(newModel);
        View2D        newView  = newPanel.select2DView;

        newView.setWindow(config.x_min, config.x_max, config.y_min, config.y_max);

        newPanel.setXPath(model.getXPath());       
        newPanel.setYPath(model.getYPath());       
        
        if (model.hasSelections()) {
            List<Selection> selections = model.getSelections();
            for (Selection s : selections) {
                List<Point> points = s.getPoints();
                newView.newSelection();
                for (Point pt : points) {
                    newView.addPointToSelection(pt.realX, pt.realY);
                }
            }
        }
        
        return newPanel;
    }
    
    
    private final static String EXTENT    = "Extent"; //$NON-NLS-1$
    private final static String XMIN      = "xmin"; //$NON-NLS-1$
    private final static String XMAX      = "xmax"; //$NON-NLS-1$
    private final static String YMIN      = "ymin"; //$NON-NLS-1$
    private final static String YMAX      = "ymax"; //$NON-NLS-1$
    private final static String XPATH     = "XPath"; //$NON-NLS-1$
    private final static String YPATH     = "YPath"; //$NON-NLS-1$
    private final static String POINT     = "Point"; //$NON-NLS-1$
    private final static String SELECTION = "Selection"; //$NON-NLS-1$
    private final static String X_ATTR    = "X"; //$NON-NLS-1$
    private final static String Y_ATTR    = "Y"; //$NON-NLS-1$
    
    public void toXML(XMLWriter writer) {
        WindowConfig config = model.getWindowConfig();
        writer.empty(EXTENT, XMIN, config.x_min,
                             XMAX, config.x_max,
                             YMIN, config.y_min,
                             YMAX, config.y_max);
        if (model.getXPath() != null) {
            writer.empty(XPATH, VALUE_ATTR, pathToString(model.getXPath()));
        }
        if (model.getYPath() != null) {
            writer.empty(YPATH, VALUE_ATTR, pathToString(model.getYPath()));
        }
        if (model.hasSelections()) {
            List<Selection> selections = model.getSelections();
            for (Selection s : selections) {
                writer.openBlock(SELECTION);
                List<Point> points = s.getPoints();
                for (Point pt : points) {
                    writer.empty(POINT, X_ATTR, pt.realX, Y_ATTR, pt.realY);
                }
                writer.closeBlock();
            }
        }
    }
    
    
    public static Select2DPanel fromXML(XMLReader reader, Element element, Form form) {
        // parse extent
        Element child = XMLReader.getChild(element, EXTENT);
        if (child == null) {
            reader.setError("Missing element <%1>", EXTENT); //$NON-NLS-1$
            return null;
        }

        View2DModel   model = new View2DModel(form);
        Select2DPanel panel = new Select2DPanel(model);
        View2D        view  = panel.select2DView;

        double xmin = XMLReader.getRealAttribute(child, XMIN, 0.0);        
        double xmax = XMLReader.getRealAttribute(child, XMAX, 0.0);        
        double ymin = XMLReader.getRealAttribute(child, YMIN, 0.0);        
        double ymax = XMLReader.getRealAttribute(child, YMAX, 0.0);

        view.setWindow(xmin, xmax, ymin, ymax);
        
        // parse paths
        Element pathChild = XMLReader.getNextSibling(child, XPATH);
        if (pathChild != null) {
            int[] path = stringToPath(XMLReader.getStringAttribute(pathChild, VALUE_ATTR));
            panel.setXPath(path);
        }
        pathChild = XMLReader.getNextSibling(child, YPATH);
        if (pathChild != null) {
            int[] path = stringToPath(XMLReader.getStringAttribute(pathChild, VALUE_ATTR));
            panel.setYPath(path);
        }
        
        // parse points of selection
        Element selChild = XMLReader.getNextSibling(child, SELECTION);
        while (selChild != null) {
            view.newSelection();
            child = XMLReader.getChild(selChild, POINT);
            while (child != null) {
                double x = XMLReader.getRealAttribute(child, X_ATTR, 0.0); 
                double y = XMLReader.getRealAttribute(child, Y_ATTR, 0.0);
                view.addPointToSelection(x, y);
                child = XMLReader.getNextSibling(child, POINT);
            }
            selChild = XMLReader.getNextSibling(selChild, SELECTION);
        }
        
        return panel;
    }
    

    private View2DModel model;
    
    private JFormTree xAxisTree;
    private JFormTree yAxisTree;
    private View2D    select2DView;
    
    private JPanel        sliderPanel;
    private JElementSlider elementSlider;

    private JTextField xminField;
    private JTextField xmaxField;
    private JTextField yminField;
    private JTextField ymaxField;

    private JTextField curXField;
    private JTextField curYField;
    
    private JToolBar actionToolBar;
    
    private JToggleButton newSelectionButton;
    private JToggleButton selectSelectionButton;
    private JToggleButton addPointButton;
    private JToggleButton removePointButton;
    private JToggleButton moveButton;
    private JToggleButton identifyButton;
    private JToggleButton windowZoomButton;
    private JButton       autoZoomButton;
    
    //
    // Actions
    //
    
    private Action2D newSelectionAction    = null;
    private Action2D selectSelectionAction = null;
    private Action2D removePointAction     = null;
    private Action2D addPointAction        = null;
    private Action2D moveAction            = null;
    private Action2D identifyAction        = null;
    private Action2D windowZoomAction      = null;

    //
    // Constants
    //
    
    private static final Dimension PREFERRED_SIZE = new Dimension(700, 500);
    
    //
    // Strings (tooltips, titles...)
    //
    
    private final static String NEWSEL_TIP      = Messages.getString("Select2DPanel.createnewselectiontip"); //$NON-NLS-1$
    private final static String SELSEL_TIP      = Messages.getString("Select2DPanel.selectselectiontip"); //$NON-NLS-1$
    private final static String ADD_TIP         = Messages.getString("Select2DPanel.addselpt"); //$NON-NLS-1$
    private final static String REMOVE_TIP      = Messages.getString("Select2DPanel.remselpt"); //$NON-NLS-1$
    private final static String MOVE_TIP        = Messages.getString("Select2DPanel.move"); //$NON-NLS-1$
    private final static String IDENTIFY_TIP    = Messages.getString("Select2DPanel.identify"); //$NON-NLS-1$
    private final static String ZOOM_TIP        = Messages.getString("Select2DPanel.windowzoomtip"); //$NON-NLS-1$
    private final static String AUTO_ZOOM_TIP   = Messages.getString("Select2DPanel.zoom"); //$NON-NLS-1$
    private final static String XAXISTREE_TITLE = Messages.getString("Select2DPanel.xaxis"); //$NON-NLS-1$
    private final static String YAXISTREE_TITLE = Messages.getString("Select2DPanel.yaxis"); //$NON-NLS-1$;
    private final static String EXTENT_TITLE    = Messages.getString("Select2DPanel.extent"); //$NON-NLS-1$;
    private final static String POINTER_TITLE   = Messages.getString("Select2DPanel.pointer"); //$NON-NLS-1$;    
    
    //
    // Icons
    //
    
    private final static ImageIcon getIcon(String name) {
        return Icons.loadIcon(Select2DPanel.class, name);        
    }
    
    private static final ImageIcon NEWSEL_ICON    = getIcon("newselicon.png");
    private static final ImageIcon SELSEL_ICON    = getIcon("selselicon.png");
    private static final ImageIcon ADD_ICON       = getIcon("addicon.png");
    private static final ImageIcon REMOVE_ICON    = getIcon("removeicon.png");
    private static final ImageIcon MOVE_ICON      = getIcon("moveicon.png");
    private static final ImageIcon IDENTIFY_ICON  = getIcon("infoicon.png");
    private static final ImageIcon ZOOM_ICON      = getIcon("zoomicon.png");
    private static final ImageIcon AUTO_ZOOM_ICON = getIcon("autozoomicon.png");
}
