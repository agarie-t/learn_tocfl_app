package my.xxpt;

import static my.xxpt.Util.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gyf.immersionbar.ImmersionBar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Activity0 extends AppCompatActivity {
    public static final String[] permissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    private TextView levelText;
    private FirebaseDatabase database;

    boolean isFx = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_0);
        levelText = findViewById(R.id.level);
        ImmersionBar.with(Activity0.this).statusBarColor(R.color.main).init();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= 23)
                requestPermissions(permissions, 0x123);
        }
        No = -1;
        initData();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (isFx){
            isFx = !isFx;
            No = -1;
        }
        database
                .getReference("record")
                .child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child("listeninglevel").exists()) {
                            String level = snapshot.child("listeninglevel").child("level").getValue().toString();
                            String num = snapshot.child("listeninglevel").child("num").getValue().toString();

                            levelText.setText("Level " + level + "\n(#" + (No>=0?(No+1):0) + "/50)");

                        }else {
                            levelText.setText("Level " + 0 + "\n(#" + (No>=0?(No+1):0) + "/50)");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void initData() {
        database = FirebaseDatabase.getInstance();
        //todo  获取realtime实例

        DatabaseReference myRef = database.getReference("bandA").child("RECORDS");
        //todo  获取到bandA 整体数据
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //todo  解析数据

                Iterator<DataSnapshot> iterator = snapshot.getChildren().iterator();


                ArrayList<Data> list0 = new ArrayList<>();
                while (iterator.hasNext()) {
                    //todo  将数据添加到list中

                    /*DataSnapshot next = iterator.next();
                    next.g*/
                    Data value = iterator.next().getValue(Data.class);
                    list0.add(value);
                }
                list = new ArrayList<>();

                ArrayList<Data> list1 = new ArrayList<>();
                ArrayList<Data> list2 = new ArrayList<>();
                ArrayList<Data> list3 = new ArrayList<>();
                ArrayList<Data> list4 = new ArrayList<>();

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
                        }
                });


                HashSet<Integer> set = new HashSet<Integer>();
                Random random = new Random();

                while (set.size() < 25) {
                    int index = random.nextInt(list1.size());
                    if (!set.contains(index)) {
                        set.add(index);
                        list.add(list1.get(index));
                    }
                }

                set.clear();
                while (set.size() < 15) {
                    int index = random.nextInt(list2.size());
                    if (!set.contains(index)) {
                        set.add(index);
                        list.add(list2.get(index));
                    }
                }

                set.clear();
                while (set.size() < 5) {
                    int index = random.nextInt(list3.size());
                    if (!set.contains(index)) {
                        set.add(index);
                        list.add(list3.get(index));
                    }
                }

                set.clear();
                while (set.size() < 5) {
                    int index = random.nextInt(list4.size());
                    if (!set.contains(index)) {
                        set.add(index);
                        list.add(list4.get(index));
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((Button) findViewById(R.id.start)).setEnabled(true);
                        ((Button) findViewById(R.id.fuxi)).setEnabled(true);
                        ((Button) findViewById(R.id.newstart)).setEnabled(true);
                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public void fuxi(View view) {


        postError("xuexi?error=0");
    }


    public void newStart(View view){

        if(No!=-1){
            AlertDialog.Builder dialog = new AlertDialog.Builder(Activity0.this);
            dialog.setTitle("Tips").
                    setMessage("Whether you are sure to start again ?").
                    setNegativeButton("Sure", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            No = -1;
                            dostart();
                        }
                    })
                    .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            dialog.show();
        }else {
            No = -1;
            dostart();
        }


    }


    public void dostart() {

        database
                .getReference("record")
                .child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child("listeninglevel").exists()) {
                            String level = snapshot.child("listeninglevel").child("level").getValue().toString();

                            if ( level.equals("3") || level.equals("4")) {

                                toBandB();

                            } else {
                                toBandA();
                            }

                        } else {
                            //无数据  banda
                            toBandA();
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }


    public void start(View view) {

        if (No == -1)
            return;

        if ( Util.list1.isEmpty())
            toBandA();
        else
            toBandB();

       /* database
                .getReference("record")
                .child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child("listeninglevel").exists()) {
                            String level = snapshot.child("listeninglevel").child("level").getValue().toString();

                            if (level.equals("2") || level.equals("3") || level.equals("4")) {

                                toBandB();

                            } else {
                                toBandA();
                            }

                        } else {
                            //无数据  banda
                            toBandA();
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });*/

    }

    private void toBandB() {

        if (No != -1){
            startActivity(new Intent(Activity0.this,ActivityBandB.class));
            return;
        }

        //類型1選30題
        //類型2選20題
        //如果選到的題目聽力檔案有帶- 例如11021-0 就要選擇11021-1 到11021-n的題目為後續
        DatabaseReference myRef = database.getReference("bandB").child("RECORDS");
        //todo  获取到bandA 整体数据
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //todo  解析数据

                Iterator<DataSnapshot> iterator = snapshot.getChildren().iterator();


                ArrayList<DataB> list0 = new ArrayList<>();
                while (iterator.hasNext()) {
                    //todo  将数据添加到list中

                    /*DataSnapshot next = iterator.next();
                    next.g*/
                    DataB value = iterator.next().getValue(DataB.class);
                    list0.add(value);
                }
                Random random = new Random();
                ArrayList<DataB> list1 = new ArrayList<>();
                ArrayList<DataB> list2 = new ArrayList<>();

                //类型1   取数据>=30条
                int list1Size = 0;
                while (list1Size < 35) {
                    Log.e("mp3",list1.size()+"");
                    int index = random.nextInt(list0.size());
                    if (list0.get(index).type.equals("1")&&!list1.contains(list0.get(index))) {
                        if (list0.get(index).mp3.contains("-")) {
                            //这个时候需要把包含这个名称的文件都放到集合中
                            for (int i = 0; i < list0.size(); i++) {
                                Log.e("mp3",list0.get(i).mp3+"");
                                if (list0.get(i).mp3.contains(list0.get(index).mp3.substring(0,list0.get(index).mp3.indexOf("-")))) {
                                    list1.add(list0.get(i));
                                }
                            }
                        } else if (list0.get(index).type.equals("1")){
                            //仅放入当前数据
                            list1.add(list0.get(index));
                        }
                    }
                    list1Size = list1.size();
                }


                //类型2   取数据>=20条
                int list2Size = 0;
                while (list2Size < 15) {
                    int index = random.nextInt(list0.size());
                    if (list0.get(index).type.equals("2")&&!list2.contains(list0.get(index))) {
                        if (list0.get(index).mp3.contains("-")) {
                            //这个时候需要把包含这个名称的文件都放到集合中
                            for (int i = 0; i < list0.size(); i++) {
                                if (list0.get(i).mp3.contains(list0.get(index).mp3.substring(0,list0.get(index).mp3.indexOf("-")))) {
                                    list2.add(list0.get(i));
                                }
                            }
                        } else if (list0.get(index).type.equals("2")){
                            //仅放入当前数据
                            list2.add(list0.get(index));
                        }
                    }

                    list2Size = list2.size();
                }



                //todo  这时候开始考试  考试如果包含两个语音需要先播放0  再切换下面包含的题目
                Util.list1.clear();
                Util.list2.clear();
                Util.list1.addAll(list1);
                Util.list2.addAll(list2);
                No++;
                startActivity(new Intent(Activity0.this,ActivityBandB.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void toBandA() {
        No++;
        if (No == list.size()) {//当100道题答题完毕,即退回至首页
            Intent intent = new Intent(getApplicationContext(), Activity0.class);
            startActivity(intent);
            finish();
            return;
        }

        int index = No;

        Data data = list.get(index);

        int id = data.id;
        String type = data.type;
        String version = data.version;
        String mp3 = data.mp3;
        String mp3Path = path + "BandA_" + version + "LSMP3/" + mp3;
        String answer = data.answer;

        Intent intent = null;
        switch (type) {
            case "1":
                intent = new Intent(getApplicationContext(), Activity1.class);

                String question = data.question;
                String questionPath = path + "BandA_" + version + "LSPIC/" + question;
                intent.putExtra("questionPath", questionPath);
                intent.putExtra("data", data);

                break;
            case "2":
            case "3":
                intent = new Intent(getApplicationContext(), Activity2.class);

                String a = data.a;
                String b = data.b;
                String c = data.c;
                String aPath = path + "BandA_" + version + "LSPIC/" + a;
                String bPath = path + "BandA_" + version + "LSPIC/" + b;
                String cPath = path + "BandA_" + version + "LSPIC/" + c;

                intent.putExtra("aPath", aPath);
                intent.putExtra("bPath", bPath);
                intent.putExtra("cPath", cPath);
                intent.putExtra("data", data);
                break;
            case "4":
                intent = new Intent(getApplicationContext(), Activity3.class);

                String a1 = data.a;
                String b1 = data.b;
                String c1 = data.c;
                String d1 = data.d;
                String aStr = a1;
                String bStr = b1;
                String cStr = c1;
                String dStr = d1;

                intent.putExtra("aStr", aStr);
                intent.putExtra("bStr", bStr);
                intent.putExtra("cStr", cStr);
                intent.putExtra("dStr", dStr);
                intent.putExtra("data", data);
                break;
        }
        Util.list1.clear();
        Util.list2.clear();
        intent.putExtra("id", id);
        intent.putExtra("mp3Path", mp3Path);
        intent.putExtra("answer", answer);
        startActivity(intent);
    }


    public void startError() {
        No++;
        if (No == list.size()) {//当100道题答题完毕,即退回至首页
            Intent intent = new Intent(getApplicationContext(), Activity0.class);
            startActivity(intent);
            finish();
            return;
        }

        int index = No;

        Data data = list.get(index);

        int id = data.id;
        String type = data.type;
        String version = data.version;
        String mp3 = data.mp3;
        String mp3Path = path + "BandA_" + version + "LSMP3/" + mp3;
        String answer = data.answer;

        Intent intent = null;
        switch (type) {
            case "1":
                intent = new Intent(getApplicationContext(), Activity11.class);

                String question = data.question;
                String questionPath = path + "BandA_" + version + "LSPIC/" + question;
                intent.putExtra("questionPath", questionPath);
                intent.putExtra("data", data);
                break;
            case "2":
            case "3":
                intent = new Intent(getApplicationContext(), Activity22.class);

                String a = data.a;
                String b = data.b;
                String c = data.c;
                String aPath = path + "BandA_" + version + "LSPIC/" + a;
                String bPath = path + "BandA_" + version + "LSPIC/" + b;
                String cPath = path + "BandA_" + version + "LSPIC/" + c;

                intent.putExtra("aPath", aPath);
                intent.putExtra("bPath", bPath);
                intent.putExtra("cPath", cPath);
                intent.putExtra("data", data);
                break;
            case "4":
                intent = new Intent(getApplicationContext(), Activity33.class);

                String a1 = data.a;
                String b1 = data.b;
                String c1 = data.c;
                String d1 = data.d;
                String aStr = a1;
                String bStr = b1;
                String cStr = c1;
                String dStr = d1;

                intent.putExtra("aStr", aStr);
                intent.putExtra("bStr", bStr);
                intent.putExtra("cStr", cStr);
                intent.putExtra("dStr", dStr);
                intent.putExtra("data", data);
                break;
        }

        intent.putExtra("id", id);
        intent.putExtra("mp3Path", mp3Path);
        intent.putExtra("answer", answer);
        startActivity(intent);
    }

    public void postError(String uri) {


        new Thread(new Runnable() {
            @Override
            public void run() {
               /* String str=null;
                Request request=null;
                try {
                    OkHttpClient client=new OkHttpClient();
                    request = new Request.Builder().url(Util.path+uri).build();
                    Response response=client.newCall(request).execute();
                    str=response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                    str=null;
                }*/


                FirebaseDatabase.getInstance().getReference().child("record").child(FirebaseAuth.getInstance().getUid()).child("list").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            //todo  如果没有数据
                            Toast.makeText(Activity0.this, "Congratulations!\n" +
                                    "All your answers are correct! No \n" +
                                    "mistakes!", Toast.LENGTH_SHORT).show();
                        } else {


                            isFx = true;
                            No = -1;

                            //todo 添加数据
                            Iterator<DataSnapshot> iterator = snapshot.getChildren().iterator();
                            ArrayList<Record> records = new ArrayList<>();
                            while (iterator.hasNext()) {
                                DataSnapshot next = iterator.next();
                                Record value = next.getValue(Record.class);
                                records.add(value);
                            }


                            //todo  取到了数据   去总表中把数据取出来


                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            //todo  获取realtime实例
                            DatabaseReference myRef = database.getReference("bandA").child("RECORDS");
                            //todo  获取到bandA 整体数据
                            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    //todo  解析数据

                                    Iterator<DataSnapshot> iterator = snapshot.getChildren().iterator();
                                    ArrayList<Data> list0 = new ArrayList<>();
                                    while (iterator.hasNext()) {
                                        //todo  将数据添加到list中
                                        Data value = iterator.next().getValue(Data.class);
                                        list0.add(value);
                                    }
                                    list = new ArrayList<>();

                                    //todo 获取后   把错题加载出来

                                    for (int i = 0; i < records.size(); i++) {
                                        for (int j = 0; j < list0.size(); j++) {
                                            if (records.get(i).getId() == list0.get(j).getId()) {
                                                list.add(list0.get(j));
                                                list.add(list0.get(j));
                                            }
                                        }
                                    }
                                    //todo 加载完成数据  开始复习错题
                                    startError();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                        }








                        /*if(list0.isEmpty()){
                            if(Looper.myLooper()==null)
                                Looper.prepare();
                            Toast.makeText(Activity0.this,"恭喜您,全部正确!\n一个做错的都没有!",Toast.LENGTH_SHORT).show();
                            Looper.loop();
                            return;
                        }

                        list=list0;*/

                        // startError();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        }).start();
    }


    public void post(String uri) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String str = null;
                Request request = null;
                try {
                    OkHttpClient client = new OkHttpClient();
                    request = new Request.Builder().url(Util.path + uri).build();
                    Response response = client.newCall(request).execute();
                    str = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                    str = null;
                }

                ArrayList<Data> list0 = getDataList(str);

                list = new ArrayList<>();

                ArrayList<Data> list1 = new ArrayList<>();
                ArrayList<Data> list2 = new ArrayList<>();
                ArrayList<Data> list3 = new ArrayList<>();
                ArrayList<Data> list4 = new ArrayList<>();

                list0.forEach(x -> {
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
                    }
                });


                HashSet<Integer> set = new HashSet<Integer>();
                Random random = new Random();

                while (set.size() < 25) {
                    int index = random.nextInt(list1.size());
                    if (!set.contains(index)) {
                        set.add(index);
                        list.add(list1.get(index));
                    }
                }

                set.clear();
                while (set.size() < 15) {
                    int index = random.nextInt(list2.size());
                    if (!set.contains(index)) {
                        set.add(index);
                        list.add(list2.get(index));
                    }
                }

                set.clear();
                while (set.size() < 5) {
                    int index = random.nextInt(list3.size());
                    if (!set.contains(index)) {
                        set.add(index);
                        list.add(list3.get(index));
                    }
                }

                set.clear();
                while (set.size() < 5) {
                    int index = random.nextInt(list4.size());
                    if (!set.contains(index)) {
                        set.add(index);
                        list.add(list4.get(index));
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((Button) findViewById(R.id.start)).setEnabled(true);
                        ((Button) findViewById(R.id.fuxi)).setEnabled(true);
                    }
                });

            }
        }).start();
    }

    public void reading(View view) {
        //跳转到Reading

        startActivity(new Intent(Activity0.this, ReadingHomeActivity.class));
    }

    public void reading_err(View view) {
        FirebaseDatabase.getInstance().getReference().child("record").child(FirebaseAuth.getInstance().getUid()).child("list2").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    //没有错题
                    Toast.makeText(Activity0.this, "没有阅读错题！", Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(new Intent(Activity0.this, ReadingErrActivity.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void logout(View view) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(Activity0.this);
        dialog.setTitle("提示").
                setMessage("是否确认登出？").
                setNegativeButton("确认", (dialog1, which) -> {
                    dialog1.dismiss();
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(Activity0.this, LoginActivity.class));
                    SPUtils.clear(Activity0.this);
                    finish();
                });
        dialog.setPositiveButton("取消", (dialog12, which) -> dialog12.dismiss());

        dialog.show();


    }
}