package com.blackcatwalk.sharingpower;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class Group_add_marker extends AppCompatActivity {

    private static final int TAKE_PICTURE = 100;
    public static final int REQUEST_GALLERY = 1;

    private ImageView image;
    private TextView delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_add_marker);
        getSupportActionBar().hide(); // hide ActionBar

        ImageView btnClose = (ImageView) findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        final EditText editText = (EditText) findViewById(R.id.editText);

        image = (ImageView) findViewById(R.id.image);

        delete = (TextView) findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete.setVisibility(View.GONE);
                image.setImageResource(0);
            }
        });

        LinearLayout uploadPic = (LinearLayout) findViewById(R.id.uploadPic);
        uploadPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] lists = {"ถ่ายรูป", "เลือกจากอัลบั้ม", "ยกเลิก"};
                new AlertDialog.Builder(Group_add_marker.this).setTitle("เพิ่มรูปภาพ").setItems(lists, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent;
                        switch (which) {
                            case 0:
                                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(intent, TAKE_PICTURE);
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

        Button btnSend = (Button) findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("fsafsafsaf", editText.getText().toString());
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
         //   Glide.with(this).load(selectedImage).into(image);
            delete.setVisibility(View.VISIBLE);
        }
    }
}



/*

     if (resultCode == RESULT_OK) {
            try {
                Uri imageUri = data.getData();
                InputStream imageStream = getContentResolver().openInputStream(imageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                selectedImage = getResizedBitmap(selectedImage, 400);// 400 is for example, replace with desired size

                image.setImageBitmap(selectedImage);
                delete.setVisibility(View.VISIBLE);


                //image.setImageBitmap(Control.decodeUri(getApplication(), selectedImage, 300));

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


       }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
*/
