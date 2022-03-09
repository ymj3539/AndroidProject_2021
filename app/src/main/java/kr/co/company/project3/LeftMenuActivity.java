package kr.co.company.project3;

import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import kr.co.company.project3.Chatting.ChatMain;
import kr.co.company.project3.Util.AgmPrefer;

public class LeftMenuActivity extends AppCompatActivity {
    Spinner spinner1;
    Spinner spinner2;
    DrawerLayout drawerLayout;
    NavigationView naviView;
    FirebaseAuth firebaseAuth;
    TextView leftmenu_header_nickname;
    ImageView leftmenu_header_profile;
    AgmPrefer ap; //preferences를 사용하고 싶으면 무조건 얘를 선언해준다.

    public interface OnLogEventListener{
        void Fire();
    }

    OnLogEventListener mListener;

    public void setOnLogEVentListener(OnLogEventListener listener)
    {
        this.mListener = listener;
    }

    @Override
    public void setContentView(int layoutID){
        super.setContentView(R.layout.activity_left_menu);

        ap = new AgmPrefer(getBaseContext());

        ViewGroup viewgroup = (ViewGroup)findViewById(R.id.naviFrame);
        LayoutInflater.from(this).inflate(layoutID, viewgroup, true);
        firebaseAuth = FirebaseAuth.getInstance(); //초기화

        getNavigation();

        if(firebaseAuth.getCurrentUser() != null)
        {
            LoginText(true);
        }
    }

    private void getNavigation() {
        drawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout);
        naviView = (NavigationView)findViewById(R.id.naviView);
        naviView.setNavigationItemSelectedListener(naviListener);

        View headerview = naviView.getHeaderView(0); //선언만 한것임.

        leftmenu_header_nickname = (TextView)headerview.findViewById(R.id.leftmenu_header_nickname);
        leftmenu_header_profile = (ImageView)headerview.findViewById(R.id.leftmenu_header_profile);

        naviView.setItemIconTintList(null);
    }
    public void openDrawer()
    {
        if(drawerLayout != null){
            drawerLayout.openDrawer(Gravity.LEFT);
            //이것은 외부에서 실행할 것이기 때문에 null검사를 꼭 해줘야함.
        }
    }
    NavigationView.OnNavigationItemSelectedListener naviListener = new NavigationView.OnNavigationItemSelectedListener()
    {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item)
        {
            switch(item.getItemId()) {
                case R.id.menu_login:

                    if (item.getTitle().equals("로그인")) {
                        Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                        startActivityForResult(intent, 100);
                    } else {
                        firebaseAuth.signOut();
                        ap.clear();
                        Toast.makeText(LeftMenuActivity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LeftMenuActivity.this, MainActivity.class);
                        startActivityForResult(intent, 400);
                        drawerLayout.closeDrawer(Gravity.LEFT);
                        LoginText(false);
                    }
                    break;

                case R.id.menu_profile:
                {Intent intent = new Intent(getBaseContext(), ProfileActivity.class);
                    startActivityForResult(intent, 110);
                    break;}

                case R.id.menu_chatting:
                {Intent intent = new Intent(getBaseContext(), ChatMain.class);
                    startActivityForResult(intent, 120);
                    break;}

                case R.id.menu_homepage:
                {Intent intent = new Intent(getBaseContext(), HomepageActivity.class);
                    startActivityForResult(intent, 130);
                    break;}

                case R.id.gotoPasswordResetButton:
                {Intent intent = new Intent(getBaseContext(), PasswordResetActivity.class);
                    startActivityForResult(intent, 140);
                    break;}
            }
            return false;
        }
    };
    private void LoginText(boolean isLogin)
    {
        //leftmenu_header_nickname.setText(ap.getNickname()); //이렇게 하면 로그인했을때 이름도 바뀜.
        MenuItem item = naviView.getMenu().findItem(R.id.menu_login);
        MenuItem item_profile = naviView.getMenu().findItem(R.id.menu_profile);
        MenuItem item_chatting = naviView.getMenu().findItem(R.id.menu_chatting);

        if(isLogin)
        {
            item.setTitle("로그아웃");
            item_profile.setVisible(true);
            item_chatting.setVisible(true);

//            ap.setNickname(spinner1.getSelectedItem().toString() + spinner2.getSelectedItem().toString());
            leftmenu_header_nickname.setText(ap.getNickname());
            Glide.with(getBaseContext())
                    .load(ap.getProfileImage())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.noperson)
                            .centerCrop()
                            .dontTransform())
                    .transition(new DrawableTransitionOptions().crossFade(500))
                    .into(leftmenu_header_profile);
        }else
            {
                item.setTitle("로그인");
                item_profile.setVisible(false);
                item_chatting.setVisible(false);
                leftmenu_header_nickname.setText(R.string.login_msg);
                leftmenu_header_profile.setImageResource(R.drawable.noperson);
            }

        if(mListener != null)
            mListener.Fire();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultcode, Intent data)
    {
        //로그인
        if(requestCode == 100)
        {
            if(resultcode == RESULT_OK)
            {
                LoginText(true);
            }
        }
        //프로필
        if(requestCode == 110)
        {
            if(resultcode == RESULT_OK)
            {
                LoginText(true);
            }
        }
    }
}