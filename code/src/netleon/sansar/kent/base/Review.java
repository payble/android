package netleon.sansar.kent.base;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import android.util.Log;

public class Review {

	String id, user_id, rest_id, rating, comment, time;
	Date date = new Date();
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
	DateTime dateTime;

	public Review(String id, String user_id, String rest_id, String rating,
			String comment, String time) {

		Log.e("", "1");
		this.id = id;
		Log.e("", "1");
		this.user_id = user_id;
		Log.e("", "1");
		this.rest_id = rest_id;
		Log.e("", "1");
		this.rating = rating;
		Log.e("", "1");
		this.time = time;
		Log.e("", "1");
		this.comment = comment;
		Log.e("", "2");

		try {
			date = dateFormat.parse(time);
			Log.e("", "3");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.e("", "4");
		dateTime = formatter.parseDateTime(time);
		Log.e("", "5");
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public void setRest_id(String rest_id) {
		this.rest_id = rest_id;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getUser_id() {
		return user_id;
	}

	public String getRest_id() {
		return rest_id;
	}

	public String getRating() {
		return rating;
	}

	public String getId() {
		return id;
	}

	public String getComment() {
		return comment;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getTime() {
		return time;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getDate() {
		return date;
	}
	
	public DateTime getDateTime() {
		return dateTime;
	}
}
