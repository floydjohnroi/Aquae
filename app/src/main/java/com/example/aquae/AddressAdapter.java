package com.example.aquae;

import android.content.Context;
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
import java.util.List;
import java.util.Objects;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {

    private Context context;
    private List<AddressModel> addressModelList;

    private int selectedPosition = -1;

    private AddressCallback addressCallback;

    public interface AddressCallback {
        void saveAddress(String addresses);
    }

    public AddressAdapter(Context context, List<AddressModel> addressModelList, AddressCallback addressCallback) {
        this.context = context;
        this.addressModelList = addressModelList;
        this.addressCallback = addressCallback;
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.address_layout, parent, false);

        return new AddressAdapter.AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        AddressModel addressModel = addressModelList.get(position);

        if (selectedPosition == -1) {
            selectedPosition = getItemCount()-1;
        }

        holder.addresses.setText(addressModel.getAddress());

        holder.check.setChecked(position == selectedPosition);

        holder.check.setTag(position);

        addressCallback.saveAddress(getSelectedItem());

        holder.addresses.setOnClickListener(v -> {

            itemCheckChanged(holder.check);
            addressCallback.saveAddress(getSelectedItem());

        });

        holder.check.setOnClickListener(v -> {

            itemCheckChanged(v);
            addressCallback.saveAddress(getSelectedItem());

        });

    }

    public String getSelectedItem() {
        if (selectedPosition != -1) {
            return String.valueOf(addressModelList.get(selectedPosition).getAddress());
        }
        return "";
    }

    private void itemCheckChanged(View v) {
        selectedPosition = (Integer) v.getTag();
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return addressModelList.size();
    }

    class AddressViewHolder extends RecyclerView.ViewHolder {

        TextView addresses;
        RadioButton check;

        public AddressViewHolder(View itemView) {
            super(itemView);

            addresses = itemView.findViewById(R.id.addresses);
            check = itemView.findViewById(R.id.check);

        }
    }
}
