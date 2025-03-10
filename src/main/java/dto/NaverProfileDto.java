package dto;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.Getter;

@Getter
public class NaverProfileDto {
    private String id;
    private String name;
    private String email;
    private String profileImage;

    // 유저 정보 파싱
    public NaverProfileDto(String jsonResponseBody) {
        JsonParser jsonParser = new JsonParser();
        JsonElement element = jsonParser.parse(jsonResponseBody);

        this.id = element.getAsJsonObject().get("response").getAsJsonObject().get("id").getAsString();
        this.name = element.getAsJsonObject().get("response").getAsJsonObject().get("name").getAsString();
        this.email = element.getAsJsonObject().get("response").getAsJsonObject().get("email").getAsString();
        this.profileImage = element.getAsJsonObject().get("response").getAsJsonObject().get("profile_image").getAsString();
    }
}