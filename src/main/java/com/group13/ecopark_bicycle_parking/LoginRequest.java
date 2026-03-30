package com.group13.ecopark_bicycle_parking;

// Class này dùng để nhận dữ liệu từ request login
// Người dùng có thể nhập username hoặc email vào loginInput
public class LoginRequest {

    // loginInput: có thể là username hoặc email
    private String loginInput;

    // password đăng nhập
    private String password;

    public String getLoginInput() {
        return loginInput;
    }

    public void setLoginInput(String loginInput) {
        this.loginInput = loginInput;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}