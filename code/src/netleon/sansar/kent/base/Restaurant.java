package netleon.sansar.kent.base;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

public class Restaurant implements Parcelable {

	String name, id;
	String latitude, longitude;
	String waitTime;
	int[] categoryList;
	String phone;
	String review_count;
	String rating;
	String state, address, zip_code;
	String keywords;
	int max_party, min_party;
	int category = 0007;
	LatLng latLng;
	String menu, message, more;

	public Restaurant(Parcel in) {
		super();
		readFromParcel(in);
	}

	public static final Parcelable.Creator<Restaurant> CREATOR = new Parcelable.Creator<Restaurant>() {
		public Restaurant createFromParcel(Parcel in) {
			return new Restaurant(in);
		}

		public Restaurant[] newArray(int size) {

			return new Restaurant[size];
		}

	};

	public void readFromParcel(Parcel in) {
		name = in.readString();
		id = in.readString();
		latitude = in.readString();
		longitude = in.readString();
		waitTime = in.readString();
		phone = in.readString();
		review_count = in.readString();
		rating = in.readString();
		state = in.readString();
		address = in.readString();
		zip_code = in.readString();
		keywords = in.readString();
		max_party = in.readInt();
		min_party = in.readInt();
		menu = in.readString();
		message = in.readString();
		more = in.readString();
		in.readIntArray(categoryList);
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {

		dest.writeString(name);
		dest.writeString(id);
		dest.writeString(latitude);
		dest.writeString(longitude);
		dest.writeString(waitTime);
		dest.writeString(phone);
		dest.writeString(review_count);
		dest.writeString(rating);
		dest.writeString(state);
		dest.writeString(address);
		dest.writeString(zip_code);
		dest.writeString(keywords);
		dest.writeInt(max_party);
		dest.writeInt(min_party);
		dest.writeString(menu);
		dest.writeString(message);
		dest.writeString(more);
		dest.writeIntArray(categoryList);
	}

	public Restaurant(String id, String name, String latitude,
			String longitude, String waitTime, int[] categoryList,
			String phone, String review_count, String rating, String state,
			String address, String zip_code, String keywords, int max_party,
			int min_party, String menu, String message, String more) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.waitTime = waitTime;
		this.name = name;
		this.categoryList = categoryList;
		this.id = id;
		this.phone = phone;
		this.review_count = review_count;
		this.rating = rating;
		this.state = state;
		this.address = address;
		this.zip_code = zip_code;
		this.keywords = keywords;
		this.max_party = max_party;
		this.min_party = min_party;
		this.menu = menu;
		this.message = message;
		this.more = more;
		latLng = new LatLng(Double.parseDouble(latitude),
				Double.parseDouble(longitude));
	}

	public void setMin_party(int min_party) {
		this.min_party = min_party;
	}

	public void setMax_party(int max_party) {
		this.max_party = max_party;
	}

	public int getMin_party() {
		return min_party;
	}

	public int getMax_party() {
		return max_party;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public void setCategoryList(int[] categoryList) {
		this.categoryList = categoryList;
	}

	public int[] getCategoryList() {
		return categoryList;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setWaitTime(String waitTime) {
		this.waitTime = waitTime;
	}

	public void setReview_count(String review_count) {
		this.review_count = review_count;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public LatLng getLatLng() {
		return latLng;
	}

	public void setLatLng(LatLng latLng) {
		this.latLng = latLng;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getWaitTime() {
		return waitTime;
	}

	public String getReview_count() {
		return review_count;
	}

	public String getRating() {
		return rating;
	}

	public String getPhone() {
		return phone;
	}

	public String getLongitude() {
		return longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public String getId() {
		return id;
	}

	public int getBloodType(int blood) {

		return 0;
	}

	public void setState(String state) {
		this.state = state;
	}

	public void setZip_code(String zip_code) {
		this.zip_code = zip_code;
	}

	public String getZip_code() {
		return zip_code;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getState() {
		return state;
	}

	public int getCategory() {
		return category;
	}

	public String getAddress() {
		return address;
	}

	public void setMore(String more) {
		this.more = more;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setMenu(String menu) {
		this.menu = menu;
	}

	public String getMore() {
		return more;
	}

	public String getMessage() {
		return message;
	}

	public String getMenu() {
		return menu;
	}
}