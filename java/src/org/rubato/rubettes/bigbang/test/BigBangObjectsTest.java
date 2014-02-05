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
import org.rubato.rubettes.bigbang.model.edits.AddObjectsEdit;
import org.rubato.rubettes.bigbang.model.edits.DeleteObjectsEdit;
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
		AddObjectsEdit firstAddEdit = new AddObjectsEdit(null, null, null);
		Set<DenotatorPath> firstAddedPaths = this.createPathSet(3,0);
		this.objects.updatePaths(firstAddEdit, null, new OperationPathResults(firstAddedPaths, null));
		TestCase.assertEquals(3, this.objects.getObjects().size());
		BigBangObject object3 = new ArrayList<BigBangObject>(this.objects.getObjects()).get(2);
		
		AddObjectsEdit secondAddEdit = new AddObjectsEdit(null, null, null);
		Set<DenotatorPath> secondAddedPaths = this.createPathSet(2,0);
		this.objects.updatePaths(secondAddEdit, null, new OperationPathResults(secondAddedPaths, null));
		TestCase.assertEquals(2, this.objects.getObjects().size());
		TestCase.assertEquals(2, this.objects.getObjectsAt(null).size());
		TestCase.assertEquals(1, this.objects.getRemovedObjectsAt(null).size());
		
		AddObjectsEdit thirdAddEdit = new AddObjectsEdit(null, null, null);
		Set<DenotatorPath> thirdAddedPaths = this.createPathSet(4,0);
		this.objects.updatePaths(thirdAddEdit, null, new OperationPathResults(thirdAddedPaths, null));
		TestCase.assertEquals(4, this.objects.getObjects().size());
		TestCase.assertEquals(4, this.objects.getObjectsAt(null).size());
		TestCase.assertEquals(0, this.objects.getRemovedObjectsAt(null).size());
		//check if object3 that was removed before is back
		TestCase.assertTrue(object3 == new ArrayList<BigBangObject>(this.objects.getObjects()).get(2));
		//TODO try adding more in a later add
	}
	
	public void testUpdateWithChanging() {
		//CHANGE OBJECTS, BUT NEXT TIME CHANGE LESS
	}
	
	public void testUpdateWithAddingAndChanging() {
		AddObjectsEdit edit1 = new AddObjectsEdit(null, null, null);
		Set<DenotatorPath> addedPaths1 = this.createPathSet(1,0);
		
		AddObjectsEdit edit2 = new AddObjectsEdit(null, null, null);
		Set<DenotatorPath> addedPaths2 = this.createPathSet(1,1);
		
		Set<DenotatorPath> addedPaths3 = this.createPathSet(1,0);
		Map<DenotatorPath,DenotatorPath> changedPaths3 = this.createPathMap(new int[][]{{0},{1}});
		OperationPathResults pathResults3 = new OperationPathResults(addedPaths3, null, changedPaths3);
		
		//edit1 is being performed, edit2 not known yet
		this.objects.updatePaths(edit1, null, new OperationPathResults(addedPaths1, null));
		
		//edit1 performed, edit2 known
		this.objects.updatePaths(edit1, edit2, new OperationPathResults(addedPaths1, null));
		List<BigBangObject> objects1 = new ArrayList<BigBangObject>(this.objects.getObjects());
		TestCase.assertEquals(1, objects1.size());
		TestCase.assertEquals(new DenotatorPath(this.SOUND_SCORE_FORM, new int[]{0}), objects1.get(0).getTopDenotatorPath());
		
		//edit2 performed
		this.objects.updatePaths(edit2, null, new OperationPathResults(addedPaths2, null));
		List<BigBangObject> objects2 = new ArrayList<BigBangObject>(this.objects.getObjects());
		TestCase.assertEquals(2, objects2.size());
		TestCase.assertEquals(new DenotatorPath(this.SOUND_SCORE_FORM, new int[]{0}), objects2.get(0).getTopDenotatorPath());
		TestCase.assertEquals(new DenotatorPath(this.SOUND_SCORE_FORM, new int[]{0}), objects2.get(0).getTopDenotatorPathAt(edit2));
		TestCase.assertEquals(new DenotatorPath(this.SOUND_SCORE_FORM, new int[]{1}), objects2.get(1).getTopDenotatorPath());
		TestCase.assertEquals(null, objects2.get(1).getTopDenotatorPathAt(edit2));
		TestCase.assertTrue(objects1.get(0) == objects2.get(0));
		
		//edit2 performed, it changed, now includes changedPaths
		this.objects.updatePaths(edit2, null, pathResults3);
		List<BigBangObject> objects3 = new ArrayList<BigBangObject>(this.objects.getObjects());
		TestCase.assertEquals(2, objects3.size());
		TestCase.assertEquals(new DenotatorPath(this.SOUND_SCORE_FORM, new int[]{0}), objects3.get(1).getTopDenotatorPath());
		TestCase.assertEquals(null, objects3.get(1).getTopDenotatorPathAt(edit2));
		TestCase.assertEquals(new DenotatorPath(this.SOUND_SCORE_FORM, new int[]{1}), objects3.get(0).getTopDenotatorPath());
		TestCase.assertEquals(new DenotatorPath(this.SOUND_SCORE_FORM, new int[]{0}), objects3.get(0).getTopDenotatorPathAt(edit2));
		TestCase.assertTrue(objects2.get(0) == objects3.get(0));
		TestCase.assertTrue(objects2.get(1) == objects3.get(1));
		
		//edit1 performed again in presumed next iteration
		//TODO o40 looses its path at edit2, o41 gets replaced and gets the translation status
		this.objects.updatePaths(edit1, edit2, new OperationPathResults(addedPaths1, null));
		List<BigBangObject> objects4 = new ArrayList<BigBangObject>(this.objects.getObjects());
		TestCase.assertEquals(2, objects4.size());
		TestCase.assertEquals(new DenotatorPath(this.SOUND_SCORE_FORM, new int[]{0}), objects4.get(1).getTopDenotatorPath());
		TestCase.assertEquals(null, objects4.get(1).getTopDenotatorPathAt(edit2));
		TestCase.assertEquals(new DenotatorPath(this.SOUND_SCORE_FORM, new int[]{1}), objects4.get(0).getTopDenotatorPath());
		TestCase.assertEquals(new DenotatorPath(this.SOUND_SCORE_FORM, new int[]{0}), objects4.get(0).getTopDenotatorPathAt(edit2));
		TestCase.assertTrue(objects3.get(0) == objects4.get(0));
		TestCase.assertTrue(objects3.get(1) == objects4.get(1));
	}
	
	public void testWithRemoving() {
		AddObjectsEdit addEdit = new AddObjectsEdit(null, null, null);
		Set<DenotatorPath> addedPaths = this.createPathSet(4,0);
		DeleteObjectsEdit removeEdit = new DeleteObjectsEdit(null, new TreeSet<BigBangObject>());
		Set<DenotatorPath> removedPaths = this.createPathSet(2,1);
		Map<DenotatorPath,DenotatorPath> changedPaths = this.createPathMap(new int[][]{{3},{1}});
		
		this.objects.updatePaths(addEdit, removeEdit, new OperationPathResults(addedPaths, null));
		TestCase.assertEquals(4, this.objects.getObjects().size());
		
		this.objects.updatePaths(removeEdit, null, new OperationPathResults(null, removedPaths, changedPaths));
		TestCase.assertEquals(4, this.objects.getObjects().size());
		TestCase.assertEquals(2, this.objects.getObjectsAt(null).size());
		
		//now remove less than before
		removedPaths = this.createPathSet(1,1);
		changedPaths = this.createPathMap(new int[][]{{2},{1},{3},{2}});
		
		this.objects.updatePaths(removeEdit, null, new OperationPathResults(null, removedPaths, changedPaths));
		TestCase.assertEquals(4, this.objects.getObjects().size());
		TestCase.assertEquals(3, this.objects.getObjectsAt(null).size());
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

}
