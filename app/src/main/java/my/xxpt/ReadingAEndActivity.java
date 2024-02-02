package my.xxpt;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.gyf.immersionbar.ImmersionBar;

public class ReadingAEndActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banda_end);
        ImmersionBar.with(ReadingAEndActivity.this).statusBarColor(R.color.main).init();

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
        startActivity(new Intent(ReadingAEndActivity.this,ReadingBHomeActivity.class));
    }
}