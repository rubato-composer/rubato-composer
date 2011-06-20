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
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.Timer;

import javax.sound.midi.MidiUnavailableException;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.*;

import org.rubato.audio.midi.MidiPlayer;
import org.rubato.base.AbstractRubette;
import org.rubato.base.Repository;
import org.rubato.base.Rubette;
import org.rubato.composer.RunInfo;
import org.rubato.composer.Utilities;
import org.rubato.composer.icons.Icons;
import org.rubato.math.yoneda.*;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

public final class ScorePlayRubette extends AbstractRubette implements
        ActionListener, ChangeListener {

    public ScorePlayRubette() {
        setInCount(1);
        setOutCount(0);
    }

    
    public void run(RunInfo runInfo) {
        Denotator score = getInput(0);
        if (score == null) {
            addError("Input denotator is null.");
            stop();
            if (playButton != null) {
                playButton.setEnabled(false);
            }
            midiPlayer = null;
        }
        else if (!score.hasForm(scoreForm)) {
            addError("Input denotator is not of form \"Score\".");
            stop();
            if (playButton != null) {
                playButton.setEnabled(false);
            }
            midiPlayer = null;
        }
        else {
            setScore(score);
            if (midiPlayer != null) {
                stop();
            }
            else {
                createMidiPlayer();
            }
            midiPlayer.setScore(score);
            if (playButton != null) {
                playButton.setEnabled(true);
            }
        }
    }

    
    private void createMidiPlayer() {
        if (midiPlayer == null) {
            midiPlayer = new MidiPlayer();
            midiPlayer.setStopListener(this);
        }
    }

    
    public String getGroup() {
        return "Score";
    }

    
    public String getName() {
        return "ScorePlay";
    }

    
    public Rubette duplicate() {
        ScorePlayRubette rubette = new ScorePlayRubette();
        rubette.voices = voices;
        return rubette;
    }

    
    public boolean hasView() {
        return true;
    }

    
    public JComponent getView() {
        if (view == null) {
            view = new JPanel();
            view.setLayout(new BorderLayout());
            final JPanel buttonsPanel = new JPanel();

            autoScrollBox = new JCheckBox("Autoscroll");
            autoScrollBox.setSelected(autoScroll);
            autoScrollBox.setToolTipText("Autoscroll display when playing");
            autoScrollBox.addActionListener(this);
            buttonsPanel.add(autoScrollBox);
            
            playButton = new JButton(playIcon);
            playButton.setToolTipText("Play");
            playButton.addActionListener(this);
            playButton.setEnabled(midiPlayer != null);
            buttonsPanel.add(playButton);

            pauseButton = new JButton(pauseIcon);
            pauseButton.setToolTipText("Pause");
            pauseButton.addActionListener(this);
            pauseButton.setEnabled(false);
            buttonsPanel.add(pauseButton);

            stopButton = new JButton(stopIcon);
            stopButton.setToolTipText("Stop");
            stopButton.addActionListener(this);
            stopButton.setEnabled(false);            
            buttonsPanel.add(stopButton);

            voiceButton = new JButton("Voices");
            voiceButton.setToolTipText("Assign instruments to voices");
            voiceButton.addActionListener(this);
            buttonsPanel.add(voiceButton);

            view.add(buttonsPanel, BorderLayout.NORTH);

            scoreDisplay = new JScoreDisplay(null, 0);
            scoreDisplay.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    playAt(scoreDisplay.getRealFromScreenPosition(e.getX()));
                } 
            });
            scrollPane = new JScrollPane(scoreDisplay);
            scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
            scrollPane.addAncestorListener(new AncestorListener() {
                public void ancestorAdded(AncestorEvent event) {
                    if (notes != null) {
                        scoreDisplay.setNotes(notes, duration);
                        int w = scoreDisplay.getToolkit().getScreenSize().width * 2 / 3;
                        Dimension size = scrollPane.getSize();
                        Dimension newSize = new Dimension(Math.min(size.width, w), size.height);
                        scrollPane.setPreferredSize(newSize);
                        scrollPane.setMaximumSize(newSize);
                    }
                }
                public void ancestorRemoved(AncestorEvent event) {}
                public void ancestorMoved(AncestorEvent event) {}
            });
            view.add(scrollPane, BorderLayout.CENTER);

            Box sliderPanel = new Box(BoxLayout.X_AXIS);
            sliderPanel.setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Tempo factor"));

            tempoSlider = new JSlider();
            tempoSlider.setMaximum(MAX_FACTOR);
            tempoSlider.setMinimum(-MAX_FACTOR);
            tempoSlider.setValue(0);
            tempoSlider.setMinorTickSpacing(10);
            tempoSlider.setMajorTickSpacing(MAX_FACTOR);
            tempoSlider.setPaintTicks(true);
            tempoSlider.addChangeListener(this);
            sliderPanel.add(tempoSlider);

            sliderPanel.add(Box.createHorizontalStrut(5));
            tempoLabel = new JLabel(Utilities.NULL_STRING);
            updateTempoLabel();
            sliderPanel.add(tempoLabel);

            view.add(sliderPanel, BorderLayout.SOUTH);
        }
        return view;
    }

    
    public void actionPerformed(ActionEvent event) {
        Object src = event.getSource();
        if (src == playButton) {
            play(0);
        }
        else if (src == pauseButton) {
            pause();
        }
        else if (src == stopButton) {
            stop();
        }
        else if (src == midiPlayer) {
            stop();
        }
        else if (src == voiceButton) {
            assignVoices();
        }
        else if (src == autoScrollBox) {
            autoScroll = autoScrollBox.isSelected();
        }
    }

    
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == tempoSlider) {
            updateTempoLabel();
            if (midiPlayer != null) {
                midiPlayer.setPlayTempoFactor(getTempoFactor());
            }
        }
    }

    
    private void assignVoices() {
        createMidiPlayer();
        int[] v = VoicesDialog.showDialog(view, voices);
        if (v != null) {
            voices = v;
        }
    }

    
    private void updateTempoLabel() {
        tempoLabel.setText(format.format(getTempoFactor()));
    }

    
    private float getTempoFactor() {
        if (tempoSlider != null) {
            return (float) Math.exp(TEMPO_GAMMA*(tempoSlider.getValue()/(float)MAX_FACTOR));
        }
        else {
            return 1.0f;
        }
    }

    
    private void startTimer() {
        final TimerTask task = new TimerTask() {
            public void run() {
                drawPosition();
            }
        };
        timer = new Timer();
        timer.scheduleAtFixedRate(task, 0, TIMER_PERIOD);
    }

    
    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    
    protected void playAt(double pos) {
        if (midiPlayer != null && midiPlayer.isRunning()) {
            midiPlayer.setPosition(pos);
        }
        else {
            play(pos);
        }
    }
    
    
    private void play(double pos) {
        if (midiPlayer != null) {
            setButtonsPlaying();
            try {
                midiPlayer.open();
                paused = false;
                for (int i = 0; i < voices.length; i++) {
                    midiPlayer.setProgram(i, voices[i]);
                }
                startTimer();
                if (pos < 10) {
                    resetScoreView();
                }
                scoreDisplay.play();
                midiPlayer.setPlayTempoFactor(getTempoFactor());
                midiPlayer.play();
                midiPlayer.setPosition(pos);
            }
            catch (MidiUnavailableException e) {
                midiPlayer.close();
                setButtonsStopped();
            }
        }
    }

    
    private void stop() {
        if (scoreDisplay != null) {
            setButtonsStopped();
            scoreDisplay.stop();
            resetScoreView();
        }
        paused = false;
        if (midiPlayer != null) {
            midiPlayer.close();
        }
        stopTimer();
    }

    
    private void pause() {
        if (paused) {
            setButtonsPlaying();
            midiPlayer.resume();
            paused = false;
            scoreDisplay.resume();
        }
        else {
            setButtonsPaused();
            midiPlayer.pause();
            paused = true;
            scoreDisplay.pause();
        }
    }

    
    private void setButtonsPlaying() {
        playButton.setEnabled(false);
        pauseButton.setEnabled(true);
        stopButton.setEnabled(true);
    }
    
    
    private void setButtonsStopped() {
        playButton.setEnabled(true);
        pauseButton.setEnabled(false);
        stopButton.setEnabled(false);
    }

    
    private void setButtonsPaused() {
        playButton.setEnabled(false);
        pauseButton.setEnabled(true);
        stopButton.setEnabled(false);
    }

    
    private void setScore(Denotator score) {
        int maxVoice = 0;
        List<Denotator> scoreNotes = ((FactorDenotator)score).getFactors();
        notes = new ArrayList<Note>(scoreNotes.size());
        duration = 0;
        double begin = Integer.MAX_VALUE;
        for (Denotator d : scoreNotes) {
            LimitDenotator n = (LimitDenotator) d;
            Note note = new Note(n);
            if (note.onset + note.duration > duration) {
                duration = note.onset + note.duration;
            }
            if (note.onset < begin) {
                begin = note.onset;
            }
            maxVoice = Math.max(note.voice, maxVoice);
            notes.add(note);
        }
        
        // shift all notes, so that the first note starts at 0
        for (Note n : notes) {
            n.onset -= begin;
        }
        // also shift duration
        duration -= begin;

        Collections.sort(notes);
        if (scoreDisplay != null) {
            scoreDisplay.setNotes(notes, duration);
        }
        int[] newVoices = new int[Math.max(maxVoice + 1,voices.length)];
        System.arraycopy(voices, 0, newVoices, 0, voices.length);
        voices = newVoices;
    }

    
    protected void drawPosition() {
        if (midiPlayer.isRunning()) {
            // scoreDisplay.setLatency(midiPlayer.getLatency());
            scoreDisplay.setRealPosition(midiPlayer.getPosition());

            if (autoScroll) {
                JViewport viewPort = scrollPane.getViewport();
                Rectangle viewRect = viewPort.getViewRect();
                
                int w = viewPort.getWidth();
                int sx = scoreDisplay.getScreenPosition();
                int x = sx - viewRect.x;
                int dx = w - x;
                if (dx < 30) {
                    viewPort.setViewPosition(new Point(sx - 30, 0));
                }
            }
        }
    }
    

    protected void resetScoreView() {
        JViewport viewPort = scrollPane.getViewport();
        viewPort.setViewPosition(new Point(0, 0));
    }
    
    
    public String getShortDescription() {
        return "Plays a denotator of form \"Score\"";
    }

    
    public ImageIcon getIcon() {
        return icon;
    }

    
    public String getLongDescription() {
        return "The ScorePlay Rubette plays " +
               "a denotator of form \"Score\" " +
               "using the internal MIDI sequencer.";
    }

    
    public String getInTip(int i) {
        return "Denotator of form \"Score\"";
    }

    
    static final String VOICES = "Voices";

    static final String MAP_ATTR = "map";

    public void toXML(XMLWriter writer) {
        StringBuilder buf = new StringBuilder();
        buf.append(voices[0]);
        for (int i = 1; i < voices.length; i++) {
            buf.append(",");
            buf.append(voices[i]);
        }
        writer.empty(VOICES, MAP_ATTR, buf.toString());
    }

    public Rubette fromXML(XMLReader reader, Element element) {
        ScorePlayRubette rubette = new ScorePlayRubette();
        Element child = XMLReader.getChild(element, VOICES);
        if (child != null) {
            String map = child.getAttribute(MAP_ATTR);
            String[] vcs = map.split(",");
            int[] voices0 = new int[vcs.length];
            for (int i = 0; i < voices0.length; i++) {
                try {
                    voices0[i] = Integer.parseInt(vcs[i]);
                    if (voices0[i] < 0) {
                        voices0[i] = 0;
                    }
                    else if (voices0[i] > 15) {
                        voices0[i] = 15;
                    }
                }
                catch (NumberFormatException e) {
                    voices0[i] = 0;
                }
            }            
            rubette.voices = voices0;
        }
        return rubette;
    }

    private MidiPlayer midiPlayer = null;
    
    private JPanel    view          = null;
    private JCheckBox autoScrollBox = null;
    private JButton   playButton    = null;
    private JButton   pauseButton   = null;
    private JButton   stopButton    = null;
    private JButton   voiceButton   = null;
    private JSlider   tempoSlider   = null;
    private JLabel    tempoLabel    = null;

    private boolean paused     = false;
    private boolean autoScroll = true;

    protected JScoreDisplay   scoreDisplay = null;
    protected JScrollPane     scrollPane   = null;
    protected ArrayList<Note> notes        = null;

    private Timer timer = null;
    private final static int   TIMER_PERIOD = 50; // ms
    private final static float TEMPO_GAMMA  = 2.0f;
    private final static int   MAX_FACTOR   = 100;
    
    protected double duration = 0;
    private int[] voices = new int[1];
    private static final DecimalFormat format = new DecimalFormat("0.00");
    private static final Form scoreForm;
    private static final ImageIcon icon;
    private static final ImageIcon playIcon;
    private static final ImageIcon pauseIcon;
    private static final ImageIcon stopIcon;

    static {
        Repository rep = Repository.systemRepository();
        scoreForm = rep.getForm("Score");
        playIcon  = Icons.loadIcon(ScorePlayRubette.class, "play.png");
        pauseIcon = Icons.loadIcon(ScorePlayRubette.class, "pause.png");
        stopIcon  = Icons.loadIcon(ScorePlayRubette.class, "stop.png");
        icon      = Icons.loadIcon(ScorePlayRubette.class, "scoreplayicon.png");
    }
}
