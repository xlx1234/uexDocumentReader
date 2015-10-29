package org.zywx.wbpalmstar.plugin.uexdocument;

import java.io.File;

import org.zywx.wbpalmstar.engine.EBrowserView;
import org.zywx.wbpalmstar.engine.universalex.EUExBase;
import org.zywx.wbpalmstar.engine.universalex.EUExUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

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
			Toast.makeText(mContext,  EUExUtil.getString("plugin_uexDocumentReader_file_not_exist"), Toast.LENGTH_SHORT).show();
			return;
		}

		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		String type = DocumentUtils.getMIMEType(file);
		intent.setDataAndType(Uri.fromFile(file), type);
		try {
			intent.setAction(Intent.ACTION_VIEW);
			startActivity(Intent.createChooser(intent, null));
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
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

			if (result == null) {
				return;
			}
			File file = new File(result);
			if (file.exists()) {
				openDocumentByThrid(file);
			} else {
				FileUtils.showToast((Activity) mContext, EUExUtil.getString("plugin_uexDocumentReader_file_not_exist"));
			}
			fileTask = null;

		}

	}

	@Override
	protected boolean clean() {

		return false;
	}
}