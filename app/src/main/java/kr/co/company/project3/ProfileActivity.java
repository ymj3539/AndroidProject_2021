package kr.co.company.project3;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import kr.co.company.project3.Util.AgmPrefer;

public class ProfileActivity extends AppCompatActivity {
    ImageView ivProfile;
    Spinner spinner1;
    Spinner spinner2;
    Button btnSave;
    Button btnCancel;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    AgmPrefer ap;
    ProgressDialog pd;

    EditText etNickname;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ap = new AgmPrefer(ProfileActivity.this);
        pd = new ProgressDialog(ProfileActivity.this);

        ivProfile = (ImageView)findViewById(R.id.ivProfile);
        ivProfile.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 200);
            }
        });

        etNickname = (EditText)findViewById(R.id.etNickname);
        btnSave = (Button)findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Save();
            }
        });

        btnCancel = (Button)findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });
        //number스피너
        Spinner yearSpinner = (Spinner)findViewById(R.id.spinner1);
        ArrayAdapter yearAdapter = ArrayAdapter.createFromResource(this,R.array.number, android.R.layout.simple_spinner_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);
        spinner1 = yearSpinner;
        Spinner monthSpinner = (Spinner)findViewById(R.id.spinner2);
        ArrayAdapter monthAdapter = ArrayAdapter.createFromResource(this,R.array.test, android.R.layout.simple_spinner_item);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(monthAdapter);
        spinner2 = monthSpinner;
    }

    private void Save()
    {
        pd.setMessage("프로필 업데이트 중입니다. 잠시만 기다려주세요...");//프로그래스바 띄어줌.
        pd.show();

        //프로필 이미지 저장부분
        StorageReference storageRef = storage.getReference();
        StorageReference mountainsRef = storageRef.child("profile/"+ap.getNickname()+".jpg");

        ivProfile.setDrawingCacheEnabled(true);
        ivProfile.buildDrawingCache();
        Bitmap bitmap = ivProfile.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mountainsRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener(){
            @Override
            public void onFailure(@NonNull Exception exception){
                //실패
            }
        });
        mountainsRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

        @Override

        public void onSuccess(Uri downloadUrl) {

            ap.setProfileImage(downloadUrl.toString());
            ap.setNickname(spinner1.getSelectedItem().toString() +" "+ spinner2.getSelectedItem().toString() +"\n"+ etNickname.getText().toString());

                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(spinner1.getSelectedItem().toString() + spinner2.getSelectedItem().toString())
                        .setPhotoUri(downloadUrl)
                        .build();


                user.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>(){
                            @Override
                            public void onComplete(@NonNull Task<Void> task){
                                if(task.isSuccessful()){
                                    //성공 뒤에
                                    setResult(RESULT_OK);
                                    finish();
                                    Toast.makeText(ProfileActivity.this, "정상적으로 수정 되었습니다.", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    Toast.makeText(ProfileActivity.this, "오류가 발생했습니다. 잠시후 다시 해보세요.", Toast.LENGTH_SHORT).show();
                                }
                                pd.dismiss();
                            }
                        });
            }
        });
        //닉네임을 저장할겁니다.


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data); //data-intent로

        if(requestCode == 200)
        {
            Uri imageUri = data.getData();
            Bitmap bitmap = null;

            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                ivProfile.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}