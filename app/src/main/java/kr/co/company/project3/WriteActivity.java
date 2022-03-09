package kr.co.company.project3;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import kr.co.company.project3.Adapter.WriteAdapter;
import kr.co.company.project3.DataClass.ItemData;
import kr.co.company.project3.Util.AgmPrefer;
import kr.co.otilla.agmeditlist.ContentData;
import kr.co.otilla.agmeditlist.EditRecyclerView;

public class WriteActivity extends AppCompatActivity {

    EditText etTitle;
    EditText etContent;
    ImageButton btnGallery;
    ImageButton btnCamera;
    Button btnWrite;
    WriteAdapter mAdapter;
    ArrayList<ContentData> mList = new ArrayList<>(); //이미지들...
    private File photoFile;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    FirebaseDatabase database;
    DatabaseReference myRef;

    private FirebaseAuth mAuth; //선언함.

    AgmPrefer ap;
    ProgressDialog pd;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_SELECT_PHOTO = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        mAuth = FirebaseAuth.getInstance();//초기화함.

        ap = new AgmPrefer(WriteActivity.this);
        pd = new ProgressDialog(WriteActivity.this);

        etTitle = (EditText)findViewById(R.id.etTitle);
        etContent = (EditText)findViewById(R.id.etContent);

        btnGallery = (ImageButton)findViewById(R.id.btnGallery);
        btnGallery.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                getPhoto();
                }
        });
        btnCamera = (ImageButton)findViewById(R.id.btnCamera);
        btnCamera.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                getCamera();
            }
        });

        btnWrite = (Button)findViewById(R.id.btnWrite);
        btnWrite.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Write();
            }
        });

        EditRecyclerView mRecyclerView = (EditRecyclerView)findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
        mAdapter = new WriteAdapter(this, mList);
        mAdapter.setEdit(true); //편집모드
        mRecyclerView.setAdapter(mAdapter);
    }

    //갤러리에서 사진 가져오기
    private void getPhoto()
    {
        /*mList.add("dfklafjdfadfadkds");
                mAdapter.setEdit(true);
                mAdapter.notifyDataSetChanged();*/

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_SELECT_PHOTO); //4문장 갤러리 불러들이는 것.
    }

    //카메라 촬영해서 사진 가져오기
    private void getCamera()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(intent.resolveActivity(getPackageManager()) !=null)
        {
            photoFile = createImageFile();
        }
        if(photoFile !=null)
        {
            //버전이 높을때
            if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.M)
            {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(WriteActivity.this,"kr.co.otilla.agmeditlist.fileprovider", photoFile));
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }else{ //4.4이하일때
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
            }
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
        else
        {
            //파일 생성이 실패했을 경우
            Toast.makeText(WriteActivity.this, "이미지 생성 실패! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File storeageDir = getExternalFilesDir(null);

        try {
            return File.createTempFile(imageFileName, "jpg", storeageDir);
        } catch (IOException e) {
            return null;
        }
    }

    //등록하기
    private void Write()
    {
        pd.setMessage("글을 등록중입니다. 잠시만 기다려주세요...");//프로그래스바 띄어줌.
        pd.show();

        //프로필 이미지 저장부분
        StorageReference storageRef = storage.getReference();
        StorageReference mountainsRef;

        if(etTitle.getText().toString().isEmpty() || etContent.getText().toString().isEmpty())
        {
            Toast.makeText(WriteActivity.this, "제목과 내용을 입력해주세요.", Toast.LENGTH_SHORT);
        }
        else {

            ItemData idata = new ItemData();

            String image_time = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());

            idata.user_id = mAuth.getCurrentUser().getEmail();
            idata.idx = image_time;
            idata.reg_user = ap.getNickname();
            idata.profile = ap.getProfileImage();
            idata.title = etTitle.getText().toString();
            idata.summary = etContent.getText().toString();
            //public String image = "";
            idata.heart = 0;
            idata.reply = 0;
            idata.more = "0";
            idata.count = 0;
            idata.reg_date = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());;

            if(mList.size() > 0)
            {
                //이미지를 업로드를 할 경우
                int imageCount = 0;
                for (ContentData cdata : mList)
                {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    cdata.Bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();

                    mountainsRef = storageRef.child("board/"+ image_time + "_" + imageCount + ".jpg");

                    UploadTask uploadTask = mountainsRef.putBytes(data);
                    uploadTask.addOnFailureListener(new OnFailureListener(){
                        @Override
                        public void onFailure(@NonNull Exception exception){
                            //실패
                            idata.image += "none,";
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> task=taskSnapshot.getMetadata().getReference().getDownloadUrl();
                            task.addOnSuccessListener(new OnSuccessListener<Uri>(){
                                @Override
                                public void onSuccess(Uri uri){
                                    idata.image+=uri.toString()+",";
                                    doInsert(idata, true);
                                }
                            });
                            //Uri downloadUrl=taskSnapshot.getUploadSessionUri();
                            //idata.image += downloadUrl.toString() + ",";

                            //doInsert(idata, true);
                        }
                    });
                            imageCount++;
                }
            }
            else
            {
                //글만 썼을 경우
                doInsert(idata, false);
            }
        }
    }

    //firebase에 database에 insert 하는 메소드
    private void doInsert(ItemData idata, boolean isImageUpload)
    {
        if(isImageUpload) {
            if (idata.image.split(",").length == mList.size())
            {
                database = FirebaseDatabase.getInstance();
                myRef = database.getReference("board");
                myRef.child(idata.idx).setValue(idata); //이미지가 있을때 등록됨.
                pd.dismiss();
                setResult(RESULT_OK); //닫는 처리.
                finish();
            }
        }
        else
        {
            database = FirebaseDatabase.getInstance();
            myRef = database.getReference("board");
            myRef.child(idata.idx).setValue(idata);
            pd.dismiss();
            setResult(RESULT_OK); //닫는 처리.
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);

        ContentData cdata;

        switch(requestCode)
        {
            case REQUEST_SELECT_PHOTO:
                Uri imageUri = intent.getData();
                cdata = new ContentData();

                try
                {
                    Cursor c = getContentResolver().query(imageUri, null, null, null, null);
                    c.moveToNext();
                    String path = c.getString(c.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    cdata.Bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    cdata.FileName = path;
                    mList.add(cdata);
                    mAdapter.notifyDataSetChanged();
                } catch (IOException e){
                    e.printStackTrace();
                }
                break;
            case REQUEST_IMAGE_CAPTURE:
                cdata = new ContentData();

                Bitmap bmp = BitmapFactory.decodeFile(photoFile.getAbsolutePath());

                cdata.Bmp = bmp;
                cdata.FileName = photoFile.getName();
                mList.add(cdata);
                mAdapter.notifyDataSetChanged();
                break;
        }
    }
}