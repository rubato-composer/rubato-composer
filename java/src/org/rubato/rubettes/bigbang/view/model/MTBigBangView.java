package org.rubato.rubettes.bigbang.view.model;

import javax.swing.JOptionPane;

import org.rubato.math.matrix.RMatrix;
import org.rubato.rubettes.bigbang.controller.BigBangController;	
import org.rubato.rubettes.bigbang.view.model.tools.SelectionTool;
import org.rubato.rubettes.bigbang.view.subview.multitouch.MTBigBangApp;
import org.rubato.rubettes.util.Point;
import org.rubato.rubettes.util.Point2D;
import org.rubato.rubettes.util.PointND;
import org.rubato.rubettes.util.Rectangle2D;

public class MTBigBangView extends BigBangSwingView {
	
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
	protected void initFactsViewState() {
		this.factsViewState = new FactsViewState(this.viewController, false);
	}
	
	@Override
	public void setDisplayPosition(Point center) {
		int minX = ((int) (center.getX()-Math.round((this.app.width/2)/this.app.getContents().getXZoomFactor())));
		int minY = ((int) ((-1*center.getY())-Math.round((this.app.height/2)/this.app.getContents().getYZoomFactor())));
		//System.out.println("dy "+ center.getY() + " " + this.translateY(center.getY()) + " " + Math.round((this.app.height/2)/this.getY()ZoomFactor));
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
		return this.factsViewState.getXYZDenotatorValues(location);
	}
	
	public double getDenotatorValue(double displayValue, int parameterIndex) {
		return this.factsViewState.getDenotatorValue(displayValue, parameterIndex);
	}
	
	public void selectNotes(SelectionTool tool, Boolean stillSelecting) {
		//System.out.println(tool.getArea() + " " + this.translateToOpenGLNoteSpaceRectangle(tool.getArea()));
		this.selectObjects(this.translateToOpenGLNoteSpaceRectangle(tool.getArea()), tool, stillSelecting);
	}
	
	@Override
	public void affineTransformSelectedObjects(Point2D center, Point2D endPoint, double[] shift, RMatrix transform, Boolean copyAndTransform, Boolean previewMode) {
		//center = this.translateToOpenGLPosition(center);
		center = new Point2D(center.getX(), this.translateY(center.getY()));
		//System.out.println(center);
		shift[0] = shift[0]/this.app.getContents().getXZoomFactor();
		shift[1] = shift[1]/this.app.getContents().getYZoomFactor();
		//System.out.println(shift[0] + " " + shift[1] + " " + angleValue() + " " + scaleFactors[0] + " " + scaleFactors[1]);
		super.affineTransformSelectedObjects(center, endPoint, shift, transform, copyAndTransform, previewMode);
	}
	
	private Rectangle2D translateToOpenGLNoteSpaceRectangle(Rectangle2D r) {
		Point2D location = this.translateToOpenGLNoteSpacePosition(new Point2D(r.getX(), r.getY()));
		double width = r.getWidth()/this.app.getContents().getXZoomFactor();
		double height = r.getHeight()/this.app.getContents().getYZoomFactor();
		return new Rectangle2D(location.getX(), location.getY()-height, width, height);
	}
	
	private Point2D translateToOpenGLNoteSpacePosition(Point2D p) {
		Point displayPosition = this.app.getContents().getPosition();
		double x = (p.getX()/this.app.getContents().getXZoomFactor())+displayPosition.getX();
		double y = (displayPosition.getY()+((this.app.height-p.getY())/this.app.getContents().getYZoomFactor()));
		return new Point2D(x, y);
	}
	
	//translates the openGL y in a Swing y
	private double translateY(double y) {
		return this.app.height-y;
	}

}
