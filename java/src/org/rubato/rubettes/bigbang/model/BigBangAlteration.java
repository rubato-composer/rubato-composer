package org.rubato.rubettes.bigbang.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.LimitDenotator;
import org.rubato.rubettes.alteration.Alterator;
import org.rubato.rubettes.bigbang.controller.BigBangController;
import org.rubato.rubettes.util.NotePath;

public class BigBangAlteration extends Model {
	
	private boolean active;
	private List<Set<NotePath>> compositions;
	private List<Integer> selectedCoordinates;
	private double startDegree, endDegree;
	private Alterator alterator;
	
	public BigBangAlteration(BigBangController controller) {
		controller.addModel(this);
		this.reset();
		this.alterator = new Alterator();
	}
	
	public void reset() {
		this.compositions = new ArrayList<Set<NotePath>>();
		this.compositions.add(new TreeSet<NotePath>());
		this.compositions.add(new TreeSet<NotePath>());
		this.selectedCoordinates = new ArrayList<Integer>();
		for (int i = 0; i < 5; i++) {
			this.selectedCoordinates.add(i);
		}
	}
	
	public void toggleAlterationMode() {
		this.active = !this.active;
		this.fireActivity();
	}
	
	public Set<NotePath> getComposition(int index) {
		try {
			return this.compositions.get(index);
		} catch (ArrayIndexOutOfBoundsException e) {
			return new TreeSet<NotePath>();
		}
	}
	
	public void setAlterationComposition(TreeSet<NotePath> nodePaths, Integer index) {
		this.compositions.set(index, nodePaths);
		this.resetDegrees();
	}
	
	public void setAlterationCoordinates(ArrayList<Integer> selectedCoordinates) {
		this.resetDegrees();
		this.selectedCoordinates = selectedCoordinates;
		this.firePropertyChange(BigBangController.ALTERATION_COORDINATES, null, this.selectedCoordinates);
	}
	
	public void resetDegrees() {
		this.setStartDegree(new Double(0));
		this.setEndDegree(new Double(0));
	}
	
	public void setStartDegree(double value) {
		this.startDegree = value;
		this.firePropertyChange(BigBangController.FIRE_ALTERATION_COMPOSITION, null, -1);
		this.firePropertyChange(BigBangController.ALTERATION_START_DEGREE, null, value);
	}
	
	public void setEndDegree(double value) {
		this.endDegree = value;
		this.firePropertyChange(BigBangController.FIRE_ALTERATION_COMPOSITION, null, -1);
		this.firePropertyChange(BigBangController.ALTERATION_END_DEGREE, null, value);
	}
	
	public void alter(BigBangScore score) {
		if (this.compositions.get(0).size()>0 && this.compositions.get(1).size()>0) {
			boolean onlyModulators = this.selectedCoordinates.contains(5);
			List<Integer> coordinates = this.selectedCoordinates;
			if (onlyModulators) {
				coordinates = this.selectedCoordinates.subList(0, this.selectedCoordinates.size()-1);
			}
			System.out.println(onlyModulators);
			this.alterator.setCoordinates(coordinates);
			List<NotePath> c1 = new ArrayList<NotePath>(this.compositions.get(1));
			List<LimitDenotator> composition1 = score.getAbsoluteNodes(c1);
			for (LimitDenotator currentNeighbor: composition1) {
				this.alterator.addNeighbor(currentNeighbor);
			}
			List<NotePath> anchorPaths = NotePath.getParentPaths(new ArrayList<NotePath>(this.compositions.get(0)));
			List<Denotator> composition0 = this.toDenotatorList(score.removeNotes(new ArrayList<NotePath>(this.compositions.get(0))));
			List<LimitDenotator> alteredNodes = this.alterator.getSoundScoreAlteration(composition0, this.startDegree, this.endDegree, onlyModulators);
			score.addNotes(alteredNodes, anchorPaths);
		}
	}
	
	private List<Denotator> toDenotatorList(List<LimitDenotator> limitDenotators) {
		List<Denotator> denotators = new ArrayList<Denotator>();
		for (LimitDenotator currentDenotator: limitDenotators) {
			denotators.add(currentDenotator);
		}
		return denotators;
	}
	
	public void fireState() {
		this.fireActivity();
	}
	
	private void fireActivity() {
		if (this.active) {
			this.firePropertyChange(BigBangController.ENTER_ALTERATION_MODE, null, null);
		} else {
			this.firePropertyChange(BigBangController.EXIT_ALTERATION_MODE, null, null);
		}
	}

}
