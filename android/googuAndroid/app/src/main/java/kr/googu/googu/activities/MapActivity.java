package kr.googu.googu.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import android.view.View;
import android.widget.Button;

import android.widget.RelativeLayout;
import android.widget.Toast;


import net.daum.android.map.MapViewEventListener;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import kr.googu.googu.CalloutBalloonForNearTrade;
import kr.googu.googu.locationManager.LocationRequestor;
import kr.googu.googu.pojo.MapRepo;
import kr.googu.googu.pojo.MyActiveTradeDriver;
import kr.googu.googu.pojo.NearTradeArr;
import kr.googu.googu.R;
import kr.googu.googu.resourceController.ImageProcesser;
import kr.googu.googu.serverConnector.serverConnecterST;
import kr.googu.googu.resourceController.SharedPreferencesBeans;
import kr.googu.googu.pojo.TradeDepListPojo;
import kr.googu.googu.pojo.TradeDepPojo;
import kr.googu.googu.pojo.TradeLocList;
import kr.googu.googu.pojo.depUpdateListenerPojo;
import kr.googu.googu.staticVars.StaticVars;
import kr.googu.googu.activities.subActivities.driverInfoForMap;
import kr.googu.googu.pojo.driverListViewPojo;

/**
 * Created by Jay on 2017-12-10.
 */

public class MapActivity extends AppCompatActivity implements MapViewEventListener, MapView.POIItemEventListener {
    private static final String TAG ="맵 엑티비티";
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    LocationRequestor mLocationRequestor; //위치 요청 클래스
    Location mapCenterLocation; //현제 지도의 중앙을 뽑기 위해 쓰이는 변수.
    MapView mv; //다음에서 쓰는 MapView 지도를 그리는데 쓰이는 제일 바탕
    RelativeLayout mapViewContainer; // mv를 담고있는 객체
    MapPOIItem myMarker;    //내 위치 마커, 계속 쓰이기 때문에 field에 올라감

    private long mapUpdateFreq;
    private long myLocUpdateFreq;// 나중에 StaticVars로 밀어넣자

    MyHandler myHandler;    //맵에서 쓰이는 메인헨들러. 다른곳으로 뺄 수 없다.

    TreeMap<Integer,MapPOIItem> nearTradePOIItems; //주변 거래목록의 mv마커들
    TreeMap<String,MyActiveTradeDriver> myActiveTradeDriverList; //거래중인 드라이버 목록들,컨테이너는 pojo임.
    MapPOIItem [] tradeItemList;        //mv에 item들을 동시에 넣는방법은 배열만 가능하다, 그런 이유로 부득이하게 근방 거래목록을 하나 더 선언하는데, 직접적으로 쓰이는것은 add함수에서만 사용한다.
                                        //타 작업에 해당 배열을 사용하지 않기 때문에 신경 쓰지 않아도 될것.
    MapPOIItem clickedDriverMapPoiitem; //선택된 드라이버 의 위치를 지도에 띄울 mv마커
    Button btn_test;    //테스트용 버튼 만능이다.
    RecyclerView lv;     //우측에 나올 드라이버 이미지 목록들을 띄울 가장 껍대기 이다.
    ArrayList<driverListViewPojo> lvList;//위의 RecyclerView에 들어갈 ArrayList이다. 이것이 종말적으로 쓸모있는지 판단하여 없에던가 하자
    driverListViewAdaptor lvAdaptor;    //상단의 RecyclerView의 Adaptor이다. 뷰 안에 들어갈 자료들을 관리하는 클래스.

    Thread locationUpdateRepeater;
    List<String> updateRepeaterTradeList;   //빠른 업데이트에 필요한 목록들

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.findViewById(R.id.explicit_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MapActivity.this, "전환 버튼", Toast.LENGTH_SHORT).show();
            }
        });

        mapUpdateFreq = 5000;
        myLocUpdateFreq = 5000;

        lv = (RecyclerView) findViewById(R.id.testRecycleView);
        lv.setHasFixedSize(true);

        lv.setLayoutManager(new LinearLayoutManager(this));
        lvList = new ArrayList<>();
        lvAdaptor = new driverListViewAdaptor(lvList,this);
        lv.setAdapter(lvAdaptor);



        mapViewContainer = (RelativeLayout)findViewById(R.id.mapViewLayout);
        myHandler = new MyHandler(this);
        mLocationRequestor = new LocationRequestor(this,myHandler,getApplicationContext(),mapUpdateFreq,5000);
        myMarker = new MapPOIItem();


        nearTradePOIItems = new TreeMap<Integer,MapPOIItem>();
        myActiveTradeDriverList = new TreeMap<String,MyActiveTradeDriver>();
        clickedDriverMapPoiitem = new MapPOIItem();

        btn_test=(Button)findViewById(R.id.map_bar_test_btn);

        btn_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //updateDriverLocationView(37.0,129.0);
            }
        });
        updateRepeaterTradeList = new ArrayList<>();
        locationUpdateRepeater = new Thread(new serverConnecterST(myHandler,this){
            @Override
            public void run() {
                super.run();
                Log.i("fastLocThread","스렏드 시작됨. array 사이즈"+updateRepeaterTradeList.size());
                while(!updateRepeaterTradeList.isEmpty()){
                    Log.i(TAG,"updateRepeaterTradeList size is "+updateRepeaterTradeList.size());
                    this.myLocationUpdateRapidly(updateRepeaterTradeList,mapCenterLocation);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("fastLocThread","스렏드 종료됨. array 사이즈"+updateRepeaterTradeList.size());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mv = new MapView(this);
        mv.setMapType(MapView.MapType.Standard);
        mv.setPOIItemEventListener(this);
        mapViewContainer.addView(mv);
        mLocationRequestor.createLocationCallback();
        if(!mLocationRequestor.createLocationRequest(mapUpdateFreq,1000,1)){
            Log.i(TAG,"mLocationRequestor failed setting");
        }
        myMarker.setTag(0);
        myMarker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
        myMarker.setCustomImageResourceId(R.drawable.test3);
        myMarker.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
        myMarker.setCustomSelectedImageResourceId(R.drawable.test3);
        myMarker.setCustomImageAutoscale(false);
        myMarker.setItemName("myMarker");
        myMarker.setShowCalloutBalloonOnTouch(false);
        myMarker.setMapPoint(MapPoint.mapPointWithGeoCoord(0,0));
        mv.addPOIItem(myMarker);


        mLocationRequestor.buildLocationSettingsRequest();
        mLocationRequestor.getLastLocationForSendHandleMsg();


        Message msg = Message.obtain();
        Bundle bundle = new Bundle();
        bundle.putBoolean("setFlag",true);
        bundle.putLong("intervalFreq",myLocUpdateFreq);
        msg.setData(bundle);
        mLocationRequestor.getLocHandler().sendMessage(msg);

        clickedDriverMapPoiitem.setItemName("driver");
        clickedDriverMapPoiitem.setMarkerType(MapPOIItem.MarkerType.RedPin);

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG,"onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG,"onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG,"onStop");
        //스레드를 멈추자
        Message msg = Message.obtain();
        Bundle bundle = new Bundle();
        bundle.putBoolean("setFlag",false);
        bundle.putLong("intervalFreq",myLocUpdateFreq);
        msg.setData(bundle);
        mLocationRequestor.getLocHandler().sendMessage(msg);
        mLocationRequestor.setLocationRequesterPriority(3);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG,"onRestart");
    }

    public void viewDriverInfo(String driverId){
        Intent driverInfoPopup = new Intent(this,driverInfoForMap.class );
        driverInfoPopup.putExtra("driverId",driverId);
        startActivity(driverInfoPopup);
    } //선택된 드라이버의 정보를 팝업창에 띄우는 함수, intent를 띄우되, bundle로 값을 보낸다.

    public void updateDriverList(List<TradeLocList> list){
        for(int i = 0 ; i < list.size() ; i++){
            int nConut = 0;
            for(Map.Entry<String,MyActiveTradeDriver> entry : myActiveTradeDriverList.entrySet()){
                String key = entry.getKey();
                if(myActiveTradeDriverList.get(key).getTradeNum().equals(list.get(i).getTradeNum())){
                    myActiveTradeDriverList.get(key).setDriverLat(list.get(i).getLat());
                    myActiveTradeDriverList.get(key).setDriverLon(list.get(i).getLon());
                    if(!checkThisTradeStatusIsFour(list.get(i))){
                        updateRepeaterTradeList.remove(myActiveTradeDriverList.get(key).getTradeNum());
                    }
                    break;
                }else if(++nConut==myActiveTradeDriverList.size()){
                    Log.i(TAG,"myActiveTradeDriverList size :"+myActiveTradeDriverList.size()+ " and nCount is :"+nConut);
                    updateRepeaterTradeList.remove(key);
                }
            }
        }
    }

    private boolean checkThisTradeStatusIsFour(TradeLocList list){
        if(Integer.parseInt(list.getTradeStatus())==4){
            return true;
        }else{
            return false;
        }
    } //현제 거래번호가 4번인지 확인하는 함수, 굳이 뺄 이유가 있었니
    private boolean checkThisTradeStatusIsFour(MyActiveTradeDriver trade){
        if(trade.getTradeStatus().equals("4")){
            return true;
        }
        return false;
    }


    public void mapUpdateMyLocation(double lat, double lon){

        MapPOIItem[] list = mv.getPOIItems();
        for(MapPOIItem i : list){
            if(i.getItemName().equals("myMarker")){
                i.setMapPoint(MapPoint.mapPointWithGeoCoord(lat,lon));
            }
        }
    } //내 위치 마커를 업데이트 하는 함수.

    public void setActiveDriverStatus(List<MyActiveTradeDriver> mMyActiveTradeDriver) { // 스레드 요청으로 얻어온 내 거래에 등록된 드라이버 정보 리스트들이 여기에서 정리된다.
        if(mMyActiveTradeDriver.size()==0){
            removeAllMyActiveTradeDriverList(); //array를 비우고
            removeAllDriveRecyclerView();       //RecyclerView를 비운다.
            deleteClickDriverMapPoiItem();      //클릭된 드라어비 위치도 지워준다,

        }//내 거래중 드라이버가 없는경우, 보통 세션 로스트, 혹은 모든 거래가 끝날경우 나올것. 아무것도 없으면 루프를 못도니깐.
        List<String> newTradeNumList = new ArrayList<>(); // 신규거래들을 담는곳.
        if(myActiveTradeDriverList.isEmpty()){//첫 로딩이거나, 이전에 거래중이 아니었을때에 호출
            for(int i=0 ; i < mMyActiveTradeDriver.size() ; i ++){
                myActiveTradeDriverList.put(mMyActiveTradeDriver.get(i).getDriverId(),mMyActiveTradeDriver.get(i));
                addDriverRecyclerView(mMyActiveTradeDriver.get(i));
                if(mMyActiveTradeDriver.get(i).getTradeStatus().equals("4")){
                    newTradeNumList.add(mMyActiveTradeDriver.get(i).getTradeNum()); // 클라이언트에서는 존재하지 않던 거래목록이기에, 스케쥴 업데이트가 필요하다, 그런고로 업데이트를 위해 리스트에 해당 거래 번호를 입력한다.
                    //requestTradeScadule(mMyActiveTradeDriver.get(i).getTradeNum());//첫 입력이기 때문에 모든 거래건에 스케쥴 요청을 보낸다.//추후 수정해서 일괄전송으로 바꾸자!!!!!
                }
            }
        }else{//우선 새롭게 온 req목록을 확인하여, 새로운 드라이버 가 있는지 검사를 한다.
            for(int i = 0 ; i < mMyActiveTradeDriver.size() ; i ++) {
                String key1 = mMyActiveTradeDriver.get(i).getDriverId();
                if(mMyActiveTradeDriver.get(i).getDriverId().equals(clickedDriverMapPoiitem.getItemName())){ // 만약 mv에 드라이버가 선택되어있을때, 그리고 그 값이 요청목록에 있을때
                    updateClickDriverMapPoiItem(mMyActiveTradeDriver.get(i).getDriverId(),Double.parseDouble(mMyActiveTradeDriver.get(i).getDriverLat()),Double.parseDouble(mMyActiveTradeDriver.get(i).getDriverLon()));
                }//mv에 선택된 드라이버 pin의 위치를 업데이트한다.

                if (!myActiveTradeDriverList.containsKey(key1)) {       // 만일 새로운 드라이버라면 추가해주며
                    addMyActiveTradeDriverList(key1,mMyActiveTradeDriver.get(i));       //array에 값 추가
                    addDriverRecyclerView(mMyActiveTradeDriver.get(i));                 //RecyclerView에 값 추가
                    if(checkThisTradeStatusIsFour(mMyActiveTradeDriver.get(i))) {       //해당 거래의 상태번호가 4인지 확인한다.
                        newTradeNumList.add(mMyActiveTradeDriver.get(i).getTradeNum()); // 이전에는 상태번호가 4가 아니었기 때문에, 이 또한 스케쥴 업데이트가 필요하다, 그런고로 업데이트를 위해 리스트에 해당 거래 번호를 입력한다.
                    }
                }else{                                                       //없는경우는(즉 중복인 경우) 위치 업데이트 시켜준다.
                    updateDriverLocationMyActiveTradeDriverList(key1,mMyActiveTradeDriver.get(i).getDriverLat(),mMyActiveTradeDriver.get(i).getDriverLon());    //driverArray에 값을 업데이트하고
                    updateDriverRecyclerViewLocation(key1,mMyActiveTradeDriver.get(i).getDriverLat(),mMyActiveTradeDriver.get(i).getDriverLon());               //우측의RecyclerView에 값을 업데이트한다.
                }
            }
            for (Map.Entry<String, MyActiveTradeDriver> entry : myActiveTradeDriverList.entrySet()) { //이후 반대로 req목록을 비교하여, 더이상 저장되어있는 리스트의 드라이버가 없다면, 해제해 준다
                String key1 = entry.getKey();
                for(int i = 0 ; i < mMyActiveTradeDriver.size() ; i ++) {
                    String key2 = mMyActiveTradeDriver.get(i).getDriverId();
                    if(key1.equals(key2)){//아직까지 드라이버가 활동중일때는 넘어가며
                        break;
                    }else if(i==mMyActiveTradeDriver.size()-1) {//더이상 리스트에 드라이버가 없으면 해제한다
                        deleteDriverMyActiveTradeDriverList(key1);                       //myActiveTradeDriverList 에 값을 지우고
                        removeDriverRecyclerView(myActiveTradeDriverList.get(key1));//동일한 드라이버 정보를 RecyclerView에도 제거한다.

                    }
                }
            }// 그 다음, 현제 모든 드라이버들의 status를 검사하여, 5이며, updateRepeaterTradeList 리스트에 해당 드라이버의 거래가 있는경우엔, 그 거래를 지운다.
            //여기서는 이미 클릭된 clickedDriverMapPoiitem 의 값을 바꾸어야 하는데, 어디어세 중복이 된다.
        }
//        for(int i = 0 ; i < mMyActiveTradeDriver.size() ; i++){ // 한번 더 글
//            if(mMyActiveTradeDriver.get(i).getDriverId().equals(clickedDriverMapPoiitem.getItemName())){
//                updateClickDriverMapPoiItem(mMyActiveTradeDriver.get(i).getDriverId(),Double.parseDouble(mMyActiveTradeDriver.get(i).getDriverLat()),Double.parseDouble(mMyActiveTradeDriver.get(i).getDriverLon()));
//            }
//        }
        if(!newTradeNumList.isEmpty()){//만일 신규 드라이버가 이 함수에서 나온다면,
            requestTradeListScadule(newTradeNumList);//신규 드라이버에 물린 거래건들을 모아 스케쥴 일괄요청을 시작한다.
        }
    }
    private void updateDriverPinLocation(){
        String targetDriverId = clickedDriverMapPoiitem.getItemName();
        if(myActiveTradeDriverList.containsKey(targetDriverId)){
            clickedDriverMapPoiitem.setMapPoint(MapPoint.mapPointWithGeoCoord(Double.parseDouble(myActiveTradeDriverList.get(targetDriverId).getDriverLat()),Double.parseDouble(myActiveTradeDriverList.get(targetDriverId).getDriverLon())));

        }

    }

    private void updateDriverRecyclerationView(String driverId,Double driverLat, Double driverLon){
        MapPOIItem[] list = mv.getPOIItems();
        for(int i = 0  ; i < list.length ; i ++){
            if(list[0].getItemName().equals(driverId)) {
                list[0].setMapPoint(MapPoint.mapPointWithGeoCoord(driverLat, driverLon));
            }
        }
    }

    private void deleteClickDriverMapPoiItem(){ //선태고딘 드라이버의 위치를 지도에 지우는 함수.
        try{
            mv.removePOIItem(clickedDriverMapPoiitem);
        }catch (Exception e){
            Log.i(TAG,"선택된드라이버 맵 포인트 제거시도,실패. 없는건가 안지워진건가");
        }
    }

    private void updateClickDriverMapPoiItem(String driverId,Double driverLat, Double driverLon){
        clickedDriverMapPoiitem.setItemName(driverId);
        clickedDriverMapPoiitem.setMapPoint(MapPoint.mapPointWithGeoCoord(driverLat,driverLon));
    }

    public void setClickDriverMapPoiItem(Double lat, Double lon){ //선택된 드라이버의 위치를 지도에 띄우는 함수.
        deleteClickDriverMapPoiItem();  //혹시모르니깐 지워주고
        mv.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(lat,lon),true);   //선택된 거래의 좌표로 맵 중심점을 바꾼다.
        clickedDriverMapPoiitem.setMapPoint(MapPoint.mapPointWithGeoCoord(lat,lon));//선택된 거래의mv poiitem 의 좌표를 제설정 해주며,
        mv.addPOIItem(clickedDriverMapPoiitem);                                     //다시 지도위에 띄운다.
    }

    //////////////////// myActiveTradeDriverList 리스트를 추가 수정 삭제 하는 함수들 /////////////////////////

    private void removeAllMyActiveTradeDriverList(){
        myActiveTradeDriverList.clear();
    }

    private void deleteDriverMyActiveTradeDriverList(String key){ //
        myActiveTradeDriverList.remove(key);
    }

    private void updateDriverLocationMyActiveTradeDriverList(String driverId,String driverLat, String driverLon){
        myActiveTradeDriverList.get(driverId).setDriverLat(driverLat);
        myActiveTradeDriverList.get(driverId).setDriverLon(driverLon);
    } //myActiveTradeDriverList 에 들어있는 드라이버의 좌표 업데이트

    private void addMyActiveTradeDriverList(String key,MyActiveTradeDriver pojo){
        myActiveTradeDriverList.put(key, pojo);
    }

    ////////////////////////////recyclerView는 화면 우측에 뜨는 드라이버 프로필 목록입니다. ////////////////////////////////////
    //해당 목록에 뜨는 드라이버는 상태번호 4번과 5번일때 나타나며, 해당 드라이버를 클릭하면, 드라이버의 위치를 지도에 표시 해 줍니다 ///
    private void removeAllDriveRecyclerView(){

        Iterator<driverListViewPojo> itr = lvAdaptor.getmContacts().iterator();
        while(itr.hasNext()){
            lvAdaptor.getmContacts().remove(itr.next());
        }
        lvAdaptor.notifyDataSetChanged();
//        for(driverListViewPojo i : lvList){
//            lvList.remove(lvList.indexOf(i));
//        }
        lv.setAdapter(new driverListViewAdaptor(lvList,this));
    }//드라이버 리스트 전체 제거 함수
    private void removeDriverRecyclerView(MyActiveTradeDriver myActiveTradeDriver) {
        for(driverListViewPojo i : lvList){
            if(i.getDriverId().equals(myActiveTradeDriver.getDriverId())){
                lvAdaptor.getmContacts().remove(i);
                lvAdaptor.notifyDataSetChanged();
                //lvList.remove(lvList.indexOf(i));
                //lv.setAdapter(new driverListViewAdaptor(lvList,this));
            }
        }
    }//드라이버 리스트 제거 함수

    private  boolean  updateDriverRecyclerViewLocation(String targetDriverId, String lat, String lon){ // recyclerView에 해당하는 드라이버의 좌표값을 변경한다.
        for( driverListViewPojo i : lvAdaptor.getmContacts()){
            if(i.getDriverId().equals(targetDriverId)){
                i.setDriverLat(Double.parseDouble(lat));
                i.setDriverLon(Double.parseDouble(lon));
                return true;
            }
        }
        return false;
    }

    public void addDriverRecyclerView(MyActiveTradeDriver driverPojo){
        byte[] decodedString = Base64.decode(driverPojo.getDriverTprofile(),Base64.DEFAULT);
        Bitmap rounded = ImageProcesser.getRoundedShape(BitmapFactory.decodeByteArray(decodedString,0,decodedString.length));
        Drawable d = new BitmapDrawable(getResources(),rounded);
        Double driverLat = Double.parseDouble(driverPojo.getDriverLat());
        Double driverLon = Double.parseDouble(driverPojo.getDriverLon());
        lvAdaptor.getmContacts().add(new driverListViewPojo(getApplicationContext(),d,driverPojo.getDriverId(),driverLat,driverLon,this));
        //lvList.add(new driverListViewPojo(getApplicationContext(),d,driverPojo.getDriverId(),driverLat,driverLon,this));
        //lv.setAdapter(new driverListViewAdaptor(lvList,this));
        lvAdaptor.notifyDataSetChanged();
    }//드라이버 리스트 추가 함수

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void setNearTrade(MapRepo pojo){
        if(pojo.getNearTradeArr().size()==0){//근처에 거래가 하나도 없는경우!
            for(Map.Entry<Integer,MapPOIItem> entry : nearTradePOIItems.entrySet()){
                mv.removePOIItem(entry.getValue());
            }
            nearTradePOIItems.clear();
        }
        if(nearTradePOIItems.isEmpty()){ // 첫 로딩이나, 근방에 아무 거래도 없었다면 이곳을 방문
            tradeItemList = new MapPOIItem[pojo.getNearTradeArr().size()];
            for(int i=0 ; i < pojo.getNearTradeArr().size(); i++){
                MapPOIItem item = addItemForNearTrade(pojo.getNearTradeArr().get(i));
                nearTradePOIItems.put(Integer.parseInt(pojo.getNearTradeArr().get(i).getTradeNum()),item);
                tradeItemList[i]=item;
            }
            mv.addPOIItems(tradeItemList);
        }else{
            //pojo를 돌리면서 map에 있는지 검사, 있으면 패스 없으면 추가
            //이후 맵을 돌면서 pojo가 있는지 검사, 만약 pojo에 없으면 삭제,
            //이 삭제는 tradeItemList 에 맞는 거래건도 mv로 지운다,

            for(int i=0 ; i < pojo.getNearTradeArr().size(); i++) {
                if(!nearTradePOIItems.containsKey(Integer.parseInt(pojo.getNearTradeArr().get(i).getTradeNum()))){ //트리맵 안에 해당하는 키가 없는 경우

                    MapPOIItem item = addItemForNearTrade(pojo.getNearTradeArr().get(i));
                    nearTradePOIItems.put(Integer.parseInt(pojo.getNearTradeArr().get(i).getTradeNum()),item);
                    mv.addPOIItem(item);
                }
                //트리맵 안에 해당하는 키가 있는경우에는 그냥 지나간다.
            }

            Collection<Integer> deleteList = new ArrayList<>();
            for(Map.Entry<Integer,MapPOIItem> entry : nearTradePOIItems.entrySet()){
                int key1 = entry.getKey();
                for(int i=0 ; i < pojo.getNearTradeArr().size(); i++) {
                    int key2 = Integer.parseInt(pojo.getNearTradeArr().get(i).getTradeNum());
                    if(key1==key2){
                        break;
                    }else if(i==pojo.getNearTradeArr().size()-1){ //어딜뒤져도 없는 상태에욧! 이때는 제거할것
                        deleteList.add(key1);
                    }
                }
            }
            if(!deleteList.isEmpty()){ // collection에 값이 있는경우, 즉 지워야 할게 존재하는 경우, 순차적으로 모두 제거하준다.
                for(int i : deleteList){
                    MapPOIItem poiitem = nearTradePOIItems.get(i);
                    mv.removePOIItem(poiitem);
                    nearTradePOIItems.remove(i);
                }
            }
        }
    }
    private void checkTradeScadule(){ // 거래건이 4단계일때, 스케쥴이 등록되었는지를 검사하는곳. 검사 완료후, 없는 경우엔 새롭게 추가를 한다.

    }
    private void requestTradeListScadule(List<String> tradeNumList1){  // 미구현, 단일이 아닌 모든 객체를 요청하는 방식이다.
        final List<String> tradeNumList = tradeNumList1;
        Thread th = new Thread(new serverConnecterST(myHandler,this){
            @Override
            public void run() {
                super.run();
                this.getPickupListTime(tradeNumList);
            }
        });
        th.start();
    }
    private void requestTradeScadule(String tradeNum){ //스케쥴을 등록하는곳.
        final String tradeNumm = tradeNum;
        Thread th = new Thread(new serverConnecterST(myHandler,this){
            @Override
            public void run() {
                super.run();
                this.getPickupTime(tradeNumm);
            }
        });
        th.start();
    }
    private void addTradeScaduleList(TradeDepListPojo pojo) {
        for(TradeDepPojo i : pojo.getTradeDepPojo()){
            addTradeScadule(i);
        }
    }
    private  void  addTradeScadule(TradeDepPojo pojo){
        if(pojo.getTradeDepTime()==null || pojo.getTradeNum() == null){
            Log.i(TAG,"addTradeScadule 에서 문제발생, null data. tradeNum "+pojo.getTradeNum()+" tradeDepTime "+pojo.getTradeDepTime());
            return;
        }
        TimerTask mTask = new TimerTask() {
            @Override
            public void run() {
                locationUpdateRepeater.start();
            }
        };
        long nowtime = System.currentTimeMillis()/1000L;
        long tradeDepTime = Long.valueOf(pojo.getTradeDepTime()).longValue();

        long alarmTime = (tradeDepTime - StaticVars.getDepartTimer5min() - nowtime);

        updateRepeaterTradeList.add(pojo.getTradeNum());
        if(alarmTime < StaticVars.getAllowableErrorTime5sec()){ // 오차 이내로 남은시간일 경우, 타이머를 쓰지 않고 업데이트 요청
            if(!locationUpdateRepeater.isAlive()){
                locationUpdateRepeater = new Thread(new serverConnecterST(myHandler,this){
                    @Override
                    public void run() {
                        while(!updateRepeaterTradeList.isEmpty()){
                            this.myLocationUpdateRapidly(updateRepeaterTradeList,mapCenterLocation);
                            try {
                                Thread.sleep(StaticVars.getRapidUpdateDelay());
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                locationUpdateRepeater.start();
            }
            else{
            }
        }else{ // 남은시간이 길기 때문에 타이머를 이용
            Timer timer = new Timer();
            timer.schedule(mTask,alarmTime*1000);
        }
    }
    private MapPOIItem addItemForNearTrade(NearTradeArr mNearTradeArr){
        MapPOIItem item =new MapPOIItem();
        item.setItemName(mNearTradeArr.getTradeNum());
        item.setMapPoint(MapPoint.mapPointWithGeoCoord(Double.parseDouble(mNearTradeArr.getLat()),Double.parseDouble(mNearTradeArr.getLon())));
        item.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
        item.setCustomCalloutBalloon(new CalloutBalloonForNearTrade(this,mNearTradeArr).getCalloutBalloon(item));
        return item;
    }
    @Override
    public void onLoadMapView() {
        Log.i(TAG,"맵 로딩 성공");
    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }



    private static class MyHandler extends Handler{
        private final WeakReference<MapActivity> mActivity;
        private SharedPreferencesBeans spb;
        public Thread th;
        private MyHandler(MapActivity mActivity) {
            this.mActivity = new WeakReference<MapActivity>(mActivity);
            spb = new SharedPreferencesBeans(mActivity);
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.getData().getInt("result")){
                case 511:{
                    Log.i(TAG,"resultcode 511, proceed");
                    mActivity.get().mapUpdateMyLocation(msg.getData().getDouble("locationLat"),msg.getData().getDouble("locationLon"));
                    mActivity.get().mapCenterLocation = msg.getData().getParcelable("location");
                    th = new Thread(new serverConnecterST(mActivity.get().myHandler,mActivity.get().getApplicationContext()){
                        @Override
                        public void run() {
                            super.run();
                            this.mapRefresh(mActivity.get().mapCenterLocation.getLatitude(),mActivity.get().mapCenterLocation.getLongitude(),mActivity.get().mv.getZoomLevel());
                        }
                    },"mapRefreshTh");
                    th.start();
                    return;
                }
                case 512:{
                    Log.i(TAG,"resultcode 512, break");
                    break;
                }
                case 513:{
                    Log.i(TAG,"resultcode 513, break");
                    break;
                }
                case 514:{
                    Log.i(TAG,"resultcode 514, break");
                    break;
                }
                case 515:{
                    Log.i(TAG,"resultcode 515, proceed");
                    mActivity.get().mv.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(msg.getData().getDouble("locationLat"),msg.getData().getDouble("locationLon")),false);

                    break;
                }
                case 516:{
                    Log.i(TAG,"resultcode 516, break");
                    break;
                }
                case 521:{
                    Log.i(TAG,"resultcode 521, break");
                    MapRepo mRepo = msg.getData().getParcelable("mapRepo");
                    mActivity.get().setNearTrade(mRepo);
                    //드라이버 관리 함수 생성
                    Log.i(TAG,"여기가 문제같은데?"+mRepo.getMyActiveTradeDriver().size());
                    mActivity.get().setActiveDriverStatus(mRepo.getMyActiveTradeDriver());
                    break;
                }
                case 522 :{
                    Log.i(TAG,"resultcode 522, break");
                    break;
                }
                case  721 :{
                    TradeDepPojo pojo = msg.getData().getParcelable("tradeDepPojo");
                    mActivity.get().addTradeScadule(pojo);
                    break;
                }
                case  722 :{

                    break;
                }
                case  723 :{
                    TradeDepListPojo pojo = msg.getData().getParcelable("tradeDepListPojo");
                    mActivity.get().addTradeScaduleList(pojo);
                    break;
                }
                case  724 :{

                    break;
                }
                case  731 :{
                    depUpdateListenerPojo pojo = msg.getData().getParcelable("pojo");
                    mActivity.get().updateDriverList(pojo.getTradeLocList());
                    break;
                }
                case  732 :{
                    Log.i(TAG,"Comm failed with this code :"+msg.getData().getInt("code"));
                    break;
                }
                default : {
                    Log.i(TAG,"resultcode default reached, code :"+msg.getData().getInt("result"));
                    break;
                }

            }
        }
    }
}