package netleon.sansar.kent.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchList {

	public HashMap<SearchKey, List<SearchVal>> searchList;

	public SearchList() {
		searchList = new HashMap<SearchKey, List<SearchVal>>();
	}

	public void addchakra(SearchKey key, SearchVal val) {
		if (!searchList.containsKey(key)) {
			List<SearchVal> list = new ArrayList<SearchVal>();
			list.add(val);
			searchList.put(key, list);
		} else {
			searchList.get(key).add(val);
		}
	}

	public void setSearchList(HashMap<SearchKey, List<SearchVal>> searchList) {
		this.searchList = searchList;
	}

	public HashMap<SearchKey, List<SearchVal>> getSearchList() {
		return searchList;
	}
}
