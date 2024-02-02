package my.xxpt;

import static my.xxpt.Util.listRead2;
import static my.xxpt.Util.strings2;
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
import java.util.Random;

public class ReadingBHomeActivity extends AppCompatActivity {


    private ArrayList<DataReading2> list1;
    private ArrayList<DataReading2> list2;

    int totalTime = 60 *60;
    private TextView textView2;
    private int[] ints;
    private Button topic1;
    private Button topic2;
    private Button post;
    private Button zanli;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_readingb_home);
        ImmersionBar.with(ReadingBHomeActivity.this).statusBarColor(R.color.main).init();
        EventBus.getDefault().register(this);
        textView2 = findViewById(R.id.textView2);


        topic1 = findViewById(R.id.topic1);
        topic2 = findViewById(R.id.topic2);
        post = findViewById(R.id.post);
        zanli = findViewById(R.id.zanli);
        zanli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo 点击后保存数据及时间
                SPUtils.put(ReadingBHomeActivity.this,"b_time",totalTime);
                SPUtils.put(ReadingBHomeActivity.this,"int1",ints[0]);
                SPUtils.put(ReadingBHomeActivity.this,"int2",ints[1]);
                SPUtils.put(ReadingBHomeActivity.this,"int3",ints[2]);
                SPUtils.put(ReadingBHomeActivity.this, "ints", gson.toJson(ints));
                FirebaseDatabase
                        .getInstance()
                        .getReference()
                        .child("temp")
                                .child(FirebaseAuth.getInstance().getUid()+"")
                                        .child("listRead2")
                                                .setValue(listRead2);
                FirebaseDatabase
                        .getInstance()
                        .getReference()
                        .child("temp")
                        .child(FirebaseAuth.getInstance().getUid()+"")
                        .child("listRead2string")
                        .setValue(strings2);

                /*SPUtils.put(ReadingBHomeActivity.this,"listRead2",gson.toJson(listRead2));
                SPUtils.put(ReadingBHomeActivity.this,"listRead2string",gson.toJson(strings2));*/
                finish();
            }
        });
        ints = new int[]{0,0,0};
        initData();
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
                //时间到  发送通知   让其他界面关闭
                EventBus.getDefault().post(new CloseEvent());
                todoUpload(1);

            } else {
                EventBus.getDefault().post(new TimeEvent(totalTime));
                handler.postDelayed(runnable, 1000);
            }

        }
    };

    private void todoUpload(int type) {

        int trueNum = 0;

        for (int i = 0; i < listRead2.size(); i++) {

            if (!TextUtils.isEmpty(strings2.get(i)) && strings2.get(i).equals(listRead2.get(i).getAnswer().toUpperCase())) {
                trueNum++;
            }
        }


        AlertDialog.Builder dialog = new AlertDialog.Builder(ReadingBHomeActivity.this);
        dialog.setTitle("Time's Up!").
                setMessage("You got "+trueNum+"/"+strings2.size()+" questions right").
                setNegativeButton("Sure", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
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
    }

    Gson gson = new Gson();
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updatePosition(PositionEvent positionEvent) {
        ints[positionEvent.getType()] = positionEvent.getPosition();
    }


    public static String TAG="-----LongLog-----";
    public  static void  loge(String str){
        int max_str_length=2001-TAG.length();
        //大于4000时
        while (str.length()>max_str_length){
            Log.e(TAG, str.substring(0,max_str_length) );
            str=str.substring(max_str_length);
        }
        //剩余部分
        Log.e(TAG, str );
    }


    @SuppressLint("SetTextI18n")
    private void initData() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //判断是否暂离
        String intsString = (SPUtils.get(ReadingBHomeActivity.this, "ints", "")+"").toString();

        if (!TextUtils.isEmpty(intsString)){
            String b_time = (SPUtils.get(ReadingBHomeActivity.this, "b_time", -100)+"").toString();
            totalTime = Integer.parseInt(b_time);
            ints[0] = Integer.parseInt((SPUtils.get(ReadingBHomeActivity.this, "int1", -100)+"").toString());
            ints[1] = Integer.parseInt((SPUtils.get(ReadingBHomeActivity.this, "int2", -100)+"").toString());
            ints[2] = Integer.parseInt((SPUtils.get(ReadingBHomeActivity.this, "int3", -100)+"").toString());
            if (listRead2 == null)
                listRead2 = new ArrayList<>();
            listRead2.clear();
            if (strings2 == null)
                strings2 = new ArrayList<>();
            strings2.clear();

            FirebaseDatabase.getInstance().getReference()
                    .child("temp")
                    .child(FirebaseAuth.getInstance().getUid()+"")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            boolean exists = snapshot.exists();

                            if (exists){
                                Iterable<DataSnapshot> children = snapshot.getChildren();
                                Iterator<DataSnapshot> iterator = children.iterator();
                                while (iterator.hasNext()){
                                    DataSnapshot next = iterator.next();
                                    String key = next.getKey()+"";
                                    if (key.equals("listRead2")){

                                        Iterator<DataSnapshot> iterator1 = next.getChildren().iterator();
                                        while (iterator1.hasNext()){
                                            listRead2.add(iterator1.next().getValue(DataReading2.class));
                                        }
                                    }else if (next.getKey().equals("listRead2string")){
                                        Iterator<DataSnapshot> iterator1 = next.getChildren().iterator();
                                        while (iterator1.hasNext()){
                                            strings2.add(iterator1.next().getValue().toString());
                                        }
                                    }
                                }

                                topic1.setEnabled(true);
                                topic2.setEnabled(true);
                                post.setEnabled(true);


                                //把做题数据体现在按钮上

                                int temp1 = 0;
                                int temp2 = 0;

                                for (int i = 0; i < strings2.size(); i++) {
                                    if (i<ints[1]&&!TextUtils.isEmpty(strings2.get(i))){
                                        temp1++;
                                    }else if (i<ints[2]&&!TextUtils.isEmpty(strings2.get(i))){
                                        temp2++;
                                    }
                                }


                                topic1.setText("("+temp1+"/"+(ints[1])+")");
                                topic2.setText(" ("+temp2+"/35)");

                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });



          /*  String listRead22 = SPUtils.get(ReadingBHomeActivity.this, "listRead2", "").toString();
            loge(listRead22);
            ArrayDataB arrayDataB = gson.fromJson(listRead22, ArrayDataB.class);
            ArrayList<DataReading2> listRead21 = gson.fromJson(SPUtils.get(ReadingBHomeActivity.this, "listRead2", "").toString(), ArrayDataB.class).getArrayList();
            listRead2.addAll(listRead21);

            DatabaseReference databaseReference = database.getReference("temp").child("RECORDS");





            ArrayList<String> listRead2string = gson.fromJson(SPUtils.get(ReadingBHomeActivity.this, "listRead2string", "").toString(), ArrayDataString.class).getArrayList();
            strings2.addAll(listRead2string);*/


            return;
        }


        //todo  获取realtime实例
        DatabaseReference myRef = database.getReference("read_bandb").child("RECORDS");
        //todo  获取到bandA 整体数据
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //todo  解析数据

                Iterator<DataSnapshot> iterator = snapshot.getChildren().iterator();


                ArrayList<DataReading2> list0 = new ArrayList<>();
                while (iterator.hasNext()) {
                    //todo  将数据添加到list中

                    /*DataSnapshot next = iterator.next();
                    next.g*/
                    DataReading2 value = iterator.next().getValue(DataReading2.class);
                    list0.add(value);
                }
                listRead2 = new ArrayList<>();

                list1 = new ArrayList<>();
                list2 = new ArrayList<>();

                list0.forEach(x -> {
                    if (x.type != null)
                        switch (x.type) {
                            case "1":
                                list1.add(x);
                                break;
                            case "2":
                                list2.add(x);
                                break;
                        }
                });


                HashSet<Integer> set = new HashSet<Integer>();
                Random random = new Random();
                ArrayList<DataReading2> templist = new ArrayList<>();
                while (set.size() < 3) {
                    int index = random.nextInt(list1.size());
                    if (!set.contains(index)) {
                        if (templist.size() > 0) {
                            if (!list1.get(index).getTopicno().equals(templist.get(0).getTopicno())) {
                                //如果一致   那么题目已经加进去了   不处理  重新随机   不一致则表示可以加入  循环加入5条数据
                                set.add(index);
                                //此时size为2  不再循环
                                templist.add(list1.get(index));
                                for (int i = 0; i < list1.size(); i++) {
                                    if (list1.get(i).getTopicno().equals(list1.get(index).getTopicno())) {
                                        //会加入5条数据
                                        listRead2.add(list1.get(i));
                                    }
                                }
                            }
                        } else {
                            set.add(index);
                            templist.add(list1.get(index));
                            //这时候需要把所有的图片一致的题目添加到这个数组中
                            for (int i = 0; i < list1.size(); i++) {
                                if (list1.get(i).getTopicno().equals(list1.get(index).getTopicno())) {
                                    //会加入5条数据
                                    listRead2.add(list1.get(i));
                                }
                            }
                        }
                    }
                }
                //释放
                templist = null;

                ints[1] =listRead2.size();
                ints[2] =listRead2.size();
                set.clear();
                while (set.size() < 35) {
                    int index = random.nextInt(list2.size());
                    if (!set.contains(index)) {
                        set.add(index);
                        listRead2.add(list2.get(index));
                    }
                }
                //这时候  数据初始化完成了
                //创建一个数组   存储用户的答题数据
                if (strings2 == null) {
                    strings2 = new ArrayList<>();
                } else {
                    strings2.clear();
                }

                for (int i = 0; i < listRead2.size(); i++) {
                    strings2.add("");
                }


                Toast.makeText(ReadingBHomeActivity.this, "Data initialisation complete.", Toast.LENGTH_SHORT).show();


                topic1.setText(" (0/"+(ints[1])+")");
                topic2.setText(" (0/35)");
                topic1.setEnabled(true);
                topic2.setEnabled(true);
                post.setEnabled(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void topic1(View view) {
        startActivity(new Intent(ReadingBHomeActivity.this, ReadingBType1Activity.class)
                .putExtra("position", ints[0]));
    }

    public void topic2(View view) {
        startActivity(new Intent(ReadingBHomeActivity.this, ReadingBType2Activity.class)
                .putExtra("position", ints[1])
                .putExtra("tag",ints[2])
        );
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (strings2 == null){
            topic1.setText(" (0/"+(ints[1])+")");
            topic2.setText(" (0/35)");
            return;
        }

        int temp1 = 0;
        int temp2 = 0;

        for (int i = 0; i < strings2.size(); i++) {
            if (i<ints[1]&&!TextUtils.isEmpty(strings2.get(i))){
                temp1++;
            }else if (i<ints[2]&&!TextUtils.isEmpty(strings2.get(i))){
                temp2++;
            }
        }


        topic1.setText(" ("+temp1+"/"+(ints[1])+")");
        topic2.setText(" ("+temp2+"/35)");
    }

    public void upLoad(View view) {

        int trueNum = 0;

        for (int i = 0; i < listRead2.size(); i++) {

            if (!TextUtils.isEmpty(strings2.get(i)) && strings2.get(i).equals(listRead2.get(i).getAnswer().toUpperCase())) {
                trueNum++;
            }
        }


        AlertDialog.Builder dialog = new AlertDialog.Builder(ReadingBHomeActivity.this);
        int finalTrueNum = trueNum;
        dialog.setTitle("Congratulations").
                setMessage("You have corrctly answwered"+trueNum+"/"+strings2.size()).
                setNegativeButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();


                        //根据答题结果更新数据
                        FirebaseDatabase.getInstance().getReference()
                                .child("record").child(FirebaseAuth.getInstance().getUid())
                                .child("readinglevel")
                                .child("num").setValue(finalTrueNum +"");
                        FirebaseDatabase.getInstance().getReference()
                                .child("record").child(FirebaseAuth.getInstance().getUid())
                                .child("readinglevel")
                                .child("level").setValue(finalTrueNum>21?"4":finalTrueNum>7?"3":"0");
                        //  SPUtils.put(ReadingBHomeActivity.this,"b_time",totalTime);
                        //                SPUtils.put(ReadingBHomeActivity.this,"int1",ints[0]);
                        //                SPUtils.put(ReadingBHomeActivity.this,"int2",ints[1]);
                        //                SPUtils.put(ReadingBHomeActivity.this,"int3",ints[2]);
                        SPUtils.remove(ReadingBHomeActivity.this,"b_time");
                        SPUtils.remove(ReadingBHomeActivity.this,"int1");
                        SPUtils.remove(ReadingBHomeActivity.this,"int2");
                        SPUtils.remove(ReadingBHomeActivity.this,"int3");
                        strings2.clear();
                        listRead2.clear();
                        finish();
                    }
                });
        dialog.setPositiveButton("Continue do the test", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}