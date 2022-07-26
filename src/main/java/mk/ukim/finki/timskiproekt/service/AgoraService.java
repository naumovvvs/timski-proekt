package mk.ukim.finki.timskiproekt.service;

public interface AgoraService {
    String createRTCToken(Long roomId, Long userId);
    String createRTMToken(Long userId) throws Exception;
}
