package com.studyHard.teamnova_android;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Timer_Record_Adapter extends RecyclerView.Adapter<Timer_Record_Adapter.Timer_Record_ViewHolder> {

    Context context;
    LayoutInflater layoutInflater;
    int layout;
    String studyDuration, studyTime, getTime;
    long start_time, end_time;
    preferenceManager_TimeRecord pref = new preferenceManager_TimeRecord();

    public ArrayList<Timer_Data> timer_record_data = new ArrayList<>();

    public Timer_Record_Adapter(Context context, int layout) {
        this.context = context;
        this.layout = layout;
        // inflate 권한을 생성할 때부터 받음.
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public class Timer_Record_ViewHolder extends RecyclerView.ViewHolder {
        TextView study_duration, study_time, study_concent, record_date;
        ImageView imageView;

        public Timer_Record_ViewHolder(@NonNull View itemView) {
            super(itemView);
            study_duration = itemView.findViewById(R.id.time_duration);
            study_time = itemView.findViewById(R.id.time_study);
            study_concent = itemView.findViewById(R.id.concentrateRate);
            record_date = itemView.findViewById(R.id.Record_date);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }

    @NonNull
    @Override
    public Timer_Record_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.timer_record_list_item, parent, false);
        return new Timer_Record_ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Timer_Record_ViewHolder holder, int position) {
        int pos = position;
        Timer_Data timer_data = timer_record_data.get(position);

        refactoringTime(timer_data, pos);

        holder.record_date.setText(timer_data.getDate());

        holder.study_duration.setText(studyDuration);

        holder.study_time.setText(studyTime);

        holder.study_concent.setText(timer_data.getRate());

        holder.itemView.setTag(position);

        String key = holder.record_date.getText().toString();

        String value = holder.study_concent.getText().toString();




        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int position = (int) v.getTag();

                AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                builder.setItems(R.array.record_action_select, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            studyStartTime_modify_dialog startDialog = new studyStartTime_modify_dialog();
                            Bundle bundle = new Bundle();
                            bundle.putInt("position", pos);
                            bundle.putString("key", key);
                            bundle.putString("rate", value);
                            startDialog.setArguments(bundle);
                            startDialog.show(((FragmentActivity) context).getSupportFragmentManager(), "modify_startTime_dialog");

                        } else if (which == 1) {
                            timer_modify_ConcenRate_dialog modify_dialog = new timer_modify_ConcenRate_dialog();
                            Bundle bundle = new Bundle();
                            bundle.putInt("position", pos);
                            bundle.putString("key", key);
                            bundle.putLong("startTime", start_time);
                            bundle.putLong("endTime", end_time);
                            bundle.putString("studyTime", getTime);
                            modify_dialog.setArguments(bundle);
                            modify_dialog.show(((FragmentActivity) context).getSupportFragmentManager(), "modify_rate_dialog");
                        } else {
                            AlertDialog.Builder delete_builder = new AlertDialog.Builder(holder.itemView.getContext());

                            delete_builder.setTitle("삭제하시겠어요?");
                            delete_builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int item) {
                                    timer_record_data.remove(position);
                                    pref.removeKey(context, key);
                                    notifyDataSetChanged();
                                    Intent intent = new Intent(context, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    context.startActivity(intent);
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
                return false;
            }

        });

    }

    @Override
    public int getItemCount() {
        return timer_record_data.size();
    }

    public void addItem(Timer_Data item) {
        timer_record_data.add(item);
        notifyDataSetChanged();
    }

    public void modifyItem(int position, Timer_Data item) {
        timer_record_data.set(position, item);
        notifyDataSetChanged();
    }


    public void refactoringTime(Timer_Data item, int position) {
        int hour, minute, second;

        Timer_Data timer_data = timer_record_data.get(position);

        start_time = timer_data.getStart_time();
        end_time = timer_data.getEnd_time();
        String getStudyTime = timer_data.getStudyTime();


        String[] array = getStudyTime.split(":");

        hour = Integer.parseInt(array[0]);
        minute = Integer.parseInt(array[1]);
        second = Integer.parseInt(array[2]);

        studyTime = String.format(Locale.getDefault(), "%dh %dm %ds", hour, minute, second);
        getTime = String.format(Locale.getDefault(), "%02d:%02d:%02d", hour, minute, second);

        Date startTime = new Date(timer_data.getStart_time());
        Date endTime = new Date(timer_data.getEnd_time());

        SimpleDateFormat simpleDate = new SimpleDateFormat("HH:mm", Locale.KOREA);
        String getStartTime = simpleDate.format(startTime);
        String getEndTime = simpleDate.format(endTime);

        studyDuration = String.format(Locale.getDefault(), "%s ~ %s", getStartTime, getEndTime);


    }


}
