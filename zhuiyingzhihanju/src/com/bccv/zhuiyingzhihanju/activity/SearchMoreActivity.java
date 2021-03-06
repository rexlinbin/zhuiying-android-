package com.bccv.zhuiyingzhihanju.activity;

import java.util.ArrayList;
import java.util.List;

import com.bccv.zhuiyingzhihanju.R;
import com.bccv.zhuiyingzhihanju.adapter.SearchAdapter;
import com.bccv.zhuiyingzhihanju.api.FilterApi;
import com.bccv.zhuiyingzhihanju.api.SearchApi;
import com.bccv.zhuiyingzhihanju.model.SearchInfo;
import com.bccv.zhuiyingzhihanju.model.SearchType;
import com.tendcloud.tenddata.TCAgent;
import com.utils.net.NetUtil;
import com.utils.pulltorefresh.FooterLoadingLayout;
import com.utils.pulltorefresh.PullToRefreshBase;
import com.utils.pulltorefresh.PullToRefreshListView;
import com.utils.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.utils.tools.BaseActivity;
import com.utils.tools.Callback;
import com.utils.tools.GlobalParams;
import com.utils.tools.PromptManager;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.pdf.PdfDocument.Page;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class SearchMoreActivity extends BaseActivity {
	private SearchAdapter adapter;
	private List<SearchInfo> list, getList;
	private PullToRefreshListView pullToRefreshListView;
	private ListView listView;
	private int page = 1;
	private String type_id, keyword;
	private void tcStart(){
		TCAgent.onPageStart(getApplicationContext(), "SearchMoreActivity");
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		TCAgent.onPageEnd(getApplicationContext(), "SearchMoreActivity");
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		tcStart();
		setContentView(R.layout.activity_searchmore);
		String title = getIntent().getStringExtra("title");
		keyword = getIntent().getStringExtra("keyword");
		type_id = getIntent().getStringExtra("type_id");
		TextView titleName = (TextView) findViewById(R.id.titleName_textView);
		titleName.setText(title);
		titleName.setVisibility(View.VISIBLE);

		ImageButton backBtn = (ImageButton) findViewById(R.id.titel_back);
		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method  stub
				finish();
			}
		});
		ImageButton clearBtn = (ImageButton) findViewById(R.id.titel_search);
		clearBtn.setVisibility(View.INVISIBLE);
		TextView cleartext = (TextView) findViewById(R.id.titel_clear);
		cleartext.setVisibility(View.INVISIBLE);
		
		pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.listView);
		listView = pullToRefreshListView.getRefreshableView();
		list = new ArrayList<>();
		adapter = new SearchAdapter(getApplicationContext(), list);
		listView.setAdapter(adapter);
		listView.setSelector(new ColorDrawable(android.R.color.transparent));
		listView.setDividerHeight(0);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(), VideoInfoActivity.class);
				intent.putExtra("movie_id", list.get(position).getId());
				intent.putExtra("type_id", list.get(position).getType_id());
				intent.putExtra("episodes_id", "0");
				startActivity(intent);
			}
		});
		
		pullToRefreshListView.setPullLoadEnabled(true);
		pullToRefreshListView.setPullRefreshEnabled(true);
		pullToRefreshListView.getRefreshableView().setSelector(new ColorDrawable(android.R.color.transparent));
		pullToRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				if (!NetUtil.isNetworkAvailable(GlobalParams.context)) {
					// 提示网络不给力,直接完成刷新
					PromptManager.showToast(GlobalParams.context, "网络不给力");

					pullToRefreshListView.onPullDownRefreshComplete();
				} else {
					page = 1;
					getData(true);

				}
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				if (NetUtil.isNetworkAvailable(GlobalParams.context)) {
					((FooterLoadingLayout) pullToRefreshListView.getFooterLoadingLayout()).getmHintView()
							.setText("数据加载中...");
					getData(false);
				} else {
					PromptManager.showToast(GlobalParams.context, "网络不给力");
					pullToRefreshListView.onPullUpRefreshComplete();
				}
			}
		});
		
		pullToRefreshListView.doPullRefreshing(true, 100);
	}
	
	private SearchType searchType;
	private void getData(final boolean isRefresh) {
		Callback callback = new Callback() {

			@Override
			public void handleResult(String result) {
				// TODO Auto-generated method stub
				if (getList != null && getList.size() > 0) {
					if (isRefresh) {
						list.clear();
					}
					list.addAll(getList);
					adapter.notifyDataSetChanged();
					page++;
				}
				if (isRefresh) {
					pullToRefreshListView.onPullDownRefreshComplete();
				}else{
					pullToRefreshListView.onPullUpRefreshComplete();
				}
				
			}
		};

		new DataAsyncTask(callback, false) {

			@Override
			protected String doInBackground(String... params) {
				// TODO Auto-generated method stub
				SearchApi searchApi = new SearchApi();
				searchType = searchApi.getSearchMoreList(keyword, page + "", type_id);
				if (searchType != null) {
					getList = searchType.getResult();
				}
				
				return null;
			}
		}.execute("");
	}
}
