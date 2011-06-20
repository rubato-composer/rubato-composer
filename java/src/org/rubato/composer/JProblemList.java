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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.rubato.composer.network.JNetwork;
import org.rubato.composer.rubette.JRubette;

public class JProblemList extends JPanel {

    public JProblemList() {
        setLayout(new BorderLayout());
        problemListModel = new ProblemListModel();
        problemList = new JList(problemListModel);
        problemList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    Problem problem = (Problem)problemList.getSelectedValue();
                    if (problem != null) {
                        problem.getJNetwork().highlight(problem.getJRubette(), true);
                    }
                }
            }
        });
        add(new JScrollPane(problemList), BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        clearButton = new JButton(Messages.getString("JProblemList.clear")); //$NON-NLS-1$
        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearProblems();
            }
        });
        buttonPanel.add(clearButton);
        
        add(buttonPanel, BorderLayout.EAST);
    }
 
    
    public void addProblems(List<Problem> problems) {
        for (Problem problem : problems) {
            problemListModel.addProblem(problem);
        }
    }
    
    
    public void addProblem(Problem problem) {
        problemListModel.addProblem(problem);
    }
    
    
    public void addProblem(String msg, JNetwork jnetwork, JRubette jrubette) {
        problemListModel.addProblem(new Problem(msg, jnetwork, jrubette));
    }
    
    
    public void clearProblems() {
        problemListModel.clearProblems();        
    }
    
    
    public void removeProblemsFor(JRubette jrubette) {
        problemListModel.removeProblemsFor(jrubette);
    }
    
    
    protected  JList            problemList;
    private    ProblemListModel problemListModel;
    private    JButton          clearButton;
    
    
    public class ProblemListModel extends DefaultListModel {
        
        public void addProblem(Problem problem) {
            addElement(problem);
        }
        
        public void clearProblems() {
            for (int i = 0; i < getSize(); i++) {
                Problem problem = (Problem)get(i);
                problem.getJNetwork().highlight(problem.getJRubette(), false);
            }
            removeAllElements();
        }
        
        public void removeProblemsFor(JRubette jrubette) {
            ArrayList<Problem> removes = new ArrayList<Problem>();
            for (int i = 0; i < getSize(); i++) {
                Problem problem = (Problem)get(i);
                if (problem.getJRubette() == jrubette) {
                    removes.add(problem);
                }
            }
            for (Problem problem : removes) {
                removeElement(problem);
            }
        }
    }
}
