/*
 * Copyright (C) 2013 Florian Thalmann
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

package org.rubato.util;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class CustomFileFilter extends FileFilter {
	
	boolean directoriesAllowed;
	String[] allowedExtensions;
	
	public CustomFileFilter(boolean directoriesAllowed, String[] allowedExtensions) {
		this.directoriesAllowed = directoriesAllowed;
		this.allowedExtensions = allowedExtensions;
	}

	@Override
	public boolean accept(File file) {
		if (this.directoriesAllowed && file.isDirectory()) {
			return true;
		}
		for (String currentExtension : this.allowedExtensions) {
			if (file.getName().endsWith(currentExtension)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getDescription() {
		String description = "";
		if (this.directoriesAllowed) {
			description += "directories and ";
		}
		if (this.allowedExtensions != null && this.allowedExtensions.length > 0) {
			if (this.allowedExtensions != null && this.allowedExtensions.length > 0) {
				for (int i = 0; i < this.allowedExtensions.length-1; i++) {
					description += this.allowedExtensions[i] + ", ";
				}
				description += this.allowedExtensions[this.allowedExtensions.length-1];
			}
			
		} else {
			description += "any extension";
		}
		return description;
	}

}
