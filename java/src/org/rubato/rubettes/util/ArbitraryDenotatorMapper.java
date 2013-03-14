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
import org.rubato.math.module.QFreeModule;
import org.rubato.math.module.RFreeModule;
import org.rubato.math.module.Ring;
import org.rubato.math.module.RingElement;
import org.rubato.math.module.ZElement;
import org.rubato.math.module.morphism.CanonicalMorphism;
import org.rubato.math.module.morphism.CompositionException;
import org.rubato.math.module.morphism.GenericAffineMorphism;
import org.rubato.math.module.morphism.ModuleMorphism;
import org.rubato.math.module.morphism.QFreeAffineMorphism;
import org.rubato.math.module.morphism.RFreeAffineMorphism;
import org.rubato.math.module.morphism.ZFreeAffineMorphism;
import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.NameDenotator;
import org.rubato.math.yoneda.PowerDenotator;
import org.rubato.math.yoneda.SimpleDenotator;
import org.rubato.math.yoneda.SimpleForm;

public class ArbitraryDenotatorMapper {
	
	private ModuleMorphism morphism;
	private int[][] paths;
	private Module domain;
	private int domainDim, codomainDim;
	private List<ModuleMorphism> injectionMorphisms;
	
	public ArbitraryDenotatorMapper(ModuleMorphism morphism, List<DenotatorPath> paths) {
		this.paths = this.convertPaths(paths);
		this.init(morphism);
	}
	
	public ArbitraryDenotatorMapper(ModuleMorphism morphism, int[][] paths) {
		this.paths = paths;
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
	
	private int[][] convertPaths(List<DenotatorPath> paths) {
		int[][] arrayPaths = new int[paths.size()][];
		for (int i = 0; i < paths.size(); i++) {
			arrayPaths[i] = paths.get(i).toIntArray();
		}
		return arrayPaths;
	}
	
	/*
	 * maps the input denotator using the input morphism and respecting the paths selected
	 * in the morphisms table. The output denotator is of the same form as the input denotator.
	 */
	public PowerDenotator getMappedPowerDenotator(PowerDenotator input) throws RubatoException {
		//prepare output
		PowerDenotator output = new PowerDenotator(NameDenotator.make(""), input.getAddress(), input.getPowerForm(), new ArrayList<Denotator>());
		
		if (this.paths != null && this.paths.length == domainDim + codomainDim) {
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
		ModuleMorphism morphism = this.morphism.compose(this.makeInjectionSum(denotator));
		
		return this.makeProjections(denotator, morphism);
	}
	
	/*
	 * adapt the morphism of every simple denotator in morphismPaths to the main morphism
	 * by composing it with an injection
	 */
	private ModuleMorphism makeInjectionSum(Denotator denotator) throws RubatoException {
		ModuleMorphism injectionSum = null;
		for (int j = 0; j < domainDim; j++) {
			int[] currentPath = this.paths[j];
			ModuleMorphism currentMorphism = this.getSimpleDenotator(denotator, currentPath).getModuleMorphism();
			
			//give the current morphism a new codomain 
			Module newCodomain = domain.getComponentModule(j);
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
		return injectionSum;
	}
	
	private Denotator makeProjections(Denotator mappedDenotator, ModuleMorphism m) throws RubatoException {
		
		for (int i = 0; i < this.codomainDim; i++) {
			ModuleMorphism projectedM = m;
			
			//make projection if necessary
			if (this.codomainDim > 1) {
				projectedM = this.makeProjection(m, this.codomainDim, i);
			}
			
			//replace original coordinate by mapped coordinate 
			int[] currentCodomainPath = this.paths[this.domainDim + i];
			SimpleDenotator currentDenotator = this.getSimpleDenotator(mappedDenotator, currentCodomainPath);
			Module newCodomain = currentDenotator.getModuleMorphism().getCodomain();
			projectedM = this.getCastedMorphism(projectedM, newCodomain);
			SimpleForm currentForm = (SimpleForm)currentDenotator.getForm();
			try {
				Denotator currentSimpleDenotator = new SimpleDenotator(NameDenotator.make(""), currentForm, projectedM);
				if (currentCodomainPath.length == 0) {
					mappedDenotator = currentSimpleDenotator;
				} else {
					mappedDenotator = mappedDenotator.replace(currentCodomainPath, currentSimpleDenotator);
				}
			} catch (DomainException e) {
				e.printStackTrace();
			}
		}
		
		return mappedDenotator;
	}
	
	/*
	 * returns the simple denotator along the path within the specified denotator.
	 * also handles the empty path [] in case the denotator itself is of type simple
	 */
	private SimpleDenotator getSimpleDenotator(Denotator denotator, int[] path) throws RubatoException {
		if (path.length == 0){
			return (SimpleDenotator)denotator;
		}
		return (SimpleDenotator)denotator.get(path);
	}
	
	/*
	 * returns a list of injection morphisms, one for each dimension of the codomain module
	 * example: c:Q^2, then return [Q->Q^2, Q->Q^2]
	 * does not work for product rings, does it?
	 */
	private List<ModuleMorphism> makeInjectionMorphisms(Module codomain) {
		List<ModuleMorphism> injections = new ArrayList<ModuleMorphism>();
		int codim = codomain.getDimension();
		Ring ring = codomain.getRing();
		for (int i = 0; i < codim; i++) {
			GenericAffineMorphism currentInjection = new GenericAffineMorphism(ring, 1, codim);
			RingElement one = (RingElement)new ZElement(1).cast(ring);
			currentInjection.setMatrix(i, 0, one);
			injections.add(currentInjection);
		}
		return injections;
	}
	
	/*
	 * composes the input morphism with a projection on the component at 'index'.
	 * example: m:R^2->R^2, i:1, then return m:R^2->R with m(x,y) = m(y)
	 */
	private ModuleMorphism makeProjection(ModuleMorphism morphism, int codomainDim, int index) throws CompositionException {
		ModuleMorphism projection;
		if (morphism.getCodomain() instanceof RFreeModule) {
			double[][] projectionMatrix = new double[1][codomainDim];
			projectionMatrix[0][index] = 1;
			projection = RFreeAffineMorphism.make(new RMatrix(projectionMatrix), new double[]{0});
		} else if (morphism.getCodomain() instanceof QFreeModule) {
			Rational[][] projectionMatrix = new Rational[1][codomainDim];
			projectionMatrix[0][index] = new Rational(1);
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
