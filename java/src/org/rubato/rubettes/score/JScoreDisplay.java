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

package org.rubato.rubettes.score;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.EventListenerList;

import org.rubato.composer.icons.Icons;

public class JScoreDisplay
        extends JPanel
        implements Scrollable {
    
    public JScoreDisplay(ArrayList<Note> notes, double duration) {
        this.notes = notes;
        this.duration = duration;
        this.width = (int)(duration*stretch);
        setOpaque(true);
        setBackground(Color.WHITE);
        if (width < VIEW_HEIGHT) { width = VIEW_HEIGHT; }
        currentSize = new Dimension(width, VIEW_HEIGHT);
        setSize(currentSize);
        setPreferredSize(currentSize);
        setMaximumSize(currentSize);
        setMinimumSize(currentSize);

        setFocusable(true);
        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                requestFocus();
            }
        });
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("PAGE_UP"), "ZoomIn");
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("PAGE_DOWN"), "ZoomOut");
        getActionMap().put("ZoomIn", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                zoomIn();
            }
        });
        getActionMap().put("ZoomOut", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                zoomOut();
            }
        });
    }

    
    public void setNotes(ArrayList<Note> notes, double duration) {
        this.notes = notes;
        this.duration = duration;
        updateSize();
    }
    

    private void updateSize() {
        this.width = (int)Math.ceil(duration*stretch);
        if (width < VIEW_HEIGHT) width = VIEW_HEIGHT;
        currentSize = new Dimension(width, getHeight());
        setSize(currentSize);
        setPreferredSize(currentSize);
        setMaximumSize(currentSize);
        setMinimumSize(currentSize);
        repaint();
    }
    
    
    public void zoomIn() {
        stretch = Math.min(128, stretch*2);
        updateSize();
    }
    
    
    public void zoomOut() {
        stretch = Math.max(2, stretch/2);
        updateSize();
    }
    
    
    public void zoomDefault() {
        stretch = DEFAULT_STRETCH;
        updateSize();
    }
    
    
    public void play() {
        setRealPosition(0);
        state = State.PLAY;
        repaint();
    }
    
    
    public void stop() {
        setRealPosition(0);
        state = State.STOP;
        repaint();
    }
    
    
    public void pause() {
        state = State.PAUSE;
        repaint();
    }
    
    
    public void resume() {
        state = State.PLAY;
        repaint();
    }
    
    
    public void setLatency(double latency) {
        this.latency = latency;
    }
    
    
    public void setRealPosition(double pos) {
        // the latency adjustement is a complete hack
        // dependent on the configuration it may be
        // wrong
        currentPos = (int)(pos*stretch-1*latency/10000.0);
        repaint();
    }

    
    public double getRealFromScreenPosition(int p) {
        return (double)p/(double)stretch;
    }
    
    
    public int getScreenPosition() {
        return currentPos;
    }
    
    
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, getHeight());
        drawPosition(g2d);
        drawNotes(g2d);
    }
    
    
    private void drawNotes(Graphics2D g) {
        if (notes == null) {
            return;
        }
        g.setColor(Color.LIGHT_GRAY);
        for (int i = 1; i < (width/(4*stretch)); i++) {
            g.drawLine(i*stretch*4, 0, i*stretch*4, getHeight());
        }
        g.setColor(Color.BLACK);
        for (Note note : notes) {
            int x = (int)(note.onset*stretch);
            int y = VIEW_HEIGHT-note.pitch_int*BAR_HEIGHT-BAR_HEIGHT;
            int w = (int)(note.duration*stretch);
            if (w < 2) { w = 2; }
            setVoiceLoudnessColor(g, note.voice, note.loudness);
            g.fillRect(x, y, w, BAR_HEIGHT-1);
        }
    }
    
    
    private void drawPosition(Graphics2D g) {
        if (state == State.PLAY || state == State.PAUSE) {
            g.setColor(Color.BLACK);
            g.drawLine(currentPos, 0, currentPos, getHeight());
            if (state == State.PLAY) {
                g.drawImage(playImage, currentPos, 0, null);
            }
            else {
                g.drawImage(pauseImage, currentPos, 0, null);
            }
        }
    }
    
    
    private void setVoiceLoudnessColor(Graphics2D g, int voice, int loudness) {
        int q = (int)((loudness/128.0)*colorCount);
        g.setColor(voiceLoudnessColors[voice%voiceCount][q]);
    }

    
    public void addActionListener(ActionListener l) {
        listenerList.add(ActionListener.class, l);
    }

    
    public void removeActionListener(ActionListener l) {
        listenerList.remove(ActionListener.class, l);
    }


    protected void fireActionEvent() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==ActionListener.class) {
                if (actionEvent == null) {
                    actionEvent = new ActionEvent(this, 0, "");
                }
                ((ActionListener)listeners[i+1]).actionPerformed(actionEvent);
            }
        }
    }

    
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 50;
    }


    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 50;
    }


    public boolean getScrollableTracksViewportWidth() {
        return false;
    }


    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    
    public Dimension getPreferredScrollableViewportSize() {
        return currentSize;
    }


    public Dimension getPreferredSize() {
        return currentSize;
    }
    
    
    public Dimension getMinimumSize() {
        return MINIMUM_SIZE;
    }
    
    
    public Dimension getMaxmumSize() {
        return MINIMUM_SIZE;
    }
    
    
    private enum State {
        STOP,
        PLAY,
        PAUSE
    }
    
    
    private State  state      = State.STOP;
    private int    currentPos = 0;
    private double latency    = 0;
    private int    stretch    = DEFAULT_STRETCH;
    private double duration   = 0;
    
    private ArrayList<Note> notes;
    private int width = 0;    
    
    private EventListenerList listenerList = new EventListenerList();
    private ActionEvent actionEvent = null;
       
    private static final int DEFAULT_STRETCH = 16;
    private static final int BAR_HEIGHT      = 4;
    private static final int VIEW_HEIGHT     = 128*BAR_HEIGHT;
    private static final Dimension MINIMUM_SIZE  = new Dimension(VIEW_HEIGHT, VIEW_HEIGHT);
    private Dimension currentSize = MINIMUM_SIZE;
    
    private static final Color[][] voiceLoudnessColors;
    private static final int colorCount = 50;
    private static final int voiceCount = 16;
    private static final float hues[] = {
        0.0f,    0.3333f, 0.6666f, 0.1666f, 0.5f,    0.8333f, 0.0833f, 0.25f,
        0.4166f, 0.5833f, 0.75f,   0.9166f, 0.4166f, 0.2083f, 0.3749f, 0.5416f
    };
    
    private static final Image playImage;
    private static final Image pauseImage;
    
    static {
        playImage  = Icons.loadIcon(ScorePlayRubette.class, "play.png").getImage();
        pauseImage = Icons.loadIcon(ScorePlayRubette.class, "pause.png").getImage();
        voiceLoudnessColors = new Color[voiceCount][];
        for (int i = 0; i < voiceCount; i++) {
            voiceLoudnessColors[i] = new Color[colorCount];
            for (int j = 0; j < colorCount; j++) {
                voiceLoudnessColors[i][j] = Color.getHSBColor(hues[i], 1.0f-0.8f/colorCount*j, 1.0f);
            }
        }
    }
}
