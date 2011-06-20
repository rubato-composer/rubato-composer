package org.rubato.rubettes.bigbang.view.subview.multitouch;

import java.net.URL;

import org.mt4j.MTApplication;
import org.mt4j.components.MTComponent;
import org.mt4j.components.clipping.Clip;
import org.mt4j.components.visibleComponents.widgets.buttons.MTImageButton;
import org.mt4j.input.inputProcessors.componentProcessors.lassoProcessor.LassoProcessor;
import org.mt4j.input.inputProcessors.globalProcessors.CursorTracer;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.util.MTColor;
import org.mt4j.util.camera.MTCamera;
import org.mt4j.util.camera.Frustum;
import org.mt4j.util.math.Vector3D;

import org.rubato.rubettes.bigbang.BigBangRubette;
import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.controller.mode.DisplayModeAction;
import org.rubato.rubettes.bigbang.view.controller.mode.multitouch.MTNavigationModeAdapter;
import org.rubato.rubettes.bigbang.view.controller.mode.multitouch.MTTransformationModeAdapter;

import processing.core.PImage;

public class BigBangScene extends AbstractScene {
	
	private MTComponent axesLayer;
	private MTComponent buttonsLayer;
	private MTComponent noteLayer;
	private LassoProcessor lassoProcessor;

	public BigBangScene(MTApplication mtApplication, ViewController controller) {
		super(mtApplication, "Big Bang");
		
		this.setClearColor(new MTColor(255, 255, 255));
		
		//Show touches
		this.registerGlobalInputProcessor(new CursorTracer(mtApplication, this));
		
		
		this.lassoProcessor = new LassoProcessor(mtApplication, this.getCanvas(), this.getSceneCam());
		
		this.noteLayer = new MTComponent(mtApplication);
		this.getCanvas().addChild(this.noteLayer);
		//this.noteLayer.setChildClip(new Clip(null));
		
		this.makeButtonsLayer(mtApplication, controller);
		
		this.axesLayer = new MTComponent(mtApplication, new MTCamera(mtApplication));
		this.getCanvas().addChild(this.axesLayer);
		
		this.getSceneCam().setZoomMinDistance(1);
		//TODO:(Frustum)this.getSceneCam().getFrustum();
		//this.getCanvas().setFrustumCulling(false);
		
		//this.getSceneCam().setViewCenterPos(new Vector3D(0,-500,150));
		//System.out.println(this.getSceneCam().getPosition());
	}
	
	private void makeButtonsLayer(MTApplication mtApplication, ViewController controller) {
		this.buttonsLayer = new MTComponent(mtApplication, new MTCamera(mtApplication));
		this.getCanvas().addChild(this.buttonsLayer);
		this.makeButton(mtApplication, "navModeIcon.png", 0, new DisplayModeAction(controller, new MTNavigationModeAdapter(controller, this)));
		this.makeButton(mtApplication, "transModeIcon.png", 75, new DisplayModeAction(controller, new MTTransformationModeAdapter(controller, this)));
	}
	
	private void makeButton(MTApplication mtApplication, String iconName, int x, DisplayModeAction action) {
		URL imageURL = BigBangRubette.class.getResource(iconName);
		PImage icon = mtApplication.loadImage(imageURL.toString());
		MTImageButton button = new MTImageButton(icon, mtApplication);
		button.addActionListener(action);
		this.buttonsLayer.addChild(button);
		button.translate(new Vector3D(x, 0, 0));
		button.setNoStroke(true);
		button.setStrokeColor(new MTColor(0,0,0));
	}

	public MTComponent getAxesLayer() {
		return this.axesLayer;
	}
	
	public MTComponent getNoteLayer() {
		return this.noteLayer;
	}
	
	public LassoProcessor getLassoProcessor() {
		return this.lassoProcessor;
	}
	
	@Override
	public void init() {}
	
	@Override
	public void shutDown() {}
	
}
