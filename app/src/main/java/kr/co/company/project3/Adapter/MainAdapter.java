package kr.co.company.project3.Adapter;
//메인으로 데이터를 받는 부분.

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import kr.co.company.project3.DataClass.ItemData;
import kr.co.company.project3.DetailActivity;
import kr.co.company.project3.R;
import kr.co.company.project3.Util.AgmPrefer;

public class MainAdapter extends RecyclerView.Adapter {

    ArrayList<ItemData> al;
    Context ctx;
    AgmPrefer ap;
    private FirebaseAuth mAuth; //선언함.

    public interface OnContextClick {
        void ContextClick(int menuid, String idx);
    }

    OnContextClick onContextClickListener; //위 글 썼으면 리스너를 만들어줘야한다.

    public void setOnContextClickListener(OnContextClick listener)
    {
        this.onContextClickListener = listener;
    } //리스너 만들고 연결시켜줘야 한다. 이 메소드가 바인딩시켜주는 메소드가 되는 거다.

    public MainAdapter(Context ctx, ArrayList<ItemData> al){
        this.ctx = ctx;
        this.al = al;
        ap = new AgmPrefer(ctx);
    }

    public final static class ListItemViewHolder extends RecyclerView.ViewHolder{
        ImageView itemProfile;
        TextView itemTitle;
        ImageView itemImage;
        TextView itemSummary;
        ImageButton itemHeart;
        TextView itemReply;
        ImageButton itemMore;
        TextView itemCount;
        TextView itemDate;

        public ListItemViewHolder(View itemView)
        {
            super(itemView);

            itemProfile = (ImageView)itemView.findViewById(R.id.itemProfile);
            itemTitle = (TextView)itemView.findViewById(R.id.itemTitle);
            itemImage = (ImageView)itemView.findViewById(R.id.itemImage);
            itemSummary = (TextView)itemView.findViewById(R.id.itemSummary);
//            itemHeart = (ImageButton)itemView.findViewById(R.id.itemHeart);
            itemReply = (TextView)itemView.findViewById(R.id.itemReply);
            itemMore = (ImageButton)itemView.findViewById(R.id.itemMore);
            itemCount = (TextView)itemView.findViewById(R.id.itemCount);
            itemDate = (TextView)itemView.findViewById(R.id.itemDate);
        }
    }

    @NonNull
    @Override
    public ListItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from((viewGroup.getContext())).inflate(R.layout.item_main_row, viewGroup, false);
        return new ListItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ListItemViewHolder iviewholder = (ListItemViewHolder)viewHolder;

        ItemData idata = al.get(i);

        iviewholder.itemTitle.setText(idata.title);
        iviewholder.itemTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(FirebaseAuth.getInstance().getCurrentUser()!=null)
                {
                    //로그인이 되어있으니 상세로 이동
                    ShowDetailActivity(idata.idx);
                }
                else
                {
                    Toast.makeText(ctx, "로그인 해주세요!..", Toast.LENGTH_SHORT).show();
                }
            }
        });
        iviewholder.itemReply.setText(String.valueOf(idata.reply));
        iviewholder.itemCount.setText(String.valueOf(idata.heart) + " Heals");
        iviewholder.itemDate.setText(idata.reg_date);//바인딩

        Glide.with(ctx)
                .load(idata.profile)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.noperson) //프로필이 없을때
                        .centerCrop()
                        .dontTransform())
                .transition(new DrawableTransitionOptions().crossFade(500))
                .into(iviewholder.itemProfile);

        if(!idata.image.isEmpty() && idata.image.split(",").length>0) {
            Glide.with(ctx)
                    .load(idata.image.split(",")[0])
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.kk)
                            .centerCrop()
                            .dontTransform())
                    .transition(new DrawableTransitionOptions().crossFade(500))
                    .into(iviewholder.itemImage);
            iviewholder.itemSummary.setVisibility(View.GONE);
            iviewholder.itemImage.setVisibility(View.VISIBLE);
        }
        else{
            //이미지가 없는 경우에는 글이 보이도록.
            iviewholder.itemSummary.setText(idata.summary);
            iviewholder.itemSummary.setVisibility(View.VISIBLE);
            iviewholder.itemImage.setVisibility(View.GONE);
        }

        //삭제 팝업 contextmenu
        mAuth = FirebaseAuth.getInstance();//초기화함.
        if(mAuth.getCurrentUser()!=null)
        {
            if(idata.user_id.equals(mAuth.getCurrentUser().getEmail())) //if(idata.reg_user.equals(ap.getNickname()))
            {
                iviewholder.itemMore.setVisibility(View.VISIBLE);

                iviewholder.itemMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //클릭을 했을때 여기서 contextmenu를 띄우면 된다.
                        PopupMenu popup = new PopupMenu(ctx, iviewholder.itemMore);
                        popup.inflate(R.menu.list_contextmenu);
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                switch (menuItem.getItemId()) {
                                    case R.id.conItem_del:
                                        //여기서 삭제처리를 해주면 된다.
                                        new AlertDialog.Builder(ctx)
                                                .setMessage("정말 삭제하시겠습니까?")
                                                .setCancelable(false) //이 문장은 팝업창 뜨고 뒤로가기 버튼으로 갈 수 있냐 없냐 해주는 옵션이다.
                                                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        //얘를 클릭했을때 진짜 삭제처리.

                                                        if(onContextClickListener !=null)
                                                        {
                                                            onContextClickListener.ContextClick(R.id.conItem_del, idata.idx); //선언을 했다면 실행을 해주라는 부분이다.(얘를 실행한다는 거다.) 그리고 이 메소드(onContextClickListener)를 받아줄때가 필요해서 메인으로 갔다.
                                                        }

                                                    }
                                                })
                                                .setNegativeButton("아니오", null)
                                                .show();
                                        break;
                                    default:
                                        break;
                                }
                                return false;
                            }
                        });
                        popup.show();
                    }
                });
            }
        }
        else
        {
            iviewholder.itemMore.setVisibility(View.INVISIBLE);
        }

    }

    private void ShowDetailActivity(String board_idx)
    {
        Intent intent=new Intent(ctx, DetailActivity.class);
        intent.putExtra("board_idx", board_idx);
        ctx.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return al.size();
    }
}
