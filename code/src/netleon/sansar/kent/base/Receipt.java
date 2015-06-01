package netleon.sansar.kent.base;

public class Receipt {

	String id, company, phone, address, name, payment, subtotal, tax, kent_fee,
			tip, total, date, status, rest_id, user_card_id;

	public Receipt(String id, String company, String phone, String address,
			String name, String payment, String subtotal, String tax,
			String kent_fee, String tip, String total, String date,
			String status, String rest_id, String user_card_id) {
		this.id = id;
		this.company = company;
		this.phone = phone;
		this.address = address;
		this.name = name;
		this.payment = payment;
		this.subtotal = subtotal;
		this.tax = tax;
		this.kent_fee = kent_fee;
		this.tip = tip;
		this.total = total;
		this.date = date;
		this.status = status;
		this.rest_id = rest_id;
		this.user_card_id = user_card_id;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public void setTip(String tip) {
		this.tip = tip;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}
	
	public void setUser_card_id(String user_card_id) {
		this.user_card_id = user_card_id;
	}
	
	public String getUser_card_id() {
		return user_card_id;
	}

	public void setTax(String tax) {
		this.tax = tax;
	}

	public void setSubtotal(String subtotal) {
		this.subtotal = subtotal;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setPayment(String payment) {
		this.payment = payment;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setKent_fee(String kent_fee) {
		this.kent_fee = kent_fee;
	}

	public String getKent_fee() {
		return kent_fee;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTotal() {
		return total;
	}

	public String getTip() {
		return tip;
	}

	public String getTax() {
		return tax;
	}

	public String getSubtotal() {
		return subtotal;
	}

	public String getPhone() {
		return phone;
	}

	public String getPayment() {
		return payment;
	}

	public String getName() {
		return name;
	}

	public String getId() {
		return id;
	}

	public String getDate() {
		return date;
	}

	public String getCompany() {
		return company;
	}

	public String getAddress() {
		return address;
	}

	public void setRest_id(String rest_id) {
		this.rest_id = rest_id;
	}

	public String getRest_id() {
		return rest_id;
	}
}
