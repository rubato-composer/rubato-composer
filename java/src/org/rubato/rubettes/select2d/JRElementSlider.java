package org.rubato.rubettes.select2d;

import org.rubato.math.module.ModuleElement;
import org.rubato.math.module.RElement;

public class JRElementSlider extends JElementSlider {

    public JRElementSlider() {
        super();
        min = -10.0;
        max =  10.0;
        setMinField(Double.toString(min));
        setMaxField(Double.toString(max));
        setRange(0, (int)MAX);
        setValue(toInteger(0));
        last = 0;
    }
    

    private double getDouble() {
        return getValue()*(max-min)/MAX+min;
    }
    
    
    private int toInteger(double v) {
        return (int)((v-min)*MAX/(max-min));
    }
    
    
    protected ModuleElement getElement() {
        return new RElement(getDouble());
    }


    protected void maxFieldUpdate() {
        double cur = getDouble();
        try {
            max = Double.parseDouble(getMaxField());
        }
        catch (NumberFormatException e) {}
        if (max <= min) { max = min+1; }
        setMaxField(Double.toString(max));
        if (cur >= max) {
            cur = max;
        }
        else if (cur <= min) {
            cur = min;
        }
        setValue(toInteger(cur));
    }


    protected void minFieldUpdate() {
        double cur = getDouble();
        try {
            min = Integer.parseInt(getMinField());
        }
        catch (NumberFormatException e) {}
        if (max <= min) { min = max-1; }
        setMinField(Double.toString(min));
        if (cur >= max) {
            cur = max;
        }
        else if (cur <= min) {
            cur = min;
        }
        setValue(toInteger(cur));
    }


    protected void sliderUpdate() {
        double cur = getDouble();
        if (cur != last) {
            setCurrentField(Double.toString(cur));
            fireActionPerformed();
            last = cur;
        }
    }
    
    
    private double min;
    private double max;
    private double last;
    
    private final static double MAX =  256;
}
