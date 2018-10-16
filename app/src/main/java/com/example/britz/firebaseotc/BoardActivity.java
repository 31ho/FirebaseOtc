package com.example.britz.firebaseotc;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BoardActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<ImageDTO> imageDTOS = new ArrayList<>();
    private List<String> uidLists = new ArrayList<>();
    private FirebaseDatabase firebaseDatabase;
    private BoardRecyclerViewAdapter boardRecyclerViewAdapter;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        recyclerView = (RecyclerView) findViewById(R.id.borad_activity_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        boardRecyclerViewAdapter = new BoardRecyclerViewAdapter();
        recyclerView.setAdapter(boardRecyclerViewAdapter);

        firebaseDatabase.getReference().child("images").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                imageDTOS.clear();
                uidLists.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ImageDTO imageDTO = snapshot.getValue(ImageDTO.class);
                    String key = snapshot.getKey();
                    imageDTOS.add(imageDTO);
                    uidLists.add(key);
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
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
            ((CustomViewHolder)viewHolder).textView.setText(imageDTOS.get(i).title);
            ((CustomViewHolder)viewHolder).textView2.setText(imageDTOS.get(i).description);
            Glide.with(((CustomViewHolder) viewHolder).imageView.getContext()).load(imageDTOS.get(i).imageUrl).into(((CustomViewHolder) viewHolder).imageView);
            ((CustomViewHolder)viewHolder).starButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onStarClicked(firebaseDatabase.getReference().child("images").child(uidLists.get(i)));
                }
            });
            if (imageDTOS.get(i).stars.containsKey(auth.getCurrentUser().getUid())) {
                ((CustomViewHolder)viewHolder).starButton.setImageResource(R.drawable.baseline_favorite_black_18dp);
            } else {
                ((CustomViewHolder)viewHolder).starButton.setImageResource(R.drawable.baseline_favorite_border_black_18dp);
            }
        }

        @Override
        public int getItemCount() {
            return imageDTOS.size();
        }

        private void onStarClicked(DatabaseReference postRef) {
            postRef.runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    ImageDTO dto = mutableData.getValue(ImageDTO.class);
                    if (dto == null) {
                        Log.d("트랜젝션 널", "널값");
                        return Transaction.success(mutableData);
                    }

                    if (dto.stars.containsKey(auth.getCurrentUser().getUid())) {
                        // Unstar the post and remove self from stars
                        Log.d("트랜젝션 플러스", "더하기");
                        dto.starCount = dto.starCount - 1;
                        dto.stars.remove(auth.getCurrentUser().getUid());
                    } else {
                        // Star the post and add self to stars

                        Log.d("트랜젝션 마이너스", "빼기");
                        dto.starCount = dto.starCount + 1;
                        dto.stars.put(auth.getCurrentUser().getUid(), true);
                    }

                    // Set value and report transaction success
                    mutableData.setValue(dto);
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean b,
                                       DataSnapshot dataSnapshot) {
                    // Transaction completed
                    Log.d("트랜젝션", "postTransaction:onComplete:" + databaseError);
                }
            });
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {

            ImageView imageView;
            TextView textView;
            TextView textView2;
            ImageView starButton;

            public CustomViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = (ImageView) itemView.findViewById(R.id.item_image);
                textView = (TextView) itemView.findViewById(R.id.item_text1);
                textView2 = (TextView) itemView.findViewById(R.id.item_text2);
                starButton = (ImageView) itemView.findViewById(R.id.item_starButton_imageView);
            }
        }
    }
}