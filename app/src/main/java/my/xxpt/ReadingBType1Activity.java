package my.xxpt;

import static my.xxpt.Util.listRead;
import static my.xxpt.Util.listRead2;
import static my.xxpt.Util.strings;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

public class ReadingBType1Activity extends AppCompatActivity{
    private TextView time;
    private ArrayList<DataReading2> dataReadings;
    private TextView topicno;
    private TextView imageView1;
    private TextView imageView2;
    private TextView imageView3;
    private TextView imageView4;
    private int position;
    private FirebaseStorage storage;
    private TextView answer;
    private TextView content;
    private TextView question;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_readingb_type1);
        ImmersionBar.with(ReadingBType1Activity.this).statusBarColor(R.color.main).init();

        storage = FirebaseStorage.getInstance();
        EventBus.getDefault().register(this);
        dataReadings = new ArrayList<>();
        for (int i = 0; i < listRead2.size()-5; i++) {
            dataReadings.add(Util.listRead2.get(i));
        }

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
        time = findViewById(R.id.time);
        topicno = findViewById(R.id.topicno);
        question = findViewById(R.id.question);
        answer = findViewById(R.id.answer);
        content = findViewById(R.id.content);
        imageView1 = findViewById(R.id.answer1);
        imageView2 = findViewById(R.id.answer2);
        imageView3 = findViewById(R.id.answer3);
        imageView4 = findViewById(R.id.answer4);


        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.strings2.set(position,"A");
                answer.setText("Selected: "+"A");
                //
            }
        });

        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.strings2.set(position,"B");
                answer.setText("Selected: "+"B");
            }
        });
        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.strings2.set(position,"C");
                answer.setText("Selected: "+"C");
            }
        });

        imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.strings2.set(position,"D");
                answer.setText("Selected: "+"D");
            }
        });


        position = getIntent().getIntExtra("position", 0);
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
                    Glide.with(ReadingBType1Activity.this).load(task.getResult().toString()).into(imageView);
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
        content.setText(dataReadings.get(position).getTopiccontent());
        imageView1.setText(dataReadings.get(position).getA());
        imageView2.setText(dataReadings.get(position).getB());
        imageView3.setText(dataReadings.get(position).getC());
        imageView4.setText(dataReadings.get(position).getD());

        answer.setText(TextUtils.isEmpty(Util.strings2.get(position))?"":"Selected: "+Util.strings2.get(position));

        question.setText(dataReadings.get(position).getQuestion());
    }

    public void next(View view) {
        if (position ==listRead2.size()-6){
        }else {
            position++;
            loadData();
        }
    }

    public void finish(View view) {
        finish();
    }
}


