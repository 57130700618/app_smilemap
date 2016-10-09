
package com.blackcatwalk.sharingpower.utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.widget.Button;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Control extends AppCompatActivity {

    public void closeApp(Context _context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(_context);
        builder.setMessage("ปิดปรับปรุงระบบ 30นาที เพื่ออัพเดทเวอร์ชั่นใหม่");
        builder.setCancelable(false);
        builder.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
                System.exit(0);
                dialog.cancel();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(Color.parseColor("#147cce"));
        pbutton.setTypeface(null, Typeface.BOLD);
    }


    //---------------------- md5 ----------------------//

    public String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    //---------------------- get Version App ----------------------//

    public String getVersionApp(Activity activity) {
        PackageManager manager = activity.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(
                    activity.getPackageName(), 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    //---------------------- goto googleplay ----------------------//

    public void openGooglePlay(Activity activity, String choice) {

        String appPackageName = null;
        if (choice.equals("ads")) {
            appPackageName = activity.getPackageName(); // getPackageName() from Context or Activity object
        } else {
            appPackageName = "com.blackcatwalk.smilemap_noads";
        }
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    //---------------------- get size screen ----------------------//

    public double getSizeScrren(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float widthInInches = metrics.widthPixels / metrics.xdpi;
        float heightInInches = metrics.heightPixels / metrics.ydpi;

        double sizeInInches = Math.sqrt(Math.pow(widthInInches, 2) + Math.pow(heightInInches, 2));

        return sizeInInches;
    }


    //---------------------- Clear cache ----------------------//

    public void clearCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {
        }
    }

    public boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

}



/* hashkey facebook
try {
        PackageInfo info = getPackageManager().getPackageInfo(
        "num.app.fblogin", PackageManager.GET_SIGNATURES);

        for (Signature signature : info.signatures) {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA");
        messageDigest.update(signature.toByteArray());
        Log.d("KeyHash: ", Base64.encodeToString(messageDigest.digest(),
        Base64.DEFAULT));
        }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

*/

/* open dialog intent extanal

    String message = "Text I want to share.";
    Intent share = new Intent(Intent.ACTION_SEND);
share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, message);

        startActivity(Intent.createChooser(share, "Title of the dialog the system will open"));
*/




/*

    //---------------------- Ads mob ----------------------//

    public static AdRequest getAds(){

        AdRequest.Builder adBuilder = new AdRequest.Builder();
        adBuilder.setLocation(Control.getLocation());
        adBuilder.tagForChildDirectedTreatment(true);
        adBuilder.addTestDevice("91D46D373E255D331922C60807452C8A");

        AdRequest adRequest = adBuilder.build();

        return adRequest;
    }




    //---------------------- Resize Camera ----------------------//

    public Bitmap decodeUri(Context c, Uri uri, final int requiredSize)
            throws FileNotFoundException {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri), null, o);

        int width_tmp = o.outWidth
                , height_tmp = o.outHeight;
        int scale = 1;

        while(true) {
            if(width_tmp / 2 < requiredSize || height_tmp / 2 < requiredSize)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri), null, o2);
    }




    //---------------------- key hash ----------------------//

    public  void key(Context context) {
  /*      try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    "com.blackcatwalk.sharingpower", PackageManager.GET_SIGNATURES);

            for (Signature signature : info.signatures) {
                MessageDigest messageDigest = MessageDigest.getInstance("SHA");
                messageDigest.update(signature.toByteArray());
                Log.d("KeyHash: ", Base64.encodeToString(messageDigest.digest(),
                        Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

try {
        PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
        for (Signature signature : info.signatures) {
        MessageDigest md = MessageDigest.getInstance("SHA");
        md.update(signature.toByteArray());
        String sign = Base64.encodeToString(md.digest(), Base64.DEFAULT);
        Log.d("KEY_HASH:", sign);
        }
        } catch (PackageManager.NameNotFoundException e) {
        Log.d("KEY_HASH:", "nope");
        } catch (NoSuchAlgorithmException e) {
        }
        }


//---------------------- get Location ----------------------//

public static Location getLocation() {

        Location location = new Location("AdMobProvider");
        location.setLatitude(13.543296);
        location.setLatitude(100.924562);

        return location;
        }

*/
