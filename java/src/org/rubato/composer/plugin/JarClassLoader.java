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

package org.rubato.composer.plugin;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.jar.Attributes;

public class JarClassLoader extends URLClassLoader {
    
    public JarClassLoader(URL url, ClassLoader loader) {
        super(new URL[] {url}, loader);
        this.url = url;
    }
    
    
    public JarClassLoader(String fileName, ClassLoader loader)
            throws IOException {
        this(toURL(fileName), loader);
    }

    
    public JarClassLoader(File file, ClassLoader loader)
            throws IOException {
        this(toURL(file), loader);
    }

    
    public String getAttribute(String name) {
        try {
            URL u = new URL("jar", "", url + "!/"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            JarURLConnection uc = (JarURLConnection)u.openConnection();
            Attributes attr = uc.getMainAttributes();
            return (attr != null)?attr.getValue(name):null;
        }
        catch (MalformedURLException e) {
            return null;
        }
        catch (IOException e) {
            return null;
        }
    }
    
    
    public String[] getListAttribute(String name) {
        String attr = getAttribute(name);
        if (attr != null) {
            String[] strings = attr.split(","); //$NON-NLS-1$
            String[] res = new String[strings.length];
            for (int i = 0; i < strings.length; i++) {
                res[i] = strings[i].trim();
            }
            return res;
        }
        else {
            return null;
        }
    }

    private static URL toURL(String fileName)
            throws IOException {
        File file = new File(fileName);
        if (!file.canRead()) {
            throw new IOException("Cannot read "+file.getCanonicalPath()); //$NON-NLS-1$
        }
        return file.toURI().toURL();
    }

    
    private static URL toURL(File file)
            throws IOException {
        if (!file.canRead()) {
            throw new IOException("Cannot read "+file.getCanonicalPath()); //$NON-NLS-1$
        }
        return file.toURI().toURL();
    }


    private URL url;
}
