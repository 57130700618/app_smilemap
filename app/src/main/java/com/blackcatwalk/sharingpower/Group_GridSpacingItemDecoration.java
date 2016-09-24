package com.blackcatwalk.sharingpower;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Group_GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

    private int spanCount;
    private int spacing;
    private boolean includeEdge;

    private static final String url = "https://www.smilemap.me/android/get.php?main=";
    private static String tempUrl;

    public Group_GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
        this.spanCount = spanCount;
        this.spacing = spacing;
        this.includeEdge = includeEdge;
    }
    /**
     * RecyclerView item decoration - give equal
     * margin around grid item
     */

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view); // item position
        int column = position % spanCount; //  item column

        if (includeEdge) {
            outRect.left = spacing - column * spacing / spanCount;
            outRect.right = (column + 1) * spacing / spanCount;

            if (position < spanCount) { // top edge
                outRect.top = spacing;
            }
            outRect.bottom = spacing; // item bottom
        } else {
            outRect.left = column * spacing / spanCount;
            outRect.right = spacing - (column + 1) * spacing / spanCount;
            if (position >= spanCount) {
                outRect.top = spacing; // item top
            }
        }
    }

    /**
     * Adding few albums for testing
     */
    public static void prepareAlbums(String typeGroup, final GroupAdapter adapter, final List<Group> albumList,Context c) {

        tempUrl=null;

        switch (typeGroup){
            case "day":
                tempUrl = url + "group_day";
                break;
            case "hothit":
                tempUrl = url + "group_hothit";
                break;
            case "follow":
                tempUrl = url + "group_follow&sub=" + Control.getUsername(c);
                break;
            case "my":
                tempUrl = url + "group_my&sub=" + Control.getUsername(c);
                break;
        }

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(tempUrl + "&ramdom=" + Control.randomNumber() , new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject obj = response.getJSONObject(i);

                        Group album99999 = new Group(obj.getString("name"), obj.getString
                                ("detail"), obj.getString("image"), "สุดหล่อ", 1111,
                                obj.getString("update_date").substring(0,obj.getString("update_date").indexOf(" ")+1));

                        albumList.add(album99999);
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                }
                adapter.notifyDataSetChanged();
                Control.hDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse
                    (VolleyError error) {
            }
        });
        AppController.getmInstance().addToRequesQueue(jsonArrayRequest);
    }

    /**
     * Converting dp to pixel
     */
    public static int dpToPx(int dp,Context context) {
        Resources r = context.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

}