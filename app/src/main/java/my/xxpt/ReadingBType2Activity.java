package my.xxpt;

import static my.xxpt.Util.listRead2;
import static my.xxpt.Util.path;

import android.annotation.SuppressLint;
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

public class ReadingBType2Activity extends AppCompatActivity {
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
    private ImageView content2;
    private TextView question;
    private int tag;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_readingb_type2);
        ImmersionBar.with(ReadingBType2Activity.this).statusBarColor(R.color.main).init();
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
        storage = FirebaseStorage.getInstance();
        EventBus.getDefault().register(this);
        dataReadings = new ArrayList<>();
        for (int i = tag; i < listRead2.size(); i++) {
            dataReadings.add(Util.listRead2.get(i));
        }
        time = findViewById(R.id.time);
        topicno = findViewById(R.id.topicno);
        question = findViewById(R.id.question);
        answer = findViewById(R.id.answer);
        content = findViewById(R.id.content);
        content2 = findViewById(R.id.content2);
        imageView1 = findViewById(R.id.answer1);
        imageView2 = findViewById(R.id.answer2);
        imageView3 = findViewById(R.id.answer3);
        imageView4 = findViewById(R.id.answer4);


        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.strings2.set(position, "A");
                answer.setText("Selected: " + "A");
                //
            }
        });

        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.strings2.set(position, "B");
                answer.setText("Selected: " + "B");
            }
        });
        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.strings2.set(position, "C");
                answer.setText("Selected: " + "C");
            }
        });

        imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.strings2.set(position, "D");
                answer.setText("Selected: " + "D");
            }
        });


        position = getIntent().getIntExtra("position", 0);
        tag = getIntent().getIntExtra("tag", 0);
        loadData();
    }


    private void loadImage(String path, ImageView imageView) {
        StorageReference gsReference = storage.getReferenceFromUrl(path);
        Task<Uri> downloadUrl = gsReference.getDownloadUrl();
        downloadUrl.addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isComplete()) {
                    Glide.with(ReadingBType2Activity.this).load(task.getResult().toString()).into(imageView);
                } else {
                    Log.e("---->", "fail------");
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
        EventBus.getDefault().post(new PositionEvent(1, position));

        EventBus.getDefault().unregister(this);
    }

    public void previous(View view) {
        if (position == tag) {
        } else {
            position--;
            loadData();
        }
    }

    @SuppressLint("SetTextI18n")
    private void loadData() {
        topicno.setText(/*"第"+(position +1)+"题"*/"");
        if (listRead2.get(position).getTopiccontent() == null) {
            content.setVisibility(View.GONE);
            content2.setVisibility(View.VISIBLE);
            String aPath = path + "B" + listRead2.get(position).getVersion() + "_reading_img/" + toUP(listRead2.get(position).getTopicimage());

            loadImage(aPath, content2);
        } else {
            content.setVisibility(View.VISIBLE);
            content2.setVisibility(View.GONE);
            content.setText(listRead2.get(position).getTopiccontent());
        }


        imageView1.setText(listRead2.get(position).getA());
        imageView2.setText(listRead2.get(position).getB());
        imageView3.setText(listRead2.get(position).getC());
        imageView4.setText(listRead2.get(position).getD());

        answer.setText(TextUtils.isEmpty(Util.strings2.get(position)) ? "" : "Selected: " + Util.strings2.get(position));

        question.setText(listRead2.get(position).getQuestion());
    }

    private String toUP(String s) {

        String s1 = s.substring(0, s.indexOf("."));
        String s2 = s.substring(s.indexOf("."), s.length());
        return s1.toUpperCase() + s2;
    }

    public void next(View view) {
        if (position == listRead2.size() - 1) {
        } else {
            position++;
            loadData();
        }
    }

    public void finish(View view) {
        finish();
    }
}


