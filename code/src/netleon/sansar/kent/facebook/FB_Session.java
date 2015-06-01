package netleon.sansar.kent.facebook;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import netleon.sansar.kent.Perspective;
import netleon.sansar.kent.network.GetData;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
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
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;

public class FB_Session {

	Context mContext;
	public Session.StatusCallback sessionStatusCallback;
	public static Session currentSession;
	String fbName, fbEmail;
	String url_fblogin = "http://netleondev.com/kentapi/user/facebooklogin";
	String url_fbreg = "http://netleondev.com/kentapi/user/register";
	String str_error1;
	boolean str_ckeck;
	ProgressDialog dialog;
	String user_id;
	String user_total_receipt;
	String first_name;
	String TAG;
	String last_name;
	String user_email;
	String user_phone, user_name;
	public static String fbUserId;

	public FB_Session(Context context, String TAG) {

		mContext = context;
		this.TAG = TAG;
		sessionStatusCallback = new Session.StatusCallback() {

			@Override
			public void call(Session session, SessionState state,
					Exception exception) {
				onSessionStateChange(session, state, exception);

			}
		};

	}

	public Session startSession() {
		List<String> permissions = new ArrayList<String>();
		permissions.add("publish_stream");
		currentSession = new Session.Builder(mContext).build();
		currentSession.addCallback(sessionStatusCallback);
		Session.OpenRequest openRequest = new Session.OpenRequest(
				(Activity) mContext);
		openRequest.setLoginBehavior(SessionLoginBehavior.SUPPRESS_SSO);
		openRequest.setRequestCode(Session.DEFAULT_AUTHORIZE_ACTIVITY_CODE);
		openRequest.setPermissions(permissions);
		currentSession.openForPublish(openRequest);

		return currentSession;
	}

	public void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		if (session != currentSession) {
			return;
		}

		if (state.isOpened()) {

			Request.newMeRequest(session, new Request.GraphUserCallback() {

				@Override
				public void onCompleted(GraphUser user, Response response) {
					// TODO Auto-generated method stub

					try {
						fbName = user.getName().toString();
						fbEmail = user.asMap().get("email").toString();
						fbUserId = user.getId().toString();
						ImageView profile = new ImageView(mContext);

					} catch (NullPointerException e) {
						// TODO: handle exception
					}

					System.out.println("facebook user id" + user.getId());
					System.out.println(user.asMap().get("email").toString());

					Log.d("", "fb name : - " + fbName);
					Log.d("", "fb email : - " + fbEmail);
					Log.d("", "fb user id : - " + fbUserId);

					if (TAG.equalsIgnoreCase("reg")) {

						Log.d("", "Register...!!!!");
						JSONObject jsonObject = create_JSON_Register(fbName,
								"1234567890", fbEmail, "testing", fbUserId);

						new FB_Register(url_fbreg, jsonObject.toString())
								.execute();

					} else {
						Log.d("", "Login...!!!!");

						new FB_Login().execute();

					}

				}

			}).executeAsync();

		} else if (state.isClosed()) {

		}
	}

	public void setSessionStatusCallback(
			Session.StatusCallback sessionStatusCallback) {
		this.sessionStatusCallback = sessionStatusCallback;
	}

	public void setCurrentSession(Session currentSession) {
		this.currentSession = currentSession;
	}

	public Session.StatusCallback getSessionStatusCallback() {
		return sessionStatusCallback;
	}

	public void setmContext(Context mContext) {
		this.mContext = mContext;
	}

	public Context getmContext() {
		return mContext;
	}

	public Session getCurrentSession() {
		return currentSession;
	}

	public class FB_Login extends AsyncTask<String, String, Void> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			dialog = new ProgressDialog(mContext);
			dialog.setCancelable(false);
			dialog.setMessage("Please wait ... ");
			dialog.setIndeterminate(false);
			dialog.show();
			super.onPreExecute();

		}

		@Override
		protected Void doInBackground(String... params) {
			HttpClient client = new DefaultHttpClient();
			String postURL = (url_fblogin);
			HttpPost post = new HttpPost(postURL);
			post.addHeader("Content-type", "application/json");
			post.addHeader("Authorization", "aDRF@F#JG_a34-n3d");
			try {
				List<NameValuePair> pairs = new ArrayList<NameValuePair>(3);
				pairs.add(new BasicNameValuePair("facebook_id", fbUserId));
				pairs.add(new BasicNameValuePair("email", fbEmail));

				JSONObject json = createJsonLogin(fbUserId, fbEmail);

				try {
					Log.e("", "posting data on fb : " + json.toString());
					post.setEntity(new StringEntity(json.toString()));
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
				HttpResponse response = client.execute(post);
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

							}

						}

					} catch (JSONException e) {
						e.printStackTrace();
					}
					try {
						if (str_error1.equalsIgnoreCase("1")) {
							str_ckeck = true;
						} else {
							str_ckeck = false;
						}
					} catch (Exception e) {
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

			if (str_ckeck) {

				mContext.getSharedPreferences("NESSIE", mContext.MODE_PRIVATE)
						.edit().putBoolean("isLogged", true).commit();

				mContext.getSharedPreferences("NESSIE", mContext.MODE_PRIVATE)
						.edit().putString("ID", user_id).commit();

				mContext.getSharedPreferences("NESSIE", mContext.MODE_PRIVATE)
						.edit().putString("TOTAL_RECEIPTS", user_total_receipt)
						.commit();

				mContext.getSharedPreferences("NESSIE", mContext.MODE_PRIVATE)
						.edit().putString("USER_FULL_NAME", user_name).commit();

				mContext.getSharedPreferences("NESSIE", mContext.MODE_PRIVATE)
						.edit().putString("USER_EMAIL", user_email).commit();

				mContext.getSharedPreferences("NESSIE", mContext.MODE_PRIVATE)
						.edit().putString("USER_PHONE", user_phone).commit();

				Intent i = new Intent(mContext, Perspective.class);

				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				mContext.startActivity(i);
				((Activity) mContext).finish();

			} else {
				Log.e("", "Wrong...!!!!!!");

				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						Toast.makeText(mContext, "Invaild Email or Password.",
								Toast.LENGTH_LONG).show();
					}
				}, 1000);
			}
			dialog.cancel();
			super.onPostExecute(result);
		}

	}

	private JSONObject createJsonLogin(String email, String password) {
		JSONObject obj = new JSONObject();
		try {
			obj.put("facebook_id", fbUserId);
			obj.put("email", fbEmail);

			System.out.print(obj);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj;
	}

	public class FB_Register extends AsyncTask<String, String, String> {

		String response;
		String jdata;
		String url;

		public FB_Register(String url, String data) {
			this.jdata = data;
			this.url = url;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(mContext);
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

				Log.e("", "1");

				String user_id, user_total_receipt, first_name, last_name, user_email, user_phone, user_name;
				String status;

				try {

					JSONArray array = new JSONArray(response);
					Log.e("", "2");
					for (int i = 0; i < array.length(); i++) {

						JSONObject jsonObject = array.getJSONObject(i);

						if (jsonObject.has("status")) {
							status = jsonObject.getString("status");

							if (status.equalsIgnoreCase("0")) {

								((Activity) mContext)
										.runOnUiThread(new Runnable() {
											public void run() {
												Toast.makeText(mContext,
														"Email already exists",
														Toast.LENGTH_SHORT)
														.show();
											}
										});

							} else {

								Log.e("", "2");
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
								Log.e("", "3");

								SharedPreferences preferences = mContext
										.getSharedPreferences("NESSIE",
												mContext.MODE_PRIVATE);
								Editor editor = preferences.edit();

								editor.putBoolean("isLogged", true);
								editor.putString("ID", user_id);
								editor.putString("TOTAL_RECEIPTS",
										user_total_receipt);
								editor.putString("USER_FULL_NAME", user_name);
								editor.putString("USER_EMAIL", user_email);
								editor.putString("USER_PHONE", user_phone);
								editor.commit();

								Log.e("", "4");

								Intent intent = new Intent(mContext,
										Perspective.class);
								intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								mContext.startActivity(intent);
								((Activity) mContext).finish();
								Log.e("", "5");
							}
						}

					}

				} catch (JSONException e) {
					e.printStackTrace();
				}

				dialog.cancel();
			} else {
				((Activity) mContext).runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						Toast.makeText(mContext, "Internet Error...!!!",
								Toast.LENGTH_SHORT).show();
					}
				});
			}
		}
	}

	private JSONObject create_JSON_Register(String name, String phone,
			String email, String password, String fbId) {
		JSONObject obj = new JSONObject();
		try {

			obj.put("password", "testing");
			obj.put("email", fbEmail);
			obj.put("phone_number", "1234567890");
			obj.put("first_name", fbName);
			obj.put("facebook_id", fbUserId);

			System.out.print(obj);
		} catch (JSONException e) {

			e.printStackTrace();
		}

		return obj;
	}

}
