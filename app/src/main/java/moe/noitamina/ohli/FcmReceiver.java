package moe.noitamina.ohli;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import moe.noitamina.ohli.db.DBHelper;
import moe.noitamina.ohli.db.TableAni;
import moe.noitamina.ohli.db.TableMaker;

public class FcmReceiver extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String type = remoteMessage.getMessageType();
        if (!"notification".equals(type)) {
            try {
                Map<String, String> data = remoteMessage.getData();
                String aniNo    = data.get("aniNo"   );
                String aniTitle = data.get("aniTitle");
                String makerNo  = data.get("makerNo" );
                String nick     = data.get("nick"    );
                String url      = data.get("url"     );
                String ep       = data.get("episode" );

                int aNo = (int) Double.parseDouble(aniNo);
                int mNo = (int) Double.parseDouble(makerNo);
                int notiId = mNo * 100000 + aNo;

                if (!url.startsWith("http")) url = "http://" + url;


                // 애니 필터
                TableAni ta = DBHelper.getInstance(getApplicationContext()).useAni();
                // 선택된 애니가 0개일 경우 필터링 안 함
                if (ta.usingCount() > 0) {
                    if (ta.selectUsing(mNo) == null) {
                        return;
                    }
                }

                // 제작자 필터
                TableMaker tm = DBHelper.getInstance(getApplicationContext()).useMaker();
                if (tm.usingCount() > 0) {
                    if (tm.selectUsing(mNo) == null) {
                        return;
                    }
                }

                // 알림 띄우기
                NotiManager.getInstance(this).notiSubtitle(notiId, nick, aniTitle, ep, url);

            } catch (Exception e) {
                // 일단은 테스트 중 값 잘못 넣었을 때 여기로 올 거임
                e.printStackTrace();
            }
        }
    }
}
