package com.example.android.mdc.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.mdc.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jandro on 3/2/2017.
 */

public class JobsListAdapter extends RecyclerView.Adapter<JobsListAdapter.JobsHolder>{

    private JSONArray data;
    private LayoutInflater inflater;

    public JobsListAdapter(JSONArray data, Context ctx){
        this.data = data;
        inflater = LayoutInflater.from(ctx);
    }

    @Override
    public JobsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.job_list_item, parent, false);
        return new JobsHolder(view);
    }

    @Override
    public void onBindViewHolder(JobsHolder holder, int position) {
        try{
            JSONObject job = data.getJSONObject(position);
            holder.jobName.setText(job.getString("Name"));
            holder.address.setText(job.getString("Address"));
            holder.logo.setImageResource(R.drawable.surveyor_dflt);
            switch(job.getString("Status")){
                case "1":
                    //status in-progress #4fc3f7
                    holder.status.setBackgroundColor(Color.parseColor("#4fc3f7"));
                    break;
                case "2":
                    //status finished #4caf50
                    holder.status.setBackgroundColor(Color.parseColor("#4caf50"));
                    break;
                case "3":
                    //status started #f44336
                    holder.status.setBackgroundColor(Color.parseColor("#f44336"));
                    break;
                case "4":
                    //status working #9c27b0
                    holder.status.setBackgroundColor(Color.parseColor("#9c27b0"));
                    break;
            }

        }catch(JSONException e){
            Log.v("Jandro", e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return data.length();
    }

    class JobsHolder extends RecyclerView.ViewHolder{
        private TextView jobName;
        private ImageView logo;
        private TextView address;
        private View status;

        public JobsHolder(View itemView) {
            super(itemView);
            jobName = (TextView) itemView.findViewById(R.id.job_item_name);
            address = (TextView) itemView.findViewById(R.id.job_item_address);
            logo = (ImageView) itemView.findViewById(R.id.job_item_logo);
            status = itemView.findViewById(R.id.job_item_status);
        }
    }
}
