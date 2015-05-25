package org.rubato.rubettes.bigbang.view.subview.multitouch;

import java.util.ArrayList;

import org.mt4j.MTApplication;
import org.mt4j.components.MTComponent;
import org.mt4j.components.visibleComponents.font.FontManager;
import org.mt4j.components.visibleComponents.font.IFont;
import org.mt4j.components.visibleComponents.shapes.MTEllipse;
import org.mt4j.components.visibleComponents.shapes.MTLine;
import org.mt4j.components.visibleComponents.shapes.MTPolygon;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.components.visibleComponents.widgets.MTTextArea;
import org.mt4j.input.inputProcessors.componentProcessors.lassoProcessor.LassoProcessor;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.math.Vertex;
import org.rubato.rubettes.bigbang.view.subview.AbstractPainter;
import org.rubato.rubettes.util.Point2D;
import org.rubato.rubettes.util.Polygon2D;

public class MTPainter extends AbstractPainter {
	
	private MTApplication mtApplication;
	private MTComponent parentComponent;
	private MTColor color;
	private IFont font;
	//private LassoProcessor lassoProcessor;
	
	public MTPainter(MTApplication mtApplication, MTComponent parentComponent, LassoProcessor lassoProcessor) {
		this.mtApplication = mtApplication;
		this.parentComponent = parentComponent;
		//this.lassoProcessor = lassoProcessor;
		this.setColor(new float[]{0,0,0,1}); //black
		this.updateFont();
	}
	
	public void setComponent(MTComponent parentComponent) {
		this.parentComponent = parentComponent;
	}
	
	@Override
	public void setColor(float[] rgba) {
		this.color = new MTColor(rgba[0], rgba[1], rgba[2], rgba[3]);
	}

	@Override
	public void drawLine(double x1, double y1, double x2, double y2) {
		MTLine l = new MTLine(this.mtApplication, (float)x1, (float)y1, (float)x2, (float)y2);
		l.setStrokeColor(this.color);
		//l.addGestureListener(DragProcessor.class, new InertiaDragAction());
		this.parentComponent.addChild(l);
	}

	@Override
	public void drawOval(double x, double y, double width, double height) {
		MTEllipse e = new MTEllipse(this.mtApplication, new Vector3D((float)(x+width/2), (float)(y+height/2)), (float)width, (float)height);
		e.setStrokeColor(this.color);
		//e.addGestureListener(DragProcessor.class, new InertiaDragAction());
		this.parentComponent.addChild(e);
	}

	@Override
	public void drawPolygon(Polygon2D path) {
		MTPolygon mtP = new MTPolygon(this.extractVertices(path), this.mtApplication);
		mtP.setStrokeColor(this.color);
		mtP.setNoFill(true);
		//mtP.addGestureListener(DragProcessor.class, new InertiaDragAction());
		this.parentComponent.addChild(mtP);
	}

	@Override
	public void drawRect(double x, double y, double width, double height) {
		MTRectangle r = new MTRectangle(0, 0, (float)width, (float)height, this.mtApplication);
		r.setStrokeColor(this.color);
		r.setNoFill(true);
		//r.addGestureListener(DragProcessor.class, new InertiaDragAction());
		this.parentComponent.addChild(r);
		r.setPositionGlobal(new Vector3D((float)(x+width/2), (float)(y+height/2)));
	}

	@Override
	public void drawString(String str, double x, double y) {
		this.updateFont();
		MTTextArea textField = new MTTextArea(this.mtApplication, this.font); 
		textField.setNoStroke(true);
		textField.setNoFill(true);
		textField.setText(str);
		textField.setPositionGlobal(new Vector3D((float)x, (float)y));
		this.parentComponent.addChild(textField);
	}

	@Override
	public void fillArc(double x, double y, double width, double height, double startAngle, double arcAngle) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fillOval(double x, double y, double width, double height) {
		MTEllipse e = new MTEllipse(this.mtApplication, new Vector3D((float)(x+width/2), (float)(y+height/2)), (float)width, (float)height);
		e.setFillColor(this.color);
		//e.addGestureListener(DragProcessor.class, new InertiaDragAction());
		this.parentComponent.addChild(e);
	}

	@Override
	public void fillPolygon(Polygon2D path) {
		MTPolygon mtP = new MTPolygon(this.extractVertices(path), this.mtApplication);
		mtP.setFillColor(this.color);
		//mtP.addGestureListener(DragProcessor.class, new InertiaDragAction());
		this.parentComponent.addChild(mtP);
	}
	
	@Override
	public void fillRect(double x, double y, double width, double height) {
		this.fillRect(new MTRectangle(0, 0, (float)width, (float)height, this.mtApplication), (float)(x+width/2), (float)(y+height/2));
	}
	
	/*@Override
	public void fillNote(int x, int y, int width, int height) {
		this.fillPolygon(this.createPolygonFromRectangle(x, -y, width, height));
	}*/
	
	@Override
	public void fillObject(double x, double y, double width, double height) {
		this.fillRect(new MTRectangle(0, 0, (float)width, (float)height, this.mtApplication), x, -y);
	}
	
	private void fillRect(MTRectangle rectangle, double xPosition, double yPosition) {
		rectangle.setFillColor(this.color);
		//r.addGestureListener(DragProcessor.class, new InertiaDragAction());
		//this.lassoProcessor.addClusterable(r);
		this.parentComponent.addChild(rectangle);
		//System.out.println(xPosition + " " + yPosition + new Vector3D((float)xPosition, yPosition));
		rectangle.setPositionGlobal(new Vector3D((float)xPosition, (float)yPosition));
	}

	@Override
	public int getStringHeight(String s) {
		MTTextArea textField = new MTTextArea(this.mtApplication, this.font);
		textField.setText(s);
		//WAS DEPRECATED return Math.round(textField.getWidthXYVectLocal().length());
		return Math.round(textField.getWidthXY(null));
	}

	@Override
	public int getStringWidth(String s) {
		MTTextArea textField = new MTTextArea(this.mtApplication, this.font);
		textField.setText(s);
		//WAS DEPRECATED return Math.round(textField.getHeightXYVectLocal().length());
		return Math.round(textField.getHeightXY(null));
	}
	
	/*private Polygon createPolygonFromRectangle(int x, int y, int width, int height) {
		Polygon p = new Polygon();
		p.addPoint(x, y);
		p.addPoint(x+width, y);
		p.addPoint(x, y+height);
		p.addPoint(x+width, y+height);
		return p;
	}*/
	
	private Vertex[] extractVertices(Polygon2D polygon) {
		ArrayList<Vertex> vertices = new ArrayList<Vertex>();
		for (Point2D currentPoint : polygon.getVertices()) {
			vertices.add(new Vertex((float)currentPoint.getX(), (float)currentPoint.getY()));
		}
		return vertices.toArray(new Vertex[vertices.size()]);
	}
	
	private void updateFont() {
		if (this.font == null || !this.font.getFillColor().equals(this.color)) {
			this.font = FontManager.getInstance().createFont(this.mtApplication, "arial.ttf", 10, this.color, this.color);
		}
	}

}
