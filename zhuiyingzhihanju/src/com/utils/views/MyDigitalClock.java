package com.utils.views;

import java.util.Calendar;

import android.content.Context;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.widget.DigitalClock;

@SuppressWarnings("deprecation")
public class MyDigitalClock extends DigitalClock {

	Calendar mCalendar;
//	private final static String mFormat = "yyyy.MM/dd kk:mm";// h:mm:ss
	private final static String mFormat = "kk:mm";// hh:mm:ss
																		// aa
	private FormatChangeObserver mFormatChangeObserver;

	private Runnable mTicker;
	private Handler mHandler;

	private boolean mTickerStopped = false;

	public MyDigitalClock(Context context) {
		super(context);
		initClock(context);
	}

	public MyDigitalClock(Context context, AttributeSet attrs) {
		super(context, attrs);
		initClock(context);
	}

	@SuppressWarnings("unused")
	private void initClock(Context context) {
		Resources r = context.getResources();
		if (mCalendar == null) {
			mCalendar = Calendar.getInstance();
		}
		mFormatChangeObserver = new FormatChangeObserver();
		getContext().getContentResolver().registerContentObserver(
				Settings.System.CONTENT_URI, true, mFormatChangeObserver);

	}

	@Override
	protected void onAttachedToWindow() {
		mTickerStopped = false;
		super.onAttachedToWindow();

		mHandler = new Handler();

		mTicker = new Runnable() {
			@Override
			public void run() {
				if (mTickerStopped) {
					return;
				}
				mCalendar.setTimeInMillis(System.currentTimeMillis());
				setText(DateFormat.format(mFormat, mCalendar));
				invalidate();
				long now = SystemClock.uptimeMillis();
				long next = now + (1000 - now % 1000);
				mHandler.postAtTime(mTicker, next);
			}
		};
		mTicker.run();
	}

	private class FormatChangeObserver extends ContentObserver {

		public FormatChangeObserver() {
			super(new Handler());
		}

		@Override
		public void onChange(boolean selfChange) {

		}
	}

}
