package com.lovec.network.netty.protocol.mesage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestMessage extends Message {

    private String username;

    private String password;

    private String nickname;

    @Override
    public int getMessageType() {
        return LoginResponseMessage;
    }
}
