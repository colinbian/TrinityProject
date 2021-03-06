package com.github.thomasahle.trainbox.trainbox.uimodel;

import java.util.ArrayList;
import java.util.List;

import com.github.thomasahle.trainbox.trainbox.model.Component;

public final class UIComponentFactory {
	public static UIComponent fromModel(Component component) {
		// TODO: Create this factory. Perhaps using the visitor pattern.
		return null;
	}
	
	/**
	 * @param cargos The cargos in order such that cargos[0] will be the first in line.
	 * @return trains such that trains[0] is the first in line. The trains are all singleton.
	 */
	public static List<UITrain> trainsFromCargos(List<Integer> cargos) {
		List<UITrain> trains = new ArrayList<UITrain>();
		for (Integer cargo : cargos) {
			trains.add(new UITrain(cargo));
		}
		return trains;
	}
}
