package com.blackcatwalk.sharingpower;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.blackcatwalk.sharingpower.customAdapter.CustomSpinnerBusLocattionNerbyMenu;
import com.blackcatwalk.sharingpower.customAdapter.NearbyCustomSpinnerAdapter;
import com.blackcatwalk.sharingpower.google.GoogleNavigator;
import com.blackcatwalk.sharingpower.google.JsonParserDirections;
import com.blackcatwalk.sharingpower.google.PlaceJSONParser;
import com.blackcatwalk.sharingpower.utility.ControlCheckConnect;
import com.blackcatwalk.sharingpower.utility.ControlProgress;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.blackcatwalk.sharingpower.R.id.map;


public class DetailMap extends AppCompatActivity {


    private float currentZoom = 30;
    private ControlCheckConnect mControlCheckConnect;

    // ----------------- Google_Map -------------------//
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private GoogleMap mMap;
    private HashMap<String, String> mMarkerPlaceLink = new HashMap<String, String>();
    private double Latitude = 0;
    private double Longitude = 0;
    private Marker mSelectedMaker;
    private Marker makerStreet;
    private String distance;
    private String duration;
    private String markerName = null;
    private int mSelectType;

    // ----------------- Data for get -------------------//
    private String locationPutExtra;
    private int typeMoveMap;
    private String typeFavorite;
    private String detailFavorite;
    private String type = "";
    private String TimeFavorite;

    // ----------------- User Interface -------------------//
    private Spinner mSprPlaceType;
    private TextView mLabelTv;
    private TextView mTxtDetailTv;
    private ImageView mCureentLocationBtn;
    private CircleImageView mNavigationBtn;
    private SupportMapFragment mapFragment;
    private ImageView mBackIm;
    private ImageView mMainIm;
    private RelativeLayout smallGoogleStreetRy;
    private SupportStreetViewPanoramaFragment streetViewFracment;
    private LinearLayout mLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_map);
        getSupportActionBar().hide();

        bindWidget();

        getBundle();

        mControlCheckConnect = new ControlCheckConnect();

        mBackIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                setupMap();
            }
        });

        mMainIm.setVisibility(View.VISIBLE);
        mMainIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!mControlCheckConnect.checkInternet(this)) {
            mControlCheckConnect.alertInternet(this);
        }

        if (!mControlCheckConnect.checkGPS(this)) {
            mControlCheckConnect.alertGps(this);
        }
    }

    private void getBundle() {
        Bundle bundle = getIntent().getExtras();
        locationPutExtra = bundle.getString("location");
        typeMoveMap = bundle.getInt("typeMoveMap");
        Latitude = bundle.getDouble("latFavorite");
        Longitude = bundle.getDouble("lngFavorite");
        typeFavorite = bundle.getString("typeFavorite");
        detailFavorite = bundle.getString("detailFavorite");
        TimeFavorite = bundle.getString("timeFavorite");
    }

    private void setMarkerCustom() {
        mTxtDetailTv.setText("คลิกค้างที่แผนที่ เพื่อปักหมุด");
        markerName = "ตำแหน่งปัจจุบัน";
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                smallGoogleStreetRy.setVisibility(View.GONE);
                mTxtDetailTv.setText("ค้นหาสถานที่บริเวณรอบๆ 1กิโลเมตร");
                ControlProgress.showProgressDialog(DetailMap.this);
                mMap.clear();
                Latitude = latLng.latitude;
                Longitude = latLng.longitude;
                addMarkerStart();
            }
        });
    }

    @SuppressWarnings({"MissingPermission"})
    private void setMarkerCurrent() {
        ControlProgress.showProgressDialog(this);
        markerName = "ตำแหน่งปัจจุบัน";
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        mLastLocation = LocationServices.FusedLocationApi
                                .getLastLocation(mGoogleApiClient);

                        if (mLastLocation != null) {
                            Latitude = mLastLocation.getLatitude();
                            Longitude = mLastLocation.getLongitude();
                            addMarkerStart();
                        } else {
                            mControlCheckConnect.alertCurrentGps(DetailMap.this);
                        }
                        ControlProgress.hideDialog();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        mGoogleApiClient.connect();
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        mControlCheckConnect.alertCurrentGps(DetailMap.this);
                    }
                })
                .build();

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    private void setMarkerArl(String _location) {
        String _tempMarkerName = "ARL ";
        switch (_location) {
            case "พญาไท":
                markerName = _tempMarkerName + "พญาไท";
                Latitude = 13.75671;
                Longitude = 100.53497;
                break;
            case "ราชปรารภ":
                markerName = _tempMarkerName + "ราชปรารภ";
                Latitude = 13.75510;
                Longitude = 100.54179;
                break;
            case "มักกะสัน":
                markerName = _tempMarkerName + "มักกะสัน";
                Latitude = 13.75106;
                Longitude = 100.56117;
                break;
            case "รามคำแหง":
                markerName = _tempMarkerName + "รามคำแหง";
                Latitude = 13.74297;
                Longitude = 100.60029;
                break;
            case "หัวหมาก":
                markerName = _tempMarkerName + "หัวหมาก";
                Latitude = 13.73801;
                Longitude = 100.64539;
                break;
            case "บ้านทับช้าง":
                markerName = _tempMarkerName + "บ้านทับช้าง";
                Latitude = 13.73280;
                Longitude = 100.69144;
                break;
            case "ลาดกระบัง":
                markerName = _tempMarkerName + "ลาดกระบัง";
                Latitude = 13.72782;
                Longitude = 100.74871;
                break;
            case "สุวรรณภูมิ":
                markerName = _tempMarkerName + "สุวรรณภูมิ";
                Latitude = 13.69805;
                Longitude = 100.75225;
                break;
        }
    }

    private void setMarkerTemple(String _location) {
        switch (_location) {
            case "วัดศาลหลักเมือง กรุงเทพมหานคร":
                markerName = "วัดศาลหลักเมือง กรุงเทพมหานคร";
                Latitude = 13.75285;
                Longitude = 100.49401;
                break;
            case "วัดพระศรีรัตนศาสดาราม(วัดพระแก้ว)":
                markerName = "วัดพระศรีรัตนศาสดาราม(วัดพระแก้ว)";
                Latitude = 13.75143;
                Longitude = 100.49250;
                break;
            case "วัดพระเชตุพนวิมลมังคลาราม(วัดโพธิ์)":
                markerName = "วัดพระเชตุพนวิมลมังคลาราม(วัดโพธิ์)";
                Latitude = 13.74664;
                Longitude = 100.49293;
                break;
            case "วัดสุทัศน์เทพวราราม":
                markerName = "วัดสุทัศน์เทพวราราม";
                Latitude = 13.75119;
                Longitude = 100.50108;
                break;
            case "วัดชนะสงครามราชวรมหาวิหาร":
                markerName = "วัดชนะสงครามราชวรมหาวิหาร";
                Latitude = 13.76005;
                Longitude = 100.49570;
                break;
            case "วัดระฆังโฆสิตารามวรมหาวิหาร":
                markerName = "วัดระฆังโฆสิตารามวรมหาวิหาร";
                Latitude = 13.75275;
                Longitude = 100.48545;
                break;
            case "วัดไตรมิตร(หลวงพ่อทองคำ)":
                markerName = "วัดไตรมิตร(หลวงพ่อทองคำ)";
                Latitude = 13.73807;
                Longitude = 100.51379;
                break;
            case "วัดอรุณราชวรารามวรมหาวิหาร(วัดแจ้ง)":
                markerName = "วัดอรุณราชวรารามวรมหาวิหาร(วัดแจ้ง)";
                Latitude = 13.74379;
                Longitude = 100.48896;
                break;
            case "วัดกัลยาณมิตรวรมหาวิหาร(วัดซำปอกง)":
                markerName = "วัดกัลยาณมิตรวรมหาวิหาร(วัดซำปอกง)";
                Latitude = 13.73984;
                Longitude = 100.49124;
                break;
            case "วัดบวรนิเวศราชวรวิหาร":
                markerName = "วัดบวรนิเวศราชวรวิหาร";
                Latitude = 13.76035;
                Longitude = 100.49996;
                break;
            case "วัดสระเกศราชวรมหาวิหาร(ภูเขาทอง)":
                markerName = "วัดสระเกศราชวรมหาวิหาร(ภูเขาทอง)";
                Latitude = 13.75384;
                Longitude = 100.50663;
                break;
            case "วัดอมรินทรารามวรวิหาร":
                markerName = "วัดอมรินทรารามวรวิหาร";
                Latitude = 13.75921;
                Longitude = 100.48351;
                break;
            case "วัดสุวรรณารามราชวรวิหาร":
                markerName = "วัดสุวรรณารามราชวรวิหาร";
                Latitude = 13.76329;
                Longitude = 100.47691;
                break;
            case "วัดคฤหบดี":
                markerName = "วัดคฤหบดี";
                Latitude = 13.77253;
                Longitude = 100.49558;
                break;
            case "วัดราชาธิวาสราชวรวิหาร":
                markerName = "วัดราชาธิวาสราชวรวิหาร";
                Latitude = 13.77600;
                Longitude = 100.50359;
                break;
            case "วัดเทวราชกุญชรวรวิหาร":
                markerName = "วัดเทวราชกุญชรวรวิหาร";
                Latitude = 13.77215;
                Longitude = 100.50278;
                break;
            case "วัดยานนาวา":
                markerName = "วัดยานนาวา";
                Latitude = 13.71730;
                Longitude = 100.51352;
                break;
            case "วัดศาลเจ้าพ่อเสือ":
                markerName = "วัดศาลเจ้าพ่อเสือ";
                Latitude = 13.75384;
                Longitude = 100.49820;
                break;
        }
    }

    private void setMarkerMrt(String _location) {
        String _tempMarkerName = "MRT ";
        switch (_location) {
            case "บางซื่อ":
                markerName = _tempMarkerName + "บางซื่อ";
                Latitude = 13.80340;
                Longitude = 100.53933;
                break;
            case "กำแพงเพชร":
                markerName = _tempMarkerName + "กำแพงเพชร";
                Latitude = 13.79782;
                Longitude = 100.54760;
                break;
            case "สวนจตุจักร":
                markerName = _tempMarkerName + "สวนจตุจักร";
                Latitude = 13.80387;
                Longitude = 100.55406;
                break;
            case "พหลโยธิน":
                markerName = _tempMarkerName + "พหลโยธิน";
                Latitude = 13.81425;
                Longitude = 100.56017;
                break;
            case "ลาดพร้าว":
                markerName = _tempMarkerName + "ลาดพร้าว";
                Latitude = 13.80620;
                Longitude = 100.57341;
                break;
            case "รัชดาภิเษก":
                markerName = _tempMarkerName + "รัชดาภิเษก";
                Latitude = 13.79918;
                Longitude = 100.57443;
                break;
            case "สุทธิสาร":
                markerName = _tempMarkerName + "สุทธิสาร";
                Latitude = 13.78901;
                Longitude = 100.57441;
                break;
            case "ห้วยขวาง":
                markerName = _tempMarkerName + "ห้วยขวาง";
                Latitude = 13.77904;
                Longitude = 100.57395;
                break;
            case "ศูนย์วัฒนธรรม":
                markerName = _tempMarkerName + "ศูนย์วัฒนธรรม";
                Latitude = 13.76726;
                Longitude = 100.57139;
                break;
            case "พระราม 9":
                markerName = _tempMarkerName + "พระราม 9";
                Latitude = 13.75777;
                Longitude = 100.56519;
                break;
            case "เพชรบุรี":
                markerName = _tempMarkerName + "เพชรบุรี";
                Latitude = 13.74858;
                Longitude = 100.56311;
                break;
            case "สุขุมวิท":
                markerName = _tempMarkerName + "สุขุมวิท";
                Latitude = 13.73853;
                Longitude = 100.56145;
                break;
            case "ศูนย์ประชุมแห่งชาติสิริกิติ์":
                markerName = _tempMarkerName + "ศูนย์ประชุมแห่งชาติสิริกิติ์";
                Latitude = 13.72378;
                Longitude = 100.56006;
                break;
            case "คลองเตย":
                markerName = _tempMarkerName + "คลองเตย";
                Latitude = 13.72261;
                Longitude = 100.55380;
                break;
            case "ลุมพินี":
                markerName = _tempMarkerName + "ลุมพินี";
                Latitude = 13.72642;
                Longitude = 100.54505;
                break;
            case "สีลม":
                markerName = _tempMarkerName + "สีลม";
                Latitude = 13.72923;
                Longitude = 100.53642;
                break;
            case "สามย่าน":
                markerName = _tempMarkerName + "สามย่าน";
                Latitude = 13.73284;
                Longitude = 100.52945;
                break;
            case "หัวลำโพง":
                markerName = _tempMarkerName + "หัวลำโพง";
                Latitude = 13.73792;
                Longitude = 100.51694;
                break;
        }
    }

    private void setMarkerBoat(String _location) {
        String _tempMarkerName = "ท่าเรือ ";
        switch (_location) {
            case "ท่าเรือวัดศรีบุญเรือง":
                markerName = _tempMarkerName + "ท่าเรือวัดศรีบุญเรือง";
                Latitude = 13.76721;
                Longitude = 100.65156;
                break;
            case "ท่าเรือบางกะปิ":
                markerName = _tempMarkerName + "ท่าเรือบางกะปิ";
                Latitude = 13.76506;
                Longitude = 100.64736;
                break;
            case "ท่าเรือเดอะมอลล์บางกะปิ":
                markerName = _tempMarkerName + "ท่าเรือเดอะมอลล์บางกะปิ";
                Latitude = 13.76465;
                Longitude = 100.64310;
                break;
            case "ท่าเรือวัดกลาง":
                markerName = _tempMarkerName + "ท่าเรือวัดกลาง";
                Latitude = 13.76349;
                Longitude = 100.63349;
                break;
            case "สะพานมิตรมหาดไทย":
                markerName = _tempMarkerName + "สะพานมิตรมหาดไทย";
                Latitude = 13.76210;
                Longitude = 100.62388;
                break;
            case "ม.รามคำแหง":
                markerName = _tempMarkerName + "ม.รามคำแหง";
                Latitude = 13.76151;
                Longitude = 100.61835;
                break;
            case "ท่าเรือวัดเทพลีลา":
                markerName = _tempMarkerName + "ท่าเรือวัดเทพลีลา";
                Latitude = 13.75990;
                Longitude = 100.61379;
                break;
            case "รามคำแหง 29":
                markerName = _tempMarkerName + "รามคำแหง 29";
                Latitude = 13.75799;
                Longitude = 100.61186;
                break;
            case "เดอะมอลล์ 3":
                markerName = _tempMarkerName + "เดอะมอลล์ 3";
                Latitude = 13.75433;
                Longitude = 100.60784;
                break;
            case "ท่าเรือสะพานคลองตัน":
                markerName = _tempMarkerName + "ท่าเรือสะพานคลองตัน";
                Latitude = 13.74197;
                Longitude = 100.59825;
                break;
            case "ท่าเรือชาญอิสระ":
                markerName = _tempMarkerName + "ท่าเรือชาญอิสระ";
                Latitude = 13.74274;
                Longitude = 100.58849;
                break;
            case "ทองหล่อ,ท่าเรือ":
                markerName = _tempMarkerName + "ทองหล่อ,ท่าเรือ";
                Latitude = 13.74323;
                Longitude = 100.58587;
                break;
            case "สุเหร่าบ้านดอน":
                markerName = _tempMarkerName + "สุเหร่าบ้านดอน";
                Latitude = 13.74412;
                Longitude = 100.58145;
                break;
            case "วัดใหม่ช่องลม":
                markerName = _tempMarkerName + "วัดใหม่ช่องลม";
                Latitude = 13.74580;
                Longitude = 100.57432;
                break;
            case "อิตัลไทยทาวเวอร์":
                markerName = _tempMarkerName + "อิตัลไทยทาวเวอร์";
                Latitude = 13.74597;
                Longitude = 100.57351;
                break;
            case "ท่าเรือมศว ประสานมิตร":
                markerName = _tempMarkerName + "ท่าเรือมศว ประสานมิตร";
                Latitude = 13.74729;
                Longitude = 100.56580;
                break;
            case "สะพานอโศก":
                markerName = _tempMarkerName + "สะพานอโศก";
                Latitude = 13.74792;
                Longitude = 100.56294;
                break;
            case "นานาเหนือ":
                markerName = _tempMarkerName + "นานาเหนือ";
                Latitude = 13.74829;
                Longitude = 100.55400;
                break;
            case "สะพานวิทยุ":
                markerName = _tempMarkerName + "สะพานวิทยุ";
                Latitude = 13.74822;
                Longitude = 100.54823;
                break;
            case "สะพานชิดลม":
                markerName = _tempMarkerName + "สะพานชิดลม";
                Latitude = 13.74872;
                Longitude = 100.54455;
                break;
            case "ท่าเรือประตูน้ำ":
                markerName = _tempMarkerName + "ท่าเรือประตูน้ำ";
                Latitude = 13.74920;
                Longitude = 100.54153;
                break;
            case "หัวช้าง":
                markerName = _tempMarkerName + "หัวช้าง";
                Latitude = 13.74911;
                Longitude = 100.53088;
                break;
            case "ชุมชนบ้านครัวเหนือ":
                markerName = _tempMarkerName + "ชุมชนบ้านครัวเหนือ";
                Latitude = 13.74940;
                Longitude = 100.52484;
                break;
            case "สะพานเจริญผล":
                markerName = _tempMarkerName + "สะพานเจริญผล";
                Latitude = 13.74984;
                Longitude = 100.52442;
                break;
            case "ตลาดโบ้เบ้":
                markerName = _tempMarkerName + "ตลาดโบ้เบ้";
                Latitude = 13.75350;
                Longitude = 100.51591;
                break;
            case "ผ่านฟ้าลีลาส":
                markerName = _tempMarkerName + "ผ่านฟ้าลีลาส";
                Latitude = 13.75556;
                Longitude = 100.50639;
                break;
            case "สาทร(สะพานตากสิน, ท่าเรือกลาง)":
                markerName = _tempMarkerName + "สาทร(สะพานตากสิน, ท่าเรือกลาง)";
                Latitude = 13.71897;
                Longitude = 100.51273;
                break;
            case "โอเรียนเต็ล":
                markerName = _tempMarkerName + "โอเรียนเต็ล";
                Latitude = 13.72338;
                Longitude = 100.51360;
                break;
            case "สี่พระยา":
                markerName = _tempMarkerName + "สี่พระยา";
                Latitude = 13.72853;
                Longitude = 100.51327;
                break;
            case "ท่าเรือสวัสดี":
                markerName = _tempMarkerName + "ท่าเรือสวัสดี";
                Latitude = 13.73250;
                Longitude = 100.51202;
                break;
            case "ราชวงศ์":
                markerName = _tempMarkerName + "ราชวงศ์";
                Latitude = 13.73846;
                Longitude = 100.50452;
                break;
            case "สะพานพุทธ":
                markerName = _tempMarkerName + "สะพานพุทธ";
                Latitude = 13.73996;
                Longitude = 100.49820;
                break;
            case "ท่าเตียน":
                markerName = _tempMarkerName + "ท่าเตียน";
                Latitude = 13.74613;
                Longitude = 100.49013;
                break;
            case "ท่าช้าง":
                markerName = _tempMarkerName + "ท่าช้าง";
                Latitude = 13.75240;
                Longitude = 100.48829;
                break;
            case "วังหลัง(ศิริราช)":
                markerName = _tempMarkerName + "วังหลัง(ศิริราช)";
                Latitude = 13.75600;
                Longitude = 100.48680;
                break;
            case "พระอาทิตย์":
                markerName = _tempMarkerName + "พระอาทิตย์";
                Latitude = 13.76364;
                Longitude = 100.49413;
                break;
            case "เทเวศน์":
                markerName = _tempMarkerName + "เทเวศน์";
                Latitude = 13.77196;
                Longitude = 100.50022;
                break;
            case "สะพานกรุงธน":
                markerName = _tempMarkerName + "สะพานกรุงธน";
                Latitude = 13.78188;
                Longitude = 100.50109;
                break;
            case "พายัพ":
                markerName = _tempMarkerName + "พายัพ";
                Latitude = 13.78745;
                Longitude = 100.50827;
                break;
            case "เกียกกาย":
                markerName = _tempMarkerName + "เกียกกาย";
                Latitude = 13.79871;
                Longitude = 100.517253;
                break;
            case "บางโพ":
                markerName = _tempMarkerName + "บางโพ";
                Latitude = 13.80644;
                Longitude = 100.51892;
                break;
            case "สะพานพระราม7":
                markerName = _tempMarkerName + "สะพานพระราม7";
                Latitude = 13.81294;
                Longitude = 100.51336;
                break;
        }
    }

    private void setMarkerBts(String _location) {
        String _tempMarkerName = "BTS ";
        switch (_location) {
            case "หมอชิต":
                markerName = _tempMarkerName + "หมอชิต";
                Latitude = 13.80236;
                Longitude = 100.55396;
                break;
            case "สะพานควาย":
                markerName = _tempMarkerName + "สะพานควาย";
                Latitude = 13.79358;
                Longitude = 100.54995;
                break;
            case "อารีย์":
                markerName = _tempMarkerName + "อารีย์";
                Latitude = 13.77967;
                Longitude = 100.54495;
                break;
            case "สนามเป้า":
                markerName = _tempMarkerName + "สนามเป้า";
                Latitude = 13.77220;
                Longitude = 100.54230;
                break;
            case "อนุสาวรีย์ชัยสมรภูมิ":
                markerName = _tempMarkerName + "อนุสาวรีย์ชัยสมรภูมิ";
                Latitude = 13.76265;
                Longitude = 100.53722;
                break;
            case "พญาไท":
                markerName = _tempMarkerName + "พญาไท";
                Latitude = 13.75669;
                Longitude = 100.53401;
                break;
            case "ราชเทวี":
                markerName = _tempMarkerName + "ราชเทวี";
                Latitude = 13.75151;
                Longitude = 100.53175;
                break;
            case "สยาม":
                markerName = _tempMarkerName + "สยาม";
                Latitude = 13.74573;
                Longitude = 100.53452;
                break;
            case "ชิดลม":
                markerName = _tempMarkerName + "ชิดลม";
                Latitude = 13.74429;
                Longitude = 100.54303;
                break;
            case "เพลินจิต":
                markerName = _tempMarkerName + "เพลินจิต";
                Latitude = 13.74323;
                Longitude = 100.54889;
                break;
            case "นานา":
                markerName = _tempMarkerName + "นานา";
                Latitude = 13.74066;
                Longitude = 100.55567;
                break;
            case "อโศก":
                markerName = _tempMarkerName + "อโศก";
                Latitude = 13.73715;
                Longitude = 100.56061;
                break;
            case "พร้อมพงษ์":
                markerName = _tempMarkerName + "พร้อมพงษ์";
                Latitude = 13.73053;
                Longitude = 100.56995;
                break;
            case "ทองหล่อ":
                markerName = _tempMarkerName + "ทองหล่อ";
                Latitude = 13.72435;
                Longitude = 100.57863;
                break;
            case "เอกมัย":
                markerName = _tempMarkerName + "เอกมัย";
                Latitude = 13.71959;
                Longitude = 100.58541;
                break;
            case "พระโขนง":
                markerName = _tempMarkerName + "พระโขนง";
                Latitude = 13.71535;
                Longitude = 100.59144;
                break;
            case "อ่อนนุช":
                markerName = _tempMarkerName + "อ่อนนุช";
                Latitude = 13.70557;
                Longitude = 100.60126;
                break;
            case "บางจาก":
                markerName = _tempMarkerName + "บางจาก";
                Latitude = 13.69665;
                Longitude = 100.60576;
                break;
            case "ปุณณวิถี":
                markerName = _tempMarkerName + "ปุณณวิถี";
                Latitude = 13.68925;
                Longitude = 100.60884;
                break;
            case "อุดมสุข":
                markerName = _tempMarkerName + "อุดมสุข";
                Latitude = 13.67973;
                Longitude = 100.60981;
                break;
            case "บางนา":
                markerName = _tempMarkerName + "บางนา";
                Latitude = 13.66819;
                Longitude = 100.60501;
                break;
            case "แบริ่ง":
                markerName = _tempMarkerName + "แบริ่ง";
                Latitude = 13.66109;
                Longitude = 100.60218;
                break;
            case "สนามกีฬาแห่งชาติ":
                markerName = _tempMarkerName + "สนามกีฬาแห่งชาติ";
                Latitude = 13.74629;
                Longitude = 100.52912;
                break;
            case "ราชดำริ":
                markerName = _tempMarkerName + "ราชดำริ";
                Latitude = 13.73919;
                Longitude = 100.53978;
                break;
            case "ศาลาแดง":
                markerName = _tempMarkerName + "ศาลาแดง";
                Latitude = 13.72839;
                Longitude = 100.53438;
                break;
            case "ช่องนนทรี":
                markerName = _tempMarkerName + "ช่องนนทรี";
                Latitude = 13.72386;
                Longitude = 100.529478;
                break;
            case "สุรศักดิ์":
                markerName = _tempMarkerName + "สุรศักดิ์";
                Latitude = 13.71904;
                Longitude = 100.52169;
                break;
            case "สะพานตากสิน":
                markerName = _tempMarkerName + "สะพานตากสิน";
                Latitude = 13.71856;
                Longitude = 100.51415;
                break;
            case "กรุงธนบุรี":
                markerName = _tempMarkerName + "กรุงธนบุรี";
                Latitude = 13.72066;
                Longitude = 100.50290;
                break;
            case "วงเวียนใหญ่":
                markerName = _tempMarkerName + "วงเวียนใหญ่";
                Latitude = 13.72087;
                Longitude = 100.49531;
                break;
            case "โพธิ์นิมิตร":
                markerName = _tempMarkerName + "โพธิ์นิมิตร";
                Latitude = 13.71906;
                Longitude = 100.48602;
                break;
            case "ตลาดพลู":
                markerName = _tempMarkerName + "ตลาดพลู";
                Latitude = 13.71421;
                Longitude = 100.47684;
                break;
            case "วุฒากาศ":
                markerName = _tempMarkerName + "วุฒากาศ";
                Latitude = 13.71287;
                Longitude = 100.46882;
                break;
            case "บางหว้า":
                markerName = _tempMarkerName + "บางหว้า";
                Latitude = 13.72056;
                Longitude = 100.45762;
                break;
        }
    }

    private void setMarkerBrt(String _location) {
        String _tempMarkerName = "BRT ";
        switch (_location) {
            case "สาทร":
                markerName = _tempMarkerName + "สาทร";
                Latitude = 13.72165;
                Longitude = 100.53086;
                break;
            case "อาคารสงเคราะห์":
                markerName = _tempMarkerName + "อาคารสงเคราะห์";
                Latitude = 13.71735;
                Longitude = 100.53280;
                break;
            case "เทคนิคกรุงเทพ":
                markerName = _tempMarkerName + "เทคนิคกรุงเทพ";
                Latitude = 13.71271;
                Longitude = 100.53546;
                break;
            case "ถนนจันทน์":
                markerName = _tempMarkerName + "ถนนจันทน์";
                Latitude = 13.70480;
                Longitude = 100.53919;
                break;
            case "นราราม 3":
                markerName = _tempMarkerName + "นราราม 3";
                Latitude = 13.69624;
                Longitude = 100.54510;
                break;
            case "วัดด่าน":
                markerName = _tempMarkerName + "วัดด่าน";
                Latitude = 13.67405;
                Longitude = 100.54334;
                break;
            case "วัดปริวาส":
                markerName = _tempMarkerName + "วัดปริวาส";
                Latitude = 13.67457;
                Longitude = 100.53400;
                break;
            case "วัดดอกไม้":
                markerName = _tempMarkerName + "วัดดอกไม้";
                Latitude = 13.68218;
                Longitude = 100.52516;
                break;
            case "สะพานพระราม 9":
                markerName = _tempMarkerName + "สะพานพระราม 9";
                Latitude = 13.68795;
                Longitude = 100.51533;
                break;
            case "เจริญราษฎร์":
                markerName = _tempMarkerName + "เจริญราษฎร์";
                Latitude = 13.69007;
                Longitude = 100.50410;
                break;
            case "สะพานพระราม 3":
                markerName = _tempMarkerName + "สะพานพระราม 3";
                Latitude = 13.69379;
                Longitude = 100.49992;
                break;
            case "ราชพฤกษ์":
                markerName = _tempMarkerName + "ราชพฤกษ์";
                Latitude = 13.71599;
                Longitude = 100.47917;
                break;
        }
    }

    public void setMarkerFormType(int _typeMoveMap, String _location) {

        mTxtDetailTv.setText("ค้นหาสถานที่บริเวณรอบๆ 1กิโลเมตร");

        switch (_typeMoveMap) {
            case 1:
                setMarkerBrt(_location);
                break;
            case 2:
                setMarkerBts(_location);
                break;
            case 3:
                setMarkerBoat(_location);
                break;
            case 4:
                setMarkerMrt(_location);
                break;
            case 5:
                setMarkerTemple(_location);
                break;
            case 6:
                setMarkerArl(_location);
                break;
            case 7:
                setMarkerCurrent();
                break;
            case 8:
                setMarkerCustom();
                break;
        }
    }

    public void setupMap() {
        LatLng _latlng = new LatLng(13.74573, 100.53452);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(_latlng, 10));
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);

        mCureentLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveCurrentLocation();
            }
        });



        if (typeMoveMap == 9) {
            setupMapFavorite();
            mNavigationBtn.setImageResource(R.drawable.icon_list);
            mNavigationBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogDetailFavorite();
                }
            });
        } else {
            setupMapNearby();
            mNavigationBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moveNavigator();
                }
            });
        }

        if (typeMoveMap < 7) {
            addMarkerStart();
        }

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

            @Override
            public void onCameraChange(CameraPosition pos) {
                if (pos.zoom != currentZoom) {
                    currentZoom = pos.zoom;
                }
            }
        });

        mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if (!marker.getSnippet().equals("@#$%^&")) {
                    startActivity(new Intent(getBaseContext(), PlaceDetailsActivity.class)
                            .putExtra("reference", mMarkerPlaceLink.get(marker.getId())).putExtra("type", type));
                }
            }
        });
    }

    private void dialogDetailFavorite() {

        String _tempTypeName = null;
        switch (typeFavorite) {
            case "atm":
                _tempTypeName = "เอทีเอ็ม";
                break;
            case "bank":
                _tempTypeName = "ธนาคาร";
                break;
            case "bus_station":
                _tempTypeName = "ป้ายรถเมล์";
                break;
            case "doctor":
                _tempTypeName = "คลีนิค";
                break;
            case "police":
                _tempTypeName = "ตำรวจ";
                break;
            case "hospital":
                _tempTypeName = "โรงพยาบาล";
                break;
            case "restaurant":
                _tempTypeName = "อาหารและเครื่องดื่ม";
                break;
            case "cafe":
                _tempTypeName = "คาเฟ่";
                break;
            case "department_store":
                _tempTypeName = "ห้างสรรพสินค้า";
                break;
            case "shopping_mall":
                _tempTypeName = "ช้อปปิ้งมอลล์";
                break;
            case "grocery_or_supermarket":
                _tempTypeName = "ซุปเปอร์มาร์เก็ต";
                break;
            case "beauty_salon":
                _tempTypeName = "ร้านเสริมสวย";
                break;
            case "gym":
                _tempTypeName = "ยิม";
                break;
            case "post_office":
                _tempTypeName = "ที่ทำการไปรษณีย์";
                break;
            case "school":
                _tempTypeName = "โรงเรียน";
                break;
            case "university":
                _tempTypeName = "มหาวิทยาลัย";
                break;
            case "gas_station":
                _tempTypeName = "ปั้มน้ำมันและแก็ส";
                break;
            case "parking":
                _tempTypeName = "ที่จอดรถ";
                break;
            case "car_repair":
                _tempTypeName = "อู่ซ่อมรถ";
                break;
            case "restroom":
                _tempTypeName = "ห้องน้ำ";
                break;
            case "pharmacy":
                _tempTypeName = "ร้านขายยา";
                break;
            case "clinic":
                _tempTypeName = "คลีนิค";
                break;
            case "veterinary_clinic":
                _tempTypeName = "คลีนิครักษาสัตว์";
                break;
            case "daily_home":
                _tempTypeName = "ที่พักรายวัน";
                break;
            case "officer":
                _tempTypeName = "จุดพักผ่อน";
                break;
            case "garage":
                _tempTypeName = "ร้านปะยาง";
                break;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("ประเภท: " + _tempTypeName + "\n\n" + detailFavorite
        + "\n\n" + TimeFavorite);
        builder.setPositiveButton("ปิด", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void setupMapNearby() {
        setMarkerFormType(typeMoveMap, locationPutExtra);

        mLabelTv.setText("สถานที่ใกล้เคียง");

        smallGoogleStreetRy = (RelativeLayout) findViewById(R.id.smallGoogleStreetRy);

        LinearLayout smallGoogleStreetly = (LinearLayout) findViewById(R.id.smallGoogleStreetly);
        smallGoogleStreetly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), GoogleMapStreet.class)
                        .putExtra("lat", makerStreet.getPosition().latitude).putExtra("lng", makerStreet.getPosition().longitude));
            }
        });

        streetViewFracment = (SupportStreetViewPanoramaFragment) getSupportFragmentManager()
                .findFragmentById(R.id.smallGoogleStreet);

        CustomSpinnerBusLocattionNerbyMenu location_data[] = new CustomSpinnerBusLocattionNerbyMenu[]{
                new CustomSpinnerBusLocattionNerbyMenu(R.drawable.spinner_search_gray, getString(R.string.select_nearby)),
                new CustomSpinnerBusLocattionNerbyMenu(R.drawable.mini1_1, getString(R.string.atm)),
                new CustomSpinnerBusLocattionNerbyMenu(R.drawable.mini2_1, getString(R.string.bank)),
                new CustomSpinnerBusLocattionNerbyMenu(R.drawable.mini3_1, getString(R.string.bus_station)),
                new CustomSpinnerBusLocattionNerbyMenu(R.drawable.mini4_1, getString(R.string.doctor)),
                new CustomSpinnerBusLocattionNerbyMenu(R.drawable.mini5_1, getString(R.string.police)),
                new CustomSpinnerBusLocattionNerbyMenu(R.drawable.mini6_1, getString(R.string.hospital)),
                new CustomSpinnerBusLocattionNerbyMenu(R.drawable.mini7_1, getString(R.string.restaurant)),
                new CustomSpinnerBusLocattionNerbyMenu(R.drawable.mini8_1, getString(R.string.cafe)),
                new CustomSpinnerBusLocattionNerbyMenu(R.drawable.mini9_1, getString(R.string.department_store)),
                new CustomSpinnerBusLocattionNerbyMenu(R.drawable.mini10_1, getString(R.string.shopping_mall)),
                new CustomSpinnerBusLocattionNerbyMenu(R.drawable.mini11_1, getString(R.string.grocery_or_supermarket)),
                new CustomSpinnerBusLocattionNerbyMenu(R.drawable.mini12_1, getString(R.string.beauty_salon)),
                new CustomSpinnerBusLocattionNerbyMenu(R.drawable.mini13_1, getString(R.string.gym)),
                new CustomSpinnerBusLocattionNerbyMenu(R.drawable.mini14_1, getString(R.string.post_office)),
                new CustomSpinnerBusLocattionNerbyMenu(R.drawable.mini15_1, getString(R.string.school)),
                new CustomSpinnerBusLocattionNerbyMenu(R.drawable.mini16_1, getString(R.string.university)),
                new CustomSpinnerBusLocattionNerbyMenu(R.drawable.mini17_1, getString(R.string.gas_station)),
                new CustomSpinnerBusLocattionNerbyMenu(R.drawable.mini18_1, getString(R.string.parking)),
                new CustomSpinnerBusLocattionNerbyMenu(R.drawable.mini19_1, getString(R.string.car_repair))};

        NearbyCustomSpinnerAdapter adapter = new NearbyCustomSpinnerAdapter(this, R.layout.activity_spinner,
                location_data);
        mSprPlaceType.setAdapter(adapter);

        mSprPlaceType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> av, View v, int position, long id) {

                smallGoogleStreetRy.setVisibility(View.GONE);

                if (!ControlCheckConnect.checkInternet(DetailMap.this)) {
                    ControlCheckConnect.alertInternet(DetailMap.this);
                } else {

                    if (position == 0) {
                        type = "";
                        return;
                    }

                    if (Latitude != 0 && Longitude != 0) {

                        ControlProgress.showProgressDialog(DetailMap.this);

                        mSelectedMaker = null;

                        mSelectType = position;

                        getIdMarkerLocation();

                        mTxtDetailTv.setText("ค้นหาสถานที่บริเวณรอบๆ 1กิโลเมตร");

                        if (!type.equals("")) {
                            StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
                            sb.append("location=" + Latitude + "," + Longitude);
                            sb.append("&radius=1000");
                            sb.append("&types=" + type);
                            sb.append("&sensor=true");
                            sb.append("&key=AIzaSyAwJatyU0DGovYEOKYYbDu2KzE0mKEzUPY");
                            sb.append("&language=th");

                            // Creating locationSpinnerMinicalGray new non-ui thread task to download Google place json data
                            PlacesTask placesTask = new PlacesTask();

                            // Invokes the "doInBackground()" method of the class PlaceTask
                            placesTask.execute(sb.toString());
                        }
                    } else {
                        if (typeMoveMap == 8) {
                            alertCurrentCustom();
                        } else {
                            mControlCheckConnect.alertCurrentGps(DetailMap.this);
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> av) {
                return;
            }

        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker marker) {

                if (!marker.getTitle().equals(markerName)) {

                    if (mSelectedMaker != null) {
                        mSelectedMaker.setIcon(BitmapDescriptorFactory.fromResource(getIdMarkerLocation()));
                    }

                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_selected));

                    ControlProgress.showProgressDialog(DetailMap.this);
                    mSelectedMaker = marker;

                    DownloadTask downloadTask = new DownloadTask();
                    downloadTask.execute(getDirectionsUrl(new LatLng(Latitude, Longitude), marker.getPosition()));

                } else {
                    marker.showInfoWindow();
                }
                CameraPosition cameraPosition = new CameraPosition.Builder().target(marker.getPosition()).zoom(currentZoom).tilt(30).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                return true;
            }
        });

    }

    private void setupMapFavorite() {

        mMap.setPadding(0, 400, 0, 0);

        markerName = "ตำแหน่งรายการโปรด";

        RelativeLayout googleStreetRy = (RelativeLayout) findViewById(R.id.googleStreetRy);
        googleStreetRy.setVisibility(View.VISIBLE);

        LinearLayout googleStreetLy = (LinearLayout) findViewById(R.id.googleStreetLy);
        googleStreetLy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), GoogleMapStreet.class)
                        .putExtra("lat", Latitude).putExtra("lng", Longitude));
            }
        });

        final SupportStreetViewPanoramaFragment streetViewFracment =
                (SupportStreetViewPanoramaFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.streetviewpanorama);
        streetViewFracment.getStreetViewPanoramaAsync(new OnStreetViewPanoramaReadyCallback() {
            @Override
            public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
                showStreetView(new LatLng(Latitude, Longitude), streetViewPanorama);
            }
        });

        mTxtDetailTv.setText("เมนูการนำทาง");

        mLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveNavigator();
            }
        });

        mLabelTv.setText("รายการโปรด");
        mNavigationBtn.setVisibility(View.VISIBLE);
        mSprPlaceType.setVisibility(View.INVISIBLE);
        addMarkerStart();
    }

    private void moveNavigator() {
        if (Latitude != 0 && Longitude != 0) {
            new GoogleNavigator().showDialogNavigator(DetailMap.this
                    ,String.valueOf(Latitude),String.valueOf(Longitude));

        } else {
            if (typeMoveMap == 8) {
                alertCurrentCustom();
            } else {
                mControlCheckConnect.alertCurrentGps(DetailMap.this);
            }
        }
    }

    private void moveCurrentLocation() {
        if (Latitude != 0 && Longitude != 0) {
            LatLng currentLocation = new LatLng(Latitude, Longitude);

            CameraPosition cameraPosition = new CameraPosition.Builder().target(currentLocation).zoom(15).tilt(30).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        } else {
            if (typeMoveMap == 8) {
                alertCurrentCustom();
            } else {
                mControlCheckConnect.alertCurrentGps(DetailMap.this);
            }
        }
    }


    public void addMarkerStart() {
        MarkerOptions marker = new MarkerOptions();
        LatLng currentLocation = new LatLng(Latitude, Longitude);

        marker.position(currentLocation).title(markerName).snippet("@#$%^&");

        if (typeMoveMap <= 6 || typeMoveMap == 9) {
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_station));
        } else {
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_current));
        }

        mMap.addMarker(marker);

        CameraPosition cameraPosition = new CameraPosition.Builder().target(currentLocation).zoom(15).tilt(30).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                LayoutInflater inflater = getLayoutInflater();
                View infoWindow = inflater.inflate(R.layout.activity_info_window, null);

                TextView nameMarker = (TextView) infoWindow.findViewById(R.id.nameMarker);
                nameMarker.setText(marker.getTitle());

                return infoWindow;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });

        ControlProgress.hideDialog();
    }

    public int getIdMarkerLocation() {

        int id = R.drawable.marker_nearby_atm;
        switch (mSelectType) {
            case 0:
                type = "";
                return 0;
            case 1:
                type = "atm";
                break;
            case 2:
                id = R.drawable.marker_nearby_bank;
                type = "bank";
                break;
            case 3:
                id = R.drawable.marker_nearby_bus_station;
                type = "bus_station";
                break;
            case 4:
                id = R.drawable.marker_nearby_doctor;
                type = "doctor";
                break;
            case 5:
                id = R.drawable.marker_nearby_police;
                type = "police";
                break;
            case 6:
                id = R.drawable.marker_nearby_hospital;
                type = "hospital";
                break;
            case 7:
                id = R.drawable.marker_nearby_restaurant;
                type = "restaurant";
                break;
            case 8:
                id = R.drawable.marker_nearby_cafe;
                type = "cafe";
                break;
            case 9:
                id = R.drawable.marker_nearby_department_store;
                type = "department_store";
                break;
            case 10:
                id = R.drawable.marker_nearby_shopping_mall;
                type = "shopping_mall";
                break;
            case 11:
                id = R.drawable.marker_nearby_grocery_or_supermarket;
                type = "grocery_or_supermarket";
                break;
            case 12:
                id = R.drawable.marker_nearby_beauty_salon;
                type = "beauty_salon";
                break;
            case 13:
                id = R.drawable.marker_nearby_gym;
                type = "gym";
                break;
            case 14:
                id = R.drawable.marker_nearby_post_office;
                type = "post_office";
                break;
            case 15:
                id = R.drawable.marker_nearby_school;
                type = "school";
                break;
            case 16:
                id = R.drawable.marker_nearby_university;
                type = "university";
                break;
            case 17:
                id = R.drawable.marker_nearby_gas_station;
                type = "gas_station";
                break;
            case 18:
                id = R.drawable.marker_nearby_parking;
                type = "parking";
                break;
            case 19:
                id = R.drawable.marker_nearby_car_repair;
                type = "car_repair";
                break;
        }
        return id;
    }


    //---------------------  User Interface -------------------------------//

    public void alertCurrentCustom() {

        ControlProgress.hideDialog();
        final Dialog dialog = new Dialog(DetailMap.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_dialog_current_custom);

        Button btnClose = (Button) dialog.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.show();
    }

    //---------------------  Get Place Nearby -------------------------------//

    /**
     * A class, to download Google Places
     */
    private class PlacesTask extends AsyncTask<String, Integer, String> {

        String data = null;

        // Invoked by execute() method of this object
        @Override
        protected String doInBackground(String... url) {
            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {

            }
            return data;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(String result) {
            if(result != null) {
                ParserTask parserTask = new ParserTask();
                parserTask.execute(result);
            }
        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> implements GoogleMap.InfoWindowAdapter {

        JSONObject jObject;

        // Invoked by execute() method of this object
        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {

            List<HashMap<String, String>> places = null;
            PlaceJSONParser placeJsonParser = new PlaceJSONParser();

            try {
                jObject = new JSONObject(jsonData[0]);

                /** Getting the parsed data as a List construct */
                places = placeJsonParser.parse(jObject);

            } catch (Exception e) {
            }
            return places;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(List<HashMap<String, String>> list) {

            mMap.clear();
            addMarkerStart();

            if (list.size() > 0) {

                for (int i = 0; i < list.size(); i++) {

                    // Creating a marker
                    MarkerOptions markerOptions = new MarkerOptions();

                    // Getting a place from the places list
                    HashMap<String, String> hmPlace = list.get(i);

                    double lat = Double.parseDouble(hmPlace.get("lat"));
                    double lng = Double.parseDouble(hmPlace.get("lng"));
                    LatLng latLng = new LatLng(lat, lng);

                    String name = hmPlace.get("place_name");

                    markerOptions.position(latLng);
                    markerOptions.snippet(" ");

                /*

                IconGenerator factory = new IconGenerator(getApplication());
                Bitmap icon;

                factory.setColor(Color.parseColor("#2196F3"));
                factory.setTextAppearance(R.style.iconGenText);


                if (name.length() > 16) {
                    icon = factory.makeIcon(name.substring(0, 15) + "...");
                } else {
                    markerOptions.title(name);
                    icon = factory.makeIcon(name);
                }
                */

                    markerOptions.title(name);
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(getIdMarkerLocation()));

                    Marker m = mMap.addMarker(markerOptions);
                    mMap.addMarker(markerOptions);
                    mMap.setInfoWindowAdapter(this);

                    mMarkerPlaceLink.put(m.getId(), hmPlace.get("reference"));
                }
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailMap.this);
                builder.setMessage("ไม่พบข้อมูลสถานที่ใกล้เคียง");

                builder.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();

                Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                pbutton.setTextColor(Color.parseColor("#147cce"));
                pbutton.setTypeface(null, Typeface.BOLD);
            }
            ControlProgress.hideDialog();
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }

        @Override
        public View getInfoWindow(Marker marker) {

            View infoWindow;

            if (marker.getSnippet().equals("@#$%^&")) {
                LayoutInflater inflater = getLayoutInflater();
                infoWindow = inflater.inflate(R.layout.activity_info_window, null);

                TextView nameMarker = (TextView) infoWindow.findViewById(R.id.nameMarker);
                nameMarker.setText(marker.getTitle());
            } else {

                makerStreet = marker;

                smallGoogleStreetRy.setVisibility(View.VISIBLE);

                streetViewFracment.getStreetViewPanoramaAsync(new OnStreetViewPanoramaReadyCallback() {
                    @Override
                    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
                        showStreetView(new LatLng(makerStreet.getPosition().latitude,
                                makerStreet.getPosition().longitude), streetViewPanorama);
                    }
                });

                mTxtDetailTv.setText(marker.getTitle());

                LayoutInflater inflater = getLayoutInflater();
                infoWindow = inflater.inflate(R.layout.activity_info_window_nearby, null);

                TextView distanceTxt = (TextView) infoWindow.findViewById(R.id.distanceTxt);
                distanceTxt.setText(distance);

                TextView durationTxt = (TextView) infoWindow.findViewById(R.id.durationTxt);
                durationTxt.setText(duration);
            }
            return infoWindow;
        }
    }

    //---------------------  get distance , duration -------------------------------//

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask1 parserTask = new ParserTask1();
            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }


    public String downloadUrl(String strUrl) throws IOException {
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

    public String getDirectionsUrl(LatLng origin, LatLng dest) {

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

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask1 extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                JsonParserDirections parser = new JsonParserDirections();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {


            if (result == null) {
                return;
            }

            if (result.size() < 1) {
                return;
            }


            ArrayList<LatLng> points = null;
            distance = "";
            duration = "";

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    if (j == 0) {    // Get distance from the list
                        distance = (String) point.get("distance");
                        continue;
                    } else if (j == 1) { // Get duration from the list
                        duration = (String) point.get("duration");
                        break;
                    }

                }
                int a = distance.length();
                a -= 2;
                distance = distance.substring(0, a);
                distance = distance + "กม.";

                a = duration.length();
                if (a > 7) {
                    int b;
                    String temp = "";

                    b = duration.indexOf("r");
                    temp = duration.substring(b + 2, a - 4);

                    b = duration.indexOf("h");
                    b = b - 1;
                    duration = duration.substring(0, b);
                    duration = duration + " ชม. ";

                    duration = duration + temp + "น.";
                } else {
                    a -= 4;
                    duration = duration.substring(0, a);
                    duration = duration + "นาที";
                }

                ControlProgress.hideDialog();
                mSelectedMaker.showInfoWindow();
            }
        }
    }

    private void showStreetView(LatLng latLng, StreetViewPanorama streetViewPanorama) {
        streetViewPanorama.getPanoramaCamera();

        StreetViewPanoramaCamera.Builder builder = new StreetViewPanoramaCamera.Builder(streetViewPanorama.getPanoramaCamera());
        builder.tilt(0.0f);
        builder.zoom(0.0f);
        builder.bearing(0.0f);
        streetViewPanorama.animateTo(builder.build(), 0);

        streetViewPanorama.setPosition(latLng, 300);
        streetViewPanorama.setStreetNamesEnabled(true);
    }

    private void bindWidget() {
        mBackIm = (ImageView) findViewById(R.id.backIm);
        mLabelTv = (TextView) findViewById(R.id.labelTv);
        mMainIm = (ImageView) findViewById(R.id.mainIm);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(map);
        mSprPlaceType = (Spinner) findViewById(R.id.spinner);
        mCureentLocationBtn = (ImageView) findViewById(R.id.cureentLocationBtn);
        mNavigationBtn = (CircleImageView) findViewById(R.id.navigationBtn);
        mLinearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        mTxtDetailTv = (TextView) findViewById(R.id.txtDetailTv);
    }
}

