package com.example.notesapp;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LoginHistoryAdapter extends FirestoreRecyclerAdapter<LoginHistory, LoginHistoryAdapter.LoginHistoryViewHolder> {

    Context context;
    public LoginHistoryAdapter(@NonNull FirestoreRecyclerOptions<LoginHistory> options, Context context) {
        super(options);
        this.context = context;
    }

    @NonNull
    @Override
    public LoginHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_login_history, parent, false);
        return new LoginHistoryViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull LoginHistoryViewHolder holder, int position, @NonNull LoginHistory loginHistory) {
        holder.timestampTextView.setText(Utility.timestampToString(loginHistory.getTimestamp()));
        holder.deviceNameTextView.setText(loginHistory.getDeviceName());
        String location = "Lat: " + loginHistory.getLatitude() + ", Lon: " + loginHistory.getLongitude();
        holder.locationTextView.setText(location);

        String address = getAddressFromLocation(context, loginHistory.getLatitude(), loginHistory.getLongitude());
        holder.addressTextView.setText(address);
    }

    class LoginHistoryViewHolder extends RecyclerView.ViewHolder {
        TextView timestampTextView, deviceNameTextView, locationTextView, addressTextView;

        public LoginHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            timestampTextView = itemView.findViewById(R.id.timestamp_tv);
            deviceNameTextView = itemView.findViewById(R.id.device_name_tv);
            locationTextView = itemView.findViewById(R.id.location_tv);
            addressTextView = itemView.findViewById(R.id.address_tv);
        }
    }

    private String getAddressFromLocation(Context context, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String addressLine = address.getAddressLine(0); // Alamat jalur
                String countryName = address.getCountryName(); // Nama negara
                return addressLine + ", " + countryName;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Address not found";
    }

}