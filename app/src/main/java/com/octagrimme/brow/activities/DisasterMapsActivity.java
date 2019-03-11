package com.octagrimme.brow.activities;

import android.graphics.Color;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.octagrimme.brow.AddDisasterBottomFragment;
import com.octagrimme.brow.DialogueControlInterface;
import com.octagrimme.brow.R;
import com.octagrimme.brow.models.Information;
import com.octagrimme.brow.utils.UtilConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

public class DisasterMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;
    AddDisasterBottomFragment addDisasterBottomFragment;
    private static DialogueControlInterface dialogueControlInterface;
    private static final String TAG = "DisasterMapsActivity";
    HeatmapTileProvider mProvider;
    List<LatLng> heatMapData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disaster_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        heatMapData = new ArrayList<>();

        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference().child("DisasterInformation");

        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Information disaster = dataSnapshot.getValue(Information.class);
                String arr[] = UtilConstants.arr;
                float color[] = UtilConstants.color;
                int length = arr.length;
                if (Arrays.asList(arr).indexOf(disaster.getAlertType()) > 0) {
                    BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory
                            .defaultMarker(color[Arrays.asList(arr).indexOf(disaster.getAlertType())]);
                    LatLng latLng = new LatLng(disaster.getLatitude(), disaster.getLongitude());
                    heatMapData.add(latLng);
                    mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .icon(bitmapDescriptor).title(disaster.getAlertType()));
                    moveCamera(latLng, 15f);
                } else {
                    BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory
                            .defaultMarker(color[length-1]);
                    LatLng latLng = new LatLng(disaster.getLatitude(), disaster.getLongitude());
                    heatMapData.add(latLng);
                    mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .icon(bitmapDescriptor).title(disaster.getAlertType()));
                    moveCamera(latLng, 15f);
                }

                int[] colors = {
                        Color.GREEN,
                        Color.YELLOW,
                        Color.RED,
                        Color.BLUE
                };

                float[] startPoints = {
                        0.1f, 0.8f, 1.5f, 2f
                };

                Gradient gradient = new Gradient(colors, startPoints);

                mProvider = new HeatmapTileProvider.Builder()
                        .data(heatMapData)
                        .radius(40)
                        .gradient(gradient)
                        .opacity(0.7)
                        .build();
                // Add a tile overlay to the map, using the heat map tile provider.
                TileOverlay mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        dialogueControlInterface = () -> dismissDialogue();

        findViewById(R.id.fab_add_disaster).setOnClickListener(view -> openDialogue());

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        mDatabaseReference.addChildEventListener(mChildEventListener);
    }

    private void moveCamera(LatLng latLng, float zoom){
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    void openDialogue(){
        addDisasterBottomFragment = new AddDisasterBottomFragment();
        addDisasterBottomFragment.show(getSupportFragmentManager(), "AddDisaster");
    }

    void dismissDialogue(){
        addDisasterBottomFragment.dismiss();
    }

    public static DialogueControlInterface getDialogueControlInterface() {
        return dialogueControlInterface;
    }

}
