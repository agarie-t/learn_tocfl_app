package my.xxpt;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.gyf.immersionbar.ImmersionBar;

public class HomeActivity extends AppCompatActivity {
    public static final String[] permissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ImmersionBar.with(HomeActivity.this).statusBarColor(R.color.main).init();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(permissions, 0x123);
        }

        //当系统在11及以上
        // 没文件管理权限时申请权限
        if (!Environment.isExternalStorageManager()) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            intent.setData(Uri.parse("package:" + HomeActivity.this.getPackageName()));
            startActivityForResult(intent, 1000);
        }
    }


    public void start(View view) {
        startActivity(new Intent(HomeActivity.this, Activity0.class));
    }


    public void reading(View view) {
        //跳转到Reading

        startActivity(new Intent(HomeActivity.this, ActivityReading0.class));
    }


    public void logout(View view) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(HomeActivity.this);
        dialog.setTitle("Warning").
                setMessage("Are you sure you want to leave the test? (You will proceed to the next question upon your return)").
                setNegativeButton("Confirm", (dialog1, which) -> {
                    dialog1.dismiss();
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                    SPUtils.clear(HomeActivity.this);
                    finish();
                });
        dialog.setPositiveButton("Cancel", (dialog12, which) -> dialog12.dismiss());

        dialog.show();


    }
}