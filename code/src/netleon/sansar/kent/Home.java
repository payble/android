package netleon.sansar.kent;

import netleon.sansar.kent.support.AlertDialogManager;
import netleon.sansar.kent.support.ConnectionDetector;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Home extends Activity {

	SharedPreferences preferences;
	ConnectionDetector cd;
	AlertDialogManager alert;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.home);
		preferences = getSharedPreferences("NESSIE", MODE_PRIVATE);
		check_status();

		Typeface face1 = Typeface.createFromAsset(getAssets(),
				"fonts/Avenir.ttc");
		Typeface face2 = Typeface.createFromAsset(getAssets(),
				"fonts/LetterGothicStd.otf");

		((TextView) findViewById(R.id.text_kent)).setTypeface(face2);
		((Button) findViewById(R.id.btn_signin)).setTypeface(face1);
		((Button) findViewById(R.id.btn_registr)).setTypeface(face1);

	}

	public void SignIn(View v) {

		Intent intent = new Intent(Home.this, LogIn.class);
		startActivity(intent);
		finish();

	}

	public void Register(View v) {
		Intent intent = new Intent(Home.this, Register.class);
		startActivity(intent);
		finish();
	}

	private void check_status() {

		Context context = Home.this;
		alert = new AlertDialogManager();
		cd = new ConnectionDetector(context);
		if (!cd.isConnectingToInternet()) {
			alert.showAlertDialog(context, "Internet Connection Error",
					"Please connect to working Internet connection", false);

			return;

		}

		if (preferences.contains("isLogged")) {

			boolean temp = preferences.getBoolean("isLogged", false);

			if (temp) {
				startActivity(new Intent(context, Perspective.class));
				finish();
			}
		}
	}

}
