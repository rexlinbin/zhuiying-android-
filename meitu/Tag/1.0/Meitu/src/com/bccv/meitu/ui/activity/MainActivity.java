package com.bccv.meitu.ui.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.ImageView;

import com.bccv.meitu.ApplicationManager;
import com.bccv.meitu.R;
import com.bccv.meitu.api.NetWorkAPI;
import com.bccv.meitu.model.Version;
import com.bccv.meitu.network.HttpCallback;
import com.bccv.meitu.network.NetResBean;
import com.bccv.meitu.sns.UserInfoManager;
import com.bccv.meitu.ui.adapter.HomeFragmentPagerAdapter;
import com.bccv.meitu.ui.fragment.HomeChildFramgent;
import com.bccv.meitu.utils.ExitUtils;
import com.bccv.meitu.utils.Logger;
import com.bccv.meitu.utils.SystemUtil;
import com.bccv.meitu.utils.YuseUtil;
import com.bccv.meitu.view.MenuPopwindow;
import com.nostra13.universalimageloader.utils.ImageLoaderUtil;

public class MainActivity extends BaseFragmentActivity  {
	
	private ViewPager mViewPager;
	private HomeFragmentPagerAdapter homeFragmentPagerAdapter;
	private FragmentManager fragmentManager;
	private ArrayList<Fragment> fragmentList;
	
	private View home_root;
	private View home_top_title;
	
	private View left_buton;
	private View right_buton;
	private View home_attention_btn;
	private View home_hot_btn;
	private View home_fresh_btn;
	
	private ImageView user_icon;
	
	private MenuPopwindow menuPopwindow;
	
	private static final int HOME_ATTENTION = 0;
	private static final int HOME_HOT = 1;
	private static final int HOME_FRESH = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		initView();
	}

	private void initView(){
		
		home_root = findViewById(R.id.home_root);
		
		fragmentManager = getSupportFragmentManager();
		
		home_top_title = findViewById(R.id.home_top_title);
		left_buton = home_top_title.findViewById(R.id.left_buton);
		right_buton = home_top_title.findViewById(R.id.right_buton);
		home_attention_btn = home_top_title.findViewById(R.id.home_attention_btn);
		home_hot_btn = home_top_title.findViewById(R.id.home_hot_btn);
		home_fresh_btn = home_top_title.findViewById(R.id.home_fresh_btn);
		
		user_icon = (ImageView)findViewById(R.id.user_icon);
		
		if(UserInfoManager.isLogin()){
			ImageLoaderUtil.getInstance(mContext).displayImage(UserInfoManager.getUserIcon(),
					user_icon, ImageLoaderUtil.getUserIconImageOptions());
		}else{
			user_icon.setImageBitmap(null);
		}
		
		left_buton.setOnClickListener(this);
		right_buton.setOnClickListener(this);
		home_attention_btn.setOnClickListener(this);
		home_hot_btn.setOnClickListener(this);
		home_fresh_btn.setOnClickListener(this);
		right_buton.setVisibility(View.VISIBLE);
		
		mViewPager = (ViewPager) findViewById(R.id.home_vp);
		initChilds();
		homeFragmentPagerAdapter = new HomeFragmentPagerAdapter(fragmentManager, fragmentList);
		mViewPager.setAdapter(homeFragmentPagerAdapter);
		mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());
		mViewPager.setCurrentItem(1);
		mViewPager.setOffscreenPageLimit(3);
		getVersion();//获取版本更新信息
	}
	
	private void initChilds()
	{
		// 初始化各个孩子fragment;
		// cate 查询分类id（0 关注，1热门，2最新）
		fragmentList = new ArrayList<Fragment>();
		for(int i = 0;i < 3;i++)
		{
			HomeChildFramgent homeChildFramgent = new HomeChildFramgent();
			switch (i) {
			case 0://关注
				homeChildFramgent.setCate(HomeChildFramgent.ATTENTION);
				break;
			case 1://热门
				homeChildFramgent.setCate(HomeChildFramgent.HOT);
				break;
			case 2://最新
				homeChildFramgent.setCate(HomeChildFramgent.FRESH);
				break;
			default:
				break;
			}
			
			fragmentList.add(homeChildFramgent);
		}
	}
	
	private void resetTags(){
		home_attention_btn.setBackgroundResource(R.drawable.home_attention_btn);
		home_hot_btn.setBackgroundResource(R.drawable.home_hot_btn);
		home_fresh_btn.setBackgroundResource(R.drawable.home_fresh_btn);
	}
	
	
	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.left_buton: //左按钮
			if(menuPopwindow==null){
				menuPopwindow = new MenuPopwindow(this);
			}
			menuPopwindow.show(home_root);
			
			// download test
			
//			String url = "http://dldir1.qq.com/weixin/android/weixin540android480.apk";
//			String name = "weixin540android480.apk";
//			int ver = 1;
//			DownLoadAPI.downLoadApk(url, name, "com.chatmm.weixin", 1, true, true);
//			
//			DownLoadAPI.downLoadFile("http://b.hiphotos.baidu.com/image/pic/item/9e3df8dcd100baa184040b054510b912c8fc2e67.jpg", "9e3df8dcd100baa184040b054510b912c8fc2e67.jpg", true);
			
			break;
		case R.id.right_buton: // 右按钮
			
//			if(UserInfoManager.isLogin()){
				Intent intent2 = new Intent(this, UserActivity.class);
				startActivity(intent2);
//			}else{
//				Intent intent = new Intent(this, LoginActivity.class);
//				startActivity(intent);
//			}
			
			break;
		case R.id.home_attention_btn: //关注
			
			if(UserInfoManager.isLogin()){
				mViewPager.setCurrentItem(0);
			}else{
				Intent intent = new Intent(mContext, LoginActivity.class);
				startActivity(intent);
			}
			
			break;
		case R.id.home_hot_btn: // 热门
			mViewPager.setCurrentItem(1);
			break;
		case R.id.home_fresh_btn: // 最新
			mViewPager.setCurrentItem(2);
			break;

		default:
			break;
		}
		
	}
	
	@Override
	protected void onResume() {
		if(UserInfoManager.isLogin()){
			ImageLoaderUtil.getInstance(mContext).displayImage(UserInfoManager.getUserIcon(),
					user_icon, ImageLoaderUtil.getUserIconImageOptions());
		}else{
			if(mViewPager.getCurrentItem()==HOME_ATTENTION){
				mViewPager.setCurrentItem(HOME_HOT);
			}
			HomeChildFramgent fragment = (HomeChildFramgent) fragmentList.get(HOME_ATTENTION);
			fragment.reset();
			user_icon.setImageBitmap(null);
		}
		super.onResume();
	}
	
	@Override
	public void onBackPressed() {
		if(ExitUtils.isExit(this)){
			super.onBackPressed();
			ApplicationManager.getInstance().exitSystem();
		}
	}
	
	private class MyOnPageChangeListener implements OnPageChangeListener{

		@Override
		public void onPageScrollStateChanged(int position) {}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {}

		@Override
		public void onPageSelected(int position) {
			// 滑动ViewPager 回调  处理
			
			if(position == HOME_ATTENTION && (!UserInfoManager.isLogin())){
				mViewPager.setCurrentItem(1);
				Intent intent = new Intent(mContext, LoginActivity.class);
				startActivity(intent);
				return;
			}
			
			resetTags();
			switch (position) {
			case HOME_ATTENTION:
				home_attention_btn.setBackgroundResource(R.drawable.home_attention_btn_select);
				break;
			case HOME_HOT:
				home_hot_btn.setBackgroundResource(R.drawable.home_hot_btn_select);
				break;
			case HOME_FRESH:
				home_fresh_btn.setBackgroundResource(R.drawable.home_fresh_btn_select);
				break;

			default:
				break;
			}
		}
	}
	
	private void getVersion(){
		if (SystemUtil.isNetOkWithToast(getApplicationContext())) {
			NetWorkAPI.getversion(getApplicationContext(), new HttpCallback() {
				@Override
				public void onResult(NetResBean response) {
					if (response.success && response instanceof Version) {
						Version data = (Version) response;
						YuseUtil.showUpdateDialog(data, mContext);
					} else {
						Logger.e(TAG, "getVersion onResult", "response info is error");
					}
				}
	
				@Override
				public void onError(String errorMsg) {
					Logger.e(TAG, "getVersion onError", "errorMsg : " + errorMsg);
				}
	
				@Override
				public void onCancel() {}
			});
		}
	}
}
