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

package org.rubato.rubettes.image;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

import org.rubato.base.BooleanProperty;
import org.rubato.base.FileProperty;
import org.rubato.base.Repository;
import org.rubato.base.SimpleAbstractRubette;
import org.rubato.composer.RunInfo;
import org.rubato.composer.icons.Icons;
import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.PowerDenotator;
import org.rubato.math.yoneda.PowerForm;
import org.rubato.rubettes.util.DenotatorAnalyzer;
import org.rubato.rubettes.util.ObjectGenerator;

/**
 * Exports an incoming image denotator to a GIF, PNG, JPEG, BMP image file 
 * 
 * @author Florian Thalmann
 */
public class ImageFileOutRubette extends SimpleAbstractRubette {
	
	private static PowerForm IMAGE_FORM = (PowerForm)Repository.systemRepository().getForm("Image");
	
	private File imageFile;
	private boolean fillEmptyPixels;
	private final String imageFileKey = "imageFile";
	private final String fillEmptyPixelsKey = "fillEmptyPixels";
	private ObjectGenerator objectGenerator;
	
	private static final ImageIcon icon = Icons.loadIcon(ImageFileInRubette.class, "imagefileouticon.png");
	
	/**
	 * Creates a basic ImageFileInRubette.
	 */
	public ImageFileOutRubette() {
        this.setInCount(1);
        this.setOutCount(0);
        this.objectGenerator = new ObjectGenerator();
        String[] allowedExtensions = new String[]{".gif", ".png", ".jpg", ".bmp"};
        this.putProperty(new FileProperty(this.imageFileKey, "Image file", allowedExtensions, true));
        this.putProperty(new BooleanProperty(this.fillEmptyPixelsKey, "Fill empty pixels", false));
    }
	
	public void init() { }

    public void run(RunInfo runInfo) {
    	Denotator input = this.getInput(0);
    	if (this.imageFile == null) {
            this.addError("No file has been set.");
    	} else if (input == null) {
            this.addError("Input denotator is null.");
    	} else if (!input.hasForm(ImageFileOutRubette.IMAGE_FORM)) {
    		this.addError("Input denotator is not of form \"Image\".");
    	} else {
    		this.writeImageFile(this.getBufferedImage((PowerDenotator)input));
    	}
    }
    
    private BufferedImage getBufferedImage(PowerDenotator imageDenotator) {
    	double[] minAndMaxX = new DenotatorAnalyzer().getMinAndMaxValue(imageDenotator, 0);
    	double[] minAndMaxY = new DenotatorAnalyzer().getMinAndMaxValue(imageDenotator, 1);
    	int width = (int)Math.round(minAndMaxX[1]-minAndMaxX[0]+1);
    	int height = (int)Math.round(minAndMaxY[1]-minAndMaxY[0]+1);
    	BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    	for (Denotator currentPixel : imageDenotator.getFactors()) {
    		int x = (int)Math.round(this.objectGenerator.getIntegerValue(currentPixel, 0)-minAndMaxX[0]);
    		int y = (int)Math.round(this.objectGenerator.getIntegerValue(currentPixel, 1)-minAndMaxY[0]);
    		int red = this.objectGenerator.getIntegerValue(currentPixel, 2);
    		int green = this.objectGenerator.getIntegerValue(currentPixel, 3);
    		int blue = this.objectGenerator.getIntegerValue(currentPixel, 4);
    		int rgb = new Color(red, green, blue).getRGB();
    		image.setRGB(x, y, rgb);
    	}
    	return image;
    }
    
    private void writeImageFile(BufferedImage image) {
    	try {
    		String imageName = this.imageFile.getName();
    		String format = imageName.substring(imageName.length()-3);
    	    ImageIO.write(image, format, this.imageFile);
    	} catch (IOException e) {
    		addError("File %%1 could not be written.", this.imageFile.getName());
    	}
    }
	
	public JComponent getProperties() {
		return super.getProperties();
	}
	
	//has to be overriden so that the info sets!!
	@Override
	public boolean applyProperties() {
        super.applyProperties();
        this.imageFile = ((FileProperty)this.getProperty(this.imageFileKey)).getFile();
        this.fillEmptyPixels = ((BooleanProperty)this.getProperty(this.fillEmptyPixelsKey)).getBoolean();
        return true;
    }
	
	public boolean hasInfo() {
        return true;
    }
	
	public String getInfo() {
        if (this.imageFile != null) {
        	return this.imageFile.getName();
        }
        return "File not set";
    }    
    
    public String getGroup() {
        return "Image";
    }

    public String getName() {
        return "ImageFileOut";
    }
    
    public String getShortDescription() {
        return "Exports an image denotator";
    }
    
    public ImageIcon getIcon() {
        return ImageFileOutRubette.icon;
    }

    public String getLongDescription() {
        return "The ImageFileOut rubette writes an image denotator to an image file";
    }

    public String getInTip(int i) {
        return "Input Image denotator";
    }
    
}