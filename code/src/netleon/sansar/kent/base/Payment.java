package netleon.sansar.kent.base;

public class Payment {

	String id, card_type, name, cc_no, expiration_year, expiration_month, ccv;
	public boolean is_default;

	public Payment(String id, String card_type, String name, String cc_no,
			String expiration_year, String expiration_month, String ccv,
			Boolean is_default) {

		this.id = id;
		this.card_type = card_type;
		this.name = name;
		this.cc_no = cc_no;
		this.expiration_month = expiration_month;
		this.expiration_year = expiration_year;
		this.ccv = ccv;
		this.is_default = is_default;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setIs_default(boolean is_default) {
		this.is_default = is_default;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setExpiration_year(String expiration_year) {
		this.expiration_year = expiration_year;
	}

	public void setExpiration_month(String expiration_month) {
		this.expiration_month = expiration_month;
	}

	public void setCcv(String ccv) {
		this.ccv = ccv;
	}

	public void setCc_no(String cc_no) {
		this.cc_no = cc_no;
	}

	public void setCard_type(String card_type) {
		this.card_type = card_type;
	}

	public boolean isIs_default() {
		return is_default;
	}

	public String getName() {
		return name;
	}

	public String getId() {
		return id;
	}

	public String getExpiration_year() {
		return expiration_year;
	}

	public String getExpiration_month() {
		return expiration_month;
	}

	public String getCcv() {
		return ccv;
	}

	public String getCc_no() {
		return cc_no;
	}

	public String getCard_type() {
		return card_type;
	}

}
