package netleon.sansar.kent;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import netleon.sansar.kent.facebook.FB_Session;
import netleon.sansar.kent.facebook.Helicopter;
import netleon.sansar.kent.support.DatabaseHandler;
import netleon.sansar.kent.support.UserFunctions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class LogIn extends Activity {

	TextView btnLogin;

	private static String APP_ID = "779169015483457";
	private Facebook facebook = new Facebook(APP_ID);
	private AsyncFacebookRunner mAsyncRunner;

	Helicopter helicopter;
	private SharedPreferences mPrefs;
	EditText inputEmail;
	EditText inputPassword;
	ProgressDialog dialog;

	UserFunctions userFunction = new UserFunctions();

	// private static String KEY_SUCCESS = "true";
	private static String KEY_UID = "uid";
	private static String KEY_NAME = "name";
	private static String KEY_EMAIL = "email";
	private static String KEY_CREATED_AT = "created_at";

	String email;
	String password;
	String res;
	String str1, str2, str3, str4, str_email, str_password, first_name,
			last_name;

	TextView cancel;
	LinearLayout lin_connect_fb;
	FB_Session fb_Session;

	String user_id, user_total_receipt, user_name, user_email, user_phone;

	boolean str_ckeck = false;
	String endpoint = "http://netleondev.com/kentapi/user/autenticate";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signin);

		helicopter = new Helicopter();
		mAsyncRunner = helicopter.getFacebookRunner();

		fb_Session = new FB_Session(LogIn.this, "log");

		Typeface face1 = Typeface.createFromAsset(getAssets(),
				"fonts/Avenir.ttc");
		Typeface face2 = Typeface.createFromAsset(getAssets(),
				"fonts/LetterGothicStd.otf");

		inputEmail = (EditText) findViewById(R.id.editsignin_email);
		inputPassword = (EditText) findViewById(R.id.editsignin_password);
		cancel = (TextView) findViewById(R.id.txt_cancel);
		cancel.setTypeface(face1);
		lin_connect_fb = (LinearLayout) findViewById(R.id.connect_fb);
		btnLogin = (TextView) findViewById(R.id.forward_arrow);

		btnLogin.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {

				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(
						((LinearLayout) findViewById(R.id.lin_log))
								.getWindowToken(), 0);
				if (isVailed()) {
					Log.e("", "in side is vaild method");
					// new callServiceTask().execute();
					// getProfileInformation();
					new SendData().execute();
				}

				// startActivity(new Intent(LogIn.this, Perspective.class));
			}
		});
		btnLogin.setTypeface(face1);

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
				// loginToFacebook();
				Session session = fb_Session.startSession();
				fb_Session.setCurrentSession(session);

			}
		});

		((TextView) findViewById(R.id.head_1)).setTypeface(face2);
		((TextView) findViewById(R.id.txt_conect_fb)).setTypeface(face1);
		((TextView) findViewById(R.id.txt_email)).setTypeface(face1);
		((TextView) findViewById(R.id.txt_password)).setTypeface(face1);
		((TextView) findViewById(R.id.forgot)).setTypeface(face1);
		((TextView) findViewById(R.id.forgot))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						startActivity(new Intent(LogIn.this,
								ForgotPassword.class));
					}
				});
		inputEmail.setTypeface(face1);
		inputPassword.setTypeface(face1);
	}

	public class callServiceTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {

			email = inputEmail.getText().toString();
			password = inputPassword.getText().toString();

			dialog = new ProgressDialog(LogIn.this);
			dialog.setCancelable(false);
			dialog.setMessage("Please wait ... ");
			dialog.setIndeterminate(false);
			dialog.show();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {

			JSONObject json = userFunction.loginUser(email, password);

			try {
				// if (json.getString(KEY_SUCCESS) != null) {
				// res = json.getString(KEY_SUCCESS);

				if (Integer.parseInt(res) == 1) {
					// if (res.equalsIgnoreCase("s")) {
					JSONObject json_user = json.getJSONObject("user");

					str1 = json_user.getString(KEY_NAME);
					str2 = json_user.getString(KEY_EMAIL);
					str3 = json.getString(KEY_UID);
					str4 = json_user.getString(KEY_CREATED_AT);
				} else {

					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							Toast.makeText(getApplicationContext(),
									"Incorrect Email or Password.",
									Toast.LENGTH_LONG).show();
						}
					});
				}

				// }
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			dialog.cancel();

			if (res.equalsIgnoreCase("true")) {

				userFunction.logoutUser(getApplicationContext());
				DatabaseHandler db = new DatabaseHandler(
						getApplicationContext());
				// db.addUser(str1, str2, str3, str4);
				getSharedPreferences("NESSIE", MODE_PRIVATE).edit()
						.putBoolean("isLogged", true).commit();

				Intent dashboard = new Intent(getApplicationContext(),
						Perspective.class);

				dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(dashboard);
				helicopter.setFacebookRunner(mAsyncRunner);
				finish();
			}

			super.onPostExecute(result);
		}

	}

	@SuppressWarnings("deprecation")
	public void loginToFacebook() {

		mPrefs = getPreferences(MODE_PRIVATE);
		String access_token = mPrefs.getString("access_token", null);
		long expires = mPrefs.getLong("access_expires", 0);

		if (access_token != null) {
			facebook.setAccessToken(access_token);
			Log.d("FB Sessions", "" + facebook.isSessionValid());
		}

		if (expires != 0) {
			facebook.setAccessExpires(expires);
		}

		if (!facebook.isSessionValid()) {
			facebook.authorize(this,
					new String[] { "email", "publish_stream" },
					new DialogListener() {

						@Override
						public void onCancel() {
						}

						@Override
						public void onComplete(Bundle values) {
							SharedPreferences.Editor editor = mPrefs.edit();
							editor.putString("access_token",
									facebook.getAccessToken());
							editor.putLong("access_expires",
									facebook.getAccessExpires());
							editor.commit();

							getSharedPreferences("NESSIE", MODE_PRIVATE).edit()
									.putBoolean("isLogged", true).commit();

							getProfileInformation();

							Intent i = new Intent(LogIn.this, Perspective.class);
							i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							LogIn.this.startActivity(i);
							helicopter.setFacebookRunner(mAsyncRunner);
						}

						@Override
						public void onError(DialogError error) {

						}

						@Override
						public void onFacebookError(FacebookError fberror) {

						}

					});
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (fb_Session.getCurrentSession() != null) {
			fb_Session.getCurrentSession().onActivityResult(this, requestCode,
					resultCode, data);
		}
	}

	@SuppressWarnings("deprecation")
	public void getProfileInformation() {
		mAsyncRunner.request("me", new RequestListener() {
			@Override
			public void onComplete(String response, Object state) {
				Log.d("Profile", response);
				String json = response;
				System.out.println("JSON --------- " + json);
				try {
					JSONObject profile = new JSONObject(json);

					final String name = profile.getString("name");

					final String email = profile.getString("email");
					final String link = profile.getString("link");

					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(
									getApplicationContext(),
									"Name: " + name + "\nEmail: " + email
											+ "\nLink: " + link,
									Toast.LENGTH_LONG).show();
						}

					});

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onIOException(IOException e, Object state) {
			}

			@Override
			public void onFileNotFoundException(FileNotFoundException e,
					Object state) {
			}

			@Override
			public void onMalformedURLException(MalformedURLException e,
					Object state) {
			}

			@Override
			public void onFacebookError(FacebookError e, Object state) {
			}
		});
	}

	@SuppressWarnings("deprecation")
	public void logoutFromFacebook() {
		mAsyncRunner.logout(this, new RequestListener() {
			@Override
			public void onComplete(String response, Object state) {

				Log.d("Logout from Facebook", response);

				if (Boolean.parseBoolean(response) == true) {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {

							Log.d("hola mission  done", "");

						}

					});

				}
			}

			@Override
			public void onIOException(IOException e, Object state) {
			}

			@Override
			public void onFileNotFoundException(FileNotFoundException e,
					Object state) {
			}

			@Override
			public void onMalformedURLException(MalformedURLException e,
					Object state) {
			}

			@Override
			public void onFacebookError(FacebookError e, Object state) {
			}
		});
	}

	boolean isVailed() {
		if (inputEmail.getText().toString().length() < 2) {

			inputEmail.setError("Enter vaild email");
			return false;
		} else if (inputPassword.getText().toString().length() < 5) {
			inputPassword.setError("Enter mimimun 5 character");
			return false;
		} else {
			return true;
		}

	}

	private JSONObject margya(String email, String password) {
		JSONObject obj = new JSONObject();
		try {
			obj.put("password", str_password);
			obj.put("email", str_email);

			// obj.put("nickname", null);
			System.out.print(obj);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj;
	}

	private class SendData extends AsyncTask<String, Integer, Void> {
		String str_error1 = null;

		@Override
		protected void onPreExecute() {

			str_email = inputEmail.getText().toString();
			str_password = inputPassword.getText().toString();

			dialog = new ProgressDialog(LogIn.this);
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
			String postURL = (endpoint);
			HttpPost post = new HttpPost(postURL);
			post.addHeader("Content-type", "application/json");
			post.addHeader("Authorization", "aDRF@F#JG_a34-n3d");
			try {
				// Add the data
				List<NameValuePair> pairs = new ArrayList<NameValuePair>(3);
				pairs.add(new BasicNameValuePair("password", str_password));
				pairs.add(new BasicNameValuePair("email", str_email));
				JSONObject json = margya(str_email, str_password);

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
					try {
						JSONArray array = new JSONArray(sts);
						for (int i = 0; i < array.length(); i++) {
							JSONObject jsonObject = array.getJSONObject(i);

							user_id = jsonObject.getString("id");
							user_total_receipt = jsonObject
									.getString("total_receipts");
							first_name = jsonObject.getString("first_name");
							last_name = jsonObject.getString("last_name");
							user_email = jsonObject.getString("email");
							user_phone = jsonObject.getString("phone_number");

							user_name = first_name.concat(" " + last_name);

							if (jsonObject.has("status")) {
								str_error1 = jsonObject.getString("status");
								Log.e("", "this is user id :" + user_id);
								Log.e("", "this is eroor :" + str_error1);
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

								Log.e("",
										"this is photo url"
												+ jsonObject.getString("photo"));

							}

						}

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						if (str_error1.equalsIgnoreCase("1")) {
							str_ckeck = true;
						} else {
							str_ckeck = false;
						}
					} catch (Exception e) {
						// TODO: handle exception
					}

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
			dialog.cancel();
			if (str_ckeck) {

				getSharedPreferences("NESSIE", MODE_PRIVATE).edit()
						.putBoolean("isLogged", true).commit();

				getSharedPreferences("NESSIE", MODE_PRIVATE).edit()
						.putString("ID", user_id).commit();

				getSharedPreferences("NESSIE", MODE_PRIVATE).edit()
						.putString("TOTAL_RECEIPTS", user_total_receipt)
						.commit();

				getSharedPreferences("NESSIE", MODE_PRIVATE).edit()
						.putString("USER_FULL_NAME", user_name).commit();

				getSharedPreferences("NESSIE", MODE_PRIVATE).edit()
						.putString("USER_EMAIL", user_email).commit();

				getSharedPreferences("NESSIE", MODE_PRIVATE).edit()
						.putString("USER_PHONE", user_phone).commit();

				Intent i = new Intent(LogIn.this, Perspective.class);

				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
				finish();

			} else {
				Log.e("", "Wrong...!!!!!!");

				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						Toast.makeText(LogIn.this,
								"Invaild Email or Password.", Toast.LENGTH_LONG)
								.show();
					}
				}, 1000);
			}
			super.onPostExecute(result);
		}
	}

	@Override
	public void onBackPressed() {
		Intent myIntent = new Intent(getApplicationContext(), Home.class);
		startActivity(myIntent);
		super.onBackPressed();
	}

}