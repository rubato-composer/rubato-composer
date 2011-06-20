/*
 * file     $RCSfile: ListView.java,v $
 * @author  $Author: milmei $
 * @version $Revision: 1.8 $ $Date: 2007/01/04 22:13:13 $ 
 *
 * this file is part of the rubato project
 *
 * copyright (c) 2002 gérard milmeister
 * department of computer science / university of zurich
 */

package org.rubato.composer.denobrowser;

import java.awt.Dimension;
import java.awt.event.*;

import javax.swing.JPanel;


/**
 * @author Gérard Milmeister
 */
@SuppressWarnings("all")
public class ListView 
	extends JPanel 
	implements MouseListener, ActionListener {

    public void setLevel(int i) {
    	level = i;
    }
    
    public int getLevel() {
    	return level;
    }
    
    public void setListViewListener(ListViewListener listener) {
    	this.listener = listener;
    }
    
    public void clear() {}
    
	public Dimension getPreferredSize() {
		return super.getPreferredSize();
	}
    
	public void mouseClicked(MouseEvent e) {}

	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e) {}

	public void mousePressed(MouseEvent e) {}

	public void mouseReleased(MouseEvent e) {}

	public void actionPerformed(ActionEvent e) {}
	
    protected ListViewListener listener;
    private int level;
}
