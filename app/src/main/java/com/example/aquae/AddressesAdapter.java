package com.example.aquae;

import android.content.Context;
import android.provider.ContactsContract;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class AddressesAdapter extends RecyclerView.Adapter<AddressesAdapter.AddressesViewHolder> {

    private Context context;
    private List<AddressModel> addressModelList;

    public AddressesAdapter(Context context, List<AddressModel> addressModelList) {
        this.context = context;
        this.addressModelList = addressModelList;
    }

    @NonNull
    @Override
    public AddressesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_addresses, parent, false);
        return new AddressesAdapter.AddressesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressesViewHolder holder, int position) {
        AddressModel addressModel = addressModelList.get(position);
        holder.addr.setText(addressModel.getAddress());
        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference().child("customers")
                        .orderByChild("customer_id").equalTo(new Session(context).getId())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    for (DataSnapshot snapshot1 : snapshot.child("addresses").getChildren()) {
                                        if (addressModel.getAddress().equals(snapshot1.getValue())) {
                                            snapshot1.getRef().removeValue()
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(context, "Address Removed", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return addressModelList.size();
    }

    class AddressesViewHolder extends RecyclerView.ViewHolder {

        TextView addr;
        ImageView remove;

        public AddressesViewHolder(View itemView) {
            super(itemView);
            addr = itemView.findViewById(R.id.addr);
            remove = itemView.findViewById(R.id.remove);
        }
    }
}
