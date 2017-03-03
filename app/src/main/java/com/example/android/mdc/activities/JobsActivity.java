package com.example.android.mdc.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.mdc.R;
import com.example.android.mdc.adapters.JobsListAdapter;
import com.example.android.mdc.helpers.CircleTransform;
import com.example.android.mdc.services.ApiParams;
import com.example.android.mdc.utils.SharedPreference;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class JobsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    Context ctx; String userId, token;
    SharedPreference prefs;
    ImageView avatar_toolbar, avatar_navbar, back_drop_navbar;
    TextView bar_header, navUserName, navUserEmail;
    android.support.v7.app.ActionBar actionBar;
    View viewActionBar;
    DrawerLayout drawer;
    Typeface robotoLight;
    ApiParams api=new ApiParams();
    AlertDialog.Builder alertBuilder;
    RecyclerView recView;
    JobsListAdapter adapter;
    Integer page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobs);
        ctx = this;
        prefs = new SharedPreference(ctx);
        userId = prefs.getValue("userId");
        token = prefs.getValue("userToken");
        alertBuilder = new AlertDialog.Builder(ctx);

        Toolbar toolbar = (Toolbar) findViewById(R.id.jobsToolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        viewActionBar = ((Activity) ctx).getLayoutInflater().inflate(R.layout.app_bar_custom, null);
        avatar_toolbar = (ImageView) viewActionBar.findViewById(R.id.user_avatar);
        bar_header = (TextView) viewActionBar.findViewById(R.id.app_bar_name);

        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        bar_header.setText(ctx.getResources().getString(R.string.welcome_back));

        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        actionBar.setCustomView(viewActionBar, params);

        recView = (RecyclerView) findViewById(R.id.jobs_list);
        recView.setLayoutManager(new LinearLayoutManager(this));



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
        if(token!=null && userId!=null && !userId.equals("")){
            GetJobsByPage(token, page);
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
            startActivity(new Intent(ctx, WelcomeBack.class));
        } else if (id == R.id.jobs) {
            //go to jobs activity, since we're on it don't do nothing

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

    public void GetJobsByPage(String token, int page){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(api.getBaseUrl() + "jobs?page=" + Integer.toString(page)+"&token="+token, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode==200){
                    try{
                        JSONObject response = new JSONObject(new String(responseBody));
                        Log.v("Jandro", "Total "+response.getString("total"));
                        adapter = new JobsListAdapter(response.getJSONArray("data"), ctx);
                        recView.setAdapter(adapter);
                    }catch(JSONException e){
                        alertBuilder.setTitle(R.string.error_getting_user_data).setMessage(R.string.error_getting_user_description+" Error Message: "+e.getMessage()).setPositiveButton("OK", null).create().show();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if(statusCode==401){
                    startActivity(new Intent(ctx, LoginActivity.class));
                }
                Log.v("Jandro", error.getMessage());
            }
        });
    }
}
