package com.blackcatwalk.sharingpower.camera;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class UploadImageUtils {

	public int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {

		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}

	public Bitmap decodeFile(String path, int reqWidth, int reqHeight) {
		int orientation;

		try {
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, options);

			options.inSampleSize = calculateInSampleSize(options, reqWidth,
					reqHeight);

			options.inJustDecodeBounds = false;
			Bitmap bm = BitmapFactory.decodeFile(path, options);

			ExifInterface exif = new ExifInterface(path);
			orientation = exif
					.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
			Matrix m = new Matrix();

			if ((orientation == 3)) {
				m.postRotate(180);
				return Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
						bm.getHeight(), m, true);

			} else if (orientation == 6) {
				m.postRotate(90);
				return Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
						bm.getHeight(), m, true);

			} else if (orientation == 8) {
				m.postRotate(270);
				return Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
						bm.getHeight(), m, true);

			}
			return bm;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public  String uploadFile(String fileNameInServer, String urlServer, Bitmap bitmap) {
		try {

			HttpURLConnection connection = null;
			DataOutputStream outputStream = null;
			String lineEnd = "\r\n";
			String twoHyphens = "--";
			String boundary = "*****";

			URL url = new URL(urlServer);
			connection = (HttpURLConnection) url.openConnection();

			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);

			connection.setRequestMethod("POST");

			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);

			outputStream = new DataOutputStream(connection.getOutputStream());
			outputStream.writeBytes(twoHyphens + boundary + lineEnd);
			outputStream
					.writeBytes("Content-Disposition: form-data; name=\"userfile\";filename=\""
							+ fileNameInServer + "\"" + lineEnd);
			outputStream.writeBytes(lineEnd);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			byte[] data = baos.toByteArray();

			outputStream.write(data);

			outputStream.writeBytes(lineEnd);
			outputStream.writeBytes(twoHyphens + boundary + twoHyphens
					+ lineEnd);

			StringBuilder sb = new StringBuilder();
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			String line;
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}

			outputStream.flush();
			outputStream.close();

			return sb.toString();

		} catch (Exception e) {
			return null;
		}
	}

	public String getImageFilePath(Uri originalUri, Activity activity) {
		String selectedImagePath = null;
		String[] projection = { MediaStore.Images.ImageColumns.DATA };
		Cursor cursor = activity.managedQuery(originalUri, projection, null,
				null, null);
		if (cursor != null) {
			int index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();

			selectedImagePath = cursor.getString(index);
			if (selectedImagePath == null) {

				String id = originalUri.getLastPathSegment().split(":")[1];
				final String[] imageColumns = { MediaStore.Images.Media.DATA };
				final String imageOrderBy = null;

				Uri uri = getUri();

				Cursor imageCursor = activity.managedQuery(uri, imageColumns,
						MediaStore.Images.Media._ID + "=" + id, null,
						imageOrderBy);

				if (imageCursor.moveToFirst()) {
					selectedImagePath = imageCursor.getString(imageCursor
							.getColumnIndex(MediaStore.Images.Media.DATA));
				}
			}
		}else {
			return  new File(originalUri.getPath()).getAbsolutePath();
		}
		return selectedImagePath;
	}

	private Uri getUri() {
		String state = Environment.getExternalStorageState();
		if (!state.equalsIgnoreCase(Environment.MEDIA_MOUNTED))
			return MediaStore.Images.Media.INTERNAL_CONTENT_URI;

		return MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
	}

	public String getRandomFileName() {
		String _df = android.text.format.DateFormat.format("MMddyyyyhhmmss",
				new java.util.Date()).toString();
		Random r = new Random();
		int random = Math.abs(r.nextInt() % 100);
		return String.format("%d%s.jpg", random, _df);
	}
}