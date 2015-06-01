package netleon.sansar.kent.fragments;

import java.util.ArrayList;

import netleon.sansar.kent.R;
import netleon.sansar.kent.base.KentValues;
import netleon.sansar.kent.base.Payment;
import netleon.sansar.kent.network.GetData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class RecieptFragment extends Fragment {

	String id, company, phone, address, name, payment, subtotal, tax, kent_fee,
			tip, total, date, status, rest_id, user_card_id;
	String URL_Payments, user_id;
	ProgressDialog proDialog;
	String card_ids[];
	String card_nos[];
	String[] vaStrings;
	Spinner spinner;
	String usercardid;
	EditText edit_tip;
	String na = "n/a";
	TextView txt_total, txt_sub;
	Float float_total, float_tip, float_subtotal, float_kentfee, float_tax,
			Total;
	Typeface face1;
	String Url_pay = "http://netleondev.com/kentapi/user/processpayment";

	ArrayList<Payment> payments = new ArrayList<Payment>();

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		Bundle bundle = getArguments();
		id = bundle.getString("id");
		company = bundle.getString("restaurant_name");
		phone = bundle.getString("phone_number");
		address = bundle.getString("address");
		name = bundle.getString("first_name");
		payment = bundle.getString("payment");
		subtotal = bundle.getString("sub_total");
		rest_id = bundle.getString("restaurant_id");
		tax = bundle.getString("tax_amount");
		user_card_id = bundle.getString("user_card_id");
		kent_fee = bundle.getString("kent_fee");
		tip = bundle.getString("tip");
		total = bundle.getString("invoice_amount");
		date = bundle.getString("date");
		status = bundle.getString("status");
		float_kentfee = Float.parseFloat(kent_fee);
		float_subtotal = Float.parseFloat(subtotal);
		float_tax = Float.parseFloat(tax);
		Total = Float.parseFloat(total);
		Log.d("", "subtotal: " + subtotal);
		return inflater.inflate(R.layout.receiptfragment, container, false);
	}

	@Override
	public void onDestroyView() {
		payments.clear();
		super.onDestroyView();
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		face1 = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/Avenir.ttc");
		Typeface face2 = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/LetterGothicStd.otf");

		user_id = getActivity().getSharedPreferences("NESSIE",
				getActivity().MODE_PRIVATE).getString("ID", "00");

		URL_Payments = "http://netleondev.com/kentapi/user/creditcards/userid/"
				+ user_id;

		((TextView) view.findViewById(R.id.txtrec_restname)).setText(company);
		((TextView) view.findViewById(R.id.txtrec_phone)).setText(phone);
		((TextView) view.findViewById(R.id.txtrec_address)).setText(address);
		((TextView) view.findViewById(R.id.txtrec_username)).setText(name);
		spinner = ((Spinner) view.findViewById(R.id.spinrec_payment));
		((TextView) view.findViewById(R.id.txtrec_tax)).setText(tax);
		((TextView) view.findViewById(R.id.txtrec_delivery)).setText(kent_fee);
		txt_total = (TextView) view.findViewById(R.id.txtrec_total);
		txt_sub = (TextView) view.findViewById(R.id.txtrec_totalsub);
		edit_tip = (EditText) view.findViewById(R.id.txtrec_tip);
		edit_tip.setText(tip);
		edit_tip.setTypeface(face1);

		subtotal = "$ " + subtotal;
		tax = "$ " + tax;
		kent_fee = "$ " + kent_fee;

		edit_tip.addTextChangedListener(new MyTextWatcher());

		if (address.equalsIgnoreCase(na) || address.equalsIgnoreCase("null")
				|| address.equalsIgnoreCase("")) {

			((TextView) view.findViewById(R.id.txtrec_address)).setText("n/a");

		} else {
			((TextView) view.findViewById(R.id.txtrec_address))
					.setText(address);
		}

		if (subtotal.equalsIgnoreCase(na) || subtotal.equalsIgnoreCase("null")
				|| subtotal.equalsIgnoreCase("")) {

			setFloatedText_grey(
					((TextView) view.findViewById(R.id.txt_subtotal)),
					((TextView) view.findViewById(R.id.txtrec_subtotal)),
					((TextView) view.findViewById(R.id.txtrec_subtotalsub)),
					subtotal);

		} else {

			setFloatedText(
					((TextView) view.findViewById(R.id.txtrec_subtotal)),
					((TextView) view.findViewById(R.id.txtrec_subtotalsub)),
					subtotal);

		}

		if (tax.equalsIgnoreCase(na) || tax.equalsIgnoreCase("null")
				|| tax.equalsIgnoreCase("")) {
			setFloatedText_grey((TextView) view.findViewById(R.id.txt_tax),
					((TextView) view.findViewById(R.id.txtrec_tax)),
					((TextView) view.findViewById(R.id.txtrec_taxsub)), tax);

		} else {

			setFloatedText(((TextView) view.findViewById(R.id.txtrec_tax)),
					((TextView) view.findViewById(R.id.txtrec_taxsub)), tax);

		}

		if (kent_fee.equalsIgnoreCase(na) || kent_fee.equalsIgnoreCase("null")
				|| kent_fee.equalsIgnoreCase("")) {
			setFloatedText_grey((TextView) view.findViewById(R.id.txt_delivey),
					((TextView) view.findViewById(R.id.txtrec_delivery)),
					((TextView) view.findViewById(R.id.txtrec_deliverysub)),
					kent_fee);
		} else {

			setFloatedText(
					((TextView) view.findViewById(R.id.txtrec_delivery)),
					((TextView) view.findViewById(R.id.txtrec_deliverysub)),
					kent_fee);
		}

		if (tip.equalsIgnoreCase(na) || tip.equalsIgnoreCase("null")
				|| tip.equalsIgnoreCase("")) {
			((EditText) view.findViewById(R.id.txtrec_tip)).setText(tip);
		}

		if (total.equalsIgnoreCase(na) || total.equalsIgnoreCase("null")
				|| total.equalsIgnoreCase("")) {
			setFloatedText_grey((TextView) view.findViewById(R.id.txt_total),
					((TextView) view.findViewById(R.id.txtrec_total)),
					((TextView) view.findViewById(R.id.txtrec_totalsub)), total);
		} else {

			setFloatedText(((TextView) view.findViewById(R.id.txtrec_total)),
					((TextView) view.findViewById(R.id.txtrec_totalsub)), total);
		}

		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				if (payments.size() > 0) {
					usercardid = card_ids[position];
					Log.e("", "selected id : " + usercardid);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				if (payments.size() > 0) {
					usercardid = card_ids[0];
					Log.e("", "nothing id : " + usercardid);
				} else {
					usercardid = "no cards";
				}
			}
		});
		((Button) view.findViewById(R.id.butreciept_back)).setTypeface(face1);
		((Button) view.findViewById(R.id.butreciept_back))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// getActivity()
						// .getSupportFragmentManager()
						// .beginTransaction()
						// .show(getActivity().getSupportFragmentManager()
						// .findFragmentByTag("RecieptlistFrag"))
						// .remove(getActivity()
						// .getSupportFragmentManager()
						// .findFragmentByTag("RecieptFragment"))
						// .commit();

						getActivity().getSupportFragmentManager()
								.popBackStackImmediate();
					}
				});

		((Button) view.findViewById(R.id.butreceipt_pay))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (!usercardid.equalsIgnoreCase("no cards")) {

							JSONObject object = create_JSON(id, user_id,
									usercardid, Float.toString(Total), edit_tip
											.getText().toString());
							new PayProcess(object.toString()).execute();
						} else {
							getActivity().runOnUiThread(new Runnable() {

								@Override
								public void run() {
									Toast.makeText(getActivity(),
											"Please add a card",
											Toast.LENGTH_SHORT).show();
								}
							});
						}
					}
				});

		((TextView) view.findViewById(R.id.txt_receicpts)).setTypeface(face2);
		((TextView) view.findViewById(R.id.txtrec_restname)).setTypeface(face1);
		((TextView) view.findViewById(R.id.txtrec_phone)).setTypeface(face1);
		((TextView) view.findViewById(R.id.txtrec_address)).setTypeface(face1);
		((TextView) view.findViewById(R.id.txtrec_username)).setTypeface(face1);

		((TextView) view.findViewById(R.id.txtrec_tax)).setTypeface(face1);
		((TextView) view.findViewById(R.id.txtrec_delivery)).setTypeface(face1);
		((TextView) view.findViewById(R.id.txtrec_tip)).setTypeface(face1);
		((TextView) view.findViewById(R.id.txtrec_subtotal)).setTypeface(face1);
		((TextView) view.findViewById(R.id.txtrec_total)).setTypeface(face1);

		((TextView) view.findViewById(R.id.txt_companty)).setTypeface(face2);
		((TextView) view.findViewById(R.id.txt_phone)).setTypeface(face1);
		((TextView) view.findViewById(R.id.txt_address)).setTypeface(face1);
		((TextView) view.findViewById(R.id.txt_name)).setTypeface(face1);
		((TextView) view.findViewById(R.id.txt_payment)).setTypeface(face1);
		((TextView) view.findViewById(R.id.txt_summry)).setTypeface(face1);
		((TextView) view.findViewById(R.id.txt_subtotal)).setTypeface(face1);
		((TextView) view.findViewById(R.id.txt_tax)).setTypeface(face1);
		((TextView) view.findViewById(R.id.txt_delivey)).setTypeface(face1);
		((TextView) view.findViewById(R.id.txt_tip)).setTypeface(face1);
		((TextView) view.findViewById(R.id.txtrec_tipsub)).setTypeface(face1);

		((TextView) view.findViewById(R.id.txt_total)).setTypeface(face1);
		((Button) view.findViewById(R.id.butreceipt_pay)).setTypeface(face1);
		((TextView) view.findViewById(R.id.txtreciept_tag)).setTypeface(face1);
		((TextView) view.findViewById(R.id.txtrec_tiptest)).setTypeface(face1);

		if (status.equalsIgnoreCase("processed")) {
			((Button) view.findViewById(R.id.butreceipt_pay))
					.setVisibility(View.GONE);
			edit_tip.setEnabled(false);
			((TextView) view.findViewById(R.id.txtrec_tiptest))
					.setVisibility(View.VISIBLE);
			((TextView) view.findViewById(R.id.txtrec_tipsub))
					.setVisibility(View.VISIBLE);
			((TextView) view.findViewById(R.id.test)).setVisibility(View.GONE);
			tip = "$ " + tip;
			if (tip.equalsIgnoreCase(na) || subtotal.equalsIgnoreCase("null")
					|| subtotal.equalsIgnoreCase("")) {

				setFloatedText_grey(
						((TextView) view.findViewById(R.id.txt_tip)),
						((TextView) view.findViewById(R.id.txtrec_tiptest)),
						((TextView) view.findViewById(R.id.txtrec_tipsub)), tip);

			} else {

				setFloatedText(
						((TextView) view.findViewById(R.id.txtrec_tiptest)),
						((TextView) view.findViewById(R.id.txtrec_tipsub)), tip);

			}
			edit_tip.setVisibility(View.GONE);
			spinner.setEnabled(false);
		}

		new GetUserCards().execute();
	}

	Float calculate_total(Float float_subtotal, Float float_tax,
			Float float_kent_fee, Float float_tip) {
		Float float_total;
		float_total = float_subtotal + float_kent_fee + float_tax + float_tip;
		return float_total;
	}

	private void setFloatedText(TextView view, TextView view_sub, String str) {
		StringBuilder sb = new StringBuilder(str);
		view.setText(sb.substring(0, str.indexOf(".")));
		view_sub.setText(sb.substring(str.indexOf("."), sb.length()));
	}

	private void setFloatedText_grey(TextView tag, TextView view,
			TextView view_sub, String str) {
		view.setText(str);
		tag.setTextColor(Color.parseColor("#949494"));
		view.setTextColor(Color.parseColor("#949494"));
		view_sub.setText("");
	}

	class GetUserCards extends AsyncTask<String, String, String> {

		String result = null;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			proDialog = new ProgressDialog(getActivity());
			proDialog.setMessage("Please wait...");
			proDialog.setIndeterminate(false);
			proDialog.setCancelable(false);
			proDialog.show();
		}

		protected String doInBackground(String... args) {
			GetData getData = new GetData();
			result = getData.getDataFromServer(URL_Payments);
			return null;
		}

		protected void onPostExecute(String file_url) {
			proDialog.dismiss();
			if (result != null) {
				try {

					JSONArray array = new JSONArray(result);
					for (int i = 0; i < array.length(); i++) {
						System.out.println(i + " : " + array.getString(i));
					}

					for (int i = 0; i < array.length(); i++) {

						JSONObject c = array.getJSONObject(i);

						String id = c.getString("id");
						String card_typee = c.getString("cc_type");
						Log.e("", "card type : : " + card_typee);
						String name = c.getString("cc_name");
						String cc_no = c.getString("cc_number");
						String expiration_month = c
								.getString("cc_expiration_month");
						String expiration_year = c
								.getString("cc_expiration_year");
						String ccv = c.getString("cc_cvv");
						Boolean is_default;

						if (c.getString("is_default").equalsIgnoreCase("1")) {
							is_default = true;
						} else {
							is_default = false;
						}

						Payment payment = new Payment(id, card_typee, name,
								cc_no, expiration_year, expiration_month, ccv,
								is_default);
						payments.add(payment);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(getActivity(),
								"Please check network connection",
								Toast.LENGTH_SHORT).show();
					}
				});
				getActivity().getSupportFragmentManager().popBackStack();
			}

			if (payments.size() > 0) {

				vaStrings = new String[payments.size()];
				card_ids = new String[payments.size()];
				card_nos = new String[payments.size()];
				for (int spinn = 0; spinn < payments.size(); spinn++) {

					vaStrings[spinn] = payments.get(spinn).getCard_type();
					card_ids[spinn] = payments.get(spinn).getId();
					String no = payments.get(spinn).getCc_no().toString();
					int length = no.length();
					no = no.substring(length - 4, length);
					card_nos[spinn] = no;
				}

				for (int i = 0; i < card_nos.length; i++) {
					Log.e("", "card " + i + " : " + card_nos[i]);
				}

				spinner.setAdapter(new Custom_Spinner(getActivity(), vaStrings,
						card_nos));
			} else {
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(getActivity(),
								"No cards added yet , Please add a card",
								Toast.LENGTH_SHORT).show();
					}
				});

				vaStrings = new String[1];
				vaStrings[0] = "No Cards";
				spinner.setAdapter(new Custom_NullSpinner(getActivity(),
						vaStrings));
				// spinner.setEnabled(false);wq
				usercardid = "no cards";
			}

			System.out.println("hua");

			if (!status.equalsIgnoreCase("processed")) {
				for (int i = 0; i < payments.size(); i++) {
					Log.e("", "he : " + i);
					if (payments.get(i).is_default) {
						spinner.setSelection(i);
						Log.e("", "spinner pos selected : " + i);
					}
				}
			} else {

				int cardedid = 0;
				try {
					cardedid = Integer.parseInt(user_card_id);
				} catch (Exception e) {
					// TODO: handle exception
				}

				if (cardedid == 0) {
					for (int i = 0; i < payments.size(); i++) {
						Log.e("", "he : " + i);
						if (payments.get(i).is_default) {
							spinner.setSelection(i);
							Log.e("", "spinner pos selected : " + i);
						}

					}
				} else {
					for (int i = 0; i < payments.size(); i++) {

						int id = Integer.parseInt(payments.get(i).getId());
						if (id == cardedid) {
							spinner.setSelection(i);
						}

					}
				}
			}
			KentValues.isLoading = false;
		}
	}

	public class Custom_Spinner extends BaseAdapter {

		Context context;
		String card_Titles[];
		String[] card_nos;
		LayoutInflater inflator;

		public Custom_Spinner(Context context, String card_Titles[],
				String[] card_nos) {
			this.context = context;
			this.card_Titles = card_Titles;
			this.card_nos = card_nos;
			inflator = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
			txt_name.setText(card_Titles[position].concat(" *").concat(
					card_nos[position]));
			return v;
		}

		// @Override
		// public View getDropDownView(int position, View convertView,
		// ViewGroup parent) {
		// View v = inflator.inflate(R.layout.orian, null);
		// TextView txt_name = (TextView) v.findViewById(R.id.txter);
		// txt_name.setTypeface(face1);
		// txt_name.setText(card_Titles[position]);
		// return v;
		// }

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		// public void setCustomText(String customText) {
		// mCustomText = customText;
		// notifyDataSetChanged();
		// }

	}

	public class Custom_NullSpinner extends BaseAdapter {

		Context context;
		String card_Titles[];
		LayoutInflater inflator;

		public Custom_NullSpinner(Context context, String card_Titles[]) {
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

	class PayProcess extends AsyncTask<String, String, String> {

		String data;
		String response = null;

		public PayProcess(String data) {
			this.data = data;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			proDialog = new ProgressDialog(getActivity());
			proDialog.setMessage("Please wait...");
			proDialog.setIndeterminate(false);
			proDialog.setCancelable(false);
			proDialog.show();
		}

		protected String doInBackground(String... args) {
			GetData data = new GetData();
			response = data.postDataToServer(Url_pay, this.data);
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			proDialog.dismiss();
			if (response != null) {
				try {
					JSONObject object = new JSONObject(response);
					if (object.has("error")) {
						final String error = object.getString("error");
						getActivity().runOnUiThread(new Runnable() {

							@Override
							public void run() {
								Toast.makeText(getActivity(), error,
										Toast.LENGTH_LONG).show();
							}
						});

					} else {

						getActivity().runOnUiThread(new Runnable() {

							@Override
							public void run() {

								Toast.makeText(getActivity(),
										"Payment Processed!", Toast.LENGTH_LONG)
										.show();

								Fragment fragment = new AddReviewFragment();
								Bundle bundle = new Bundle();
								bundle.putString("company", company);
								bundle.putString("restaurant_id", rest_id);

								fragment.setArguments(bundle);
								getActivity()
										.getSupportFragmentManager()
										.beginTransaction()
										.hide(getActivity()
												.getSupportFragmentManager()
												.findFragmentByTag(
														"RecieptFragment"))
										.add(R.id.container_top, fragment,
												"AddReviewFragment")
										.addToBackStack("AddReviewFragment")
										.commit();
							}
						});
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
	}

	private JSONObject create_JSON(String receipt_id, String user_id,
			String user_card_id, String total_amount, String tip_amount) {
		JSONObject obj = new JSONObject();
		try {
			obj.put("receipt_id", receipt_id);
			obj.put("user_id", user_id);
			obj.put("user_card_id", user_card_id);
			obj.put("tip_amount", tip_amount);
			obj.put("total_amount", total_amount);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj;
	}

	public class MyTextWatcher implements TextWatcher {
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

			if (count > 0) {
				try {
					Log.e("", "inside if-loop: ");
					float_tip = Float.parseFloat(s.toString());
				} catch (Exception e) {
					Toast.makeText(getActivity(), "Only numbers",
							Toast.LENGTH_SHORT).show();
				}

				Total = calculate_total(float_subtotal, float_tax,
						float_kentfee, float_tip);
				txt_total.setText(Float.toString(Total));
				setFloatedText(txt_total, txt_sub, Float.toString(Total));
				Log.e("", "toatal now : " + total);
			}
		}

		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {

		}

		public void afterTextChanged(Editable arg0) {
		}
	}

}
