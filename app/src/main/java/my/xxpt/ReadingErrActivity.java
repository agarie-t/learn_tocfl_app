package my.xxpt;

import static my.xxpt.Util.path;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
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
import java.util.Iterator;
import java.util.Locale;

import my.xxpt.databinding.ActivityReadingErrBinding;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class ReadingErrActivity extends AppCompatActivity {
    private int position = 0;
    private FirebaseStorage storage;
    ArrayList<DataReading> errDatas = new ArrayList<>();
    private my.xxpt.databinding.ActivityReadingErrBinding inflate;
    //这里处理题型1  三张图片选项     这里拿到util中list中前15个题目

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inflate = ActivityReadingErrBinding.inflate(getLayoutInflater());
        setContentView(inflate.getRoot());
        ImmersionBar.with(ReadingErrActivity.this).statusBarColor(R.color.main).init();

        storage = FirebaseStorage.getInstance();
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
        mSpeech = new TextToSpeech(ReadingErrActivity.this, new TTSListener());
        //获取数据
        FirebaseDatabase.getInstance().getReference().child("record").child(FirebaseAuth.getInstance().getUid()).child("list2").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //todo 添加数据
                errDatas.clear();
                Iterator<DataSnapshot> iterator = snapshot.getChildren().iterator();
                while (iterator.hasNext()) {
                    DataSnapshot next = iterator.next();
                    DataReading value = next.getValue(DataReading.class);
                    errDatas.add(value);
                }
                //开始展示数据

                loadData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void loadImage(String path, ImageView imageView) {
        StorageReference gsReference = storage.getReferenceFromUrl(path);
        Task<Uri> downloadUrl = gsReference.getDownloadUrl();
        downloadUrl.addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isComplete()) {
                    Glide.with(ReadingErrActivity.this).load(task.getResult().toString()).into(imageView);
                } else {
                    Log.e("---->", "fail------");
                }
            }
        });


    }


    private String toUP(String s) {
        String s1 = s.substring(0, s.indexOf("."));
        String s2 = s.substring(s.indexOf("."), s.length());
        return s1.toUpperCase() + s2;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().post(new PositionEvent(0, position));

        EventBus.getDefault().unregister(this);
    }

    public void previous(View view) {
        if (position == 0) {
        } else {
            position--;
            loadData();
        }
    }

    @SuppressLint("SetTextI18n")
    private void loadData() {
        inflate.topicno.setText("第" + (position + 1) + "题");
        //获取当前的数据
        DataReading dataReading = errDatas.get(position);

        //展示了数据  则需要删除这条数据
        FirebaseDatabase.getInstance().getReference().child("record").child(FirebaseAuth.getInstance().getUid()).child("list2").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    //todo  如果没有数据  不处理
                } else {
                    //todo 删除这条数据
                    Iterator<DataSnapshot> iterator = snapshot.getChildren().iterator();
                    ArrayList<DataReading> records = new ArrayList<>();
                    while (iterator.hasNext()) {


                        DataSnapshot next = iterator.next();
                        DataReading value = next.getValue(DataReading.class);
                        //表示  把这条数据更新掉
                        if (dataReading.getId() != value.getId()) {
                            records.add(value);
                        }
                    }
                    //重新set数据
                    FirebaseDatabase.getInstance().getReference().child("record").child(FirebaseAuth.getInstance().getUid()).child("list2").setValue(records);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //首先判断是什么类型  然后展示对应的布局
        int type = Integer.parseInt(dataReading.getType());
        loadView(type);


        //如果类型是1  加载数据
        //加载每一题之前   把数据变成中文
        isEnglish = false;

        if (type == 1) {
            inflate.content.setText(dataReading.getQuestion());

            String aPath = path + "BandA_" + dataReading.getVersion() + "RdPIC/" + toUP(dataReading.getA());
            String bPath = path + "BandA_" + dataReading.getVersion() + "RdPIC/" + toUP(dataReading.getB());
            String cPath = path + "BandA_" + dataReading.getVersion() + "RdPIC/" + toUP(dataReading.getC());

            loadImage(aPath, inflate.image1);
            loadImage(bPath, inflate.image2);
            loadImage(cPath, inflate.image3);


        inflate.imageTr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEnglish = !isEnglish;
                inflate.content.setText(isEnglish?dataReading.getQ_Translation():dataReading.getQuestion());
            }
        });

        } else if (type == 2) {
            String aPath = path + "BandA_" + dataReading.getVersion() + "RdPIC/" + toUP(dataReading.getTopicimage());

            loadImage(aPath, inflate.content2);

            inflate.answer1.setText(dataReading.getA());
            inflate.answer2.setText(dataReading.getB());
            inflate.answer3.setText(dataReading.getC());


            inflate.answer1.setTextColor(dataReading.getAnswer().toUpperCase().equals("A")?
                    getResources().getColor(R.color.main_red):getResources().getColor(R.color.black));
            inflate.answer2.setTextColor(dataReading.getAnswer().toUpperCase().equals("B")?
                    getResources().getColor(R.color.main_red):getResources().getColor(R.color.black));
            inflate.answer3.setTextColor(dataReading.getAnswer().toUpperCase().equals("C")?
                    getResources().getColor(R.color.main_red):getResources().getColor(R.color.black));


            inflate.imageTr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isEnglish = !isEnglish;
                    inflate.answer1.setText(isEnglish?dataReading.getA_Translation():dataReading.getA());
                    inflate.answer2.setText(isEnglish?dataReading.getB_Translation():dataReading.getB());
                    inflate.answer3.setText(isEnglish?dataReading.getC_Translation():dataReading.getC());
                }
            });

        } else if (type == 3) {
            String aPath = path + "BandA_" + dataReading.getVersion() + "RdPIC/" + toUP(dataReading.getTopicimage());

            loadImage(aPath, inflate.content3);

            inflate.answer31.setText(dataReading.getA());
            inflate.answer32.setText(dataReading.getB());
            inflate.answer33.setText(dataReading.getC());

            inflate.question3.setText(dataReading.getQuestion());


            inflate.answer31.setTextColor(dataReading.getAnswer().toUpperCase().equals("A")?
                    getResources().getColor(R.color.main_red):getResources().getColor(R.color.black));
            inflate.answer32.setTextColor(dataReading.getAnswer().toUpperCase().equals("B")?
                    getResources().getColor(R.color.main_red):getResources().getColor(R.color.black));
            inflate.answer33.setTextColor(dataReading.getAnswer().toUpperCase().equals("C")?
                    getResources().getColor(R.color.main_red):getResources().getColor(R.color.black));

            inflate.question3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    readText(inflate.question3.getText().toString());
                }
            });

            inflate.imageTr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isEnglish = !isEnglish;
                    inflate.question3.setText(isEnglish?dataReading.getQ_Translation():dataReading.getQuestion());
                    inflate.answer31.setText(isEnglish?dataReading.getA_Translation():dataReading.getA());
                    inflate.answer32.setText(isEnglish?dataReading.getB_Translation():dataReading.getB());
                    inflate.answer33.setText(isEnglish?dataReading.getC_Translation():dataReading.getC());
                }
            });


        } else if (type == 4) {
            inflate.content4.setText(dataReading.getTopiccontent());
            inflate.answer41.setText(dataReading.getA());
            inflate.answer42.setText(dataReading.getB());
            inflate.answer43.setText(dataReading.getC());
            inflate.answer44.setText(dataReading.getD());
            inflate.answer45.setText(dataReading.getE());
            inflate.answer46.setText(dataReading.getF());


            inflate.question4.setText(dataReading.getQuestion());



            inflate.answer41.setTextColor(dataReading.getAnswer().toUpperCase().equals("A")?
                    getResources().getColor(R.color.main_red):getResources().getColor(R.color.black));
            inflate.answer42.setTextColor(dataReading.getAnswer().toUpperCase().equals("B")?
                    getResources().getColor(R.color.main_red):getResources().getColor(R.color.black));
            inflate.answer43.setTextColor(dataReading.getAnswer().toUpperCase().equals("C")?
                    getResources().getColor(R.color.main_red):getResources().getColor(R.color.black));
            inflate.answer44.setTextColor(dataReading.getAnswer().toUpperCase().equals("D")?
                    getResources().getColor(R.color.main_red):getResources().getColor(R.color.black));
            inflate.answer45.setTextColor(dataReading.getAnswer().toUpperCase().equals("E")?
                    getResources().getColor(R.color.main_red):getResources().getColor(R.color.black));
            inflate.answer46.setTextColor(dataReading.getAnswer().toUpperCase().equals("F")?
                    getResources().getColor(R.color.main_red):getResources().getColor(R.color.black));

            inflate.question4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    readText(inflate.question4.getText().toString());
                }
            });
            inflate.content4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    readText(inflate.content4.getText().toString());
                }
            });


            inflate.imageTr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isEnglish = !isEnglish;
                    inflate.question4.setText(isEnglish?dataReading.getQ_Translation():dataReading.getQuestion());
                    inflate.content4.setText(isEnglish?dataReading.getContent_Translation():dataReading.getTopiccontent());
                    inflate.answer41.setText(isEnglish?dataReading.getA_Translation():dataReading.getA());
                    inflate.answer42.setText(isEnglish?dataReading.getB_Translation():dataReading.getB());
                    inflate.answer43.setText(isEnglish?dataReading.getC_Translation():dataReading.getC());
                    inflate.answer44.setText(isEnglish?dataReading.getD_Translation():dataReading.getD());
                    inflate.answer45.setText(isEnglish?dataReading.getE_Translation():dataReading.getE());
                    inflate.answer46.setText(isEnglish?dataReading.getF_Translation():dataReading.getF());
                }
            });

        } else if (type == 5) {

            inflate.content5.setText(dataReading.getTopiccontent());
            inflate.answer51.setText(dataReading.getA());
            inflate.answer52.setText(dataReading.getB());
            inflate.answer53.setText(dataReading.getC());
            inflate.answer54.setText(dataReading.getD());


            inflate.question5.setText(dataReading.getQuestion());


            inflate.answer51.setTextColor(dataReading.getAnswer().toUpperCase().equals("A")?
                    getResources().getColor(R.color.main_red):getResources().getColor(R.color.black));
            inflate.answer52.setTextColor(dataReading.getAnswer().toUpperCase().equals("B")?
                    getResources().getColor(R.color.main_red):getResources().getColor(R.color.black));
            inflate.answer53.setTextColor(dataReading.getAnswer().toUpperCase().equals("C")?
                    getResources().getColor(R.color.main_red):getResources().getColor(R.color.black));
            inflate.answer54.setTextColor(dataReading.getAnswer().toUpperCase().equals("D")?
                    getResources().getColor(R.color.main_red):getResources().getColor(R.color.black));

            inflate.question5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    readText(inflate.question4.getText().toString());
                }
            });
            inflate.content5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    readText(inflate.content5.getText().toString());
                }
            });
            inflate.imageTr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isEnglish = !isEnglish;
                    inflate.question5.setText(isEnglish?dataReading.getQ_Translation():dataReading.getQuestion());
                    inflate.content5.setText(isEnglish?dataReading.getContent_Translation():dataReading.getTopiccontent());
                    inflate.answer51.setText(isEnglish?dataReading.getA_Translation():dataReading.getA());
                    inflate.answer52.setText(isEnglish?dataReading.getB_Translation():dataReading.getB());
                    inflate.answer53.setText(isEnglish?dataReading.getC_Translation():dataReading.getC());
                    inflate.answer54.setText(isEnglish?dataReading.getD_Translation():dataReading.getD());
                }
            });

        }
        inflate.answer.setText("Correct Answer: " + dataReading.getAnswer().toUpperCase());
    }

    private void loadView(int type) {

        inflate.type1.setVisibility(type == 1 ? View.VISIBLE : View.GONE);
        inflate.type2.setVisibility(type == 2 ? View.VISIBLE : View.GONE);
        inflate.type3.setVisibility(type == 3 ? View.VISIBLE : View.GONE);
        inflate.type4.setVisibility(type == 4 ? View.VISIBLE : View.GONE);
        inflate.type5.setVisibility(type == 5 ? View.VISIBLE : View.GONE);
    }

    public void next(View view) {
        if (position == errDatas.size() - 1) {
        } else {
            position++;
            loadData();
        }
    }


    public void  readText(String s){
        mSpeech.speak(s, TextToSpeech.QUEUE_FLUSH, null);
    }

    public void finish(View view) {
        finish();

        mSpeech.stop(); // 不管是否正在朗读TTS都被中断,看情景需要，在播放下一条时是否中断当前的朗读

        mSpeech.shutdown(); // 关闭，释放资源（这是完全释放textToSpeech，在完全不需要的时候再调用）
    }


    private TextToSpeech mSpeech;
    private boolean isEnglish = false;

    class TTSListener implements TextToSpeech.OnInitListener {
        @Override
        public void onInit(int status) {
            if (mSpeech != null) {
                int isSupportChinese = mSpeech.isLanguageAvailable(Locale.CHINESE);//是否支持中文

                if (isSupportChinese == TextToSpeech.LANG_AVAILABLE) {
                    mSpeech.setLanguage(isEnglish?Locale.ENGLISH:Locale.CHINESE);//设置语言
                    mSpeech.setSpeechRate(1.0f);//设置语
                     mSpeech.setPitch(1.0f);//设置音量
                    mSpeech.getDefaultEngine();//默认引擎
                    if (status == TextToSpeech.SUCCESS) {
                        //初始化TextToSpeech引擎成功，初始化成功后才可以play等

                       // mSpeech.speak(, TextToSpeech.QUEUE_FLUSH, null);
                    }
                }

            } else {
                //初始化TextToSpeech引擎失败

            }
        }
    }
}


