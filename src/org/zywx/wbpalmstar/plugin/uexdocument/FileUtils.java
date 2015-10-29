package org.zywx.wbpalmstar.plugin.uexdocument;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.zywx.wbpalmstar.base.BUtility;
import org.zywx.wbpalmstar.engine.EBrowserView;
import org.zywx.wbpalmstar.engine.universalex.EUExUtil;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Environment;
import android.widget.Toast;

public class FileUtils {

	public static void showToast(final Activity activity, final String msg) {
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
			}
		});
	}

	public static void showMsg(Context context, String resKey) {
		String msg = context.getString(EUExUtil.getResStringID(resKey));
		showToast((Activity) context, msg);
	}

	public static String getStr(Context context, String resKey) {
		return context.getString(EUExUtil.getResStringID(resKey));
	}

	public static void toast(final Activity activity, final String msg) {
        ((ActivityGroup) activity).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
            }
        });
	}

	public static String assetToSd(Context ctx, String inFileName,
			String outFileName) {
		try {
			InputStream is = ctx.getAssets().open(inFileName);
			if (is == null) {
				return null;
			}

			if (Environment.MEDIA_MOUNTED.equals(Environment
					.getExternalStorageState())) {
				String sd = Environment.getExternalStorageDirectory()
						.getAbsolutePath();
				String path = sd + "/uexDocument/";
				File filePath = new File(path);
				if (!filePath.exists()) {
					filePath.mkdirs();
				}
				String fileName = path + outFileName;
				FileOutputStream fos = new FileOutputStream(fileName);

				byte[] buf = new byte[1024];
				int len = 0;
				while ((len = is.read(buf)) != -1) {
					fos.write(buf, 0, len);
				}
				is.close();
				fos.close();
				return fileName;
			}

		} catch (Exception e) {
			toast((Activity) ctx,
			        EUExUtil.getString("plugin_uexDocumentReader_file_not_exist_or_path_error"));
			e.printStackTrace();
			return null;
		}
		return null;
	}

	public static byte[] assetToByte(Context ctx, String inFileName) {
		try {
			InputStream is = ctx.getAssets().open(inFileName);
			if (is == null) {
				return null;
			}
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			int len = 0;
			while ((len = is.read(buf)) != -1) {
				baos.write(buf, 0, len);
			}
			is.close();
			baos.close();
			return baos.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getAbsPath(String path, EBrowserView mBrwView) {
		return BUtility.makeRealPath(path,
				mBrwView.getCurrentWidget().m_widgetPath,
				mBrwView.getCurrentWidget().m_wgtType);
	}

	public static String makeFile(Context context, String filePath) {
		if (filePath == null || filePath.length() == 0) {
			return null;
		}
		String fileName;
		if (!filePath.startsWith("/")) {
			if (filePath.contains("/")) {
				fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
			} else {
				fileName = filePath;
			}
			return assetToSd(context, filePath, fileName);
		} else {
			return filePath;
		}
	}

	public static ProgressDialog showLoadDialog(Context context) {
		ProgressDialog dialog = new ProgressDialog(context);
		dialog.show();
		return dialog;
	}

	private FileUtils(Context ctx) {

	}

	public static class ResUtils {
		private static ResUtils sInstance = null;

		private static String packName;
		private static Resources res;

		private ResUtils(Context ctx) {
			packName = ctx.getPackageName();
			res = ctx.getResources();
		}

		public static ResUtils getInstanct(Context ctx) {
			if (sInstance == null) {
				sInstance = new ResUtils(ctx);
			}
			return sInstance;
		}

		public static int getResId(String key, String type) {
			return res.getIdentifier(key, type, packName);
		}

		public static int getResMenuId(String key) {
			return getResId(key, "menu");
		}

		public static int getResArrayId(String key) {
			return getResId(key, "array");
		}

		public static int[] getStyleable(String key) {
			return res.getIntArray(getResId(key, "styleable"));
		}
	}
}