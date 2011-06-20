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

package org.rubato.xml;


/**
 * Commonly used constants for XML tags and attributes.
 * 
 * @author Gérard Milmeister
 */
@SuppressWarnings(value = { "nls" })
public interface XMLConstants {

    public static final String ROOT_ELEMENT          = "Rubato";
    public static final String DEFINE_MODULE         = "DefineModule";
    public static final String DEFINE_MODULEELEMENT  = "DefineElement";
    public static final String DEFINE_MODULEMORPHISM = "DefineModuleMorphism";
    public static final String DENOTATOR             = "Denotator";
    public static final String FORM                  = "Form";
    public static final String MODULE                = "Module";
    public static final String MODULEELEMENT         = "ModuleElement";
    public static final String MODULEMORPHISM        = "ModuleMorphism";
    public static final String MORPHISM              = "Morphism";
    public static final String MORPHISMMAP           = "MorphismMap";
    public static final String DIAGRAM               = "Diagram";
    public static final String RUBETTE               = "Rubette";
    public static final String NETWORK               = "Network";
    public static final String LINK                  = "Link";
    public static final String NOTE                  = "Note";
    public static final String SCHEME                = "Scheme";
    
    public static final String NAME_ATTR             = "name";
    public static final String CLASS_ATTR            = "class";
    public static final String REF_ATTR              = "ref";
    public static final String FORM_ATTR             = "form";
    public static final String TYPE_ATTR             = "type";
    public static final String VALUE_ATTR            = "value";
    public static final String VALUES_ATTR           = "values";
    public static final String DIMENSION_ATTR        = "dimension";
    public static final String MODULUS_ATTR          = "modulus";
    public static final String ROWS_ATTR             = "rows";
    public static final String COLUMNS_ATTR          = "columns";
    public static final String SERIAL_ATTR           = "serial";
    public static final String X_ATTR                = "x";
    public static final String Y_ATTR                = "y";
    public static final String SRC_ATTR              = "src";
    public static final String SRCPOS_ATTR           = "srcPos";
    public static final String DEST_ATTR             = "dest";
    public static final String DESTPOS_ATTR          = "destPos";
    
    public static final String TRUE_VALUE            = "true";
    public static final String FALSE_VALUE           = "false";
    
    public static final String DQUOTE                = "\"";
    public static final String QUOTE                 = "'";
    public static final String EQUALS                = "=";
    public static final String TAG_OPEN              = "<";
    public static final String TAG_CLOSE             = ">";
    public static final String SPACE                 = " ";
}
