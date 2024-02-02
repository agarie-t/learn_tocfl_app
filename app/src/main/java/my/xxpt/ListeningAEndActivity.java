package my.xxpt;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gyf.immersionbar.ImmersionBar;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class ListeningAEndActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listeninga_end);
        ImmersionBar.with(ListeningAEndActivity.this).statusBarColor(R.color.main).init();

        TextView textView4 = findViewById(R.id.textView4);
        TextView post2 = findViewById(R.id.post2);

        int aTrue = getIntent().getIntExtra("true", 0);
        int total = getIntent().getIntExtra("total", 0);
        textView4.setText("End of questions, total correct answers："+aTrue+"/"+total+"questions!");
        post2.setVisibility(aTrue>=Util.No_?View.VISIBLE:View.GONE);
    }

    public void close(View view) {
        finish();
    }

    public void tob(View view) {
        toBandB();

    }


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
                startActivity(new Intent(ListeningAEndActivity.this,ActivityBandB.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

}