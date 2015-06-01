package netleon.sansar.kent.base;

public class Reservation {

	String rest_name, strat_date, end_date, id, rest_id, user_id, party_size,
			start_date_time, end_date_time, status;

	public Reservation(String rest_name, String strat_date,
			String end_date, String id, String rest_id, String user_id,
			String party_size, String start_date_time, String end_date_time,
			String status) {

		this.rest_name = rest_name;
		this.strat_date = strat_date;
		this.end_date = end_date;
		this.id = id;
		this.rest_id = rest_id;
		this.user_id = user_id;
		this.party_size = party_size;
		this.start_date_time = start_date_time;
		this.end_date_time = end_date_time;
		this.status = status;

	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public void setStrat_date(String strat_date) {
		this.strat_date = strat_date;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setStart_date_time(String start_date_time) {
		this.start_date_time = start_date_time;
	}

	public void setRest_name(String rest_name) {
		this.rest_name = rest_name;
	}

	public void setRest_id(String rest_id) {
		this.rest_id = rest_id;
	}

	public void setParty_size(String party_size) {
		this.party_size = party_size;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setEnd_date_time(String end_date_time) {
		this.end_date_time = end_date_time;
	}

	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}

	public String getUser_id() {
		return user_id;
	}

	public String getStrat_date() {
		return strat_date;
	}

	public String getStatus() {
		return status;
	}

	public String getStart_date_time() {
		return start_date_time;
	}

	public String getRest_name() {
		return rest_name;
	}

	public String getRest_id() {
		return rest_id;
	}

	public String getParty_size() {
		return party_size;
	}

	public String getId() {
		return id;
	}

	public String getEnd_date_time() {
		return end_date_time;
	}

	public String getEnd_date() {
		return end_date;
	}

}
