package com.example.mycanvas;

public class User {
    public String email, age, gender;

    public User() {

    }

    public User(String email, String age, String gender) {
        this.email = email;
        this.gender = gender;
        this.age = age;

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() { return gender; }

    public void setGender(String gender) { this.gender = gender;}

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }
}
