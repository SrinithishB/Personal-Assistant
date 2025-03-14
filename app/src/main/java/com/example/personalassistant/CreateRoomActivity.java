package com.example.personalassistant;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class CreateRoomActivity extends AppCompatActivity {
    EditText createRoomEditText;
    ImageView close,create;
    String RoomName;
    String roomCode;
    FirebaseDatabase database;
    DatabaseReference myRef;
    int imageId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_room);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        createRoomEditText=findViewById(R.id.RoomName);
        close=findViewById(R.id.close);
        create=findViewById(R.id.tick);
        database = FirebaseDatabase.getInstance(String.valueOf(R.string.firebase_realtime_db_url));
        myRef = database.getReference("rooms");

        myRef.setValue("Hello, World!");
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RoomName=createRoomEditText.getText().toString();
                if (RoomName.isEmpty()){
                    createRoomEditText.setError("Room name is required");
                }else{
                    createClass();
                }
            }
        });
    }
    private void createClass() {
//        FirebaseAuth firebaseAuth;
//        firebaseAuth= FirebaseAuth.getInstance();
//        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
//        Random random=new Random();
//        imageId=random.nextInt(5);
//        if (imageId<=0||imageId>5){
//            imageId=1;
//        }
//        RoomName=createRoomEditText.getText().toString();
//        roomCode=createRoomCode();
//        Boolean check=checkRoomCode(roomCode);
//        while (check==false){
//            classCode=createClassCode();
//            check=checkClassCode(classCode);
//        }
//        ClassData classData=new ClassData(ClassName,Section,Room,Subject,firebaseUser.getDisplayName(),firebaseUser.getEmail(),classCode, SimpleDateFormat.getDateInstance().format(new Date()),imageId,TeacherCode);
//        firestore.collection("classroom").document("dDNnzpXf1wYDRKsYuby1").collection(classCode).document("class_data").set(classData).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                createTeacherList();
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_SHORT).show();
//            }
//        });
    }
//    private Boolean checkRoomCode(String classCode) {
//        final Boolean[] s = new Boolean[1];
//        s[0]=true;
//        firestore=FirebaseFirestore.getInstance();
//        firestore.collection("classroom").document("dDNnzpXf1wYDRKsYuby1").collection(classCode).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//            @Override
//            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                if (!(queryDocumentSnapshots.isEmpty())){
//                    s[0] =false;
//                }
//            }
//        });
//        return s[0];
//    }
    private String createRoomCode() {
        String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 8) { // length of the random string.
            int index = (int) (rnd.nextFloat() * CHARS.length());
            salt.append(CHARS.charAt(index));
        }
        String code = salt.toString();
        return code;
    }
}