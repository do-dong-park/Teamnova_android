package com.studyHard.teamnova_android;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Memo_Adapter extends RecyclerView.Adapter<Memo_Adapter.Memo_ViewHolder> {

    //생성자에 들어갈 변수 미리 선언.
    Context context;
    LayoutInflater inflater;
    int layout;
    preferenceManager_MEMO pref = new preferenceManager_MEMO();
    boolean memo_type_add, memo_type_modify = false;

    //로컬 데이터 객체 선언
    public static ArrayList<Memo_Data> memo_data = new ArrayList<>();

    // 어뎁터 생성자. - context, layout 선언
    public Memo_Adapter(Context context, int layout) {
        this.context = context;
        this.layout = layout;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // 내부클래스 ,뷰홀더 선언
    public class Memo_ViewHolder extends RecyclerView.ViewHolder {
        TextView memo_type;
        TextView memo_content;
        TextView memo_delete;
        TextView memo_modify;
        TextView memo_date;
        TextView memo_title;
        ImageView photo_image;

        public Memo_ViewHolder(@NonNull View itemView) {
            super(itemView);
            memo_type = itemView.findViewById(R.id.memo_type);
            memo_title = itemView.findViewById(R.id.memo_title);
            memo_content = itemView.findViewById(R.id.note_text);
            memo_delete = itemView.findViewById(R.id.note_delete);
            memo_modify = itemView.findViewById(R.id.note_modify);
            memo_date = itemView.findViewById(R.id.memo_date);
            photo_image = itemView.findViewById(R.id.imageView);
        }
    }

    // 뷰홀더 생성 (xml을 컨텍스트가 메모리에 올려줌)
    @NonNull
    @Override
    public Memo_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.memo_layout, parent, false);
        return new Memo_ViewHolder(view);
    }

    // 데이터 셋에서 뷰에게 위치에 맞는 데이터를 뿌려줌.
    @Override
    public void onBindViewHolder(@NonNull Memo_ViewHolder holder, int position) {

        int pos = position;

        Memo_Data data = memo_data.get(position);

        Uri image = data.getImage();

        holder.memo_date.setText(data.getDate());
        holder.memo_title.setText(data.getMemo_title());
        holder.memo_content.setText(data.getMemo_content());
        Glide.with(context).load(image).override(300, 400).into(holder.photo_image);

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat simpleDate = new SimpleDateFormat("MM-dd HH:mm");
        String getTime = simpleDate.format(date);

        String memo_date = data.getDate();
        String reform_date = "21-" + memo_date.substring(6, 11); // 21-05-16

        if(getTime.substring(0,5).equals(memo_date.substring(6, 11))) {
            holder.memo_type.setText(memo_date.substring(12, 17));
        } else {
            holder.memo_type.setText(reform_date);
        }


        String key = holder.memo_date.getText().toString();

        holder.memo_delete.setTag(position);
        holder.memo_modify.setTag(position);

        holder.photo_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memo_addPhoto_dialog dialog = new memo_addPhoto_dialog();
                Bundle bundle = new Bundle();
                bundle.putParcelable("image", image);
                dialog.setArguments(bundle);
                dialog.show(((FragmentActivity) context).getSupportFragmentManager(), "memo_photo_dialog");
            }
        });

        holder.memo_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 수정 dialog 로 이동하기!
                int itemPosition = (int) v.getTag();

                String memo_content = data.getMemo_content();
                String memo_title = data.getMemo_title();

                memo_modify_dialog dialog = new memo_modify_dialog();
                Bundle bundle = new Bundle();
                bundle.putParcelable("image", image);
                bundle.putString("key", key);
                bundle.putInt("position", pos);
                bundle.putString("memo_title", memo_title);
                bundle.putString("memo_content", memo_content);
                dialog.setArguments(bundle);
                dialog.show(((FragmentActivity) context).getSupportFragmentManager(), "memo_modify_dialog");
            }
        });

        holder.memo_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());

                builder.setTitle("삭제하시겠어요?");
                builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        int itemPosition = (int) v.getTag();
                        memo_data.remove(itemPosition);
                        pref.removeKey(context, key);
                        notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return memo_data.size();
    }

    // activity에서 호출되는 애
    public void addItem(Memo_Data item) {
        memo_type_add = true;
        memo_data.add(item);
        notifyDataSetChanged();
    }

    public void modifyItem(Memo_Data item) {
        memo_type_modify = true;
        memo_data.add(item);
        notifyDataSetChanged();
    }


}
