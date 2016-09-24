package com.blackcatwalk.sharingpower;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;

public class GoogleMapStreet extends AppCompatActivity {

    private ImageView btnClose;
    private SupportStreetViewPanoramaFragment streetViewFracment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_street);
        getSupportActionBar().hide();

        Bundle bundle = getIntent().getExtras();
        final double lat = bundle.getDouble("lat");
        final double lng = bundle.getDouble("lng");

        bindWidget();

        streetViewFracment.getStreetViewPanoramaAsync(new OnStreetViewPanoramaReadyCallback() {
            @Override
            public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
                showStreetView( new LatLng(lat, lng ),streetViewPanorama );
            }
        });


        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                onBackPressed();
            }
        });
    }

    private void bindWidget() {
        btnClose = (ImageView) findViewById(R.id.btnClose);
        streetViewFracment = (SupportStreetViewPanoramaFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.streetviewpanorama);
    }

    private void showStreetView(LatLng latLng, StreetViewPanorama streetViewPanorama) {
        if( streetViewPanorama == null )
            return;

        StreetViewPanoramaCamera.Builder builder = new StreetViewPanoramaCamera.Builder( streetViewPanorama.getPanoramaCamera() );
        builder.tilt( 0.0f );
        builder.zoom( 0.0f );
        builder.bearing( 0.0f );
        streetViewPanorama.animateTo( builder.build(), 0 );

        streetViewPanorama.setPosition( latLng, 300 );
        streetViewPanorama.setStreetNamesEnabled( true );
    }
}
