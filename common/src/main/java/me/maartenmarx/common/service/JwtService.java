package me.maartenmarx.common.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.Serializable;
import java.util.Base64;

public class JwtService {
    static Base64.Decoder decoder = Base64.getDecoder();
    static ObjectMapper om = new ObjectMapper();

    public static UserData getUserData(String bearer) {
        var token = bearer.startsWith("Bearer ") ? bearer.split(" ")[1] : bearer;

        var payload = decoder.decode(token.split("\\.")[1]);

        try {
            return om.readValue(payload, UserData.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UserData implements Serializable {
        String email;
        String name;
    }
}
