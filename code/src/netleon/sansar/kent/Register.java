package netleon.sansar.kent;

import netleon.sansar.kent.facebook.FB_Session;
import netleon.sansar.kent.facebook.Helicopter;
import netleon.sansar.kent.network.GetData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.android.AsyncFacebookRunner;

@SuppressWarnings("deprecation")
public class Register extends Activity {

	TextView btnRegister;

	@SuppressWarnings("unused")
	private AsyncFacebookRunner mAsyncRunner;
	FB_Session fb_Session;

	EditText inputFullName;
	EditText inputEmail;
	EditText inputPassword;
	EditText inputMobile;

	ProgressDialog dialog;

	TextView cancel;
	LinearLayout lin_connect_fb;
	String Name, Email, Phone, Pass;

	String Url_register = "http://netleondev.com/kentapi/user/register";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signup);

		Typeface face1 = Typeface.createFromAsset(getAssets(),
				"fonts/Avenir.ttc");
		Typeface face2 = Typeface.createFromAsset(getAssets(),
				"fonts/LetterGothicStd.otf");

		mAsyncRunner = (new Helicopter()).getFacebookRunner();

		fb_Session = new FB_Session(Register.this, "Reg");

		inputFullName = (EditText) findViewById(R.id.editsignup_name);
		inputEmail = (EditText) findViewById(R.id.editsignup_email);
		inputPassword = (EditText) findViewById(R.id.editsignup_pass);
		inputMobile = (EditText) findViewById(R.id.editsignup_mobile);

		cancel = (TextView) findViewById(R.id.txt_cancel_register);
		lin_connect_fb = (LinearLayout) findViewById(R.id.layout_fb);

		cancel.setTypeface(face1);

		((TextView) findViewById(R.id.head_1)).setTypeface(face2);
		((TextView) findViewById(R.id.txt_connect_fb)).setTypeface(face1);
		((TextView) findViewById(R.id.txt_name)).setTypeface(face1);
		((TextView) findViewById(R.id.txt_email)).setTypeface(face1);
		((TextView) findViewById(R.id.txt_mobile)).setTypeface(face1);
		((TextView) findViewById(R.id.txt_password)).setTypeface(face1);
		((TextView) findViewById(R.id.txt_rgistr)).setTypeface(face1);
		inputEmail.setTypeface(face1);
		inputFullName.setTypeface(face1);
		inputMobile.setTypeface(face1);
		inputPassword.setTypeface(face1);

		btnRegister = (TextView) findViewById(R.id.forward_arrow);

		btnRegister.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {

				Name = inputFullName.getText().toString();
				Email = inputEmail.getText().toString();
				Phone = inputMobile.getText().toString();
				Pass = inputPassword.getText().toString();

				if (isVailed()) {

					JSONObject json = create_JSON(Name, Phone, Email, Pass);
					new Runner(Url_register, json.toString()).execute();
				}

			}
		});
		btnRegister.setTypeface(face1);
		cancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Intent myIntent = new Intent(getApplicationContext(),
						Home.class);
				startActivity(myIntent);
			}
		});

		lin_connect_fb.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				Log.d("Image Button", "button Clicked");

				Session session = fb_Session.startSession();
				fb_Session.setCurrentSession(session);

			}
		});

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// facebook.authorizeCallback(requestCode, resultCode, data);
		if (fb_Session.getCurrentSession() != null) {
			fb_Session.getCurrentSession().onActivityResult(this, requestCode,
					resultCode, data);
		}
	}

	boolean isVailed() {

		Name = inputFullName.getText().toString().trim();
		Phone = inputMobile.getText().toString().trim();
		Email = inputEmail.getText().toString().trim();
		Pass = inputPassword.getText().toString().trim();

		if (Name.equalsIgnoreCase("")) {
			inputFullName.setError("Enter name");
			return false;
		} else if (Name.length() < 2) {
			inputFullName.setError("Enter vaild name");
			return false;
		}

		else if (Email.equalsIgnoreCase("")) {
			inputEmail.setError("Enter email");
			return false;
		} else if (Email.length() < 2) {
			inputEmail.setError("Enter vaild email");
			return false;
		} else if (!isValidEmail(Email)) {
			inputEmail.setError("Enter vaild email");
			return false;
		}

		else if (Phone.equalsIgnoreCase("")) {
			inputEmail.setError("Enter mobile no.");
			return false;
		} else if (Phone.length() < 7) {
			inputMobile.setError("Enter vaild mobile");
			return false;
		}

		else if (Pass.equalsIgnoreCase("")) {
			inputEmail.setError("Enter password");
			return false;
		} else if (Pass.length() < 5) {
			inputPassword.setError("Enter mimimun 5 character");
			return false;
		}

		else {
			return true;
		}

	}

	public final boolean isValidEmail(CharSequence target) {
		if (TextUtils.isEmpty(target)) {
			return false;
		} else {
			return android.util.Patterns.EMAIL_ADDRESS.matcher(target)
					.matches();
		}
	}

	public class Runner extends AsyncTask<String, String, String> {

		String jdata;
		String url;
		String response;

		public Runner(String url, String data) {
			this.jdata = data;
			this.url = url;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(Register.this);
			dialog.setCancelable(false);
			dialog.setMessage("Please wait ... ");
			dialog.setIndeterminate(false);
			dialog.show();
		}

		@Override
		protected String doInBackground(String... params) {

			GetData data = new GetData();
			response = data.putDataToServer(url, jdata);
			return null;

		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			if (response != null) {

				String user_id, user_total_receipt, first_name, last_name, user_email, user_phone, user_name;
				String status;

				try {

					JSONArray array = new JSONArray(response);
					for (int i = 0; i < array.length(); i++) {

						JSONObject jsonObject = array.getJSONObject(i);

						if (jsonObject.has("status")) {
							status = jsonObject.getString("status");

							if (status.equalsIgnoreCase("0")) {

								runOnUiThread(new Runnable() {
									public void run() {
										Toast.makeText(Register.this,
												"Email already exists",
												Toast.LENGTH_SHORT).show();
									}
								});

							} else {

								user_id = jsonObject.getString("id");
								user_total_receipt = jsonObject
										.getString("total_receipts");
								first_name = jsonObject.getString("first_name");
								last_name = jsonObject.getString("last_name");
								user_email = jsonObject.getString("email");
								user_phone = jsonObject
										.getString("phone_number");
								user_name = first_name.concat(" " + last_name);

								Log.e("", "this is user id :" + user_id);
								Log.e("", "this is total receipts :"
										+ user_total_receipt);
								Log.e("", "this is user first name :"
										+ first_name);
								Log.e("", "this is user last name :"
										+ last_name);
								Log.e("", "this is user email :" + user_email);
								Log.e("", "this is user phone :" + user_phone);
								Log.e("", "this is user full name :"
										+ user_name);

								SharedPreferences preferences = getSharedPreferences(
										"NESSIE", MODE_PRIVATE);
								Editor editor = preferences.edit();

								editor.putBoolean("isLogged", true);
								editor.putString("ID", user_id);
								editor.putString("TOTAL_RECEIPTS",
										user_total_receipt);
								editor.putString("USER_FULL_NAME", user_name);
								editor.putString("USER_EMAIL", user_email);
								editor.putString("USER_PHONE", user_phone);
								editor.commit();

								Intent intent = new Intent(Register.this,
										Perspective.class);
								intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(intent);
								finish();
							}
						}

					}

				} catch (JSONException e) {
					e.printStackTrace();
				}

				dialog.cancel();
			}
		}
	}

	private JSONObject create_JSON(String name, String phone, String email,
			String password) {
		JSONObject obj = new JSONObject();
		try {

			obj.put("password", Pass);
			obj.put("email", Email);
			obj.put("phone_number", Phone);
			obj.put("first_name", Name);

			System.out.print(obj);
		} catch (JSONException e) {

			e.printStackTrace();
		}

		return obj;
	}

	@Override
	public void onBackPressed() {
		Intent myIntent = new Intent(getApplicationContext(), Home.class);
		startActivity(myIntent);
		super.onBackPressed();
	}
}
