package my.xxpt;

import static my.xxpt.Util.No;
import static my.xxpt.Util.list;
import static my.xxpt.Util.path;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class Activity22 extends AppCompatActivity {

    private MediaPlayer mPlayer;
    private Timer timer;
    private static SeekBar sb;
    TextView tv_title;

    int id;
    String mp3Path;
    String answer;

    String aPath;
    String bPath;
    String cPath;

    ImageView img_a;
    ImageView img_b;
    ImageView img_c;
    private FirebaseStorage storage;
    boolean isEnglish = false;
    private TextView content, content2;
    private ImageView imageTr;
    private Data data;
    private LinearLayout ll1;
    private LinearLayout ll2;
    private LinearLayout ll3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_22);
        storage = FirebaseStorage.getInstance();
        id = getIntent().getIntExtra("id", 0);
        data = (Data) getIntent().getSerializableExtra("data");
        timer = new Timer();
        content = findViewById(R.id.content);
        content2 = findViewById(R.id.content2);
        imageTr = findViewById(R.id.image_tr);
        ll1 = findViewById(R.id.ll1);
        ll2 = findViewById(R.id.ll2);
        ll3 = findViewById(R.id.ll3);
        Button break_btn = findViewById(R.id.break_btn);
        break_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
        mp3Path = getIntent().getStringExtra("mp3Path");
        answer = getIntent().getStringExtra("answer");

        aPath = getIntent().getStringExtra("aPath");
        bPath = getIntent().getStringExtra("bPath");
        cPath = getIntent().getStringExtra("cPath");

        img_a = findViewById(R.id.img_a);
        img_b = findViewById(R.id.img_b);
        img_c = findViewById(R.id.img_c);

        RadioButton a = findViewById(R.id.a);
        RadioButton b = findViewById(R.id.b);
        RadioButton c = findViewById(R.id.c);
        a.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b1) {
                if (b1){
                    b.setChecked(false);
                    c.setChecked(false);
                }

            }
        });
        b.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b1) {
                if (b1){
                    a.setChecked(false);
                    c.setChecked(false);
                }
            }
        });
        c.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b1) {
                if (b1){
                    b.setChecked(false);
                    a.setChecked(false);
                }
            }
        });
        switch (answer) {
            case "A":
                //a.setChecked(true);
                if (No % 2 == 1){
                    a.setChecked(true);
                    ll1.setBackgroundColor(getResources().getColor(R.color.main));
                }

                break;
            case "B":
                //b.setChecked(true);
                if (No % 2 == 1){
                    b.setChecked(true);
                    ll2.setBackgroundColor(getResources().getColor(R.color.main));
                }

                break;
            case "C":
                //c.setChecked(true);
                if (No % 2 == 1){
                    c.setChecked(true);
                    ll3.setBackgroundColor(getResources().getColor(R.color.main));
                }

                break;
        }

        if (No % 2 == 1) {
            content.setText(TextUtils.isEmpty(data.getQ_Script()) ? "" : data.getQ_Script());
            content2.setText(TextUtils.isEmpty(data.getQ_Question()) ? "" : data.getQ_Question());
            imageTr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isEnglish = !isEnglish;
                    content.setText(TextUtils.isEmpty(data.getQ_Script()) ? "" :
                            isEnglish ? data.getQ_ScriptEng() : data.getQ_Script());
                    content2.setText(TextUtils.isEmpty(data.getQ_Question()) ? "" :
                            isEnglish ? data.getQ_QuestionEng() : data.getQ_Question());
                }
            });


            FirebaseDatabase.getInstance().getReference().child("record").child(FirebaseAuth.getInstance().getUid()).child("list").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        //todo  如果没有数据
                    } else {
                        //todo 添加数据
                        Iterator<DataSnapshot> iterator = snapshot.getChildren().iterator();
                        ArrayList<Record> records = new ArrayList<>();
                        while (iterator.hasNext()) {
                            DataSnapshot next = iterator.next();
                            Record value = next.getValue(Record.class);

                            if (value.getId() != data.id){
                                records.add(value);
                            }

                        }
                        //这时候 播放了两遍   删除掉错题数据  变更数据
                        FirebaseDatabase.getInstance().getReference().child("record").child(FirebaseAuth.getInstance().getUid()).child("list").setValue(records);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } else {
            imageTr.setVisibility(View.GONE);
        }


        //依次绑定控件
        tv_title = findViewById(R.id.title);
        tv_title.setText("第 " + (No + 1) + " 题");


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
                int progress = seekBar.getProgress();
                if (progress <= Util.T)
                    progress = Util.T;
                mPlayer.seekTo(progress);
            }
        });
        StorageReference gsReference = storage.getReferenceFromUrl(aPath);
        Task<Uri> downloadUrl = gsReference.getDownloadUrl();
        downloadUrl.addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isComplete()) {
                    post(task.getResult().toString(), 'a');
                } else {
                    Log.e("---->", "fail------");
                }
            }
        });
        // post(aPath,'a');

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
        finish();
    }

    //音乐

    boolean isRun = false;
    int duration;
    int currentPosition;
    Runnable runnable = new Runnable() {//创建消息处理器对象
        @Override
        public void run() {
            while (isRun) {
                SystemClock.sleep(10);
                if (mPlayer == null)
                    break;
                sb.setMax(duration = mPlayer.getDuration());
                sb.setProgress(currentPosition = mPlayer.getCurrentPosition());
                if (duration - currentPosition < 20) {
                    isRun = false;
                    startError();
                    break;
                }
            }

        }
    };


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
                    //runnable.run();
                    if (timer == null)
                        return;
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            // 更新SeekBar的进度
                            if (mPlayer == null || sb == null)
                                return;
                            sb.setProgress(currentPosition = mPlayer.getCurrentPosition());
                            Log.e("-----","执行了");

                            if (duration - currentPosition < 500) {
                                timer.cancel();
                                isRun = false;
                                startError();
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

    public void post(String uri, int x) {
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
                            RequestOptions options = new RequestOptions().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE);
                            if (x == 'a') {
                                Glide.with(Activity22.this).load(file.getAbsolutePath()).apply(options).into(img_a);

                                StorageReference gsReference = storage.getReferenceFromUrl(bPath);
                                Task<Uri> downloadUrl = gsReference.getDownloadUrl();
                                downloadUrl.addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isComplete()) {
                                            post(task.getResult().toString(), 'b');
                                        } else {
                                            Log.e("---->", "fail------");
                                        }
                                    }
                                });


                                // post(bPath,'b');
                            }
                            if (x == 'b') {
                                Glide.with(Activity22.this).load(file.getAbsolutePath()).apply(options).into(img_b);

                                StorageReference gsReference = storage.getReferenceFromUrl(cPath);
                                Task<Uri> downloadUrl = gsReference.getDownloadUrl();
                                downloadUrl.addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isComplete()) {
                                            post(task.getResult().toString(), 'c');
                                        } else {
                                            Log.e("---->", "fail------");
                                        }
                                    }
                                });
                                //post(cPath,'c');
                            }
                            if (x == 'c') {
                                Glide.with(Activity22.this).load(file.getAbsolutePath()).apply(options).into(img_c);


                                StorageReference gsReference = storage.getReferenceFromUrl(mp3Path);
                                Task<Uri> downloadUrl = gsReference.getDownloadUrl();
                                downloadUrl.addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isComplete()) {
                                            post(task.getResult().toString(), null);
                                        } else {
                                            Log.e("---->", "fail------");
                                        }
                                    }
                                });
                                //  post(mp3Path, null);
                            }
                        }
                    });

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
        runnable = null;
        timer.cancel();
        timer = null;
        finish();
    }

}


