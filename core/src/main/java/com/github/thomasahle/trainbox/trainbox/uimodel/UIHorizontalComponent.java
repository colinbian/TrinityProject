package com.github.thomasahle.trainbox.trainbox.uimodel;

import static playn.core.PlayN.graphics;
import static playn.core.PlayN.log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import playn.core.CanvasImage;
import playn.core.GroupLayer;
import playn.core.ImageLayer;
import playn.core.Layer;
import pythagoras.f.Dimension;
import pythagoras.f.Point;

public class UIHorizontalComponent extends AbstractComposite {
	
	public final int padding;
	
	// Invariant: |mComponents| > 0
	private List<UIComponent> mComponents = new ArrayList<UIComponent>();
	private GroupLayer mBackLayer = graphics().createGroupLayer();
	private GroupLayer mFrontLayer = graphics().createGroupLayer();
	private ImageLayer bg;
	
	public UIHorizontalComponent(int padding) {
		this.padding = padding;
		
		CanvasImage bgImage = graphics().createImage(1000, 1000);
		bgImage.canvas().setFillColor(0xaa00ff00)
						.fillRect(0, 0, getSize().width, getSize().height)
						.fillRect(0, 0, 50, 50);
		bg = graphics().createImageLayer(bgImage);
		mBackLayer.add(bg);
		
		insert(new UIIdentityComponent(padding), 0);
	}
	
	public void add(UIComponent comp) {
		insert(comp, getChildren().size());
		insert(new UIIdentityComponent(padding), getChildren().size());
	}
	
	@Override
	public boolean insertChildAt(UIComponent child, Point position) {
		// We accept positions that are on top of an identity component.
		// For the user that corresponds to the spaces between 'real' components.
		
		for (int p = 0; p < mComponents.size(); p++) {
			UIComponent c = mComponents.get(p);
			if (c.getPosition().x <= position.x
					&& position.x < c.getPosition().x+c.getSize().width) {
				// Okay, this is not terribly object oriented. But it works for now.
				if (c instanceof UIComposite) {
					Point recursivePoint = new Point(position.x-c.getPosition().x, position.y-c.getPosition().y);
					return ((UIComposite)c).insertChildAt(c, recursivePoint);
				}
				else if (c instanceof UIIdentityComponent) {
					log().debug("Inserting at position "+p);
					// Insert the new component before the identity clicked on
					insert(child, p);
					// And insert a new identity before the new component
					insert(new UIIdentityComponent(padding), p);
					// TODO: Do we also need to shift the trains, or do we assume
					// that this is only called when trains are stopped?
					return true;
				}
			}
		}
		return false;
	}
	
	private void insert(UIComponent comp, int pos) {
		assert 0 <= pos && pos <= mComponents.size();
		
		// Insert component correctly in the 'TrainTaker' chain
		if (pos > 0)
			mComponents.get(pos-1).setTrainTaker(comp);
		if (pos < mComponents.size())
			comp.setTrainTaker(mComponents.get(pos));
		if (pos == mComponents.size())
			comp.setTrainTaker(getTrainTaker());
		
		// Reposition old layers to fit the new one
		for (int p = pos; p < mComponents.size(); p++) {
			UIComponent c = mComponents.get(p);
			c.setPosition(new Point(c.getPosition().x + comp.getSize().width, c.getPosition().y));
		}
		
		// Add the new layer
		mBackLayer.add(comp.getBackLayer());
		mFrontLayer.add(comp.getFrontLayer());
		if (pos != 0) {
			float x = mComponents.get(pos-1).getPosition().x + mComponents.get(pos-1).getSize().width;
			comp.setPosition(new Point(x, 0));
		}
			
		// Install in data structures
		mComponents.add(pos, comp);
		comp.onAdded(this);
		super.install(comp);
		
		// We have now resized, so we need to redraw.
		// TODO: Actually this component shouldn't paint anything.
		updateBackground();
	}

	private void updateBackground() {
		CanvasImage bgImage = graphics().createImage(1000, 1000);
		bgImage.canvas().setFillColor(0xaa00ff00);
		bgImage.canvas().fillRect(0, 0, getSize().width, getSize().height);
		bg.setImage(bgImage);
	}
	
	@Override
	public List<UIComponent> getChildren() {
		return Collections.unmodifiableList(mComponents);
	}

	@Override
	public Dimension getSize() {
		int width = 0;
		float height = Float.MIN_VALUE;
		for (UIComponent child : getChildren()) {
			width += child.getSize().width;
			height = Math.max(height, child.getSize().height);
		}
		return new Dimension(width, height);
	}

	@Override
	public Layer getBackLayer() {
		return mBackLayer;
	}
	
	@Override
	public Layer getFrontLayer() {
		return mFrontLayer;
	}

	@Override
	public void update(float delta) {
		super.update(delta);
	}

	@Override
	public void setTrainTaker(TrainTaker listener) {
		super.setTrainTaker(listener);
		mComponents.get(mComponents.size()-1).setTrainTaker(listener);
	}

	@Override
	public void takeTrain(UITrain train) {
		log().debug("Passing train down from "+this+" to "+mComponents.get(0));
		mComponents.get(0).takeTrain(train);
	}

	@Override
	public float leftBlock() {
		return mComponents.get(0).leftBlock();
	}
}
