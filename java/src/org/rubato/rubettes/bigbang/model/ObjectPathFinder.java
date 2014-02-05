package org.rubato.rubettes.bigbang.model;

import java.util.Set;
import java.util.TreeSet;

import org.rubato.math.yoneda.ColimitDenotator;
import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.FactorDenotator;
import org.rubato.math.yoneda.LimitDenotator;
import org.rubato.rubettes.util.DenotatorPath;

public class ObjectPathFinder {
	
	private Set<DenotatorPath> foundObjectPaths = new TreeSet<DenotatorPath>(); 
	private boolean powersetOrListFound;
	
	public Set<DenotatorPath> findPaths(Denotator currentDenotator, DenotatorPath currentPath) {
		this.addObjectPathsToNewPaths(currentDenotator, currentPath);
		return this.foundObjectPaths;
	}
	
	public boolean powersetOrListFound() {
		return this.powersetOrListFound;
	}
	
	//recursive search method
	private void addObjectPathsToNewPaths(Denotator currentDenotator, DenotatorPath currentPath) {
		int denotatorType = currentDenotator.getType();
		if (currentDenotator.getType() == Denotator.POWER || currentDenotator.getType() == Denotator.LIST) {
			this.powersetOrListFound = true;
			FactorDenotator currentFactorDenotator = (FactorDenotator)currentDenotator;
			for (int i = 0; i < currentFactorDenotator.getFactorCount(); i++) {
				DenotatorPath currentChildPath = currentPath.getChildPath(i);
				this.foundObjectPaths.add(currentChildPath);
				this.addObjectPathsToNewPaths(currentFactorDenotator.getFactor(i), currentChildPath);
			}
		} else if (denotatorType == Denotator.LIMIT) {
			LimitDenotator currentLimit = (LimitDenotator)currentDenotator;
			for (int i = 0; i < currentLimit.getFactorCount(); i++) {
				Denotator currentChild = currentLimit.getFactor(i);
				this.addObjectPathsToNewPaths(currentChild, currentPath.getChildPath(i));
			}
		} else if (denotatorType == Denotator.COLIMIT) {
			ColimitDenotator currentColimit = (ColimitDenotator)currentDenotator;
			Denotator onlyChild = currentColimit.getFactor();
			int childIndex = currentColimit.getIndex();
			for (int i = 0; i < currentColimit.getForm().getFormCount(); i++) {
				if (i == childIndex) {
					DenotatorPath childPath = currentPath.getChildPath(childIndex);
					this.addObjectPathsToNewPaths(onlyChild, childPath);
				}
			}
		}
	}

}
