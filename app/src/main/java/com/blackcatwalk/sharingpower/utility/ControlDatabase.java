package com.blackcatwalk.sharingpower.utility;

import android.app.Activity;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.blackcatwalk.sharingpower.BusGps;
import com.blackcatwalk.sharingpower.Favorite;
import com.blackcatwalk.sharingpower.FavoriteMain;
import com.blackcatwalk.sharingpower.ForgetPassword;
import com.blackcatwalk.sharingpower.LocationComment;
import com.blackcatwalk.sharingpower.LocationCommentMain;
import com.blackcatwalk.sharingpower.LocationDetail;
import com.blackcatwalk.sharingpower.LocationGps;
import com.blackcatwalk.sharingpower.LoginMain;
import com.blackcatwalk.sharingpower.LoginSub;
import com.blackcatwalk.sharingpower.Profile;
import com.blackcatwalk.sharingpower.ProfileSetting;
import com.blackcatwalk.sharingpower.R;
import com.blackcatwalk.sharingpower.Rank;
import com.blackcatwalk.sharingpower.RankMain;
import com.blackcatwalk.sharingpower.RegisterPage1;
import com.blackcatwalk.sharingpower.RegisterPage2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ControlDatabase {

    private final String mSetDatabase = "https://www.smilemap.me/android/set.php";
    private final String mGetDatabase = "https://www.smilemap.me/android/get.php?main=";
    private final String mGetDatabaseResetPassword = "https://www.smilemap.me/android/resetpassword.php";

    private final String mUserName;
    private Activity mActivity;

    public ControlDatabase(Activity _activity) {
        this.mActivity = _activity;
        this.mUserName = ControlFile.getUsername(_activity);
    }

    public String getMSetDatabase() {
        return mSetDatabase;
    }

    public String getMGetDatabase() {
        return mGetDatabase;
    }

    public String getMGetDatabaseResetPassword() {
        return mGetDatabaseResetPassword;
    }

    private int randomNumber() {
        Random rand = new Random();
        return rand.nextInt(5000) + 1;
    }

    private void showToast(String _temp) {
        LayoutInflater inflater = mActivity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast,
                (ViewGroup) mActivity.findViewById(R.id.toast_layout_root));

        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(_temp);

        final Toast toast = new Toast(mActivity.getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 500);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();

        new CountDownTimer(5000, 1000) {

            @Override
            public void onTick(long l) {
                toast.show();
            }

            @Override
            public void onFinish() {
            }
        }.start();
    }

    private boolean checkInternet() {
        ControlProgress.hideDialog();

        ControlProgress.showProgressDialogDonTouch(mActivity);

        if (ControlCheckConnect.checkInternet(mActivity)) {
            return true;
        }
        ControlCheckConnect.alertCurrentInternet(mActivity);
        ControlProgress.hideDialog();
        return false;
    }

    private boolean checkInternetProgressCustom() {

        if (ControlCheckConnect.checkInternet(mActivity)) {
            return true;
        }
        ControlCheckConnect.alertCurrentInternet(mActivity);
        return false;
    }


    //------------------------------------------------------------

    public void reportGeneral(final String _detail, final String _type_report) {

        if (checkInternet()) {

            RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
            StringRequest jor = new StringRequest(Request.Method.POST, getMSetDatabase(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            showToast("ส่งข้อมูลสำเร็จ");
                            ControlProgress.hideDialog();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            showToast("ส่งข้อมูลไม่สำเร็จ");
                            ControlProgress.hideDialog();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("main", "reportGeneral");
                    params.put("username", mUserName);
                    params.put("detail", _detail);
                    params.put("type_report", _type_report);
                    return params;
                }
            };
            jor.setShouldCache(false);
            requestQueue.add(jor);
        }
    }


    //------------------------------------------------------------

    public void reportComment(final String _idUser, final String _idUserComment,final String _lat,
                              final String _lng, final String _type, final String _detail) {

        if (checkInternet()) {

            RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
            StringRequest jor = new StringRequest(Request.Method.POST, getMSetDatabase(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            showToast("ส่งข้อมูลสำเร็จ");
                            ControlProgress.hideDialog();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            showToast("ส่งข้อมูลไม่สำเร็จ");
                            ControlProgress.hideDialog();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("main", "reportComment");
                    params.put("username", _idUser);
                    params.put("detail", _detail);
                    params.put("lat", _lat);
                    params.put("lng", _lng);
                    params.put("type", _type);
                    params.put("usercomment", _idUserComment);
                    return params;
                }
            };
            jor.setShouldCache(false);
            requestQueue.add(jor);
        }
    }

    //---------------------------------------------------------------------------------------------------

    public void getDatabaseFavoriteMain() {

        if (checkInternetProgressCustom()) {

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(getMGetDatabase() + "favorite&sub="
                    + mUserName + ((FavoriteMain) mActivity).getmTempUrl()
                    + "&ramdom=" + randomNumber(), new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {

                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            Favorite item = new Favorite();

                            item.setDetail(obj.getString("detail"));
                            item.setLat(obj.getDouble("lat"));
                            item.setLng(obj.getDouble("lng"));
                            item.setTime(obj.getString("update_date"));
                            item.setType(obj.getString("type"));
                            ((FavoriteMain) mActivity).mFavoriteList.add(item);

                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }
                    if (response.length() > 0) {
                        ((FavoriteMain) mActivity).mFavotiteIm.setVisibility(View.INVISIBLE);
                        ((FavoriteMain) mActivity).mFavotiteTv.setVisibility(View.INVISIBLE);
                    } else {
                        ((FavoriteMain) mActivity).mFavotiteIm.setVisibility(View.VISIBLE);
                        ((FavoriteMain) mActivity).mFavotiteTv.setVisibility(View.VISIBLE);
                    }
                    ((FavoriteMain) mActivity).mAdapter.notifyDataSetChanged();
                    ((FavoriteMain) mActivity).mSwipeRefreshLayout.setRefreshing(false);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    ((FavoriteMain) mActivity).mSwipeRefreshLayout.setRefreshing(false);
                }
            });
            AppController.getmInstance().
                    addToRequesQueue(jsonArrayRequest);
        }else {
            ((FavoriteMain) mActivity).mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    public void setFavoriteMain(final String _detailName, final String _job) {

        if (checkInternetProgressCustom()) {

            RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
            StringRequest jor = new StringRequest(Request.Method.POST, getMSetDatabase(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            ((FavoriteMain) mActivity).fetchData();
                            ((FavoriteMain) mActivity).mSwipeRefreshLayout.setRefreshing(false);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            showToast("บันทึกไม่สำเร็จ");
                            ((FavoriteMain) mActivity).mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("main", "favorite");
                    params.put("job", _job);
                    params.put("username", mUserName);
                    params.put("lat", ((FavoriteMain) mActivity).mItem.getLat().toString());
                    params.put("lng", ((FavoriteMain) mActivity).mItem.getLng().toString());
                    params.put("detailold", ((FavoriteMain) mActivity).mItem.getDetail());
                    params.put("detailnew", _detailName);
                    params.put("type", ((FavoriteMain) mActivity).mItem.getType());
                    return params;
                }
            };
            jor.setShouldCache(false);
            requestQueue.add(jor);
        }else {
            ((FavoriteMain) mActivity).mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    //----------------------------------------------------------------------------------------


    public void setForgetPassword(final String _email) {

        if (checkInternet()) {

            RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
            StringRequest jor = new StringRequest(Request.Method.POST, getMGetDatabaseResetPassword(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            ControlProgress.hideDialog();
                            ((ForgetPassword) mActivity).showDialogSuccess();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            ControlProgress.hideDialog();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("email", _email);
                    return params;
                }
            };
            jor.setShouldCache(false);
            requestQueue.add(jor);

        }
    }

    //--------------------------------------------------------------------------------

    public void getDatabaeeLocationCommentMain(String _tempUrl) {

        if (checkInternetProgressCustom()) {

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(getMGetDatabase() + _tempUrl + "&ramdom=" + randomNumber(),
                    new Response.Listener<JSONArray>() {

                        @Override
                        public void onResponse(JSONArray response) {

                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject obj = response.getJSONObject(i);
                                    LocationComment _item = new LocationComment();

                                    _item.setmComment(obj.getString("comment"));
                                    _item.setmUpdateDate(obj.getString("update_date"));
                                    _item.setmUsers(obj.getString("name"));
                                    _item.setmId(obj.getInt("id_users"));
                                    _item.setmSex(obj.getString("sex"));

                                    ((LocationCommentMain) mActivity).mLocationComment.add(_item);
                                } catch (JSONException ex) {
                                    ex.printStackTrace();
                                }
                            }
                            ((LocationCommentMain) mActivity).mSwipeRefreshLayout.setRefreshing(false);
                            ((LocationCommentMain) mActivity).mAdapter.notifyDataSetChanged();
                            ((LocationCommentMain) mActivity).mListView.setSelection(
                                    ((LocationCommentMain) mActivity).mListView.getAdapter().getCount() - 1);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    ((LocationCommentMain) mActivity).mSwipeRefreshLayout.setRefreshing(false);
                }
            });
            AppController.getmInstance().addToRequesQueue(jsonArrayRequest);
        }else{
            ((LocationCommentMain) mActivity).mSwipeRefreshLayout.setRefreshing(false);
        }


    }

    public void getDatabaeeLocationCommentMainIdUser() {

        if (checkInternet()) {

            RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
            JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, getMGetDatabase() +
                    "get_id_user&user=" + mUserName + "&ramdom=" + randomNumber(), null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONArray ja = response.getJSONArray("id_user");

                                ((LocationCommentMain) mActivity).setmIdUser(ja.getJSONObject(0).getInt("id"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            ControlProgress.hideDialog();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            ControlProgress.hideDialog();
                        }
                    }
            );
            jor.setShouldCache(false);
            requestQueue.add(jor);
        }
    }

    //--------------------------------------------------------

    public void setDatabaeeLocationCommentMain(final String _lat, final String _lng, final String _comment, final String _type) {

        if (checkInternetProgressCustom()) {

            RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
            StringRequest jor = new StringRequest(Request.Method.POST, getMSetDatabase(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            ((LocationCommentMain) mActivity).fetchData();
                            ((LocationCommentMain) mActivity).mSwipeRefreshLayout.setRefreshing(false);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            showToast("ส่งข้อมูลไม่สำเร็จ");
                            ((LocationCommentMain) mActivity).mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("main", "comment_location");
                    params.put("username", mUserName);
                    params.put("lat", _lat);
                    params.put("lng", _lng);
                    params.put("comment", _comment);
                    params.put("type", _type);
                    return params;
                }
            };
            jor.setShouldCache(false);
            requestQueue.add(jor);
        }else {
            ((LocationCommentMain) mActivity).mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    //--------------------------------------------------------

    public void setDatabaeeLocationCommentMainEdit(final String _userId, final String _detail,
                                                   final String _lat, final String _lng,
                                                   final String _type, final String _updateDate) {

        if (checkInternetProgressCustom()) {

            RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
            StringRequest jor = new StringRequest(Request.Method.POST, getMSetDatabase(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            ((LocationCommentMain) mActivity).fetchData();
                            ((LocationCommentMain) mActivity).mSwipeRefreshLayout.setRefreshing(false);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            showToast("ส่งข้อมูลไม่สำเร็จ");
                            ((LocationCommentMain) mActivity).mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("main", "editComment");
                    params.put("username", _userId);
                    params.put("comment", _detail);
                    params.put("lat", _lat);
                    params.put("lng", _lng);
                    params.put("type", _type);
                    params.put("updatedate", _updateDate);
                    return params;
                }
            };
            jor.setShouldCache(false);
            requestQueue.add(jor);
        }else {
            ((LocationCommentMain) mActivity).mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    //--------------------------------------------------------

    public void setDatabaeeLocationCommentMainDelete(final String _userId, final String _updateDate) {

        if (checkInternet()) {

            RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
            StringRequest jor = new StringRequest(Request.Method.POST, getMSetDatabase(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            ((LocationCommentMain) mActivity).fetchData();
                            ControlProgress.hideDialog();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            showToast("ส่งข้อมูลไม่สำเร็จ");
                            ControlProgress.hideDialog();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("main", "deleteComment");
                    params.put("username", _userId);
                    params.put("updatedate", _updateDate);
                    return params;
                }
            };
            jor.setShouldCache(false);
            requestQueue.add(jor);
        }
    }

    //---------------------------------------------------------------------------------


    public void getDatabaseLocationDetail(String _tempUtl) {

        if (checkInternet()) {

            RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
            JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, getMGetDatabase() +
                    _tempUtl + "&ramdom=" + randomNumber(), null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {
                                JSONArray ja = response.getJSONArray("vote");
                                JSONObject jsonObject = null;

                                for (int i = 0; i < ja.length(); i++) {
                                    jsonObject = ja.getJSONObject(i);

                                    ((LocationDetail) mActivity).setVote(
                                            jsonObject.getString("vote"), jsonObject.getString("count(vote)"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            ControlProgress.hideDialog();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            ControlProgress.hideDialog();
                        }
                    }
            );
            jor.setShouldCache(false);
            requestQueue.add(jor);

        }
    }

    public void setVoteDatabaseLocationDetail(final String _lat, final String _lng, final String _ratingbar) {

        if (checkInternet()) {

            RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
            StringRequest jor = new StringRequest(Request.Method.POST, getMSetDatabase(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            getDatabaseLocationDetail("vote&lat=" + _lat + "&lng=" + _lng);
                            showToast("ให้คะแนนเรียบร้อย");
                            ControlProgress.hideDialog();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            showToast("ให้คะแนนไม่สำเร็จ");
                            ControlProgress.hideDialog();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("main", "vote");
                    params.put("lat", _lat);
                    params.put("lng", _lng);
                    params.put("vote", _ratingbar);
                    return params;
                }
            };

            jor.setShouldCache(false);
            requestQueue.add(jor);
        }
    }

    public void setFavoriteDatabaseLocationDetail(final String _lat, final String _lng,
                                                  final String _detail, final String _type) {

        if (checkInternet()) {

            RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
            StringRequest jor = new StringRequest(Request.Method.POST, getMSetDatabase(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            showToast("บันทึกสำเร็จ");
                            ControlProgress.hideDialog();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            showToast("บันทึกไม่สำเร็จ");
                            ControlProgress.hideDialog();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("main", "favorite");
                    params.put("job", "savefavorite");
                    params.put("username", mUserName);
                    params.put("lat", _lat);
                    params.put("lng", _lng);
                    params.put("detailnew", _detail);
                    params.put("type", _type);

                    return params;
                }
            };
            jor.setShouldCache(false);
            requestQueue.add(jor);
        }
    }


    public void setReportDatabaseLocationDetail(final String _lat, final String _lng, final String _detail) {

        if (checkInternet()) {

            RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
            StringRequest jor = new StringRequest(Request.Method.POST, mGetDatabase,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            showToast("ส่งข้อมูลสำเร็จ");
                            ControlProgress.hideDialog();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            showToast("ส่งข้อมูลไม่สำเร็จ");
                            ControlProgress.hideDialog();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("main", "reportGeneral");
                    params.put("username", mUserName);
                    params.put("lat", _lat);
                    params.put("lng", _lng);
                    params.put("detail", _detail);
                    return params;
                }
            };
            jor.setShouldCache(false);
            requestQueue.add(jor);
        }
    }

    //----------------------------------------------------------------------------------------------------------------

    public void setDabaseLoginMain(final String _userName, final String _passWord, final String _nickName, final String _sex) {

        if (checkInternet()) {

            final String _tempPassword = new Control().md5(_passWord) + randomNumber();

            RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
            StringRequest jor = new StringRequest(Request.Method.POST, getMSetDatabase(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            ((LoginMain) mActivity).updateFile(_userName);
                            ControlProgress.hideDialog();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            ControlProgress.hideDialog();
                            new Control().closeApp(mActivity);
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("main", "register");
                    params.put("email", _userName);
                    params.put("password", _tempPassword);
                    params.put("nickname", _nickName);
                    params.put("sex", _sex);
                    params.put("facebook", "1");
                    // params.put("picture", _urlPic);
                    return params;
                }
            };
            jor.setShouldCache(false);
            requestQueue.add(jor);
        }
    }

    //-------------------------------------------------------------------------------------------------------------

    public void getDatabaseLoginub(String _userName, String _passWord) {

        if (checkInternet()) {

            RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
            JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, getMGetDatabase() + "login&email="
                    + _userName + "&password=" + _passWord + "&ramdom=" + randomNumber(), null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {

                                JSONArray ja = response.getJSONArray("users");

                                String test = "";
                                JSONObject jsonObject = null;

                                for (int i = 0; i < ja.length(); i++) {
                                    jsonObject = ja.getJSONObject(i);
                                    test = jsonObject.getString("check");
                                }

                                if (test.equals("true")) {
                                    ((LoginSub) mActivity).saveStausLoginToFile();
                                } else {
                                    ((LoginSub) mActivity).countInvalid();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            ControlProgress.hideDialog();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            ControlProgress.hideDialog();
                            new Control().closeApp(mActivity);
                        }
                    }
            );
            jor.setShouldCache(false);
            requestQueue.add(jor);
        }
    }

    //----------------------------------------------------------------------------------------

    public void getDatabaseProfile() {

        if (checkInternet()) {

            RequestQueue requestQueue = Volley.newRequestQueue(mActivity);

            JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, getMGetDatabase() +
                    "users&sub=" + mUserName + "&ramdom=" + randomNumber(), null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONArray ja = response.getJSONArray("users");

                                JSONObject jsonObject = null;

                                for (int i = 0; i < ja.length(); i++) {

                                    jsonObject = ja.getJSONObject(i);

                                    ((Profile) mActivity).setProfile(jsonObject.getString("name"), jsonObject.getString("sex")
                                            , jsonObject.getInt("count_traffic"), jsonObject.getInt("count_location"),
                                            jsonObject.getInt("point")
                                            , jsonObject.getString("staus"), jsonObject.getInt("sum_hour"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            ControlProgress.hideDialog();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            ControlProgress.hideDialog();
                        }
                    }
            );
            jor.setShouldCache(false);
            requestQueue.add(jor);
        }
    }

    //---------------------------------------------------------------------------


    public void setDatabaseProfileSettingUpdateName(final String _name, final String _staus) {

        if (checkInternet()) {

            RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
            StringRequest jor = new StringRequest(Request.Method.POST, getMSetDatabase(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            setDatabaseProfileSettingUpdatestatu(_staus);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            ControlProgress.hideDialog();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("main", "profile");
                    params.put("job", "changename");
                    params.put("username", mUserName);
                    params.put("name", _name);
                    return params;
                }
            };
            jor.setShouldCache(false);
            requestQueue.add(jor);
        }
    }

    public void setDatabaseProfileSettingUpdatestatu(final String _staus) {

        if (checkInternet()) {

            RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
            StringRequest jor = new StringRequest(Request.Method.POST, mSetDatabase,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            ControlProgress.hideDialog();
                            mActivity.onBackPressed();
                            mActivity.finish();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            ControlProgress.hideDialog();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("main", "profile");
                    params.put("job", "changestaus");
                    params.put("username", mUserName);
                    params.put("name", _staus);
                    return params;
                }
            };
            jor.setShouldCache(false);
            requestQueue.add(jor);
        }
    }


    public void getDatabaseProfileSettingPassword(String _tempUrl) {

        if (checkInternet()) {

            RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
            JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, getMGetDatabase() + "check_password"
                    + "&email=" + mUserName + _tempUrl + "&ramdom=" + randomNumber(), null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONArray ja = response.getJSONArray("users");

                                String test = "";
                                JSONObject jsonObject = null;

                                for (int i = 0; i < ja.length(); i++) {
                                    jsonObject = ja.getJSONObject(i);
                                    test = jsonObject.getString("check");
                                }
                                ((ProfileSetting) mActivity).setCheckPassword(test);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            ControlProgress.hideDialog();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            ControlProgress.hideDialog();
                        }
                    }
            );
            jor.setShouldCache(false);
            requestQueue.add(jor);
        }
    }

    public void setDatabaseProfileSettingPassword(final String _password) {

        if (checkInternet()) {

            RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
            StringRequest jor = new StringRequest(Request.Method.POST, mSetDatabase,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            ((ProfileSetting) mActivity).setDialogPasswordSuccess();
                            ControlProgress.hideDialog();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            showToast("เปลี่ยนรหัสผ่านใหม่ไม่สำเร็จ");
                            ControlProgress.hideDialog();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("main", "update_password");
                    params.put("email", mUserName);
                    params.put("password_new", _password);
                    return params;
                }
            };
            jor.setShouldCache(false);
            requestQueue.add(jor);
        }
    }

    public void setDatabaseProfileSettingStopSharedBus(final String _typeDelete) {

        if (checkInternet()) {

            RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
            StringRequest jor = new StringRequest(Request.Method.POST, getMSetDatabase(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            new ControlFile().setFile(mActivity, "0+", "stausShared");
                            ControlProgress.hideDialog();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            ControlProgress.hideDialog();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("main", "delete");
                    params.put("username", mUserName);
                    params.put("type", _typeDelete);
                    return params;
                }
            };
            jor.setShouldCache(false);
            requestQueue.add(jor);
        }
    }

    //----------------------------------------------------------------------------------------------

    public void getDatabaseRankMain() {

        if (checkInternet()) {

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(mGetDatabase + "rank" +
                    "&ramdom=" + randomNumber(), new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {

                    int count = 1;

                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);

                            Rank item = new Rank();
                            item.setmSequeneNumber(String.valueOf(count));
                            item.setmName(obj.getString("name"));
                            item.setmStaus(obj.getString("staus"));
                            item.setmSex(obj.getString("sex"));
                            item.setmSumHour(obj.getInt("sum_hour") / 60);
                            item.setmPoint(obj.getInt("point"));
                            //item.setPicture(obj.getString("picture"));

                            if (((obj.getInt("sum_hour") / 60) >= 0) && ((obj.getInt("sum_hour") / 60) <= 50)) {
                                item.setmNickName("ผู้แบ่งปันเริ่มต้น");
                            } else if (((obj.getInt("sum_hour") / 60) > 50) && ((obj.getInt("sum_hour") / 60) <= 500)) {
                                item.setmNickName("ผู้แบ่งปันขั้นกลาง");
                            } else {
                                item.setmNickName("ผู้แบ่งปันสูงสูด");
                            }

                            count++;
                            ((RankMain) mActivity).mRankList.add(item);
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }
                    ((RankMain) mActivity).mAdapter.notifyDataSetChanged();
                    ControlProgress.hideDialog();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    ControlProgress.hideDialog();
                }
            });
            AppController.getmInstance().addToRequesQueue(jsonArrayRequest);
        }
    }

    public void setDatabaseRankMain(final String _stausRank) {

        if (checkInternet()) {

            RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
            StringRequest jor = new StringRequest(Request.Method.POST, getMSetDatabase(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            ((RankMain) mActivity).setData();
                            ControlProgress.hideDialog();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            showToast("บันทึกไม่สำเร็จ");
                            ControlProgress.hideDialog();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("main", "profile");
                    params.put("job", "changeStausRank");
                    params.put("username", mUserName);
                    params.put("name", _stausRank);
                    return params;
                }
            };
            jor.setShouldCache(false);
            requestQueue.add(jor);
        }
    }

    //----------------------------------------------------------------------------------------------

    public void getDatabaseRegisPage1(String _email) {

        if (checkInternet()) {

            RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
            JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, getMGetDatabase() + "register&job=" +
                    "email&email=" + _email + "&ramdom=" + randomNumber(), null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {
                                JSONArray _ja = response.getJSONArray("users");

                                JSONObject _jsonObject = null;
                                String _temp = "";

                                for (int i = 0; i < _ja.length(); i++) {
                                    _jsonObject = _ja.getJSONObject(i);
                                    _temp = _jsonObject.getString("check");
                                }
                                ((RegisterPage1) mActivity).checkEmail(_temp);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            ControlProgress.hideDialog();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            ControlProgress.hideDialog();
                            new Control().closeApp(mActivity);
                        }
                    }
            );
            jor.setShouldCache(false);
            requestQueue.add(jor);
        }
    }

    //-------------------------------------------------------------------------------------------

    public void getDatabaseRegisPage2(final String _email, final String _password,
                                      final String _nickname, final String _sex) {
        if (checkInternet()) {

            RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
            StringRequest jor = new StringRequest(Request.Method.POST, getMSetDatabase(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            ((RegisterPage2) mActivity).sendData();
                            ControlProgress.hideDialog();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            ControlProgress.hideDialog();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("main", "register");
                    params.put("password", _password);
                    params.put("email", _email);
                    params.put("nickname", _nickname);
                    params.put("sex", _sex);
                    return params;
                }
            };
            jor.setShouldCache(false);
            requestQueue.add(jor);
        }
    }

    //----------------------------------------------------------------------------------------------------

    public void setDatabaseTrafficDetail(final String _lat, final String _lng, final String _type,
                                         final String _name, final String _detail) {

        if (checkInternet()) {

            RequestQueue _requestQueue = Volley.newRequestQueue(mActivity);
            StringRequest jor = new StringRequest(Request.Method.POST, getMSetDatabase(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            showToast("ส่งข้อมูลสำเร็จ");
                            ControlProgress.hideDialog();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            showToast("ส่งข้อมูลไม่สำเร็จ");
                            ControlProgress.hideDialog();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("main", "reportGeneral");
                    params.put("username", mUserName);
                    params.put("lat", _lat);
                    params.put("lng", _lng);
                    params.put("type", _type);
                    params.put("name", _name);
                    params.put("type_report", "traffic");
                    params.put("detail", _detail);
                    return params;
                }
            };
            jor.setShouldCache(false);
            _requestQueue.add(jor);
        }
    }

    //--------------------------------------------------------------------------------------------

    public void getDatabaseLocationGps(final String _tempUrl) {

        if (checkInternet()) {
            RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
            JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, getMGetDatabase() +
                    "location&sub=" + _tempUrl + "&ramdom=" + randomNumber(), null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {
                                JSONArray ja = response.getJSONArray("shared_location");

                                String LocationDetail = null;
                                String username = null;
                                double longitude = 0;
                                double latitude = 0;
                                double distance = 0;
                                JSONObject jsonObject = null;
                                DecimalFormat decim = new DecimalFormat("0.0");

                                for (int i = 0; i < ja.length(); i++) {

                                    jsonObject = ja.getJSONObject(i);
                                    latitude = Double.parseDouble(jsonObject.getString("lat"));
                                    longitude = Double.parseDouble(jsonObject.getString("lng"));
                                    //username = jsonObject.getString("username");
                                    LocationDetail = jsonObject.getString("location_detail");
                                    distance = Double.parseDouble(decim.format(Double.parseDouble(jsonObject.getString("distance"))));
                                    ((LocationGps) mActivity).addMarkerDatabase(latitude, longitude, LocationDetail, username, distance);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            ControlProgress.hideDialog();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            ControlProgress.hideDialog();
                            new Control().closeApp(mActivity);
                        }
                    }
            );
            jor.setShouldCache(false);
            requestQueue.add(jor);
        }
    }


    //-----------------------------------------------------------------------------------------

    public void setDatabaseLocationGps(final String _lat, final String _lng, final String _type, final String _detail) {

        if (checkInternet()) {

            RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
            StringRequest jor = new StringRequest(Request.Method.POST, getMSetDatabase(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            ((LocationGps) mActivity).setMap();
                            ControlProgress.hideDialog();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            ControlProgress.hideDialog();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("main", "location");
                    params.put("username", mUserName);
                    params.put("lat", _lat);
                    params.put("lng", _lng);
                    params.put("type", _type);
                    params.put("location_detail", _detail);
                    return params;
                }
            };
            jor.setShouldCache(false);
            requestQueue.add(jor);
        }
    }


    //------------------------------------------------------------------------------------

    public void getDatabaseBusGps(final String _tempUrl, final int _selectTraffic) {

        if (checkInternet()) {

            RequestQueue requestQueue = Volley.newRequestQueue(mActivity);

            JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, getMGetDatabase() +
                    "bus&sub=" + _tempUrl + "&ramdom=" + randomNumber(), null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {
                                JSONArray ja = response.getJSONArray("shared_bus");


                                String _busNo = null;
                                String _busType = null;
                                String _busDetail = null;
                                String _amountPerson = null;
                                String _username = "";
                                String _color = null;
                                String _tempTime;

                                double _longitude = 0;
                                double _latitude = 0;
                                double _distance = 0;

                                int _busFree = 0;

                                DecimalFormat decim = new DecimalFormat("0.0");

                                JSONObject _jsonObject = null;

                                for (int i = 0; i < ja.length(); i++) {

                                    _jsonObject = ja.getJSONObject(i);
                                    _latitude = Double.parseDouble(_jsonObject.getString("lat"));
                                    _longitude = Double.parseDouble(_jsonObject.getString("lng"));
                                    _busNo = _jsonObject.getString("name");
                                    // _username = _jsonObject.getString("username");
                                    _color = _jsonObject.getString("color");

                                    //---------------------------------------------------------------------------
                                    // 0=bus, 1=bts, 2=brt, 3 = van, 4=public, 5=boat, 6=accident, 7=checkpoint
                                    //---------------------------------------------------------------------------
                                    switch (_selectTraffic) {
                                        case 0:
                                            _busType = _jsonObject.getString("category");
                                            _busFree = _jsonObject.getInt("bus_free");
                                            _amountPerson = _jsonObject.getString("amount_person");
                                            break;
                                        case 2:
                                            _amountPerson = _jsonObject.getString("amount_person");
                                            break;
                                        case 3:
                                            _busDetail = _jsonObject.getString("bus_detail");
                                            _amountPerson = _jsonObject.getString("amount_person");
                                            break;
                                        case 4:
                                            _busDetail = _jsonObject.getString("bus_detail");
                                            break;
                                        case 5:
                                            _amountPerson = _jsonObject.getString("amount_person");
                                            break;
                                        case 6:
                                            _busNo = _jsonObject.getString("category");
                                            _tempTime = _jsonObject.getString("update_date");
                                            _busType = _tempTime.substring(_tempTime.indexOf(" ") + 1, _tempTime.length()).substring(0, 5) + " นาที";
                                            _busDetail = _jsonObject.getString("bus_detail");
                                            break;
                                        case 7:
                                            _busNo = _jsonObject.getString("category");
                                            _busType = _jsonObject.getString("update_date");
                                            _busDetail = _jsonObject.getString("bus_detail");
                                            break;
                                    }

                                    _distance = Double.parseDouble(decim.format(Double.parseDouble(_jsonObject.getString("distance"))));

                                    ((BusGps) mActivity).addMarkerDatabase(_busNo, _latitude, _longitude, _busType,
                                            _busFree, _busDetail, _username, _color, _distance, _amountPerson);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            ControlProgress.hideDialog();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            ControlProgress.hideDialog();
                        }
                    }
            );
            jor.setShouldCache(false);
            requestQueue.add(jor);
        }
    }

    //----------------------------------------------------------------------------------------

    public void getDatabaseBusGpsVersion() {

        if (checkInternet()) {

            RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
            JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, getMGetDatabase()
                    + "version" + "&ramdom=" + randomNumber(), null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONArray ja = response.getJSONArray("version");

                                String _versionAppForDatabase = "";
                                JSONObject jsonObject = null;

                                for (int i = 0; i < ja.length(); i++) {
                                    jsonObject = ja.getJSONObject(i);
                                    _versionAppForDatabase = jsonObject.getString("version");
                                }
                                ((BusGps) mActivity).showDialogVersionApp(_versionAppForDatabase);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            ControlProgress.hideDialog();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            ControlProgress.hideDialog();
                        }
                    }
            );
            jor.setShouldCache(false);
            requestQueue.add(jor);
        }
    }

    //-------------------------------------------------------------------------------------------


    public void getDatabaseBusGpsStopShared() {

        if (checkInternet()) {

            RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
            JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, getMGetDatabase() + "game&sub="
                    + mUserName + "&ramdom=" + randomNumber(), null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONArray _ja = response.getJSONArray("temp");

                                for (int i = 0; i < _ja.length(); i++) {
                                    JSONObject jsonObject = _ja.getJSONObject(i);
                                    ((BusGps) mActivity).setValueTemp(jsonObject.getString("name"),
                                            jsonObject.getString("type"), jsonObject.getString("category"),
                                            jsonObject.getString("bus_free"), jsonObject.getString("bus_detail"),
                                            jsonObject.getString("amount_person"), jsonObject.getString("color"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            ControlProgress.hideDialog();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            ControlProgress.hideDialog();
                        }
                    }
            );
            jor.setShouldCache(false);
            requestQueue.add(jor);

        }
    }


    //--------------------------------------------------------------------------------------------------

    public void setDatabaseBusGps(final String _lat, final String _lng, final String _type,
                                  final String _trafficName, final String _detail, final String _category) {

        if (checkInternet()) {

            RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
            StringRequest jor = new StringRequest(Request.Method.POST, getMSetDatabase(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            ((BusGps) mActivity).setDelayAfterSetDatabase();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            ControlProgress.hideDialog();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("main", "bus");
                    params.put("username", mUserName);
                    params.put("lat", _lat);
                    params.put("lng", _lng);
                    params.put("name", _trafficName);
                    params.put("type", _type);
                    params.put("category", _category);
                    params.put("bus_detail", _detail);
                    return params;
                }
            };
            jor.setShouldCache(false);
            requestQueue.add(jor);
        }
    }

    //-------------------------------------------------------------------------------------------------------

    public void setDatabaseBusGpsStopShared(final String _typeDelete) {

        if (checkInternet()) {

            RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
            StringRequest jor = new StringRequest(Request.Method.POST, mGetDatabase,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            ((BusGps) mActivity).setAlfetStopshared();
                            ControlProgress.hideDialog();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            ControlProgress.hideDialog();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("main", "delete");
                    params.put("username", mUserName);
                    params.put("type", _typeDelete);
                    return params;
                }
            };
            jor.setShouldCache(false);
            requestQueue.add(jor);
        }
    }

    //------------------------------------------------------------------------------------------

    public void setDatabaseBusGpsTemp(final String _lat, final String _lng, final String _typeBus,
                                      final String _busNo, final String _category, final String _stausBusFree,
                                      final String _detail, final String _colorMarker, final String _amountPerson) {

        if (checkInternet()) {

            RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
            StringRequest jor = new StringRequest(Request.Method.POST, getMSetDatabase(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            setDatabaseBusGpsTempf(_lat, _lng, _typeBus, _busNo, _category,
                                    _stausBusFree, _detail, _colorMarker, _amountPerson);
                            ControlProgress.hideDialog();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            ControlProgress.hideDialog();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("main", "game");
                    params.put("username", mUserName);
                    params.put("type", _typeBus);
                    params.put("name", _busNo);
                    params.put("category", _category);
                    params.put("bus_free", _stausBusFree);
                    params.put("bus_detail", _detail);
                    params.put("color", _colorMarker);
                    params.put("amount_person", _amountPerson);
                    return params;

                }
            };
            jor.setShouldCache(false);
            requestQueue.add(jor);
        }
    }

    public void setDatabaseBusGpsTempf(final String _lat, final String _lng, final String _typeBus, final String _busNo
            , final String _category, final String _stausBusFree, final String _detail,
                                       final String _colorMarker, final String _amountPerson) {

        if (checkInternet()) {

            RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
            StringRequest jor = new StringRequest(Request.Method.POST, mSetDatabase,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            ((BusGps) mActivity).setAlfetStopsharedt(_typeBus);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            ControlProgress.hideDialog();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("main", "bus");
                    params.put("username", mUserName);
                    params.put("lat", _lat);
                    params.put("lng", _lng);
                    params.put("name", _busNo);
                    params.put("type", _typeBus);
                    params.put("category", _category);
                    params.put("amount_person", _amountPerson);
                    params.put("bus_free", _stausBusFree);
                    params.put("bus_detail", _detail);
                    params.put("color", _colorMarker);

                    return params;
                }
            };
            jor.setShouldCache(false);
            requestQueue.add(jor);
        }
    }
}
