package kr.co.company.project3.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import kr.co.company.project3.R;
import kr.co.otilla.agmeditlist.ContentData;
import kr.co.otilla.agmeditlist.EditAdapter;
import kr.co.otilla.agmeditlist.EditLayout;
import kr.co.otilla.agmeditlist.EditViewHolder;

public class WriteAdapter extends EditAdapter {


    public WriteAdapter(Context context, ArrayList<ContentData> list){
        super(context, list);
    }

    @Override
    public EditViewHolder onCreateEditViewHolder(ViewGroup parent, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_write, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindEditViewHolder(EditViewHolder editViewHolder, int i) {
        //((ViewHolder) editViewHolder).cdata(mList.get(i));

        TextView tvName = (TextView) editViewHolder.vContent;
        ImageView ivImage = (ImageView) editViewHolder.vImage;

        ContentData cdata = (ContentData)mList.get(i);

        //tvName = (TextView) findViewById(R.id.imageView01);
        //Bitmap tvName = BitmapFactory.decodeFile(cdata.FileName);
        //TextView tvName = (TextView)findViewById(R.id.tvName);
        //(EditLayout)TextView.findViewById(R.id.edit_layout);
        //FileName = BitmapFactory.decodeStream(is);

        tvName.setText(cdata.FileName);
        ivImage.setImageBitmap(cdata.Bmp);

    }
    private static class ViewHolder extends EditViewHolder{
        ViewHolder(View itemView){super(itemView);}

        @Override
        public EditLayout setEditLayout(View itemView){
            return (EditLayout)itemView.findViewById(R.id.edit_layout);
        }
        @Override
        public View setContent(View itemView){
            return itemView.findViewById(R.id.tv_name);}

        @Override
        public View setImage(View view) {
            return itemView.findViewById(R.id.ivImage);
        }

        @Override
        public View setPreDelete(View itemView){
            return itemView.findViewById(R.id.fl_pre_delete);
        }
        @Override
        public View setDelete(View itemView){return itemView.findViewById(R.id.fl_delete);}

        @Override
        public View setSort(View itemView){return itemView.findViewById(R.id.fl_sort);}



        public void cdata(ContentData contentData) {

        }
    }
}