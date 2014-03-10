package org.rubato.rubettes.bigbang.view.model;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JOptionPane;

import org.rubato.math.matrix.RMatrix;
import org.rubato.rubettes.bigbang.controller.BigBangController;	
import org.rubato.rubettes.bigbang.view.model.tools.SelectionTool;
import org.rubato.rubettes.bigbang.view.subview.multitouch.MTBigBangApp;
import org.rubato.rubettes.util.PointND;

public class MTBigBangView extends BigBangView {
	
	private MTBigBangApp app;
	
	public MTBigBangView(BigBangController controller) {
		super(controller);
	}
	
	@Override
	protected void initVisibleInterface() {
		//TODO: was JPanel, refactor later!!!
		//this.panel = new JBigBangPanel(this.viewController, this.);
		
		//Create our mt4j applet
        this.app = new MTBigBangApp(this.viewController, this.player);
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
		int minX = ((int) (center.x-Math.round((this.app.width/2)/this.app.getContents().getXZoomFactor())));
		int minY = ((int) ((-1*center.y)-Math.round((this.app.height/2)/this.app.getContents().getYZoomFactor())));
		//System.out.println("dy "+ center.y + " " + this.translateY(center.y) + " " + Math.round((this.app.height/2)/this.yZoomFactor));
		super.setDisplayPosition(new Point(minX, minY));
		//System.out.println("dp "+ center.toString() + " " + new Point(minX, maxY));
	}
	
	public Point getDisplayPosition() {
		return this.app.getContents().getPosition();
	}
	
	public double[] getXYDisplayValues(double[] denotatorValues) {
		return this.app.getContents().getXYDisplayValues(denotatorValues);
	}
	
	public double[] getXYZDenotatorValues(PointND location) {
		return this.app.getContents().getXYZDenotatorValues(location);
	}
	
	public double getDenotatorValue(double displayValue, int parameterIndex) {
		return this.app.getContents().getDenotatorValue(displayValue, parameterIndex);
	}
	
	public void selectNotes(SelectionTool tool, Boolean stillSelecting) {
		//System.out.println(tool.getArea() + " " + this.translateToOpenGLNoteSpaceRectangle(tool.getArea()));
		this.selectObjects(this.translateToOpenGLNoteSpaceRectangle(tool.getArea()), tool, stillSelecting);
	}
	
	@Override
	public void affineTransformSelectedObjects(Point2D.Double center, Point2D.Double endPoint, double[] shift, RMatrix transform, Boolean copyAndTransform, Boolean previewMode) {
		//center = this.translateToOpenGLPosition(center);
		center = new Point2D.Double(center.x, this.translateY(center.y));
		//System.out.println(center);
		shift[0] = shift[0]/this.app.getContents().getXZoomFactor();
		shift[1] = shift[1]/this.app.getContents().getYZoomFactor();
		//System.out.println(shift[0] + " " + shift[1] + " " + angle.doubleValue() + " " + scaleFactors[0] + " " + scaleFactors[1]);
		super.affineTransformSelectedObjects(center, endPoint, shift, transform, copyAndTransform, previewMode);
	}
	
	private Rectangle2D.Double translateToOpenGLNoteSpaceRectangle(Rectangle2D.Double r) {
		Point2D.Double location = this.translateToOpenGLNoteSpacePosition(new Point2D.Double(r.getX(), r.getY()));
		double width = r.width/this.app.getContents().getXZoomFactor();
		double height = r.height/this.app.getContents().getYZoomFactor();
		return new Rectangle2D.Double(location.x, location.y-height, width, height);
	}
	
	private Point2D.Double translateToOpenGLNoteSpacePosition(Point2D.Double p) {
		Point displayPosition = this.app.getContents().getPosition();
		double x = (p.x/this.app.getContents().getXZoomFactor())+displayPosition.x;
		double y = (displayPosition.y+((this.app.height-p.y)/this.app.getContents().getYZoomFactor()));
		return new Point2D.Double(x, y);
	}
	
	//translates the openGL y in a Swing y
	private double translateY(double y) {
		return this.app.height-y;
	}

}
