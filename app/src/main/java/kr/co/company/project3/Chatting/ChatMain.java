package kr.co.company.project3.Chatting;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;

import kr.co.company.project3.DataClass.ItemData;
import kr.co.company.project3.R;
import kr.co.company.project3.Util.AgmPrefer;

public class ChatMain extends AppCompatActivity implements View.OnClickListener {

    FirebaseDatabase database;
    DatabaseReference myRef;

    private EditText chatroom;
    private Button enter;
    private ListView listView;
    private String nick;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> arr_roomList = new ArrayList<>();
    AgmPrefer ap;
    private FirebaseAuth mAuth; //선언함.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        ap = new AgmPrefer(ChatMain.this);
        ItemData idata = new ItemData();
        //닉네임
        mAuth = FirebaseAuth.getInstance();//초기화함.
        idata.user_id = mAuth.getCurrentUser().getEmail();
        nick = idata.user_id.split("@")[0];


        chatroom = findViewById(R.id.chatroom_et);
        enter = findViewById(R.id.enter_btn);
        listView = findViewById(R.id.list);

        enter.setOnClickListener(this);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("id_list").child(nick).child("chatroom");

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);


        ChatList();

    }

    private void ChatList() {
        // 리스트 어댑터 생성 및 세팅

        arrayAdapter = new ArrayAdapter<String>(this, R.layout.listview_text, arr_roomList);
        listView.setAdapter(arrayAdapter);

        //리스트뷰가 갱신될 때 하단으로 자동 스크롤
        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        // 데이터 받아오기 및 어댑터 데이터 추가 및 삭제 등..리스너 관리
        database.getReference("id_list").child(nick).child("chatroom").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("chatlist", dataSnapshot.getValue().toString());
                arrayAdapter.add(dataSnapshot.getValue().toString());

                HashSet<String> set = new LinkedHashSet<String>(arr_roomList);
                Iterator i = dataSnapshot.getChildren().iterator();

                while (i.hasNext()) {
                    set.add(((DataSnapshot) i.next()).getKey());
                }

                arr_roomList.clear();
                arr_roomList.addAll(set);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {


            }


            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra("chatName", ((TextView) view).getText().toString());

                startActivity(intent);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ChatMain.this);
                builder.setTitle("스터디룸 목록 삭제");
                builder.setMessage("스터디룸 목록을 삭제 하시겠습니까?");
                builder.setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                //       String key = database.getReference("id_list").child(nick).child("chatroom").getKey();

                                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        arr_roomList.remove(position);
                                        arrayAdapter.notifyDataSetChanged();
                                        Toast.makeText(ChatMain.this, "스터디룸 목록이 삭제되었습니다.", Toast.LENGTH_SHORT).show();

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }
                        });
                builder.setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                builder.show();
                return true;
            }

        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.enter_btn:
                if (chatroom.getText().toString().trim().length() > 0) {

                    String newMessage = chatroom.getText().toString().trim();
                    myRef.push().setValue(newMessage);

                    Toast.makeText(ChatMain.this, "입장", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                    intent.putExtra("chatName", chatroom.getText().toString());
                    startActivity(intent);
                    chatroom.setText(""); //입력필드 초기화
                } else {
                    Toast.makeText(ChatMain.this, "스터디룸 이름을 입력하세요.", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

}