package com.example.rizzplayer.DAO;

import android.app.Activity;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.rizzplayer.Business.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

public class UserDAO {
    FirebaseAuth auth;

    public UserDAO() {
        this.auth = FirebaseAuth.getInstance();
    }

    public interface CreateUserCallback {
        void onComplete(String email, String errorMessage);
    }

    public void createUser(User user, final CreateUserCallback callback) {
        String email = user.getEmail();
        String password = user.getPassword();

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            callback.onComplete(email, null); // Trả về thông tin người dùng
                        } else {
                            String errorMessage = ((FirebaseAuthException) task.getException()).getErrorCode();
                            callback.onComplete(null, errorMessage); // Trả về thông báo lỗi
                        }
                    }
                });
    }

    public interface SignInCallback {
        void onComplete(boolean isSuccess, String errorMessage);
    }

    public static void signIn(String email, String password, final SignInCallback callback) {
        FirebaseAuth auth;
        auth = FirebaseAuth.getInstance();

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Đăng nhập thành công
                            callback.onComplete(true, null);
                        } else {
                            // Đăng nhập thất bại
                            String errorMessage = task.getException().getMessage();
                            callback.onComplete(false, errorMessage);
                        }
                    }
                });
    }
}
