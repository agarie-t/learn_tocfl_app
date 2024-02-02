package my.xxpt;

import static my.xxpt.Util.No;
import static my.xxpt.Util.list;
import static my.xxpt.Util.path;
import static my.xxpt.Util.strings;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class Activity1 extends AppCompatActivity {

    private MediaPlayer mPlayer;

    private static SeekBar sb;
    TextView tv_title;
    ImageView img_question;

    int id;
    String mp3Path;
    String questionPath;
    String answer;

    boolean boo = true;
    private FirebaseStorage storage;
    private Timer timer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_1);
        timer = new Timer();
        storage = FirebaseStorage.getInstance();
        FirebaseDatabase.getInstance().getReference("record")
                .child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child("listeninglevel").exists()) {
                            String level = snapshot.child("listeninglevel").child("level").getValue().toString();
                            ((TextView)findViewById(R.id.level)).setText("Level " + level /*+ "\n(" + num + "/50)"*/);

                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        id = getIntent().getIntExtra("id", 0);
        mp3Path = getIntent().getStringExtra("mp3Path");
        answer = getIntent().getStringExtra("answer");

        questionPath = getIntent().getStringExtra("questionPath");

        Button break_btn = findViewById(R.id.break_btn);
        break_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //依次绑定控件
        tv_title = findViewById(R.id.title);

        tv_title.setText("第 " + (No + 1) + " 题");
        img_question = findViewById(R.id.img_question);


        sb = findViewById(R.id.sb);
        //为滑动条添加事件监听
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {//滑动条开始滑动时调用
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {//滑动条停止滑动时调用

            }
        });





        Log.e("onCreate-=-->", questionPath);
        Log.e("onCreate-=-->2", mp3Path);
        StorageReference gsReference = storage.getReferenceFromUrl(questionPath);
        Task<Uri> downloadUrl = gsReference.getDownloadUrl();
        downloadUrl.addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isComplete()) {
                    post(task.getResult().toString());
                } else {
                    Log.e("---->", "fail------");
                }
            }
        });
        // post(downloadUrl.getResult());

    }


    public void start() {
        No++;
        if (No == list.size()) {//当100道题答题完毕,即退回至首页
            /*Intent intent = new Intent(getApplicationContext(), Activity0.class);
            startActivity(intent);
            finish();*/


            Intent intent = new Intent(Activity1.this, ListeningAEndActivity.class);
            intent.putExtra("true", Util.No_true);
            intent.putExtra("total", list.size());
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

    //音乐

    boolean isRun = false;
    int duration;
    int currentPosition;
    Runnable runnable = new Runnable() {//创建消息处理器对象
        @Override
        public void run() {
            sb.setMax(duration = mPlayer.getDuration());
            while (isRun) {
                SystemClock.sleep(500);
                if (mPlayer == null || sb == null)
                    break;
                sb.setProgress(currentPosition = mPlayer.getCurrentPosition());
                if (duration - currentPosition < 20) {
                    isRun = false;
                    boo = false;
                    RadioButton a = findViewById(R.id.a);
                    RadioButton b = findViewById(R.id.b);
                    RadioButton c = findViewById(R.id.c);
                    String answer1 = null;
                    if (a.isChecked())
                        answer1 = "A";
                    else if (b.isChecked())
                        answer1 = "B";
                    else if (c.isChecked())
                        answer1 = "C";
                    else
                        answer1 = "no select";

                    int score = answer.equals(answer1) ? 1 : 0;

                    if (score == 1) {
                        Util.No_true++;
                        if (Util.No_true == Util.No_) {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(Activity1.this);
                            dialog.setTitle("You have done 25 band A questions correctly. Do you want to continue?").
                                    setNegativeButton("to band B", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            toBandB();
                                            finish();
                                        }
                                    });
                            dialog.setPositiveButton("continue band A", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    post(id, score);
                                }
                            });
                            if (Looper.myLooper() == null)
                                Looper.prepare();
                            dialog.show();
                            Looper.loop();
                        } else
                            post(id, score);
                    } else
                        post(id, score);

                    break;
                }
            }
        }
    };


    private void toBandB() {
        //類型1選30題
        //類型2選20題
        //如果選到的題目聽力檔案有帶- 例如11021-0 就要選擇11021-1 到11021-n的題目為後續
        FirebaseDatabase database = FirebaseDatabase.getInstance();
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
                while (list1Size < 30) {
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
                while (list2Size < 20) {
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
                startActivity(new Intent(Activity1.this,ActivityBandB.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    public void post(String uri, SurfaceHolder surfaceHolder) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream is = null;
                Request request = null;
                try {
                    OkHttpClient client = new OkHttpClient();
                    request = new Request.Builder().url(uri).build();
                    Response response = client.newCall(request).execute();
                    is = response.body().byteStream();
                    // 在sd卡中创建一保存图片（原图和缩略图共用的）文件夹
                    String fileA = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/nbinpic";
                    File fileJA = new File(fileA);
                    if (!fileJA.exists()) {
                        fileJA.mkdirs();
                    }
                    File file = new File(fileA, "a.mp3");
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    byte[] buf = new byte[2048];
                    int len = 0;
                    OutputStream os = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1) {
                        os.write(buf, 0, len);
                    }
                    os.flush();
                    os.close();
                    is.close();
                    mPlayer = new MediaPlayer();
                    //指定视频的地址
                    mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mPlayer.setDataSource(file.getAbsolutePath());
                    //立即播放视频
                    isRun = true;
                    mPlayer.prepare();
                    mPlayer.seekTo(Util.T);
                    mPlayer.start();
                    sb.setMax(duration = mPlayer.getDuration());
                   // runnable.run();
                    if (timer == null)
                        return;
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            // 更新SeekBar的进度
                            if (mPlayer == null || sb == null)
                                return;
                            sb.setMax(duration=mPlayer.getDuration());
                            sb.setProgress(currentPosition = mPlayer.getCurrentPosition());
                            if (duration - currentPosition < 500) {
                                timer.cancel();
                                isRun = false;
                                boo = false;
                                RadioButton a = findViewById(R.id.a);
                                RadioButton b = findViewById(R.id.b);
                                RadioButton c = findViewById(R.id.c);
                                String answer1 = null;
                                if (a.isChecked())
                                    answer1 = "A";
                                else if (b.isChecked())
                                    answer1 = "B";
                                else if (c.isChecked())
                                    answer1 = "C";
                                else
                                    answer1 ="no select";
                                int score = answer.equals(answer1) ? 1 : 0;

                                if (score == 1) {
                                    Util.No_true++;
                                    /*if (Util.No_true == Util.No_) {
                                        AlertDialog.Builder dialog = new AlertDialog.Builder(Activity1.this);
                                        dialog.setTitle("您已正确做对25道band A题,是否继续?").
                                                setNegativeButton("跳转到band B题", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                        toBandB();
                                                        finish();
                                                    }
                                                });
                                        dialog.setPositiveButton("继续做剩余band A题", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                post(id, score);
                                            }
                                        });
                                        if (Looper.myLooper() == null)
                                            Looper.prepare();
                                        dialog.show();
                                        Looper.loop();
                                    } else{
                                        post(id, score);
                                    }*/
                                    post(id, score);
                                } else{
                                    post(id, score);
                                }

                            }
                        }
                    }, 0, 500);
                } catch (IOException e) {
                    e.printStackTrace();
                    is = null;
                }

            }
        }).start();
    }

    public void post(String uri) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream is = null;
                Request request = null;
                try {
                    OkHttpClient client = new OkHttpClient();
                    request = new Request.Builder().url(uri).build();
                    Response response = client.newCall(request).execute();
                    is = response.body().byteStream();
                    // 在sd卡中创建一保存图片（原图和缩略图共用的）文件夹
                    String fileA = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/nbinpic";
                    File fileJA = new File(fileA);
                    if (!fileJA.exists()) {
                        fileJA.mkdirs();
                    }
                    File file = new File(fileA, "b.jpg");
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    byte[] buf = new byte[2048];
                    int len = 0;
                    OutputStream os = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1) {
                        os.write(buf, 0, len);
                    }
                    os.flush();
                    os.close();
                    is.close();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (Activity1.this.isDestroyed())
                                return;
                            RequestOptions options = new RequestOptions().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE);
                            Glide.with(Activity1.this).load(file.getAbsolutePath()).apply(options).into(img_question);
                            Log.i("美国", uri);

                            StorageReference gsReference = storage.getReferenceFromUrl(mp3Path);
                            Task<Uri> downloadUrl = gsReference.getDownloadUrl();
                            downloadUrl.addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isComplete()) {
                                        post(task.getResult().toString(), null);
                                    } else {
                                        Log.e("---->", "fail2------");
                                    }
                                }
                            });

                            // post(mp3Path,null);
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                    is = null;
                }

            }
        }).start();
    }

    public void post(int id, int score) {
        Log.e("-------->","1212"+Activity1.this);
        new Thread(new Runnable() {
            @Override
            public void run() {
               /* InputStream is=null;
                Request request=null;
                try {*/
                   /* OkHttpClient client=new OkHttpClient();
                    request = new Request.Builder().url(path+"xuexi?id="+id+"&score="+score).build();
                    Response response=client.newCall(request).execute();*/

                //todo   存储答题结果到realtime

                if (score == 1) {
                    start();
                    //   这时候把答题正确的数字更新到数据库
                    FirebaseDatabase.getInstance().getReference()
                            .child("record").child(FirebaseAuth.getInstance().getUid())
                            .child("listeninglevel")
                            .child("num").setValue(Util.No_true+"");
                    FirebaseDatabase.getInstance().getReference()
                            .child("record").child(FirebaseAuth.getInstance().getUid())
                            .child("listeninglevel")
                            .child("level").setValue(Util.No_true>39?"4":Util.No_true>27?"3":Util.No_true>21?"2":Util.No_true>7?"1":"0");

                    return;
                }
                FirebaseDatabase.getInstance().getReference().child("record").child(FirebaseAuth.getInstance().getUid()).child("list").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            //todo  如果没有数据
                            ArrayList<Record> records = new ArrayList<>();
                            Record record = new Record(id, score);
                            records.add(record);
                            FirebaseDatabase.getInstance().getReference().child("record").child(FirebaseAuth.getInstance().getUid()).child("list").setValue(records);
                        } else {
                            //todo 添加数据
                            Iterator<DataSnapshot> iterator = snapshot.getChildren().iterator();
                            ArrayList<Record> records = new ArrayList<>();
                            while (iterator.hasNext()) {
                                DataSnapshot next = iterator.next();
                                Record value = next.getValue(Record.class);
                                records.add(value);
                            }
                            Record record = new Record(id, score);
                            records.add(record);
                            FirebaseDatabase.getInstance().getReference().child("record").child(FirebaseAuth.getInstance().getUid()).child("list").setValue(records);


                        }
                        //todo  记录添加完成后  再到下一题
                        start();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



                /*} catch (IOException e) {
                    e.printStackTrace();
                }*/

            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayer != null && mPlayer.isPlaying()) {
            try {
                mPlayer.stop();
                mPlayer = null;
            } catch (Exception e) {
                mPlayer = null;
            }
        }
        isRun = false;
        timer.cancel();
        timer = null;
        finish();

    }
}


