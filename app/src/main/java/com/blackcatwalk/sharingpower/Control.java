
package com.blackcatwalk.sharingpower;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class Control extends AppCompatActivity {

    public static ProgressDialog pDialog;
    public static Dialog dialog;
    public static Dialog dialogGps;

    private static final String mSetDatabase = "https://www.smilemap.me/android/set.php";
    private static final String mGetDatabase = "https://www.smilemap.me/android/get.php?main=";
    private static final String mGetDatabaseResetPassword = "https://www.smilemap.me/android/resetpassword.php";

    public static String getMSetDatabase(){
        return mSetDatabase;
    }

    public static String getMGetDatabase(){
        return mGetDatabase;
    }

    public static String getMGetDatabaseResetPassword(){
        return mGetDatabaseResetPassword;
    }

    //---------------------- ProgressDialog ----------------------//

    public static void sDialog(Context context) {
        pDialog = new ProgressDialog(context,R.style.MyTheme);
        pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        pDialog.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.aaaaaaaaaaaaaaa));
        pDialog.show();
    }

    public static void hDialog() {

        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    //---------------------- randomNumber ----------------------//

    public static int randomNumber() {
        Random rand = new Random();
        return rand.nextInt(5000) + 1;
    }


    //---------------------- md5 ----------------------//

    public static String md5(final String s) {
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


    //---------------------- Check gps ----------------------//

    public static boolean checkGPS(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            return false;
        }
        return true;
    }

    public static void alertGps(final Context context) {

        if (dialogGps == null) {
            dialogGps = new Dialog(context);
            dialogGps.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogGps.setContentView(R.layout.activity_dialog_checkconnect);
            dialogGps.setCancelable(false);

            ImageView label = (ImageView) dialogGps.findViewById(R.id.label);
            label.setImageResource(R.drawable.label_gps);

            Button button1 = (Button) dialogGps.findViewById(R.id.btnTry);
            button1.setText(R.string.ok_bold);
            button1.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(intent);
                    dialogGps.cancel();
                    dialogGps = null;
                }
            });

            TextView textView = (TextView) dialogGps.findViewById(R.id.textView);
            textView.setText("เปิดใช้งาน จีพีเอส");
            TextView txtTitle = (TextView) dialogGps.findViewById(R.id.txtTitle);
            txtTitle.setText(R.string.title_open_gps);
            TextView txtDetail = (TextView) dialogGps.findViewById(R.id.txtDetail);
            txtDetail.setText(R.string.detail_open_gps);
            TextView txtDetailSetting = (TextView) dialogGps.findViewById(R.id.txtDetailSetting);
            txtDetailSetting.setVisibility(View.VISIBLE);

            dialogGps.show();
        }
    }

    public static void alertCurrentGps(Context context) {

        if (dialog == null) {
            dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.activity_dialog_current_gps);
            dialog.setCancelable(false);

            Button btnClose = (Button) dialog.findViewById(R.id.btnClose);
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                    dialog = null;
                }
            });
            dialog.show();
        }
    }

    //---------------------- Check internet ----------------------//

    public static boolean checkInternet(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        boolean is3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting(); //For 3G check
        boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting(); //For WiFi Check
        if (!is3g && !isWifi) {
            return false;
        }
        return true;
    }

    public static void alertInternet(final Context context) {

        if (dialog == null) {
            dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.activity_dialog_checkconnect);
            dialog.setCancelable(false);

            ImageView label = (ImageView) dialog.findViewById(R.id.label);
            label.setImageResource(R.drawable.label_internet);

            Button button1 = (Button) dialog.findViewById(R.id.btnTry);
            button1.setText(R.string.try_again);
            button1.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (checkInternet(context)) {
                        dialog.cancel();
                        dialog = null;
                    }
                }
            });
            TextView textView = (TextView) dialog.findViewById(R.id.textView);
            textView.setText("เปิดใช้งาน อินเทอร์เน็ต");
            TextView txtTitle = (TextView) dialog.findViewById(R.id.txtTitle);
            txtTitle.setText(R.string.title_open_network);
            TextView txtDetail = (TextView) dialog.findViewById(R.id.txtDetail);
            txtDetail.setText(R.string.detail_open_network);

            dialog.show();
        }
    }

    public static void alertCurrentInternet(Context context) {
        if (dialog == null) {
            dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.activity_dialog_current_internet);
            dialog.setCancelable(false);

            Button btnClose = (Button) dialog.findViewById(R.id.btnClose);
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                    dialog = null;
                }
            });
            dialog.show();
        }
    }


    //---------------------- Resize Camera ----------------------//

    public static Bitmap decodeUri(Context c, Uri uri, final int requiredSize)
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

    //----------------------  Read File ----------------------//

    public static String getUsername(Context context) {
        try {
            FileInputStream fIn = context.openFileInput("Username.txt");
            InputStreamReader reader = new InputStreamReader(fIn);

            char[] buffer = new char[100];
            String username = "";
            int charReadCount;
            while ((charReadCount = reader.read(buffer)) > 0) {
                String readString = String.copyValueOf(buffer, 0, charReadCount);

                username += readString;
                buffer = new char[100];
            }

            reader.close();
            return username;
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return "";
    }

    public static String getStausLogin(Context _context) {
        try {
            FileInputStream _fIn = _context.openFileInput("Stauslogin.txt");
            InputStreamReader reader = new InputStreamReader(_fIn);
            char[] _buffer = new char[100];
            String _stauslogin = "";
            int _charReadCount;

            while ((_charReadCount = reader.read(_buffer)) > 0) {
                String readString = String.copyValueOf(_buffer, 0, _charReadCount);
                _stauslogin += readString;
                _buffer = new char[100];
            }
            reader.close();
            return _stauslogin;   // 0 = not login, 1 = login
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return "";
    }

    //----------------------  Write File ----------------------//

    public static void setUserNameToFile(Context _context,String _username) {
        try {
            FileOutputStream _fOut = _context.openFileOutput("Username.txt", MODE_PRIVATE);
            OutputStreamWriter _writer = new OutputStreamWriter(_fOut);
            _writer.write(_username);
            _writer.flush();
            _writer.close();

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }


    public static void setStausLoginToFile(Context _context,String _stauslogin) {
        try {
            FileOutputStream _fOut = _context.openFileOutput("Stauslogin.txt", MODE_PRIVATE);
            OutputStreamWriter _writer = new OutputStreamWriter(_fOut);
            _writer.write(_stauslogin);
            _writer.flush();
            _writer.close();

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }






    //----------------------  map ----------------------//

    public static String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }


    public static String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            // Log.bus_spinner_bts_gray("Exception while downloading url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    //---------------------- HIDDEN Keyboard ----------------------//

    public static void hideKeyboard(Activity _activity){
        if(_activity.getCurrentFocus()!=null) {
            InputMethodManager _inputMethodManager = (InputMethodManager) _activity.getSystemService(INPUT_METHOD_SERVICE);
           // _inputMethodManager.hideSoftInputFromWindow(_activity.getCurrentFocus().getWindowToken(), 0);
            _inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);
        }
    }

    public static void hideKeyboardEditext(Activity _activity){
        _activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


    //---------------------- Show Keyboard Edittext ----------------------//

    public static void showKeyboard(Activity _activity){
        InputMethodManager _inputMethodManager = (InputMethodManager) _activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        _inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    //---------------------- get Version App ----------------------//

    public static String getVersionApp(Activity activity){
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

    public static void openGooglePlay(Activity activity,String choice){

        String appPackageName = null;
        if(choice.equals("ads")){
            appPackageName = activity.getPackageName(); // getPackageName() from Context or Activity object
        }else{
            appPackageName = "com.blackcatwalk.smilemap_noads";
        }
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    //---------------------- key hash ----------------------//

    public static void key(Context context){
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
*/
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

    //---------------------- get size screen ----------------------//

    public static double getSizeScrren(Activity activity){
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float widthInInches = metrics.widthPixels / metrics.xdpi;
        float heightInInches = metrics.heightPixels / metrics.ydpi;

        double sizeInInches = Math.sqrt(Math.pow(widthInInches, 2) + Math.pow(heightInInches, 2));

        return sizeInInches;
    }

    //---------------------- get Location ----------------------//

    public static Location getLocation(){

        Location location = new Location("AdMobProvider");
        location.setLatitude(13.543296);
        location.setLatitude(100.924562);

        return location;
    }

    //---------------------- Clear cache ----------------------//

    public static void clearCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {
        }
    }

    public static boolean deleteDir(File dir) {
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

    //---------------------- Toast Custom ----------------------//

    public static void showToast(Activity _activity,String _temp){
        LayoutInflater inflater = _activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast,
                (ViewGroup) _activity.findViewById(R.id.toast_layout_root));

        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(_temp);

        Toast toast = new Toast(_activity.getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 500);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    //---------------------- Check Email ----------------------//

    public static boolean isValidEmail(String target) {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches();
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
*/
