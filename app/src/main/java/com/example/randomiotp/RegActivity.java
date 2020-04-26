package com.example.randomiotp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

public class RegActivity extends AppCompatActivity {
    // 비밀번호 정규식
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*\\d)[a-zA-Z\\d]{6,}$");//최소 6자리, 최소 소문자 1자리, 최소 숫자 한자리

    private static final String TAG = "-------------";

    // 파이어베이스 인증 객체 생성
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestoreDB;

    //회원가입시 작성해야 하는 목록
    private EditText etxtEmail;
    private EditText etxtPw;
    private EditText etxtCheckPw; //비밀번호확인
    private EditText etxtName;
    private EditText etxtNickname;
    private EditText etxtNumber;

    private TextView txtEmail;
    private TextView txtPw;
    private TextView txtCheckPw;

    private String email = "";
    private String password = "";
    private String checkpw = "";
    private String name ="";
    private String nickName="";
    private String number="";

    private String seed="";

    private int nEmail=0;
    private int nPassword=0;
    private int nCheckPassword=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);

        Button btnReg = findViewById(R.id.btnReg);
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });

        createSeed();

        // 파이어베이스 인증 객체 선언
        firebaseAuth = FirebaseAuth.getInstance();
        firestoreDB = FirebaseFirestore.getInstance();

        etxtEmail = findViewById(R.id.etxtEmail);
        etxtPw = findViewById(R.id.etxtPw);
        etxtCheckPw = findViewById(R.id.etxtCheckPw);
        etxtName = findViewById(R.id.etxtName);
        etxtNickname = findViewById(R.id.etxtNickname);
        etxtNumber = findViewById(R.id.etxtNumber);

        txtEmail = findViewById(R.id.txtEmail);
        txtPw = findViewById(R.id.txtPw);
        txtCheckPw = findViewById(R.id.txtCheckPw);

        etxtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {   }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                email = etxtEmail.getText().toString();
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    // 이메일 형식 불일치
                    txtEmail.setText("이메일 형식이 맞지 않습니다.");
                    nEmail=0;
                }
                else {
                    txtEmail.setText("등록 가능한 이메일 입니다.");
                    nEmail=1;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {            }
        });

        etxtPw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {   }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                password = etxtPw.getText().toString();
                if (!PASSWORD_PATTERN.matcher(password).matches()) {
                    // 비밀번호 형식 불일치
                    txtPw.setText("비밀번호 형식이 맞지 않습니다.");
                    nPassword=0;
                }
                else {
                    txtPw.setText("사용가능한 비밀번호입니다.");
                    nPassword=1;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {            }
        });

        etxtCheckPw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {   }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                password = etxtPw.getText().toString();
                checkpw = etxtCheckPw.getText().toString();
                if (!password.equals(checkpw)) {//문자열 비교는 반드시 equals!!
                    // 비밀번호가 다름
                    txtCheckPw.setText("비밀번호가 다릅니다.");
                    nCheckPassword=0;
                }
                else {
                    txtCheckPw.setText("비밀번호가 일치합니다.");
                    nCheckPassword=1;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {            }
        });

    }

    public void saveUserData(){
        email = etxtEmail.getText().toString();
        password = etxtPw.getText().toString();
        name = etxtName.getText().toString();
        nickName = etxtNickname.getText().toString();
        number = etxtNumber.getText().toString();

        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("id(email)", email);
        user.put("password", password);
        user.put("name", name);
        user.put("nickname",nickName);
        user.put("phoneNumber",number);
        user.put("seed", seed);

        // Add a new document with a generated ID
        firestoreDB.collection(email).document("information")
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "데이터베이스 성공");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "데이터베이스 실패");
                    }
                });
    }

    public void signUp() {
        email = etxtEmail.getText().toString();
        password = etxtPw.getText().toString();
        checkpw = etxtCheckPw.getText().toString();

        //createUser(email, password);

        if(nEmail==1 && nPassword==1){
            if(nCheckPassword==1){
                Log.d(TAG, "nEmail = "+ nEmail);
                Log.d(TAG, "nPassword = "+nPassword);
                Log.d(TAG, "nCheckPassword = "+nCheckPassword);
                createUser(email, password);
                Log.d(TAG, "성공");
            }
        }else{
            Toast.makeText(getApplicationContext(), "회원가입 실패", Toast.LENGTH_LONG).show();
        }
    }

    private void createSeed(){
        StringBuffer buffer = new StringBuffer();
        Random random = new Random();
        String chars[]=
                "a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z".split(",");
        for (int i=0; i<8; i++) {
            buffer.append(chars[random.nextInt(chars.length)]);
        }
        //랜덤값(8자리 SEED 값 생성)
        System.out.println("랜덤값: " + buffer.toString());
        seed = buffer.toString();
    }

    // 회원가입
    private void createUser(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 회원가입 성공
                            Log.d(TAG, "성공성공!!!");
                            createSeed();
                            //saveUserData();
                            Toast.makeText(getApplicationContext(), "회원가입성공", Toast.LENGTH_LONG).show();
                            finish();
                        }else {
                            Toast.makeText(getApplicationContext(), "회원가입실패", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                });
    }
}
