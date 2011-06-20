package org.rubato.rubettes.alteration.CG;

 /**
  * KeyDuplicateException is thrown when the <TT>KDTree.insert</TT> method
  * is invoked on a key already in the KDTree.
  *
  * @author      Simon Levy
  * @version     %I%, %G%
  * @since JDK1.2 
  */

public class KeyDuplicateException extends Exception {

    protected KeyDuplicateException() {
	super("Key already in tree");
    }
}
