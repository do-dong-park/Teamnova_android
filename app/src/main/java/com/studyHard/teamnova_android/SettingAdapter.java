package com.studyHard.teamnova_android;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class SettingAdapter extends BaseAdapter {

    //아이템 리스트를 선언한다. 클래스 내부에서 계속 사용되야 함으로 여기서 선언
    public ArrayList<SettingItem> settingItemList = new ArrayList<>();


    //생성자
    public SettingAdapter() {}

    @Override
    public int getCount() {
        return settingItemList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public SettingItem getItem(int position) {
        return settingItemList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();

        // listview item의 레이아웃을 객체화 시킴 (화면을 메모리에 올려줌)
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.setting_listview_item, parent, false);
        }

        // 화면에 표시될 View로부터 위젯에 대한 참조 획득
        // view와 코드를 연결해 주는 역할.
        ImageView iconImageView = convertView.findViewById(R.id.settingIcon);
        TextView titleTextView = convertView.findViewById(R.id.settingTitle);
        TextView descTextView = convertView.findViewById(R.id.settingDesc);

        // SettingItem으로 구성된 리스트(Data set)에서 position에 대한 데이터 참조 획득
        // 각 view (아이콘,제목,설명)에 위치값을 연결해준다고 생각
        SettingItem settingItem = settingItemList.get(position);

        iconImageView.setImageDrawable(settingItem.getIcon());
        titleTextView.setText(settingItem.getTitle());
        descTextView.setText(settingItem.getDesc());

        return convertView;
    }

    //List View에 item 데이터 (아이콘, 제목, 텍스트) 추가
    public void addItem(Drawable icon, String title, String desc) {
        SettingItem item = new SettingItem();
        item.setIcon(icon);
        item.setTitle(title);
        item.setDesc(desc);
        settingItemList.add(item);

    }
}
