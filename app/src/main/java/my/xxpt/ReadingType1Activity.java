package my.xxpt;

import static my.xxpt.Util.listRead;
import static my.xxpt.Util.path;
import static my.xxpt.Util.strings;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class ReadingType1Activity extends AppCompatActivity{
    private TextView time;
    private ArrayList<DataReading> dataReadings;
    private TextView topicno;
    private ImageView imageView1;
    private ImageView imageView2;
    private ImageView imageView3;
    private int position;
    private FirebaseStorage storage;
    private TextView answer;
    private TextView content;

    //这里处理题型1  三张图片选项     这里拿到util中list中前15个题目

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading_type1);
        ImmersionBar.with(ReadingType1Activity.this).statusBarColor(R.color.main).init();

        storage = FirebaseStorage.getInstance();
        EventBus.getDefault().register(this);
        dataReadings = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            dataReadings.add(Util.listRead.get(i));
        }
        time = findViewById(R.id.time);
        topicno = findViewById(R.id.topicno);
        answer = findViewById(R.id.answer);
        content = findViewById(R.id.content);
        content.setMovementMethod(new ScrollingMovementMethod());
        imageView1 = findViewById(R.id.image1);
        imageView2 = findViewById(R.id.image2);
        imageView3 = findViewById(R.id.image3);

        FirebaseDatabase.getInstance()

                .getReference("record")
                .child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child("readinglevel").exists()) {
                            String level = snapshot.child("readinglevel").child("level").getValue().toString();



                            ((TextView)findViewById(R.id.level)).setText("Level " + level /*+ "\n(" + num + "/"+((level.equals("3")||level.equals("4"))?"15":"45")+")"*/);

                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.strings.set(position,"A");
                answer.setText("Selected: "+"A");
                toB();
                //
            }
        });

        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.strings.set(position,"B");
                answer.setText("Selected: "+"B");
                toB();
            }
        });
        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.strings.set(position,"C");
                answer.setText("Selected: "+"C");
                toB();
            }
        });

        position = getIntent().getIntExtra("position", 0);
        loadData();
    }

    private void toB() {
        //交卷  type 1 时间结束   2主动交卷
       /* int trueNum = 0;

        for (int i = 0; i < listRead.size(); i++) {

            if (!TextUtils.isEmpty(strings.get(i)) && strings.get(i).equals(listRead.get(i).getAnswer().toUpperCase())) {
                trueNum++;
            }
        }
        if (trueNum == Util.No_){
            AlertDialog.Builder dialog = new AlertDialog.Builder(ReadingType1Activity.this);
            dialog.setTitle("提示").
                    setMessage("您已经答对了"+trueNum+"/50题！是否前往BandB？").
                    setNegativeButton("前往BandB", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            EventBus.getDefault().post(new ToBEvent());
                            finish();
                        }
                    });
            dialog.setPositiveButton("继续答题", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }*/


    }



    private void loadImage(String path, ImageView imageView) {
        StorageReference gsReference = storage.getReferenceFromUrl(path);
        Task<Uri> downloadUrl = gsReference.getDownloadUrl();
        downloadUrl.addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isComplete()){
                    Glide.with(ReadingType1Activity.this).load(task.getResult().toString()).into(imageView);
                }else {
                    Log.e("---->","fail------");
                }
            }
        });


    }


    private String toUP(String s){

        String s1 = s.substring(0, s.indexOf("."));
        String s2 = s.substring(s.indexOf("."),s.length());
        return s1.toUpperCase()+s2;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void timeover(CloseEvent closeEvent) {
        finish();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void time(TimeEvent timeEvent) {
        time.setText(Util.timeConversion(timeEvent.getTime()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().post(new PositionEvent(0,position));

        EventBus.getDefault().unregister(this);
    }

    public void previous(View view) {
        if (position ==0){
        }else {
            position--;
            loadData();
        }
    }

    @SuppressLint("SetTextI18n")
    private void loadData() {
        topicno.setText(/*"第"+(position +1)+"题"*/"");
        content.setText(dataReadings.get(position).getQuestion());


        String aPath = path + "BandA_" + dataReadings.get(position).getVersion() + "RdPIC/" + toUP(dataReadings.get(position).getA());
        String bPath = path + "BandA_" + dataReadings.get(position).getVersion() + "RdPIC/" + toUP(dataReadings.get(position).getB());
        String cPath = path + "BandA_" + dataReadings.get(position).getVersion() + "RdPIC/" + toUP(dataReadings.get(position).getC());

        Log.e("--->",aPath);

        loadImage(aPath,imageView1);
        loadImage(bPath,imageView2);
        loadImage(cPath,imageView3);

        answer.setText(TextUtils.isEmpty(Util.strings.get(position))?"":"Selected: "+Util.strings.get(position));
    }

    public void next(View view) {
        if (position ==14){
        }else {
            position++;
            loadData();
        }
    }

    public void finish(View view) {
        finish();
    }
}


