package com.studyHard.teamnova_android;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Todo_Adapter extends RecyclerView.Adapter<Todo_Adapter.Todo_ViewHolder> {
    Context context;
    LayoutInflater layoutInflater;
    int layout;
    preferenceManager_ToDo pref = new preferenceManager_ToDo();

    public static ArrayList<Todo_Data> todo_data = new ArrayList<>();

    public Todo_Adapter(Context context, int layout) {
        this.context = context;
        this.layout = layout;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public class Todo_ViewHolder extends RecyclerView.ViewHolder {
        CheckBox todoBox;
        TextView DeleteTodo;
        TextView ModifyTodo;
        TextView todoDate;
        TextView todoEditDate;

        public Todo_ViewHolder(@NonNull View itemView) {
            super(itemView);
            todoBox = itemView.findViewById(R.id.Todo_checkBox);
            DeleteTodo = itemView.findViewById(R.id.todo_delete);
            ModifyTodo= itemView.findViewById(R.id.todo_modify);
            todoDate = itemView.findViewById(R.id.todo_date);
            todoEditDate = itemView.findViewById(R.id.edit_date);
        }
    }

    @NonNull
    @Override
    public Todo_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.todo_task_layout,parent,false);
        return new Todo_ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull Todo_ViewHolder holder, int position) {

        // 데이터의 리스트에서 아이템을 뽑아 냄.
        Todo_Data item = todo_data.get(position);
        holder.todoBox.setText(item.getTodo_content());
        holder.todoDate.setText(item.getDate());

        String key = holder.todoDate.getText().toString();

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat simpleDate = new SimpleDateFormat("MM-dd HH:mm");
        String getTime = simpleDate.format(date);

        String memo_date = item.getDate();
        String reform_date = "21-" + memo_date.substring(6, 11);

        if(getTime.substring(0,5).equals(memo_date.substring(6, 11))) {
            holder.todoEditDate.setText(memo_date.substring(12, 17));
        } else {
            holder.todoEditDate.setText(reform_date);
        }






        holder.DeleteTodo.setTag(position);
        holder.ModifyTodo.setTag(position);

        holder.todoBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.todoBox.isChecked()) {
                    holder.todoBox.setTextColor(0xFFCECECE);

                } else {
                    holder.todoBox.setTextColor(Color.BLACK);

                }

            }
        });

        holder.ModifyTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();

                Todo_Data data;
                data = todo_data.get(position);
                String todo_content = data.getTodo_content();

                // 현재 입력값과 키값을 수정 다이얼로그로 보내줌.
                todo_modify_dialog dialog = new todo_modify_dialog();
                Bundle bundle = new Bundle();
                bundle.putString("key", key);
                bundle.putInt("position", position);
                bundle.putString("todo_content",todo_content);
                dialog.setArguments(bundle);
                dialog.show(((FragmentActivity)context).getSupportFragmentManager(), "todo_modify_dialog");
            }
        });


        holder.DeleteTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());

                builder.setTitle("삭제하시겠어요?");
                builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        int itemPosition = (int) v.getTag();
                        todo_data.remove(itemPosition);
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
        return todo_data.size();
    }

    public void addItem(Todo_Data item){
        todo_data.add(item);
        notifyDataSetChanged();
    }

    public void modifyItem(Todo_Data item) {

        todo_data.add(item);
        notifyDataSetChanged();

    }





}
