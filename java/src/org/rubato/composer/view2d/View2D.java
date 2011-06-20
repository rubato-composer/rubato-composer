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

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.EventListenerList;

import org.rubato.composer.icons.Icons;
import org.rubato.math.yoneda.Denotator;


public class View2D
        extends JPanel
        implements ComponentListener,
                   MouseListener,
                   MouseWheelListener,
                   MouseMotionListener,
                   AncestorListener {

    /**
     * Creates a new view from the given <code>model</code>.
     */
    public View2D(View2DModel model) {
        setOpaque(true);
        setBackground(BG_COLOR);
        addComponentListener(this);
        addAncestorListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);

        this.model = model;
        this.points = new ArrayList<Point>();
        
        zoomAll();
    }
    
    
    public View2DModel getModel() {
        return model;
    }
    
    
    public ArrayList<Point> getPoints() {
        return points;
    }
    
    
    /**
     * Returns true iff the point number <code>i</code> is in
     * the selection.
     */
    public boolean isSelected(int i) {
        return model.selectionsContain(points.get(i));
    }
    

    /**
     * Returns the world x-coordinate of the current pointer position.
     */
    public double getPointerX() {
        return pointerX;
    }
    
    
    /**
     * Returns the world y-coordinate of the current pointer position.
     */
    public double getPointerY() {
        return pointerY;
    }
    
    
    public void updatePointer(double worldX, double worldY) {
        pointerX = worldX;
        pointerY = worldY;
        fireActionEvent(updateEvent);
    }
    
    /**
     * Sets the viewport of the window.
     */
    public void setWindow(double xmin, double xmax, double ymin, double ymax) {
        model.setLimits(xmin, xmax, ymin, ymax);
        fireActionEvent(updateEvent);
        recalcScreenCoords();
        repaint();
    }
    

    /**
     * Zooms the viewport by the given factors.
     * Zoom factor 1.0 means identity.
     * Factors > 1.0 zoom in, factors < 1.0 zoom out.
     * @param fx horizontal zoom factor
     * @param fy vertical zoom factor
     */
    public void zoom(double fx, double fy) {
        model.zoom(fx, fy);
        fireActionEvent(updateEvent);
        recalcScreenCoords();
        repaint();
    }
    
    
    /**
     * Sets the viewport so that everything, i.e., points
     * and selections, is visible.
     */
    public void zoomAll() {
        double xmin;
        double xmax;
        double ymin;
        double ymax;
        
        if (points.size() == 0) {
            // there are no points
            // use default window
            xmin = -1.0;
            xmax =  1.0;
            ymin = -1.0;
            ymax =  1.0;
        }
        else if (points.size() == 1) {
            // there is exactly one point
            // center window on it
            Point pt = points.get(0);
            xmin = pt.realX-1.0;
            xmax = pt.realX+1.0;
            ymin = pt.realY-1.0;
            ymax = pt.realY+1.0;
        }
        else {
            // there are enough points to get a decent window
            xmin = Double.MAX_VALUE;
            xmax = Double.MIN_VALUE;
            ymin = Double.MAX_VALUE;
            ymax = Double.MIN_VALUE;
        }
        
        //
        // maxima and minima of all data and selection points
        //
        
        // data points
        for (Point pt : points) {
            xmin = Math.min(xmin, pt.realX);
            xmax = Math.max(xmax, pt.realX);
            ymin = Math.min(ymin, pt.realY);
            ymax = Math.max(ymax, pt.realY);
        }
        
        // selection points
        if (model.hasSelections()) {
            for (Selection s : model.getSelections()) {
                for (Point pt : s.getPoints()) {
                    xmin = Math.min(xmin, pt.realX);
                    xmax = Math.max(xmax, pt.realX);
                    ymin = Math.min(ymin, pt.realY);
                    ymax = Math.max(ymax, pt.realY);
                }
            }
        }
        
        // add 10% of width/height on both sides
        double dw = Math.abs(xmax-xmin)*0.1;
        double dh = Math.abs(ymax-ymin)*0.1;
        xmin -= dw;
        xmax += dw;
        ymin -= dh;
        ymax += dh;
        
        if (ymin == ymax) {
            ymin -= 1;
            ymax += 1;
        }
        if (xmin == xmax) {
            xmin -= 1;
            xmax += 1;
        }
        
        setWindow(xmin, xmax, ymin, ymax);
    }
    
    
    /**
     * Sets the current action.
     */
    public void setAction(Action2D action) {
        currentAction = action;
    }
    

    /**
     * Adds a new data point.
     * @param x world x-coordinate 
     * @param y world y-coordinate
     */
    public void addPoint(double x, double y, Denotator d) {
        Point pt = new Point(x, y, model.getWindowConfig());
        pt.setDenotator(d);
        points.add(pt);
    }
    

    /**
     * Adds an array of new data points, specified as two
     * arrays of the same length.
     * @param x array of world x-coordinates
     * @param y array of world y-coordinates
     */
    public void addPoints(double[] x, double[] y) {
        if (x.length == y.length) {
            for (int i = 0; i < x.length; i++) {
                addPoint(x[i], y[i], null);
            }
        }
    }
    
    
    /**
     * Remove all points.
     */
    public void removeAllPoints() {
        points = new ArrayList<Point>();
    }
    

    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        g2d.setColor(BG_COLOR);
        Rectangle clip = g.getClip().getBounds();
        g2d.fillRect(clip.x, clip.y, clip.width, clip.height);
        clip.grow(20, 20);
        clip.setLocation(clip.x-10, clip.y-10);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        drawBackHook(g2d);
        drawSelectionBackground(g2d);
        drawGrid(g2d);
        drawAxes(g2d);
        drawTicks(g2d);
        drawMiddleHook(g2d);
        drawPoints(g2d, clip);
        drawSelectionPoints(g2d);
        drawFrontHook(g2d);
        if (currentAction != null) {
            currentAction.draw(g2d);
        }
    }

    
    protected void drawBackHook(Graphics2D g) {}
    
    protected void drawMiddleHook(Graphics2D g) {}
    
    protected void drawFrontHook(Graphics2D g) {}

    
    /**
     * Draws axes on the view canvas.
     */
    private void drawAxes(Graphics2D g) {
        int x = model.getWindowConfig().worldToScreenX(0);
        int y = model.getWindowConfig().worldToScreenY(0);
        g.setColor(AXES_COLOR);
        if (x >= 0 && x < getWidth()) {
            g.drawLine(x, 0, x, getHeight());
        }
        if (y >= 0 && y < getWidth()) {
            g.drawLine(0, y, getWidth(), y);
        }
    }
    

    /**
     * Draws ticks on the axes.
     */
    private void drawTicks(Graphics2D g) {
        double rx = model.getWindowConfig().x_min/model.getWindowConfig().tickX;
        double sx = Math.signum(rx);
        double ax = Math.ceil(Math.abs(rx));
        double tx = sx*ax*model.getWindowConfig().tickX;

        double ry = model.getWindowConfig().y_min/model.getWindowConfig().tickY;
        double sy = Math.signum(ry);
        double ay = Math.ceil(Math.abs(ry));
        double ty = sy*ay*model.getWindowConfig().tickY;

        g.setColor(TICK_COLOR);

        int iy;
        int ix;
        
        iy = model.worldToScreenY(0.0);
        while (tx <= model.getWindowConfig().x_max) {
            ix = model.worldToScreenX(tx); 
            g.drawLine(ix, iy-4, ix, iy+4);
            tx += model.getWindowConfig().tickX;
        }
        
        ix = model.worldToScreenX(0.0);
        while (ty <= model.getWindowConfig().y_max) {
            iy = model.worldToScreenY(ty); 
            g.drawLine(ix-4, iy, ix+4, iy);
            ty += model.getWindowConfig().tickY;
        }
    }

    
    /**
     * Draws grid on the view canvas.
     */
    private void drawGrid(Graphics2D g) {
        double rx = model.getWindowConfig().x_min/model.getWindowConfig().tickX;
        double sx = Math.signum(rx);
        double ax = Math.ceil(Math.abs(rx));
        double tx = sx*ax*model.getWindowConfig().tickX;
        
        double ry = model.getWindowConfig().y_min/model.getWindowConfig().tickY;
        double sy = Math.signum(ry);
        double ay = Math.ceil(Math.abs(ry));
        double ty = sy*ay*model.getWindowConfig().tickY;
        
        g.setColor(GRID_COLOR);
        while (tx <= model.getWindowConfig().x_max) {
            double tyy = ty;
            while (tyy <= model.getWindowConfig().y_max) {
                int x = model.worldToScreenX(tx);
                int y = model.worldToScreenY(tyy);
                g.drawLine(x-GRID_SIZE/2, y, x+GRID_SIZE/2, y);
                g.drawLine(x, y-GRID_SIZE/2, x, y+GRID_SIZE/2);
                tyy += model.getWindowConfig().tickY;
            }
            tx += model.getWindowConfig().tickX;
        }
    }
    

    /**
     * Draws data and transformed points on the view canvas.
     */
    private void drawPoints(Graphics2D g, Rectangle clip) {
        for (Point pt : points) {
            if (clip.contains(pt.screenX, pt.screenY)) {
                pt.draw(g);
            }
        }
    }
    

    /**
     * Draws the background of the selections.
     */
    private void drawSelectionBackground(Graphics2D g) {
        if (model.hasSelections()) {
            for (Selection s : model.getSelections()) {
                s.drawBackground(g);
            }
        }
    }
    

    /**
     * Draws the points of the selections.
     */
    private void drawSelectionPoints(Graphics2D g) {
        if (model.hasSelections()) {
            for (Selection s : model.getSelections()) {
                s.drawPoints(g);
            }
        }
    }
    

    /**
     * Recomputes the screen coordinates from the world
     * coordinates of the data and transformed points
     * and the selections.
     */
    public void recalcScreenCoords() {
        for (Point pt : points) {
            pt.recalcScreenCoords();
        }
        if (model.hasSelections()) {
            for (Selection s : model.getSelections()) {
                s.recalcScreenCoords();
                s.refresh();
            }
        }
        recalcHook();
    }
    
    protected void recalcHook() {}


    /**
     * Starts a new selection and makes it current.
     */
    public void newSelection() {
        if (currentSelection != null) {
            currentSelection.select(false);
        }
        currentSelection = model.getNewSelection();
        currentSelection.select(true);
        repaint();
    }
    
    
    public Selection getCurrentSelection() {
        return currentSelection;
    }
    
    
    public void setCurrentSelection(Selection selection) {
        currentSelection = selection;
    }
    
    
    /**
     * Adds a new point to the current selection
     * (screen coordinates).
     * @param screenX screen x-coordinate of the new point
     * @param screenY screen y-coordinate of the new point
     */
    public void addPointToSelection(int screenX, int screenY) {
        if (currentSelection != null) {
            currentSelection.addScreenPoint(screenX, screenY);
            currentSelection.recalcScreenCoords();
            currentSelection.refresh();
            repaint(currentSelection.getBounds());
        }
    }
    
    
    /**
     * Adds a new point to the current selection
     * (world coordinates).
     * @param worldX world x-coordinate of the new point
     * @param worldY world y-coordinate of the new point
     */
    public void addPointToSelection(double worldX, double worldY) {
        if (currentSelection != null) {
            currentSelection.addRealPoint(worldX, worldY);
        }
    }
    
    
    /**
     * Removes a point from the current selection
     * (screen coordinates).
     * If the removed points was the last one, the complete
     * selection is removed.
     * @param screenX screen x-coordinate of the point to be selected
     * @param screenY screen y-coordinate of the point to be selected
     */
    public void removePointFromSelection(int screenX, int screenY) {
        if (currentSelection != null) {
            Rectangle clip = currentSelection.getBounds();
            if (currentSelection.removeScreenPoint(screenX, screenY)) {
                if (currentSelection.getPointCount() > 0) {
                    currentSelection.recalcScreenCoords();
                    currentSelection.refresh();
                }
                else {
                    model.removeSelection(currentSelection);
                    currentSelection = null;
                }
                repaint(clip);
            }
        }
    }

    
    /**
     * Draws a rectangle indicated the region to be zoomed into.
     * @param x0 screen x-coordinate of the first corner
     * @param y0 screen y-coordinate of the first corner
     * @param x1 screen x-coordinate of the second corner
     * @param y1 screen y-coordinate of the second corner
     */
    public void drawZoomRectangle(int x0, int y0, int x1, int y1) {
        Graphics2D g = (Graphics2D)getGraphics();
        g.setColor(Color.BLACK);
        g.setXORMode(Color.WHITE);
        g.setStroke(dashedStroke);
        if (x0 > x1) { int t = x0; x0 = x1; x1 = t; }
        if (y0 > y1) { int t = y0; y0 = y1; y1 = t; }
        g.drawRect(x0, y0, x1-x0, y1-y0);
    }

    
    //
    // Cursors
    //

    /**
     * Loads the cursor from the given file name and the
     * given hotpoint, and returns it.
     */
    public static Cursor loadCursor(Class<?> cls, String name, int x, int y) {
        Toolkit tk = Toolkit.getDefaultToolkit();
        Image image = Icons.loadIcon(cls, name).getImage();
        return tk.createCustomCursor(image, new java.awt.Point(x, y), name);
    }
    
    
    //
    // Listener callbacks
    //    

    public void componentResized(ComponentEvent e) {
        model.setScreenSize(getWidth(), getHeight());
        recalcScreenCoords();
        repaint();
    }

    
    public void componentMoved(ComponentEvent e) {}

    public void componentShown(ComponentEvent e) {}

    public void componentHidden(ComponentEvent e) {}

    
    /**
     * The most important event is mouse clicked. What is actually
     * done depends on the current action. 
     */
    public void mouseClicked(MouseEvent e) {
        int button = e.getButton();
        if (button == ACTION_BUT) {
            // first mouse button = action button
            if (currentAction != null) {
                currentAction.mouseClicked(e);
            }
        }
        else if (button == AUTOZOOM_BUT) {
            // third mouse button = zoom all button 
            zoomAll();
        }
        dragMode = DragMode.NONE;
    }

    
    public void mousePressed(MouseEvent e) {
        dragMode = DragMode.NONE; // reset drag mode
        int button = e.getButton();
        if (button == ACTION_BUT) {
            // current action
            if (currentAction != null) {
                currentAction.mousePressed(e);
                dragMode = DragMode.ACTION;                
            }
        }
        else if (button == PAN_BUT) {
            // begin panning
            dragMode = DragMode.PAN;
            lastX = e.getX();
            lastY = e.getY();
            tmpConfig.copy(model.getWindowConfig());
            lastCursor = getCursor();
            setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        }
    }

    
    public void mouseReleased(MouseEvent e) {
        if (currentAction != null && dragMode == DragMode.ACTION) {
            currentAction.mouseReleased(e);
        }
        else if (dragMode == DragMode.PAN) {
            // and panning and reset to previous cursor
            setCursor(lastCursor);
        }
        recalcScreenCoords();
        dragMode = DragMode.NONE;
    }


    /**
     * On mouse entering the window, the cursor must be changed
     * to reflect the current action.
     */
    public void mouseEntered(MouseEvent e) {
        // sets the cursor according to the current action
        Cursor cursor = null;
        if (dragMode == DragMode.PAN) {
            cursor = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
        }
        else {
            cursor = currentAction.getCursor();
        }
        if (cursor == null) {
            cursor = Cursor.getDefaultCursor();
        }
        setCursor(cursor);
    }
    
    
    public void mouseExited(MouseEvent e) {
        updatePointer(0, 0);
    }
        
    
    public void mouseWheelMoved(MouseWheelEvent e) {
        double zoomFactor;
        int mod = e.getModifiers();
        if (e.getWheelRotation() < 0) {
            zoomFactor = ZOOM_FACTOR;
        }
        else {
            zoomFactor = 1.0/ZOOM_FACTOR;
        }
        if ((mod & ZOOM_X_MOD) == ZOOM_X_MOD) {
            zoom(zoomFactor, 1);
        }
        else if ((mod & ZOOM_Y_MOD) == ZOOM_Y_MOD) {
            zoom(1, zoomFactor);
        }
        else {
            zoom(zoomFactor, zoomFactor);
        }
    }
    
    
    public void mouseDragged(MouseEvent e) {
        if (currentAction != null && dragMode == DragMode.ACTION) {
            currentAction.mouseDragged(e);
        }
        else if (dragMode == DragMode.PAN) {
            // panning the view
            int dx = lastX-e.getX();
            int dy = e.getY()-lastY;
            model.getWindowConfig().translate(dx, dy, tmpConfig);
            fireActionEvent(updateEvent);
            recalcScreenCoords();
            repaint();
        }
    }    

    
    /**
     * Whenever the mouse is moved, the state
     * (<code>pointerX</code>,<code>pointerY</code>) is
     * updated, and a <code>updateEvent</code> is sent.
     */
    public void mouseMoved(MouseEvent e) {
        updatePointer(model.screenToWorldX(e.getX()),
                      model.screenToWorldY(e.getY()));
    }

    
    /**
     * This is called when the component is added to
     * its parent. Until now there have been no valid
     * screen coordinates. These must be computed.
     */
    public void ancestorAdded(AncestorEvent event) {
        recalcScreenCoords();
    }
 
    public void ancestorMoved(AncestorEvent event) {}

    public void ancestorRemoved(AncestorEvent event) {}
    

    //
    // Actionlistener
    //
    
    public void addActionListener(ActionListener l) {
        listenerList.add(ActionListener.class, l);
    }
    
    public void removeActionListener(ActionListener l) {
        listenerList.remove(ActionListener.class, l);
    }
    
    protected void fireActionEvent(ActionEvent event) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i] == ActionListener.class) {
                ((ActionListener)listeners[i+1]).actionPerformed(event);
            }
        }
    }
    
    private EventListenerList listenerList = new EventListenerList();

    // action events
    public final ActionEvent updateEvent = new ActionEvent(this, 0, "update");
    public final ActionEvent newSelectionEvent = new ActionEvent(this, 0, "newselection"); 

    
    public static enum DragMode {
        NONE,
        ACTION,
        PAN
    }


    // the current action, this determines what mouse event do
    //private ActionType currentAction = ActionType.NEW_SELECTION;
    private Action2D currentAction = null;

    // the model behind this view
    private View2DModel model = null;
    
    // the data points that are displayed
    private ArrayList<Point> points = null;

    private DragMode dragMode = DragMode.NONE;
    
    private WindowConfig tmpConfig = new WindowConfig();
    private int lastX, lastY;
    private double pointerX, pointerY;
    
    // the current selection
    // this is null if there is no current selection
    private Selection currentSelection = null;
    
//    private Point  lastPoint  = null;
    private Cursor lastCursor = null;
    
    // cursors
    public Cursor addCursor    = null;
    public Cursor removeCursor = null;
    public Cursor infoCursor   = null;
    
    // the backgroung color
    private static final Color BG_COLOR    = Color.WHITE;
    // the color of the axes
    private static final Color AXES_COLOR  = Color.GRAY;
    // the color of the ticks
    private static final Color TICK_COLOR  = Color.GRAY;
    // the color of the ticks
    private static final Color GRID_COLOR  = new Color(0.0f, 0.0f, 1.0f, 0.3f);
    
    // the size of the grid crosses
    private static final int GRID_SIZE = 5;

    // the default zoom factor
    private static final double ZOOM_FACTOR = 1.2;
    
    private static final int ACTION_BUT   = MouseEvent.BUTTON1;
    private static final int PAN_BUT      = MouseEvent.BUTTON2;
    private static final int AUTOZOOM_BUT = MouseEvent.BUTTON3;
    private static final int ZOOM_X_MOD   = MouseWheelEvent.CTRL_MASK;
    private static final int ZOOM_Y_MOD   = MouseWheelEvent.SHIFT_MASK;

    private static final Stroke dashedStroke = 
        new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] {9}, 0);
}
