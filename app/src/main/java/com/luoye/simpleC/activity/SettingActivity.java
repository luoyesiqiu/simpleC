package com.luoye.simpleC.activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.luoye.simpleC.R;

import java.io.FileDescriptor;
import java.io.PrintWriter;

/**
 * Created by zyw on 2017/11/2.
 */
public class SettingActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar=getActionBar();
        if(actionBar!=null)
            actionBar.setDisplayHomeAsUpEnabled(true);
        FragmentTransaction fragmentTransaction=getFragmentManager().beginTransaction();
        fragmentTransaction.replace(android.R.id.content,new SettingFragment());
        fragmentTransaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    public  static class SettingFragment extends PreferenceFragment
    {

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            findPreference("preference_qq_group").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    try {
                        Intent it = new Intent(Intent.ACTION_VIEW);
                        it.setData(Uri.parse("https://jq.qq.com/?_wv=1027&k=56wZNxE"));
                        startActivity(it);
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    return false;
                }
            });
            findPreference("preference_github").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    try {
                        Intent it = new Intent(Intent.ACTION_VIEW);
                        it.setData(Uri.parse(getString(R.string.github)));
                        startActivity(it);
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    return false;
                }
            });
        }
    }
}
