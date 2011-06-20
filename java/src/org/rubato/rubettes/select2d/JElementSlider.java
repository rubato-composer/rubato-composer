package org.rubato.rubettes.select2d;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.rubato.math.module.*;

public abstract class JElementSlider extends JPanel {

    public static JElementSlider make(Module module) {
        if (module == null) {
            return null;
        }
        else if (ZRing.ring.equals(module)){
            return new JZElementSlider();
        }
        else if (RRing.ring.equals(module)){
            return new JRElementSlider();
        }
        else if (QRing.ring.equals(module)){
            return new JQElementSlider();
        }
        else if (module instanceof ZnRing){
            return new JZnElementSlider(((ZnRing)module).getModulus());
        }
        else {
            return null;
        }
    }

    
    public JElementSlider() {
        createLayout();
        actionEvent = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "valueChanged");
    }

    
    public void addActionListener(ActionListener listener) {
        actionListeners.add(listener);
    }

    
    protected void fireActionPerformed() {
        for (ActionListener listener : actionListeners) {
            listener.actionPerformed(actionEvent);
        }
    }
    
    
    private void createLayout() {
        setLayout(new BorderLayout());
        slider = new JSlider();
        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                sliderUpdate();
            }
        });
        add(slider, BorderLayout.CENTER);
        
        Box leftBox = Box.createHorizontalBox();
        leftBox.add(new JLabel("min: "));
        minField = new JTextField(6);        
        minField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                minFieldUpdate();
            }
        });
        leftBox.add(minField);
        leftBox.add(Box.createHorizontalStrut(5));
        leftBox.add(new JLabel("max: "));
        maxField = new JTextField(6);
        maxField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                maxFieldUpdate();
            }
        });
        leftBox.add(maxField);
        add(leftBox, BorderLayout.WEST);
        
        Box rightBox = Box.createHorizontalBox();
        rightBox.add(new JLabel("current: "));
        currentField = new JTextField(6);
        currentField.setEditable(false);
        currentField.setBackground(Color.WHITE);
        rightBox.add(currentField);
        add(rightBox, BorderLayout.EAST);
    }
    
    
    protected abstract ModuleElement getElement();
    
    
    protected abstract void maxFieldUpdate();
    
    
    protected abstract void minFieldUpdate();
    
    
    protected abstract void sliderUpdate();

    
    protected String getMinField() {
        return minField.getText();
    }
    
    
    protected void setMinField(String s) {
        minField.setText(s);
    }
    
    
    protected String getMaxField() {
        return maxField.getText();
    }
    
    
    protected void setMaxField(String s) {
        maxField.setText(s);
    }
    
    
    protected void setCurrentField(String s) {
        currentField.setText(s);
    }
    
    
    protected void setRange(int min, int max) {
        slider.setMinimum(min);
        slider.setMaximum(max);
    }
    
    
    protected void setValue(int val) {
        slider.setValue(val);
    }
    
    
    protected int getValue() {
        return slider.getValue();
    }
    
    
    private JSlider    slider;
    private JTextField minField;
    private JTextField maxField;
    private JTextField currentField;

    private ActionEvent actionEvent;    
    private LinkedList<ActionListener> actionListeners = new LinkedList<ActionListener>();
}
