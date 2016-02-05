package com.mad.umad.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.mad.umad.R;
import com.mad.umad.models.EventInfo;
import com.mad.umad.models.Helper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Schedule Adapter
 * This adapter is to organize the event information based on time and also allows for the time
 * headers to be sticky, in order for a better UI experience
 */
public class ScheduleAdapter extends BaseAdapter implements StickyListHeadersAdapter {

    private static final String TAG = "ScheduleAdapter";

    private String[] times;
    private LayoutInflater inflater;
    private ArrayList<EventInfo> events;

    public ScheduleAdapter(Context context, String[] times, ArrayList<EventInfo> events) {
        inflater = LayoutInflater.from(context);
        this.times = times;
        this.events = events;
    }

    private class HeaderViewHolder {
        TextView timeText;

        private HeaderViewHolder(View view) {
            timeText = (TextView) view.findViewById(R.id.textItem);
        }
    }

    private class ViewHolder {
        TextView text;
        TextView  sessionName;
        TextView timeText;
        ImageView sponsorIcon;
        TextView roomInfo;

        private ViewHolder(View view) {
            text = (TextView) view.findViewById(R.id.text);
            timeText = (TextView) view.findViewById(R.id.time_text);
            sessionName = (TextView) view.findViewById(R.id.subtitle_text);
            sponsorIcon = (ImageView) view.findViewById(R.id.sponsor_icon);
            roomInfo = (TextView) view.findViewById(R.id.roomInfo);
        }
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public Object getItem(int position) {
        return events.get(position).getSessionName();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.row_schedule_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        EventInfo event = events.get(position);

        // Update view with event info
        holder.text.setText(event.getCompanyName());
        holder.timeText.setText(event.getStartingTime() + " - " + event.getEndingTime());
        holder.sessionName.setText(event.getSessionName());
        holder.roomInfo.setText(event.getRoom());
//        Log.i(TAG, "getView: " + event.getRoom());
        ParseQuery<ParseObject> query2 = ParseQuery.getQuery("Company");
        query2.whereEqualTo("name", event.getCompanyName());
        query2.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if(e == null) {
                    ParseFile thumbnail = (ParseFile) parseObject.get("thumbnail");
                    thumbnail.getDataInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] bytes, ParseException e) {
                            if(e == null) {
                                holder.sponsorIcon.setImageBitmap(Helper.byteArrayToBitmap(bytes, 256, 256));
                            }
                        }
                    });
                }
            }
        });

        return convertView;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.row_schedule_time_header, parent, false);
            holder = new HeaderViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }
        holder.timeText.setText("" + events.get(position).getStartingTime());
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        try {
            cal.setTime(dateFormat.parse(events.get(position).getStartingTime()));
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return cal.getTime().getHours() + cal.getTime().getMinutes();
    }

}