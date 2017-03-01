package com.example.android.mdc.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.mdc.R;
import com.example.android.mdc.helpers.CircleTransform;
import com.example.android.mdc.services.ApiParams;
import com.example.android.mdc.utils.DensityCalculator;
import com.example.android.mdc.utils.SharedPreference;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import static com.example.android.mdc.R.id.map;

public class JobViewer extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    private GoogleMap mMap;
    SharedPreference prefs;
    Context ctx; String userId;
    ImageView avatar_toolbar, avatar_navbar, back_drop_navbar;
    TextView bar_header, navUserName, navUserEmail, progressCount, finishCount, startedCount, workingCount;
    DensityCalculator dcalc;
    android.support.v7.app.ActionBar actionBar;
    View viewActionBar;
    DrawerLayout drawer;
    Typeface robotoLight;
    ApiParams api=new ApiParams();
    ConstraintLayout infoPanel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;
        prefs = new SharedPreference(ctx); userId = prefs.getValue("userId");
        setContentView(R.layout.activity_test_maps);
        dcalc  = new DensityCalculator(ctx);
        infoPanel= (ConstraintLayout) findViewById(R.id.jobMap_infoContainer) ;
        robotoLight = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");


        Toolbar toolbar = (Toolbar) findViewById(R.id.mapToolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        viewActionBar = ((Activity) ctx).getLayoutInflater().inflate(R.layout.app_bar_custom, null);
        avatar_toolbar = (ImageView) viewActionBar.findViewById(R.id.user_avatar);
        bar_header = (TextView) viewActionBar.findViewById(R.id.app_bar_name);

        //setting fonts

        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        bar_header.setText(ctx.getResources().getString(R.string.welcome_back));

        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        actionBar.setCustomView(viewActionBar, params);

        //syncing toggle btn to the drawer state
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //navigation menu
        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View nav_header = navigationView.getHeaderView(0);
        navUserName = (TextView) nav_header.findViewById(R.id.nav_userName);
        navUserEmail = (TextView) nav_header.findViewById(R.id.nav_userEmail);
        avatar_navbar = (ImageView) nav_header.findViewById(R.id.nav_userAvatar);
        back_drop_navbar = (ImageView) nav_header.findViewById(R.id.back_drop_img);
        if(userId!=null && !userId.equals("")){
            //these lines have to come twice (Picasso Bug);
            //Picasso.with(ctx).load(api.getBaseUrl()+"avatar/"+userId).transform(new CircleTransform()).into(avatar_toolbar);
            //Picasso.with(ctx).load(api.getBaseUrl()+"avatar/"+userId).transform(new CircleTransform()).into(avatar_toolbar);
            Picasso.with(ctx).load(api.getBaseUrl()+"avatar/"+userId).error(R.drawable.user_unknown).transform(new CircleTransform()).into(avatar_navbar);
            Picasso.with(ctx).load(api.getBaseUrl()+"avatar/"+userId).error(R.drawable.user_unknown).transform(new CircleTransform()).into(avatar_navbar);
            Picasso.with(ctx).load(api.getBaseUrl()+"bdrop/"+userId).error(R.drawable.side_nav_bar).into(back_drop_navbar);
            Picasso.with(ctx).load(api.getBaseUrl()+"bdrop/"+userId).error(R.drawable.side_nav_bar).into(back_drop_navbar);
            navUserName.setText(prefs.getValue("userName"));
            navUserEmail.setText(prefs.getValue("userEmail"));
        }else{
            startActivity(new Intent(ctx, LoginActivity.class));
        }


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(map);
        mapFragment.getMapAsync(this);
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

        // Add a marker in Sydney and move the camera
        LatLng usa = new LatLng(42.877742, -97.380979);
        LatLng mrkr = new LatLng(25.715873, -80.322762);

        mMap.setPadding(0,Math.round(dcalc.getPxFromDp(56)),0,Math.round(dcalc.getScreenHeight("px")-dcalc.getPxFromDp(256)));

        mMap.addMarker(new MarkerOptions().position(mrkr).title("Job Name"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(usa));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mrkr, 15), 2000, new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                slideUpPanel(infoPanel);
            }

            @Override
            public void onCancel() {

            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.welcome_back, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.home) {
            //go to home, since we're on it don't do nothing
        } else if (id == R.id.jobs) {
            startActivity(new Intent(ctx, JobViewer.class));
        } else if (id == R.id.messages) {

        } else if (id == R.id.profile) {

        } else if(id==R.id.out){
            prefs.clearPrefs();
            startActivity(new Intent(ctx, LoginActivity.class));
        }
//        else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void slideUpPanel(ConstraintLayout layout){
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams)layout.getLayoutParams();
        params.topMargin = Math.round(dcalc.getPxFromDp(220));
        layout.setLayoutParams(params);
    }
}
