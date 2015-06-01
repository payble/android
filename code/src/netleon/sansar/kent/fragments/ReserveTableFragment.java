package netleon.sansar.kent.fragments;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import netleon.sansar.kent.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class ReserveTableFragment extends Fragment {

	TextView txt_start_date, txt_end_date, txt_start_time, txt_end_time;
	EditText edit_partysize;
	String str_strat_date, str_end_date, str_partysize, str_strat_time,
			str_end_time;

	String date, time;
	ProgressDialog proDialog;
	Button btn_submit;

	String url_create_revers = "http://netleondev.com/kentapi/restaurant/reservation";
	String rest_id;
	String str_user_id, str_start_datetym, str_end_datetym;
	boolean succes = false;
	boolean isCheck = false;
	int partysize;
	String error;
	SharedPreferences preferences;
	ImageView cal_1, cal_2, clk_1, clk_2;

	TextView send_start_time, send_end_time, send_start_date, send_end_date;

	SimpleDateFormat displayFormat, sendFormat, timeFormat;
	Date date1, date2, time2, time3, date3, date4;
	int max_party;
	int min_party;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.reserve_table, container, false);
	}

	@Override
	public void onViewCreated(final View view,
			@Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		Typeface face1 = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/Avenir.ttc");

		preferences = getActivity().getSharedPreferences("NESSIE",
				getActivity().MODE_PRIVATE);

		str_user_id = preferences.getString("ID", "00");
		rest_id = getArguments().getString("REST_ID");
		min_party = getArguments().getInt("min_party_size");
		max_party = getArguments().getInt("max_party_size");

		Log.e("", "this is rest id :" + rest_id);
		Log.e("", "min party  :" + min_party);
		Log.e("", "max party :" + max_party);

		send_start_date = new TextView(getActivity());
		send_end_date = new TextView(getActivity());
		send_start_time = new TextView(getActivity());
		send_end_time = new TextView(getActivity());

		txt_start_date = (TextView) view.findViewById(R.id.txt_start_date);
		txt_end_date = (TextView) view.findViewById(R.id.txt_end_date);
		txt_start_time = (TextView) view.findViewById(R.id.txt_start_time);
		txt_end_time = (TextView) view.findViewById(R.id.txt_end_time);

		cal_1 = (ImageView) view.findViewById(R.id.image_calander_1);
		cal_2 = (ImageView) view.findViewById(R.id.image_calander_2);
		clk_1 = (ImageView) view.findViewById(R.id.image_clock_1);
		clk_2 = (ImageView) view.findViewById(R.id.image_clock_2);

		edit_partysize = (EditText) view.findViewById(R.id.edit_party_size);
		btn_submit = (Button) view.findViewById(R.id.btn_submit);
		txt_start_date.setTypeface(face1);
		txt_end_date.setTypeface(face1);
		txt_start_time.setTypeface(face1);
		txt_end_time.setTypeface(face1);

		btn_submit.setTypeface(face1);

		cal_1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				date = "START_DATE";
				DialogFragment newFragment = new DateDialog();
				newFragment.show(getActivity().getSupportFragmentManager(),
						"datePicker");

			}
		});

		cal_2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				date = "END_DATE";
				DialogFragment newFragment = new DateDialog();
				newFragment.show(getActivity().getSupportFragmentManager(),
						"datePicker");

			}
		});

		clk_1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				time = "START_TIME";
				DialogFragment newFragment = new TimeDialog();
				newFragment.show(getActivity().getSupportFragmentManager(),
						"timePicker");

			}
		});

		clk_2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				time = "END_TIME";
				DialogFragment newFragment = new TimeDialog();
				newFragment.show(getActivity().getSupportFragmentManager(),
						"timePicker");
			}
		});

		btn_submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.e("", "click inside onclick");
				try {
					String display = "mm/dd/yyyy";
					String send = "yyyy-mm-dd";
					String time = "HH:mm";

					displayFormat = new SimpleDateFormat(display);
					sendFormat = new SimpleDateFormat(send);
					timeFormat = new SimpleDateFormat(time);

					date1 = displayFormat.parse(txt_start_date.getText()
							.toString());
					date2 = displayFormat.parse(txt_end_date.getText()
							.toString());

					date3 = sendFormat.parse(send_start_date.getText()
							.toString());
					date4 = sendFormat
							.parse(send_end_date.getText().toString());

					time2 = timeFormat.parse(send_start_time.getText()
							.toString());
					time3 = timeFormat
							.parse(send_end_time.getText().toString());

				} catch (ParseException e) {

					e.printStackTrace();
				}

				if (date1 != null && date2 != null && time2 != null
						&& time3 != null) {
					
					if (date1.equals(date2)) {
						
						Log.e("", "equals");

						try {
							if (edit_partysize.getText().toString()
									.equalsIgnoreCase("")) {
								getActivity().runOnUiThread(new Runnable() {
									public void run() {
										Toast.makeText(getActivity(),
												"Please add party size",
												Toast.LENGTH_SHORT).show();
									}
								});
							} else {
								partysize = Integer.parseInt(edit_partysize
										.getText().toString());
							}

						} catch (Exception e) {
							getActivity().runOnUiThread(new Runnable() {
								public void run() {
									Toast.makeText(getActivity(),
											"Please enter valid number",
											Toast.LENGTH_SHORT).show();
								}
							});
						}

						if (time2.before(time3)) {

							if (isPartySizeAvail(partysize)) {
								new SendData().execute();
							}
						}

						else {
							Toast.makeText(getActivity(), "Select proper time",
									Toast.LENGTH_SHORT).show();
						}

					} else if (!(date1.after(date2))) {
						Log.e("", "!after");
						try {
							if (edit_partysize.getText().toString()
									.equalsIgnoreCase("")) {
								getActivity().runOnUiThread(new Runnable() {
									public void run() {
										Toast.makeText(getActivity(),
												"Please add party size",
												Toast.LENGTH_SHORT).show();
									}
								});
							} else {
								partysize = Integer.parseInt(edit_partysize
										.getText().toString());
							}

						} catch (Exception e) {
							getActivity().runOnUiThread(new Runnable() {
								public void run() {
									Toast.makeText(getActivity(),
											"Please enter valid number",
											Toast.LENGTH_SHORT).show();
								}
							});
						}
						
						new SendData().execute();

					} else {
						Toast.makeText(getActivity(), "Select proper date",
								Toast.LENGTH_SHORT).show();
					}
				}

				InputMethodManager imm = (InputMethodManager) getActivity()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm.isActive()) {
					imm.hideSoftInputFromWindow(
							edit_partysize.getWindowToken(), 0);
				}

			}
		});

		((Button) view.findViewById(R.id.butpayfrag_back))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						getActivity().getSupportFragmentManager()
								.popBackStackImmediate();
					}
				});
	}

	boolean isVaild() {

		if (txt_start_date.getText().toString() == "Select start date") {
			txt_start_date.setError("Please select date.");
			return false;
		} else if (txt_start_time.getText().toString()
				.equalsIgnoreCase("Select start time")) {
			txt_start_time.setError("Please select date.");
			return false;
		} else if (txt_end_date.getText().toString()
				.equalsIgnoreCase("Select end date")) {
			txt_end_date.setError("Please select date.");
			return false;
		} else if (txt_end_time.getText().toString()
				.equalsIgnoreCase("Select end time")) {
			txt_end_time.setError("Please select date.");
			return false;
		}

		else {
			return true;
		}

	}

	public class DateDialog extends DialogFragment implements
			DatePickerDialog.OnDateSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);

			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		public void onDateSet(DatePicker view, int year, int month, int day) {
			month = month + 1;
			if (date.equalsIgnoreCase("START_DATE")) {

				txt_start_date.setText(month + "/" + day + "/" + year);
				txt_start_date.setTextColor(Color.parseColor("#333333"));

				send_start_date.setText(year + "-" + month + "-" + day);
				str_strat_date = send_start_date.getText().toString();

				Log.d("", "display start date : "
						+ txt_start_date.getText().toString());
				Log.d("", "send strat date : " + str_strat_date);

			} else if (date.equalsIgnoreCase("END_DATE")) {

				txt_end_date.setText(month + "/" + day + "/" + year);
				txt_end_date.setTextColor(Color.parseColor("#333333"));
				send_end_date.setText(year + "-" + month + "-" + day);
				str_end_date = send_end_date.getText().toString();

				Log.d("", "display end date : "
						+ txt_end_date.getText().toString());
				Log.d("", "send end date : " + str_end_date);
			}
		}
	}

	public class TimeDialog extends DialogFragment implements
			TimePickerDialog.OnTimeSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			final Calendar c = Calendar.getInstance();
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);

			return new TimePickerDialog(getActivity(), this, hour, minute,
					DateFormat.is24HourFormat(getActivity()));
		}

		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

			int hour;
			String am_pm;
			if (hourOfDay > 12) {
				hour = hourOfDay - 12;
				am_pm = "PM";
			} else {
				hour = hourOfDay;
				am_pm = "AM";
			}

			if (minute < 10) {
				if (time.equalsIgnoreCase("START_TIME")) {

					send_start_time.setText(hourOfDay + ":" + "0" + minute);
					send_start_time.setTextColor(Color.parseColor("#333333"));
					txt_start_time.setText(hour + ":" + "0" + minute + " "
							+ am_pm);
					str_strat_time = send_start_time.getText().toString();

					Log.e("", "strat time : " + str_strat_time);
				} else if (time.equalsIgnoreCase("END_TIME")) {

					send_end_time.setText(hourOfDay + ":" + "0" + minute);
					send_end_time.setTextColor(Color.parseColor("#333333"));
					txt_end_time.setText(hour + ":" + "0" + minute + " "
							+ am_pm);
					str_end_time = send_end_time.getText().toString();

					Log.e("", "end time : " + str_end_time);
				}
			} else {
				if (time.equalsIgnoreCase("START_TIME")) {

					send_start_time.setText(hourOfDay + ":" + minute);
					send_start_time.setTextColor(Color.parseColor("#333333"));
					txt_start_time.setText(hour + ":" + minute + " " + am_pm);
					str_strat_time = send_start_time.getText().toString();

					Log.e("", "strat time : " + str_strat_time);
				} else if (time.equalsIgnoreCase("END_TIME")) {

					send_end_time.setText(hourOfDay + ":" + minute);
					send_end_time.setTextColor(Color.parseColor("#333333"));
					txt_end_time.setText(hour + ":" + minute + " " + am_pm);
					str_end_time = send_end_time.getText().toString();

					Log.e("", "end time : " + str_end_time);
				}
			}

		}
	}

	private JSONObject margya(String rest_id, String user_id,
			String party_size, String startdate, String enddate) {

		JSONObject obj = new JSONObject();
		try {
			obj.put("restaurant_id", rest_id);
			obj.put("user_id", user_id);
			obj.put("party_size", party_size);
			obj.put("start_datetime", startdate);
			obj.put("end_datetime", enddate);

			System.out.print(obj);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj;
	}

	private class SendData extends AsyncTask<String, Integer, Void> {
		@Override
		protected void onPreExecute() {

			str_start_datetym = str_strat_date.concat(" " + str_strat_time);
			str_end_datetym = str_end_date.concat(" " + str_end_time);

			super.onPreExecute();
			proDialog = new ProgressDialog(getActivity());
			proDialog.setMessage("Please wait...");
			proDialog.setIndeterminate(false);
			proDialog.setCancelable(false);
			proDialog.show();
		}

		@Override
		protected Void doInBackground(String... params) {

			HttpClient client = new DefaultHttpClient();
			String postURL = (url_create_revers);
			HttpPut post = new HttpPut(postURL);

			post.addHeader("Content-type", "application/json");
			post.addHeader("Authorization", "aDRF@F#JG_a34-n3d");

			try {

				JSONObject json = margya(rest_id, str_user_id,
						Integer.toString(partysize), str_start_datetym + ":"
								+ "00", str_end_datetym + ":" + "00");
				Log.e("", "Kkkkkkkk : " + json.toString());
				try {
					post.setEntity(new StringEntity(json.toString()));
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
				HttpResponse response = client.execute(post);
				HttpEntity resEntity = response.getEntity();

				if (resEntity != null) {
					String sts = EntityUtils.toString(resEntity);
					Log.i("RESPONSE", sts);
					if (sts.matches("[0-9]+") && sts.length() > 1) {

						Log.e("", "we got it:");
						succes = true;
					} else {
						JSONObject jsonObject = new JSONObject(sts);
						error = jsonObject.getString("error").toString();
						isCheck = true;
					}

					Log.e("", ":" + sts);

				}

			} catch (UnsupportedEncodingException uee) {
				uee.printStackTrace();
			} catch (ClientProtocolException cpe) {
				cpe.printStackTrace();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;

		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			proDialog.dismiss();
			if (isCheck) {
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT)
								.show();
						getActivity().getSupportFragmentManager()
								.popBackStackImmediate();
					}
				});
			}
			if (succes) {

				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						Toast.makeText(getActivity(), "Thanks for Booking",
								Toast.LENGTH_SHORT).show();

						new Handler().postDelayed(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								getActivity().getSupportFragmentManager()
										.popBackStackImmediate();
							}
						}, 800);

					}
				});

			}
			super.onPostExecute(result);
		}
	}

	boolean isPartySizeAvail(int got) {

		if (min_party <= got && max_party >= got) {

			return true;

		} else {
			Log.e("", "min party2  :" + min_party);
			Log.e("", "max party2 :" + max_party);
			Toast.makeText(
					getActivity(),
					"Party size should be between min " + min_party
							+ " to max size " + max_party, Toast.LENGTH_SHORT)
					.show();
			return false;
		}

	}
}
