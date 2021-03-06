package com.bccv.meitu.download;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.bccv.meitu.R;
import com.bccv.meitu.api.DownLoadAPI;
import com.bccv.meitu.model.Download;
import com.bccv.meitu.model.UnfinshedDownloadInfo;
import com.bccv.meitu.utils.Logger;
import com.bccv.meitu.utils.SystemUtil;

public class DownloadManager {
	
	private static final String TAG = "DownLoadManager";
	public static ArrayList<UnfinshedDownloadInfo> unFishedDownLod ;
	private HashMap<String, DownloadRunnable> mDownloadRunnableMap = new HashMap<String, DownloadRunnable>();
	private static final DownloadManager mInstance = new DownloadManager();
	private static NotificationManager mNotificationManager = null;
	private Handler mHandler;
	
	public static final int DOWNLOAD_SUCCESS = 1;
	public static final int DOWNLOAD_FAILED = 2;
	public static final int SHOW_PROGRESS = 3;
	
	private static Context mContext;
	
	public static void initDownload(Context context){
		mContext = context;
		if (mNotificationManager == null) {
			mNotificationManager = (NotificationManager) mContext
					.getSystemService(Context.NOTIFICATION_SERVICE);
		}
	}
	
	private DownloadManager() {
		if(mHandler == null){
			iniHandler();
		}
	}
	
	public static DownloadManager getInstance() {
		if(mContext==null){
			throw new IllegalArgumentException(" initDownload(Context context) should be run first !!! ");
		}
		return mInstance;
	}
	
	
	@SuppressLint("HandlerLeak") 
	private void iniHandler(){
		mHandler = new Handler(){
			
			@Override
			public void handleMessage(Message msg) {
				
				Download download = null;
				if(msg.obj instanceof Download){
					download = (Download)(msg.obj);
				}
				switch (msg.what) {
				case DOWNLOAD_SUCCESS:
					// 下载成功  
					if(download!=null){
						Logger.v(TAG, "downLoad",  " download.DOWNLOAD_SUCCESS ！！！");
						mDownloadRunnableMap.remove(download.getDownloadUrl());
						removeProgressNotifi(download);
						//上报下载成功
						
						if(download.isApk() && download.isDownloadIsInstall()){
							if(!TextUtils.isEmpty(download.getDownloadFilePackageName())&&download.getDownloadFileVer()>0){
								PackageInfo info = SystemUtil.getInstance().checkApkExist(mContext, download.getDownloadFilePackageName());
								if(info!=null&&info.versionCode>=download.getDownloadFileVer()){
									Logger.v(TAG, "downLoad",  "info.versionCode : " +info.versionCode);
									Logger.v(TAG, "downLoad",  download.getDownloadFileName() +" is exist or higher version is exist !!! ");
									return;
								}
							}
							Logger.v(TAG, "downLoad",  " download.isDownloadIsInstall() : " + download.isDownloadIsInstall());
							// 安装
							SystemUtil.getInstance().installApp(download.getDownloadStoragePath());
//							PackageInstaller pi = new PackageInstaller(mContext);
//							pi.installBatch(download.getDownloadStoragePath());
						}
						//TODO 下载完成提示 
						
						// 如果是下载文件  或 下载apk但没有自动安装
						if((!download.isApk()) || (download.isApk() && !download.isDownloadIsInstall())){
							Toast.makeText(mContext, "已保存到 ：" + download.getDownloadStoragePath(), Toast.LENGTH_SHORT).show();
						}
						
						
					}
					break;
				case DOWNLOAD_FAILED:
					// 下载失败 移除列表
					if(download!=null){
						//TODO  下载失败
						Logger.v(TAG, "downLoad",  " download.DOWNLOAD_FAILED ！！！");
						mDownloadRunnableMap.remove(download.getDownloadUrl());
						removeProgressNotifi(download);
						Toast.makeText(mContext, "下载失败", Toast.LENGTH_SHORT).show();
					}
					//TODO 上报下载失败 
					break;
				case SHOW_PROGRESS:
					Logger.v(TAG, "downLoad",  " download.SHOW_PROGRESS ！！！");
					//TODO 发送下载进度通知进度条
					showProgressNotifi(download);
					break;

				default:
					break;
				}
				
				//下载列表为空  关闭服务
				if(mDownloadRunnableMap==null||mDownloadRunnableMap.size()==0){
					DownLoadAPI.stopDownLoadService(mContext);
				}
				
			}
		};
	}
	
	
	private void removeProgressNotifi(Download download){
		if(download.showProgress()){
			//TODO 移除下载进度条
			mNotificationManager.cancel(download.getProgressTag());
			
		}
	}
	
	private void showProgressNotifi(Download download){
		if(download.showProgress()){
			//TODO 发送进度条
			
			Notification runningNotification = download
					.getDownloadRunningNotification();

			if (runningNotification != null
					&& runningNotification.contentView != null) {
				// 更新进度
				runningNotification.contentView.setTextViewText(R.id.down_title,
						download.getDownloadFileName());
				runningNotification.contentView.setTextViewText(R.id.tvProcess,
						"已下载" + download.getDownloadProgress() + "%");
				runningNotification.contentView.setProgressBar(R.id.pbDownload,
						100, (int)download.getDownloadProgress(), false);
				
				mNotificationManager.notify(
						download.getProgressTag(),
						runningNotification);
				
			}
			
			
		}
	}
	
	
	/**
	 * 
	 * 下载任务
	 * 
	 * @param download
	 *            下载对象
	 * @return int 操作结果<br>
	 *         1 添加成功<br>
	 *         -1 任务已存在 <br>
	 */
	public int addDownloadFile(Download download) {

		// 判断任务是否重复
		if (mDownloadRunnableMap.containsKey(download.getDownloadUrl())) {

			Logger.v(TAG, "addDownloadFile", " this url already in the download list !!! ");
			
			DownloadRunnable downloadRunnable = mDownloadRunnableMap
					.get(download.getDownloadUrl());

			if (downloadRunnable != null) {
				if (downloadRunnable.isIsStop()) {
					// 删除已停止的任务
					mDownloadRunnableMap.remove(download.getDownloadUrl());
				} else {
					// 重复
					return -1;
				}
			}
		}
		if(download.showProgress()){
			download.setDownloadNotification(mContext);
		}
		// 创建下载任务,开始下载
		DownloadRunnable downloadRunnable = new DownloadRunnable(download,mHandler);
		ThreadPoolManager.getInstance().addFileTask(downloadRunnable);
		mDownloadRunnableMap.put(download.getDownloadUrl(), downloadRunnable);
		return 1;
	}
	
	

}
