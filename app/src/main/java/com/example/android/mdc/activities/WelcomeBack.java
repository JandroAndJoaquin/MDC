package com.example.android.mdc.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.android.mdc.R;
import com.example.android.mdc.helpers.CircleTransform;
import com.example.android.mdc.models.JobsData;
import com.example.android.mdc.services.ApiParams;
import com.example.android.mdc.services.ApiService;
import com.example.android.mdc.utils.DensityCalculator;
import com.example.android.mdc.utils.SharedPreference;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WelcomeBack extends ActionBarActivity  implements NavigationView.OnNavigationItemSelectedListener {
    Context ctx; String userId;
    SharedPreference prefs;
    ImageView avatar_toolbar, avatar_navbar;
    TextView bar_header, navUserName, navUserEmail, progressCount, finishCount, startedCount, workingCount;
    RelativeLayout ringsContainer;
    Typeface robotoLight;
    DensityCalculator dCalculator;
    ActionBar actionBar;
    View viewActionBar, overlay;
    DrawerLayout drawer;
    ProgressBar progressBar;
    AlertDialog.Builder alertBuilder;
    ApiParams api = new ApiParams();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;
        setContentView(R.layout.activity_welcome_back);

        //initializing
        alertBuilder = new AlertDialog.Builder(ctx);
        prefs = new SharedPreference(ctx); userId = prefs.getValue("userId");
        dCalculator = new DensityCalculator(ctx);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        robotoLight = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
        progressCount = (TextView) findViewById(R.id.jobs_tile_progress_counter);
        finishCount = (TextView) findViewById(R.id.jobs_tile_finished_counter);
        startedCount = (TextView) findViewById(R.id.jobs_tile_started_counter);
        workingCount = (TextView) findViewById(R.id.jobs_tile_working_counter);
        ringsContainer = (RelativeLayout) findViewById(R.id.rings_container);
        actionBar = getSupportActionBar();
        viewActionBar = ((Activity) ctx).getLayoutInflater().inflate(R.layout.app_bar_custom, null);
        final Circles rings = new Circles(ctx, null);
        avatar_toolbar = (ImageView) viewActionBar.findViewById(R.id.user_avatar);
        bar_header = (TextView) viewActionBar.findViewById(R.id.app_bar_name);
        overlay = findViewById(R.id.welcome_loader_overlay);
        progressBar = (ProgressBar) findViewById(R.id.welcome_progress);

        //setting fonts
        ((TextView) findViewById(R.id.welcome_in_progress_leyend)).setTypeface(robotoLight);
        ((TextView) findViewById(R.id.welcome_finished_leyend)).setTypeface(robotoLight);
        ((TextView) findViewById(R.id.welcome_started_leyend)).setTypeface(robotoLight);
        ((TextView) findViewById(R.id.welcome_working_leyend)).setTypeface(robotoLight);
        progressCount.setTypeface(robotoLight);
        finishCount.setTypeface(robotoLight);
        startedCount.setTypeface(robotoLight);
        workingCount.setTypeface(robotoLight);



        ringsContainer.addView(rings);
        trygetJobsData(prefs.getValue("userToken"), progressCount, finishCount, startedCount, workingCount, rings, ringsContainer);
        //actionBar.setBackgroundDrawable(new ColorDrawable(0xaa000000));
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
        if(userId!=null && !userId.equals("")){
            //this line has to be twice (Picasso Bug);
            Picasso.with(ctx).load(api.getBaseUrl()+"avatar/"+userId).transform(new CircleTransform()).into(avatar_toolbar);
            Picasso.with(ctx).load(api.getBaseUrl()+"avatar/"+userId).transform(new CircleTransform()).into(avatar_toolbar);
            Target tgt = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    View header = navigationView.getHeaderView(0);
                    header.setBackground(new BitmapDrawable(bitmap));
//                    if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.JELLY_BEAN){
//                        header.setBackground(new BitmapDrawable(bitmap));
//                    }else{
////                        header.setBackgroundDrawable(new BitmapDrawable(bitmap));
//                    }
                }
                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    Log.v("Jandro", "Something failed here");
                }
                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {                }
            };
            Picasso.with(ctx).load(api.getBaseUrl()+"bdrop/"+userId).into(tgt);
            Picasso.with(ctx).load(api.getBaseUrl()+"bdrop/"+userId).into(tgt);
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
            //go to home, since we're on it don't do nothing
        } else if (id == R.id.jobs) {

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

    public void trygetJobsData(String token, final TextView progress, final TextView finished, final TextView started, final TextView working, final Circles rings, final ViewGroup parent){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://jandrorojas.xyz/api/jobsdata?token="+token, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode==200){
                    try{
                        JSONObject response = new JSONObject(new String(responseBody));
                        int total = response.getInt("total"); int nProgress = response.getInt("inProgress"); int nWorking = response.getInt("working");
                        int nfinished = response.getInt("finished"); int nstarted = response.getInt("started");
                        progress.setText(Integer.toString(nProgress));
                        finished.setText(Integer.toString(nfinished));
                        started.setText(Integer.toString(nstarted));
                        working.setText(Integer.toString(nWorking));
                        CircleAngleAnimation animation;
                        if(total>0){
                            float p = nProgress==0?-358:calcAngle(total,nProgress);
                            float f = nfinished==0?-358:calcAngle(total,nfinished);
                            float s = nstarted==0?-358:calcAngle(total,nstarted);
                            float w = nWorking==0?-358:calcAngle(total,nWorking);
                            animation = new CircleAngleAnimation(rings, p, f, s, w, total);
                        }else{
                            animation = new CircleAngleAnimation(rings, -358, -358,-358,-358, 0);
                        }
                        animation.setDuration(1000);
                        rings.startAnimation(animation);
                    }catch(JSONException e){
                        alertBuilder.setTitle(R.string.error_getting_user_data).setMessage(R.string.error_getting_user_description+" Error Message: "+e.getMessage()).setPositiveButton("OK", null).create().show();
                    }
                    toggleLoading(false);

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                startActivity(new Intent(ctx, LoginActivity.class));
            }
        });
    }
    public void getJobsData(String token, final TextView progress, final TextView finished, final TextView started, final TextView working, final Circles rings, final ViewGroup parent){
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://jandrorojas.xyz/public/").addConverterFactory(GsonConverterFactory.create()).build();
        final ApiService service = retrofit.create(ApiService.class);
        //TODO try fetching user data
        Call<JobsData> call = service.getJobsData("Bearer{"+token+"}");
        call.enqueue(new Callback<JobsData>() {
            @Override
            public void onResponse(Call<JobsData> call, Response<JobsData> response) {

                JobsData data = response.body();
                if(response.raw().code()==200){
                    toggleLoading(false);
                    int total = data.getTotal(); int nProgress = data.getInProgress(); int nWorking = data.getWorking();
                    int nfinished = data.getFinished(); int nstarted = data.getStarted();
                    progress.setText(Integer.toString(nProgress));
                    finished.setText(Integer.toString(nfinished));
                    started.setText(Integer.toString(nstarted));
                    working.setText(Integer.toString(nWorking));
                    CircleAngleAnimation animation;
                    if(total>0){
                        float p = nProgress==0?-358:calcAngle(total,nProgress);
                        float f = nfinished==0?-358:calcAngle(total,nfinished);
                        float s = nstarted==0?-358:calcAngle(total,nstarted);
                        float w = nWorking==0?-358:calcAngle(total,nWorking);
                        animation = new CircleAngleAnimation(rings, p, f, s, w, total);
                    }else{
                        animation = new CircleAngleAnimation(rings, -358, -358,-358,-358, 0);
                    }
                    animation.setDuration(1000);
                    rings.startAnimation(animation);


                }else if(response.raw().code()==401){
                    startActivity(new Intent(ctx, LoginActivity.class));
                }
            }

            @Override
            public void onFailure(Call<JobsData> call, Throwable t) {}
        });
    }

    public float calcAngle(int total, int part){
        return (360*part)/total - 360;
    }

    public class Circles extends View {

        private static final int START_ANGLE_POINT = -90;
        private static final int WIDTH=350, BX=0, BY=0, SEP=20;
        private final Paint paint, textPaint, textPaint2;

        private float blueAngle, greenAngle, redAngle, purpAngle;
        private int count;

        public Circles(Context context, AttributeSet attrs) {
            super(context, attrs);

            final int strokeWidth = 50;
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(strokeWidth);
            paint.setColor(0xee383838);

            textPaint = new Paint();
            textPaint.setColor(Color.WHITE);
            textPaint.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics()));
            textPaint.setTextAlign(Paint.Align.CENTER);
            Typeface robotoLight = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
            textPaint.setTypeface(robotoLight);
            //textPaint.setShadowLayer(50, 0, 0, 0xaaffffff);
            blueAngle = 0; greenAngle = 0; redAngle = 0; purpAngle = 0;

            textPaint2 = new Paint();
            textPaint2.setColor(Color.WHITE);
            textPaint2.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()));
            textPaint2.setTextAlign(Paint.Align.CENTER);
            textPaint2.setTypeface(robotoLight);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            Drawable blue = getResources().getDrawable(R.drawable.ring_blue);
            Drawable green = getResources().getDrawable(R.drawable.ring_green);
            Drawable red = getResources().getDrawable(R.drawable.ring_red);
            Drawable purple = getResources().getDrawable(R.drawable.ring_purple);


            blue.setBounds(BX, BY, WIDTH, WIDTH);
            blue.draw(canvas);
            canvas.drawArc(new RectF(BX+25, BX+25, WIDTH-25, WIDTH-25), START_ANGLE_POINT, blueAngle, false, paint);

            green.setBounds(BX+SEP, BY+SEP, WIDTH-SEP, WIDTH-SEP);
            green.draw(canvas);
            canvas.drawArc(new RectF(BX+SEP+25, BY+SEP+25, WIDTH-SEP-25, WIDTH-SEP-25), START_ANGLE_POINT, greenAngle, false, paint);

            red.setBounds(BX+2*SEP, BY+2*SEP, WIDTH-2*SEP, WIDTH-2*SEP);
            red.draw(canvas);
            canvas.drawArc(new RectF(BX+2*SEP+25, BY+2*SEP+25, WIDTH-2*SEP-25, WIDTH-2*SEP-25), START_ANGLE_POINT, redAngle, false, paint);

            purple.setBounds(BX+3*SEP, BY+3*SEP, WIDTH-3*SEP, WIDTH-3*SEP);
            purple.draw(canvas);
            canvas.drawArc(new RectF(BX+3*SEP+25, BY+3*SEP+25, WIDTH-3*SEP-25, WIDTH-3*SEP-25), START_ANGLE_POINT, purpAngle, false, paint);

            canvas.drawText(Integer.toString(count), WIDTH/2, canvas.getHeight()/2-40, textPaint);
            canvas.drawText("JOBS", WIDTH/2, canvas.getHeight()/2+0, textPaint2);
        }

        public float getBlueAngle() {
            return blueAngle;
        }

        public void setBlueAngle(float angle) {
            this.blueAngle = angle;
        }

        public float getGreenAngle() {  return greenAngle; }

        public void setGreenAngle(float greenAngle) { this.greenAngle = greenAngle;  }

        public float getRedAngle() { return redAngle; }

        public void setRedAngle(float redAngle) { this.redAngle = redAngle; }

        public float getPurpAngle() {  return purpAngle; }

        public void setPurpAngle(float purpAngle) { this.purpAngle = purpAngle; }

        public int getCount() { return count; }

        public void setCount(int count) { this.count = count; }
    }

    public class CircleAngleAnimation extends Animation {

        private Circles circle;

        private float oldBAngle,newBAngle, oldGAngle, newGAngle,
                oldRAngle, newRAngle, oldPAngle, newPAngle;
        private int oldCount, newCount;

        public CircleAngleAnimation(Circles circle, float newBAngle, float newGAngle, float newRAngle, float newPAngle, int count) {
            this.oldBAngle = circle.getBlueAngle();
            this.newBAngle = newBAngle;
            this.oldGAngle = circle.getGreenAngle();
            this.newGAngle = newGAngle;
            this.oldRAngle = circle.getRedAngle();
            this.newRAngle = newRAngle;
            this.oldPAngle = circle.getPurpAngle();
            this.newPAngle = newPAngle;
            this.oldCount = circle.getCount();
            this.newCount = count;
            this.circle = circle;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation transformation) {
            float bAngle = oldBAngle + ((newBAngle - oldBAngle) * interpolatedTime);
            float gAngle = oldGAngle + ((newGAngle - oldGAngle) * interpolatedTime);
            float rAngle = oldRAngle + ((newRAngle - oldRAngle) * interpolatedTime);
            float pAngle = oldPAngle + ((newPAngle - oldPAngle) * interpolatedTime);
            float nCount = oldCount + ((newCount - oldCount) * interpolatedTime);
            circle.setBlueAngle(bAngle);
            circle.setGreenAngle(gAngle);
            circle.setRedAngle(rAngle);
            circle.setPurpAngle(pAngle);
            circle.setCount((int) nCount);
            circle.requestLayout();
        }
    }

    public void toggleLoading(Boolean toShow){
        if(toShow){
            //overlay.setVisibility(View.INVISIBLE);
            overlay.setVisibility(View.VISIBLE);
            //progressBar.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }else{
            progressBar.setVisibility(View.INVISIBLE);
            overlay.setVisibility(View.INVISIBLE);
            //progressBar.setVisibility(View.GONE);
            //overlay.setVisibility(View.GONE);
        }
    }
}
