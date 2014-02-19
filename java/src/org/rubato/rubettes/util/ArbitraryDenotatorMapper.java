package org.rubato.rubettes.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.rubato.base.RubatoException;
import org.rubato.math.arith.Rational;
import org.rubato.math.matrix.QMatrix;
import org.rubato.math.matrix.RMatrix;
import org.rubato.math.matrix.ZMatrix;
import org.rubato.math.module.DomainException;
import org.rubato.math.module.Module;
import org.rubato.math.module.ModuleElement;
import org.rubato.math.module.ProductElement;
import org.rubato.math.module.ProductRing;
import org.rubato.math.module.QFreeModule;
import org.rubato.math.module.RFreeModule;
import org.rubato.math.module.Ring;
import org.rubato.math.module.RingElement;
import org.rubato.math.module.ZElement;
import org.rubato.math.module.morphism.CanonicalMorphism;
import org.rubato.math.module.morphism.CompositionException;
import org.rubato.math.module.morphism.ConstantMorphism;
import org.rubato.math.module.morphism.EmbeddingMorphism;
import org.rubato.math.module.morphism.GenericAffineMorphism;
import org.rubato.math.module.morphism.ModuleMorphism;
import org.rubato.math.module.morphism.ProjectionMorphism;
import org.rubato.math.module.morphism.QFreeAffineMorphism;
import org.rubato.math.module.morphism.RFreeAffineMorphism;
import org.rubato.math.module.morphism.SumMorphism;
import org.rubato.math.module.morphism.ZFreeAffineMorphism;
import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.NameDenotator;
import org.rubato.math.yoneda.PowerDenotator;
import org.rubato.math.yoneda.SimpleDenotator;
import org.rubato.math.yoneda.SimpleForm;
import org.rubato.rubettes.bigbang.model.TransformationPaths;

/**
 * 
 * @author flo
 * TODO: make everything DenotatorPath!! (adjust wallpaper rubette)
 */
public class ArbitraryDenotatorMapper {
	
	private ModuleMorphism morphism;
	private TransformationPaths transformationPaths;
	private Module domain;
	private int domainDim, codomainDim;
	private List<ModuleMorphism> injectionMorphisms;
	
	public ArbitraryDenotatorMapper(ModuleMorphism morphism, TransformationPaths paths) {
		this.transformationPaths = paths;
		this.init(morphism);
	}
	
	//define morphism-specific variables
	private void init(ModuleMorphism morphism) {
		this.morphism = morphism;
		this.domain = this.morphism.getDomain();
		this.domainDim = this.domain.getDimension();
		this.codomainDim = this.morphism.getCodomain().getDimension();
		this.injectionMorphisms = this.makeInjectionMorphisms(domain);
	}
	
	/*
	 * maps the input denotator using the input morphism and respecting the paths selected
	 * in the morphisms table. The output denotator is of the same form as the input denotator.
	 */
	public PowerDenotator getMappedPowerDenotator(PowerDenotator input) throws RubatoException {
		//prepare output
		PowerDenotator output = new PowerDenotator(NameDenotator.make(""), input.getAddress(), input.getPowerForm(), new ArrayList<Denotator>());
		
		if (this.transformationPaths != null && this.transformationPaths.getDomainDim() == this.domainDim
				&& this.transformationPaths.getCodomainDim() == this.codomainDim) {
			//iterate through the coordinates of the input and add their mapping to the output
			Iterator<Denotator> inputCoordinates = input.iterator();
			Denotator currentCoordinate; //später allgemein!!
			while (inputCoordinates.hasNext()) {
				currentCoordinate = inputCoordinates.next();
				
				Denotator mappedCoordinate = this.getMappedDenotator(currentCoordinate);
				
				if (!output.getAddress().equals(mappedCoordinate.getAddress())) {
					mappedCoordinate = mappedCoordinate.changeAddress(output.getAddress());
				}
				output.appendFactor(mappedCoordinate);
			}
		}
		return output;
	}
	
	public Denotator getMappedDenotator(Denotator denotator) throws RubatoException {
		ModuleMorphism morphism = this.morphism.compose(this.makeInitialInjectionSum(denotator));
		return this.makeFinalProjections(denotator, morphism);
	}
	
	/*
	 * adapt the morphism of every simple denotator in morphismPaths to the main morphism
	 * by composing it with an injection
	 */
	private ModuleMorphism makeInitialInjectionSum(Denotator denotator) throws RubatoException {
		ModuleMorphism injectionSum = null;
		for (int j = 0; j < this.domainDim; j++) {
			
			//TODO: mapping relatively easy for now since we're already doing powersets element by element..
			
			DenotatorPath currentPath = this.transformationPaths.getDomainPath(j, denotator);
			
			if (currentPath != null) {
				ModuleMorphism currentMorphism = null;
				if (currentPath.isElementPath()) {
					currentMorphism = this.makeInitialProjection(denotator, currentPath);
				} else {
					SimpleDenotator currentSimple = this.getSimpleDenotator(denotator, currentPath.toIntArray());
					if (currentSimple != null) {
						currentMorphism = currentSimple.getModuleMorphism();
					}
				}
				
				if (currentMorphism != null) {
					//give the current morphism a new codomain 
					Module newCodomain = this.domain.getComponentModule(j);
					currentMorphism = this.getCastedMorphism(currentMorphism, newCodomain);
					//inject into domain of main morphism
					currentMorphism = this.injectionMorphisms.get(j).compose(currentMorphism);
					//sum all morphisms
					if (injectionSum == null) {
						injectionSum = currentMorphism;
					} else {
						injectionSum = injectionSum.sum(currentMorphism);
					}
				}
			}
		}
		return injectionSum;
	}
	
	private ModuleMorphism makeInitialProjection(Denotator denotator, DenotatorPath path) throws RubatoException {
		SimpleDenotator simple = this.getSimpleDenotator(denotator, path.getDenotatorSubpath().toIntArray());
		ModuleMorphism currentMorphism = simple.getModuleMorphism();
		ModuleElement currentElement = simple.getElement();
		DenotatorPath elementPath = path.getElementSubpath();
		for (int currentIndex : elementPath.toIntArray()) {
			if (currentElement instanceof ProductElement) {
				currentMorphism = this.makeProjection(currentMorphism, ((ProductElement)currentElement).getFactorCount(), currentIndex);
				currentElement = ((ProductElement)currentElement).getFactor(currentIndex);
			} else {
				currentMorphism = this.makeProjection(currentMorphism, currentElement.getModule().getDimension(), currentIndex);
				currentElement = currentElement.getComponent(currentIndex);
			}
		}
		return currentMorphism;
	}
	
	private Denotator makeFinalProjections(Denotator mappedDenotator, ModuleMorphism m) throws RubatoException {
		for (int i = 0; i < this.codomainDim; i++) {
			ModuleMorphism projectedM = m;
			
			//make projection if necessary
			if (this.codomainDim > 1) {
				projectedM = this.makeProjection(m, this.codomainDim, i);
			}
			
			//replace original coordinate by mapped coordinate 
			DenotatorPath currentCodomainPath = this.transformationPaths.getCodomainPath(i, mappedDenotator);
			
			if (currentCodomainPath != null) {
				//currentCodomainPath = currentCodomainPath.neutralizeColimitIndices();
				SimpleDenotator oldSimple;
				Module newCodomain = null;
				if (currentCodomainPath.isElementPath()) {
					oldSimple = this.getSimpleDenotator(mappedDenotator, currentCodomainPath.getDenotatorSubpath().toIntArray());
					newCodomain = this.getElement(oldSimple, currentCodomainPath.getElementSubpath()).getModule();
				} else {
					oldSimple = this.getSimpleDenotator(mappedDenotator, currentCodomainPath.toIntArray());
					if (oldSimple != null) {
						newCodomain = oldSimple.getModuleMorphism().getCodomain();
					}
				}
				
				if (newCodomain != null) {
					projectedM = this.getCastedMorphism(projectedM, newCodomain);
					
					if (currentCodomainPath.isElementPath()) {
						//ModuleMorphism finalInjection = this.makeFinalInjection(oldSimple, this.denotatorPaths.get(this.domainDim + i));
						int dimension = oldSimple.getElement().getModule().getDimension();
						if (oldSimple.getElement().getModule() instanceof ProductRing) {
							dimension = ((ProductRing)oldSimple.getElement().getModule()).getFactorCount();
						}
						ModuleMorphism sum = null;
						for (int j = 0; j < dimension; j++) {
							ModuleMorphism currentAddend;
							if (j == currentCodomainPath.getLastIndex()) {
								currentAddend = this.makeFinalInjection(oldSimple, currentCodomainPath).compose(projectedM);
							} else {
								DenotatorPath replacedPath = currentCodomainPath.replaceLast(j);
								currentAddend = this.makeInitialProjection(mappedDenotator, replacedPath);
								currentAddend = this.makeFinalInjection(oldSimple, replacedPath).compose(currentAddend);
							}
						
							if (sum != null) {
								sum = SumMorphism.make(sum, currentAddend);
							} else {
								sum = currentAddend;
							}
						}
						
						projectedM = sum;
					}
						
					SimpleForm currentForm = (SimpleForm)oldSimple.getForm();
					try {
						Denotator currentSimpleDenotator;
						//strange that null-addressed denotator yields constantmorphism
						if (oldSimple.getAddress().isNullModule()) {
							currentSimpleDenotator = new SimpleDenotator(NameDenotator.make(""), currentForm, (((ConstantMorphism)projectedM).getValue()));
						} else {
							currentSimpleDenotator = new SimpleDenotator(NameDenotator.make(""), currentForm, projectedM);
						}
						if (currentCodomainPath.size() == 0) {
							mappedDenotator = currentSimpleDenotator;
						} else if (this.transformationPaths != null && currentCodomainPath.isElementPath()) {
							mappedDenotator = mappedDenotator.replace(currentCodomainPath.getDenotatorSubpath().toIntArray(), currentSimpleDenotator);
						} else {
							mappedDenotator = mappedDenotator.replace(currentCodomainPath.toIntArray(), currentSimpleDenotator);
						}
					} catch (DomainException e) {
						e.printStackTrace();
					}
				}
				
			}
		}
		
		return mappedDenotator;
	}
	
	//TODO: put all these somewhere else
	private ModuleElement getElement(SimpleDenotator denotator, DenotatorPath elementPath) {
		ModuleElement currentElement = denotator.getElement();
		for (int currentIndex : elementPath.toIntArray()) {
			if (currentElement instanceof ProductElement) {
				currentElement = ((ProductElement)currentElement).getFactor(currentIndex);
			} else {
				currentElement = currentElement.getComponent(currentIndex);
			}
		}
		return currentElement; 
	}
	
	private ModuleMorphism makeFinalInjection(SimpleDenotator simple, DenotatorPath path) throws CompositionException {
		Module currentModule = simple.getElement().getModule();
		ModuleMorphism finalInjection = null;
		for (int currentIndex : path.toIntArray()) {
			ModuleMorphism currentInjection = this.makeInjectionMorphism(currentModule.getRing(), currentModule.getDimension(), currentIndex);
			if (finalInjection != null) {
				finalInjection = currentInjection.compose(finalInjection);
			} else {
				finalInjection = currentInjection;
			}
			if (currentModule instanceof ProductRing) {
				currentModule = ((ProductRing)currentModule).getFactor(currentIndex);
			} else {
				currentModule = currentModule.getComponentModule(currentIndex);
			}
		}
		return finalInjection;
	}
	
	/*
	 * returns the simple denotator along the path within the specified denotator.
	 * also handles the empty path [] in case the denotator itself is of type simple
	 */
	//TODO: put all these somewhere else
	private SimpleDenotator getSimpleDenotator(Denotator denotator, int[] path) throws RubatoException {
		if (path.length == 0){
			return (SimpleDenotator)denotator;
		}
		return (SimpleDenotator)denotator.get(path);
	}
	
	/*
	 * returns a list of injection morphisms, one for each dimension of the codomain module
	 * example: c:Q^2, then return [Q->Q^2, Q->Q^2]
	 * TODO: does not work for product rings, does it?
	 */
	private List<ModuleMorphism> makeInjectionMorphisms(Module codomain) {
		List<ModuleMorphism> injections = new ArrayList<ModuleMorphism>();
		int codim = codomain.getDimension();
		Ring ring = codomain.getRing();
		for (int i = 0; i < codim; i++) {
			injections.add(this.makeInjectionMorphism(ring, codim, i));
		}
		return injections;
	}
	
	private ModuleMorphism makeInjectionMorphism(Ring ring, int codomainDim, int index) {
		if (ring instanceof ProductRing) {
			ProductRing product = (ProductRing)ring;
			return EmbeddingMorphism.makeProductRingEmbedding(product.getFactor(index), product, index);
		}
		GenericAffineMorphism injection = new GenericAffineMorphism(ring, 1, codomainDim);
		RingElement one = (RingElement)new ZElement(1).cast(ring);
		injection.setMatrix(index, 0, one);
		return injection;
	}
	
	/*
	 * composes the input morphism with a projection on the component at 'index'.
	 * example: m:R^2->R^2, i:1, then return m:R^2->R with m(x,y) = m(y)
	 */
	private ModuleMorphism makeProjection(ModuleMorphism morphism, int codomainDim, int index) throws CompositionException {
		ModuleMorphism projection;
		if (morphism.getCodomain() instanceof ProductRing) {
			projection = ProjectionMorphism.make((ProductRing)morphism.getCodomain(), index);
		} else if (morphism.getCodomain() instanceof RFreeModule) {
			double[][] projectionMatrix = new double[1][codomainDim];
			projectionMatrix[0][index] = 1;
			projection = RFreeAffineMorphism.make(new RMatrix(projectionMatrix), new double[]{0});
		} else if (morphism.getCodomain() instanceof QFreeModule) {
			Rational[][] projectionMatrix = new Rational[1][codomainDim];
			for (int i = 0; i < codomainDim; i++) {
				if (i == index) {
					projectionMatrix[0][i] = new Rational(1);
				} else {
					projectionMatrix[0][i] = new Rational(0);
				}
			}
			projection = QFreeAffineMorphism.make(new QMatrix(projectionMatrix), new Rational[]{new Rational(0)});
		} else {
			int[][] projectionMatrix = new int[1][codomainDim];
			projectionMatrix[0][index] = 1;
			projection = ZFreeAffineMorphism.make(new ZMatrix(projectionMatrix), new int[]{0});
		}
		return projection.compose(morphism);
	}
	
	/*
	 * composes the input morphism with a casting morphism for the specified codomain.
	 * example: m:Q->Q, c:R, then return m:Q->R 
	 */
	private ModuleMorphism getCastedMorphism(ModuleMorphism morphism, Module newCodomain) throws CompositionException {
		Module oldCodomain = morphism.getCodomain();
		if (!newCodomain.equals(oldCodomain)) {
			ModuleMorphism castingMorphism = CanonicalMorphism.make(oldCodomain, newCodomain);
			morphism = castingMorphism.compose(morphism);
		}
		return morphism;
	}

}
