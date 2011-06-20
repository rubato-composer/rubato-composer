package org.rubato.rubettes.bigbang.view.subview;

import java.awt.Color;

import org.rubato.rubettes.bigbang.view.model.DenotatorValueExtractor;

public class DisplayAxes {
	
	private DisplayContents display;
	
	public DisplayAxes(DisplayContents display) {
		this.display = display;
	}
	
	public void paint(AbstractPainter painter) {
		int currentWidth = this.display.getCurrentWidth();
		int currentHeight = this.display.getCurrentHeight();
		painter.setColor(Color.BLACK);
        //x-axis
		painter.drawLine(0, currentHeight-1, currentWidth-1, currentHeight-1);
        String xName = this.getAxisName(0);
        int xNameWidth = painter.getStringWidth(xName);
        painter.drawString(xName, currentWidth-xNameWidth-1, currentHeight-4);
        //y-axis
        painter.drawLine(0, 0, 0, currentHeight-1);
        painter.drawString(this.getAxisName(1), 2, 10);
        //draw marks
        this.drawXAxisMarks(painter, currentWidth, currentHeight, this.display.getMinVisibleX(), this.display.getMaxVisibleX());
		this.drawYAxisMarks(painter, currentHeight, this.display.getMinVisibleY(), this.display.getMaxVisibleY());
	}
	
	private void drawXAxisMarks(AbstractPainter painter, double axisLength, int currentHeight, double min, double max) {
		double interval = max-min;
		double stepSize = this.calculateMarkStepSize(axisLength, interval);
		double currentMark = Math.ceil(min/stepSize)*stepSize; 
		//System.out.println("x" + axisLength + " " + min + " " + max + " " + stepSize + " " + currentMark);
		while (currentMark <= max) {
			this.drawXAxisMark(painter, currentMark, currentHeight);
			currentMark += stepSize;
		}
	}
	
	private void drawYAxisMarks(AbstractPainter painter, double axisLength, double min, double max) {
		double interval = max-min;
		double stepSize = this.calculateMarkStepSize(axisLength, interval);
		double currentMark = Math.ceil(min/stepSize)*stepSize; 
		//System.out.println("y" + axisLength + " " + min + " " + max + " " + stepSize + " " + currentMark);
		while (currentMark <= max) {
			this.drawYAxisMark(painter, currentMark);
			currentMark += stepSize;
		}
	}
	
	private double calculateMarkStepSize(double axisLength, double visibleInterval) {
		double stepSize = 1;
		int markCount = (int) Math.floor(axisLength/50);
		while (visibleInterval/stepSize < markCount) {
			stepSize /= 10;
		}
		while (visibleInterval/stepSize > markCount) {
			stepSize *= 10;
		}
		return stepSize;
	}
	
	private void drawXAxisMark(AbstractPainter painter, double mark, int currentHeight) {
		//mark
		mark = this.removeDoubleError(mark);
		double markPosition = this.display.translateXDenotatorValue(mark);
    	painter.drawLine(markPosition, currentHeight-10, markPosition, currentHeight);
    	//mark label
    	String markString = mark+"";
    	double markStringWidth = painter.getStringWidth(markString);
    	double markStringPosition = markPosition-(markStringWidth/2);
    	painter.drawString(markString, markStringPosition, currentHeight-11);
	}
	
	private void drawYAxisMark(AbstractPainter painter, double mark) {
		//mark
		mark = this.removeDoubleError(mark);
		double markPosition = this.display.translateYDenotatorValue(mark);
		painter.drawLine(0, markPosition, 10, markPosition);
    	//mark label
    	String markString = mark+"";
    	double markStringHeight = painter.getStringHeight(markString);
    	double markStringPosition = markPosition+(markStringHeight/2)-2;
    	painter.drawString(markString, 12, markStringPosition);
	}
	
	private double removeDoubleError(double number) {
		number = Math.round(number*1000000);
		return number/1000000;
	}
	
	private String getAxisName(int i) {
		int selectedParameter = this.display.getSelectedViewParameter(i);
		if (selectedParameter >= 0) {
			return DenotatorValueExtractor.VALUE_NAMES[selectedParameter];
		}
		return "";
	}

}
