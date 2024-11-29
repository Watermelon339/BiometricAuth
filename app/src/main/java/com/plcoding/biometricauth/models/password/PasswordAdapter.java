package com.plcoding.biometricauth.models.password;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.plcoding.biometricauth.R;
import com.plcoding.biometricauth.models.password.Password;

import java.util.List;

public class PasswordAdapter extends RecyclerView.Adapter<PasswordAdapter.PasswordViewHolder> {

    private List<Password> passwordList;

    public PasswordAdapter(List<Password> passwordList) {
        this.passwordList = passwordList;
    }

    @NonNull
    @Override
    public PasswordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_password, parent, false);
        return new PasswordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PasswordViewHolder holder, int position) {
        Password password = passwordList.get(position);
        holder.textName.setText(password.getName());
        holder.textLogin.setText(password.getLogin());
        holder.textPassword.setText(password.getPassword());
    }

    @Override
    public int getItemCount() {
        return passwordList.size();
    }

    public static class PasswordViewHolder extends RecyclerView.ViewHolder {
        TextView textName, textLogin, textPassword;

        public PasswordViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.text_password_name);
            textLogin = itemView.findViewById(R.id.text_password_login);
            textPassword = itemView.findViewById(R.id.text_password_value);
        }
    }
}
