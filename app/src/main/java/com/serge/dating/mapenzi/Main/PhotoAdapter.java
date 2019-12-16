package com.serge.dating.mapenzi.Main;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.serge.dating.mapenzi.R;
import com.serge.dating.mapenzi.Utils.CalculateAge;
import com.serge.dating.mapenzi.Utils.GPS;
import com.serge.dating.mapenzi.Utils.User;


import java.util.List;


public class PhotoAdapter extends ArrayAdapter<User> {
    Context mContext;

    private GPS gps;

    int age = 0;
    public PhotoAdapter(@NonNull Context context, int resource, @NonNull List<User> objects) {
        super(context, resource, objects);
        this.mContext = context;

        gps = new GPS(context);


    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final User card_item = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
        }

        TextView name = convertView.findViewById(R.id.name);
        ImageView image = convertView.findViewById(R.id.image);
        ImageButton btnInfo = convertView.findViewById(R.id.checkInfoBeforeMatched);

        CalculateAge cal = new CalculateAge(card_item.getProfile().getDateOfBirth());
        age = cal.getAge();

        int distance = gps.calculateDistance(card_item.getLatitude(),card_item.getLongitude(), gps.getLatitude(),gps.getLongitude());

        name.setText(card_item.getUsername() + ", " + age);
        btnInfo.setOnClickListener(v -> {

            Intent intent = new Intent(mContext, ProfileCheckinMain.class);
            intent.putExtra("name", card_item.getUsername() + ", " + age);
            intent.putExtra("photo", card_item.getProfileImageUrl());
            intent.putExtra("bio", card_item.getProfile().getAboutMe());
            intent.putExtra("classUser", card_item);
            intent.putExtra("interest", card_item.getProfile().getJobTitle());
            intent.putExtra("distance", distance);
            mContext.startActivity(intent);
        });

      try {
          switch (card_item.getProfileImageUrl()) {
              case "defaultFemale":
                  Glide.with(getContext()).load(R.drawable.default_woman).into(image);
                  break;
              case "defaultMale":
                  Glide.with(getContext()).load(R.drawable.default_man).into(image);
                  break;
              default:
                  Glide.with(getContext()).load(card_item.getProfileImageUrl()).into(image);
                  break;
          }

      }catch (NullPointerException ex){

      }
        return convertView;
    }
}
