package org.rubato.rubettes.bigbang.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import junit.framework.TestCase;

import org.rubato.math.yoneda.Form;
import org.rubato.rubettes.bigbang.model.BigBangObject;
import org.rubato.rubettes.bigbang.model.BigBangObjects;
import org.rubato.rubettes.bigbang.model.OperationPathResults;
import org.rubato.rubettes.bigbang.model.operations.AbstractOperation;
import org.rubato.rubettes.bigbang.model.operations.AddObjectsOperation;
import org.rubato.rubettes.bigbang.model.operations.DeleteObjectsOperation;
import org.rubato.rubettes.util.DenotatorPath;

public class BigBangObjectsTest extends TestCase {
	
	private final Form SOUND_SCORE_FORM = new TestObjects().SOUND_SCORE_FORM;
	private BigBangObjects objects;
	
	protected void setUp() {
		this.objects = new BigBangObjects(this.SOUND_SCORE_FORM);
	}
	
	public void testGeneralMethods() {
		
	}
	
	public void testUpdateWithAdding() {
		//add three objects
		AddObjectsOperation addEdit = new AddObjectsOperation(null, null, null, false);
		Set<DenotatorPath> firstAddedPaths = this.createPathSet(3,0);
		this.objects.updatePaths(addEdit, null, new OperationPathResults(firstAddedPaths, null));
		TestCase.assertEquals(3, this.objects.getAllObjects().size());
		TestCase.assertEquals(3, this.objects.getObjectsAt(null).size());
		BigBangObject object3 = new ArrayList<BigBangObject>(this.objects.getAllObjects()).get(2);
		
		//then decide to only add two instead
		Set<DenotatorPath> secondAddedPaths = this.createPathSet(2,0);
		this.objects.updatePaths(addEdit, null, new OperationPathResults(secondAddedPaths, null));
		TestCase.assertEquals(3, this.objects.getAllObjects().size());
		TestCase.assertEquals(2, this.objects.getObjectsAt(null).size());
		
		//then add four instead
		Set<DenotatorPath> thirdAddedPaths = this.createPathSet(4,0);
		this.objects.updatePaths(addEdit, null, new OperationPathResults(thirdAddedPaths, null));
		TestCase.assertEquals(4, this.objects.getAllObjects().size());
		TestCase.assertEquals(4, this.objects.getObjectsAt(null).size());
		//check if object3 that was removed before is back
		TestCase.assertTrue(object3 == new ArrayList<BigBangObject>(this.objects.getAllObjects()).get(2));
		
		//add more in a later add
		AddObjectsOperation addEdit2 = new AddObjectsOperation(null, null, null, false);
		Set<DenotatorPath> fourthAddedPaths = this.createPathSet(2,4);
		this.objects.updatePaths(addEdit, addEdit2, new OperationPathResults(thirdAddedPaths, null));
		this.objects.updatePaths(addEdit2, null, new OperationPathResults(fourthAddedPaths, null));
		TestCase.assertEquals(6, this.objects.getAllObjects().size());
		TestCase.assertEquals(6, this.objects.getObjectsAt(null).size());
		//check if object3 is still there
		TestCase.assertTrue(object3 == new ArrayList<BigBangObject>(this.objects.getAllObjects()).get(2));
	}
	
	public void testUpdateWithAddingSatellites() {
		//add an anchor
		AddObjectsOperation addEdit = new AddObjectsOperation(null, null, null, false);
		Set<DenotatorPath> addedPaths = new TreeSet<DenotatorPath>();
		addedPaths.add(new DenotatorPath(this.SOUND_SCORE_FORM, new int[]{0}));
		this.objects.updatePaths(addEdit, null, new OperationPathResults(addedPaths, null));
		TestCase.assertEquals(1, this.objects.getAllObjects().size());
		TestCase.assertEquals(1, this.objects.getObjectsAt(null).size());
		BigBangObject anchor = new ArrayList<BigBangObject>(this.objects.getAllObjects()).get(0);
		
		//then decide to also add a satellite
		addedPaths.add(new DenotatorPath(this.SOUND_SCORE_FORM, new int[]{0,1,0}));
		this.objects.updatePaths(addEdit, null, new OperationPathResults(addedPaths, null));
		TestCase.assertEquals(2, this.objects.getAllObjects().size());
		TestCase.assertEquals(2, this.objects.getObjectsAt(null).size());
		BigBangObject firstSatellite = new ArrayList<BigBangObject>(this.objects.getAllObjects()).get(1);
		
		//then also add a second satellite
		addedPaths.add(new DenotatorPath(this.SOUND_SCORE_FORM, new int[]{0,1,1}));
		this.objects.updatePaths(addEdit, null, new OperationPathResults(addedPaths, null));
		TestCase.assertEquals(3, this.objects.getAllObjects().size());
		TestCase.assertEquals(3, this.objects.getObjectsAt(null).size());
		//check if anchor and first satellite still the same
		TestCase.assertTrue(anchor == new ArrayList<BigBangObject>(this.objects.getAllObjects()).get(0));
		TestCase.assertTrue(firstSatellite == new ArrayList<BigBangObject>(this.objects.getAllObjects()).get(1));
	}
	
	public void testUpdateWithChanging() {
		//CHANGE OBJECTS, BUT NEXT TIME CHANGE LESS
	}
	
	public void testUpdateWithAddingAndChanging() {
		AddObjectsOperation edit1 = new AddObjectsOperation(null, null, null, false);
		Set<DenotatorPath> addedPaths1 = this.createPathSet(1,0);
		
		AddObjectsOperation edit2 = new AddObjectsOperation(null, null, null, false);
		Set<DenotatorPath> addedPaths2 = this.createPathSet(1,1);
		
		Set<DenotatorPath> addedPaths3 = this.createPathSet(1,0);
		Map<DenotatorPath,DenotatorPath> changedPaths3 = this.createPathMap(new int[][]{{0},{1}});
		OperationPathResults pathResults3 = new OperationPathResults(addedPaths3, null, changedPaths3);
		
		//edit1 is being performed, edit2 not known yet
		this.objects.updatePaths(edit1, null, new OperationPathResults(addedPaths1, null));
		
		//edit1 performed, edit2 known
		this.objects.updatePaths(edit1, edit2, new OperationPathResults(addedPaths1, null));
		List<BigBangObject> objects1 = new ArrayList<BigBangObject>(this.objects.getObjectsAt(edit2));
		TestCase.assertEquals(1, objects1.size());
		TestCase.assertEquals(new DenotatorPath(this.SOUND_SCORE_FORM, new int[]{0}), objects1.get(0).getTopDenotatorPath());
		
		//edit2 performed
		this.objects.updatePaths(edit2, null, new OperationPathResults(addedPaths2, null));
		List<BigBangObject> objects2 = new ArrayList<BigBangObject>(this.objects.getObjectsAt(null));
		TestCase.assertEquals(2, objects2.size());
		TestCase.assertEquals(new DenotatorPath(this.SOUND_SCORE_FORM, new int[]{0}), objects2.get(0).getTopDenotatorPath());
		TestCase.assertEquals(new DenotatorPath(this.SOUND_SCORE_FORM, new int[]{0}), objects2.get(0).getTopDenotatorPathAt(edit2));
		TestCase.assertEquals(new DenotatorPath(this.SOUND_SCORE_FORM, new int[]{1}), objects2.get(1).getTopDenotatorPath());
		TestCase.assertEquals(null, objects2.get(1).getTopDenotatorPathAt(edit2));
		TestCase.assertTrue(objects1.get(0) == objects2.get(0));
		
		//edit2 performed, it changed, now includes changedPaths
		this.objects.updatePaths(edit2, null, pathResults3);
		List<BigBangObject> objects3 = new ArrayList<BigBangObject>(this.objects.getObjectsAt(null));
		TestCase.assertEquals(2, objects3.size());
		TestCase.assertEquals(new DenotatorPath(this.SOUND_SCORE_FORM, new int[]{0}), objects3.get(0).getTopDenotatorPath());
		TestCase.assertEquals(null, objects3.get(0).getTopDenotatorPathAt(edit2));
		TestCase.assertEquals(new DenotatorPath(this.SOUND_SCORE_FORM, new int[]{1}), objects3.get(1).getTopDenotatorPath());
		TestCase.assertEquals(new DenotatorPath(this.SOUND_SCORE_FORM, new int[]{0}), objects3.get(1).getTopDenotatorPathAt(edit2));
		TestCase.assertTrue(objects2.get(0) == objects3.get(1));
		TestCase.assertTrue(objects2.get(1) == objects3.get(0));
		
		//edit1 performed again in presumed next iteration
		this.objects.updatePaths(edit1, edit2, new OperationPathResults(addedPaths1, null));
		List<BigBangObject> objects4 = new ArrayList<BigBangObject>(this.objects.getObjectsAt(edit2));
		TestCase.assertEquals(1, objects4.size());
		TestCase.assertEquals(new DenotatorPath(this.SOUND_SCORE_FORM, new int[]{1}), objects4.get(0).getTopDenotatorPath());
		TestCase.assertEquals(new DenotatorPath(this.SOUND_SCORE_FORM, new int[]{0}), objects4.get(0).getTopDenotatorPathAt(edit2));
		TestCase.assertTrue(objects3.get(1) == objects4.get(0));
	}
	
	public void testWithRemoving() {
		AddObjectsOperation addEdit = new AddObjectsOperation(null, null, null, false);
		Set<DenotatorPath> addedPaths = this.createPathSet(4,0);
		DeleteObjectsOperation removeEdit = new DeleteObjectsOperation(null, new TreeSet<BigBangObject>());
		Set<DenotatorPath> removedPaths = this.createPathSet(2,1);
		Map<DenotatorPath,DenotatorPath> changedPaths = this.createPathMap(new int[][]{{3},{1}});
		
		this.objects.updatePaths(addEdit, removeEdit, new OperationPathResults(addedPaths, null));
		TestCase.assertEquals(4, this.objects.getAllObjects().size());
		
		this.objects.updatePaths(removeEdit, null, new OperationPathResults(null, removedPaths, changedPaths));
		TestCase.assertEquals(4, this.objects.getAllObjects().size());
		TestCase.assertEquals(2, this.objects.getObjectsAt(null).size());
		
		//now remove less than before
		removedPaths = this.createPathSet(1,1);
		changedPaths = this.createPathMap(new int[][]{{2},{1},{3},{2}});
		
		this.objects.updatePaths(removeEdit, null, new OperationPathResults(null, removedPaths, changedPaths));
		TestCase.assertEquals(4, this.objects.getAllObjects().size());
		TestCase.assertEquals(3, this.objects.getObjectsAt(null).size());
	}
	
	public void testUpdateWithProjection() {
		//tests update in case of transformation that loses objects and suddenly doesn't (e.g. projection)
		AddObjectsOperation addEdit = new AddObjectsOperation(null, null, null, false);
		Set<DenotatorPath> addedPaths = this.createPathSet(3,0);
		//just a dummy edit simulation a ScalingEdit
		DeleteObjectsOperation scalingEdit = new DeleteObjectsOperation(null, new TreeSet<BigBangObject>());
		Set<DenotatorPath> removedPaths = this.createPathSet(2,0);
		Map<DenotatorPath,DenotatorPath> changedPaths = this.createPathMap(new int[][]{{2},{0}});
		
		this.objects.updatePaths(addEdit, scalingEdit, new OperationPathResults(addedPaths, null));
		TestCase.assertEquals(3, this.objects.getAllObjects().size());
		TestCase.assertEquals(3, this.objects.getObjectsAt(scalingEdit).size());
		BigBangObject object2 = this.getObjectAt(scalingEdit, 2);
		BigBangObject object0 = this.getObjectAt(scalingEdit, 0);
		
		this.objects.updatePaths(scalingEdit, null, new OperationPathResults(null, removedPaths, changedPaths));
		TestCase.assertEquals(3, this.objects.getAllObjects().size());
		TestCase.assertEquals(1, this.objects.getObjectsAt(null).size());
		//object0 is now old object2
		TestCase.assertTrue(object2 == this.getObjectAt(null, 0));
		
		//now change paths instead of removing
		this.objects.updatePaths(addEdit, scalingEdit, new OperationPathResults(addedPaths, null));
		changedPaths = this.createPathMap(new int[][]{{1},{2},{2},{1}});
		this.objects.updatePaths(scalingEdit, null, new OperationPathResults(null, null, changedPaths));
		TestCase.assertEquals(3, this.objects.getAllObjects().size());
		TestCase.assertEquals(3, this.objects.getObjectsAt(null).size());
		//object2 should now be at object1
		TestCase.assertTrue(object2 == this.getObjectAt(null, 1));
		//object0 should be back at its place
		TestCase.assertTrue(object0 == this.getObjectAt(null, 0));
	}
	
	private Set<DenotatorPath> createPathSet(int numberOfPaths, int startingIndex) {
		Set<DenotatorPath> paths = new TreeSet<DenotatorPath>();
		for (int i = 0; i < numberOfPaths; i++) {
			paths.add(new DenotatorPath(this.SOUND_SCORE_FORM, new int[]{i+startingIndex}));
		}
		return paths;
	}
	
	private Map<DenotatorPath,DenotatorPath> createPathMap(int[][] paths) {
		Map<DenotatorPath,DenotatorPath> pathMap = new TreeMap<DenotatorPath,DenotatorPath>();
		for (int i = 0; i < paths.length-1; i+=2) {
			pathMap.put(new DenotatorPath(this.SOUND_SCORE_FORM, paths[i]),
					new DenotatorPath(this.SOUND_SCORE_FORM, paths[i+1]));
		}
		return pathMap;
	}
	
	private BigBangObject getObjectAt(AbstractOperation edit, int index) {
		 return new ArrayList<BigBangObject>(this.objects.getObjectsAt(edit)).get(index);
	}

}
