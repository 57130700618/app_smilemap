package com.blackcatwalk.sharingpower.camera;


import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


public class BitmapHelper {

    public byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public Bitmap byteArrayToBitmap(byte[] byteArray) {
        if (byteArray == null) {
            return null;
        } else {
            return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        }
    }

    public static Bitmap shrinkBitmap(Bitmap bm, int maxLengthOfEdge) {
        return shrinkBitmap(bm, maxLengthOfEdge, 0);
    }

    public static Bitmap shrinkBitmap(Bitmap bm, int maxLengthOfEdge, int rotateXDegree) {
        if (maxLengthOfEdge > bm.getWidth() && maxLengthOfEdge > bm.getHeight()) {
            return bm;
        } else {
            float scale = (float) 1.0;
            if (bm.getHeight() > bm.getWidth()) {
                scale = ((float) maxLengthOfEdge) / bm.getHeight();
            } else {
                scale = ((float) maxLengthOfEdge) / bm.getWidth();
            }

            Matrix matrix = new Matrix();

            matrix.postScale(scale, scale);
            matrix.postRotate(rotateXDegree);

            bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(),
                    matrix, false);

            matrix = null;
            System.gc();

            return bm;
        }
    }

    public Bitmap readBitmap(Context context, Uri selectedImage) {
        Bitmap bm = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inScaled = false;
        AssetFileDescriptor fileDescriptor = null;
        try {
            fileDescriptor = context.getContentResolver().openAssetFileDescriptor(selectedImage, "r");
        } catch (FileNotFoundException e) {
            return null;
        } finally {
            try {
                bm = BitmapFactory.decodeFileDescriptor(
                        fileDescriptor.getFileDescriptor(), null, options);
                fileDescriptor.close();
            } catch (IOException e) {
                return null;
            }
        }
        return bm;
    }

    public void clearBitmap(Bitmap bm) {
        bm.recycle();
        System.gc();
    }	

	public boolean deleteImageWithUriIfExists(Uri cameraPicUri, Context context) {
		try {
			if (cameraPicUri != null) {
				File fdelete = new File(cameraPicUri.getPath());
		        if (fdelete.exists()) {
		            if (fdelete.delete()) {
	            		refreshGalleryImages(context, fdelete);
		            	return true;
		            }
		        }	
			}
		} catch (Exception e) {
		}
		return false;
	}

	private void refreshGalleryImages(Context context, File fdelete) {
		try {
    		context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" +  Environment.getExternalStorageDirectory())));
    	} catch (Exception e1) {
    		try {
        		Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        	    Uri contentUri = Uri.fromFile(fdelete);
        	    mediaScanIntent.setData(contentUri);
        	    context.sendBroadcast(mediaScanIntent);
    		} catch (Exception e2) {
    		}
    	}		
	}
}