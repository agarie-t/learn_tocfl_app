package my.xxpt;

import static my.xxpt.Util.listRead;
import static my.xxpt.Util.path;
import static my.xxpt.Util.strings;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
public class ReadingType4Activity extends AppCompatActivity{
    private TextView time;
    private ArrayList<DataReading> dataReadings;
    private TextView topicno;
    private TextView imageView1;
    private TextView imageView2;
    private TextView imageView3;
    private TextView imageView4;
    private TextView imageView5;
    private TextView imageView6;
    private int position;
    private FirebaseStorage storage;
    private TextView answer;
    private TextView content;
    private TextView question;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading_type4);
        ImmersionBar.with(ReadingType4Activity.this).statusBarColor(R.color.main).init();

        storage = FirebaseStorage.getInstance();
        EventBus.getDefault().register(this);
        dataReadings = new ArrayList<>();
        for (int i = 40; i < 45; i++) {
            dataReadings.add(Util.listRead.get(i));
        }
        time = findViewById(R.id.time);
        topicno = findViewById(R.id.topicno);
        question = findViewById(R.id.question);
        answer = findViewById(R.id.answer);
        content = findViewById(R.id.content);
        imageView1 = findViewById(R.id.answer1);
        imageView2 = findViewById(R.id.answer2);
        imageView3 = findViewById(R.id.answer3);
        imageView4 = findViewById(R.id.answer4);
        imageView5 = findViewById(R.id.answer5);
        imageView6 = findViewById(R.id.answer6);

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

        imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.strings.set(position,"D");
                answer.setText("Selected: "+"D");
                toB();
            }
        });

        imageView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.strings.set(position,"E");
                answer.setText("Selected: "+"E");
                toB();
            }
        });

        imageView6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.strings.set(position,"F");
                answer.setText("Selected: "+"F");
                toB();
            }
        });

        position = getIntent().getIntExtra("position", 40);
        loadData();
    }



    private void toB() {}


    private void loadImage(String path, ImageView imageView) {
        StorageReference gsReference = storage.getReferenceFromUrl(path);
        Task<Uri> downloadUrl = gsReference.getDownloadUrl();
        downloadUrl.addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isComplete()){
                    Glide.with(ReadingType4Activity.this).load(task.getResult().toString()).into(imageView);
                }else {
                    Log.e("---->","fail------");
                }
            }
        });


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
        EventBus.getDefault().post(new PositionEvent(3,position));

        EventBus.getDefault().unregister(this);
    }

    public void previous(View view) {
        if (position ==40){
        }else {
            position--;
            loadData();
        }
    }

    @SuppressLint("SetTextI18n")
    private void loadData() {
        topicno.setText(/*"第"+(position +1)+"题"*/"");







        content.setText(dataReadings.get(position-40).getTopiccontent());
        imageView1.setText(dataReadings.get(position-40).getA());
        imageView2.setText(dataReadings.get(position-40).getB());
        imageView3.setText(dataReadings.get(position-40).getC());
        imageView4.setText(dataReadings.get(position-40).getD());
        imageView5.setText(dataReadings.get(position-40).getE());
        imageView6.setText(dataReadings.get(position-40).getF());

        answer.setText(TextUtils.isEmpty(Util.strings.get(position))?"":"Selected: "+Util.strings.get(position));

        question.setText(dataReadings.get(position-40).getQuestion());
    }

    public void next(View view) {
        if (position ==44){
        }else {
            position++;
            loadData();
        }
    }

    public void finish(View view) {
        finish();
    }
}


