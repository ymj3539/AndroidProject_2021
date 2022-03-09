package kr.co.company.project3.Chatting;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import kr.co.company.project3.DataClass.ItemData;
import kr.co.company.project3.R;
import kr.co.company.project3.Util.AgmPrefer;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<ChatData> chatList;
    private String nick; // 1:1 or 1:da로

    String chatroom;
    EditText EditText_chat;
    Button Button_send;
    DatabaseReference myRef;
    AgmPrefer ap;
    private FirebaseAuth mAuth; //선언함.



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();//초기화함.


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ap = new AgmPrefer(ChatActivity.this);
        ItemData idata = new ItemData();
        //닉네임
        idata.reg_user = ap.getNickname();
        nick = idata.reg_user;

        Button_send = findViewById(R.id.Button_send);
        EditText_chat = findViewById(R.id.EditText_chat);


        Intent intent = getIntent();
        chatroom= intent.getStringExtra("chatName");

        String newMessage = chatroom;
        final TextView chatroomName=findViewById(R.id.chatroomName);
        chatroomName.setText(newMessage);

        Calendar calendar= Calendar.getInstance(); //현재 시간을 가지고 있는 객체

        //샌드 눌렀을떄
        Button_send.setOnClickListener(view -> {
            String time = calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE); //날짜 메시지로 입력
            String msg = EditText_chat.getText().toString(); //msg
            String user_id = mAuth.getCurrentUser().getEmail();


            //널이 아닐때만 값전송하게
            if(msg.trim().length() > 0){
                ChatData chat = new ChatData();
                chat.setNickname(nick);
                chat.setMsg(msg);
                chat.setTime(time);
                chat.setUser_id(user_id);



                myRef.push().setValue(chat); //setValue(chat)에서 수정 push() 붙였음
                EditText_chat.setText(""); //입력필드 초기화

            }else{
                Toast.makeText(getApplicationContext(),"메시지를 입력하세요.", Toast.LENGTH_LONG).show();;
            }
        });


        mRecyclerView = findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        chatList = new ArrayList<>();
        mAdapter = new ChatAdapter(chatList, ChatActivity.this, nick);

        mRecyclerView.setAdapter(mAdapter);
        //어뎁터


        // Write a message to the database
        //database 선언과 생성
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("message").child(chatroom);

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d("CHATCHAT", dataSnapshot.getValue().toString());
                ChatData chat = dataSnapshot.getValue(ChatData.class);
                ((ChatAdapter) mAdapter).addChat(chat);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}