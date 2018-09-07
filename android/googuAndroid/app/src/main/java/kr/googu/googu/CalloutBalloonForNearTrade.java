package kr.googu.googu;



import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import net.daum.mf.map.api.CalloutBalloonAdapter;
import net.daum.mf.map.api.MapPOIItem;

import kr.googu.googu.pojo.NearTradeArr;

/**
 * Created by Jay on 2017-12-31.
 */

public class CalloutBalloonForNearTrade implements CalloutBalloonAdapter {
    private final View nearTradeBalloon;
    private LayoutInflater mLayoutInflater;
    NearTradeArr mNearTrade;


    public CalloutBalloonForNearTrade(Context context,NearTradeArr mNearTrade){
        this.mNearTrade=mNearTrade;
        mLayoutInflater=LayoutInflater.from(context);
        nearTradeBalloon = mLayoutInflater.inflate(R.layout.activity_custom_item_list, null);
    }

    @Override
    public View getCalloutBalloon(MapPOIItem mapPOIItem) {
        Log.i("CalloutBalloonForNear","돌았다돌았다"+nearTradeBalloon);
        ((TextView)nearTradeBalloon.findViewById(R.id.tvProductName)).setText(mNearTrade.getItemName());
        ((TextView)nearTradeBalloon.findViewById(R.id.tvProductPrice)).setText(mNearTrade.getItemPrice());
        ((TextView)nearTradeBalloon.findViewById(R.id.tvStartTime)).setText(mNearTrade.getItemDepTime());
        ((TextView)nearTradeBalloon.findViewById(R.id.tvArrivalPlace)).setText(mNearTrade.getItemArrPlace());
        ((TextView)nearTradeBalloon.findViewById(R.id.tvTradeNum)).setText(mNearTrade.getTradeNum());
        return nearTradeBalloon;
    }

    @Override
    public View getPressedCalloutBalloon(MapPOIItem mapPOIItem) {
        return null;
    }
}
