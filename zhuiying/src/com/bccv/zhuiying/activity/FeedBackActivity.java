package com.bccv.zhuiying.activity;

import com.bccv.zhuiying.R;
import com.bccv.zhuiying.api.FeedbackApi;
import com.tendcloud.tenddata.TCAgent;
import com.utils.tools.BaseActivity;
import com.utils.tools.Callback;

import android.annotation.SuppressLint;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class FeedBackActivity extends BaseActivity {
	EditText feedBackEdit;

	
	private int MAX_LENGTH = 200;
	private TextView lengThText;
	int Rest_Length = MAX_LENGTH;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		TCAgent.onPageStart(getApplicationContext(), "FeedBackActivity");
		setContentView(R.layout.activity_feedback);

		ImageButton backBtn = (ImageButton) findViewById(R.id.titel_back);
		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		TextView text = (TextView) findViewById(R.id.titleName_textView);
		text.setVisibility(View.VISIBLE);
		text.setText("意见反馈");

		lengThText = (TextView) findViewById(R.id.feedback_text);

		ImageButton clearBtn = (ImageButton) findViewById(R.id.titel_search);
		clearBtn.setVisibility(View.INVISIBLE);
		TextView cleartext = (TextView) findViewById(R.id.titel_clear);
		cleartext.setVisibility(View.VISIBLE);
		cleartext.setText("提交");

		feedBackEdit = (EditText) findViewById(R.id.feedback_edit);

		feedBackEdit.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (Rest_Length > 0) {
					Rest_Length = MAX_LENGTH - feedBackEdit.getText().length();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				lengThText.setText("还可以输入" + Rest_Length + "个字");

			}

			@Override
			public void afterTextChanged(Editable s) {
				lengThText.setText("还可以输入" + Rest_Length + "个字");
			}

		});

		cleartext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				sendFeedback(feedBackEdit.getText().toString());

			}
		});

	}

	private void sendFeedback(final String content) {
		Callback callback = new Callback() {

			@SuppressLint("ShowToast")
			@Override
			public void handleResult(String result) {
				// TODO Auto-generated method stub
				if (result.equals("true")) {
					Toast.makeText(getApplicationContext(), "反馈成功", 1).show();
					finish();
				} else {
					Toast.makeText(getApplicationContext(), "反馈失败", 1).show();
				}

			}
		};

		new DataAsyncTask(callback, true) {

			@Override
			protected String doInBackground(String... params) {
				FeedbackApi feedbackApi = new FeedbackApi();
				boolean request;
				try {
					request = feedbackApi.seedFeedback(getVersionName(), content);
					if (request) {
						return "true";
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return "false";
			}
		}.executeProxy("");
	}

	private String getVersionName() throws Exception {
		// 获取packagemanager的实例
		PackageManager packageManager = getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
		String version = packInfo.versionName;
		return version;
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		TCAgent.onPageEnd(getApplicationContext(), "FeedBackActivity");
	}
}
