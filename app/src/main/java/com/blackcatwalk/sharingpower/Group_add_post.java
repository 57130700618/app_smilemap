package com.blackcatwalk.sharingpower;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class Group_add_post extends AppCompatActivity {

    private  static  final int TAKE_PICTURE = 100;
    public static final int REQUEST_GALLERY = 1;

    private ImageView image;
    private TextView delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_create);
        getSupportActionBar().hide();

        ImageView btnClose = (ImageView) findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        TextView head = (TextView) findViewById(R.id.head);
        head.setText("โพสต์ข้อความ");

        image = (ImageView) findViewById(R.id.image);

        delete = (TextView) findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete.setVisibility(View.GONE);
                image.setImageResource(0);
            }
        });


        final EditText groupName = (EditText) findViewById(R.id.groupName);
        groupName.setHint("ระบุชื่อกระทู้");

        final EditText groupDetail = (EditText) findViewById(R.id.groupDetail);


        LinearLayout uploadPic = (LinearLayout) findViewById(R.id.uploadPic);
        uploadPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] lists = {"ถ่ายรูป", "เลือกจากอัลบั้ม", "ยกเลิก"};
                new AlertDialog.Builder(Group_add_post.this).setTitle("เพิ่มรูปภาพ").setItems(lists, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent;
                        switch (which) {
                            case 0:
                                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(intent,TAKE_PICTURE);
                                break;
                            case 1:
                                intent = new Intent();
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                intent.setType("image/*");
                                startActivityForResult(Intent.createChooser(intent,
                                        "Select app to pick image"), REQUEST_GALLERY);
                                break;
                        }
                        dialog.cancel();
                    }
                }).show();
            }
        });

        Button btnCreate = (Button) findViewById(R.id.btnCreate);
        btnCreate.setText("โพสต์");
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d("fsafsafsaf",editText.getText().toString());
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
          //  Glide.with(this).load(selectedImage).into(image);
            delete.setVisibility(View.VISIBLE);
        }
    }
}
