package com.studyHard.teamnova_android;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class timer_preset_adapter extends RecyclerView.Adapter<timer_preset_adapter.TimerViewHolder> {
    private static final String TAG = "timer_preset_adapter";
    Context context;
    preferenceManager_timer_PreSet pref = new preferenceManager_timer_PreSet();

    //로컬 데이터 셋.
    private ArrayList<timer_preset_data> timerData;

    // 어뎁터 생성자. 입력된 데이터 셋이 어뎁터가 사용할 놈
    public timer_preset_adapter(Context context, ArrayList<timer_preset_data> timerData) {
        this.timerData = timerData;
        this.context = context;
    }

    // 뷰홀더 내부클래스 -> 뷰홀더 안에 들어갈 뷰들 참조
    public class TimerViewHolder extends RecyclerView.ViewHolder {
        TextView TextView_itemView_title;
        TextView TextView_itemView_time;
        TextView timerKey;
        ImageButton ImageButton_itemView;

        public TimerViewHolder(@NonNull View itemView) {
            super(itemView);
            TextView_itemView_title = itemView.findViewById(R.id.timer_title);
            TextView_itemView_time = itemView.findViewById(R.id.timer_timeset);
            ImageButton_itemView = itemView.findViewById(R.id.imageButton);
            timerKey = itemView.findViewById(R.id.preset_date);
        }

    }

    //layout manager에 의해 실행, 뷰홀더 생성될 때 xml 메모리 위에 올리기.
    @NonNull
    @Override
    public TimerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.timer_item_layout, parent, false);
        return new TimerViewHolder(view);
    }


    /*onbindviewholder 란 ListView / RecyclerView 는 inflate를 최소화 하기 위해서 뷰를 재활용 하는데,

    이 때 각 뷰의 내용을 업데이트 하기 위해 findViewById 를 매번 호출 해야합니다.

    이로 인해 성능저하가 일어남에 따라 ItemView의 각 요소를 바로 엑세스 할 수 있도록 저장해두고 사용하기 위한 객체
    뷰홀더는 재활용하면서, 내용물(데이터)만 교체함.*/
    @Override
    public void onBindViewHolder(@NonNull TimerViewHolder holder, final int position) {

        int pos = position;
        timer_preset_data data;
        data = timerData.get(position);

        holder.TextView_itemView_title.setText(data.getTitle());
        holder.TextView_itemView_time.setText(data.getTimeSet());
        holder.timerKey.setText(data.getDate());

        String timeSet = data.getTimeSet();
        String title = data.getTitle();
        String key = holder.timerKey.getText().toString();

//        // 각 뷰에 들어갈 데이터가 리스트의 몇번째 위치인지 알려줌. + 위치에 맞는 데이터 넣어줌.
//        holder.onBind(timerData.get(position));

        // 이미지 버튼에 tag 붙이기
        holder.ImageButton_itemView.setTag(position);
        holder.itemView.setTag(position);

        //이미지 버튼이 눌리면, 테그의 포지션에 있는 데이터를 타이머 레이아웃으로 넘긴다.
        holder.ImageButton_itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int btnPosition = (int) v.getTag();
                if (btnPosition != RecyclerView.NO_POSITION) {
                    timer_preset_data test;

                    test = timerData.get(btnPosition);

                    String timeSet = test.getTimeSet();

                    Intent intent = new Intent(v.getContext(), timer_layout.class);

                    intent.putExtra("timeSet", timeSet);

                    v.getContext().startActivity(intent);
                }

            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {


                AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());

                builder.setItems(R.array.preset_action_select, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //수정
                        if (which == 0) {
                            timer_set_modify_dialog modify_set_dialog = new timer_set_modify_dialog();

                            Bundle bundle = new Bundle();
                            bundle.putString("key", key);
                            bundle.putInt("position", pos);
                            bundle.putString("title", title);
                            bundle.putString("timeSet", timeSet);

                            modify_set_dialog.setArguments(bundle);
                            modify_set_dialog.show(((FragmentActivity) context).getSupportFragmentManager(), "modify_set_dialog");

                            // 삭제
                        } else {
                            AlertDialog.Builder delete_builder = new AlertDialog.Builder(holder.itemView.getContext());

                            delete_builder.setTitle("삭제하시겠어요?");
                            delete_builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int item) {
                                    int itemPosition = (int) v.getTag();
                                    timerData.remove(itemPosition);
                                    pref.removeKey(context, key);
                                    notifyDataSetChanged();
                                    dialog.dismiss();
                                }
                            });
                            delete_builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    notifyDataSetChanged();
                                    dialog.dismiss();
                                }
                            });
                            delete_builder.show();
                        }
                    }
                });


                builder.show();
                return true;
            }
        });

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return timerData.size();
    }

    public void addItem(timer_preset_data item) {
        timerData.add(item);
        notifyDataSetChanged();
    }

    public void modifyItem(int position, timer_preset_data item) {

        timerData.set(position, item);
        notifyDataSetChanged();

    }
}
