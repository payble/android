package netleon.sansar.kent.base;

public class SearchKey {

	String name;
	int id;

	public SearchKey(String name, int id) {
		this.id = id;
		this.name = name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}
}
