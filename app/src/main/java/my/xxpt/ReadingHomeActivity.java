package my.xxpt;

import static my.xxpt.Util.listRead;
import static my.xxpt.Util.strings;
import static my.xxpt.Util.timeConversion;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.gyf.immersionbar.ImmersionBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class ReadingHomeActivity extends AppCompatActivity {


    private ArrayList<DataReading> list1;
    private ArrayList<DataReading> list2;
    private ArrayList<DataReading> list3;
    private ArrayList<DataReading> list4;
    private ArrayList<DataReading> list5;

    int totalTime = 60 * 60;
    private TextView textView2;
    private int[] ints;
    private Button topic1;
    private Button topic2;
    private Button topic3;
    private Button topic4;
    private Button topic5;
    private Button post;
    private Button zanli;
    private Gson gson = new Gson();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading_home);
        ImmersionBar.with(ReadingHomeActivity.this).statusBarColor(R.color.main).init();
        EventBus.getDefault().register(this);
        textView2 = findViewById(R.id.textView2);


        topic1 = findViewById(R.id.topic1);
        topic2 = findViewById(R.id.topic2);
        topic3 = findViewById(R.id.topic3);
        topic4 = findViewById(R.id.topic4);
        topic5 = findViewById(R.id.topic5);
        post = findViewById(R.id.post);
        zanli = findViewById(R.id.zanli);
        zanli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击暂离后   需要把时间   答题记录存储到缓存中

                AlertDialog.Builder dialog = new AlertDialog.Builder(ReadingHomeActivity.this);
                dialog.setTitle("WARNING!").
                        setMessage("Are you sure to take a break?").
                        setNegativeButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                //todo  保存数据并结束页面
                                IntsData intsData = new IntsData();
                                intsData.setInts(ints);
                                SPUtils.put(ReadingHomeActivity.this, "ints", gson.toJson(intsData));
                                SPUtils.put(ReadingHomeActivity.this, "array1", gson.toJson(new ArrayData(list1)));
                                SPUtils.put(ReadingHomeActivity.this, "array2", gson.toJson(new ArrayData(list2)));
                                SPUtils.put(ReadingHomeActivity.this, "array3", gson.toJson(new ArrayData(list3)));
                                SPUtils.put(ReadingHomeActivity.this, "array4", gson.toJson(new ArrayData(list4)));
                                SPUtils.put(ReadingHomeActivity.this, "array5", gson.toJson(new ArrayData(list5)));
                                SPUtils.put(ReadingHomeActivity.this, "listRead", gson.toJson(new ArrayData(listRead)));
                                SPUtils.put(ReadingHomeActivity.this, "strings", gson.toJson(new StringsData(strings)));
                                SPUtils.put(ReadingHomeActivity.this, "totalTime", totalTime);
                                finish();
                            }
                        });
                dialog.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                dialog.show();


            }
        });


        if (TextUtils.isEmpty(SPUtils.get(ReadingHomeActivity.this, "ints", "").toString())) {
            initData();
            ints = new int[]{0, 15, 30, 40, 45};
        } else {
            ints = gson.fromJson(SPUtils.get(ReadingHomeActivity.this, "ints", "").toString(), IntsData.class).getInts();
            totalTime = (int) SPUtils.get(ReadingHomeActivity.this, "totalTime", 60 * 60);
            //这里的话需要加载之前的数据

            list1 = gson.fromJson(SPUtils.get(ReadingHomeActivity.this, "array1", "").toString(), ArrayData.class).getArrayList();
            list2 = gson.fromJson(SPUtils.get(ReadingHomeActivity.this, "array2", "").toString(), ArrayData.class).getArrayList();
            list3 = gson.fromJson(SPUtils.get(ReadingHomeActivity.this, "array3", "").toString(), ArrayData.class).getArrayList();
            list4 = gson.fromJson(SPUtils.get(ReadingHomeActivity.this, "array4", "").toString(), ArrayData.class).getArrayList();
            list5 = gson.fromJson(SPUtils.get(ReadingHomeActivity.this, "array5", "").toString(), ArrayData.class).getArrayList();


            if (listRead == null)
                listRead = gson.fromJson(SPUtils.get(ReadingHomeActivity.this, "listRead", "").toString(), ArrayData.class).getArrayList();


            if (strings == null)
                strings = gson.fromJson(SPUtils.get(ReadingHomeActivity.this, "strings", "").toString(), StringsData.class).getStrings();

            topic1.setEnabled(true);
            topic2.setEnabled(true);
            topic3.setEnabled(true);
            topic4.setEnabled(true);
            topic5.setEnabled(true);
            post.setEnabled(true);

        }
        textView2.setText(timeConversion(totalTime));
        //进入这个界面开始倒计时  并且重新初始化答案记录的集合
        handler.postDelayed(runnable, 1000);
    }

    Handler handler = new Handler();

    Runnable runnable = new Runnable() {

        @Override
        public void run() {
            totalTime = totalTime - 1;
            //
            textView2.setText(timeConversion(totalTime));


            if (totalTime == 0) {
                todoUpload(1);
                //时间到  发送通知   让其他界面关闭
                EventBus.getDefault().post(new CloseEvent());
            } else {
                EventBus.getDefault().post(new TimeEvent(totalTime));
                handler.postDelayed(runnable, 1000);
            }

        }
    };


    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();

        if (strings == null || strings.size() == 0)
            return;
        initNum();

    }

    private void initNum() {
        //检验有无答题数据
        int temp1 = 0;
        int temp2 = 0;
        int temp3 = 0;
        int temp4 = 0;
        int temp5 = 0;

        for (int i = 0; i < 15; i++) {
            if (!TextUtils.isEmpty(strings.get(i))) {
                temp1++;
            }
        }

        for (int i = 15; i < 30; i++) {
            if (!TextUtils.isEmpty(strings.get(i))) {
                temp2++;
            }
        }
        for (int i = 30; i < 40; i++) {
            if (!TextUtils.isEmpty(strings.get(i))) {
                temp3++;
            }
        }
        for (int i = 40; i < 45; i++) {
            if (!TextUtils.isEmpty(strings.get(i))) {
                temp4++;
            }
        }
        for (int i = 45; i < 50; i++) {
            if (!TextUtils.isEmpty(strings.get(i))) {
                temp5++;
            }
        }

        topic1.setText(((temp1+"/15  ")));
        topic2.setText(((temp2+"/15  ")));
        topic3.setText(((temp3+"/10  ")));
        topic4.setText(((temp4+"/5  ")));
        topic5.setText(((temp5+"/5  ")));
    }


    private void todoUpload(int type) {
        //交卷  type 1 时间结束   2主动交卷
        int trueNum = 0;

        for (int i = 0; i < listRead.size(); i++) {

            if (!TextUtils.isEmpty(strings.get(i)) && strings.get(i).equals(listRead.get(i).getAnswer().toUpperCase())) {
                trueNum++;
            }
        }

        final int finalTrueNum = trueNum;
        AlertDialog.Builder dialog = new AlertDialog.Builder(ReadingHomeActivity.this);
        dialog.setTitle("WARNING!").
                setMessage("Answer time is over! You are both correct" + trueNum + "/50！").
                setNegativeButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        initErr();
                        Intent intent = new Intent(ReadingHomeActivity.this, ReadingAEndActivity.class);
                        intent.putExtra("true", finalTrueNum);
                        intent.putExtra("total", strings.size());
                        startActivity(intent);
                        SPUtils.clear(ReadingHomeActivity.this);
                        finish();
                    }
                });
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
        handler = null;
        EventBus.getDefault().unregister(this);
        //结束的时候  如果不是暂离  直接清空

        if (TextUtils.isEmpty(SPUtils.get(ReadingHomeActivity.this, "ints", "").toString())){
            SPUtils.clear(ReadingHomeActivity.this);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updatePosition(PositionEvent positionEvent) {
        ints[positionEvent.getType()] = positionEvent.getPosition();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void toB(ToBEvent toBEvent) {
        startActivity(new Intent(ReadingHomeActivity.this, ReadingBHomeActivity.class));
        finish();
    }


    private void initData() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //todo  获取realtime实例
        DatabaseReference myRef = database.getReference("read_bandA").child("RECORDS");
        //todo  获取到bandA 整体数据
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //todo  解析数据

                Iterator<DataSnapshot> iterator = snapshot.getChildren().iterator();


                ArrayList<DataReading> list0 = new ArrayList<>();
                while (iterator.hasNext()) {
                    //todo  将数据添加到list中

                    /*DataSnapshot next = iterator.next();
                    next.g*/
                    DataReading value = iterator.next().getValue(DataReading.class);
                    list0.add(value);
                }

                if (listRead == null)
                    listRead = new ArrayList<>();
                else listRead.clear();

                list1 = new ArrayList<>();
                list2 = new ArrayList<>();
                list3 = new ArrayList<>();
                list4 = new ArrayList<>();
                list5 = new ArrayList<>();

                list0.forEach(x -> {
                    if (x.type != null)
                        switch (x.type) {
                            case "1":
                                list1.add(x);
                                break;
                            case "2":
                                list2.add(x);
                                break;
                            case "3":
                                list3.add(x);
                                break;
                            case "4":
                                list4.add(x);
                                break;
                            case "5":
                                list5.add(x);
                                break;
                        }
                });


                HashSet<Integer> set = new HashSet<Integer>();
                Random random = new Random();

                while (set.size() < 15) {
                    int index = random.nextInt(list1.size());
                    if (!set.contains(index)) {
                        set.add(index);
                        listRead.add(list1.get(index));
                    }
                }

                set.clear();
                while (set.size() < 15) {
                    int index = random.nextInt(list2.size());
                    if (!set.contains(index)) {
                        set.add(index);
                        listRead.add(list2.get(index));
                    }
                }

                set.clear();
                ArrayList<DataReading> templist3 = new ArrayList<>();
                while (set.size() < 2) {
                    int index = random.nextInt(list3.size());
                    if (!set.contains(index)) {
                        if (templist3.size() > 0) {
                            if (!list3.get(index).getTopicimage().equals(templist3.get(0).getTopicimage())) {
                                //如果一致   那么题目已经加进去了   不处理  重新随机   不一致则表示可以加入  循环加入5条数据
                                set.add(index);

                                //此时size为2  不再循环
                                templist3.add(list3.get(index));
                                for (int i = 0; i < list3.size(); i++) {
                                    if (list3.get(i).getTopicimage().equals(list3.get(index).getTopicimage())) {
                                           //会加入5条数据
                                           listRead.add(list3.get(i));

                                    }
                                }
                            }
                        } else {
                            set.add(index);

                            templist3.add(list3.get(index));
                            //这时候需要把所有的图片一致的题目添加到这个数组中
                            for (int i = 0; i < list3.size(); i++) {
                                if (list3.get(i).getTopicimage().equals(list3.get(index).getTopicimage())) {
                                    //会加入5条数据
                                    listRead.add(list3.get(i));
                                }
                            }
                        }
                    }
                }
                //释放
                templist3 = null;
                set.clear();
                while (set.size() < 1) {
                    int index = random.nextInt(list4.size());
                    if (!set.contains(index)) {
                        set.add(index);

                        for (int i = 0; i < list4.size(); i++) {
                            if (list4.get(i).getTopicno().equals(list4.get(index).getTopicno())) {
                                //会加入5条数据
                                listRead.add(list4.get(i));
                            }
                        }

                    }
                }
                set.clear();
                while (set.size() < 5) {
                    int index = random.nextInt(list5.size());
                    if (!set.contains(index)) {
                        set.add(index);
                        listRead.add(list5.get(index));
                    }
                }
                //这时候  数据初始化完成了
                //创建一个数组   存储用户的答题数据
                if (strings == null) {
                    strings = new ArrayList<>();
                } else {
                    strings.clear();
                }

                for (int i = 0; i < listRead.size(); i++) {
                    strings.add("");
                }


                Toast.makeText(ReadingHomeActivity.this, "Data initialisation complete", Toast.LENGTH_SHORT).show();

                initNum();
                topic1.setEnabled(true);
                topic2.setEnabled(true);
                topic3.setEnabled(true);
                topic4.setEnabled(true);
                topic5.setEnabled(true);
                post.setEnabled(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void topic1(View view) {
        startActivity(new Intent(ReadingHomeActivity.this, ReadingType1Activity.class).putExtra("position", ints[0]));
    }

    public void topic2(View view) {
        startActivity(new Intent(ReadingHomeActivity.this, ReadingType2Activity.class).putExtra("position", ints[1]));
    }

    public void topic3(View view) {
        startActivity(new Intent(ReadingHomeActivity.this, ReadingType3Activity.class).putExtra("position", ints[2]));
    }

    public void topic4(View view) {
        startActivity(new Intent(ReadingHomeActivity.this, ReadingType4Activity.class).putExtra("position", ints[3]));
    }

    public void topic5(View view) {
        startActivity(new Intent(ReadingHomeActivity.this, ReadingType5Activity.class).putExtra("position", ints[4]));
    }

    public void upLoad(View view) {

        int trueNum = 0;

        for (int i = 0; i < listRead.size(); i++) {

            if (!TextUtils.isEmpty(strings.get(i)) && strings.get(i).equals(listRead.get(i).getAnswer().toUpperCase())) {
                trueNum++;
            }
        }


        AlertDialog.Builder dialog = new AlertDialog.Builder(ReadingHomeActivity.this);
        int finalTrueNum = trueNum;
        dialog.setTitle("WARNING!").
                setMessage("Do you confirm to submit the test?").
                setNegativeButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //todo  到新的界面交卷  传递做题数据
                        Intent intent = new Intent(ReadingHomeActivity.this, ReadingAEndActivity.class);
                        intent.putExtra("true", finalTrueNum);
                        intent.putExtra("total", strings.size());
                        startActivity(intent);
                        SPUtils.clear(ReadingHomeActivity.this);

                        //写入答题的结果数据
                        FirebaseDatabase.getInstance().getReference()
                                .child("record").child(FirebaseAuth.getInstance().getUid())
                                .child("readinglevel")
                                .child("num").setValue(finalTrueNum+"");
                        FirebaseDatabase.getInstance().getReference()
                                .child("record").child(FirebaseAuth.getInstance().getUid())
                                .child("readinglevel")
                                .child("level").setValue(finalTrueNum>39?"4":finalTrueNum>27?"3":finalTrueNum>21?"2":finalTrueNum>7?"1":"0");
                        initErr();

                        finish();
                    }
                });
        dialog.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void initErr() {
        //错题存储到  firebase

        List<DataReading> errList = new ArrayList<>();
        for (int i = 0; i < listRead.size(); i++) {
            if (TextUtils.isEmpty(strings.get(i)) || !strings.get(i).equals(listRead.get(i).getAnswer().toUpperCase())) {
                errList.add(listRead.get(i));
            }
        }

        FirebaseDatabase.getInstance().getReference().child("record").child(FirebaseAuth.getInstance().getUid()).child("list2").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    //todo  如果没有数据
                    FirebaseDatabase.getInstance().getReference().child("record").child(FirebaseAuth.getInstance().getUid()).child("list2").setValue(errList);
                } else {
                    //todo 添加数据
                    Iterator<DataSnapshot> iterator = snapshot.getChildren().iterator();
                    ArrayList<DataReading> records = new ArrayList<>();
                    while (iterator.hasNext()) {
                        DataSnapshot next = iterator.next();
                        DataReading value = next.getValue(DataReading.class);
                        records.add(value);
                    }
                    records.addAll(errList);
                    FirebaseDatabase.getInstance().getReference().child("record").child(FirebaseAuth.getInstance().getUid()).child("list2").setValue(records);
                }
                //todo  记录添加完成后
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}