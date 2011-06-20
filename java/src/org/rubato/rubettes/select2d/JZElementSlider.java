package org.rubato.rubettes.select2d;

import org.rubato.math.module.ModuleElement;
import org.rubato.math.module.ZElement;

public class JZElementSlider extends JElementSlider {

    public JZElementSlider() {
        super();
        min = -10;
        max =  10;
        setMinField(Integer.toString(min));
        setMaxField(Integer.toString(max));
        setRange(0, MAX);
        setValue(toInteger(0));
        last = 0;
    }
    

    private int getInteger() {
        return getValue()*(max-min)/MAX+min;
    }
    
    
    private int toInteger(int v) {
        return (v-min)*MAX/(max-min);
    }
    
    
    protected ModuleElement getElement() {
        return new ZElement(getInteger());
    }


    protected void maxFieldUpdate() {
        int cur = getInteger();
        try {
            max = Integer.parseInt(getMaxField());
        }
        catch (NumberFormatException e) {}
        if (max <= min) { max = min+1; }
        setMaxField(Integer.toString(max));
        if (cur >= max) {
            cur = max;
        }
        else if (cur <= min) {
            cur = min;
        }
        setValue(toInteger(cur));
    }


    protected void minFieldUpdate() {
        int cur = getInteger();
        try {
            min = Integer.parseInt(getMinField());
        }
        catch (NumberFormatException e) {}
        if (max <= min) { min = max-1; }
        setMinField(Integer.toString(min));
        if (cur >= max) {
            cur = max;
        }
        else if (cur <= min) {
            cur = min;
        }
        setValue(toInteger(cur));
    }


    protected void sliderUpdate() {
        int cur = getInteger();
        if (cur != last) {
            setCurrentField(Integer.toString(cur));
            fireActionPerformed();
            last = cur;
        }
    }
    
    
    private int min;
    private int max;
    private int last;
    
    private final static int MAX =  256;
}
