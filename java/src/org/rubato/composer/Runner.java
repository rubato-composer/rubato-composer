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

package org.rubato.composer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.rubato.base.Rubette;
import org.rubato.composer.network.NetworkModel;
import org.rubato.composer.rubette.RubetteModel;
import org.rubato.util.TextUtils;


/**
 * A Runner runs the network previously set using the method setNetwork().
 * 
 * @author Gérard Milmeister
 */
public class Runner implements Runnable {

    /**
     * Creates a new runner for the given JComposer.
     */
    public Runner(JComposer composer) {
        this.composer = composer;
        this.runInfo = new RunnerRunInfo(composer);
    }
    
    
    /**
     * Sets the network to run.
     */
    public void setNetwork(NetworkModel network) {
        this.network = network;
        dependents = network.getDependents();
    }
    
    
    /**
     * Sets a list of rubettes to run in the given network.
     */
    public void setList(NetworkModel network, ArrayList<RubetteModel> list) {
        this.network = network;
        dependents = list;
    }
    
    
    /**
     * Runs the current network.
     */
    public void run() {
        composer.resetProgressWindow(dependents.size());
        for (int i = 0; i < dependents.size(); i++) {
            if (runInfo.stopped()) { break; }
            RubetteModel model = dependents.get(i);
            Rubette rubette = model.getRubette();
            rubette.clearErrors();
            try {
                logger.info(TextUtils.replaceStrings("Running rubette %%1", model.getName())); //$NON-NLS-1$
                composer.addProgressMessage(TextUtils.replaceStrings("Running rubette %%1", model.getName()));
                if (model.isPassThrough()) {
                    rubette.setOutput(0, rubette.getInput(0));
                }
                else {
                    rubette.run(runInfo);
                    if (rubette.hasErrors()) {
                        addProblems(rubette.getErrors(), model);
                    }
                    else {
                        rubette.updateView();
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                addProblem(Messages.getString("Runner.exceptionproblem"), model); //$NON-NLS-1$
            }
            composer.makeProgress(i+1);
        }
        composer.finishRun();
    }
    

    /**
     * Adds a new problem to the list of problems.
     * @param msg the string describing the problem
     * @param model the RubetteModel where the problem occurred
     */
    public void addProblem(String msg, RubetteModel model) {
        problems.add(new Problem(msg, network.getJNetwork(), model.getJRubette()));
    }

    
    /**
     * Adds a list of new problems to the list of problems.
     * @param msgs a list of the strings describing the problems
     * @param model the RubetteModel where the problems occurred
     */
    public void addProblems(List<String> msgs, RubetteModel model) {
        for (String msg : msgs) {
            addProblem(msg, model);
        }
    }
    
    
    /**
     * Returns the list of problems.
     */
    public List<Problem> getProblems() {
        return problems;
    }


    /**
     * Begins running of the network.
     */
    public void start() {
        thread = new Thread(this);
        runInfo.reset();
        problems = new LinkedList<Problem>();
        nrClicked = 0;
        thread.start();
    }
    

    /**
     * Stops running of the network.
     */
    @SuppressWarnings("deprecation")
    public void stop() {
        if (nrClicked < 2) {
            runInfo.stop();
            nrClicked++;
        }
        else {
            // at the third click on stop button, force the thread to stop
            thread.stop();
            //composer.showProgessWindow(false);
            composer.finishRun();            
            composer.showErrorDialog("The execution of the network has been forcibly halted.");
        }
    }
    
    
    private JComposer     composer;
    private Thread        thread;
    private NetworkModel  network;
    private ArrayList<RubetteModel> dependents;
    private LinkedList<Problem> problems;    
    private RunnerRunInfo runInfo;
    private int           nrClicked = 0;     
    
    private final Logger logger = Logger.getLogger("org.rubato.composer.runner"); //$NON-NLS-1$
    
    protected class RunnerRunInfo implements RunInfo {
        
        public RunnerRunInfo(JComposer c) {
            jcomposer = c;
        }
        
        public boolean stopped() {
            return stop;
        }
        
        public void stop() {
            stop = true;
        }

        public void reset() {
            stop = false;
        }
        
        public void addMessage(RubetteModel rubette, String msg) {
            jcomposer.addProgressMessage(rubette.getName()+": "+msg);
        }
        
        private boolean stop;
        private JComposer jcomposer;
    }
}
