package com.example.personalassistant;

public class RoomData {

    private String room_id;
    private String room_name;
    private String created_by;
    private String created_by_email;
    private String created_by_user_id; // Missing variable
    private String room_code;
    private String created_date;
    private int imageview_id;

    // Constructor to initialize all fields
    public RoomData( String room_name, String created_by, String created_by_email,
                    String created_by_user_id, String room_code, String created_date, int imageview_id) {
        this.room_name = room_name;
        this.created_by = created_by;
        this.created_by_email = created_by_email;
        this.created_by_user_id = created_by_user_id;
        this.room_code = room_code;
        this.created_date = created_date;
        this.imageview_id = imageview_id;
    }
//    public RoomData(String room_id,String room_name, String created_by, String created_by_email,
//                     String created_by_user_id, String room_code, String created_date, int imageview_id) {
//        this.room_id = room_id;
//        this.room_name = room_name;
//        this.created_by = created_by;
//        this.created_by_email = created_by_email;
//        this.created_by_user_id = created_by_user_id;
//        this.room_code = room_code;
//        this.created_date = created_date;
//        this.imageview_id = imageview_id;
//    }
    public RoomData() {
    }

    // Getter and Setter for room_id
    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    // Getter and Setter for room_name
    public String getRoom_name() {
        return room_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }

    // Getter and Setter for created_by
    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    // Getter and Setter for created_by_email
    public String getCreated_by_email() {
        return created_by_email;
    }

    public void setCreated_by_email(String created_by_email) {
        this.created_by_email = created_by_email;
    }

    // Getter and Setter for created_by_user_id (Missing variable)
    public String getCreated_by_user_id() {
        return created_by_user_id;
    }

    public void setCreated_by_user_id(String created_by_user_id) {
        this.created_by_user_id = created_by_user_id;
    }

    // Getter and Setter for room_code
    public String getRoom_code() {
        return room_code;
    }

    public void setRoom_code(String room_code) {
        this.room_code = room_code;
    }

    // Getter and Setter for created_date
    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    // Getter and Setter for imageview_id
    public int getImageview_id() {
        return imageview_id;
    }

    public void setImageview_id(int imageview_id) {
        this.imageview_id = imageview_id;
    }
}
