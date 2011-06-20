/*
 * Copyright (C) 2006 Florian Thalmann
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

package org.rubato.rubettes.wallpaper;

import java.util.List;
import java.util.ArrayList;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import org.rubato.composer.dialogs.morphisms.JGeometryView;
import org.rubato.math.module.Module;
import org.rubato.math.module.ModuleElement;
import org.rubato.math.module.ZElement;
import org.rubato.math.module.RElement;
import org.rubato.math.module.RRing;
import org.rubato.math.module.morphism.ModuleMorphism;
import org.rubato.math.module.morphism.MappingException;

/**
 * This view is similar to JGeometryView, except for that it does not just show the colored square
 * and its image, but a wallpaper that uses the square as its motif.
 * 
 * @author Florian Thalmann
 */
@SuppressWarnings("serial")
public class JWallpaperView extends JGeometryView {
	
	private JWallpaperDimensionsTable table;
	private int[] coordinatesShown = new int[] {0,1};
	private List<Double> xs, ys;
	//calculated when updating morphisms
	private double maxX, maxY, minX, minY;
	//calculated when computing bounds
	private double x_min, x_max, y_min, y_max;
	
	
	//for testing
	protected JWallpaperView() {
	}
	
	/**
	 * Creates a JWallpaperView for the wallpaper dimensions in specified table.
	 * 
	 * @param table the table, the morphisms of which are to be drawn
	 */
	public JWallpaperView(JWallpaperDimensionsTable table) {
		this.table = table;
		this.updateMorphisms();
	}
	
	/**
	 * Sets the shown x coordinate to the specified one. 
	 * 
	 * @param x - the index of the SimpleForm in the table's SimpleForm list
	 */
	public void SetXCoordinateShown(int x) {
		if (this.coordinatesShown[0] != x) {
			this.coordinatesShown[0] = x;
			this.updateMorphisms();
		}
	}
	
	/**
	 * Sets the shown y coordinate to the specified one. 
	 * 
	 * @param y - the index of the SimpleForm in the table's SimpleForm list
	 */
	public void SetYCoordinateShown(int y) {
		if (this.coordinatesShown[1] != y) {
			this.coordinatesShown[1] = y;
			this.updateMorphisms();
		}
	}
	
	private void updateMorphisms() {
		//if (this.coordinatesShownContainedBy(coordinates)) {
		int morphismCount = this.table.getMorphismCount();
		
		//describe the initial square
		List<Double> squareXs = new ArrayList<Double>();
		squareXs.add(new Double(0));
		squareXs.add(new Double(1));
		squareXs.add(new Double(1));
		squareXs.add(new Double(0));
		List<Double> squareYs = new ArrayList<Double>();
		squareYs.add(new Double(0));
		squareYs.add(new Double(0));
		squareYs.add(new Double(1));
		squareYs.add(new Double(1));
		
		for (int m = morphismCount-1; m >= 0; m--) {
			ModuleMorphism morphism = this.table.getTempMorphism(m);
			int rangeFrom = this.table.getTempRangeFrom(morphism);
			int rangeTo = this.table.getTempRangeTo(morphism);
			List<List<Integer>> coordinates = this.table.getTempCoordinates(morphism);
			Module domain = morphism.getDomain();
			int domainDim = domain.getDimension();
			List<List<Integer>> domainIndices = this.getDomainIndices(domainDim, coordinates);
			
			List<Double> originalXs, originalYs;
			if (m == morphismCount-1) {
				//first time
				originalXs = squareXs;
				originalYs = squareYs;
			} else {
				//next times
				originalXs = new ArrayList<Double>(this.xs);
				originalYs = new ArrayList<Double>(this.ys);
			}
			List<ModuleElement> originalElements = this.createDomainElements(originalXs, originalYs, domain, domainIndices);
			this.xs = new ArrayList<Double>();
			this.ys = new ArrayList<Double>();
			this.maxX = 1;
			this.maxY = 1;
			this.minY = 0;
			this.minY = 0;
			
			if (rangeFrom == 0) {
				this.xs.addAll(originalXs);
				this.ys.addAll(originalYs);
			}
			
			for (int r = 0; r <= rangeTo; r++) {
				List<ModuleElement> mappedElements = new ArrayList<ModuleElement>();
				
				try {
					for (int i = 0; i < originalElements.size(); i++) {
						mappedElements.add(morphism.map(originalElements.get(i)));
					}
				} catch (MappingException e) {
					e.printStackTrace();
				}
				
				if (r >= rangeFrom) {
					List<List<Integer>> codomainIndices = this.getCodomainIndices(domainDim, coordinates);
					List<Integer> xIndices = codomainIndices.get(0);
					List<Integer> yIndices = codomainIndices.get(1);
					
					if (xIndices.size() > 0 && yIndices.size() > 0) {
						for (int i = 0; i < xIndices.size(); i++) {
							int xIndex = xIndices.get(i);
							for (int j = 0; j < yIndices.size(); j++) {
								int yIndex = yIndices.get(j);
								//four new corners for each x/y combination
								for (int k = 0; k < originalElements.size(); k++) {
									ModuleElement currentElement = originalElements.get(k);
									double x = ((RElement)RRing.ring.cast(currentElement.getComponent(xIndex))).getValue();
									double y = ((RElement)RRing.ring.cast(currentElement.getComponent(yIndex))).getValue();
									this.addPoint(x, y);
								}
							}
						}
					} else if (xIndices.size() > 0) {
						for (int i = 0; i < xIndices.size(); i++) {
							int xIndex = xIndices.get(i);
							for (int k = 0; k < originalElements.size(); k++) {
								ModuleElement currentElement = originalElements.get(k);
								double x = ((RElement)RRing.ring.cast(currentElement.getComponent(xIndex))).getValue();
								double y = originalYs.get(k).doubleValue();
								this.addPoint(x, y);
							}
						}
					} else if (yIndices.size() > 0) {
						for (int j = 0; j < yIndices.size(); j++) {
							int yIndex = yIndices.get(j);
							for (int k = 0; k < originalElements.size(); k++) {
								ModuleElement currentElement = originalElements.get(k);
								double x = originalXs.get(k).doubleValue();
								double y = ((RElement)RRing.ring.cast(currentElement.getComponent(yIndex))).getValue();
								this.addPoint(x, y);
							}
						}
					} else {
						for (int k = 0; k < originalElements.size(); k++) {
							double x = originalXs.get(k).doubleValue();
							double y = originalYs.get(k).doubleValue();
							this.addPoint(x, y);
						}
					}
				}
				originalElements = mappedElements;
			}
		}
		this.repaint();
	}
	
	private void addPoint(double x, double y) {
		this.xs.add(new Double(x));
		this.ys.add(new Double(y));
		this.maxX = Math.max(maxX, x);
		this.maxY = Math.max(maxY, y);
		this.minX = Math.min(minX, x);
		this.minY = Math.min(minY, y);
	}
	
	private List<List<Integer>> getDomainIndices(int domainDim, List<List<Integer>> coordinates) {
		return this.getIndices(false, domainDim, coordinates);
	}
	
	private List<List<Integer>> getCodomainIndices(int domainDim, List<List<Integer>> coordinates) {
		return this.getIndices(true, domainDim, coordinates);
	}
	
	List<List<Integer>> getIndices(boolean codomain, int domainDim, List<List<Integer>> coordinates) {
		List<List<Integer>> indices = new ArrayList<List<Integer>>();
		indices.add(new ArrayList<Integer>());
		indices.add(new ArrayList<Integer>());
		int start = 0;
		int end = domainDim;
		if (codomain) {
			start = domainDim;
			end = coordinates.size();
		}
		
		for (int i = start; i < end; i++) {
			List<Integer> path = coordinates.get(i);
			int currentCoordinate = path.get(path.size()-1).intValue();
			int currentIndex = i;
			if (codomain) {
				currentIndex = currentIndex - domainDim;
			}
			if (currentCoordinate == this.coordinatesShown[0]) {
				indices.get(0).add(new Integer(currentIndex));
			} else if (currentCoordinate == this.coordinatesShown[1]) {
				indices.get(1).add(new Integer(currentIndex));
			}
		}
		return indices;
	}
	
	List<ModuleElement> createDomainElements(List<Double> xs, List<Double> ys, Module domain, List<List<Integer>> indices) {
		List<ModuleElement> elements = new ArrayList<ModuleElement>();
		
		for (int i = 0; i < xs.size(); i++) {
			int x = xs.get(i).intValue();
			int y = ys.get(i).intValue();
			elements.add(this.createCorner(x, y, domain, indices));
		}
		return elements;
	}
	
	private ModuleElement createCorner(int x, int y, Module domain, List<List<Integer>> indices) {
		ModuleElement zero = domain.getZero();
		List<Integer> xIndices = indices.get(0);
		List<Integer> yIndices = indices.get(1);
		List<ModuleElement> elements = new ArrayList<ModuleElement>();
		for (int i = 0; i < domain.getDimension(); i++) {
			ModuleElement currentComponent = zero.getComponent(i);
			if (xIndices.contains(new Integer(i))) {
				elements.add(currentComponent.getModule().cast(new ZElement(x)));
			} else if (yIndices.contains(new Integer(i))) {
				elements.add(currentComponent.getModule().cast(new ZElement(y)));
			} else {
				elements.add(currentComponent);
			}
		}
		return domain.createElement(elements);
	}
	
	protected void paintComponent(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());
        Graphics2D g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        this.drawAll(g2d);
    }
	
	private void drawAll(Graphics2D g) {
        computeBounds();
        
        //original
        int x0 = worldToScreenX(0.0);
        int y0 = worldToScreenY(0.0);
        int x1 = worldToScreenX(1.0);
        int y1 = worldToScreenY(1.0);
        g.setColor(Color.LIGHT_GRAY);
        int[] xs = { x0, x1, x1, x0 };
        int[] ys = { y0, y0, y1, y1 };
        g.fillPolygon(xs, ys, xs.length);
        g.setColor(Color.BLACK);
        g.drawPolygon(xs, ys, xs.length);
        g.drawLine(0, y0, getWidth(), y0);
        g.drawLine(x0, 0, x0, getHeight());
        for (int i = 0; i < 4; i++) {
            g.setColor(ovalColors[i]);
            g.fillOval(xs[i]-2, ys[i]-2, 5, 5);
        }
        
        //image
        for(int s = 0; s < (this.xs.size()/4); s++) {
        	xs = new int[4];
            ys = new int[4];
            for (int i = 0; i < 4; i++) {
                xs[i] = worldToScreenX(this.xs.get(4*s+i).doubleValue());
                ys[i] = worldToScreenY(this.ys.get(4*s+i).doubleValue());
            }
            double vx1 = xs[1]-xs[0];
            double vy1 = ys[1]-ys[0];
            double vx2 = xs[3]-xs[0];
            double vy2 = ys[3]-ys[0];
            if (vx1*vy2-vx2*vy1 > 0) {
                g.setColor(mirrorColor);
            }
            else {
                g.setColor(rectColor);
            }
            g.fillPolygon(xs, ys, xs.length);
            g.setColor(Color.BLACK);
            g.drawPolygon(xs, ys, xs.length);
            for (int i = 0; i < 4; i++) {
                g.setColor(ovalColors[i]);
                g.fillOval(xs[i]-3, ys[i]-3, 7, 7);
                g.drawOval(xs[i]-3, ys[i]-3, 7, 7);            
            }
        }
    }
	
	private void computeBounds() {
		x_min = this.minX;
		y_min = this.minY;
		x_max = this.maxX;
		y_max = this.maxY;
		double rx = 0.2*(x_max-x_min);
        double ry = 0.2*(y_max-y_min);
        x_max += rx;
        x_min -= rx;
        y_max += ry;
        y_min -= ry;
        double rw = (x_max-x_min)/(y_max-y_min);
        double rs = getWidth()/(double)getHeight();
        if (rw > rs) {
            double dy = (x_max-x_min)/rs;
            double edy = dy-(y_max-y_min);
            y_min -= edy/2;
            y_max += edy/2;
        }
        else if (rw < rs) {
            double dx = (y_max-y_min)*rs;
            double edx = dx-(x_max-x_min);
            x_min -= edx/2;
            x_max += edx/2;
        }
	}
	
	private int worldToScreenX(double x) {
        return (int)Math.round((x-x_min)*getWidth()/(x_max-x_min));
    }
    

    private int worldToScreenY(double y) {
        int res = (int)Math.round((y-y_min)*getHeight()/(y_max-y_min));
        res = (getHeight()-1)-res;
        return res;
    }
    
    private final static Color[] ovalColors  = new Color[] { Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW };
    private final static Color   rectColor   = new Color(0.0f, 1.0f, 0.0f, 0.5f);
    private final static Color   mirrorColor = new Color(1.0f, 0.0f, 0.0f, 0.5f);

}
