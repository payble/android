package netleon.sansar.kent.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class CatgorizedRestos {

	public HashMap<String, List<Restaurant>> list;

	public CatgorizedRestos() {
		list = new HashMap<String, List<Restaurant>>();
	}

	public void addRestosToCat(String key, Restaurant restaurant) {
		if (list.containsKey(key)) {
			list.get(key).add(restaurant);
		} else {
			List<Restaurant> lister = new ArrayList<Restaurant>();
			lister.add(restaurant);
			list.put(key, lister);
		}
	}

	public void setList(HashMap<String, List<Restaurant>> list) {
		this.list = list;
	}

	public HashMap<String, List<Restaurant>> getList() {
		return list;
	}

	public List<Restaurant> getListByCategory(String category) {
		return list.get(category);
	}
}
