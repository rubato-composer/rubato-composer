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

import java.awt.*;

import javax.swing.*;

import org.rubato.composer.icons.Icons;


/**
 * Splash screen shown while starting the composer.
 * During startup messages can be displayed in the splash screen
 * using the setMessage() method.
 * 
 * @author Gérard Milmeister
 */
public class Splash {
    
    final static int imageWidth = 420;
    final static int imageHeight = 300;
    final static Dimension imageDim = new Dimension(imageWidth, imageHeight);
    final static ImageIcon image = Icons.splashIcon;
    
    public Splash() {
        splashFrame = new JWindow();
        splashFrame.setBackground(new Color(0.23f, 0.26f, 0.44f));
        
        JPanel splashPanel = new JPanel();
        
        splashPanel.setOpaque(false);
        splashPanel.setLayout(null);
        splashPanel.setMinimumSize(imageDim);
        splashPanel.setPreferredSize(imageDim);
        splashPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 0));

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Point centerPoint = ge.getCenterPoint();
        
        JLabel splashLabel = new JLabel(Icons.splashIcon);
        
        splashFrame.setContentPane(splashPanel);
        
        messageArea = new JLabel(Messages.getString("Splash.loading")+" Rubato Composer..."); //$NON-NLS-1$ //$NON-NLS-2$
        messageArea.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        messageArea.setForeground(new Color(0.59f, 0.67f, 0.99f));
        messageArea.setOpaque(false);

        messageAreaShadow = new JLabel(Messages.getString("Splash.loading")+" Rubato Composer..."); //$NON-NLS-1$ //$NON-NLS-2$
        messageAreaShadow.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        messageAreaShadow.setForeground(Color.BLACK);
        messageAreaShadow.setOpaque(false);

        splashPanel.add(messageArea);
        messageArea.setBounds(10, 270, 380, 20);
        splashPanel.add(messageAreaShadow);
        messageAreaShadow.setBounds(11, 271, 380, 20);
        splashPanel.add(splashLabel);
        splashLabel.setBounds(0, 0, imageWidth, imageHeight);
        
        splashFrame.pack();
        splashFrame.setLocation(centerPoint.x-(splashFrame.getWidth())/2, centerPoint.y-(splashFrame.getHeight())/2);
    }
    
    
    /**
     * Shows the splash screen.
     */
    public void showSplash() {
        splashFrame.setVisible(true);        
    }
    
    
    /**
     * Hides the splash screen.
     */    
    public void hideSplash() {
        splashFrame.setVisible(false);        
    }
    
    
    /**
     * Displays a message in the splash screen.
     */
    public void setMessage(String msg) {
        messageArea.setText(msg);
        messageAreaShadow.setText(msg);
    }

    
    private JLabel messageArea;
    private JLabel messageAreaShadow;
    private JWindow splashFrame;
}
