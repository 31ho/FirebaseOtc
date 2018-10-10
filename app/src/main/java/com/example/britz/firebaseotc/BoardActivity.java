package com.example.britz.firebaseotc;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BoardActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<ImageDTO> imageDTOS = new ArrayList<>();
    private List<String> uidLists = new ArrayList<>();
    private FirebaseDatabase firebaseDatabase;
    private BoardRecyclerViewAdapter boardRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        firebaseDatabase = FirebaseDatabase.getInstance();
        recyclerView = (RecyclerView) findViewById(R.id.borad_activity_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        boardRecyclerViewAdapter = new BoardRecyclerViewAdapter();
        recyclerView.setAdapter(boardRecyclerViewAdapter);

        firebaseDatabase.getReference().child("images").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                imageDTOS.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ImageDTO imageDTO = snapshot.getValue(ImageDTO.class);
                    imageDTOS.add(imageDTO);
                }
                boardRecyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    class  BoardRecyclerViewAdapter extends  RecyclerView.Adapter<RecyclerView.ViewHolder>{

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.board_item,viewGroup,false);

            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            ((CustomViewHolder)viewHolder).textView.setText(imageDTOS.get(i).title);
            ((CustomViewHolder)viewHolder).textView2.setText(imageDTOS.get(i).description);
            Glide.with(((CustomViewHolder) viewHolder).imageView.getContext()).load(imageDTOS.get(i).imageUrl).into(((CustomViewHolder) viewHolder).imageView);
        }

        @Override
        public int getItemCount() {
            return imageDTOS.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {

            ImageView imageView;
            TextView textView;
            TextView textView2;

            public CustomViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = (ImageView) itemView.findViewById(R.id.item_image);
                textView = (TextView) itemView.findViewById(R.id.item_text1);
                textView2 = (TextView) itemView.findViewById(R.id.item_text2);
            }
        }
    }
}