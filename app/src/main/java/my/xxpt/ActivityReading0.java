package my.xxpt;

import static my.xxpt.Util.No;
import static my.xxpt.Util.getDataList;
import static my.xxpt.Util.list;
import static my.xxpt.Util.path;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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
import java.util.Random;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ActivityReading0 extends AppCompatActivity {
    public static final String[] permissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    private TextView levelText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading0);
        levelText = findViewById(R.id.level);
        ImmersionBar.with(ActivityReading0.this).statusBarColor(R.color.main).init();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= 23)
                requestPermissions(permissions, 0x123);
        }


    }


    @Override
    protected void onResume() {
        super.onResume();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database
                .getReference("record")
                .child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child("readinglevel").exists()) {
                            String level = snapshot.child("readinglevel").child("level").getValue().toString();
                            String num = snapshot.child("readinglevel").child("num").getValue().toString();



                            levelText.setText("Level " + level /*+ "\n(" + num + "/"+((level.equals("3")||level.equals("4"))?"15":"45")+")"*/);

                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    public void start(View view) {
        No++;
        if (No == list.size()) {//当100道题答题完毕,即退回至首页
            Intent intent = new Intent(getApplicationContext(), ActivityReading0.class);
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

                break;
        }

        intent.putExtra("id", id);
        intent.putExtra("mp3Path", mp3Path);
        intent.putExtra("answer", answer);
        startActivity(intent);
        finish();
    }


    public void startError() {
        No++;
        if (No == list.size()) {//当100道题答题完毕,即退回至首页
            Intent intent = new Intent(getApplicationContext(), ActivityReading0.class);
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

                break;
        }

        intent.putExtra("id", id);
        intent.putExtra("mp3Path", mp3Path);
        intent.putExtra("answer", answer);
        startActivity(intent);
        finish();
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
                            Toast.makeText(ActivityReading0.this, "Congratulations!All your answers are correct!\nNo mistakes!", Toast.LENGTH_SHORT).show();
                        } else {
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
                while (set.size() < 25) {
                    int index = random.nextInt(list2.size());
                    if (!set.contains(index)) {
                        set.add(index);
                        list.add(list2.get(index));
                    }
                }

                set.clear();
                while (set.size() < 25) {
                    int index = random.nextInt(list3.size());
                    if (!set.contains(index)) {
                        set.add(index);
                        list.add(list3.get(index));
                    }
                }

                set.clear();
                while (set.size() < 25) {
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
                        ((Button) findViewById(R.id.newstart1)).setEnabled(true);
                    }
                });

            }
        }).start();
    }



    public void newC(View view){


        if (TextUtils.isEmpty(SPUtils.get(ActivityReading0.this,"ints","").toString())){
            doreading();
        }else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(ActivityReading0.this);
            dialog.setTitle("WARNING!").
                    setMessage("Are you sure to take a new test?").
                    setNegativeButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            SPUtils.remove(ActivityReading0.this,"ints");
                            doreading();
                        }
                    })
                    .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            dialog.show();
        }

    }

    public void doreading(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database
                .getReference("record")
                .child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child("readinglevel").exists()) {
                            String level = snapshot.child("readinglevel").child("level").getValue().toString();

                            if (level.equals("2") || level.equals("3") || level.equals("4")) {
                                startActivity(new Intent(ActivityReading0.this, ReadingBHomeActivity.class));
                            } else {
                                startActivity(new Intent(ActivityReading0.this, ReadingHomeActivity.class));
                            }
                        } else {
                            startActivity(new Intent(ActivityReading0.this, ReadingHomeActivity.class));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }



    public void reading(View view) {
        //todo  跳转到Reading  根据等级判断到A还是B

        if (!TextUtils.isEmpty(SPUtils.get(ActivityReading0.this,"ints","").toString())){
            doreading();
        }
    }

    public void reading_err(View view) {
        FirebaseDatabase.getInstance().getReference().child("record").child(FirebaseAuth.getInstance().getUid()).child("list2").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    //没有错题
                    Toast.makeText(ActivityReading0.this, "Congratulations! \n" +
                            "All your answers are correct! No mistakes!", Toast.LENGTH_SHORT).show();
                } else {
                    No = -1;
                    startActivity(new Intent(ActivityReading0.this, ReadingErrActivity.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void logout(View view) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(ActivityReading0.this);
        dialog.setTitle("WARNING!").
                setMessage("Are you sure to logout?").
                setNegativeButton("Confirm", (dialog1, which) -> {
                    dialog1.dismiss();
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(ActivityReading0.this, LoginActivity.class));
                    SPUtils.clear(ActivityReading0.this);
                    finish();
                });
        dialog.setPositiveButton("Cancel", (dialog12, which) -> dialog12.dismiss());

        dialog.show();


    }
}