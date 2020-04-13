package moe.noitamina.ohli;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.List;

import moe.noitamina.ohli.db.DBHelper;
import moe.noitamina.ohli.db.TableAni;
import moe.noitamina.ohli.db.TableMaker;
import moe.noitamina.ohli.db.vo.VoAni;
import moe.noitamina.ohli.db.vo.VoMaker;
import moe.noitamina.ohli.filter.FilterAniActivity;
import moe.noitamina.ohli.filter.FilterMakerActivity;

public class MainActivity extends Activity implements View.OnClickListener {

    private TextView tvAniTitle, tvAniList, tvMakerTitle, tvMakerList;
    private LinearLayout llAni, llMaker;
    private WebView wvWidget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                FirebaseMessaging.getInstance().subscribeToTopic("OHLI");
//                FirebaseMessaging.getInstance().subscribeToTopic("test");
            }
        });


        tvAniTitle   = findViewById(R.id.tvAniTitle  );
        tvAniList    = findViewById(R.id.tvAniList   );
        tvMakerTitle = findViewById(R.id.tvMakerTitle);
        tvMakerList  = findViewById(R.id.tvMakerList );
        llAni        = findViewById(R.id.llAni       );
        llMaker      = findViewById(R.id.llMaker     );
        wvWidget     = findViewById(R.id.wvWidget    );

        llAni  .setOnClickListener(this);
        llMaker.setOnClickListener(this);

        WebSettings ws = wvWidget.getSettings();
        ws.setJavaScriptEnabled(true);
        ws.setDomStorageEnabled(true);
        wvWidget.loadUrl("https://ohli.moe/timetable/widget.html");

        boolean notiTest = false;
        if (notiTest) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        NotiManager nm = NotiManager.getInstance(MainActivity.this);
                        nm.notiSubtitle(00, "하느", "가나다라", "1", "https://www.google.com/");
                        Thread.sleep(5000);
                        nm.notiSubtitle(10, "하느", "가나다라", "2", "https://www.google.com/");
                        Thread.sleep(5000);
                        nm.notiSubtitle(20, "하느", "가나다라", "3", "https://www.google.com/");
                        Thread.sleep(5000);
                        nm.notiSubtitle(20, "하느", "가나다라", "4", "https://www.google.com/");
                        Thread.sleep(5000);
                        nm.notiSubtitle(10, "하느", "가나다라", "5", "https://www.google.com/");
                        Thread.sleep(5000);
                        nm.notiSubtitle(30, "하느", "가나다라", "6", "https://www.google.com/");
                        Thread.sleep(5000);
                        nm.notiSubtitle(30, "하느", "가나다라", "7", "https://www.google.com/");
                        Thread.sleep(5000);
                        nm.notiSubtitle(40, "하느", "가나다라", "8", "https://www.google.com/");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        DBHelper db = DBHelper.getInstance(getApplicationContext());
        TableAni   ta = db.useAni();
        TableMaker tm = db.useMaker();

        StringBuilder sb;

        sb = new StringBuilder();
        List<VoAni> aList = ta.usingList();
        for (VoAni item : aList) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(item.getTitle());
        }
        tvAniList.setText(sb.length() > 0 ? sb.toString() : getString(R.string.all_ani));

        sb = new StringBuilder();
        List<VoMaker> mList = tm.usingList();
        for (VoMaker item : mList) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(item.getTitle());
        }
        tvMakerList.setText(sb.length() > 0 ? sb.toString() : getString(R.string.all_maker));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llAni:
                startActivity(new Intent().setClass(this, FilterAniActivity.class));
                break;
            case R.id.llMaker:
                startActivity(new Intent().setClass(this, FilterMakerActivity.class));
                break;
        }
    }
}
