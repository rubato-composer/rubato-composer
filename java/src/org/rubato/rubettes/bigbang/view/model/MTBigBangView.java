package org.rubato.rubettes.bigbang.view.model;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.rubato.rubettes.bigbang.controller.BigBangController;	
import org.rubato.rubettes.bigbang.view.model.tools.SelectionTool;
import org.rubato.rubettes.bigbang.view.subview.multitouch.MTBigBangApp;

public class MTBigBangView extends BigBangView {
	
	private MTBigBangApp app;
	
	public MTBigBangView(BigBangController controller) {
		super(controller);
	}
	
	@Override
	protected void initVisibleInterface() {
		this.panel = new JPanel();
		
		//Create our mt4j applet
        this.app = new MTBigBangApp(this.viewController);
        this.app.frame = JOptionPane.getFrameForComponent(this.panel); //Important for registering the Windows 7 Touch input
        this.app.init();
        
        //Add MT4j applet
        this.panel.add(this.app);
	}
	
	@Override
	protected void initViewParameters() {
		this.viewParameters = new ViewParameters(viewController, false);
	}
	
	@Override
	public void setDisplayPosition(Point center) {
		int minX = ((int) (center.x-Math.round((this.app.width/2)/this.xZoomFactor)));
		int minY = ((int) ((-1*center.y)-Math.round((this.app.height/2)/this.yZoomFactor)));
		//System.out.println("dy "+ center.y + " " + this.translateY(center.y) + " " + Math.round((this.app.height/2)/this.yZoomFactor));
		super.setDisplayPosition(new Point(minX, minY));
		//System.out.println("dp "+ center.toString() + " " + new Point(minX, maxY));
	}
	
	public void selectNotes(SelectionTool tool, Boolean stillSelecting) {
		//System.out.println(tool.getArea() + " " + this.translateToOpenGLNoteSpaceRectangle(tool.getArea()));
		this.selectNotes(this.translateToOpenGLNoteSpaceRectangle(tool.getArea()), tool, stillSelecting);
	}
	
	@Override
	public void affineTransformSelectedNotes(Point2D.Double center, Point2D.Double endPoint, double[] shift, Double angle, double[] scaleFactors, Boolean copyAndTransform, Boolean previewMode) {
		//center = this.translateToOpenGLPosition(center);
		center = new Point2D.Double(center.x, this.translateY(center.y));
		//System.out.println(center);
		shift[0] = shift[0]/this.xZoomFactor;
		shift[1] = shift[1]/this.yZoomFactor;
		//System.out.println(shift[0] + " " + shift[1] + " " + angle.doubleValue() + " " + scaleFactors[0] + " " + scaleFactors[1]);
		super.affineTransformSelectedNotes(center, endPoint, shift, angle, scaleFactors, copyAndTransform, previewMode);
	}
	
	private Rectangle2D.Double translateToOpenGLNoteSpaceRectangle(Rectangle2D.Double r) {
		Point2D.Double location = this.translateToOpenGLNoteSpacePosition(new Point2D.Double(r.getX(), r.getY()));
		double width = r.width/this.xZoomFactor;
		double height = r.height/this.yZoomFactor;
		return new Rectangle2D.Double(location.x, location.y-height, width, height);
	}
	
	private Point2D.Double translateToOpenGLNoteSpacePosition(Point2D.Double p) {
		double x = (p.x/this.xZoomFactor)+this.displayPosition.x;
		double y = (this.displayPosition.y+((this.app.height-p.y)/this.yZoomFactor));
		return new Point2D.Double(x, y);
	}
	
	//translates the openGL y in a Swing y
	private double translateY(double y) {
		return this.app.height-y;
	}
	
	@Override
	protected double getDenotatorValue(double displayValue, int parameterIndex, int position, double zoomFactor) {
		double value = (displayValue/zoomFactor)+position;
		return this.viewParameters.get(parameterIndex).translateDisplayValue(value);
	}
	

}
