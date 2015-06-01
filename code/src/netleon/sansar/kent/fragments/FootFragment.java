package netleon.sansar.kent.fragments;

import static netleon.sansar.kent.base.KentValues.isLoading;
import static netleon.sansar.kent.base.KentValues.screenDensity;
import static netleon.sansar.kent.base.KentValues.width;

import java.util.List;

import netleon.sansar.kent.Perspective;
import netleon.sansar.kent.R;
import netleon.sansar.kent.base.KentValues;
import netleon.sansar.kent.base.Restaurant;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FootFragment extends Fragment {

	ImageView img_circle;
	HorizontalScrollView scrollView;
	boolean is_NotFirst = false;
	boolean is_CircleVisible = true;
	LinearLayout lin_root;
	String[] footerParameters;
	public int[] stations, parts;
	boolean is_scrolled = false;

	public final int finalWidthChild = 130;
	public TextView[] textViews;
	int current = 0;
	FragmentManager fm;
	Typeface tf_subhead;
	private GestureDetector gestureDetector;
	View.OnTouchListener gestureListener;
	FootListFragment footListFragment;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		fm = getActivity().getSupportFragmentManager();
		footListFragment = ((Perspective) getActivity()).footListFragment;
		initializeGestures();
		return inflater.inflate(R.layout.footfragment, container, false);
	}

	public void clearFooterData() {
		textViews = null;
		stations = null;
		parts = null;
		textViews = new TextView[footerParameters.length];
		stations = new int[this.footerParameters.length];
		parts = new int[this.footerParameters.length];
	}

	private void initializeGestures() {
		gestureDetector = new GestureDetector(getActivity(),
				new MyGestureListener());
		gestureListener = new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		};
	}

	public void setFooterParameters(String[] footerParameters) {
		this.footerParameters = footerParameters;
	}

	public void createNewLayout() {
		clearFooterData();
		lin_root.removeAllViews();
		generateLayout(getActivity(), footerParameters, width);
		lin_root.invalidate();
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				try {
					calStations();
				} catch (Exception e) {
				}
			}
		}, 200);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		clearFooterData();
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		if (!hidden) {
			if (fm.findFragmentByTag("ReserveTableFragment") != null) {
				fm.beginTransaction()
						.hide(((Perspective) getActivity()).footListFragment)
						.commit();
			}
		}
		super.onHiddenChanged(hidden);
	}

	public boolean isAllStationsAvailable() {
		boolean isit = true;
		for (int i = 0; i < stations.length; i++) {
			int st = stations[i];
			if (st == 0) {
				isit = false;
				break;
			}
		}
		return isit;
	}

	@Override
	public void onViewCreated(final View view,
			@Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		tf_subhead = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/Avenir.ttc");
		lin_root = (LinearLayout) view.findViewById(R.id.linfootfrag_captain);
		lin_root.setOnTouchListener(gestureListener);
		createNewLayout();
	}

	private void trans(View view, float from, float to, final TextView textView) {

		Log.e("", "trans from : " + from);
		Log.e("", "trans to : " + to);
		AnimationSet animSet = new AnimationSet(true);
		int duration = getResources().getInteger(R.integer.transtime);
		if (!is_NotFirst) {
			TranslateAnimation anim = new TranslateAnimation(from, to, 0, 0);
			anim.setDuration(duration);
			AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
			alphaAnimation.setDuration(duration);
			animSet.addAnimation(anim);
			animSet.addAnimation(alphaAnimation);
		} else {
			TranslateAnimation anim = new TranslateAnimation(from, to, 0, 0);
			anim.setDuration(duration);
			animSet.addAnimation(anim);
		}

		animSet.setFillAfter(true);
		view.setVisibility(View.VISIBLE);
		view.startAnimation(animSet);
		lin_root.invalidate();
		duration = duration / 2;

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				textView.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
			}
		}, duration);
		is_NotFirst = true;
	}

	public void setDefaultView() {
		try {
			img_circle.clearAnimation();
			img_circle.setVisibility(View.GONE);
			for (int i = 0; i < textViews.length; i++) {
				textViews[i].setGravity(Gravity.BOTTOM
						| Gravity.CENTER_HORIZONTAL);
				textViews[i].setTag("off");
			}
			is_NotFirst = false;
		} catch (Exception e) {
		}
	}

	public void generateLayout(Context context, String[] footerParameters,
			int width) {
		if ((finalWidthChild * footerParameters.length) < width) {
			layGeneratorStreched(context, this.footerParameters);
			is_scrolled = false;
		} else {
			layGeneratorScrolled(context, this.footerParameters);
			is_scrolled = true;
		}
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public void layGeneratorScrolled(Context context, String[] footerParameters) {

		scrollView = new HorizontalScrollView(context);
		scrollView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		scrollView.setHorizontalScrollBarEnabled(false);
		scrollView.setVerticalScrollBarEnabled(false);
		if (android.os.Build.VERSION.SDK_INT >= 9) {
			scrollView
					.setOverScrollMode(HorizontalScrollView.OVER_SCROLL_NEVER);
		}

		LinearLayout layOye = new LinearLayout(context);
		layOye.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		layOye.setOrientation(LinearLayout.VERTICAL);

		LinearLayout layout_1 = new LinearLayout(context);
		layout_1.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				(int) (40 * screenDensity)));
		layout_1.setOrientation(LinearLayout.HORIZONTAL);

		for (int i = 0; i < footerParameters.length; i++) {
			Log.e("", " scrolled calciing i id : " + i);
			TextView view = createTextviews(context, footerParameters[i]);
			view.setTypeface(tf_subhead);
			textViews[i] = view;
			layout_1.addView(view);
		}

		layOye.addView(layout_1);

		RelativeLayout rLayout = new RelativeLayout(context);
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		params.setMargins(0, (int) (5 * screenDensity), 0,
				(int) (15 * screenDensity));
		rLayout.setLayoutParams(params);
		rLayout.setBackgroundColor(Color.parseColor("#20262B"));

		View view = new View(context);
		RelativeLayout.LayoutParams pa = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.FILL_PARENT,
				(int) (1 * screenDensity));
		pa.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		view.setBackgroundColor(Color.parseColor("#585C60"));
		view.setLayoutParams(pa);
		rLayout.addView(view);

		LinearLayout liner = new LinearLayout(context);
		liner.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		liner.setOrientation(LinearLayout.HORIZONTAL);

		for (int i = 0; i < footerParameters.length; i++) {
			liner.addView(getsomelay(context));
		}
		rLayout.addView(liner);

		img_circle = new ImageView(context);
		RelativeLayout.LayoutParams pr = new RelativeLayout.LayoutParams(
				(int) (14 * screenDensity), (int) (14 * screenDensity));
		pr.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		pr.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
		img_circle.setVisibility(View.INVISIBLE);
		img_circle.setImageResource(R.drawable.circleanim);
		img_circle.setLayoutParams(pr);

		rLayout.addView(img_circle);

		layOye.addView(rLayout);
		scrollView.addView(layOye);

		lin_root.addView(scrollView);
	}

	public void changeFootlistViewForStart() {
		// TODO Auto-generated method stub
		List<Restaurant> footlist;
		if (SearchFragment.isSearch) {
			footlist = SearchFragment.restos.getListByCategory(textViews[0]
					.getText().toString());
		} else {
			footlist = KentValues.catgorizedRestos
					.getListByCategory(textViews[0].getText().toString());
		}
		footListFragment.setFootlist(footlist);
	}

	public void layGeneratorStreched(Context context, String[] footerParameters) {

		LinearLayout layout_1 = new LinearLayout(context);
		layout_1.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				(int) (40 * screenDensity)));
		layout_1.setOrientation(LinearLayout.HORIZONTAL);

		for (int i = 0; i < footerParameters.length; i++) {
			Log.e("", " streched calciing i id : " + i);
			TextView view = createTextviewStreched(context, footerParameters[i]);
			view.setTypeface(tf_subhead);
			textViews[i] = view;
			layout_1.addView(view);
		}

		lin_root.addView(layout_1);

		RelativeLayout rLayout = new RelativeLayout(context);
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		params.setMargins(0, (int) (5 * screenDensity), 0,
				(int) (15 * screenDensity));
		rLayout.setLayoutParams(params);
		rLayout.setBackgroundColor(Color.parseColor("#20262B"));

		View view = new View(context);
		RelativeLayout.LayoutParams pa = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.FILL_PARENT,
				(int) (1 * screenDensity));
		pa.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		view.setBackgroundColor(Color.parseColor("#585C60"));
		view.setLayoutParams(pa);
		rLayout.addView(view);

		LinearLayout liner = new LinearLayout(context);
		liner.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		liner.setOrientation(LinearLayout.HORIZONTAL);

		for (int i = 0; i < footerParameters.length; i++) {
			liner.addView(getsomelay(context));
		}
		rLayout.addView(liner);

		img_circle = new ImageView(context);
		RelativeLayout.LayoutParams pr = new RelativeLayout.LayoutParams(
				(int) (14 * screenDensity), (int) (14 * screenDensity));
		pr.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		pr.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
		img_circle.setVisibility(View.INVISIBLE);
		img_circle.setImageResource(R.drawable.circleanim);
		img_circle.setLayoutParams(pr);
		rLayout.addView(img_circle);

		lin_root.addView(rLayout);
	}

	private RelativeLayout getsomelay(Context context) {
		final RelativeLayout rLayouter = new RelativeLayout(context);
		LinearLayout.LayoutParams para = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT, 1);
		para.setMargins((int) (15 * screenDensity), 0, 0, 0);
		rLayouter.setLayoutParams(para);

		ImageView imageView = new ImageView(context);
		RelativeLayout.LayoutParams pp = new RelativeLayout.LayoutParams(
				(int) (14 * screenDensity), (int) (14 * screenDensity));
		pp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		imageView.setImageResource(R.drawable.dil);
		imageView.setLayoutParams(pp);
		rLayouter.addView(imageView);
		return rLayouter;
	}

	public TextView createTextviews(Context context, String title) {
		TextView textView_1 = new TextView(context);
		textView_1.setLayoutParams(new LinearLayout.LayoutParams(
				(int) (finalWidthChild * screenDensity),
				LinearLayout.LayoutParams.FILL_PARENT));
		textView_1.setClickable(false);
		textView_1.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
		textView_1.setText(title);
		textView_1.setTextAppearance(context,
				android.R.style.TextAppearance_Small);
		textView_1.setTextColor(Color.WHITE);
		return textView_1;
	}

	public TextView createTextviewStreched(Context context, String title) {
		TextView textView_1 = new TextView(context);
		textView_1.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT, 1));
		textView_1.setClickable(false);
		textView_1.setSingleLine(true);
		textView_1.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
		textView_1.setText(title);
		textView_1.setTextAppearance(context,
				android.R.style.TextAppearance_Small);
		textView_1.setTextColor(Color.WHITE);
		return textView_1;
	}

	public void calStations() {
		for (int i = 0; i < textViews.length; i++) {
			TextView textView = textViews[i];
			textView.setTag("off");
			stations[i] = ((textView.getLeft()) + (textView.getWidth() / 2));
			parts[i] = textView.getRight();
			for (int j = 0; j < stations.length; j++) {
				Log.e("", j + " : " + stations[j]);
			}
		}
	}

	class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
		private static final String DEBUG_TAG = "Gestures";
		private int swipe_Min_Distance = 20;
		private int swipe_Max_Distance = 350;
		private int swipe_Min_Velocity = 15;

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			Log.e(DEBUG_TAG, "onFling");
			boolean result = false;
			if (!isLoading) {
				isLoading = true;
				final float xDistance = Math.abs(e1.getX() - e2.getX());
				final float yDistance = Math.abs(e1.getY() - e2.getY());

				if (xDistance > this.swipe_Max_Distance
						|| yDistance > this.swipe_Max_Distance)
					return false;

				velocityX = Math.abs(velocityX);
				velocityY = Math.abs(velocityY);

				if (velocityY > this.swipe_Min_Velocity
						&& yDistance > this.swipe_Min_Distance) {

					if (e1.getY() > e2.getY()) // bottom to up
					{
						TextView textView = textViews[0];
						trans(img_circle, 0, stations[0], textView);
						current = stations[0];

						fm.beginTransaction().show(footListFragment).commit();
						textView.setTag("on");
						for (int j = 0; j < textViews.length; j++) {
							if (j != 0) {
								textViews[j].setTag("off");
							}
						}
					} else {
						getActivity().onBackPressed();
						for (int j = 0; j < textViews.length; j++) {
							textViews[j].setTag("off");
						}
						setDefaultView();
					}
					result = true;
				}
				isLoading = false;

			}
			return result;
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			if (!isLoading) {
				isLoading = true;
				int touchPoint = (int) e.getX();
				Log.e("", "touchPoint is : " + touchPoint);
				int lastpoint = 0;
				int eventI = 0;

				for (int i = 0; i < parts.length; i++) {
					int p = parts[i];
					if (touchPoint > lastpoint && (touchPoint <= p)) {
						Log.e("", "got true");
						eventI = i;
						lastpoint = p;
					} else {
						lastpoint = p;
					}
				}

				TextView textView = textViews[eventI];
				if (textView.getTag() != null
						&& textView.getTag().toString().equalsIgnoreCase("off")) {
					if (!is_NotFirst) {
						trans(img_circle, 0, stations[eventI], textView);
						current = stations[eventI];

						Log.e("", "1");
						footListFragment.setCategory(textView.getText()
								.toString());
						Log.e("", "2");
						fm.beginTransaction().show(footListFragment).commit();
						textView.setTag("on");
						for (int j = 0; j < textViews.length; j++) {
							if (j != eventI) {
								textViews[j].setTag("off");
							}
						}
					} else {
						trans(img_circle, current, stations[eventI], textView);
						current = stations[eventI];
						Log.e("", "3");
						List<Restaurant> footlist;
						if (SearchFragment.isSearch) {
							footlist = SearchFragment.restos
									.getListByCategory(textView.getText()
											.toString());
							Log.e("", "4");
						} else {
							footlist = KentValues.catgorizedRestos
									.getListByCategory(textView.getText()
											.toString());
							Log.e("", "5");
						}
						Log.e("", "6");
						footListFragment.setFootlist(footlist);
						textView.setTag("on");
						for (int j = 0; j < textViews.length; j++) {
							if (j != eventI) {
								textViews[j].setTag("off");
							}
						}
					}
				} else {
					getActivity().onBackPressed();
					textView.setTag("off");
					setDefaultView();
				}

				Log.e(DEBUG_TAG, "onSingleTapConfirmed");
				isLoading = false;

			}
			return super.onSingleTapConfirmed(e);
		}
	}

	// public void calStations() {
	//
	// int total = parts.length;
	// int art = width / total;
	//
	// for (int i = 0; i < total; i++) {
	// parts[i] = art * (i + 1);
	// }
	//
	// int part = art / 2;
	// for (int i = 0; i < total; i++) {
	// stations[i] = part * (i + 1);
	// for (int j = 0; j < stations.length; j++) {
	// Log.e("", j + " : " + stations[j]);
	// }
	// }
	// }
}
