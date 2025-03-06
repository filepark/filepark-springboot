package service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dto.NaverProfileDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class NaverService {
    @Value("${naver.client_id}")
    private String naverClientId;
    @Value("${naver.redirect_uri}")
    private String naverRedirectUri;
    @Value("${naver.client_secret}")
    private String naverClientSecret;

    // access token 요청
    public String getAccessToken(String code, String state) {
        String reqUrl = "https://nid.naver.com/oauth2.0/token";
        RestTemplate restTemplate = new RestTemplate();

        // HttpHeader Object
        HttpHeaders headers = new HttpHeaders();

        // HttpBody Object
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", naverClientId);
        params.add("client_secret", naverClientSecret);
        params.add("code", code);
        params.add("state", state);

        // http body params 와 http headers 를 가진 엔티티
        HttpEntity<MultiValueMap<String, String>> naverTokenRequest = new HttpEntity<>(params, headers);

        // reqUrl로 Http 요청, POST 방식
        ResponseEntity<String> response = restTemplate.exchange(reqUrl,
                HttpMethod.POST,
                naverTokenRequest,
                String.class);

        String responseBody = response.getBody();
        JsonObject asJsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
        return asJsonObject.get("access_token").getAsString();
    }

    // 유저 정보 반환
    public NaverProfileDto getUserInfo(String accessToken) {
        String reqUrl = "https://openapi.naver.com/v1/nid/me";

        RestTemplate restTemplate = new RestTemplate();

        // HttpHeader 오브젝트
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        HttpEntity<MultiValueMap<String, String>> naverProfileRequest = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(reqUrl,
                HttpMethod.POST,
                naverProfileRequest,
                String.class);

//        System.out.println("response = " + response);
        NaverProfileDto naverProfile = new NaverProfileDto(response.getBody());

        return naverProfile;
    }

    // 로그아웃 처리
    public void logout(String accessToken) {
        String logoutUrl = "https://nid.naver.com/oauth2.0/token?grant_type=delete" +
                "&client_id=" + naverClientId +
                "&client_secret=" + naverClientSecret +
                "&access_token=" + accessToken +
                "&service_provider=NAVER";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        HttpEntity<String> logoutRequest = new HttpEntity<>(headers);

        // 로그아웃 요청을 보내는 POST 요청
        ResponseEntity<String> response = restTemplate.exchange(logoutUrl,
                HttpMethod.POST,
                logoutRequest,
                String.class);

        // 로그아웃 성공 여부 확인
        System.out.println("로그아웃 응답 = " + response.getBody());
    }
}
