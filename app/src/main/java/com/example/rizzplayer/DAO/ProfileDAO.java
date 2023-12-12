package com.example.rizzplayer.DAO;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.rizzplayer.Business.Profile;
import com.example.rizzplayer.Business.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ProfileDAO {
    private DatabaseReference profileRef;

    public ProfileDAO() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        profileRef = database.getReference("profiles");
    }

    public void saveProfile(Profile profile) {
        profileRef.child(profile.getEmail()).setValue(profile);
    }

    public interface GetProfileCallback {
        void onSuccess(Profile profile);

        void onFailure(String errorMessage);
    }

    public void getProfileByEmail(String email, final GetProfileCallback callback) {
        Log.d("D", "getProfileByEmail: " + email);
        String encodedEmail = email.replace(".", ",");
        Query query = profileRef.orderByChild("email").equalTo(encodedEmail); // 1
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) { // 2
                if (dataSnapshot.exists()) { // 3
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) { // 4
                        Profile profile = childSnapshot.getValue(Profile.class); // 5
                        callback.onSuccess(profile); // 6
                        return; // 7
                    }
                }
                // Không tìm thấy profile
                else callback.onFailure("Profile not found"); // 8
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu cần thiết
                callback.onFailure(databaseError.getMessage()); // 9
            }
        });
    }

    public Profile findProfileByUser(User user) {
        Profile profile = new Profile(null, null, null, null, false);
        return profile;
    }
}
