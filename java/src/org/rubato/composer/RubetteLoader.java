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

import java.util.LinkedList;
import java.util.List;

import org.rubato.base.Rubette;
import org.rubato.composer.plugin.Plugin;
import org.rubato.composer.plugin.PluginManager;
import org.rubato.util.TextUtils;

/**
 * The RubetteLoader is reponsible for loading and registering Rubettes.
 * 
 * @author Gérard Milmeister
 */
public class RubetteLoader {
    
    public RubetteLoader() { /* do nothing */ }

    
    public void setRubetteManager(RubetteManager manager) {
        rubetteManager = manager;
    }
    
    
    public void setPluginManager(PluginManager manager) {
        pluginManager = manager;
    }
    
    
    public void setSplash(Splash splash) {
        this.splash = splash;
    }
    

    /**
     * Loads all builtin Rubettes and shows a message to the splash screen.
     */
    public void loadBuiltins() {
        ClassLoader classLoader = getClass().getClassLoader();
        for (int i = 0; i < BuiltinRubettes.classes.length; i++) {
            String className = BuiltinRubettes.classes[i];
            try {
                Class<?> cls = classLoader.loadClass(className);
                Rubette rubette = (Rubette)cls.newInstance();
                String name = rubette.getName();
                splash.setMessage(Messages.getString("RubetteLoader.loading") + " " + name + " Rubette"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                rubetteManager.addRubette(className, rubette);
            }
            catch (ClassNotFoundException e) {
                setError(Messages.getString("RubetteLoader.classnotfound"), className); //$NON-NLS-1$
            }
            catch (IllegalAccessException e) {
                setError(Messages.getString("RubetteLoader.illegalaccess"), className); //$NON-NLS-1$
            }
            catch (InstantiationException e) {
                setError(Messages.getString("RubetteLoader.couldnotinst"), className);           //$NON-NLS-1$
            }
        }
    }

    
    /**
     * Loads all plugins.
     */
    public void loadPlugins() {
        pluginManager.loadPlugins();
        for (Rubette rubette : pluginManager.getRubettes()) {
            String name = rubette.getName();
            splash.setMessage(Messages.getString("RubetteLoader.loading")+" "+name+" Rubette"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            rubetteManager.addRubette(rubette);
        }
        for (Plugin plugin : pluginManager.getPlugins()) {
            String name = plugin.getClass().getCanonicalName();
            splash.setMessage(Messages.getString("RubetteLoader.loading")+" "+name); //$NON-NLS-1$ //$NON-NLS-2$;
            plugin.init();
        }
        splash.setMessage(" "); //$NON-NLS-1$
        addErrors(pluginManager.getErrors());
    }
    
    
    private void addErrors(List<String> errorList) {
        errors.addAll(errorList);
    }
    
    
    private void setError(String string, Object ... objects) {
        errors.add(TextUtils.replaceStrings(string, objects));
    }
    
    
    public List<String> getErrors() {
        return errors;
    }
    
    
    private RubetteManager rubetteManager;
    private PluginManager pluginManager;
    private Splash splash;
    private LinkedList<String> errors = new LinkedList<String>();
    
}
