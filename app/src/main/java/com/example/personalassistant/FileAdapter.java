package com.example.personalassistant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> {
    private Context context;
    private List<FileModel> fileList;

    public FileAdapter(Context context, List<FileModel> fileList) {
        this.context = context;
        this.fileList = fileList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_file, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FileModel file = fileList.get(position);
        holder.fileName.setText(file.getFileName());

        // Load image using Glide
        Glide.with(holder.itemView.getContext())
                .load(file.getFileUrl())
                .placeholder(R.drawable.baseline_downloading_24)  // Show loading icon
                .error(R.drawable.file_icon)           // Show default icon if failed
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache images
                .into(holder.fileImage);
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView fileName;
        ImageView fileImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fileName = itemView.findViewById(R.id.fileName);
            fileImage = itemView.findViewById(R.id.fileIcon);
        }
    }
}
