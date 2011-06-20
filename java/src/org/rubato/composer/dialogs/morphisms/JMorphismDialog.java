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

package org.rubato.composer.dialogs.morphisms;

import static org.rubato.composer.Utilities.installEnterKey;
import static org.rubato.composer.Utilities.installEscapeKey;
import static org.rubato.composer.Utilities.makeTitledBorder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.*;

import org.rubato.base.Repository;
import org.rubato.composer.components.JModuleEntry;
import org.rubato.composer.plugin.ModuleMorphismPlugin;
import org.rubato.composer.plugin.PluginManager;
import org.rubato.math.module.*;
import org.rubato.math.module.morphism.*;

/**
 * ModuleMorphism creator dialog.
 * 
 * @author Gérard Milmeister
 */
public class JMorphismDialog
        extends JDialog
        implements CaretListener, JMorphismContainer {

    public JMorphismDialog(Frame frame, boolean modal, boolean naming) {
        this(frame, modal, naming, null, null);
    }
    
    
    public JMorphismDialog(Frame frame, boolean naming, ModuleMorphism morphism) {
        super(frame, Messages.getString("JMorphismDialog.dialogtitle"), true); //$NON-NLS-1$
        this.domain   = morphism.getDomain();
        this.codomain = morphism.getCodomain();
        this.naming   = naming;
        layoutDialog();
        editMorphism(morphism);
    }
    
    
    public JMorphismDialog(Frame frame, boolean modal, boolean naming, Module domain, Module codomain) {
        super(frame, Messages.getString("JMorphismDialog.createmorphism"), modal); //$NON-NLS-1$
        this.domain   = domain;
        this.codomain = codomain;
        this.naming   = naming;
        layoutDialog();
    }

    
    private void layoutDialog() {
        setLayout(new BorderLayout(0, 5));
        
        // domain and codomain entries
        Box topBox = new Box(BoxLayout.Y_AXIS);        
        // domain entry
        if (domain == null) {
            domainEntry = new JModuleEntry();
            domainEntry.setBorder(domainBorder);
            domainEntry.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    updateDomain();
                }
            });
            topBox.add(domainEntry);
        }
        else {
            JTextField domainLabel = new JTextField(domain.toVisualString());
            domainLabel.setEditable(false);
            domainLabel.setBackground(Color.WHITE);
            Box domainBox = new Box(BoxLayout.Y_AXIS);
            domainBox.add(domainLabel);
            domainBox.setBorder(domainBorder);
            topBox.add(domainBox);
        }
        // codomain entry
        if (codomain == null) {
            codomainEntry = new JModuleEntry();
            codomainEntry.setBorder(codomainBorder);
            codomainEntry.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    updateCodomain();
                }
            });
            topBox.add(codomainEntry);
        }
        else {
            JTextField codomainLabel = new JTextField(codomain.toVisualString());
            codomainLabel.setEditable(false);
            codomainLabel.setBackground(Color.WHITE);
            Box codomainBox = new Box(BoxLayout.Y_AXIS);
            codomainBox.add(codomainLabel);
            codomainBox.setBorder(codomainBorder);
            topBox.add(codomainBox);
        }
        topBox.add(Box.createVerticalStrut(5));
        
        // type combobox
        JPanel morphismTypePanel = new JPanel();
        morphismTypePanel.setLayout(new BorderLayout());
        morphismTypePanel.setBorder(morphismTypeBorder);
        morphismType = new JComboBox();
        morphismType.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectMorphismType();
            }
        });
        morphismTypePanel.add(morphismType, BorderLayout.CENTER);
        topBox.add(morphismTypePanel);
        
        add(topBox, BorderLayout.NORTH);

        // main morphism entry panel (dynamic)
        morphismPanel = new JPanel();
        morphismPanel.setLayout(new BorderLayout());
        morphismPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(morphismPanel, BorderLayout.CENTER);
        
        // bottom box containing name entry field and buttons
        Box bottomBox = new Box(BoxLayout.Y_AXIS);
        bottomBox.setBorder(BorderFactory.createEmptyBorder());
        
        // name entry field if required
        nameok = true;
        if (naming) {
            nameok = false;
            Box nameBox = new Box(BoxLayout.X_AXIS);
            nameField = new JTextField();
            nameField.addCaretListener(this);
            nameBox.add(nameField);
            nameBox.setBorder(nameBorder);
            bottomBox.add(nameBox);
            bottomBox.add(Box.createVerticalStrut(10));
        }
        
        // buttons
        Box buttonBox = new Box(BoxLayout.X_AXIS);
        clearButton = new JButton(Messages.getString("JMorphismDialog.clear")); //$NON-NLS-1$
        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clear();
            }
        });
        buttonBox.add(clearButton);
        buttonBox.add(Box.createHorizontalStrut(10));
        
        createButton = new JButton(Messages.getString("JMorphismDialog.create")); //$NON-NLS-1$
        createButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                create();
            }
        });
        buttonBox.add(createButton);
        buttonBox.add(Box.createHorizontalStrut(10));
        
        cancelButton = new JButton(Messages.getString("JMorphismDialog.cancel")); //$NON-NLS-1$
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        buttonBox.add(cancelButton);
        bottomBox.add(buttonBox);
        
        add(bottomBox, BorderLayout.SOUTH);
        
        installEscapeKey(this);
        Action enterAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                createButton.doClick();
            }
        };         
        installEnterKey(this, enterAction);
        
        pack();
        
        // create morphism entry panel
        fillMorphismType(morphismType);
        setMorphism(null);
        
        updateButtonState();
    }

    
    public ModuleMorphism getMorphism() {
        return morphism;
    }
    
    
    public Module getDomain() {
        return domain;
    }
    
    
    public Module getCodomain() {
        return codomain;
    }
    
    
    public void setMorphism(ModuleMorphism m) {
        morphism = m;
        updateButtonState();
    }
    
    
    private void editMorphism(ModuleMorphism m) {
        if (m instanceof IdentityMorphism) {
            morphismType.setSelectedItem(IDENTITY_TYPE);
        }
        else if (m instanceof EmbeddingMorphism) {
            morphismType.setSelectedItem(CANONICAL_TYPE);
        }
        else if (m instanceof ModuloMorphism) {
            morphismType.setSelectedItem(CANONICAL_TYPE);
        }
        else if (m instanceof ConstantMorphism) {
            morphismType.setSelectedItem(CONSTANT_TYPE);
        }
        else if (m instanceof CompositionMorphism) {
            morphismType.setSelectedItem(COMPOSITION_TYPE);
        }
        else if (m instanceof SumMorphism) {
            morphismType.setSelectedItem(SUM_TYPE);
        }
        else if (m instanceof DifferenceMorphism) {
            morphismType.setSelectedItem(DIFFERENCE_TYPE);
        }
        else if (m instanceof PowerMorphism) {
            morphismType.setSelectedItem(POWER_TYPE);
        }
        else if (m instanceof ScaledMorphism) {
            morphismType.setSelectedItem(SCALED_TYPE);
        }
        else if (m instanceof TranslationMorphism) {
            morphismType.setSelectedItem(TRANSLATION_TYPE);
        }
        else if (m instanceof SplitMorphism) {
            morphismType.setSelectedItem(SPLIT_TYPE);
        }
        else if (m instanceof PolynomialMorphism) {
            morphismType.setSelectedItem(POLYNOMIAL_TYPE);
        }
        else if (m instanceof ProductMorphism) {
            morphismType.setSelectedItem(PRODUCT_TYPE);
        }
        else if (m instanceof ZnAffineMorphism ||
                 m instanceof ZnFreeAffineMorphism ||
                 m instanceof ZAffineMorphism ||
                 m instanceof ZFreeAffineMorphism ||
                 m instanceof QAffineMorphism ||
                 m instanceof QFreeAffineMorphism ||
                 m instanceof RAffineMorphism ||
                 m instanceof RFreeAffineMorphism ||
                 m instanceof CAffineMorphism ||
                 m instanceof CFreeAffineMorphism) {
            morphismType.setSelectedItem(AFFINE_TYPE);
        }
        else if (m instanceof GenericAffineMorphism) {
            JOptionPane.showMessageDialog(this, Messages.getString("JMorphismDialog.cannotedit")); //$NON-NLS-1$
        }
        else {
            JOptionPane.showMessageDialog(this, Messages.getString("JMorphismDialog.cannotedit")); //$NON-NLS-1$
        }
        selectMorphismType();
        if (jmorphismType != null) {
            jmorphismType.editMorphism(m);
        }
    }
    
    
    public void caretUpdate(CaretEvent e) {
        if (e.getSource() == nameField) {
            nameok = nameField.getText().trim().length() > 0;
            updateButtonState();
        }
    }
    
    
    private void updateButtonState() {
        createButton.setEnabled(morphism != null && (!naming || nameok));
    }
    
    
    protected void create() {
        if (naming) {
            String name = nameField.getText().trim();
            if (name.length() == 0) {
                JOptionPane.showMessageDialog(this, Messages.getString("JMorphismDialog.namenotempty"), Messages.getString("JMorphismDialog.nameerror"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
                return;
            }
            else {
                ModuleMorphism m = getMorphism();
                if (m != null) {
                    rep.registerModuleMorphism(name, m);
                }
            }
        }
        setVisible(false);
        dispose();
    }
    
    
    public void reset() {
        clear();
    }
    
    
    public void clear() {
        fillMorphismType(morphismType);
        if (naming) {
            nameField.setText(""); //$NON-NLS-1$
        }
        if (domainEntry != null) {
            domainEntry.clear();
        }
        if (codomainEntry != null) {
            codomainEntry.clear();
        }    
        updateButtonState();
    }
    
    
    public void setName(String name) {
        if (nameField != null) {
            nameField.setText(name);
        }
    }
    
    
    
    /**
     * Sets the collection of module morphism types possible for
     * the selected domain and codomain.
     */
    private void fillMorphismType(JComboBox comboBox) {
        setMorphism(null);        
        ArrayList<String> items = new ArrayList<String>();
        if (domain != null && codomain != null) {
            items.add(CONSTANT_TYPE);
            items.add(CANONICAL_TYPE);
            items.add(COMPOSITION_TYPE);
            items.add(SUM_TYPE);
            items.add(DIFFERENCE_TYPE);
            items.add(SCALED_TYPE);
            Ring domainRing = domain.getRing();
            if (domainRing.equals(codomain.getRing())) {
                if (domainRing instanceof NumberRing ||
                    domainRing instanceof ZnRing ||
                    domainRing instanceof PolynomialRing ||
                    domainRing instanceof ModularPolynomialRing) {
                    items.add(AFFINE_TYPE);
                }
                if (domain instanceof FreeModule && codomain instanceof FreeModule) {
                    items.add(SHUFFLE_TYPE);
                }
            }
            if (domain.equals(codomain)) {
                items.add(IDENTITY_TYPE);
                items.add(POWER_TYPE);
                items.add(TRANSLATION_TYPE);
                if (domain.isRing()) {
                    items.add(POLYNOMIAL_TYPE);
                }
            }
            if (domain instanceof ProductRing) {
                if (codomain instanceof ProductRing) {
                    items.add(REORDER_TYPE);
                }
                if (codomain instanceof Ring) {
                    boolean ok = false;
                    for (Ring ring : ((ProductRing)domain).getFactors()) {
                        if (ring.equals(codomain)) {
                            ok = true;
                        }
                    }
                    if (ok) {
                        items.add(PROJECTION_TYPE);
                    }
                }
            }
            if (domain instanceof CFreeModule && domain.equals(codomain)) {
                items.add(CONJUGATION_TYPE);
            }
            if (domain instanceof RFreeModule && codomain instanceof RFreeModule &&
                domain.getDimension() == 2 && codomain.getDimension() == 2) {
                items.add(GEOMETRY_TYPE);
            }
            if (domain instanceof FreeModule && domain.equals(codomain)) {
                items.add(SPLIT_TYPE);
            }
            if (domain instanceof ZFreeModule && codomain instanceof ZnFreeModule) {
                if (((ZFreeModule)domain).getDimension() == ((ZnFreeModule)codomain).getDimension()) {
                    items.add(MODULO_TYPE);
                }
            }
            if (codomain.isRing()) {
                items.add(PRODUCT_TYPE);
            }
            
            // add all module morphisms from plugins
            for (ModuleMorphismPlugin m : pluginManager.getModuleMorphismPlugins()) {
                if (m.checkSignature(domain, codomain)) {
                    items.add(m.getName());
                }
            }
        }
        
        comboBox.removeAllItems();
        comboBox.addItem(NONE_TYPE);
        Collections.sort(items);
        for (String i : items) {
            comboBox.addItem(i);
        }
        updateButtonState();
    }
    
    
    protected void updateDomain() {
        domain = domainEntry.getModule();
        fillMorphismType(morphismType);
    }
    
    
    protected void updateCodomain() {
        codomain = codomainEntry.getModule(); 
        fillMorphismType(morphismType);
    }

    
    protected void selectMorphismType() {
        setMorphism(null);
        jmorphismType = null;
        String selectedType = (String)morphismType.getSelectedItem();
        if (selectedType != null) {
            if (selectedType.equals(NONE_TYPE)) {
                morphismPanel.removeAll();
                pack();
            }
            else if (selectedType.equals(IDENTITY_TYPE)) {
                setMorphism(new IdentityMorphism(domain));
                setResult(morphism.toString());
            }
            else if (selectedType.equals(CANONICAL_TYPE)) {
                setMorphism(CanonicalMorphism.make(domain, codomain));
                if (morphism == null) {
                    setError(Messages.getString("JMorphismDialog.cannotcreatecanonical")); //$NON-NLS-1$
                }
                else {
                    setResult(morphism.toString());
                }                
            }
            else if (selectedType.equals(MODULO_TYPE)) {
                int dim = ((ZFreeModule)domain).getDimension();
                int mod = ((ZnFreeModule)codomain).getModulus();
                setMorphism(ModuloMorphism.make(dim, mod));
                setResult(morphism.toString());
            }
            else if (selectedType.equals(CONJUGATION_TYPE)) {
                setMorphism(new ConjugationMorphism(domain.getDimension()));
                setResult(morphism.toString());
            }
            else if (selectedType.equals(SUM_TYPE)) {
                setJMorphismType(new JSumDiffMorphismType(this, true));
            }
            else if (selectedType.equals(DIFFERENCE_TYPE)) {
                setJMorphismType(new JSumDiffMorphismType(this, false));                
            }
            else if (selectedType.equals(PRODUCT_TYPE)) {
                setJMorphismType(new JProductMorphismType(this));                
            }
            else if (selectedType.equals(POWER_TYPE)) {
                setJMorphismType(new JPowerMorphismType(this));                
            }
            else if (selectedType.equals(COMPOSITION_TYPE)) {
                setJMorphismType(new JCompositionMorphismType(this));                
            }
            else if (selectedType.equals(CONSTANT_TYPE)) {
                setJMorphismType(new JConstMorphismType(this));                
            }
            else if (selectedType.equals(SCALED_TYPE)) {
                setJMorphismType(new JScaleMorphismType(this));                
            }
            else if (selectedType.equals(TRANSLATION_TYPE)) {
                setJMorphismType(new JTranslateMorphismType(this));                
            }
            else if (selectedType.equals(POLYNOMIAL_TYPE)) {
                setJMorphismType(new JPolyMorphismType(this));                
            }
            else if (selectedType.equals(AFFINE_TYPE)) {
                setJMorphismType(new JAffineMorphismType(this));                
            }
            else if (selectedType.equals(SHUFFLE_TYPE)) {
                setJMorphismType(new JShuffleMorphismType(this));                
            }
            else if (selectedType.equals(GEOMETRY_TYPE)) {
                setJMorphismType(new JGeometryMorphismType(this));                
            }
            else if (selectedType.equals(SPLIT_TYPE)) {
                setJMorphismType(new JSplitMorphismType(this));
            }
            else {
                boolean isplugin = false;
                for (ModuleMorphismPlugin m : pluginManager.getModuleMorphismPlugins()) {
                    if (selectedType.equals(m.getName())) {
                        JMorphismType jtype = m.getJMorphismType(this);
                        if (jtype != null) {
                            setJMorphismType(jtype);
                        }
                        else {
                            setMorphism(m.getModuleMorphism(domain, codomain));
                            setResult(morphism.toString());
                        }
                        isplugin = true;
                    }
                }
                if (!isplugin) {
                    setError(Messages.getString("JMorphismDialog.nyi")); //$NON-NLS-1$
                }
            }
        }
        morphismType.grabFocus();
    }
    
    
    private void setError(String string) {
        JLabel error = new JLabel(string);
        error.setForeground(Color.RED);
        setMorphismPanel(error);
    }
    
    
    private void setResult(String string) {
        JTextField result = new JTextField(string);
        result.setEditable(false);
        result.setBackground(Color.WHITE);
        setMorphismPanel(result);
    }
    
    
    private void setMorphismPanel(JComponent c) {
        jmorphismType = null;
        morphismPanel.removeAll();
        morphismPanel.add(c);
        pack();
    }
    
    
    private void setJMorphismType(JMorphismType type) {
        setMorphismPanel(type);
        jmorphismType = type;
    }
    
    
    public static JComponent createTitle(String title) {
        Box titleBox = Box.createHorizontalBox();
        titleBox.add(Box.createHorizontalGlue());
        titleBox.add(new JLabel(title));
        titleBox.add(Box.createHorizontalGlue());
        return titleBox;
    }
    
    
    private JModuleEntry   domainEntry = null;
    private JModuleEntry   codomainEntry = null;
    private JComboBox      morphismType = null;
    private ModuleMorphism morphism = null;    
    private JPanel         morphismPanel;
    private JTextField     nameField = new JTextField();
    private JButton        clearButton;
    protected JButton      createButton;
    private JButton        cancelButton;
    private JMorphismType  jmorphismType = null;
    
    Module  domain;
    Module  codomain;
    private boolean naming;
    private boolean nameok;

    private static final Repository rep = Repository.systemRepository();
    private static final PluginManager pluginManager = PluginManager.getManager();
    
    private static final Border domainBorder =
        makeTitledBorder(Messages.getString("JMorphismDialog.domain")); //$NON-NLS-1$
    private static final Border codomainBorder =
        makeTitledBorder(Messages.getString("JMorphismDialog.codomain")); //$NON-NLS-1$
    private static final Border nameBorder =
        makeTitledBorder(Messages.getString("JMorphismDialog.name")); //$NON-NLS-1$
    private static final Border morphismTypeBorder =
        makeTitledBorder(Messages.getString("JMorphismDialog.morphismtype")); //$NON-NLS-1$
    
    private static final String AFFINE_TYPE      = Messages.getString("JMorphismDialog.affine"); //$NON-NLS-1$
    private static final String CANONICAL_TYPE   = Messages.getString("JMorphismDialog.canonical");    //$NON-NLS-1$
    private static final String COMPOSITION_TYPE = Messages.getString("JMorphismDialog.composition"); //$NON-NLS-1$
    private static final String CONJUGATION_TYPE = Messages.getString("JMorphismDialog.conjugation"); //$NON-NLS-1$
    private static final String CONSTANT_TYPE    = Messages.getString("JMorphismDialog.constant"); //$NON-NLS-1$
    private static final String DIFFERENCE_TYPE  = Messages.getString("JMorphismDialog.difference"); //$NON-NLS-1$
    private static final String GEOMETRY_TYPE    = Messages.getString("JMorphismDialog.geometry"); //$NON-NLS-1$
    private static final String IDENTITY_TYPE    = Messages.getString("JMorphismDialog.identity"); //$NON-NLS-1$
    private static final String MODULO_TYPE      = Messages.getString("JMorphismDialog.modulo"); //$NON-NLS-1$
    private static final String NONE_TYPE        = Messages.getString("JMorphismDialog.none"); //$NON-NLS-1$
    private static final String POLYNOMIAL_TYPE  = Messages.getString("JMorphismDialog.polynomial"); //$NON-NLS-1$
    private static final String POWER_TYPE       = Messages.getString("JMorphismDialog.power"); //$NON-NLS-1$
    private static final String PRODUCT_TYPE     = Messages.getString("JMorphismDialog.product"); //$NON-NLS-1$
    private static final String PROJECTION_TYPE  = Messages.getString("JMorphismDialog.projection"); //$NON-NLS-1$
    private static final String REORDER_TYPE     = Messages.getString("JMorphismDialog.reorder"); //$NON-NLS-1$
    private static final String SCALED_TYPE      = Messages.getString("JMorphismDialog.scaled"); //$NON-NLS-1$
    private static final String SHUFFLE_TYPE     = Messages.getString("JMorphismDialog.shuffle"); //$NON-NLS-1$
    private static final String SPLIT_TYPE       = Messages.getString("JMorphismDialog.split"); //$NON-NLS-1$
    private static final String SUM_TYPE         = Messages.getString("JMorphismDialog.sum"); //$NON-NLS-1$
    private static final String TRANSLATION_TYPE = Messages.getString("JMorphismDialog.translation"); //$NON-NLS-1$
}
