package my.xxpt;

import static my.xxpt.Util.No;
import static my.xxpt.Util.path;

import android.annotation.SuppressLint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.gyf.immersionbar.ImmersionBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class ActivityBandB extends AppCompatActivity {

    private MediaPlayer mPlayer;

    private SeekBar sb;
    TextView tv_title;
    TextView question;

    private Timer timer;
    private FirebaseStorage storage;

    private RadioButton a;
    private RadioButton b;
    private RadioButton c;
    private RadioButton d;
    private RadioGroup loved;
    private ArrayList<DataB> listData;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bandb);
        ImmersionBar.with(ActivityBandB.this).statusBarColor(R.color.main).init();
        storage = FirebaseStorage.getInstance();
        timer = new Timer();
        mPlayer = new MediaPlayer();
        //依次绑定控件
        tv_title = findViewById(R.id.title);
        question = findViewById(R.id.question);
        tv_title.setText("第 " + (No + 1) + " 题");
        loved = findViewById(R.id.loved);
        a = findViewById(R.id.a);
        b = findViewById(R.id.b);
        c = findViewById(R.id.c);
        d = findViewById(R.id.d);

        sb = findViewById(R.id.sb);
        FirebaseDatabase.getInstance().getReference("record")
                .child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child("listeninglevel").exists()) {
                            String level = snapshot.child("listeninglevel").child("level").getValue().toString();
                            ((TextView) findViewById(R.id.level)).setText("Level " + level /*+ "\n(" + num + "/50)"*/);

                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        //将题目数据完全加载出来
        listData = new ArrayList<>();
        listData.addAll(Util.list1);
        listData.addAll(Util.list2);
        //加载第一题数据

        loadNum(No);


        // post(downloadUrl.getResult());

    }

    private void loadNum(int num) {
        timer = new Timer();

        //先判断是不是  组合题的第一题  是的话先加载-0的音频文件
        if (listData.get(num).mp3.contains("-1")) {
            //是组合第一题  这时候开始先播放 0 音频

            String s = path + "BandB_" + listData.get(num).getVersion() + "LSMP3/"

                    //处理数据的路径区别  1跟5不做处理  2-4需要添加中间的路径
                    +
                    ((listData.get(num).getVersion().equals("1") || listData.get(num).getVersion().equals("5")) ? ""
                            : listData.get(num).getVersion().equals("2") ? "mock2_BandB_mp3/mock2_BandB_mp3/"
                            : listData.get(num).getVersion().equals("3") ? "mock3_BandB_mp3/mock3_BandB_mp3/"
                            : "mock4_BandB_mp3/mock4_BandB_mp3_en/")

                    + listData.get(num).getMp30().substring(0, listData.get(num).getMp30().indexOf("-")) + "0.mp3";
            Log.e("s--->", s);
            //开始播放0的语音
            StorageReference gsReference = storage.getReferenceFromUrl(s);
            Task<Uri> downloadUrl = gsReference.getDownloadUrl();
            downloadUrl.addOnCompleteListener(task -> {
                if (task.isComplete()) {
                    post(task.getResult().toString(), 0);
                } else {
                    Log.e("---->", "fail------");
                }
            });

            //展示题目


            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    question.setText("");
                    loved.clearCheck();
                    loved.setVisibility(View.GONE);
                }
            });

        } else {
            //不是

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    question.setText((listData.get(num).getQuestion() == null || TextUtils.isEmpty(listData.get(num).getQuestion())) ?
                            "" : listData.get(num).getQuestion());
                    loved.clearCheck();
                    loved.setVisibility(View.VISIBLE);
                    a.setText(listData.get(num).getA());
                    b.setText(listData.get(num).getB());
                    c.setText(listData.get(num).getC());
                    d.setText(listData.get(num).getD());
                }
            });

            String s;
            if (listData.get(num).getMp3().contains("-")) {
                s = path + "BandB_" + listData.get(num).getVersion() + "LSMP3/"
                        //处理数据的路径区别  1跟5不做处理  2-4需要添加中间的路径
                        +
                        ((listData.get(num).getVersion().equals("1") || listData.get(num).getVersion().equals("5")) ? ""
                                : listData.get(num).getVersion().equals("2") ? "mock2_BandB_mp3/mock2_BandB_mp3/"
                                : listData.get(num).getVersion().equals("3") ? "mock3_BandB_mp3/mock3_BandB_mp3/"
                                : "mock4_BandB_mp3/mock4_BandB_mp3_en/")
                        + (listData.get(num).getMp3().replace("-", ""));

            } else {
                s = path + "BandB_" + listData.get(num).getVersion() + "LSMP3/"
                        //处理数据的路径区别  1跟5不做处理  2-4需要添加中间的路径
                        +
                        ((listData.get(num).getVersion().equals("1") || listData.get(num).getVersion().equals("5")) ? ""
                                : listData.get(num).getVersion().equals("2") ? "mock2_BandB_mp3/mock2_BandB_mp3/"
                                : listData.get(num).getVersion().equals("3") ? "mock3_BandB_mp3/mock3_BandB_mp3/"
                                : "mock4_BandB_mp3/mock4_BandB_mp3_en/")
                        + (listData.get(num).getMp3().replace("-", ""));

            }
            Log.e("s--->", s);

            StorageReference gsReference = storage.getReferenceFromUrl(s);
            Task<Uri> downloadUrl = gsReference.getDownloadUrl();
            downloadUrl.addOnCompleteListener(task -> {
                if (task.isComplete()) {
                    post(task.getResult().toString(), 1);
                } else {
                    Log.e("---->", "fail------");
                }
            });
        }


    }


    //音乐

    boolean isRun = false;
    int duration;
    int currentPosition;
    Runnable runnable = new Runnable() {//创建消息处理器对象
        @Override
        public void run() {

        }
    };

    private int tagInt = -1;

    //下载播放语音
    public void post(String uri, int tag) {
        Log.e("uri", uri);
        tagInt = tag;
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
                    String fileA = Environment.getExternalStorageDirectory().getAbsolutePath() + "/nbinpic";
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

                    //指定视频的地址
                    mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mPlayer.setDataSource(file.getAbsolutePath());
                    //立即播放视频
                    isRun = true;

                    mPlayer.prepare();
                    mPlayer.seekTo(Util.T);
                    mPlayer.start();
                    // runnable.run();

                    sb.setMax(duration = mPlayer.getDuration());
                    // runnable.run();
                    if (timer == null){
                        return;
                    }


                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            {
                               // sb.setMax(duration = mPlayer.getDuration());
                                sb.setProgress(currentPosition = mPlayer.getCurrentPosition());

                                if (duration - currentPosition < 500) {

                                    mPlayer.reset();
                                    sb.setProgress(0);
                                    //这个时候语音播放完了    判断下是不是tag0的第一个语音
                                    if (tagInt == 0) {
                                        //继续播放  tag0中的第二条语音

                                        String s = path + "BandB_" + listData.get(No).getVersion() + "LSMP3/"
                                                //处理数据的路径区别  1跟5不做处理  2-4需要添加中间的路径
                                                +
                                                ((listData.get(No).getVersion().equals("1") || listData.get(No).getVersion().equals("5")) ? ""
                                                        : listData.get(No).getVersion().equals("2") ? "mock2_BandB_mp3/mock2_BandB_mp3/"
                                                        : listData.get(No).getVersion().equals("3") ? "mock3_BandB_mp3/mock3_BandB_mp3/"
                                                        : "mock4_BandB_mp3/mock4_BandB_mp3_en/")
                                                + listData.get(No).getMp3().replace("-", "");
                                        Log.e("S-->", s);


                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                question.setText((listData.get(No).getQuestion() == null || TextUtils.isEmpty(listData.get(No).getQuestion())) ?
                                                        "" : listData.get(No).getQuestion());
                                                loved.clearCheck();
                                                loved.setVisibility(View.VISIBLE);
                                                a.setText(listData.get(No).getA());
                                                b.setText(listData.get(No).getB());
                                                c.setText(listData.get(No).getC());
                                                d.setText(listData.get(No).getD());
                                            }
                                        });
                                        StorageReference gsReference = storage.getReferenceFromUrl(s);
                                        Task<Uri> downloadUrl = gsReference.getDownloadUrl();
                                        downloadUrl.addOnCompleteListener(new OnCompleteListener<Uri>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Uri> task) {
                                                if (task.isComplete()) {
                                                    post(task.getResult().toString(), 1);
                                                } else {
                                                    Log.e("---->", "fail------");
                                                }
                                            }
                                        });
                                    } else {
                                        timer.cancel();
                                        timer.purge();
                                        timer = null;
                                        //加载答案   播放下一题
                                        String answer1 = null;
                                        if (a.isChecked())
                                            answer1 = "A";
                                        else if (b.isChecked())
                                            answer1 = "B";
                                        else if (c.isChecked())
                                            answer1 = "C";
                                        else if (d.isChecked())
                                            answer1 = "D";
                                        int score = listData.get(No).getAnswer().equals(answer1) ? 1 : 0;

                                        //todo  这里需要去更新用户的等级

                                        if (score == 1) {
                                            Util.No_true2++;

                                            FirebaseDatabase.getInstance().getReference()
                                                    .child("record").child(FirebaseAuth.getInstance().getUid())
                                                    .child("listeninglevel")
                                                    .child("num").setValue(Util.No_true2 + "");
                                            FirebaseDatabase.getInstance().getReference()
                                                    .child("record").child(FirebaseAuth.getInstance().getUid())
                                                    .child("listeninglevel")
                                                    .child("num2").setValue(listData.size() + "");
                                            FirebaseDatabase.getInstance().getReference()
                                                    .child("record").child(FirebaseAuth.getInstance().getUid())
                                                    .child("listeninglevel")
                                                    .child("level").setValue(Util.No_true2 > 39 ? "4" : Util.No_true2 > 27 ? "3"
                                                            : Util.No_true2 > 21 ? "2" : "1");
                                        }

                                        No++;
                                        runOnUiThread(new Runnable() {
                                            @SuppressLint("SetTextI18n")
                                            @Override
                                            public void run() {
                                                tv_title.setText("第 " + (No + 1) + " 题");
                                            }
                                        });

                                        loadNum(No);

                                        //加载下一题
                                    }
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

    public void finish(View view) {
        finish();
    }
}


