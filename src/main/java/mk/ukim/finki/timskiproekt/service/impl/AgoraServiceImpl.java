package mk.ukim.finki.timskiproekt.service.impl;

import io.agora.media.RtcTokenBuilder;
import io.agora.rtm.RtmTokenBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mk.ukim.finki.timskiproekt.service.AgoraService;
import mk.ukim.finki.timskiproekt.service.RoomService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AgoraServiceImpl implements AgoraService {

    private final RoomService roomService;

    static String appId = "4b77f4fc58994fdd9fe727a7106ad66a";
    static String appCertificate = "17949a1407ec4dc9b365ca950f99e9e2";
    static int expirationTimeInSeconds = 36000;

    @Override
    public String createRTCToken(Long roomId, Long userId) {
        log.info("Creating RTC token for user: {}, trying to join in room: {}", userId, roomId);

        String channelName = roomService.getRoomById(roomId).getId().toString();
        String userAccount = userId.toString();


        RtcTokenBuilder token = new RtcTokenBuilder();
        int timestamp = (int)(System.currentTimeMillis() / 1000 + expirationTimeInSeconds);
//        String result = token.buildTokenWithUserAccount(appId, appCertificate,
//                channelName, userAccount, RtcTokenBuilder.Role.Role_Publisher, timestamp);

        return token.buildTokenWithUid(appId, appCertificate,
                channelName, userId.intValue(), RtcTokenBuilder.Role.Role_Publisher, timestamp);
    }

    @Override
    public String createRTMToken(Long userId) throws Exception {
        log.info("Creating RTM token for user: {}", userId);
        int expireTimestamp = 0;
        RtmTokenBuilder token = new RtmTokenBuilder();

        return token.buildToken(appId, appCertificate, String.valueOf(userId),
                RtmTokenBuilder.Role.Rtm_User, expireTimestamp);
    }
}
