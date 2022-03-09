package kr.co.company.project3.Chatting;

import android.content.Context;

import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import kr.co.company.project3.R;

/**
 * Created by KPlo on 2018. 10. 28..
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {
    private List<ChatData> mDataset;
    private String myNickName;
    private FirebaseAuth mAuth; //선언함.

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView TextView_nickname;
        public TextView TextView_msg;
        public View rootView;
        public LinearLayout msgLinear;
        public TextView time;

        public MyViewHolder(View v) {
            super(v);
            TextView_nickname = v.findViewById(R.id.TextView_nickname);
            TextView_msg = v.findViewById(R.id.TextView_msg);
            time = v.findViewById(R.id.time);
            msgLinear = itemView.findViewById(R.id.msgLinear);
            rootView = v;

        }


    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ChatAdapter(List<ChatData> myDataset, Context context, String myNickName) {
        //{"1","2"}
        mDataset = myDataset;
        this.myNickName = myNickName;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ChatAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        // create a new view
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_chat, parent, false);

        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        ChatData chat = mDataset.get(position);

        holder.TextView_nickname.setText(chat.getNickname());
        holder.TextView_msg.setText(chat.getMsg());
        holder.time.setText(chat.getTime());

        mAuth = FirebaseAuth.getInstance();//초기화함.
        if(chat.getUser_id().equals(mAuth.getCurrentUser().getEmail())) {
            holder.TextView_msg.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
            holder.TextView_nickname.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
            holder.msgLinear.setGravity(Gravity.RIGHT);
        }
        else {
            holder.TextView_msg.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            holder.TextView_nickname.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            holder.msgLinear.setGravity(Gravity.LEFT);
        }

    }

    @Override
    public int getItemCount() {

        //삼항 연산자
        return mDataset == null ? 0 :  mDataset.size();
    }

    public ChatData getChat(int position) {
        return mDataset != null ? mDataset.get(position) : null;
    }

    public void addChat(ChatData chat) {
        mDataset.add(chat);
        notifyItemInserted(mDataset.size()-1); //갱신
    }

}