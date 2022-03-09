package kr.co.company.project3;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import kr.co.company.project3.Adapter.ReplyAdapter;
import kr.co.company.project3.DataClass.ItemData;
import kr.co.company.project3.DataClass.ReplyData;
import kr.co.company.project3.Util.AgmPrefer;

public class DetailActivity extends AppCompatActivity {

    String board_idx;
    FirebaseDatabase database;
    DatabaseReference myRef;
    AgmPrefer ap;


    //내용 부분
    TextView tvTitle;
    TextView tvRegDateWriter;
    TextView tvRegDateWriter2;
    TextView tvContent;
    LinearLayout IncreaseHeal;
    TextView itemCount;
    ImageView tvImage;
    TextView itemSummary;

    //댓글 부분
    EditText etMsg;
    ListView lvReply;
    ImageButton btnSend;
    ReplyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ap = new AgmPrefer(DetailActivity.this);

        board_idx = getIntent().getStringExtra("board_idx"); //board_idx로 받는다.
        database = FirebaseDatabase.getInstance(); //데이터베이스를 초기화 하고
        myRef = database.getReference(); //데이터베이스 부분을 레퍼런스(myRef) 했다.



        tvTitle=(TextView)findViewById(R.id.tvTitle);
        tvRegDateWriter=(TextView)findViewById(R.id.tvRegDateWriter);
        tvRegDateWriter2=(TextView)findViewById(R.id.tvRegDateWriter2);
        tvContent=(TextView)findViewById(R.id.tvContent);
        IncreaseHeal = (LinearLayout)findViewById(R.id.IncreaseHeal);
        itemCount = (TextView)findViewById(R.id.itemCount);
        tvImage=(ImageView)findViewById(R.id.tvImage);
        itemSummary = (TextView)findViewById(R.id.itemSummary);

        IncreaseHeal.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //여기서 힐증가.
                myRef.child("board").child(board_idx).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot2) {
                        if(snapshot2.hasChildren())
                        {
                            int heart = snapshot2.getValue(ItemData.class).heart;
                            myRef.child("board").child(board_idx).child("heart").setValue(heart +1);
                            itemCount.setText(heart + 1 + "Heals");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        setDetailListen();

        //댓글 부분
        etMsg =(EditText)findViewById(R.id.etMsg);
        lvReply = (ListView)findViewById(R.id.lvReply);
        btnSend = (ImageButton)findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                //대화 보내는 자리
                ReplyData newData = new ReplyData();

                String write_time = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
                String reg_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date());
                newData.idx = write_time;
                newData.reg_user_profile = ap.getProfileImage();
                newData.reg_user_nickname = ap.getNickname();
                newData.content = etMsg.getText().toString();
                newData.reg_date = reg_date;

                myRef.child("reply").child(board_idx).push().setValue(newData);
                etMsg.setText("");

                myRef.child("board").child(board_idx).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot2) {
                        if(snapshot2.hasChildren())
                        {
                            int reply = snapshot2.getValue(ItemData.class).reply;
                            myRef.child("board").child(board_idx).child("reply").setValue(reply +1);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        adapter = new ReplyAdapter(DetailActivity.this, new ArrayList<ReplyData>());
        lvReply.setAdapter(adapter); //이렇게 하면 바인딩이 되었다.
        setReplyListen();
    }

    private void setReplyListen()
    {
        myRef.child("reply").child(board_idx).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.hasChildren())
                {
                    ReplyData newData = new ReplyData();
                    ReplyData rcvData = snapshot.getValue(ReplyData.class);

                    newData.reg_user_nickname = rcvData.reg_user_nickname;
                    newData.reg_user_profile = rcvData.reg_user_profile;
                    newData.content = rcvData.content;
                    newData.reg_date = rcvData.reg_date;
                    newData.isMe = rcvData.reg_user_nickname.equals(ap.getNickname());

                    adapter.add(newData);
                    adapter.notifyDataSetChanged();
                    lvReply.setSelection(lvReply.getCount()-1); //스크롤을 항상 아래로.


                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setDetailListen()
    {
        myRef.child("board").orderByChild("idx").equalTo(board_idx).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot oneSnap:snapshot.getChildren())
                {
                    ItemData idata=oneSnap.getValue(ItemData.class);

                    tvTitle.setText(idata.title);
                    tvContent.setText(idata.summary);
                    tvRegDateWriter.setText(idata.reg_user);
                    tvRegDateWriter2.setText(idata.reg_date);
                    itemCount.setText(idata.heart + "Heals");
                    if(!idata.image.isEmpty() && idata.image.split(",").length>0) {
                        Glide.with(getBaseContext())
                                .load(idata.image)
                                .apply(new RequestOptions()
                                        //.centerCrop()
                                        .dontTransform())
                                //.transition(new DrawableTransitionOptions().crossFade(1000))
                                .into(tvImage);
                        tvImage.setVisibility(View.GONE);
                        tvImage.setVisibility(View.VISIBLE);
                    }
                    else{
                        //이미지가 없는 경우에는 글이 보이도록.
                        tvImage.setVisibility(View.VISIBLE);
                        tvImage.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}