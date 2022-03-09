package kr.co.company.project3;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import kr.co.company.project3.Util.AgmPrefer;

public class LoginActivity extends AppCompatActivity {
    EditText etID;
    EditText etPWD;
    Button btnLogin;
    Button btnJoin;

    private FirebaseAuth mAuth; //선언함.
    ProgressDialog pd; //선언.
    AgmPrefer ap;

    //ButtenKnife

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ap = new AgmPrefer(LoginActivity.this);//컨텐츠를 받는 여기서 초기화를 해준다.

        pd = new ProgressDialog(LoginActivity.this); //초기화.

        etID = (EditText)findViewById(R.id.etID);
        etPWD = (EditText)findViewById(R.id.etPWD);
        btnJoin = (Button)findViewById(R.id.btnJoin);
        btnLogin = (Button)findViewById(R.id.btnLogin);

        mAuth = FirebaseAuth.getInstance();//초기화함.

        btnJoin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                userJoin();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                userLogin();
            }
        });


    }

    private void userLogin() {
        String id = etID.getText().toString();
        String password = etPWD.getText().toString();
        String email = id + "@office.skhu.ac.kr";

        pd.setMessage("로그인중입니다. 잠시만 기다려주세요...");//프로그래스바 띄어줌.
        pd.show();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>(){
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task){
                        if(task.isSuccessful())
                        {
                            //사용자인증 부분
                            if(firebaseAuth.getCurrentUser().isEmailVerified()){
                                String email = mAuth.getCurrentUser().getEmail();
                                if(!email.isEmpty())
                                {
                                    ap.setEmail(email);//이메일이 있다면
                                    ap.setNickname(email.split("@")[0]);
                                }
                                setResult(RESULT_OK);
                                finish(); //로그인 완료된 부분.
                                Toast.makeText(LoginActivity.this, "로그인 되었습니다", Toast.LENGTH_SHORT).show();

                            }else{
                                Toast.makeText(LoginActivity.this, "이메일을 인증해주세요",
                                        Toast.LENGTH_SHORT).show();

                            }

                        }else{
                            Toast.makeText(LoginActivity.this, "이메일, 비밀번호가 틀렸습니다!", Toast.LENGTH_SHORT).show();
                        }
                        pd.dismiss();
                    }
                });
    }

    private void userJoin()
    {
        String id = etID.getText().toString();
        String password = etPWD.getText().toString();
        String email = id + "@office.skhu.ac.kr";


        pd.setMessage("등록중입니다. 잠시만 기다려주세요...");//프로그래스바 띄어줌.
        pd.show(); //여기서 보여주고,
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>(){
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if(task.isSuccessful())
                        {
                            //사용자 인증 부분
                            firebaseAuth.getCurrentUser().sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                setResult(RESULT_OK);
                                                Toast.makeText(LoginActivity.this,
                                                        "인증 메일을 발송하였습니다."+"\n"+"인증을 완료해주세요",
                                                        Toast.LENGTH_SHORT).show();
                                                //etID.setText("");
                                                //etPWD.setText("");

                                            }else{
                                                Toast.makeText(LoginActivity.this, task.getException().getMessage(),
                                                        Toast.LENGTH_SHORT).show();

                                            }


                                        }
                                    });

                        }else{
                            Toast.makeText(LoginActivity.this, "등록 에러! 다시 시도해주세요", Toast.LENGTH_SHORT).show();
                        }
                        firebaseAuth.setLanguageCode("ko"); //인증메일 언어를 한국어로 설정
                        pd.dismiss();//여기서 닫아준다.
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data); //data-intent로

        if(requestCode == 400)
        {
            finish();
            startActivity(getIntent()); //간단한 새로고침.
        }
    }
}