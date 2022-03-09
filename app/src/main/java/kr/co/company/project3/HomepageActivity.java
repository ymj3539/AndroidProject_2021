package kr.co.company.project3;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import kr.co.company.project3.Adapter.RecyclerAdapter;

public class HomepageActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerAdapter adapter;
    String melon_chart_url = "http://www.skhu.ac.kr/board/boardlist.aspx?bsid=10004&searchBun=51";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        recyclerView = findViewById(R.id.mainScrollView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new RecyclerAdapter();
        recyclerView.setAdapter(adapter);

        getData();

    }

    public void myListener(View target) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.skhu.ac.kr/board/boardlist.aspx?bsid=10004&searchBun=51"));
        startActivity(intent);
    }



    private void getData(){
        MelonJsoup jsoupAsyncTask = new MelonJsoup();
        jsoupAsyncTask.execute();
    }

    private class MelonJsoup extends AsyncTask<Void, Void, Void> {
        ArrayList<String> listTitle = new ArrayList<>();
        ArrayList<String> listName = new ArrayList<>();
        ArrayList<String> listLink = new ArrayList<>();

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Document doc = Jsoup.connect(melon_chart_url).get();
                final Elements rank_list1 = doc.select("td.left15");
                final Elements rank_list_name = doc.select("td");
                final Elements rank_list_link = doc.select("td.left15 a");


                Handler handler = new Handler(Looper.getMainLooper()); // 객체생성
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //회사정보
                        for(Element element: rank_list1) {
                            listTitle.add(element.text());
                        }
                        //채용제목
                        for (Element element : rank_list_name) {
                            listName.add(element.text());
                        }


                        for (int i = 0; i < 18 ; i++) {
                            ChartDTO data = new ChartDTO();
                            data.setTitle(listTitle.get(i));
                            data.setName(listName.get((i*6)+4));


                            adapter.addItem(data);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });


            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;


        }
    }

}