package netleon.sansar.kent.fragments;

import java.util.ArrayList;
import java.util.List;

import netleon.sansar.kent.R;
import netleon.sansar.kent.base.KentValues;
import netleon.sansar.kent.base.Receipt;
import netleon.sansar.kent.network.JSONParser;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class Recieptlistfrag extends Fragment {

	ProgressDialog proDialog;

	String URL_Reciepts;
	ArrayList<Receipt> receipts = new ArrayList<Receipt>();
	ArrayList<Receipt> pendingreciepts = new ArrayList<Receipt>();
	ArrayList<Receipt> processedreciepts = new ArrayList<Receipt>();
	ListView listView;
	SharedPreferences preferences;
	String user_id;
	String delivery;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		proDialog = new ProgressDialog(getActivity());
		proDialog.setMessage("Please wait...");
		((AlertDialog) proDialog)
				.setOnShowListener(new DialogInterface.OnShowListener() {
					@Override
					public void onShow(DialogInterface dialog) {
						final int idAlertTitle = getActivity().getResources()
								.getIdentifier("alertTitle", "id", "android");
						TextView textDialog = (TextView) ((AlertDialog) dialog)
								.findViewById(idAlertTitle);

						textDialog.setTypeface(Typeface.createFromAsset(
								getActivity().getAssets(), "fonts/Avenir.ttc"));
						;
					}
				});
		proDialog.setIndeterminate(false);
		proDialog.setCancelable(false);
		proDialog.show();
		return inflater.inflate(R.layout.recieptlistfrag, container, false);
	}

	@Override
	public void onDestroy() {
		receipts.clear();
		processedreciepts.clear();
		pendingreciepts.clear();
		super.onDestroy();
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		listView = (ListView) view.findViewById(R.id.listreceiptlist);
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		View footerView = inflater.inflate(R.layout.footer_recieptlist, null,
				false);
		listView.addFooterView(footerView);

		preferences = getActivity().getSharedPreferences("NESSIE",
				getActivity().MODE_PRIVATE);

		user_id = preferences.getString("ID", "00");
		URL_Reciepts = "http://netleondev.com/kentapi/user/receipts/userid/"
				+ user_id;

		Typeface face1 = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/Avenir.ttc");
		Typeface face2 = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/LetterGothicStd.otf");

		((TextView) view.findViewById(R.id.receipt_list)).setTypeface(face2);
		((Button) view.findViewById(R.id.butreceiptlist_back))
				.setTypeface(face1);
		((TextView) footerView.findViewById(R.id.txtreciept_tag))
				.setTypeface(face1);
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				if (position == 0) {
				} else if (position > 0 && position <= pendingreciepts.size()) {
					KentValues.isLoading = true;
					Receipt receipt = pendingreciepts.get(position - 1);
					Bundle bundle = new Bundle();

					bundle.putString("id", receipt.getId());
					bundle.putString("restaurant_id", receipt.getRest_id());
					bundle.putString("restaurant_name", receipt.getCompany());
					bundle.putString("phone_number", receipt.getPhone());
					bundle.putString("address", receipt.getAddress());
					bundle.putString("first_name", receipt.getName());
					bundle.putString("user_card_id", receipt.getUser_card_id());
					bundle.putString("payment", receipt.getPayment());
					bundle.putString("sub_total", receipt.getSubtotal());
					bundle.putString("tax_amount", receipt.getTax());
					Log.e("", "list tax : " + receipt.getTax());
					bundle.putString("kent_fee", receipt.getKent_fee());
					bundle.putString("delivery", delivery);
					bundle.putString("tip", receipt.getTip());
					bundle.putString("invoice_amount", receipt.getTotal());
					bundle.putString("date", receipt.getDate());
					bundle.putString("status", receipt.getStatus());

					Fragment fragment = new RecieptFragment();
					fragment.setArguments(bundle);

					getActivity()
							.getSupportFragmentManager()
							.beginTransaction()
							.hide(getActivity().getSupportFragmentManager()
									.findFragmentByTag("RecieptlistFrag"))
							.add(R.id.container_top, fragment,
									"RecieptFragment")
							.addToBackStack("RecieptFragment").commit();

				} else if (position == pendingreciepts.size() + 1) {
				} else {
					KentValues.isLoading = true;
					Receipt receipt = processedreciepts.get(position
							- (pendingreciepts.size() + 2));
					Bundle bundle = new Bundle();
					bundle.putString("id", receipt.getId());
					bundle.putString("restaurant_id", receipt.getRest_id());
					bundle.putString("restaurant_name", receipt.getCompany());
					bundle.putString("phone_number", receipt.getPhone());
					bundle.putString("address", receipt.getAddress());
					bundle.putString("first_name", receipt.getName());
					bundle.putString("user_card_id", receipt.getUser_card_id());
					bundle.putString("payment", receipt.getPayment());
					bundle.putString("sub_total", receipt.getSubtotal());
					bundle.putString("tax_amount", receipt.getTax());
					Log.e("", "list tax : " + receipt.getTax());
					bundle.putString("kent_fee", receipt.getKent_fee());
					bundle.putString("tip", receipt.getTip());
					bundle.putString("invoice_amount", receipt.getTotal());
					bundle.putString("date", receipt.getDate());
					bundle.putString("status", receipt.getStatus());
					Fragment fragment = new RecieptFragment();
					fragment.setArguments(bundle);

					getActivity()
							.getSupportFragmentManager()
							.beginTransaction()
							.hide(getActivity().getSupportFragmentManager()
									.findFragmentByTag("RecieptlistFrag"))
							.add(R.id.container_top, fragment,
									"RecieptFragment")
							.addToBackStack("RecieptFragment").commit();
				}
			}
		});

		((Button) view.findViewById(R.id.butreceiptlist_back))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						getActivity().getSupportFragmentManager()
								.popBackStackImmediate();
						receipts.clear();
						processedreciepts.clear();
						pendingreciepts.clear();
					}
				});
		new LoadAllData().execute();
	}

	public class RecieptAdapter extends BaseAdapter {

		LayoutInflater mInflater;
		Context mContext;
		ArrayList<Receipt> pendingreciepts, processedreciepts;

		public RecieptAdapter(Context context,
				ArrayList<Receipt> pendingreciepts,
				ArrayList<Receipt> processedreciepts) {
			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.mContext = context;
			this.pendingreciepts = pendingreciepts;
			this.processedreciepts = processedreciepts;
		}

		@Override
		public Object getItem(int position) {
			return receipts.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public int getCount() {
			return pendingreciepts.size() + 2 + processedreciepts.size();
		}

		@SuppressLint("NewApi")
		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			Typeface face3 = Typeface.createFromAsset(
					getActivity().getAssets(), "fonts/Avenir.ttc");

			TextView txt_date = null;
			TextView txt_name = null;
			TextView txt_total = null;
			TextView txt_totalsub = null;

			Log.e("", "convert view is null");
			Log.e("", "convert pos is : " + position);

			if (position == 0) {

				if (pendingreciepts.size() > 0) {
					Log.e("", "positioned : " + position);
					convertView = mInflater.inflate(
							R.layout.header_recieptlist, parent, false);
					((TextView) convertView.findViewById(R.id.headerrec_head))
							.setTypeface(face3);
				} else {
					convertView = mInflater.inflate(R.layout.blank, parent,
							false);
				}

			} else if (position > 0 && position <= pendingreciepts.size()) {
				if (pendingreciepts.size() > 0) {

					if (position == pendingreciepts.size()) {
						convertView = mInflater.inflate(
								R.layout.custom_recieptlist, parent, false);
						txt_date = (TextView) convertView
								.findViewById(R.id.txtcustomreciept_date);
						txt_name = (TextView) convertView
								.findViewById(R.id.txtcustomreciept_name);
						txt_total = (TextView) convertView
								.findViewById(R.id.txtcustomreciept_total);
						txt_totalsub = (TextView) convertView
								.findViewById(R.id.txtcustomreciept_totalsub);

						txt_date.setTypeface(face3);
						txt_name.setTypeface(face3);
						txt_total.setTypeface(face3);
						txt_totalsub.setTypeface(face3);

						String date = pendingreciepts.get(position - 1)
								.getDate();

						txt_date.setText(date);
						txt_name.setText(pendingreciepts.get(position - 1)
								.getName());

						String total, total_1, total_2;
						total = pendingreciepts.get(position - 1).getTotal();
						if (total.equalsIgnoreCase("n/a")
								|| total.equalsIgnoreCase("null")
								|| total.equalsIgnoreCase("")) {
							txt_total.setText(total);
							txt_total.setTextColor(Color.parseColor("#949494"));
							txt_totalsub.setText("");
						} else {
							StringBuilder sb = new StringBuilder(total);

							total_1 = sb.substring(0, total.indexOf("."));
							total_2 = sb.substring(total.indexOf("."),
									sb.length());
							System.out.println("::::::: " + total_1);
							System.out.println("::::::: " + total_2);
							txt_total.setText(total_1);
							txt_totalsub.setText(total_2);
						}
					} else {

						Log.e("", "pendingreciepts positioned : " + position);
						convertView = mInflater.inflate(
								R.layout.custom_recieptlistmargined, parent,
								false);
						txt_date = (TextView) convertView
								.findViewById(R.id.txtcustomreciept_date);
						txt_name = (TextView) convertView
								.findViewById(R.id.txtcustomreciept_name);
						txt_total = (TextView) convertView
								.findViewById(R.id.txtcustomreciept_total);
						txt_totalsub = (TextView) convertView
								.findViewById(R.id.txtcustomreciept_totalsub);

						txt_date.setTypeface(face3);
						txt_name.setTypeface(face3);
						txt_total.setTypeface(face3);
						txt_totalsub.setTypeface(face3);

						String date = pendingreciepts.get(position - 1)
								.getDate();

						txt_date.setText(date);
						txt_name.setText(pendingreciepts.get(position - 1)
								.getName());

						String total, total_1, total_2;
						total = pendingreciepts.get(position - 1).getTotal();
						if (total.equalsIgnoreCase("n/a")
								|| total.equalsIgnoreCase("null")
								|| total.equalsIgnoreCase("")) {
							txt_total.setText(total);
							txt_total.setTextColor(Color.parseColor("#949494"));
							txt_totalsub.setText("");
						} else {
							StringBuilder sb = new StringBuilder(total);

							total_1 = sb.substring(0, total.indexOf("."));
							total_2 = sb.substring(total.indexOf("."),
									sb.length());
							System.out.println("::::::: " + total_1);
							System.out.println("::::::: " + total_2);
							txt_total.setText(total_1);
							txt_totalsub.setText(total_2);
						}

					}
				}
			} else if (position == pendingreciepts.size() + 1) {
				if (processedreciepts.size() > 0) {
					Log.e("", "sec positioned : " + position);
					convertView = mInflater.inflate(
							R.layout.header_recieptlist, parent, false);
					((TextView) convertView.findViewById(R.id.headerrec_head))
							.setText("Processed");
					((TextView) convertView.findViewById(R.id.headerrec_head))
							.setTypeface(face3);
				} else {
					convertView = mInflater.inflate(R.layout.blank, parent,
							false);
				}

			} else {
				Log.e("", "positioned : " + position);
				// listView.setDividerHeight(2);
				if (processedreciepts.size() > 0) {
					if (position == processedreciepts.size() + 2) {
						convertView = mInflater.inflate(
								R.layout.custom_recieptlist, parent, false);

						txt_date = (TextView) convertView
								.findViewById(R.id.txtcustomreciept_date);
						txt_name = (TextView) convertView
								.findViewById(R.id.txtcustomreciept_name);
						txt_total = (TextView) convertView
								.findViewById(R.id.txtcustomreciept_total);
						txt_totalsub = (TextView) convertView
								.findViewById(R.id.txtcustomreciept_totalsub);

						txt_date.setTypeface(face3);
						txt_name.setTypeface(face3);
						txt_total.setTypeface(face3);
						txt_totalsub.setTypeface(face3);

						String date = processedreciepts.get(
								position - (pendingreciepts.size() + 2))
								.getDate();

						txt_date.setText(date);
						txt_name.setText(processedreciepts.get(
								position - (pendingreciepts.size() + 2))
								.getName());

						String total, total_1, total_2;
						total = processedreciepts.get(
								position - (pendingreciepts.size() + 2))
								.getTotal();
						if (total.equalsIgnoreCase("n/a")
								|| total.equalsIgnoreCase("null")
								|| total.equalsIgnoreCase("")) {
							txt_total.setText(total);
							txt_total.setTextColor(Color.parseColor("#949494"));
							txt_totalsub.setText("");
						} else {
							StringBuilder sb = new StringBuilder(total);
							total_1 = sb.substring(0, total.indexOf("."));
							total_2 = sb.substring(total.indexOf("."),
									sb.length());
							System.out.println("::::::: " + total_1);
							System.out.println("::::::: " + total_2);
							txt_total.setText(total_1);
							txt_totalsub.setText(total_2);
						}
					} else {
						convertView = mInflater.inflate(
								R.layout.custom_recieptlistmargined, parent,
								false);

						txt_date = (TextView) convertView
								.findViewById(R.id.txtcustomreciept_date);
						txt_name = (TextView) convertView
								.findViewById(R.id.txtcustomreciept_name);
						txt_total = (TextView) convertView
								.findViewById(R.id.txtcustomreciept_total);
						txt_totalsub = (TextView) convertView
								.findViewById(R.id.txtcustomreciept_totalsub);

						txt_date.setTypeface(face3);
						txt_name.setTypeface(face3);
						txt_total.setTypeface(face3);
						txt_totalsub.setTypeface(face3);

						String date = processedreciepts.get(
								position - (pendingreciepts.size() + 2))
								.getDate();

						txt_date.setText(date);
						txt_name.setText(processedreciepts.get(
								position - (pendingreciepts.size() + 2))
								.getName());

						String total, total_1, total_2;
						total = processedreciepts.get(
								position - (pendingreciepts.size() + 2))
								.getTotal();
						if (total.equalsIgnoreCase("n/a")
								|| total.equalsIgnoreCase("null")
								|| total.equalsIgnoreCase("")) {
							txt_total.setText(total);
							txt_total.setTextColor(Color.parseColor("#949494"));
							txt_totalsub.setText("");
						} else {
							StringBuilder sb = new StringBuilder(total);
							total_1 = sb.substring(0, total.indexOf("."));
							total_2 = sb.substring(total.indexOf("."),
									sb.length());
							System.out.println("::::::: " + total_1);
							System.out.println("::::::: " + total_2);
							txt_total.setText(total_1);
							txt_totalsub.setText(total_2);
						}
					}
				}

			}

			return convertView;
		}

	}

	class LoadAllData extends AsyncTask<String, String, String> {

		protected String doInBackground(String... args) {
			JSONParser parser = new JSONParser();
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			String result = parser.makeHttpRequest(URL_Reciepts, "GET", params);

			if (result != null) {
				try {

					JSONArray array = new JSONArray(result);
					for (int i = 0; i < array.length(); i++) {
						System.out.println(i + " : " + array.getString(i));
					}

					for (int i = 0; i < array.length(); i++) {

						// JSONArray array2 = array.getJSONArray(i);
						JSONObject c = array.getJSONObject(i);

						String id = c.getString("id");
						String company = c.getString("restaurant_name");
						String phone = c.getString("phone_number");
						String address = c.getString("address");
						String name = c.getString("first_name");
						String rest_id = c.getString("restaurant_id");
						// String payment = c.getString("payment");
						String subtotal = c.getString("sub_total");
						String user_card_id = c.getString("user_card_id");
						if (subtotal.contains(".")) {

						} else {
							subtotal = subtotal.concat(".00");

						}
						Log.e("", "sub_total : " + subtotal);
						String tax = c.getString("tax");
						if (tax.contains(".")) {

						} else {
							tax = tax.concat(".00");

						}
						String kent_fee = c.getString("kent_fee");
						if (kent_fee.contains(".")) {

						} else {
							kent_fee = kent_fee.concat(".00");

						}
						String tip = c.getString("tip");
						if (tip.contains(".")) {

						} else {
							tip = tip.concat(".00");

						}
						String total = c.getString("invoice_amount");
						if (total.contains(".")) {

						} else {
							total = total.concat(".00");

						}
						String date = c.getString("receipt_created");
						String status = c.getString("status");
						String payment = "Visa *6633";

						Receipt receipt = new Receipt(id, company, phone,
								address, name, payment, subtotal, tax,
								kent_fee, tip, total, date, status, rest_id,
								user_card_id);
						receipts.add(receipt);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				Log.e("ServiceHandler", "Couldn't get any data from the url");
			}

			for (int i = 0; i < receipts.size(); i++) {
				Log.e("", " rec name : " + receipts.get(i).getName());
			}

			return null;
		}

		protected void onPostExecute(String file_url) {
			proDialog.dismiss();
			for (int i = 0; i < receipts.size(); i++) {
				if (receipts.get(i).getStatus().equalsIgnoreCase("pending")) {
					pendingreciepts.add(receipts.get(i));
				} else {
					processedreciepts.add(receipts.get(i));
				}
			}
			listView.setAdapter(new RecieptAdapter(getActivity(),
					pendingreciepts, processedreciepts));
			KentValues.isLoading = false;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			Log.d("", "CANCELED");
			proDialog.dismiss();
		}
	}
}
