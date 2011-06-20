// Key-size mismatch exception supporting KDTree class

package org.rubato.rubettes.alteration.CG;

class KeyMissingException extends Exception {

    public KeyMissingException() {
	super("Key not found");
    }
}
