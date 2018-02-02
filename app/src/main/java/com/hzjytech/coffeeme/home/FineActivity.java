package com.hzjytech.coffeeme.home;

import android.animation.ObjectAnimator;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hzjytech.coffeeme.BaseActivity;
import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.entities.AppDosages;
import com.hzjytech.coffeeme.entities.Material;
import com.hzjytech.coffeeme.entities.RestoreAppDosages;
import com.hzjytech.coffeeme.utils.DensityUtil;
import com.hzjytech.coffeeme.utils.LogUtil;
import com.hzjytech.coffeeme.utils.MyMath;
import com.hzjytech.coffeeme.utils.SharedPrefUtil;
import com.hzjytech.coffeeme.widgets.BubbleSeekBar;
import com.hzjytech.coffeeme.widgets.CoffeeCupView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

//增加固定口味控制阀，每种物料加入水量控制
@ContentView(R.layout.activity_fine)
public class FineActivity extends BaseActivity {
    private static final int COFFEE = 2;
    private static final int SUGAR = 1;
    private static final int MILK = 3;
    private static final int CHOCOLATE = 4;
    //最大容量
    private static final int MAXCOLUME=240;
    //杯子、水以及机器折旧所消耗的基础价格
    private static final float BASICPRICE = 2.0f;
    public static final String FULLCUP="杯子已经放不下啦！";
    public static final String MOSTMATERIAL="该物料已达上限";
    public static final String MINDENSITY="该物料浓度已最低";
    public static final String MOSTDENSITY="该物料浓度已达上限";
    public static final String MINUVOLUME="饮品容量最少也要40ml哦！";
    private int tempPreTag=-1;
    @ViewInject(R.id.rv_fine)
     RecyclerView rv_fine;
    @ViewInject(R.id.iv_coffeecup)
    CoffeeCupView ccv;
    @ViewInject(R.id.rl_guide)
    RelativeLayout rl_guide;
    @ViewInject(R.id.tv_guide)
    TextView tv_guide;
    @ViewInject(R.id.tvprice)
    TextView tv_price;
    @ViewInject(R.id.tvVolume)
    TextView tv_Volume;
    @ViewInject(R.id.ll_choose_button)
    LinearLayout ll_choose_button;
    @ViewInject(R.id.ll_choose_button2)
    LinearLayout ll_choose_button2;
    @ViewInject(R.id.tv_notify)
    private TextView tv_notify;
    private ArrayList<AppDosages> appDosages;
    private FineItemAdapter fineItemAdapter;
    private ArrayList<RestoreAppDosages> oldAppDosages;
    private ArrayList<AppDosages> intentAppDosages;
    private Material coffeeMaterial;
    private Material sugarMaterial;
    private Material milkMaterial;
    private Material chocolateMaterial;
    private double totleSugarMaterial;
    private double totleCoffeeMaterial;
    private double totleMilkMaterial;
    private double totleChocolateMaterial;
    private float totleWater;
    private double totlePrice;
    private DecimalFormat df;
    private ArrayList<Material> materials;
    private boolean isFirst=true;
    private double commonDesRate=0.4;
    private double totleCoffeeWater;
    private double totleSugarWater;
    private double totleMilkWater;
    private double totleChocolateWater;
    private Handler handler=new Handler();
    private ItemTouchHelper itemTouchHelper;
    private ItemTouchHelper.Callback mCallback;
    private boolean isIdle=true;
    private boolean canDrag=true;
    private boolean isFromRecipe;
    private int totleVolume;
    private String realTotlePrice;
    private int reclen;
    private float top;
    private float inTop;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        intentAppDosages = (ArrayList<AppDosages>) intent.getSerializableExtra("appDosages");
        materials = intent.getParcelableArrayListExtra("materials");
        reclen = intent.getIntExtra("reclen", 0);
        isFromRecipe = intent.getBooleanExtra("isFromRecipe", false);
        parceMaterials(materials);
        initView();
    }


    private void setCanDrag(){
       //isIdle=false;
        canDrag=false;
    }
    private void parceMaterials(ArrayList<Material> materials) {
       LogUtil.e("materials",materials.toString());
        for (Material material : materials) {
            switch (material.getName()){
                case "糖":sugarMaterial=material;
                    break;
                case "咖啡豆":coffeeMaterial=material;
                    break;
                case "奶粉":milkMaterial=material;
                    break;
                case "巧克力粉":chocolateMaterial=material;
                    break;
                default:
                    break;
            }
        }
    }

    private void initView() {
        if(SharedPrefUtil.getIsFirstEnterFine()){
            rl_guide.setVisibility(View.GONE);
        }else{
            rl_guide.setVisibility(View.GONE);
        }
        tv_guide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rl_guide.setVisibility(View.GONE);
                SharedPrefUtil.saveIsFirstEnterFine(false);
            }
        });
        tv_notify.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(motionEvent.getAction()==MotionEvent.ACTION_MOVE){
                    handler.removeCallbacksAndMessages(null);
                    ObjectAnimator anim2 = ObjectAnimator.ofFloat(tv_notify, "translationY", top,-inTop);
                    anim2.setDuration(300);
                    anim2.start();
                }
                return false;
            }
        });
        //根据是否是从recipe界面进入从而展示不同的底部button栏
        initBotomView();
        intData();
        //初始化微调条目
        initRecyclerView();
        isIdle=true;

    }

    private void initBotomView() {
        if(isFromRecipe){
          ll_choose_button.setVisibility(View.GONE);
            ll_choose_button2.setVisibility(View.VISIBLE);
        }else{
            ll_choose_button2.setVisibility(View.GONE);
            ll_choose_button.setVisibility(View.VISIBLE);
        }
    }

    private void intData() {
        df = new DecimalFormat("0.00");
        appDosages = new ArrayList<>();
        oldAppDosages = new ArrayList<>();
        for (AppDosages intentDosage : intentAppDosages) {
            RestoreAppDosages dosages = new RestoreAppDosages(intentDosage.getId(),intentDosage.getWeight(),intentDosage.getSelfWater(),intentDosage.getWater(),intentDosage.getSequence(),intentDosage.getMaterial_name());
            oldAppDosages.add(dosages);
        }
        appDosages.clear();
        appDosages.addAll(intentAppDosages);
        Collections.reverse(appDosages);
        ccv.setMaterialData(materials);
        //Log.e("ccvAppdosages",appDosages.toString());
        ccv.setData(appDosages,getApplicationContext());
        getTotleData();
    }

    private void initRecyclerView() {
        fineItemAdapter = new FineItemAdapter();
        rv_fine.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        rv_fine.setAdapter(fineItemAdapter);
        //得到拖动ViewHolder的position
//得到目标ViewHolder的position
//分别把中间所有的item的位置重新交换
//返回true表示执行拖动
        mCallback = new ItemTouchHelper.Callback() {
            @Override
            public boolean isLongPressDragEnabled() {
                LogUtil.e("isIdle",isIdle+"");
                LogUtil.e("canDrag",canDrag+"");
                boolean b = isIdle && canDrag;
                canDrag=true;
                return b;
            }


            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

            }

            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                /**
                 * ItemTouchHelper支持事件方向判断，但是必须重写当前getMovementFlags来指定支持的方向
                 * 这里我同时设置了dragFlag为上下左右四个方向，swipeFlag的左右方向
                 * 最后通过makeMovementFlags（dragFlag，swipe）创建方向的Flag，
                 * 因为我们目前只需要实现拖拽，所以我并未创建swipe的flag
                 */
                if(viewHolder.getLayoutPosition()==0){
                    return makeMovementFlags(0,0);
                }
                int dragFlag = ItemTouchHelper.UP|ItemTouchHelper.DOWN|ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT;
                int swipe = ItemTouchHelper.START|ItemTouchHelper.END;
                return makeMovementFlags(dragFlag,0);
            }
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();//得到拖动ViewHolder的position
                int toPosition = target.getAdapterPosition();//得到目标ViewHolder的position
                if(fromPosition==0||toPosition==0){
                    return false;
                }
                if (fromPosition < toPosition) {
                    //分别把中间所有的item的位置重新交换
                    for (int i = fromPosition; i < toPosition; i++) {
                        Collections.swap(appDosages, i-1, i );
                    }
                } else {
                    for (int i = fromPosition; i > toPosition; i--) {
                        Collections.swap(appDosages, i-1, i-2);
                    }
                }
                for (int i=1;i<appDosages.size()+1;i++) {
                    appDosages.get(i-1).setSequence(i);
                }
                fineItemAdapter.notifyItemMoved(fromPosition, toPosition);
                ccv.setData(appDosages,getApplicationContext());
                //返回true表示执行拖动
                return true;

            }


            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                //Log.e("actionState",actionState+"actionState");
                if(actionState!=ItemTouchHelper.ACTION_STATE_IDLE){
                    int width = viewHolder.itemView.getWidth();
                    int height = viewHolder.itemView.getHeight();
                    viewHolder.itemView.setScaleY(1.005f);
                    viewHolder.itemView.setScaleX(1.005f);
                    Vibrator vibrator = (Vibrator) FineActivity.this.getSystemService(Service.VIBRATOR_SERVICE);
                    vibrator.vibrate(70);
                }
                super.onSelectedChanged(viewHolder, actionState);
            }

            @Override
            public void clearView(final RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                viewHolder.itemView.setScaleY(1.0f/1.005f);
                viewHolder.itemView.setScaleX(1.0f/1.005f);
                //fineItemAdapter.notifyDataSetChanged();



            }

        };
        itemTouchHelper = new ItemTouchHelper(mCallback);
        itemTouchHelper.attachToRecyclerView(rv_fine);
    }

    private class FineItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        private static final int HEAD = 0;
        private static final int NORMAL = 1;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder viewHolder=null;
             if(viewType==HEAD){
                 LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
                 View view = layoutInflater.inflate(R.layout.item_fine_head,parent,false);
                 viewHolder = new ViewHolder(view);
             }else{
                 LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
                 View view = layoutInflater.inflate(R.layout.layout_item_coffee_water2,parent,false);
                 viewHolder = new ViewHolder2(view);
             }

                return viewHolder;

        }

        @Override
        public int getItemViewType(int position) {
            if(position==0){
                return HEAD;
            }else{
                return NORMAL;
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                    if(position==0){
                        return;
                    }
                    position--;
                    ViewHolder2 holder1= (ViewHolder2) holder;
                    switch (appDosages.get(position).getMaterial_name()){
                        case "糖":
                            holder1.tv_material_name.setText("糖");
                            holder1.tv_max.setText(String.valueOf((int) sugarMaterial.getMax_weight()));
                            holder1.tv_min.setText("0");
                            holder1.tv_unit.setText("g");
                            holder1.bt_link.setVisibility(View.VISIBLE);
                            holder1.bt_link.setBackgroundResource(R.drawable.button_link);
                            holder1.rl_parent.setBackgroundResource(R.drawable.rect_fine_sugar_parent);
                            holder1.ll_child.setBackgroundResource(R.drawable.rect_fine_sugar_child);
                            holder1.lockTag=true;
                            float max = getValueFromText(holder1.tv_max);
                            float min = getValueFromText(holder1.tv_min);
                            holder1.real_numSkbarModulation.setMax((int) max);
                            holder1.real_numSkbarModulation.setMin((int) min);
                            holder1.real_numSkbarModulation.setType(BubbleSeekBar.NORMAL);
                            holder1.real_numSkbarModulation.setShowProgressInFloat(true);
                            holder1.real_numSkbarModulation.setProgress ((appDosages.get(position).getWeight()));
                           // holder1.real_numSkbarModulation.correctOffsetWhenContainerOnScrolling();
                            holder1.real_numSkbarModulation.bindLeftDrag(false);
                            holder1.real_numSkbarModulation.bindRightDrag(false);
                            holder1.real_numSkbarModulation.setThumbDrawble(R.drawable.bumb_sugar);
                            // holder1.real_numSkbarModulation.setType(NumberSeekBar.SEEKBARTYPE_FINE);
                            //holder1.real_numSkbarModulation.setRange(min,max);
                            //绑定水进度条数据
                            holder1.real_numSkbarModulation_water.setType(BubbleSeekBar.NORMAL);
                            holder1.tv_min_water.setText("8");
                            holder1.tv_max_water.setText("240");
                            holder1.real_numSkbarModulation_water.setMax((int) getValueFromText(holder1.tv_max_water));
                            holder1.real_numSkbarModulation_water.setMin((int) getValueFromText(holder1.tv_min_water));
                            holder1.real_numSkbarModulation_water.setProgress((appDosages.get(position).getWater()));
                            //holder1.real_numSkbarModulation_water.correctOffsetWhenContainerOnScrolling();
                            holder1.real_numSkbarModulation_water.bindLeftDrag(false);
                            holder1.real_numSkbarModulation_water.bindRightDrag(false);
                            holder1.real_numSkbarModulation_water.setThumbDrawble(R.drawable.bumb_water);
                            break;
                        case "咖啡浓度":
                            holder1.tv_material_name.setText("咖啡浓度");
                            holder1.tv_max.setText(String.valueOf((int) coffeeMaterial.getMax_weight()));
                            holder1.tv_min.setText("0");
                            holder1.tv_unit.setText("%");
                            holder1.rl_parent.setBackgroundResource(R.drawable.rect_fine_coffee_parent);
                            holder1.ll_child.setBackgroundResource(R.drawable.rect_fine_coffee_parent);
                            float max2 = getValueFromText(holder1.tv_max);
                            float min2 = getValueFromText(holder1.tv_min);
                            holder1.bt_link.setVisibility(View.INVISIBLE);
                            holder1.bt_link.setClickable(false);
                            holder1.bt_link.setBackgroundResource(R.drawable.button_link);
                            holder1.lockTag=false;
                            holder1.real_numSkbarModulation.setType(BubbleSeekBar.COFFEE);
                            holder1.real_numSkbarModulation.setMax((int) max2);
                            holder1.real_numSkbarModulation.setMin((int) min2);
                            holder1.real_numSkbarModulation.setShowProgressInFloat(false);
                            holder1.real_numSkbarModulation.setProgress ((int) (appDosages.get(position).getWeight()));
                           // holder1.real_numSkbarModulation.correctOffsetWhenContainerOnScrolling();
                            holder1.real_numSkbarModulation.bindLeftDrag(false);
                            holder1.real_numSkbarModulation.bindRightDrag(false);
                            holder1.real_numSkbarModulation.setThumbDrawble(R.drawable.bumb_coffee);
                            //绑定水进度条数据
                            holder1.tv_min_water.setText("0");
                            holder1.tv_max_water.setText("240");
                            //holder1.real_numSkbarModulation_water.setType(NumberSeekBar.SEEKBARTYPE_FINE);
                           // holder1.real_numSkbarModulation_water.setRange(getValueFromText(holder1.tv_min_water),getValueFromText(holder1.tv_max_water));
                            holder1.real_numSkbarModulation_water.setType(BubbleSeekBar.NORMAL);
                            holder1.real_numSkbarModulation_water.setMax((int) getValueFromText(holder1.tv_max_water));
                            holder1.real_numSkbarModulation_water.setMin((int) getValueFromText(holder1.tv_min_water));
                            float water = appDosages.get(position).getWater() - getValueFromText(holder1.tv_min_water);
                            holder1.real_numSkbarModulation_water.setProgress( ((water)));
                            //holder1.real_numSkbarModulation_water.correctOffsetWhenContainerOnScrolling();
                            holder1.real_numSkbarModulation_water.bindLeftDrag(false);
                            holder1.real_numSkbarModulation_water.bindRightDrag(false);
                            holder1.real_numSkbarModulation_water.setThumbDrawble(R.drawable.bumb_water);
                            break;
                        case "奶粉":
                            holder1.tv_material_name.setText("奶粉");
                            holder1.tv_max.setText(String.valueOf((int) milkMaterial.getMax_weight()));
                            holder1.tv_min.setText("0");
                            holder1.tv_unit.setText("g");
                            holder1.bt_link.setVisibility(View.VISIBLE);
                            holder1.bt_link.setBackgroundResource(R.drawable.button_link);
                            holder1.rl_parent.setBackgroundResource(R.drawable.rect_fine_milk_parent);
                            holder1.ll_child.setBackgroundResource(R.drawable.rect_fine_milk_child);
                            holder1.lockTag=true;
                            //holder1.real_numSkbarModulation.setType(NumberSeekBar.SEEKBARTYPE_FINE);
                            float max3 = getValueFromText(holder1.tv_max);
                            float min3 = getValueFromText(holder1.tv_min);
                            holder1.real_numSkbarModulation.setType(BubbleSeekBar.NORMAL);
                            holder1.real_numSkbarModulation.setMax((int) max3);
                            holder1.real_numSkbarModulation.setMin((int) min3);
                            holder1.real_numSkbarModulation.setShowProgressInFloat(true);
                            holder1.real_numSkbarModulation.setProgress (appDosages.get(position).getWeight());
                            //holder1.real_numSkbarModulation.correctOffsetWhenContainerOnScrolling();
                            holder1.real_numSkbarModulation.bindLeftDrag(false);
                            holder1.real_numSkbarModulation.bindRightDrag(false);
                            holder1.real_numSkbarModulation.setThumbDrawble(R.drawable.bumb_milk);
                            //绑定水进度条数据
                            holder1.tv_min_water.setText("8");
                            holder1.tv_max_water.setText("240");
                            //holder1.real_numSkbarModulation_water.setType(NumberSeekBar.SEEKBARTYPE_FINE);
                           // holder1.real_numSkbarModulation_water.setRange(getValueFromText(holder1.tv_min_water),getValueFromText(holder1.tv_max_water));
                            holder1.real_numSkbarModulation_water.setType(BubbleSeekBar.NORMAL);
                            holder1.real_numSkbarModulation_water.setMax((int) getValueFromText(holder1.tv_max_water));
                            holder1.real_numSkbarModulation_water.setMin((int) getValueFromText(holder1.tv_min_water));
                            holder1.real_numSkbarModulation_water.setProgress((appDosages.get(position).getWater()));
                            //holder1.real_numSkbarModulation_water.correctOffsetWhenContainerOnScrolling();
                            holder1.real_numSkbarModulation_water.bindLeftDrag(false);
                            holder1.real_numSkbarModulation_water.bindRightDrag(false);
                            holder1.real_numSkbarModulation_water.setThumbDrawble(R.drawable.bumb_water);
                            break;
                        case "巧克力粉":
                            holder1.tv_material_name.setText("巧克力粉");
                            holder1.tv_max.setText(String.valueOf((int) chocolateMaterial.getMax_weight()));
                            holder1.tv_min.setText("0");
                            holder1.tv_unit.setText("g");
                            holder1.bt_link.setVisibility(View.VISIBLE);
                            holder1.bt_link.setBackgroundResource(R.drawable.button_link);
                            holder1.rl_parent.setBackgroundResource(R.drawable.rect_fine_chocolate_parent);
                            holder1.ll_child.setBackgroundResource(R.drawable.rect_fine_chocolate_child);
                            holder1.lockTag=true;
                            float max4 = getValueFromText(holder1.tv_max);
                            float min4 = getValueFromText(holder1.tv_min);
                            //holder1.real_numSkbarModulation.setRange(min4,max4);
                            holder1.real_numSkbarModulation.setMax((int) max4);
                            holder1.real_numSkbarModulation.setMin((int) min4);
                            holder1.real_numSkbarModulation.setType(BubbleSeekBar.NORMAL);
                            holder1.real_numSkbarModulation.setShowProgressInFloat(true);
                            holder1.real_numSkbarModulation.setProgress (appDosages.get(position).getWeight());
                            //holder1.real_numSkbarModulation.correctOffsetWhenContainerOnScrolling();
                            holder1.real_numSkbarModulation.bindLeftDrag(false);
                            holder1.real_numSkbarModulation.bindRightDrag(false);
                            holder1.real_numSkbarModulation.setThumbDrawble(R.drawable.bumb_chocolate);
                            //绑定水进度条数据
                            holder1.tv_min_water.setText("8");
                            holder1.tv_max_water.setText("240");
                            //holder1.real_numSkbarModulation_water.setType(NumberSeekBar.SEEKBARTYPE_FINE);
                           // holder1.real_numSkbarModulation_water.setRange(getValueFromText(holder1.tv_min_water),getValueFromText(holder1.tv_max_water));
                            holder1.real_numSkbarModulation_water.setType(BubbleSeekBar.NORMAL);
                            holder1.real_numSkbarModulation_water.setMax((int) getValueFromText(holder1.tv_max_water));
                            holder1.real_numSkbarModulation_water.setMin((int) getValueFromText(holder1.tv_min_water));
                            holder1.real_numSkbarModulation_water.setProgress( (appDosages.get(position).getWater()));
                            //holder1.real_numSkbarModulation_water.correctOffsetWhenContainerOnScrolling();
                            holder1.real_numSkbarModulation_water.bindLeftDrag(false);
                            holder1.real_numSkbarModulation_water.bindRightDrag(false);
                            holder1.real_numSkbarModulation_water.setThumbDrawble(R.drawable.bumb_water);
                            break;

            }
        }

        @Override
        public int getItemCount() {
            return appDosages==null?1:appDosages.size()+1;
        }
        private class ViewHolder extends  RecyclerView.ViewHolder{
            public ViewHolder(View itemView) {
                super(itemView);
                itemView.setEnabled(false);
            }
        }
        private class ViewHolder2 extends RecyclerView.ViewHolder{
            public View view;
            public BubbleSeekBar real_numSkbarModulation;
            public BubbleSeekBar real_numSkbarModulation_water;
            public TextView tv_material_name;
            public TextView tv_material_name_water;
            public TextView tv_min;
            public TextView tv_min_water;
            public TextView tv_max;
            public TextView tv_max_water;
            public TextView tv_unit;
            public TextView tv_unit_water;
            private AppDosages appDosage;
            public  ImageView bt_link;
            public LinearLayout ll_child;
            public RelativeLayout rl_parent;
            public RelativeLayout ll_lock_check;
            private boolean lockTag=true;
            public float restoreWeight;
            public float restoreWater;
            public float minWater;
            public static final int OUTWATER=1;
            public static final int OUTMATERIAL=2;
            public static final float MAXDENSITY=4.2f;
            public boolean bySelfSet=false;
            //控制水量和物料判断条件的flag；
            public int flag=0;
            public ViewHolder2(final View itemView) {
                super(itemView);
                real_numSkbarModulation = (BubbleSeekBar) itemView.findViewById(R.id.real_numSkbarodulation);
                real_numSkbarModulation_water = (BubbleSeekBar) itemView.findViewById(R.id.real_numSkbarodulation_water);
                tv_material_name = (TextView) itemView.findViewById(R.id.tv_material_name);
                tv_material_name_water = (TextView) itemView.findViewById(R.id.tv_material_name_water);
                tv_min = (TextView) itemView.findViewById(R.id.tvModulationMin);
                tv_min_water = (TextView) itemView.findViewById(R.id.tvModulationMin_water);
                tv_max = (TextView) itemView.findViewById(R.id.tvModulationMax);
                tv_max_water = (TextView) itemView.findViewById(R.id.tvModulationMax_water);
                tv_unit = (TextView) itemView.findViewById(R.id.tvModulationUnit);
                tv_unit_water = (TextView) itemView.findViewById(R.id.tvModulationUnit_water);
                rl_parent = (RelativeLayout) itemView.findViewById(R.id.rl_parent);
                ll_child = (LinearLayout) itemView.findViewById(R.id.ll_child);
                bt_link = (ImageView) itemView.findViewById(R.id.bt_link);
                ll_lock_check = (RelativeLayout) itemView.findViewById(R.id.ll_lock_check);
                rv_fine.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        real_numSkbarModulation.correctOffsetWhenContainerOnScrolling();
                        real_numSkbarModulation_water.correctOffsetWhenContainerOnScrolling();
                    }
                });
                isFirst=false;
                //开关锁处理
                ll_lock_check.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(lockTag){
                            switch (tv_material_name.getText().toString()){
                                case "咖啡浓度":
                                    lockTag=false;
                                    break;
                                case "糖":
                                    ll_child.setBackgroundResource(R.drawable.rect_fine_sugar_parent);
                                    lockTag=false;
                                    bt_link.setBackgroundResource(R.drawable.button_unlink);
                                    break;
                                case "奶粉":
                                    ll_child.setBackgroundResource(R.drawable.rect_fine_milk_parent);
                                    lockTag=false;
                                    bt_link.setBackgroundResource(R.drawable.button_unlink);
                                    break;
                                case "巧克力粉":
                                    ll_child.setBackgroundResource(R.drawable.rect_fine_chocolate_parent);
                                    lockTag=false;
                                    bt_link.setBackgroundResource(R.drawable.button_unlink);
                                    break;
                            }
                            real_numSkbarModulation.setMinLimit(Float.valueOf((String) tv_min.getText()));
                            real_numSkbarModulation.setMaxLimit(Float.valueOf((String)tv_max.getText()));
                            real_numSkbarModulation_water.setMinLimit(Float.valueOf((String) tv_min_water.getText()));
                            real_numSkbarModulation_water.setMaxLimit(Float.valueOf((String)tv_max_water.getText()));
                        }else{

                            isFirst=true;
                            switch (tv_material_name.getText().toString()){
                                case "咖啡浓度":
                                    lockTag=false;
                                    break;
                                case "糖":
                                    lockTag=true;
                                    ll_child.setBackgroundResource(R.drawable.rect_fine_sugar_child);
                                    bt_link.setBackgroundResource(R.drawable.button_link);
                                    break;
                                case "奶粉":
                                    lockTag=true;
                                    ll_child.setBackgroundResource(R.drawable.rect_fine_milk_child);
                                    bt_link.setBackgroundResource(R.drawable.button_link);
                                    break;
                                case "巧克力粉":
                                    lockTag=true;
                                    ll_child.setBackgroundResource(R.drawable.rect_fine_chocolate_child);
                                    bt_link.setBackgroundResource(R.drawable.button_link);
                                    break;
                            }
                            //
                            real_numSkbarModulation.setMinLimit(Float.valueOf((String) tv_min.getText()));
                            real_numSkbarModulation.setMaxLimit(Float.valueOf((String)tv_max.getText()));
                            real_numSkbarModulation_water.setMinLimit(Float.valueOf((String) tv_min_water.getText()));
                            real_numSkbarModulation_water.setMaxLimit(Float.valueOf((String)tv_max_water.getText()));
                        }
                    }
                });
                //物料进度条监听
                real_numSkbarModulation.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListenerAdapter() {
                    @Override
                    public void onProgressChanged(float progress,boolean isDraged) {
                        //Log.e("action","progresschange");
                        if(isDraged){
                            //isIdle=false;
                            real_numSkbarModulation.getParent().requestDisallowInterceptTouchEvent(true);
                        }
                        appDosage = appDosages.get(getLayoutPosition()-1);
                        getRestoreMaterial();
                        //restoreWeight=appDosage.getWeight();
                            //restoreWater=appDosage.getWater();
                            isFirst=false;
                        float value = progress;
                        appDosage.setWeight(value);
                        //// TODO: 2017/2/6 咖啡浓度 ，水量超过处理
                        if(appDosage.getMaterial_name().equals("咖啡浓度")){
                            appDosage.setSelfWater((float) (value*coffeeMaterial.getWater_rate()));
                        }
                        getTotleData();
                        if(lockTag){
                            locking(value);
                        }else{
                            unLocking(value);
                        }
                        getTotleData();
                       // reFormAppDosages(totleVolume);
                        ccv.setData(appDosages,getApplicationContext());
                    }

                    @Override
                    public void getProgressOnActionUp(float progress) {
                        //Log.e("action","up");
                        real_numSkbarModulation.getParent().requestDisallowInterceptTouchEvent(false);
                        if(Math.round(appDosage.getWeight()*10)/10f==0){
                            appDosages.remove(getLayoutPosition()-1);
                            notifyItemRemoved(getLayoutPosition());
                            getTotleData();
                          /*  handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    fineItemAdapter.notifyDataSetChanged();
                                }
                            },500);*/
                            ccv.setData(appDosages,getApplicationContext());
                        }
                    }
                    @Override
                    public void getProgressOnFinally(float progress) {
                        //Log.e("action","finally");
                        super.getProgressOnFinally(progress);
                    }
                });
                real_numSkbarModulation.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        real_numSkbarModulation.correctOffsetWhenContainerOnScrolling();
                        switch (motionEvent.getAction()){
                            case MotionEvent.ACTION_DOWN:
                                //Log.e("action","down");
                                setCanDrag();

                                break;
                            case MotionEvent.ACTION_UP:
                                //Log.e("action","up");
                                isIdle=true;
                                if(real_numSkbarModulation.getThumbIsDragging()){
                                    setCanDrag();
                                }

                                break;
                        }
                        return false;
                    }
                });
                real_numSkbarModulation_water.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        real_numSkbarModulation_water.correctOffsetWhenContainerOnScrolling();
                        switch (motionEvent.getAction()){
                            case MotionEvent.ACTION_DOWN:
                                    setCanDrag();

                                //isIdle=false;
                                break;
                            case MotionEvent.ACTION_UP:
                                isIdle=true;
                                if(real_numSkbarModulation_water.getThumbIsDragging()){
                                    setCanDrag();
                                }

                                break;
                            case MotionEvent.ACTION_CANCEL:
                                //isIdle=true;
                                //Log.e("action","cancel");
                                break;
                        }
                        return false;
                    }
                });
                real_numSkbarModulation_water.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListenerAdapter() {
                    @Override
                    public void onProgressChanged(float progress,boolean isDraged) {
                        if(isDraged){
                            real_numSkbarModulation_water.getParent().requestDisallowInterceptTouchEvent(true);
                        }
                        appDosage = appDosages.get(getLayoutPosition()-1);
                        float value =progress;
                        //Log.e("value","progress"+value);
                            getRestoreMaterial();
                            isFirst=false;
                        appDosage.setWater(value);
                        //// TODO: 2017/2/6 锁定情况下水量改变
                        if(lockTag){
                            lockingWater(value,isDraged);
                        }else {
                            unLockingWater(value);
                        }
                        getTotleData();
                       // reFormAppDosages(totleVolume);
                        ccv.setData(appDosages,getApplicationContext());

                    }

                    @Override
                    public void getProgressOnActionUp(float progress) {
                        super.getProgressOnActionUp(progress);
                        real_numSkbarModulation_water.getParent().requestDisallowInterceptTouchEvent(false);
                        if(Math.round(appDosage.getWeight()*10)/10f==0){
                            appDosages.remove(getLayoutPosition()-1);
                            notifyItemRemoved(getLayoutPosition());
                            getTotleData();
                         /*   handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    fineItemAdapter.notifyDataSetChanged();
                                }
                            },500);*/
                            ccv.setData(appDosages,getApplicationContext());
                        }
                    }

                    @Override
                    public void getProgressOnFinally(float progress) {
                        super.getProgressOnFinally(progress);
                    }
                });
            }

            private void getRestoreMaterial() {
                switch (appDosage.getMaterial_name()){
                    case "咖啡浓度":
                        if(appDosage.getWater()!=0){
                            restoreWeight=0;
                            restoreWater=appDosage.getWater();
                        }
                        break;
                    case "糖":
                        if(appDosage.getWeight()*sugarMaterial.getWater_rate()>appDosage.getWater()){
                            restoreWeight= (float) (appDosage.getWeight()-(appDosage.getWater()/sugarMaterial.getWater_rate()));
                            restoreWater=0;
                        }else if(appDosage.getWeight()*sugarMaterial.getWater_rate()<appDosage.getWater()){
                            restoreWater= (float) (appDosage.getWater()-appDosage.getWeight()*sugarMaterial.getWater_rate());
                            restoreWeight=0;
                        }else{
                            restoreWater=0;
                            restoreWeight=0;
                        }
                        break;
                    case "奶粉":
                        if(appDosage.getWeight()*milkMaterial.getWater_rate()>appDosage.getWater()){
                            restoreWeight= (float) (appDosage.getWeight()-(appDosage.getWater()/milkMaterial.getWater_rate()));
                            restoreWater=0;
                        }else if(appDosage.getWeight()*milkMaterial.getWater_rate()<appDosage.getWater()){
                            restoreWater= (float) (appDosage.getWater()-appDosage.getWeight()*milkMaterial.getWater_rate());
                            restoreWeight=0;
                        }else{
                            restoreWater=0;
                            restoreWeight=0;
                        }
                        break;
                    case "巧克力粉":
                        if(appDosage.getWeight()*chocolateMaterial.getWater_rate()>appDosage.getWater()){
                            restoreWeight= (float) (appDosage.getWeight()-(appDosage.getWater()/chocolateMaterial.getWater_rate()));
                            restoreWater=0;
                        }else if(appDosage.getWeight()*chocolateMaterial.getWater_rate()<appDosage.getWater()){
                            restoreWater= (float) (appDosage.getWater()-appDosage.getWeight()*chocolateMaterial.getWater_rate());
                            restoreWeight=0;
                        }else{
                            restoreWater=0;
                            restoreWeight=0;
                        }
                        break;
                }
            }

            //锁定情况下水量progress的处理
            //// TODO: 2017/2/6 锁定情况下水量处理 
            private void lockingWater(float value, boolean isDraged) {
                switch (appDosage.getMaterial_name()){
                    case "糖":
                        appDosage.setWeight((float) (restoreWeight+(value-restoreWater)/sugarMaterial.getWater_rate()));
                        if(appDosage.getWeight()>sugarMaterial.getMax_weight()){
                            appDosage.setWeight((float) sugarMaterial.getMax_weight());
                        }
                        real_numSkbarModulation.setProgress(appDosage.getWeight());
                        getTotleData();
                        setWaterMaxAndMinLimit(value,isDraged);
                        break;
                    case "奶粉":
                        appDosage.setWeight((float) (restoreWeight+(value-restoreWater)/milkMaterial.getWater_rate()));
                        if(appDosage.getWeight()>milkMaterial.getMax_weight()){
                            appDosage.setWeight((float) milkMaterial.getMax_weight());
                        }
                        real_numSkbarModulation.setProgress(appDosage.getWeight());
                        getTotleData();
                        setWaterMaxAndMinLimit(value, isDraged);
                        break;
                    case "巧克力粉":
                        appDosage.setWeight((float) (restoreWeight+(value-restoreWater)/chocolateMaterial.getWater_rate()));
                        if(appDosage.getWeight()>chocolateMaterial.getMax_weight()){
                            appDosage.setWeight((float) chocolateMaterial.getMax_weight());
                        }
                        real_numSkbarModulation.setProgress(appDosage.getWeight());
                        getTotleData();
                        setWaterMaxAndMinLimit(value, isDraged);
                        break;
                }
            }

            /**
             * 未锁定情况下水量进度条处理
             * 1、水量超过240ml
             * 2、最小浓度
             */
           private void unLockingWater(float value){
               getTotleData();
               switch (appDosage.getMaterial_name()){
                   case "咖啡浓度":
                       float maxWaterForWater = MAXCOLUME - (totleWater - appDosage.getWater());
                       float max=maxWaterForWater<0.5?0.001f:maxWaterForWater;
                       LogUtil.e("maxWaterCoffee",max+"");
                       real_numSkbarModulation_water.setMaxLimit(max);
                       if(value+0.1>=max){
                           setTipText(FULLCUP);
                           setCanDrag();
                       }
                       break;
                   case "糖":
                       float maxWaterForWater2 = MAXCOLUME - (totleWater - appDosage.getWater());
                       real_numSkbarModulation_water.setMaxLimit(maxWaterForWater2);
                       float minWaterForSugar = appDosage.getWeight() * MAXDENSITY;
                       real_numSkbarModulation_water.setMinLimit(minWaterForSugar);
                       if(value+0.1>=maxWaterForWater2){
                          setTipText(FULLCUP);
                           setCanDrag();
                       }else if(value-0.1<=minWaterForSugar){
                           setTipText(MOSTDENSITY);
                           setCanDrag();
                       }
                       break;
                   case "奶粉" :
                       float maxWaterForWater3 = MAXCOLUME - (totleWater - appDosage.getWater());
                       real_numSkbarModulation_water.setMaxLimit(maxWaterForWater3);
                       float minWaterForMilk = appDosage.getWeight() * MAXDENSITY;
                       real_numSkbarModulation_water.setMinLimit(minWaterForMilk);
                       if(value+0.1>=maxWaterForWater3){
                          setTipText(FULLCUP);
                           setCanDrag();
                       }else if(value-0.1<=minWaterForMilk){
                          setTipText(MOSTDENSITY);
                           setCanDrag();
                       }
                       break;
                   case "巧克力粉":
                       float maxWaterForWater4 = MAXCOLUME - (totleWater - appDosage.getWater());
                       real_numSkbarModulation_water.setMaxLimit(maxWaterForWater4);
                       float minWaterForChocolate = appDosage.getWeight() * MAXDENSITY;
                       real_numSkbarModulation_water.setMinLimit(minWaterForChocolate);
                       if(value+0.1>=maxWaterForWater4){
                          setTipText(FULLCUP);
                           setCanDrag();
                       }else if(value-0.1<=minWaterForChocolate){
                          setTipText(MOSTDENSITY);
                           setCanDrag();
                       }
                       break;
               }
           }
            /**
             * 水量进度条锁定情况下的处理
             *1、水量超过240ml
             * 2、物料超过最大值
             * 3、物料为0的最小限定
             * 4、浓度达到最小浓度
             * @param value
             * @param isDraged
             */
            private void setWaterMaxAndMinLimit(float value, boolean isDraged){
                switch (appDosage.getMaterial_name()){
                    case "糖":
                        float maxWaterForWater2 = MAXCOLUME - (totleWater - appDosage.getWater());
                        double maxWaterForSugar = (sugarMaterial.getMax_weight() - (totleSugarMaterial - appDosage.getWeight())) * sugarMaterial.getWater_rate() + restoreWater-restoreWeight*sugarMaterial.getWater_rate();
                        real_numSkbarModulation_water.setMaxLimit(maxWaterForWater2<maxWaterForSugar?maxWaterForWater2: (float) maxWaterForSugar);
                        double minLimitForMaterial = appDosage.getWater() - appDosage.getWeight() * sugarMaterial.getWater_rate();
                        float min = (float) ((appDosage.getWeight() * sugarMaterial.getWater_rate() - appDosage.getWater()) * (MAXDENSITY / (sugarMaterial.getWater_rate() - MAXDENSITY)));
                        min=min>8?min:8;
                        real_numSkbarModulation_water.setMinLimit((float) minLimitForMaterial>min? (float) minLimitForMaterial :min);
                        if(value+0.1>=maxWaterForWater2){
                            setTipText(FULLCUP);
                            setCanDrag();
                        }else if(value+0.1>=maxWaterForSugar){
                            setCanDrag();
                            if(isDraged){
                               setTipText(MOSTMATERIAL);
                            }
                        }else if(value-0.1<=minLimitForMaterial){
                           setTipText(MINDENSITY);
                            setTipText(MINDENSITY);
                            setCanDrag();
                        }else if(value-0.1<=min){
                           setTipText(MINDENSITY);
                            setCanDrag();
                        }
                        break;
                    case "奶粉":
                        float maxWaterForWater3 = MAXCOLUME - (totleWater - appDosage.getWater());
                        double maxWaterForMilk = (milkMaterial.getMax_weight() - (totleMilkMaterial - appDosage.getWeight())) * milkMaterial.getWater_rate() + restoreWater-restoreWeight*milkMaterial.getWater_rate();
                        real_numSkbarModulation_water.setMaxLimit(maxWaterForWater3<maxWaterForMilk?maxWaterForWater3: (float) maxWaterForMilk);
                        double minLimitForMaterial2 =appDosage.getWater() - appDosage.getWeight() * milkMaterial.getWater_rate();
                        float min2 = (float) ((appDosage.getWeight() * milkMaterial.getWater_rate() - appDosage.getWater()) * (MAXDENSITY / (milkMaterial.getWater_rate() - MAXDENSITY)));
                         min2=min2>8?min2:8;
                        real_numSkbarModulation_water.setMinLimit((float) minLimitForMaterial2>min2? (float) minLimitForMaterial2 :min2);
                        if(value+0.1>=maxWaterForWater3){
                            setTipText(FULLCUP);
                            setCanDrag();
                        }else if(value+0.1>=maxWaterForMilk){
                            setCanDrag();
                            if(isDraged){
                               setTipText(MOSTMATERIAL);
                                setTipText(MOSTMATERIAL);
                            }
                        }else if(value-0.1<=minLimitForMaterial2){
                           setTipText(MINDENSITY);
                            setCanDrag();
                        }else if(value-0.1<min2){
                           setTipText(MINDENSITY);
                        }
                        break;
                    case "巧克力粉":
                        float maxWaterForWater4 = MAXCOLUME - (totleWater - appDosage.getWater());
                        double maxWaterForChocolate = (chocolateMaterial.getMax_weight() - (totleChocolateMaterial - appDosage.getWeight())) * chocolateMaterial.getWater_rate() + (restoreWater-restoreWeight*chocolateMaterial.getWater_rate());
                        real_numSkbarModulation_water.setMaxLimit(maxWaterForWater4<maxWaterForChocolate?maxWaterForWater4: (float) maxWaterForChocolate);
                        //Log.e("maxChocolate",maxWaterForWater4+"==="+maxWaterForChocolate);
                        double minLimitForMaterial3 =appDosage.getWater() - appDosage.getWeight() * chocolateMaterial.getWater_rate();
                        float min3 = (float) ((appDosage.getWeight() * chocolateMaterial.getWater_rate() - appDosage.getWater()) * (MAXDENSITY / (chocolateMaterial.getWater_rate() - MAXDENSITY)));
                        min3=min3>8?min3:8;
                        real_numSkbarModulation_water.setMinLimit((float) minLimitForMaterial3>min3? (float) minLimitForMaterial3 :min3);
                        if(value+0.1>=maxWaterForWater4){
                            setTipText(FULLCUP);
                            setCanDrag();
                        }else if(value+0.1>=maxWaterForChocolate){
                            setCanDrag();
                            if(isDraged){
                               setTipText(MOSTMATERIAL);
                            }
                        }else if(value-0.1<=minLimitForMaterial3){
                           setTipText(MINDENSITY);
                            setCanDrag();
                        }else if(value-0.1<=min3){
                           setTipText(MINDENSITY);
                            setCanDrag();
                        }
                        break;
                }
                if(value-0.1<=real_numSkbarModulation_water.getMin()){
                    setCanDrag();
                }
            }

            /**
             * 1、8ml最小值
             * 2、最大浓度时的最xiao值
             * @param value
             */
            private void setMaxAndMinValue(float value) {
                //设置8ml时最小值
                float min = real_numSkbarModulation_water.getMin();
                switch (appDosage.getMaterial_name()){
                    case "糖":
                        float materialMax = (float) (sugarMaterial.getMax_weight() - (totleSugarMaterial-appDosage.getWeight()));
                        double materialWater =restoreWeight+( (MAXCOLUME -(totleWater-appDosage.getWeight()-appDosage.getWater())-restoreWeight-restoreWater) / (1f+sugarMaterial.getWater_rate()));
                        float materialMaxForDensity = appDosage.getWater() / MAXDENSITY;
                        float maxTag =materialMax<materialWater?materialMax: (float) materialWater;
                        //float max=maxTag<materialMaxForDensity?maxTag:materialMaxForDensity;
                        real_numSkbarModulation.setMaxLimit(maxTag);
                        float min8Material=(float) ((appDosage.getWeight()-(appDosage.getWater()/sugarMaterial.getWater_rate()))+min/sugarMaterial.getWater_rate());
                        float min2 = (float) ((sugarMaterial.getWater_rate() * appDosage.getWeight() - appDosage.getWater()) / (sugarMaterial.getWater_rate() - MAXDENSITY));
                        if(min8Material<min/sugarMaterial.getWater_rate()){
                            min8Material=min8Material<0?0:min8Material;
                        }
                        LogUtil.e("min8",min8Material+"");
                        LogUtil.e("min8",(appDosage.getWeight()-(appDosage.getWater()/sugarMaterial.getWater_rate()))+"extra");
                        LogUtil.e("minTang",min2+"");
                        real_numSkbarModulation.setMinLimit(min8Material>min2?min8Material:min2);
                        //Log.e("max",value+"=="+materialMax+"=="+materialWater+"=="+sugarMaterial.getMax_weight());
                        if(value+0.1>=materialMax){
                            setCanDrag();
                            if(!((value+0.1)>sugarMaterial.getMax_weight())){
                                setTipText(MOSTMATERIAL);
                            }
                        }else if(value+0.1>=materialWater){
                            setTipText(FULLCUP);
                            setCanDrag();
                        }else if(value-0.1<=min8Material){
                            setCanDrag();
                        }else if(value-0.1<=min2){
                           setTipText(MINDENSITY);
                            setCanDrag();
                        }
                    /*    else if(value+0.1>=materialMaxForDensity){
                           setTipText(MINDENSITY);
                            setCanDrag();
                        }*/
                        break;
                    case "奶粉":
                        float materialMax2 = (float) (milkMaterial.getMax_weight() - (totleMilkMaterial-appDosage.getWeight()));
                        double materialWater2 =restoreWeight+( (MAXCOLUME -(totleWater-appDosage.getWeight()-appDosage.getWater())-restoreWeight-restoreWater) / (1f+milkMaterial.getWater_rate()));
                        float materialMaxForDensity2 = appDosage.getWater() / MAXDENSITY;
                        LogUtil.e("max",restoreWeight+"===="+(MAXCOLUME -(totleWater-appDosage.getWeight()-appDosage.getWater())-restoreWeight-restoreWater)+"====");
                        float maxTag2 =materialMax2<materialWater2?materialMax2: (float) materialWater2;
                       // float max2=maxTag2<materialMaxForDensity2?maxTag2:materialMaxForDensity2;
                        real_numSkbarModulation.setMaxLimit(maxTag2);
                        float min8MilkMaterial= (float) ((appDosage.getWeight()-(appDosage.getWater()/milkMaterial.getWater_rate()))+min/milkMaterial.getWater_rate());
                        float min3 = (float) ((milkMaterial.getWater_rate() * appDosage.getWeight() - appDosage.getWater()) / (milkMaterial.getWater_rate() - MAXDENSITY));
                        if(min8MilkMaterial<min/milkMaterial.getWater_rate()){
                            min8MilkMaterial= min8MilkMaterial<0?0:min8MilkMaterial;
                        }
                        //Log.e("min8",min8MilkMaterial+"");
                        //Log.e("min8",(appDosage.getWeight()-(appDosage.getWater()/milkMaterial.getWater_rate()))+"extra");
                        real_numSkbarModulation.setMinLimit(min8MilkMaterial>min3?min8MilkMaterial:min3);
                        if(value+0.1>=materialMax2){
                            setCanDrag();
                            if(!(value+0.1>milkMaterial.getMax_weight())){
                                setTipText(MOSTMATERIAL);
                            }
                        }else if(value+0.1>=materialWater2){
                            setTipText(FULLCUP);
                            setCanDrag();
                        }else if(value-0.1<=min8MilkMaterial){
                            setCanDrag();
                        }else if(value-0.1<min3){
                           setTipText(MINDENSITY);
                            setCanDrag();
                        }
                      /*  else if(value+0.1>materialMaxForDensity2){
                           setTipText(MINDENSITY);
                            setCanDrag();
                        }*/
                        break;
                    case "巧克力粉":
                        float materialMax3 = (float) (chocolateMaterial.getMax_weight()- (totleChocolateMaterial-appDosage.getWeight()));
                        double materialWater3 =restoreWeight+( (MAXCOLUME -(totleWater-appDosage.getWeight()-appDosage.getWater())-restoreWeight-restoreWater) / (1f+chocolateMaterial.getWater_rate()));
                        float materialMaxForDensity3= appDosage.getWater() / MAXDENSITY;
                        //Log.e("max",materialMax3+"===="+materialWater3+"====");
                        float maxTag3 =materialMax3<materialWater3?materialMax3: (float) materialWater3;
                       // float max3=maxTag3<materialMaxForDensity3?maxTag3:materialMaxForDensity3;
                        //Log.e("max",materialMax3+"===="+materialWater3+"====");
                        real_numSkbarModulation.setMaxLimit(maxTag3);
                        float min8ChocolateMaterial=(float) ((appDosage.getWeight()-(appDosage.getWater()/chocolateMaterial.getWater_rate()))+min/chocolateMaterial.getWater_rate());
                        //Log.e("min8",min8ChocolateMaterial+"");
                        //Log.e("min8",(appDosage.getWeight()-(appDosage.getWater()/chocolateMaterial.getWater_rate()))+"extra");
                        if(min8ChocolateMaterial<min/chocolateMaterial.getWater_rate()){
                            min8ChocolateMaterial= min8ChocolateMaterial<0?0:min8ChocolateMaterial;
                        }
                        float min4 = (float) ((chocolateMaterial.getWater_rate() * appDosage.getWeight() - appDosage.getWater()) / (chocolateMaterial.getWater_rate() - MAXDENSITY));
                        real_numSkbarModulation.setMinLimit(min8ChocolateMaterial>min4?min8ChocolateMaterial:min4);
                        if(value+0.1>=materialMax3){
                            setCanDrag();
                            if(!(value+0.1>chocolateMaterial.getMax_weight())){
                                setTipText(MOSTMATERIAL);
                            }
                        }else if(value+0.1>=materialWater3){
                            setTipText(FULLCUP);
                            setCanDrag();
                        }else if(value-0.1<=min8ChocolateMaterial){
                            setCanDrag();
                        }else if(value-0.1<min4){
                           setTipText(MINDENSITY);
                            setCanDrag();
                        }
                        /*else if(value+0.1>materialMaxForDensity3){
                           setTipText(MINDENSITY);
                            setCanDrag();
                        }*/
                        break;
                }

            }

            //锁定情况下物料progress的处理
            private void locking(float value) {
                switch (appDosage.getMaterial_name()) {
                    case "糖":
                        appDosage.setWater((float) (restoreWater+((value-restoreWeight) * sugarMaterial.getWater_rate())));
                        //Log.e("tangProgress",appDosage.getWater()+"");
                        if(appDosage.getWater()<8.0){
                            appDosage.setWater(8.0f);
                        }
                        real_numSkbarModulation_water.setProgress(appDosage.getWater());
                        getTotleData();
                        setMaxAndMinValue(value);
                        break;
                    case "奶粉":
                        appDosage.setWater((float) (restoreWater+((value-restoreWeight) * milkMaterial.getWater_rate())));
                        if(appDosage.getWater()<8.0){
                            appDosage.setWater(8.0f);
                        }
                        real_numSkbarModulation_water.setProgress(appDosage.getWater());
                        getTotleData();
                        setMaxAndMinValue(value);
                        break;
                    case "巧克力粉":
                        appDosage.setWater((float) (restoreWater+((value-restoreWeight) * chocolateMaterial.getWater_rate())));
                        if(appDosage.getWater()<8.0){
                            appDosage.setWater(8.0f);
                        }
                        real_numSkbarModulation_water.setProgress(appDosage.getWater());
                        getTotleData();
                        setMaxAndMinValue(value);
                        break;
                }

            }

            /** 未锁定情况下物料progress的处理
             * 1、物料超过最大
             * 2、物料包括在内超过240ml
             * 2、浓度超过最大水分比5.0
             */

            private void unLocking(float value){
                switch (appDosage.getMaterial_name()){
                    case "咖啡浓度":
                        double maxCoffeeMaterial = coffeeMaterial.getMax_weight() - (totleCoffeeMaterial - appDosage.getWeight());
                        double maxCoffeeWater = (MAXCOLUME - (totleWater-appDosage.getSelfWater())) / coffeeMaterial.getWater_rate();
                        real_numSkbarModulation.setMaxLimit((float) (maxCoffeeMaterial<maxCoffeeWater?maxCoffeeMaterial:maxCoffeeWater));
                        real_numSkbarModulation.setMinLimit(0);
                        if(value+0.1>=maxCoffeeMaterial){
                            setCanDrag();
                            if(!(value+0.1>coffeeMaterial.getMax_weight())){
                               setTipText(MOSTMATERIAL);
                            }
                        }else if(value+0.1>=maxCoffeeWater){
                            setTipText(FULLCUP);
                            setCanDrag();
                        }
                        break;
                    case "糖":
                        double maxSugarMaterial = sugarMaterial.getMax_weight() - (totleSugarMaterial - appDosage.getWeight());
                        float maxSugarForWater = MAXCOLUME-(totleWater- appDosage.getWeight());
                        float maxSugarForDensity = appDosage.getWater() / MAXDENSITY;
                        float min= maxSugarMaterial<maxSugarForWater? (float) maxSugarMaterial :maxSugarForWater;
                        real_numSkbarModulation.setMaxLimit(min<maxSugarForDensity?min:maxSugarForDensity);
                        if(value+0.1>=maxSugarMaterial){
                            setCanDrag();
                            if(!(value+0.1>sugarMaterial.getMax_weight())){
                               setTipText(MOSTMATERIAL);
                            }
                        }else if(value+0.1>=maxSugarForWater){
                            setTipText(FULLCUP);
                            setCanDrag();
                        }else if(value+0.1>=maxSugarForDensity){
                            setTipText(MOSTDENSITY);
                            setCanDrag();
                        }
                        break;
                    case "奶粉":
                        double maxMilkMaterial = milkMaterial.getMax_weight() - (totleMilkMaterial - appDosage.getWeight());
                        float maxMilkForWater = MAXCOLUME - (totleWater-appDosage.getWeight());
                        float maxMilkForDensity = appDosage.getWater() / MAXDENSITY;
                        float min2= maxMilkMaterial<maxMilkForWater? (float) maxMilkMaterial :maxMilkForWater;
                        real_numSkbarModulation.setMaxLimit(min2<maxMilkForDensity?min2:maxMilkForDensity);
                        if(value+0.1>=maxMilkMaterial){
                            setCanDrag();
                            if(!(value+0.1>milkMaterial.getMax_weight())){
                               setTipText(MOSTMATERIAL);
                            }
                        }else if(value+0.1>=maxMilkForWater){
                            setTipText(FULLCUP);
                            setCanDrag();
                        }else if(value+0.1>=maxMilkForDensity){
                            setTipText(MOSTDENSITY);
                            setCanDrag();
                        }
                        break;
                    case "巧克力粉":
                        double maxChocolateMaterial = chocolateMaterial.getMax_weight() - (totleChocolateMaterial - appDosage.getWeight());
                        float maxChocolateForWater = MAXCOLUME -(totleWater- appDosage.getWeight());
                        float maxChocolateForDensity = appDosage.getWater() / MAXDENSITY;
                        float min3= maxChocolateMaterial<maxChocolateForWater? (float) maxChocolateMaterial :maxChocolateForWater;
                        real_numSkbarModulation.setMaxLimit(min3<maxChocolateForDensity?min3:maxChocolateForDensity);
                        if(value+0.1>=maxChocolateMaterial){
                            setCanDrag();
                            if(!(value+0.1>chocolateMaterial.getMax_weight())){
                               setTipText(MOSTMATERIAL);
                            }
                        }else if(value+0.1>=maxChocolateForWater){
                            setTipText(FULLCUP);
                            setCanDrag();
                        }else if(value+0.1>=maxChocolateForDensity){
                            setTipText(MOSTDENSITY);
                            setCanDrag();
                        }
                        break;
                }
            }

        }
    }
    //一位小数四舍五入
    private double getOneDec(double num){
        int i = (int) Math.round((double)num * 10f);
        double v = i / 10.0f;
        return v;
    }
    private void reFormAppDosages(float totleWater) {
        float decTotleWater = 0;
        if(appDosages==null||appDosages.size()==0){
            return;
        }
        for (AppDosages appDosage : appDosages) {
            appDosage.setWeight((float) getOneDec(appDosage.getWeight()));
            appDosage.setSelfWater((float) getOneDec(appDosage.getSelfWater()));
            appDosage.setWater((float) getOneDec(appDosage.getWater()));
            if(appDosage.getMaterial_name().equals("咖啡浓度")){
                decTotleWater+=(appDosage.getWater()+appDosage.getSelfWater());
            }else{
                decTotleWater+=(appDosage.getWeight()+appDosage.getWater());
            }
        }
        float extra = totleWater - decTotleWater;
        //如果咖啡的额外水量加上偏差不小于0
        for (AppDosages appDosage : appDosages) {
            if(appDosage.getMaterial_name().equals("咖啡浓度")){
                if(!((appDosage.getWater()+extra)<0)){
                    appDosage.setWater(appDosage.getWater()+extra);
                    return;
                }else{}
            }else{}
        }
        //如果没有咖啡，则在剩下的物料中取加上偏差后影响最小的
        float maxRatio=0;
        int maxIndex=0;
        for (int i=0;i<appDosages.size();i++) {
            if(!appDosages.get(i).getMaterial_name().equals("咖啡浓度")){
                if(((appDosages.get(i).getWater()+extra)/appDosages.get(i).getWeight())>maxRatio){
                    maxRatio=(appDosages.get(i).getWater()+extra)/appDosages.get(i).getWeight();
                    maxIndex=i;
                }
            }
        }
        appDosages.get(maxIndex).setWater(appDosages.get(maxIndex).getWater()+extra);
        getTotleData();
    }
    private void getTotleData() {
        totleWater=0;
        totleChocolateMaterial=0;
        totleCoffeeMaterial=0;
        totleSugarMaterial=0;
        totleMilkMaterial=0;
        totleCoffeeWater=0;
        totleSugarWater=0;
        totleMilkWater=0;
        totleChocolateWater=0;
        totlePrice=BASICPRICE;
        for (AppDosages appDosage : appDosages) {
            if(appDosage.getMaterial_name()==null){
                return;
            }
            switch (appDosage.getMaterial_name()){
                case "糖":
                    totleSugarWater+=appDosage.getWater();
                    totleWater=(totleWater+appDosage.getWater()+appDosage.getWeight());
                    totleSugarMaterial +=appDosage.getWeight();
                    //totlePrice +=getOneDec(appDosage.getWeight())*Double.valueOf(sugarMaterial.getAdjust_price());
                    break;
                case "咖啡浓度":
                    totleCoffeeWater+=(appDosage.getWater()+appDosage.getSelfWater());
                    totleWater+=(appDosage.getWater()+appDosage.getSelfWater());
                    totleCoffeeMaterial +=appDosage.getWeight();
                   // totlePrice +=getOneDec((float) (appDosage.getWeight())*Double.valueOf(coffeeMaterial.getAdjust_price()))/10f;
                    break;
                case "奶粉":
                    totleMilkWater+=appDosage.getWater();
                    totleWater=(totleWater+appDosage.getWater()+appDosage.getWeight());
                    totleMilkMaterial +=appDosage.getWeight();
                    //totlePrice +=getOneDec(appDosage.getWeight())*Double.valueOf(milkMaterial.getAdjust_price());
                    break;
                case "巧克力粉":
                    totleChocolateWater+=appDosage.getWater();
                    totleWater=(totleWater+appDosage.getWater()+appDosage.getWeight());
                    totleChocolateMaterial +=appDosage.getWeight();
                   // totlePrice +=getOneDec(appDosage.getWeight())*Double.valueOf(chocolateMaterial.getAdjust_price());
                    break;
            }
            double sum1 = MyMath.add(BASICPRICE+"", MyMath.mul(MyMath.round(totleChocolateMaterial,1)+"",Double.valueOf(chocolateMaterial.getAdjust_price())+"")+"");
            double sum2 = MyMath.add(sum1+"", MyMath.mul(MyMath.round(totleCoffeeMaterial,1)+"", (Double.valueOf(coffeeMaterial.getAdjust_price())/10.0f)+"")+"" );
            double sum3 = MyMath.add(sum2+"", MyMath.mul(MyMath.round(totleMilkMaterial,1)+"" ,Double.valueOf(milkMaterial.getAdjust_price())+"")+"");
            totlePrice = MyMath.add(sum3+"", MyMath.mul(MyMath.round(totleSugarMaterial,1)+"" , Double.valueOf(sugarMaterial.getAdjust_price())+"")+"");
            /*totlePrice =BASICPRICE+(getOneDec(totleChocolateMaterial))*Double.valueOf(chocolateMaterial.getAdjust_price())+(getOneDec(totleCoffeeMaterial))*Double.valueOf(coffeeMaterial.getAdjust_price())/10.0f
                    +getOneDec(totleMilkMaterial)*Double.valueOf(milkMaterial.getAdjust_price())+getOneDec(totleSugarMaterial)*Double.valueOf(sugarMaterial.getAdjust_price());*/

        }
        //reFormMaterialWater();
        LogUtil.e("FineactualAppdosage",appDosages.toString());
        LogUtil.e("actualTotleWater",totleWater+"");
        LogUtil.e("totlePrice",totlePrice+"");
        totleVolume = Math.round(totleWater) >= 240 ? 240  : (int) Math.round(totleWater);
        tv_Volume.setText(totleVolume+"");
        realTotlePrice = df.format(MyMath.round(totlePrice,2))+"";
        tv_price.setText(realTotlePrice);
    }
    private float getValueFromText(TextView tv_max) {
        String s = tv_max.getText().toString();
        Float aFloat = Float.valueOf(s);
        return aFloat;
    }
    @Event(value = {R.id.button_cancel,R.id.button_reset,R.id.button_confirm,R.id.button_cancel2,R.id.button_reset2,R.id.button_confirm2})
    private void onClick(View v){
        switch (v.getId()){
            case R.id.button_cancel:
                finish();
                this.overridePendingTransition(R.anim.slide_in_left_base,
                        R.anim.slide_out_right_base);
                break;
            case R.id.button_reset:
                tempPreTag=-1;
                this.appDosages.clear();
                for (RestoreAppDosages oldAppDosage : oldAppDosages) {
                    appDosages.add(new AppDosages(oldAppDosage.getId(),oldAppDosage.getWeight(),oldAppDosage.getSelfWater(),oldAppDosage.getWater(),oldAppDosage.getSequence(),oldAppDosage.getMaterial_name()));
                }
                Collections.reverse(appDosages);
                getTotleData();
                fineItemAdapter.notifyDataSetChanged();
               // initRecyclerView();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isIdle=true;
                    }
                },50);
                ccv.setData(this.appDosages,getApplicationContext());
                break;
            case R.id.button_confirm:
                Intent intent = new Intent();
                reFormAppDosages(totleVolume);
                Collections.reverse(appDosages);
                intent.putExtra("appDosages", (Serializable) appDosages);
                intent.putExtra("reclen",reclen);
                setResult(2,intent);
                finish();
                this.overridePendingTransition(R.anim.slide_in_left_base,
                        R.anim.slide_out_right_base);
                break;
            case R.id.button_cancel2:
                finish();
                this.overridePendingTransition(R.anim.slide_in_left_base,
                        R.anim.slide_out_right_base);
                break;
            case R.id.button_reset2:
                tempPreTag=-1;
                this.appDosages.clear();
                for (RestoreAppDosages oldAppDosage : oldAppDosages) {
                    appDosages.add(new AppDosages(oldAppDosage.getId(),oldAppDosage.getWeight(),oldAppDosage.getSelfWater(),oldAppDosage.getWater(),oldAppDosage.getSequence(),oldAppDosage.getMaterial_name()));
                }
                Collections.reverse(appDosages);
                getTotleData();
                fineItemAdapter.notifyDataSetChanged();
                // initRecyclerView();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isIdle=true;
                    }
                },50);
                ccv.setData(this.appDosages,getApplicationContext());
                break;

            case R.id.button_confirm2:
                if(Float.valueOf(totleVolume)<40){
                   setTipText(MINUVOLUME);
                    return;
                }
                Intent intent2 = new Intent();
                reFormAppDosages(totleVolume);
                Collections.reverse(appDosages);
                intent2.putExtra("appDosages", (Serializable) appDosages);
                intent2.putExtra("volume",totleVolume);
                intent2.putExtra("price",realTotlePrice);
                setResult(2,intent2);
                finish();
                this.overridePendingTransition(R.anim.slide_in_left_base,
                        R.anim.slide_out_right_base);
                break;
        }
    }
    private float decimalFloat(float mProgress){
        BigDecimal bigDecimal = BigDecimal.valueOf(mProgress);
        return bigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
    }
    private void setTipText(String s){
        top = DensityUtil.dp2px(this, 0);
        inTop = DensityUtil.dp2px(this, 70);
        LogUtil.e("tv_notify","translationY"+tv_notify.getTranslationY()+"intop"+ inTop);
        if(tv_notify.getText().toString().equals(s)&&(-tv_notify.getTranslationY())!= inTop){
            handler.removeCallbacksAndMessages(null);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ObjectAnimator anim2 = ObjectAnimator.ofFloat(tv_notify, "translationY", top,-inTop);
                    anim2.setDuration(300);
                    anim2.start();
                }
            },2000);
            return;
        }
        tv_notify.setText(s);
        tv_notify.setVisibility(View.VISIBLE);
        final float translationY = tv_notify.getTranslationY();
        final ObjectAnimator anim = ObjectAnimator.ofFloat(tv_notify, "translationY", -inTop, top);
        anim.setDuration(300);
        anim.start();
        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator anim2 = ObjectAnimator.ofFloat(tv_notify, "translationY", top,-inTop);
                anim2.setDuration(300);
                anim2.start();
            }
        },2000);
    }
}
