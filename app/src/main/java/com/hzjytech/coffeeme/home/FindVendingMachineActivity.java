package com.hzjytech.coffeeme.home;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviParaOption;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.OpenClientUtil;
import com.hzjytech.coffeeme.BaseActivity;
import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.baidumap.BaiduLocationService;
import com.hzjytech.coffeeme.configurations.UmengConfig;
import com.hzjytech.coffeeme.entities.Machine;
import com.hzjytech.coffeeme.listeners.MyOrientationListener;
import com.hzjytech.coffeeme.utils.AppUtil;
import com.hzjytech.coffeeme.utils.BitmapUtil;
import com.hzjytech.coffeeme.utils.LogUtil;
import com.hzjytech.coffeeme.utils.MyApplication;
import com.hzjytech.coffeeme.utils.ToastUtil;
import com.hzjytech.coffeeme.widgets.TitleBar;
import com.umeng.analytics.MobclickAgent;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.io.File;
import java.security.KeyStore;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


@ContentView(R.layout.activity_find_vending_machine)
public class FindVendingMachineActivity extends BaseActivity {

    private static final String GAODE = "com.autonavi.minimap";
    private static final String BAIDU = "com.baidu.BaiduMap";
    @ViewInject(R.id.titleBar)
    private TitleBar tbFindTitle;

    private MapView bMapView;
    private ArrayList<Machine> machines = new ArrayList<Machine>();
    @ViewInject(R.id.rcyViewFindLocations)
    private RecyclerView rcyViewMachinesAppItems;
    private MachinesAdapter machinesAdapter;
    private BaiduMap mBaiduMap;
    private List<LatLng> latLngs = new ArrayList<>();

    private int currentSelectItem = -1;

    private Handler mHandler = new Handler();

    private double mLatitude;
    private double mLongitude;
    private BaiduLocationService baiduLocationService;
    private Marker mCurrentMarker = null;
    private float mCurrentAccuracy;
    private float mXDirection;
    /**
     * 定位的客户端
     */
    private LocationClient mLocationClient;

    /***
     * 是否是第一次定位
     */
    private volatile boolean isFristLocation = true;
    private MyOrientationListener myOrientationListener;
    private double mNextLongitude;
    private double mNextLatitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        initTitle();
        bMapView = (MapView) findViewById(R.id.bMapView);
        mBaiduMap = bMapView.getMap();

        machines = (ArrayList<Machine>) getIntent().getSerializableExtra("machines");
        mLatitude = getIntent().getDoubleExtra("Latitude", 0.0d);
        mLongitude = getIntent().getDoubleExtra("Longitude", 0.0d);
        initBaiduConfig();
        startBaiduMap();
        Init();
        initOritationListener();


    }
    /**
     * 初始化方向传感器
     */
    private void initOritationListener()
    {
        myOrientationListener = new MyOrientationListener(
                getApplicationContext());
        myOrientationListener
                .setOnOrientationListener(new MyOrientationListener.OnOrientationListener()
                {
                    @Override
                    public void onOrientationChanged(float x)
                    {
                        mDirection = (int) x;
                        LogUtil.e("x",mDirection+"");

                        // 构造定位数据
                        MyLocationData locData = new MyLocationData.Builder()
                                .accuracy(mCurrentAccuracy)
                                // 此处设置开发者获取到的方向信息，顺时针0-360
                                .direction(mDirection)
                                .latitude(mNextLatitude)
                                .longitude(mNextLongitude).build();
                        // 设置定位数据
                        mBaiduMap.setMyLocationData(locData);
                        addMyLocationMarker(gaodeToBaidu(new LatLng(mNextLatitude, mNextLongitude)));
                    }
                });
    }

    private void initBaiduConfig() {
        baiduLocationService = ((MyApplication) (getApplication())).baiduLocationService;
        baiduLocationService.registerListener(bdLocationListener);
        baiduLocationService.setLocationOption(baiduLocationService.getDefaultLocationClientOption());
    }
    private float mDirection;
    private BDLocationListener bdLocationListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(final BDLocation location) {
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                // map view 销毁后不在处理新接收的位置
                if (location == null || bMapView == null)
                    return;
                // 构造定位数据
                MyLocationData locData = new MyLocationData.Builder()
                        //.accuracy(location.getRadius())
                        .accuracy(location.getRadius())
                        // 此处设置开发者获取到的方向信息，顺时针0-360
                        .direction(mDirection).latitude(location.getLatitude())
                        .longitude(location.getLongitude()).build();
                // 设置定位数据
                  mBaiduMap.setMyLocationData(locData);
                // 第一次定位时，将地图位置移动到当前位置
                if (isFristLocation)
                {
                    isFristLocation = false;
                  LatLng ll = new LatLng(location.getLatitude(),
                            location.getLongitude());
                    MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(gaodeToBaidu(ll));
                    mBaiduMap.animateMapStatus(u);
                    //addMyLocationMarker(ll);
                }
                mNextLongitude = location.getLongitude();
                mNextLatitude = location.getLatitude();
                mCurrentAccuracy = 200;
                mDirection=location.getDirection();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        addMyLocationMarker(gaodeToBaidu(new LatLng(mNextLatitude, mNextLongitude)));
                    }
                });

//                StringBuffer sb = new StringBuffer(256);
//                sb.append(location.getTime());
//                sb.append("\nlocType : ");// 定位类型
//                sb.append(location.getLocType());
//                sb.append("\nlocType description : ");// *****对应的定位类型说明*****
//                sb.append(location.getLocTypeDescription());
//                sb.append("\nlatitude : ");// 纬度
//                sb.append(location.getLatitude());
//                sb.append("\nlontitude : ");// 经度
//                sb.append(location.getLongitude());
//                sb.append("\nradius : ");// 半径
//                sb.append(location.getRadius());
//                sb.append("\nCountryCode : ");// 国家码
//                sb.append(location.getCountryCode());
//                sb.append("\nCountry : ");// 国家名称
//                sb.append(location.getCountry());
//                sb.append("\ncitycode : ");// 城市编码
//                sb.append(location.getCityCode());
//                sb.append("\ncity : ");// 城市
//                sb.append(location.getCity());
//                sb.append("\nDistrict : ");// 区
//                sb.append(location.getDistrict());
//                sb.append("\nStreet : ");// 街道
//                sb.append(location.getStreet());
//                sb.append("\naddr : ");// 地址信息
//                sb.append(location.getAddrStr());
//                sb.append("\nUserIndoorState: ");// *****返回用户室内外判断结果*****
//                sb.append(location.getUserIndoorState());
//                sb.append("\nDirection(not all devices have value): ");
//                sb.append(location.getDirection());// 方向
//                sb.append("\nlocationdescribe: ");
//                sb.append(location.getLocationDescribe());// 位置语义化信息
//                sb.append("\nPoi: ");// POI信息
//
//                LogUtil.d("BaiduMap", sb.toString());

            } else if (location.getLocType() == BDLocation.TypeServerError) {
                ToastUtil.showShort(FindVendingMachineActivity.this, "服务端网络定位失败");
                baiduLocationService.stop();
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                ToastUtil.showShort(FindVendingMachineActivity.this, "网络导致定位失败，请检查网络是否通畅");
                baiduLocationService.stop();
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                ToastUtil.showShort(FindVendingMachineActivity.this, "您的手机当前不支持定位，请重新设置一下情景模式或者重启手机");
                baiduLocationService.stop();
            }
        }
    };

    private void startBaiduMap() {
        baiduLocationService.start();
    }

    private void initTitle() {
        tbFindTitle.setTitle("附近咖啡机");
        tbFindTitle.setLeftImageResource(R.drawable.icon_left);
        tbFindTitle.setTitleColor(Color.WHITE);
        tbFindTitle.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void Init() {
        bMapView.showScaleControl(false);//默认是true，显示缩放按钮
        bMapView.showZoomControls(false);//默认是true，显示比例尺
        //MapStatusUpdate mapstatusUpdate = MapStatusUpdateFactory.zoomTo(19);;
       // mBaiduMap.setMapStatus(mapstatusUpdate);
        machinesAdapter = new MachinesAdapter(FindVendingMachineActivity.this, machines);
        rcyViewMachinesAppItems.setAdapter(machinesAdapter);
        rcyViewMachinesAppItems.setLayoutManager(new LinearLayoutManager(FindVendingMachineActivity.this));

        mBaiduMap.setMyLocationEnabled(true);
//
//        addMyLocationMarker(gaodeToBaidu(new LatLng(mLatitude, mLongitude)));

        for (Machine machine : machines) {
        /*    LatLng sourceLatLng = new LatLng(Double.parseDouble(machine.getLatitude()), Double.parseDouble(machine.getLongitude()));
            CoordinateConverter converter = new CoordinateConverter();
            converter.from(CoordinateConverter.CoordType.COMMON);
            converter.coord(sourceLatLng);
            LatLng desLatLng = converter.convert();
            latLngs.add(desLatLng);*/
            latLngs.add(new LatLng(Double.parseDouble(machine.getLatitude()), Double.parseDouble(machine.getLongitude())));
        }
        addNewMarkers(latLngs, 0);
        MyLocationData locationData = new MyLocationData.Builder().latitude(latLngs.get(0).latitude)
                .longitude(latLngs.get(0).longitude).build();
        mBaiduMap.setMyLocationData(locationData);

        MapStatus.Builder builder = new MapStatus.Builder();
        //the more bigger zoom value is ,the more detail the map is;
        builder.target(latLngs.get(0)).zoom(16.0f);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
//        setCenterPoint(latLngs.get(0));

        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {

//                mBaiduMap.clear();
//                addMarkers(latLngs, getLatlngsPosition(marker));

                updateSelectMarker(marker.getPosition(), getLatlngsPosition(marker));
                setCenterPoint(marker.getPosition());


                currentSelectItem = getLatlngsPosition(marker);
                machinesAdapter.notifyDataSetChanged();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        rcyViewMachinesAppItems.smoothScrollToPosition(getLatlngsPosition(marker));
                    }
                });

                return false;
            }
        });
    }

    //gaode location latLng to baidu latlng
    private LatLng gaodeToBaidu(LatLng gaodeLatlng) {
       // CoordinateConverter converter = new CoordinateConverter();
       // converter.from(CoordinateConverter.CoordType.COMMON);

//            LatLng desLatLng = converter.convert();
//
//// 将GPS设备采集的原始GPS坐标转换成百度坐标
         CoordinateConverter converter  = new CoordinateConverter();
         converter.from(CoordinateConverter.CoordType.GPS);
//// sourceLatLng待转换坐标
//            converter.coord(sourceLatLng);
        //此处修改了高德转百度，因为返回已经为百度，所以此处去掉操作
       //  converter.coord(gaodeLatlng);
       // return converter.convert();
       return gaodeLatlng;
    }

    private void addMyLocationMarker(LatLng latLng) {
//        OverlayOptions myLocationMarker = new CircleOptions().center(latLng).radius(200).fillColor(0xAA0000ff).stroke(new Stroke(2, 0x000000ff)).visible(true);
//        mBaiduMap.addOverlay(myLocationMarker);

        Log.d("mDirection","mDirection"+mDirection);
        MyLocationData locationData = new MyLocationData.Builder().accuracy(mCurrentAccuracy).latitude(latLng.latitude)
                .longitude(latLng.longitude).direction(mDirection).build();
        mBaiduMap.setMyLocationData(locationData);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize=1;
        Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.location_marker,options);
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromBitmap(bitmap2);
        //BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.location_marker);
        MyLocationConfiguration configuration = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL,true,bitmap);
        mBaiduMap.setMyLocationConfigeration(configuration);
//        OverlayOptions option = new MarkerOptions()
//                .position(latLng)
//                .icon(bitmap).flat(true).rotate(mXDirection);
//         mBaiduMap.addOverlay(option);

    }

    @Override
    public void onStop() {
        baiduLocationService.unregisterListener(bdLocationListener);
        baiduLocationService.stop();
        // 关闭图层定位
        mBaiduMap.setMyLocationEnabled(false);

        // 关闭方向传感器
        myOrientationListener.stop();
        super.onStop();
    }

    @Override
    protected void onStart() {
        // 开启图层定位
        mBaiduMap.setMyLocationEnabled(true);
        // 开启方向传感器
        myOrientationListener.start();
        super.onStart();


    }



    private int getLatlngsPosition(Marker marker) {
        int position = 0;
        for (int i = 0; i < latLngs.size(); i++) {
            if (latLngs.get(i).equals(marker.getPosition())) {
                return i;
            }
        }
        return position;
    }


    private void setCenterPoint(LatLng currentPoint) {

        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(currentPoint);
//        builder.target(currentPoint).zoom(14.0f);
//        builder.target(currentPoint);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
    }

    /**
     * create unselete view with num
     *
     * @param num
     * @return
     */
    private BitmapDescriptor unselectMarker(int num) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.mapmark, null);
        view.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        TextView title = (TextView) view.findViewById(R.id.map_title);
        title.setBackgroundResource(R.drawable.mapnumbg);
        title.setText(getString(R.string.single_string, num + 1));
        return BitmapDescriptorFactory
                .fromView(view);
    }

    /**
     * create select view with num
     *
     * @param num
     * @return
     */
    private BitmapDescriptor selectMarker(int num) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.mapmark, null);
        view.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        TextView title = (TextView) view.findViewById(R.id.map_title);
        title.setBackgroundResource(R.drawable.selmapnumbg);
        title.setText(getString(R.string.single_string, num + 1));
        return BitmapDescriptorFactory
                .fromView(view);
    }

    private void addNewMarkers(List<LatLng> latLngs, int num) {
        List<OverlayOptions> options = new ArrayList<>();
        for (int i = 0; i < latLngs.size(); i++) {
            if (num != i) {
                options.add(new MarkerOptions().position(latLngs.get(i)).icon(unselectMarker(i)));
            }
        }

        mBaiduMap.addOverlays(options);
        OverlayOptions option = new MarkerOptions().position(latLngs.get(num)).icon(selectMarker(num));
        mCurrentMarker = (Marker) mBaiduMap.addOverlay(option);
        mCurrentMarker.setToTop();

    }

    /**
     * @param newLatLng 选中新点的位置，以及显示的数字
     * @param num
     */
    private void updateSelectMarker(LatLng newLatLng, int num) {
        OverlayOptions oldMarker = new MarkerOptions().position(mCurrentMarker.getPosition()).icon(unselectMarker(getLatlngsPosition(mCurrentMarker)));
        mCurrentMarker.remove();
        mBaiduMap.addOverlay(oldMarker);

        OverlayOptions newMarker = new MarkerOptions().position(newLatLng).icon(selectMarker(num));
        mCurrentMarker = (Marker) mBaiduMap.addOverlay(newMarker);
        mCurrentMarker.setToTop();
    }


    private void refreshSelMachine(int pos) {

        updateSelectMarker(latLngs.get(pos), pos);
        setCenterPoint(latLngs.get(pos));
    }


    //    /**
//     * 方法必须重写
//     */
    @Override
    protected void onResume() {
        super.onResume();
        bMapView.onResume();

        MobclickAgent.onPageStart(UmengConfig.FINDVENDINGMACHINEACTIVITY);
        MobclickAgent.onResume(this);
    }

    //
//    /**
//     * 方法必须重写
//     */
    @Override
    protected void onPause() {
        super.onPause();
        bMapView.onPause();

        MobclickAgent.onPageEnd(UmengConfig.FINDVENDINGMACHINEACTIVITY);
        MobclickAgent.onPause(this);
    }

    //
//    /**
//     * 方法必须重写
//     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBaiduMap.setMyLocationEnabled(false);
        bMapView.onDestroy();
    }

    //
    class MachinesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int NEARBY = 0;
        private static final int MACHINES = 1;
        DecimalFormat df = new DecimalFormat("0.0");
        private Context context;
        private List<Machine> machines;
        private int size;
        private int pos;


        public MachinesAdapter(Context context, List<Machine> machines) {
            this.machines = machines;
            size = machines.size();
            this.context = context;
        }


        class NearbyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            RelativeLayout nearby_layout;
            TextView nearby_distance;
            TextView nearby_name;
            TextView nearby_address;
            ImageView nearby_navimg;

            public NearbyViewHolder(View itemView) {
                super(itemView);

                nearby_layout = (RelativeLayout) itemView.findViewById(R.id.nearby_layout);
                nearby_distance = (TextView) itemView.findViewById(R.id.nearby_distance);
                nearby_name = (TextView) itemView.findViewById(R.id.nearby_name);
                nearby_address = (TextView) itemView.findViewById(R.id.nearby_address);
                nearby_navimg = (ImageView) itemView.findViewById(R.id.nearby_navimg);

                nearby_navimg.setOnClickListener(this);
                nearby_layout.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                pos = 0;

                if (AppUtil.isFastClick())
                    return;

                if (v.getId() == R.id.nearby_navimg) {

                    if (isInstallPackage(BAIDU)) {
                        openBaiduMap(latLngs.get(pos).longitude, latLngs.get(pos).latitude, "",
                                machines.get(pos).getAddress());
//                        openBaiduMap(Double.parseDouble(machines.get(pos).getLongitude()),
//                                Double.parseDouble(machines.get(pos).getLatitude()), "",
//                                machines.get(pos).getAddress());


                    } else if (isInstallPackage(GAODE)) {
                        openGaoDeMap(Double.parseDouble(machines.get(pos).getLongitude()),
                                Double.parseDouble(machines.get(pos).getLatitude()), "",
                                machines.get(pos).getAddress());
                    } else {
                        ToastUtil.showShort(FindVendingMachineActivity.this, "请先安装高德地图或百度地图后再导航");
                    }

                } else if (v.getId() == R.id.nearby_layout) {

                    currentSelectItem = pos;
                    notifyDataSetChanged();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            refreshSelMachine(pos);
                        }
                    });

                }
            }
        }

        class MachinesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            RelativeLayout machine_layout;
            TextView machine_distance;
            TextView machine_name;
            TextView machine_address;
            ImageView machine_navimg;

            public MachinesViewHolder(View itemView) {
                super(itemView);

                machine_layout = (RelativeLayout) itemView.findViewById(R.id.machine_layout);
                machine_distance = (TextView) itemView.findViewById(R.id.machine_distance);
                machine_name = (TextView) itemView.findViewById(R.id.machine_name);
                machine_address = (TextView) itemView.findViewById(R.id.machine_address);
                machine_navimg = (ImageView) itemView.findViewById(R.id.machine_navimg);

                machine_layout.setOnClickListener(this);
                machine_navimg.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                pos = getLayoutPosition();

                if (AppUtil.isFastClick())
                    return;
                if (v.getId() == R.id.machine_navimg) {

                    if (isInstallPackage(BAIDU)) {
                        openBaiduMap(latLngs.get(pos).longitude, latLngs.get(pos).latitude, "",
                                machines.get(pos).getAddress());
//                        openBaiduMap(Double.parseDouble(machines.get(pos).getLongitude()),
//                                Double.parseDouble(machines.get(pos).getLatitude()), "",
//                                machines.get(pos).getAddress());


                    } else if (isInstallPackage(GAODE)) {
                        openGaoDeMap(Double.parseDouble(machines.get(pos).getLongitude()),
                                Double.parseDouble(machines.get(pos).getLatitude()), "",
                                machines.get(pos).getAddress());

                    } else {
                        ToastUtil.showShort(FindVendingMachineActivity.this, "请先安装高德地图或百度地图后再导航");
                    }
                } else if (v.getId() == R.id.machine_layout) {

                    currentSelectItem = pos;
                    notifyDataSetChanged();

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            refreshSelMachine(pos);
                        }
                    });

                }
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return NEARBY;
            } else {
                return MACHINES;
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            RecyclerView.ViewHolder viewHolder = null;

            switch (viewType) {
                case NEARBY:
                    View nearby = inflater.inflate(R.layout.findvend_nearbyitem, parent, false);
                    viewHolder = new NearbyViewHolder(nearby);
                    break;
                case MACHINES:
                    View machines = inflater.inflate(R.layout.findvend_machinesitem, parent, false);
                    viewHolder = new MachinesViewHolder(machines);
                    break;
            }
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            switch (holder.getItemViewType()) {
                case NEARBY:
                    ((NearbyViewHolder) holder).nearby_distance.setText(df.format(machines.get(position).getLinear_distance()) + "");
                    ((NearbyViewHolder) holder).nearby_name.setText(machines.get(position).getName());
                    ((NearbyViewHolder) holder).nearby_address.setText(machines.get(position).getAddress());
                    if (position == currentSelectItem) {
                        ((NearbyViewHolder) holder).nearby_layout.setBackgroundResource(R.color.normal_grey);
                    } else {
                        ((NearbyViewHolder) holder).nearby_layout.setBackgroundResource(R.color.normal_white);
                    }
                    break;
                case MACHINES:
                    ((MachinesViewHolder) holder).machine_distance.setText(df.format(machines.get(position).getLinear_distance()) + "");
                    ((MachinesViewHolder) holder).machine_name.setText(machines.get(position).getName());
                    ((MachinesViewHolder) holder).machine_address.setText(machines.get(position).getAddress());
                    if (position == currentSelectItem) {
                        ((MachinesViewHolder) holder).machine_layout.setBackgroundResource(R.color.normal_grey);
                    } else {
                        ((MachinesViewHolder) holder).machine_layout.setBackgroundResource(R.color.normal_white);
                    }
                    break;
            }

        }


        @Override
        public int getItemCount() {
            return machines.size();
        }
    }

    private void openWebBaiduMap(double Longitude, double Latitude, String startLocation, String endLocation) {
        LatLng startLatLng = new LatLng(getIntent().getDoubleExtra("Latitude", 0.0d), getIntent().getDoubleExtra("Longitude", 0.0d));
        LatLng endLatLng = new LatLng(Latitude, Longitude);
        NaviParaOption para = new NaviParaOption()
                .startPoint(startLatLng).endPoint(endLatLng).startName(startLocation).endName(endLocation);

        BaiduMapNavigation.openWebBaiduMapNavi(para, this);
    }


    private boolean isInstallPackage(String packageName) {
        return new File("/data/data/" + packageName).exists();
    }


    private double[] bdToGaoDe(double bd_lat, double bd_lon) {
        double[] gd_lat_lon = new double[2];
        double PI = 3.14159265358979324 * 3000.0 / 180.0;
        double x = bd_lon - 0.0065, y = bd_lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * PI);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * PI);
        gd_lat_lon[0] = z * Math.cos(theta);
        gd_lat_lon[1] = z * Math.sin(theta);
        return gd_lat_lon;
    }

    private double[] gaoDeToBaidu(double gd_lon, double gd_lat) {
        double[] bd_lat_lon = new double[2];
        double PI = 3.14159265358979324 * 3000.0 / 180.0;
        double x = gd_lon, y = gd_lat;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * PI);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * PI);
        bd_lat_lon[0] = z * Math.cos(theta) + 0.0065;
        bd_lat_lon[1] = z * Math.sin(theta) + 0.006;
        return bd_lat_lon;
    }


    private void openBaiduMap(double Longitude, double Latitude, String title, String describle) {
//        LatLng startLatLng=new LatLng(getIntent().getDoubleExtra("Latitude", 0.0d), getIntent().getDoubleExtra("Longitude", 0.0d));
//        LatLng endLatLng=new LatLng(Latitude,Longitude);
//        // 构建 导航参数
//        NaviParaOption para = new NaviParaOption()
//                .startPoint(startLatLng).endPoint(endLatLng)
//                .startName(title).endName(describle);
//
//        try {
//            BaiduMapNavigation.openBaiduMapNavi(para, this);
//        } catch (BaiduMapAppNotSupportNaviException e) {
//            e.printStackTrace();
//            showBaiduDialog();
//        }


        try {
            StringBuilder loc = new StringBuilder();
            loc.append("intent://map/direction?origin=latlng:");
            loc.append(Latitude);
            loc.append(",");
            loc.append(Longitude);
            loc.append("|name:");
            loc.append("我的位置");
            loc.append("&destination=latlng:");
            loc.append(Latitude);
            loc.append(",");
            loc.append(Longitude);
            loc.append("|name:");
            loc.append(describle);
            loc.append("&mode=walking");
            loc.append("&referer=Autohome|GasStation#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
            Intent intent = Intent.getIntent(loc.toString());
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showBaiduDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("您尚未安装百度地图app或app版本过低，点击确认安装？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                OpenClientUtil.getLatestBaiduMapApp(FindVendingMachineActivity.this);
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }


    private void openGaoDeMap(double lon, double lat, String title, String describle) {
        try {
//            double[] gd_lat_lon = bdToGaoDe(lon, lat);
            StringBuilder loc = new StringBuilder();
            loc.append("androidamap://viewMap?sourceApplication=XX");
            loc.append("&poiname=");
            loc.append(describle);
            loc.append("&lat=");

//            loc.append(gd_lat_lon[0]);
            loc.append(lat);
            loc.append("&lon=");
//            loc.append(gd_lat_lon[1]);
            loc.append(lon);
            Intent intent = Intent.getIntent(loc.toString());
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}