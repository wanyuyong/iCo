package magic.yuyong.activity;

import java.io.File;

import magic.yuyong.R;
import magic.yuyong.app.AppConstant;
import magic.yuyong.app.MagicApplication;
import magic.yuyong.app.MagicDialog;
import magic.yuyong.persistence.AccessTokenKeeper;
import magic.yuyong.persistence.Persistence;
import magic.yuyong.service.NotificationService;
import magic.yuyong.util.Debug;
import magic.yuyong.util.SDCardUtils;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.Toast;


public class SettingActivity extends BaseActivity implements OnClickListener {

    private CheckBox timeline_mode, notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        actionBar.setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.setting);
        findViewById(R.id.attention_but).setOnClickListener(this);
        findViewById(R.id.search_but).setOnClickListener(this);
        findViewById(R.id.about_but).setOnClickListener(this);
        findViewById(R.id.feedback_but).setOnClickListener(this);
        findViewById(R.id.exit_but).setOnClickListener(this);
        findViewById(R.id.time_line_but).setOnClickListener(this);
        findViewById(R.id.notification_but).setOnClickListener(this);
        findViewById(R.id.donate_but).setOnClickListener(this);
        timeline_mode = (CheckBox) findViewById(R.id.timeline_mode);
        timeline_mode.setChecked(Persistence
                .isTimeLineMode(getApplicationContext()));
        notification = (CheckBox) findViewById(R.id.notification);
        notification.setChecked(Persistence
                .isReceiveNotification(getApplicationContext()));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.donate_but:
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri uri = Uri.parse(AppConstant.DONATE_HOME);
                intent.setData(uri);
                startActivity(intent);
                break;
            case R.id.attention_but:
                Intent profileIntent = new Intent(getApplicationContext(),
                        ProfileActivity.class);
                profileIntent.putExtra("uid", 1069103203l);
                startActivity(profileIntent);
                break;
            case R.id.search_but:
                Toast.makeText(
                        getApplicationContext(), "介个功能还木做...",
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.about_but:
                Intent aboutIntent = new Intent(getApplicationContext(),
                        AboutActivity.class);
                startActivity(aboutIntent);
                break;
            case R.id.feedback_but:
                Intent feedbackIntent = new Intent(getApplicationContext(), NewPostActivity.class);
                feedbackIntent.putExtra("#", getResources().getString(R.string.text_feedback_topic));
                startActivity(feedbackIntent);
                break;
            case R.id.notification_but:
                boolean receive = !notification.isChecked();
                notification.setChecked(receive);
                Persistence
                        .setReceiveNotification(getApplicationContext(), receive);
                Intent service = new Intent(getApplicationContext(),
                        NotificationService.class);
                if (receive) {
                    startService(service);
                } else {
                    stopService(service);
                }
                break;
            case R.id.time_line_but:
                boolean checked = !timeline_mode.isChecked();
                timeline_mode.setChecked(checked);
                Persistence.setTimeLineMode(getApplicationContext(), checked);
                break;
            case R.id.exit_but:
                final MagicDialog exit_dialog = new MagicDialog(this,
                        R.style.magic_dialog);
                exit_dialog.setMessage(getResources()
                        .getString(R.string.title_exit),
                        getResources().getString(R.string.text_sure_to_logout));
                exit_dialog.addButton(R.string.but_logout, new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        exit_dialog.dismiss();

                        //clean accessToken
                        AccessTokenKeeper.clear(getApplicationContext());
                        MagicApplication.getInstance().setAccessToken(null);

                        android.webkit.CookieManager.getInstance().removeAllCookie();

                        String[] databaseNames = getApplication().databaseList();
                        for(String name : databaseNames){
                            Debug.v("database name : "+name);
                            if(name.toLowerCase().startsWith("webview")){
                                getApplication().deleteDatabase(name);
                            }
                        }

                        File cache = getApplication().getCacheDir();
                        for(File file : cache.listFiles()){
                            Debug.v("cache name : "+file.getName());
                            if(file.getName().toLowerCase().startsWith("webview")){
                                SDCardUtils.deleteDir(file);
                            }
                        }

                        Toast.makeText(
                                getApplicationContext(),
                                getResources().getString(
                                        R.string.text_login_out_success),
                                Toast.LENGTH_SHORT).show();

                        //stop service
                        Intent service = new Intent(getApplicationContext(),
                                NotificationService.class);
                        stopService(service);

                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        shutDown();
                        
                    }
                });
                exit_dialog.addButton(R.string.but_cancel, new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        exit_dialog.dismiss();
                    }
                });
                exit_dialog.show();
                break;
        }
    }
}
