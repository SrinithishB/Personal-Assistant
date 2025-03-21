package com.example.personalassistant;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RoomsRecyclerListAdapter extends RecyclerView.Adapter<RoomsRecyclerListAdapter.ViewHolder> {
    public List<RoomData> roomDataList;
    public Context context;
    RoomsRecyclerListAdapter(Context context,List<RoomData> roomDataList){
        this.context=context;
        this.roomDataList=roomDataList;
    }
    @NonNull
    @Override
    public RoomsRecyclerListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.rooms_list_recycler_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RoomsRecyclerListAdapter.ViewHolder holder, int position) {
        RoomData roomData=roomDataList.get(position);
        holder.roomName.setText(roomData.getRoom_name());
        holder.createdAndNoOfMembers.setText(roomData.getCreated_by());
        switch (roomData.getImageview_id()){
            case 1:
                holder.imageView.setBackgroundResource(R.drawable.vector1);
                break;
            case 2:
                holder.imageView.setBackgroundResource(R.drawable.vector2);
                break;
            case 3:
                holder.imageView.setBackgroundResource(R.drawable.vector3);
                break;
            case 4:
                holder.imageView.setBackgroundResource(R.drawable.vector4);
//                holder.imageView.setImageResource(R.drawable.food_header);
                break;
            case 5:
                holder.imageView.setBackgroundResource(R.drawable.vector5);
                break;
        }
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent=new Intent(context,RoomActivity.class);
                    intent.putExtra("room_code",roomData.getRoom_code());
                    intent.putExtra("room_id",roomData.getRoom_id());
                    context.startActivity(intent);
                }catch (Exception e){
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return roomDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView roomName,createdAndNoOfMembers;
        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView=itemView.findViewById(R.id.cardView);
            roomName=itemView.findViewById(R.id.RoomName);
            imageView=itemView.findViewById(R.id.headerImageView);
            createdAndNoOfMembers=itemView.findViewById(R.id.createdName_and_noOfMembers);
        }
    }
}
