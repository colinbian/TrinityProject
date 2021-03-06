package com.github.thomasahle.trainbox.trainbox.uimodel;

import java.util.ArrayList;
import java.util.List;

/**
 * This is just a helper abstract.
 */
public abstract class AbstractComposite extends AbstractComponent implements UIComposite, TrainsChangedListener {
	@Override
	public List<UITrain> getCarriages() {
		List<UITrain> carriages = new ArrayList<UITrain>();
		for (UIComponent comp : getChildren())
			carriages.addAll(comp.getCarriages());
		return carriages;
	}
	
	@Override
	public void update(float delta) {
		List<UIComponent> list = getChildren();
		for (int i = list.size()-1; i >= 0; i--)
			list.get(i).update(delta);
	}
	
	protected void install(UIComponent child) {
		child.paused(paused());
		child.setTrainsChangedListener(this);
	}
	public void onTrainCreated(UITrain train) {
		fireTrainCreatedEvent(train);
	}
	public void onTrainDestroyed(UITrain train) {
		fireTrainDestroyedEvent(train);
	}
	public void paused(boolean paused) {
		super.paused(paused);
		for (UIComponent comp : getChildren())
			comp.paused(paused);
	}
}
