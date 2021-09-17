package com.studyHard.teamnova_android.유튜브API;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.studyHard.teamnova_android.R;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Youtube_Adapter extends RecyclerView.Adapter<Youtube_Adapter.Youtube_ViewHolder> {

    Context context;
    LayoutInflater layoutInflater;
    int layout;

    public ArrayList<Youtube_Data> youtube_list;

    public Youtube_Adapter(Context context, int layout, ArrayList<Youtube_Data> list) {
        this.context = context;
        this.layout = layout;
        this.youtube_list = list;
        // inflate 권한을 생성할 때부터 받음.
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // 뷰홀더 생성자
    public static class Youtube_ViewHolder extends RecyclerView.ViewHolder {
        // 뷰홀더 내에 들어가는 뷰 객체 선언

        TextView title_tx, chTitle_tx,viewCount_tx,time_tx;
        YouTubePlayerView youTubePlayerView;
        YouTubePlayer youTubePlayer;
        String currentVideoId;

        public Youtube_ViewHolder(@NonNull View itemView) {
            super(itemView);
            // 뷰 객체에 레이아웃의 뷰 참조
            title_tx = itemView.findViewById(R.id.youtube_tile);
            chTitle_tx = itemView.findViewById(R.id.youtube_channel_title);
            viewCount_tx = itemView.findViewById(R.id.youtube_count);
            time_tx = itemView.findViewById(R.id.youtube_publishedTime);
            youTubePlayerView = itemView.findViewById(R.id.youtube_player_view);
        }
    }



    // 뷰홀더가 만들어지는 시점에서 호출되는 매소드. (재활용 될 때 생성되는 것이 아님.)
    // 메모리 관리를 강제하는 역할을 수행하기도 함.
    @NonNull
    @Override
    public Youtube_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.youtube_item, parent, false);
//        YouTubePlayerView youTubePlayerView = (YouTubePlayerView) LayoutInflater.from(parent.getContext()).inflate(R.layout.youtube_item, parent, false);
        return new Youtube_ViewHolder(view);
    }


    // 메모리 관리를 위해, 뷰홀더는 재활용하고, 뷰 내부의 데이터만 갈아 끼운다.
    @Override
    public void onBindViewHolder(@NonNull Youtube_ViewHolder holder, int position) {

        Youtube_Data youtube_data = youtube_list.get(position);
        holder.title_tx.setText(youtube_data.getTitle());
        holder.chTitle_tx.setText(youtube_data.getChannelTitle());
        holder.viewCount_tx.setText(youtube_data.getViewCount());
        holder.time_tx.setText(youtube_data.getPublishedAt());

        holder.youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NotNull YouTubePlayer initializedYouTubePlayer) {
                holder.youTubePlayer = initializedYouTubePlayer;
                holder.youTubePlayer.cueVideo(youtube_data.getVideoId(), 0);

            }
        });

    }

    @Override
    public int getItemCount() {
        return youtube_list.size();
    }

    public void addItem(Youtube_Data youtube_data) {
        youtube_list.add(youtube_data);
        notifyDataSetChanged();
    }



}

