package netleon.sansar.kent.base;

public class SearchVal {

	int parent_id;
	String name;
	int id;

	public SearchVal(int parent_id, String name, int id) {
		this.id = id;
		this.name = name;
		this.parent_id = parent_id;
	}

	public void setParent_id(int parent_id) {
		this.parent_id = parent_id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getParent_id() {
		return parent_id;
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}
}
