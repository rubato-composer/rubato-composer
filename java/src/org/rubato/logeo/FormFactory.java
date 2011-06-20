/*
 * Copyright (C) 2002, 2005 Gérard Milmeister
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

package org.rubato.logeo;

import java.util.LinkedList;
import java.util.List;

import org.rubato.math.module.*;
import org.rubato.math.yoneda.*;

/**
 * Static utility class for building forms.
 * 
 * @author Gérard Milmeister
 */
public final class FormFactory {

    // Build simple forms.

    /**
     * Builds a simple form consisting of the module of integers.
     * @param name the name of the new form
     */
    public static SimpleForm makeZModuleForm(String name) {
        return new SimpleForm(NameDenotator.make(name), ZRing.ring);
    }


    /**
     * Builds a simple form consisting of the module of integers modulo p.
     * @param name the name of the new form
     * @param p the modulus of the ZnModule
     */
    public static SimpleForm makeZnModuleForm(String name, int p) {
        return new SimpleForm(NameDenotator.make(name), ZnRing.make(p));
    }


    /**
     * Builds a simple form consisting of the module of reals.
     * @param name the name of the new form
     */
    public static SimpleForm makeRModuleForm(String name) {
        return new SimpleForm(NameDenotator.make(name), RRing.ring);
    }


    /**
     * Builds a simple form consisting of the module of rationals.
     * @param name the name of the new form
     */
    public static SimpleForm makeQModuleForm(String name) {
        return new SimpleForm(NameDenotator.make(name), QRing.ring);
    }


    /**
     * Builds a simple form consisting of the module of complex numbers.
     * @param name the name of the new form
     */
    public static SimpleForm makeCModuleForm(String name) {
        return new SimpleForm(NameDenotator.make(name), CRing.ring);
    }


    /**
     * Builds a simple form consisting of the module of strings with integer
     * factors.
     * @param name the name of the new form
     */
    public static SimpleForm makeZStringModuleForm(String name) {
        return new SimpleForm(NameDenotator.make(name), ZStringRing.ring);
    }


    /**
     * Builds a simple form consisting of the indicated module.
     * @param name the name of the new form
     * @param module the module that is the base for the new form
     */
    public static SimpleForm makeModuleForm(String name, Module module) {
        return new SimpleForm(NameDenotator.make(name), module);
    }


    /**
     * Builds limit form with coordinate forms given as a list.
     * @param name the name of the new form
     * @param forms the coordinate list of forms
     */
    public static LimitForm makeLimitForm(String name, List<Form> forms) {
        return new LimitForm(NameDenotator.make(name), forms);
    }


    /**
     * Builds a limit form with several coordinate forms.
     * @param name the name of the new form
     * @param forms an array of coordinate forms
     */
    public static LimitForm makeLimitForm(String name, Form ... forms) {
        LinkedList<Form> formList = new LinkedList<Form>();
        for (Form form : forms) {
            formList.add(form);
        }
        return makeLimitForm(name, formList);
    }


    /**
     * Builds a colimit form with coordinate forms given as a list.
     * @param name the name of the new form
     * @param forms the coordinate list of forms
     */
    public static ColimitForm makeColimitForm(String name, List<Form> forms) {
        return new ColimitForm(NameDenotator.make(name), forms);
    }


    /**
     * Builds a colimit form with several coordinate forms.
     * @param name the name of the new form
     * @param forms an array of coordinate forms
     */
    public static ColimitForm makeColimitForm(String name, Form ... forms) {
        LinkedList<Form> formList = new LinkedList<Form>();
        for (Form form : forms) {
            formList.add(form);
        }
        return makeColimitForm(name, formList);
    }


    /**
     * Builds a power form with a given coordinate form.
     * @param name the name of the new form
     * @param form the single coordinate form
     */
    public static PowerForm makePowerForm(String name, Form form) {
        return new PowerForm(NameDenotator.make(name), form);
    }


    /**
     * Builds a list form with a given coordinate form.
     * @param name the name of the new form
     * @param form the single coordinate form
     */
    public static ListForm makeListForm(String name, Form form) {
        return new ListForm(NameDenotator.make(name), form);
    }
    
    
    private FormFactory() { /* pure static class */ }
}
