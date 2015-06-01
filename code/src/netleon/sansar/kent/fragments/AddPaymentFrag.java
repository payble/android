package netleon.sansar.kent.fragments;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import netleon.sansar.kent.R;
import netleon.sansar.kent.R.id;
import netleon.sansar.kent.R.layout;

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
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class AddPaymentFrag extends Fragment {

	Button button_save, button_edit, button_back;
	EditText edit_name, edit_num, edit_ccv;
	String str_card, str_name, str_num, str_ccv, str_exp, str_exp_month,
			str_exp_year, str_user_id, isDefault;
	Typeface face1;
	String add_card_url = "http://netleondev.com/kentapi/user/addusercards";
	ProgressDialog dialog;
	boolean str_ckeck = false;

	Spinner spin_card;

	String card_Titles[] = { "MasterCard", "Discover", "AmericanExpress",
			"VISA", "AmEx", "Capital One", "Chase", "First PREMIER",
			"Pentagon Federal" };

	SharedPreferences preferences;

	Calendar calendar;
	SimpleDateFormat date;
	String str_date;
	Date getting_date, current_date;
	TextView edit_exp;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		if (getActivity().getSupportFragmentManager().findFragmentByTag(
				"PaymentlistFrag") != null) {

			getActivity()
					.getSupportFragmentManager()
					.beginTransaction()
					.hide(getActivity().getSupportFragmentManager()
							.findFragmentByTag("PaymentlistFrag")).commit();
		}
		return inflater.inflate(R.layout.paymentfrag, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		preferences = getActivity().getSharedPreferences("NESSIE",
				getActivity().MODE_PRIVATE);

		str_user_id = preferences.getString("ID", "00");
		
		Log.e("", "str user id : " + str_user_id);
		face1 = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/Avenir.ttc");
		date = new SimpleDateFormat("yyyy/MM");
	
		button_edit = (Button) view.findViewById(R.id.butpayfrag_edit);
		button_back = (Button) view.findViewById(R.id.butpayfrag_back);
		button_save = (Button) view.findViewById(R.id.butpayfrag_save);

		spin_card = (Spinner) view.findViewById(R.id.spinpayfrag_card);
		edit_name = (EditText) view.findViewById(R.id.edtpayfrag_name);
		edit_num = (EditText) view.findViewById(R.id.edtpayfrag_num);
		edit_exp = (TextView) view.findViewById(R.id.edtpayfrag_exp);
		edit_ccv = (EditText) view.findViewById(R.id.edtpayfrag_ccv);

		button_edit.setText("DONE");
		button_save.setVisibility(View.VISIBLE);

		spin_card.setAdapter(new Custom_Spinner(getActivity(), card_Titles));

		spin_card.setEnabled(true);
		edit_ccv.setEnabled(true);
		edit_exp.setEnabled(true);
		edit_num.setEnabled(true);
		edit_name.setEnabled(true);

		button_edit.setTypeface(face1);
		button_back.setTypeface(face1);
		button_save.setTypeface(face1);

		edit_name.setTypeface(face1);
		edit_num.setTypeface(face1);
		edit_exp.setTypeface(face1);
		edit_ccv.setTypeface(face1);

		calendar = Calendar.getInstance();
		str_date = date.format(calendar.getTime());

		Log.e("", "currrent date : " + current_date);

		((TextView) view.findViewById(R.id.txt_pat_tag)).setTypeface(face1);

		button_edit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isValid()) {

					showMessage("Saved");
					new SendData().execute();
					getActivity().getSupportFragmentManager()
							.popBackStackImmediate();
				}
			}
		});

		button_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				try {
					InputMethodManager imm = (InputMethodManager) getActivity()
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
				} catch (Exception e) {
					// TODO: handle exception
				}

				getActivity().getSupportFragmentManager()
						.popBackStackImmediate();
			}
		});

		spin_card.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				str_card = card_Titles[position];
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				str_card = card_Titles[0];

			}
		});

		button_save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				try {
					current_date = date.parse(str_date);
					getting_date = date.parse(edit_exp.getText().toString());
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (isValid()) {
					if (current_date.before(getting_date)) {
						Log.d("", "we did it.");
						new SendData().execute();
					} else {
						Toast.makeText(getActivity(),
								"Invaild Expiration date", Toast.LENGTH_SHORT)
								.show();
					}

				}
			}
		});
		edit_exp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DialogFragment newFragment = new DateDialog();
				newFragment.show(getActivity().getSupportFragmentManager(),
						"datePicker");
			}
		});
	}

	public void showMessage(String message) {
		Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
	}

	private JSONObject margya(String user_id, String str_card, String str_name,
			String str_num, String str_cvv, String str_exp_year,
			String str_exp_month, String isdefault) {

		JSONObject obj = new JSONObject();
		try {
			obj.put("user_id", user_id);
			obj.put("cc_type", str_card);
			obj.put("cc_name", str_name);
			obj.put("cc_number", str_num);
			obj.put("cc_cvv", str_cvv);
			obj.put("cc_expiration_month", str_exp_month);
			obj.put("cc_expiration_year", str_exp_year);
			obj.put("is_default", isdefault);

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

			str_name = edit_name.getText().toString();
			str_num = edit_num.getText().toString();
			str_exp = edit_exp.getText().toString();
			str_ccv = edit_ccv.getText().toString();

			str_exp_year = str_exp.substring(0, 4);

			str_exp_month = str_exp.substring(str_exp.indexOf("/") + 1,
					str_exp.length());

			isDefault = "0";

			Log.e("", "str card : " + str_card);
			Log.e("", "str name : " + str_name);
			Log.e("", "str number : " + str_num);
			Log.e("", "str exper : " + str_exp);
			Log.e("", "str cvv : " + str_ccv);
			Log.e("", "str year : " + str_exp_year);
			Log.e("", "str months : " + str_exp_month);

			dialog = new ProgressDialog(getActivity());
			dialog.setCancelable(false);
			dialog.setMessage("Please wait ... ");
			dialog.setIndeterminate(false);
			dialog.show();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(String... params) {
			// Create a new HttpClient and Post Header
			HttpClient client = new DefaultHttpClient();
			String postURL = (add_card_url);
			HttpPut post = new HttpPut(postURL);

			post.addHeader("Content-type", "application/json");
			post.addHeader("Authorization", "aDRF@F#JG_a34-n3d");

			try {

				JSONObject json = margya(str_user_id, str_card, str_name,
						str_num, str_ccv, str_exp_year, str_exp_month,
						isDefault);

				Log.e("", "Kkkkkkkk : " + json.toString());

				try {
					post.setEntity(new StringEntity(json.toString()));
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				// Execute the HTTP Post Request
				HttpResponse response = client.execute(post);
				// Convert the response into a String
				HttpEntity resEntity = response.getEntity();
				if (resEntity != null) {
					String sts = EntityUtils.toString(resEntity);
					Log.i("RESPONSE", sts);

				}

			} catch (UnsupportedEncodingException uee) {
				uee.printStackTrace();
			} catch (ClientProtocolException cpe) {
				cpe.printStackTrace();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			return null;

		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			dialog.dismiss();
			try {

				InputMethodManager imm = (InputMethodManager) getActivity()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
			} catch (Exception e) {
			}

			Fragment fragment = getActivity().getSupportFragmentManager()
					.findFragmentByTag("PaymentlistFrag");

			getActivity().getSupportFragmentManager().beginTransaction()
					.detach(fragment).commit();
			getActivity().getSupportFragmentManager().beginTransaction()
					.attach(fragment).commit();

			getActivity().getSupportFragmentManager().popBackStackImmediate();

		}

	}

	boolean isValid() {
		if (edit_name.getText().length() < 4) {
			edit_name.setError("Enter Name");
			return false;
		} else if (edit_num.getText().length() < 16) {
			edit_num.setError("Enter valied card number");
			return false;
		} else if (edit_exp.getText().toString().length() <= 6) {
			edit_exp.setError("YYYY/MM format only");
			return false;
		} else if (edit_ccv.getText().length() >= 4) {
			edit_ccv.setError("Enter proper ccv number");
			return false;
		} else {
			return true;
		}

	}

	public class Custom_Spinner extends BaseAdapter {

		Context context;
		String card_Titles[];
		LayoutInflater inflator;

		public Custom_Spinner(Context context, String card_Titles[]) {
			this.context = context;
			this.card_Titles = card_Titles;
			inflator = LayoutInflater.from(context);
		}

		public int getCount() {
			return card_Titles.length;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View v = inflator.inflate(R.layout.orian, null);
			TextView txt_name = (TextView) v.findViewById(R.id.txter);
			txt_name.setTypeface(face1);
			txt_name.setText(card_Titles[position]);
			return v;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

	}

	@Override
	public void onDestroyView() {
		getActivity()
				.getSupportFragmentManager()
				.beginTransaction()
				.show(getActivity().getSupportFragmentManager()
						.findFragmentByTag("PaymentlistFrag")).commit();
		super.onDestroyView();
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

			if (month < 10) {
				Log.d("", "less then 10 : " + month);
				edit_exp.setText(year + "/" + "0" + month);

			} else {
				Log.d("", "above then 10 : " + month);
				edit_exp.setText(year + "/" + month);
			}

		}
	}
}
