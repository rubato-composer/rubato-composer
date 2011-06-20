/*
 * file     $RCSfile: ListViewListener.java,v $
 * @author  $Author: milmei $
 * @version $Revision: 1.4 $ $Date: 2005/03/08 14:44:00 $ 
 *
 * this file is part of the rubato project
 *
 * copyright (c) 2002 gérard milmeister
 * department of computer science / university of zurich
 */

package org.rubato.composer.denobrowser;

import java.util.EventListener;

import org.rubato.math.yoneda.Denotator;

/**
 * @author Gérard Milmeister
 */
public interface ListViewListener extends EventListener {

	public void doubleClicked(int level, Denotator d);

	public void valueChanged(int level, Denotator d);
	
	public void addNew(int level, Denotator d);
}
