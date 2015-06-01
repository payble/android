package netleon.sansar.kent.support;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextFonts_SubHead extends TextView {

	public TextFonts_SubHead(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public TextFonts_SubHead(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public TextFonts_SubHead(Context context) {
		super(context);
		init();
	}

	private void init() {
		Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
				"fonts/Avenir.ttc");
		setTypeface(tf);
	}

}