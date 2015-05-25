package org.rubato.rubettes.util;

import java.util.ArrayList;
import java.util.List;

public class Polygon2D {
	
	private List<Point2D> vertices;
	
	public Polygon2D() {
		this.vertices = new ArrayList<Point2D>();
	}
	
	public void addVertex(Point2D vertex) {
		this.vertices.add(vertex);
	}
	
	public List<Point2D> getVertices() {
		return this.vertices;
	}
	
}