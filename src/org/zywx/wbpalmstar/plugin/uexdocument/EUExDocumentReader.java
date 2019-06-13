package org.zywx.wbpalmstar.plugin.uexdocument;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import org.zywx.wbpalmstar.engine.EBrowserView;
import org.zywx.wbpalmstar.engine.universalex.EUExBase;
import org.zywx.wbpalmstar.engine.universalex.EUExUtil;

import java.io.File;
import java.util.List;

import static org.zywx.wbpalmstar.plugin.uexdocument.DocumentUtils.getMIMEType;

public class EUExDocumentReader extends EUExBase {

	public EUExDocumentReader(Context arg0, EBrowserView arg1) {
		super(arg0, arg1);
	}



	public void openDocumentReader(String[] args) {
		if (args == null || args.length < 1) {
			return;
		}
		String filePath = args[0];
		openDocument(FileUtils.getAbsPath(filePath, mBrwView));
	}

	public void close(String[] args) {

	}

	private FileTask fileTask = null;

	private void openDocument(String filePath) {
		if (fileTask == null) {
			fileTask = new FileTask(filePath);
			fileTask.execute();
		}
	}

	private void openDocumentByThrid(final File file) {
		if (!file.exists()) {
	        ((Activity) mContext).runOnUiThread(new Runnable() {
	            @Override
	            public void run() {
	                Toast.makeText(mContext,
	                        EUExUtil.getString("plugin_uexDocumentReader_file_not_exist"),
	                        Toast.LENGTH_SHORT).show();
	            }
	        });
			return;
		}
		openFile(mContext,file);
//		Intent intent = new Intent();
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		String type = getMIMEType(file);
//		Uri uri = Uri.fromFile(file);
////		Uri photoURI = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".provider",file);
//		intent.setDataAndType(uri, type);
//		try {
//			intent.setAction(Intent.ACTION_VIEW);
//			startActivity(Intent.createChooser(intent, null));
//		} catch (ActivityNotFoundException e) {
//			e.printStackTrace();
//		}
	}

	public  void openFile(Context context, File file){
		try {
			Intent intent = new Intent();
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			//设置intent的Action属性
			intent.setAction(Intent.ACTION_VIEW);
			//获取文件file的MIME类型
			String type = getMIMEType(file);
			//设置intent的data和Type属性。android 7.0以上crash,改用provider
			if (Build.VERSION.SDK_INT >= 24) {
				Uri fileUri = FileProvider.getUriForFile(context, context.getPackageName()+".provider", file);//android 7.0以上
				intent.setDataAndType(fileUri, type);
//				grantUriPermission(context, fileUri, intent);
				List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
				for (ResolveInfo resolveInfo : resInfoList) {
					String packageName = resolveInfo.activityInfo.packageName;
					context.grantUriPermission(packageName, fileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
				}
			}else {
				intent.setDataAndType(/*uri*/Uri.fromFile(file), type);
			}
			//跳转
			context.startActivity(intent);
		}catch (Exception e){
			e.printStackTrace();
			Toast.makeText(context, "File corrupted, download again please.", Toast.LENGTH_SHORT).show();
		}

	}

	class FileTask extends AsyncTask<Void, Void, String> {
		String filePath;
		ProgressDialog dialog;

		public FileTask(String path) {
			filePath = path;
		}

		@Override
		protected void onPreExecute() {
			dialog = FileUtils.showLoadDialog(mContext);
		}

		@Override
		protected String doInBackground(Void... params) {
			return FileUtils.makeFile(mContext, filePath);
		}

		@Override
		protected void onPostExecute(String result) {

			if (dialog != null) {
				dialog.dismiss();
			}

			if (result != null)
			{
				File file = new File(result);
				if (file.exists()) {
				    openDocumentByThrid(file);
				} else {
				    FileUtils.showToast((Activity) mContext,
				            EUExUtil.getString("plugin_uexDocumentReader_file_not_exist"));
				}
			}
			fileTask = null;
		}
	}

	@Override
	protected boolean clean() {

		return false;
	}
}