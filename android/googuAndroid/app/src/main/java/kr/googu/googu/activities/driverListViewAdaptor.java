package kr.googu.googu.activities;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import kr.googu.googu.R;
import kr.googu.googu.pojo.driverListViewPojo;

/**
 * Created by Jay on 2018-01-17.
 */

public class driverListViewAdaptor extends
        RecyclerView.Adapter<driverListViewAdaptor.ViewHolder> {

    public List<driverListViewPojo> getmContacts() {
        return mContacts;
    }

    public void setmContacts(List<driverListViewPojo> mContacts) {
        this.mContacts = mContacts;
    }

    private List<driverListViewPojo> mContacts;
    private  Context mContext;

    public driverListViewAdaptor(List<driverListViewPojo> mContacts, Context mContext) {
        this.mContacts = mContacts;
        this.mContext = mContext;
    }

    public Context getmContext() {
        return mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.activity_driverpic_mapview,parent,false);

        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        driverListViewPojo contact = mContacts.get(position);

        TextView tv = holder.textTv;
        tv.setText(mContacts.get(position).getDriverId());
        holder.ib.setBackground(mContacts.get(position).getDriverImage());
        holder.ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Bundle bundle = new Bundle();
                //bundle.putParcelable("driverTmpInfo",mContacts.get(position));
                mContacts.get(position).getmMapActivity().clickedDriverMapPoiitem.setItemName(mContacts.get(position).getDriverId());
                mContacts.get(position).getmMapActivity().setClickDriverMapPoiItem(mContacts.get(position).getDriverLat(),mContacts.get(position).getDriverLon());
                //mContacts.get(position).getmMapActivity().viewDriverInfo(mContacts.get(position).getDriverId());
                mContacts.get(position).getmMapActivity().viewDriverInfo(mContacts.get(position).getDriverId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView textTv;
        public ImageButton ib;
        public ViewHolder(final View itemView) {
            super(itemView);

            textTv = (TextView) itemView.findViewById(R.id.testTextView);
            ib = (ImageButton) itemView.findViewById(R.id.iB_th_driverProfileImage);

        }
    }

}
