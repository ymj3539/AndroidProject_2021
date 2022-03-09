package kr.co.company.project3;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import kr.co.company.project3.Adapter.MainAdapter;
import kr.co.company.project3.DataClass.ItemData;

public class MainActivity extends LeftMenuActivity {
    MainAdapter adapter;
    FirebaseDatabase database;
    DatabaseReference myRef;
    int index = 0;
    ArrayList<ItemData> al = new ArrayList<ItemData>();
    ImageView toolbar_leftmenu;
    FloatingActionButton btnWrite;



    @SuppressLint("RestrictedApi") //setVisibility때문에 생긴거임.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("board");
        adapter = new MainAdapter(this, al);
        adapter.setOnContextClickListener(new MainAdapter.OnContextClick() {
            @Override
            public void ContextClick(int menuid, String idx)
            {
                myRef.child(idx).removeValue(); //이제 클릭을 하면 이 메소드가 실행이 된다.(즉, 삭제 되는 메소드이다. 삭제가 된다.)
                finish();
                startActivity(getIntent());
            }
        });

        btnWrite = (FloatingActionButton)findViewById(R.id.btnWrite);
        btnWrite.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, WriteActivity.class);
                startActivityForResult(intent, 300);
            }
        });

        toolbar_leftmenu = (ImageView)findViewById(R.id.toolbar_leftmenu);
        toolbar_leftmenu.setVisibility(View.VISIBLE);
        toolbar_leftmenu.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openDrawer();
            }
        });

        RecyclerView mainScrollView = (RecyclerView)findViewById(R.id.mainScrollView);
        mainScrollView.setAdapter(adapter);
        mainScrollView.setLayoutManager(new LinearLayoutManager(this));

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                for(DataSnapshot oneSnap : snapshot.getChildren())
                {
                    ItemData idata = oneSnap.getValue(ItemData.class);
                    al.add(0,idata); //그냥 add를 해주면 글이 밑으로 쌓인다. but 위로 쌓기는 이렇게.
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }

        });

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {
                for(ItemData onedata : al)
                {
                    ItemData temp_data = snapshot.getValue(ItemData.class);

                    if(onedata.idx.equals(temp_data.idx))
                    {
                        onedata.title = temp_data.title;
                        onedata.image = temp_data.image;
                        onedata.reg_user = temp_data.reg_user;
                        onedata.reg_date = temp_data.reg_date;
                        onedata.count = temp_data.count;
                        onedata.reply = temp_data.reply;
                        onedata.more = temp_data.more;
                        onedata.profile = temp_data.profile;
                        onedata.heart = temp_data.heart;
                        onedata.summary = temp_data.summary;
                    }
                }
                adapter.notifyDataSetChanged();
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

        this.setOnLogEVentListener(new OnLogEventListener() {
            @Override
            public void Fire() { //여기서 Fire()가 실행 된다.
                CheckWriteButton();
            }
        });

        CheckWriteButton();


    }


    @SuppressLint("RestrictedApi")
    private void CheckWriteButton() {
        if(firebaseAuth.getCurrentUser()!=null)
        {
            btnWrite.setVisibility(View.VISIBLE); //로그인이 되어있으면 버튼을 visible.
        }
        else
        {
            btnWrite.setVisibility(View.INVISIBLE);
        }
    }

//    private void Writemessage(String index, ItemData idata)
//    {
//        myRef.child(index).setValue(idata);
//    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data); //data-intent로

        if(requestCode == 300)
        {
            finish();
            startActivity(getIntent()); //간단한 새로고침.
        }
    }

}