package kr.co.company.project3.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AgmPrefer {
    public SharedPreferences mSharedPref;

    public String mem_email = "mem_email";
    public String mem_nickname = "mem_nickname"; //키값들.
    public String mem_image = "mem_image";

    public AgmPrefer(Context ctx)
    {
        mSharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public void setNickname(String name)
    {
        SharedPreferences.Editor shareEditor = mSharedPref.edit();
        shareEditor.putString(mem_nickname, name);
        shareEditor.commit(); //이렇게 하면 sharedpreferences에 키값을 넣고..
    }

    public String getNickname()
    {
        return mSharedPref.getString(mem_nickname, "");
    }

    public void setEmail(String email)
    {
        SharedPreferences.Editor shareEditor = mSharedPref.edit();
        shareEditor.putString(mem_email, email);
        shareEditor.commit(); //이렇게 하면 sharedpreferences에 키값을 넣고..
    }

    public String getProfileImage() { return mSharedPref.getString(mem_image, "");}

    public void setProfileImage(String image_url)
    {
        SharedPreferences.Editor shareEditor = mSharedPref.edit();
        shareEditor.putString(mem_image, image_url);
        shareEditor.commit();
    }

    public String getEmail()
    {
        return mSharedPref.getString(mem_email, "");
    }
    public void clear()
    {
        this.setNickname("");
        this.setEmail("");
    }


}
