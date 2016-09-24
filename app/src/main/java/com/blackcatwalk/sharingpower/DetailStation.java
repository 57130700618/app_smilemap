package com.blackcatwalk.sharingpower;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;

public class DetailStation extends AppCompatActivity {

    private String[] brt = {"1. สาทร", "2. อาคารสงเคราะห์", "3. เทคนิคกรุงเทพ",
            "4. ถนนจันทน์", "5. นราราม 3", "6. วัดด่าน", "7. วัดปริวาส", "8. วัดดอกไม้"
            , "9. สะพานพระราม 9", "10. เจริญราษฎร์", "11. สะพานพระราม 3", "12. ราชพฤกษ์"};

    private String[] btsMain = {"1. หมอชิต", "2. สะพานควาย", "3. อารีย์",
            "4. สนามเป้า", "5. อนุสาวรีย์ชัยสมรภูมิ", "6. พญาไท", "7. ราชเทวี", "8. สยาม"
            , "9. ชิดลม", "10. เพลินจิต", "11. นานา", "12. อโศก"
            , "13. พร้อมพงษ์", "14. ทองหล่อ", "15. เอกมัย", "16. พระโขนง", "17. อ่อนนุช"
            , "18. บางจาก", "19. ปุณณวิถี", "20. อุดมสุข", "21. บางนา", "22. แบริ่ง"
            , "23. สนามกีฬาแห่งชาติ", "24. สยาม", "25. ราชดำริ", "26. ศาลาแดง"
            , "27. ช่องนนทรี", "28. สุรศักดิ์", "29. สะพานตากสิน", "30. กรุงธนบุรี"
            , "31. วงเวียนใหญ่", "32. โพธิ์นิมิตร", "33. ตลาดพลู", "34. วุฒากาศ"
            , "35. บางหว้า"};

    private String[] btsLine1 = {"1. หมอชิต", "2. สะพานควาย", "3. อารีย์",
            "4. สนามเป้า", "5. อนุสาวรีย์ชัยสมรภูมิ", "6. พญาไท", "7. ราชเทวี", "8. สยาม"
            , "9. ชิดลม", "10. เพลินจิต", "11. นานา", "12. อโศก"
            , "13. พร้อมพงษ์", "14. ทองหล่อ", "15. เอกมัย", "16. พระโขนง", "17. อ่อนนุช"
            , "18. บางจาก", "19. ปุณณวิถี", "20. อุดมสุข", "21. บางนา", "22. แบริ่ง"};

    private String[] btsLine2 = {"1. สนามกีฬาแห่งชาติ", "2. สยาม", "3. ราชดำริ",
            "4. ศาลาแดง", "5. ช่องนนทรี", "6. สุรศักดิ์", "7. สะพานตากสิน", "8. กรุงธนบุรี"
            , "9. วงเวียนใหญ่", "10. โพธิ์นิมิตร", "11. ตลาดพลู", "12. วุฒากาศ"
            , "13. บางหว้า"};


    private String[] boatMain = {"1. ท่าเรือวัดศรีบุญเรือง", "2. ท่าเรือบางกะปิ", "3. ท่าเรือเดอะมอลล์บางกะปิ",
            "4. ท่าเรือวัดกลาง", "5. สะพานมิตรมหาดไทย", "6. ม.รามคำแหง", "7. ท่าเรือวัดเทพลีลา", "8. รามคำแหง 29"
            , "9. เดอะมอลล์ 3", "10. ท่าเรือสะพานคลองตัน", "11. ท่าเรือชาญอิสระ", "12. ทองหล่อ,ท่าเรือ"
            , "13. สุเหร่าบ้านดอน", "14. วัดใหม่ช่องลม", "15. อิตัลไทยทาวเวอร์"
            , "16. ท่าเรือมศว ประสานมิตร", "17. สะพานอโศก", "18. นานาเหนือ", "19. สะพานวิทยุ"
            , "20. สะพานชิดลม", "21. ท่าเรือประตูน้ำ", "22. ท่าเรือประตูน้ำ", "23. หัวช้าง", "24. ชุมชนบ้านครัวเหนือ"
            , "25. สะพานเจริญผล", "26. ตลาดโบ้เบ้", "27. ผ่านฟ้าลีลาส", "28. สาทร(สะพานตากสิน, ท่าเรือกลาง)"
            , "29. โอเรียนเต็ล", "30. สี่พระยา", "31. ท่าเรือสวัสดี", "32. ราชวงศ์", "33. สะพานพุทธ"
            , "34. ท่าเตียน", "8. ท่าช้าง", "35. วังหลัง(ศิริราช)", "36. พระอาทิตย์", "37. เทเวศน์"
            , "38. สะพานกรุงธน", "39. พายัพ", "40. เกียกกาย", "41. บางโพ", "42. สะพานพระราม7"};

    private String[] boatLine1 = {"1. ท่าเรือวัดศรีบุญเรือง", "2. ท่าเรือบางกะปิ", "3. ท่าเรือเดอะมอลล์บางกะปิ",
            "4. ท่าเรือวัดกลาง", "5. สะพานมิตรมหาดไทย", "6. ม.รามคำแหง", "7. ท่าเรือวัดเทพลีลา", "8. รามคำแหง 29"
            , "9. เดอะมอลล์ 3", "10. ท่าเรือสะพานคลองตัน", "11. ท่าเรือชาญอิสระ", "12. ทองหล่อ,ท่าเรือ"
            , "13. สุเหร่าบ้านดอน", "14. วัดใหม่ช่องลม", "15. อิตัลไทยทาวเวอร์"
            , "16. ท่าเรือมศว ประสานมิตร", "17. สะพานอโศก", "18. นานาเหนือ", "19. สะพานวิทยุ"
            , "20. สะพานชิดลม", "21. ท่าเรือประตูน้ำ"};

    private String[] boatLine2 = {"1. ท่าเรือประตูน้ำ", "2. หัวช้าง", "3. ชุมชนบ้านครัวเหนือ",
            "4. สะพานเจริญผล", "5. ตลาดโบ้เบ้", "6. ผ่านฟ้าลีลาส"};


    private String[] boatLine3 = {"1. สาทร(สะพานตากสิน, ท่าเรือกลาง)", "2. โอเรียนเต็ล",
            "3. สี่พระยา", "4. ท่าเรือสวัสดี", "5. ราชวงศ์", "6. สะพานพุทธ", "7. ท่าเตียน", "8. ท่าช้าง"
            , "9. วังหลัง(ศิริราช)", "10. พระอาทิตย์", "11. เทเวศน์", "12. สะพานกรุงธน"
            , "13. พายัพ", "14. เกียกกาย", "15. บางโพ", "16. สะพานพระราม7"};


    private String[] mrt = {"1. บางซื่อ", "2. กำแพงเพชร", "3. สวนจตุจักร",
            "4. พหลโยธิน", "5. ลาดพร้าว", "6. รัชดาภิเษก", "7. สุทธิสาร", "8. ห้วยขวาง"
            , "9. ศูนย์วัฒนธรรม", "10. พระราม 9", "11. เพชรบุรี", "12. สุขุมวิท", "13. ศูนย์ประชุมแห่งชาติสิริกิติ์",
            "14. คลองเตย", "15. ลุมพินี", "16. สีลม", "17. สามย่าน", "18. หัวลำโพง"};

    private String[] airport = {"1. พญาไท", "2. ราชปรารภ", "3. มักกะสัน",
            "4. รามคำแหง", "5. หัวหมาก", "6. บ้านทับช้าง", "7. ลาดกระบัง", "8. สุวรรณภูมิ"};

    private String[] temple = {
            "ก. วัดกัลยาณมิตรวรมหาวิหาร(วัดซำปอกง)",
            "ค. วัดคฤหบดี",
            "ช. วัดชนะสงครามราชวรมหาวิหาร",
            "ต. วัดไตรมิตร(หลวงพ่อทองคำ)",
            "ท. วัดเทวราชกุญชรวรวิหาร",
            "บ. วัดบวรนิเวศราชวรวิหาร",
            "พ. วัดพระเชตุพนวิมลมังคลาราม(วัดโพธิ์)",
            "พ. วัดพระศรีรัตนศาสดาราม(วัดพระแก้ว)",
            "ย. วัดยานนาวา",
            "ร. วัดระฆังโฆสิตารามวรมหาวิหาร",
            "ร. วัดราชาธิวาสราชวรวิหาร",
            "ศ. วัดศาลเจ้าพ่อเสือ",
            "ศ. วัดศาลหลักเมือง กรุงเทพมหานคร",
            "ส. วัดสุทัศน์เทพวราราม",
            "ส. วัดสระเกศราชวรมหาวิหาร(ภูเขาทอง)",
            "ส. วัดสุวรรณารามราชวรวิหาร",
            "อ. วัดอมรินทรารามวรวิหาร",
            "อ. วัดอรุณราชวรารามวรมหาวิหาร(วัดแจ้ง)"};

    private String typeList;
    private int tempNum;
    private String[] lists;

    // ---------------  User Interface ---------------  //
    private ListView lv;    // List view
    private ArrayAdapter<String> adapter;   // Listview Adapter
    private ArrayList<HashMap<String, String>> locationName;   // ArrayList for Listview
    private TextView stationName;
    private ImageView btnClose;
    private ImageView sort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_station);
        getSupportActionBar().hide(); // hide ActionBar

        Bundle bundle = getIntent().getExtras();
        typeList = bundle.getString("typeList");

        sort = (ImageView) findViewById(R.id.sort);

        stationName = (TextView) findViewById(R.id.stationName);
        stationName.setText(typeList);

        lv = (ListView) findViewById(R.id.list_view);

        switch (typeList) {
            case "BRT station":
                adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.locationName, brt);
                tempNum = 1;
                break;
            case "BTS station":
                adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.locationName, btsMain);
                tempNum = 2;
                lists = new String[]{"สายสุขุมวิท", "สายสีลม", "ทั้งหมด"};
                break;
            case "Boat station":
                adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.locationName, boatMain);
                tempNum = 3;
                lists = new String[]{"สายนิด้า", "สายภูเขาทอง", "สายเจ้าพระยา", "ทั้งหมด"};
                break;
            case "MRT station":
                adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.locationName, mrt);
                tempNum = 4;
                break;
            case "Temples":
                adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.locationName, temple);
                tempNum = 5;
                break;
            case "Airport Rail Link":
                adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.locationName, airport);
                tempNum = 6;
                break;
        }

        lv.setAdapter(adapter);

        btnClose = (ImageView) findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        if (tempNum == 2 || tempNum == 3) {

            sort.setVisibility(View.VISIBLE);
            sort.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    switch (typeList.substring(0, 3)) {
                        case "BTS":
                            new AlertDialog.Builder(DetailStation.this).setItems(lists, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    switch (which) {
                                        case 0:
                                            adapter = new ArrayAdapter<String>(DetailStation.this, R.layout.list_item, R.id.locationName, btsLine1);
                                            break;
                                        case 1:
                                            adapter = new ArrayAdapter<String>(DetailStation.this, R.layout.list_item, R.id.locationName, btsLine2);
                                            break;
                                        case 2:
                                            adapter = new ArrayAdapter<String>(DetailStation.this, R.layout.list_item, R.id.locationName, btsMain);
                                            break;
                                    }
                                    dialog.cancel();
                                    lv.setAdapter(adapter);
                                    lv.invalidateViews();
                                }
                            }).show();
                            break;
                        case "Boa":
                            new AlertDialog.Builder(DetailStation.this).setItems(lists, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    switch (which) {
                                        case 0:
                                            adapter = new ArrayAdapter<String>(DetailStation.this, R.layout.list_item, R.id.locationName, boatLine1);
                                            break;
                                        case 1:
                                            adapter = new ArrayAdapter<String>(DetailStation.this, R.layout.list_item, R.id.locationName, boatLine2);
                                            break;
                                        case 2:
                                            adapter = new ArrayAdapter<String>(DetailStation.this, R.layout.list_item, R.id.locationName, boatLine3);
                                            break;
                                        case 3:
                                            adapter = new ArrayAdapter<String>(DetailStation.this, R.layout.list_item, R.id.locationName, boatMain);
                                            break;
                                    }
                                    dialog.cancel();
                                    lv.setAdapter(adapter);
                                    lv.invalidateViews();
                                }
                            }).show();
                            break;
                    }
                }
            });
        }

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String val = (String) parent.getItemAtPosition(position);

                Intent map = new Intent(getApplicationContext(), DetailMap.class);
                map.putExtra("location", val.substring(val.indexOf(" ") + 1));
                map.putExtra("typeMoveMap", tempNum);
                startActivity(map);
            }
        });
    }
}

