package org.rubato.rubettes.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeMap;

import org.rubato.base.RubatoException;
import org.rubato.math.module.DomainException;
import org.rubato.math.module.ModuleElement;
import org.rubato.math.module.ProductElement;
import org.rubato.math.module.RElement;
import org.rubato.math.module.RProperFreeElement;
import org.rubato.math.module.RProperFreeModule;
import org.rubato.math.module.RRing;
import org.rubato.math.module.RingElement;
import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.FactorDenotator;
import org.rubato.math.yoneda.Form;
import org.rubato.math.yoneda.NameDenotator;
import org.rubato.math.yoneda.SimpleDenotator;

/**
 * @author flo
 *
 */
public class ObjectGenerator {
	
	private Form baseForm;
	private DenotatorPath topPowersetPath;
	private Form topLevelObjectForm;
	
	public Denotator createEmptyScore() {
		return this.baseForm.createDefaultDenotator();
	}
	
	public boolean setBaseForm(Form baseForm) {
		if (this.isValid(baseForm)) {
			this.baseForm = baseForm;
			this.calculateTopPowersetPathAndForm();
			return true;
		}
		return false;
	}
	
	public Form getBaseForm() {
		return this.baseForm;
	}
	
	private boolean isValid(Form baseForm) {
		//TODO: do not accept strange forms!! e.g. ones without simples
		return true;
	}
	
	public DenotatorPath getTopPowersetPath() {
		return this.topPowersetPath;
	}
	
	private void calculateTopPowersetPathAndForm() {
		this.topPowersetPath = new DenotatorPath(this.baseForm).getFirstPowersetPath();
		if (this.topPowersetPath != null) {
			this.topLevelObjectForm = this.topPowersetPath.getForm().getForm(0);
		} else {
			this.topLevelObjectForm = this.baseForm;
		}
	}
	
	public Denotator createTopLevelObject(Map<DenotatorPath,Double> pathsWithValues) {
		Denotator object = this.topLevelObjectForm.createDefaultDenotator();
		for (DenotatorPath currentPath : pathsWithValues.keySet()) {
			Double currentValue = pathsWithValues.get(currentPath);
			object = this.replaceValue(object, currentPath, currentValue);
		}
		return object;
	}
	
	public Double getDoubleValue(Denotator note, DenotatorPath valuePath) {
		try {
			//TODO: rewrite!!! has to be either simple or element!!!!
			if (valuePath.isElementPath()) {
				return ((RElement)note.getElement(valuePath.toIntArray()).cast(RRing.ring)).getValue();
			} else if (valuePath.getForm().getType() == Form.SIMPLE) {
				return ((RElement)((SimpleDenotator)note.get(valuePath.toIntArray())).getElement().cast(RRing.ring)).getValue();
			}
		} catch (RubatoException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * @return if the denotator is not of the given form, the first instance of that form in the denotator's lower
	 * structure is returned. if there is none, the method checks if the object's form appears 
	 */
	public Denotator convertDenotatorIfNecessary(Denotator denotator, Form form) {
		if (denotator.getForm().equals(form)) {
			return denotator;
		}
		DenotatorPath formInDenotator = this.findFormInDenotator(form, denotator);
		DenotatorPath denotatorInForm = this.findFormInForm(denotator.getForm(), form);
		if (formInDenotator != null && (denotatorInForm == null || formInDenotator.size() <= denotatorInForm.size())) {
			try {
				return denotator.get(formInDenotator.toIntArray());
			} catch (RubatoException e) { e.printStackTrace(); }
		} else if (denotatorInForm != null) {
			return this.createDenotator(form, denotatorInForm, denotator);
		}
		return null;
	}
	
	private DenotatorPath findFormInDenotator(Form form, Denotator denotator) {
		PriorityQueue<DenotatorPath> subPathsQueue = new PriorityQueue<DenotatorPath>();
		subPathsQueue.add(new DenotatorPath(denotator.getForm()));
		while (!subPathsQueue.isEmpty()) {
			DenotatorPath currentSubPath = subPathsQueue.poll();
			try {
				Denotator currentDenotator = denotator.get(currentSubPath.toIntArray());
				Form currentForm = currentDenotator.getForm();
				if (currentForm.equals(form)) {
					return currentSubPath;
				} else if (currentForm.getType() != Form.SIMPLE && (currentForm.getType() != Form.POWER || currentForm.getType() != Form.LIST)) {
					for (int i = 0; i < ((FactorDenotator)currentDenotator).getFactorCount(); i++) {
						subPathsQueue.add(currentSubPath.getChildPath(i));
					}
				}
			} catch (RubatoException e) { e.printStackTrace(); }
		}
		return null;
	}
	
	private DenotatorPath findFormInForm(Form form, Form superForm) {
		PriorityQueue<Form> subFormQueue = new PriorityQueue<Form>();
		PriorityQueue<DenotatorPath> subPathQueue = new PriorityQueue<DenotatorPath>();
		subFormQueue.add(superForm);
		subPathQueue.add(new DenotatorPath(superForm));
		while (!subFormQueue.isEmpty()) {
			Form currentForm = subFormQueue.poll();
			DenotatorPath currentPath = subPathQueue.poll();
			if (currentForm.equals(form)) {
				return currentPath;
			} else if (currentForm.getType() != Form.SIMPLE && currentForm.getType() != Form.POWER && currentForm.getType() != Form.LIST) {
				for (int i = 0; i < currentForm.getFormCount(); i++) {
					subFormQueue.add(currentForm.getForm(i));
					subPathQueue.add(currentPath.getChildPath(i));
				}
			}
		}
		return null;
	}
	
	/*
	 * returns a new denotator of the given form where the denotator at the given path is replaced with the given
	 * denotator, if possible
	 */
	private Denotator createDenotator(Form form, DenotatorPath path, Denotator denotator) {
		Denotator defaultDenotator = form.createDefaultDenotator(); 
		try {
			return defaultDenotator.replace(path.toIntArray(), denotator);
		} catch (RubatoException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * @return a copy of object where all simples in its lower structure have been made absolute with respect to the
	 * ones also present in referenceObject. if no simple of the same form is found in referenceObject at the same
	 * path as in object, the first simple of that form found is considered. if none is found, the one in object
	 * stays the same.
	 */
	public Denotator makeObjectAbsolute(Denotator object, Denotator referenceObject) {
		Map<DenotatorPath,SimpleDenotator> objectSimples = this.findSimples(object);
		Map<DenotatorPath,SimpleDenotator> referenceSimples = this.findSimples(referenceObject);
		Map<DenotatorPath,SimpleDenotator> absoluteSimples = new TreeMap<DenotatorPath,SimpleDenotator>();
		for (DenotatorPath currentPath : objectSimples.keySet()) {
			SimpleDenotator currentSimple = objectSimples.get(currentPath);
			SimpleDenotator matchingReferenceSimple = this.getMatchingSimple(currentSimple.getForm(), currentPath, referenceSimples);
			if (matchingReferenceSimple != null) {
				absoluteSimples.put(currentPath, this.createAbsoluteSimple(currentSimple, matchingReferenceSimple));
			}
		}
		return this.replaceSimples(object, absoluteSimples);
	}
	
	/**
	 * @return a copy of object where all simples in its lower structure have been made relative of the ones also
	 * present in referenceObject. if no simple of the same form is found in referenceObject at the same path as
	 * in object, the first simple of that form found is considered. if none is found, the one in object stays
	 * absolute.
	 */
	public Denotator makeObjectRelative(Denotator object, Denotator referenceObject) {
		Map<DenotatorPath,SimpleDenotator> objectSimples = this.findSimples(object);
		Map<DenotatorPath,SimpleDenotator> referenceSimples = this.findSimples(referenceObject);
		Map<DenotatorPath,SimpleDenotator> relativeSimples = new TreeMap<DenotatorPath,SimpleDenotator>();
		for (DenotatorPath currentPath : objectSimples.keySet()) {
			SimpleDenotator currentSimple = objectSimples.get(currentPath);
			SimpleDenotator matchingReferenceSimple = this.getMatchingSimple(currentSimple.getForm(), currentPath, referenceSimples);
			if (matchingReferenceSimple != null) {
				relativeSimples.put(currentPath, this.createRelativeSimple(currentSimple, matchingReferenceSimple));
			}
		}
		return this.replaceSimples(object, relativeSimples);
	}
	
	/*
	 * returns a list of all simples found in the lower structure of the given denotator
	 */
	private Map<DenotatorPath,SimpleDenotator> findSimples(Denotator object) {
		Map<DenotatorPath,SimpleDenotator> simples = new TreeMap<DenotatorPath,SimpleDenotator>();
		PriorityQueue<DenotatorPath> subPathsQueue = new PriorityQueue<DenotatorPath>();
		subPathsQueue.add(new DenotatorPath(object.getForm()));
		while (!subPathsQueue.isEmpty()) {
			DenotatorPath currentSubPath = subPathsQueue.poll();
			try {
				Denotator currentSubObject = object.get(currentSubPath.toIntArray());
				Form currentForm = currentSubObject.getForm();
				if (currentForm.getType() == Form.SIMPLE) {
					simples.put(currentSubPath, (SimpleDenotator)currentSubObject);
				//do not search farther if form is either power or list!!
				} else if (currentForm.getType() == Form.LIMIT || currentForm.getType() == Form.COLIMIT) {
					for (int i = 0; i < ((FactorDenotator)currentSubObject).getFactorCount(); i++) {
						subPathsQueue.add(currentSubPath.getChildPath(i));
					}
				}
			} catch (RubatoException e) { e.printStackTrace(); }
		}
		return simples;
	}
	
	private Denotator replaceSimples(Denotator object, Map<DenotatorPath,SimpleDenotator> simples) {
		try {
			for (DenotatorPath currentPath : simples.keySet()) {
				object = object.replace(currentPath.toIntArray(), simples.get(currentPath));
			}
		} catch (RubatoException e) { e.printStackTrace(); }
		return object;
	}
	
	private SimpleDenotator getMatchingSimple(Form form, DenotatorPath path, Map<DenotatorPath,SimpleDenotator> simples) {
		//try at same path
		SimpleDenotator obviousCandidate = simples.get(path);
		if (obviousCandidate != null && obviousCandidate.getForm().equals(form)) {
			return obviousCandidate;
		}
		//search for other instance of same form
		for (SimpleDenotator currentSimple : simples.values()) {
			if (currentSimple.getForm().equals(form)) {
				return currentSimple;
			}
		}
		return null;
	}
	
	private SimpleDenotator createRelativeSimple(SimpleDenotator simple, SimpleDenotator reference) {
		try {
			ModuleElement differenceElement = simple.getElement().difference(reference.getElement());
			return new SimpleDenotator(NameDenotator.make(""), simple.getSimpleForm(), differenceElement);
		} catch(DomainException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private SimpleDenotator createAbsoluteSimple(SimpleDenotator simple, SimpleDenotator reference) {
		try {
			ModuleElement sumElement = simple.getElement().sum(reference.getElement());
			return new SimpleDenotator(NameDenotator.make(""), simple.getSimpleForm(), sumElement);
		} catch(DomainException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Denotator replaceValue(Denotator object, DenotatorPath valuePath, double value) {
		if (valuePath.isElementPath()) {
			int[] simplePath = valuePath.getDenotatorSubpath().toIntArray();
			int[] elementPath = valuePath.getElementSubpath().toIntArray();
			return this.replaceValue(object, simplePath, elementPath, value);
		}
		return this.replaceValue(object, valuePath.toIntArray(), value);
	}
	
	private Denotator replaceValue(Denotator object, int[] simplePath, double value) {
		try {
			SimpleDenotator oldSimple = (SimpleDenotator)object.get(simplePath);
			ModuleElement newElement = new RElement(value).cast(oldSimple.getElement().getModule());
			SimpleDenotator newSimple = new SimpleDenotator(NameDenotator.make(""), oldSimple.getSimpleForm(), newElement);
			return object.replace(simplePath, newSimple);
		} catch (RubatoException e) {
			e.printStackTrace();
			return object;
		}
	}
	
	private Denotator replaceValue(Denotator object, int[] simplePath, int[] elementPath, double value) {
		try {
			SimpleDenotator oldSimple = (SimpleDenotator)object.get(simplePath);
			ModuleElement newElement = this.createModuleElement(oldSimple.getElement(), elementPath, 0, value);
			SimpleDenotator newSimple = new SimpleDenotator(NameDenotator.make(""), oldSimple.getSimpleForm(), newElement);
			return object.replace(simplePath, newSimple);
		} catch (RubatoException e) {
			e.printStackTrace();
			return object;
		}
	}
	
	//TODO: fix things with current position!!!!!
	private ModuleElement createModuleElement(ModuleElement currentElement, int[] elementPath, int currentPosition, double value) {
		int currentIndex = elementPath[currentPosition];
		int currentDimension = currentElement.getModule().getDimension();
		if (currentElement instanceof ProductElement) {
			ProductElement productElement = (ProductElement)currentElement;
			List<RingElement> factors = new ArrayList<RingElement>();
			for (int i = 0; i < productElement.getFactorCount(); i++) {
				if (i == currentIndex) {
					factors.add((RingElement)this.createModuleElement(productElement.getFactor(i), elementPath, currentPosition, value));
				} else {
					factors.add(productElement.getFactor(i));
				}
			}
			return ProductElement.make(factors).cast(productElement.getModule());
		} else if (currentDimension > 1) {
			double[] values = ((RProperFreeElement)currentElement.cast(RProperFreeModule.make(currentDimension))).getValue();
			values[elementPath[elementPath.length-1]] = value;
			return RProperFreeElement.make(values).cast(currentElement.getModule());
		} else {
			return new RElement(value).cast(currentElement.getModule());
		}
	}

}
