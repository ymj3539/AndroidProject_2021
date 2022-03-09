//package kr.co.company.project3.Adapter;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.annotation.GlideModule;
//import com.bumptech.glide.module.AppGlideModule;
//
//import java.util.ArrayList;
//
//import kr.co.company.project3.DataClass.ItemObject;
//import kr.co.company.project3.R;
//
//
//public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
//
//    //데이터 배열 선언
//    private ArrayList<ItemObject> mList;
//
//    public  class ViewHolder extends RecyclerView.ViewHolder {
//        private ImageView imageView_img;
//        private TextView textView_title, textView_release, texView_director;
//
//        public ViewHolder(View itemView) {
//            super(itemView);
//
//            imageView_img = (ImageView) itemView.findViewById(R.id.imageView_img);
//            textView_title = (TextView) itemView.findViewById(R.id.textView_title);
//            textView_release = (TextView) itemView.findViewById(R.id.textView_release);
//            texView_director = (TextView) itemView.findViewById(R.id.textView_director);
//        }
//    }
//
//    //생성자
//    public MyAdapter(ArrayList<ItemObject> list) {
//        this.mList = list;
//    }
//
//    @NonNull
//    @Override
//    public MyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.crawing, parent, false);
//        return new ViewHolder(view);
//    }
//
//
//    @Override
//    public void onBindViewHolder(@NonNull MyAdapter.ViewHolder holder, int position) {
//
//        holder.textView_title.setText(String.valueOf(mList.get(position).getTitle()));
//        holder.textView_release.setText(String.valueOf(mList.get(position).getRelease()));
//        holder.texView_director.setText(String.valueOf(mList.get(position).getDirector()));
//        //다 해줬는데도 GlideApp 에러가 나면 rebuild project를 해주자.
//
//        GlideApp.with(holder.itemView).load(mList.get(position).getImg_url())
//                .override(300,400)
//                .into(holder.imageView_img);
//    }
//
//    @Override
//    public int getItemCount() {
//        return mList.size();
//    }
//}
//
