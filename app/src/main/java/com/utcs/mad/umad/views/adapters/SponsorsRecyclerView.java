package com.utcs.mad.umad.views.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;
import com.utcs.mad.umad.R;
import com.utcs.mad.umad.models.CompanyInfo;
import com.utcs.mad.umad.models.Helper;

import java.util.ArrayList;
import java.util.List;


public class SponsorsRecyclerView extends RecyclerView.Adapter<SponsorsRecyclerView.ViewHolder>  {

    private static final String TAG = "SponsorsRecycler";
    private List<CompanyInfo> sponsors;
    private Context context;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView logo;

        public ViewHolder(View v) {
            super(v);

            logo = (ImageView) v.findViewById(R.id.sponsor_logo);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public SponsorsRecyclerView(List<CompanyInfo> sponsors, Context context) {
        this.sponsors = sponsors;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public SponsorsRecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sponsor_individual,
                parent, false);
        return new SponsorsRecyclerView.ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        attachImageToView(holder, sponsors.get(position).getName());
//        holder.logo.setBackgroundColor(context.getResources().getColor(R.color.primary_accent));
        holder.logo.setContentDescription(sponsors.get(position).getName());
        holder.logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(sponsors.get(position).getWebsite()));
                context.startActivity(browserIntent);
            }
        });

        ViewGroup.LayoutParams layoutParams = holder.logo.getLayoutParams();
        layoutParams.height = 300;
        holder.logo.setLayoutParams(layoutParams);
    }

    private void attachImageToView(final ViewHolder holder, String name) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Company");
        query.whereEqualTo("name", name);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if(e == null) {
                    ParseFile thumbnail = (ParseFile) parseObject.get("image");
                    thumbnail.getDataInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] bytes, ParseException e) {
                            if(e == null) {
                                holder.logo.setImageBitmap(Helper.byteArrayToBitmap(bytes, 1024, 1024));
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (sponsors == null) return 0;
        return sponsors.size();
    }

    @Override
    public int getItemViewType(int position) {
        return sponsors.get(position).getLevel();
    }
}