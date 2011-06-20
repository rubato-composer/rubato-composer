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

package org.rubato.composer.view2d;

public final class WindowConfig {

    public double x_min;
    public double x_max;
    public double y_min;
    public double y_max;
    
    public int width;
    public int height;
    
    public double tickX = 1.0;
    public double tickY = 1.0;

    public void setLimits(double xmin, double xmax, double ymin, double ymax) {
        x_min = xmin;
        x_max = xmax;
        y_min = ymin;
        y_max = ymax;
        computeTicks();
    }
        
    
    public void setSize(int w, int h) {
        width = w;
        height = h;
        computeTicks();
    }
    
    
    public void zoom(double fx, double fy) {
        x_min = Math.abs(fx)*x_min;
        x_max = Math.abs(fx)*x_max;
        y_min = Math.abs(fy)*y_min;
        y_max = Math.abs(fy)*y_max;
        computeTicks();
    }
    
    
    public double screenToWorldX(int x) {
        return x_min+(x*(x_max-x_min)/width);
    }
    
    
    public double screenToWorldY(int y) {
        y = (height-1)-y;
        return y_min+(y*(y_max-y_min)/height);
    }
    
    
    public int worldToScreenX(double x) {
        return (int)Math.round((x-x_min)*width/(x_max-x_min));
    }

    
    public int worldToScreenY(double y) {
        int res = (int)Math.round((y-y_min)*height/(y_max-y_min));
        res = (height-1)-res;
        return res;
    }
    
    
    public void copy(WindowConfig config) {
        x_min = config.x_min;
        x_max = config.x_max;
        y_min = config.y_min;
        y_max = config.y_max;
        width = config.width;
        height = config.height;
        computeTicks();
    }
    
    
    public void translate(int dx, int dy, WindowConfig config) {
        double rdx = dx*(x_max-x_min)/width;
        double rdy = dy*(y_max-y_min)/height;
        x_min = config.x_min+rdx;
        x_max = config.x_max+rdx;
        y_min = config.y_min+rdy;
        y_max = config.y_max+rdy;
        computeTicks();
    }
    
    
    private void computeTicks() {
        double r;
        int exp;
        r = Math.abs(x_max-x_min)*MIN_TICK_SIZE/width;
        exp = (int)Math.ceil(Math.log10(r));
        tickX = Math.pow(10.0, exp);
        r = Math.abs(y_max-y_min)*MIN_TICK_SIZE/height;
        exp = (int)Math.ceil(Math.log10(r));
        tickY = Math.pow(10.0, exp);
    }
    
    
    private final static int MIN_TICK_SIZE = 30;
}
