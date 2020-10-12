package com.smeet.dictionary;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.material.tabs.TabLayout;
import com.smeet.dictionary.fragments.FragmentAntonyms;
import com.smeet.dictionary.fragments.FragmentDefinition;
import com.smeet.dictionary.fragments.FragmentExample;
import com.smeet.dictionary.fragments.FragmentSynonyms;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordMeaningActivity extends AppCompatActivity {
    private ViewPager viewPager;

    String enWord;
    DatabaseHelper myDbHelper;
    Cursor c = null;

    public String enDefinition;
    public String example;
    public String synonyms;
    public String antonyms;

    TextToSpeech tts;

    boolean startedFromShare=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_meaning);


        Bundle bundle = getIntent().getExtras();
        enWord = bundle.getString("en_word");

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null){
            if("text/plain".equals(type)){
                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                startedFromShare=true;

                if(sharedText !=null){
                    Pattern p = Pattern.compile("[A-Za-z \\-.]{1,25}");
                    Matcher m = p.matcher(sharedText);
                    if(m.matches()){
                        enWord=sharedText;
                    }
                    else {
                        enWord="Not Available";
                    }
                }
            }
        }

        myDbHelper = new DatabaseHelper(this);
        try {
            myDbHelper.openDatabase();
        }catch (SQLException sqle){
            throw sqle;
        }

        c = myDbHelper.getMeaning(enWord);

        if(c.moveToFirst()){

            enDefinition = c.getString(c.getColumnIndex("en_definition"));
            example= c.getString(c.getColumnIndex("example"));
            synonyms= c.getString(c.getColumnIndex("synonyms"));
            antonyms= c.getString(c.getColumnIndex("antonyms"));

            myDbHelper.insertHistory(enWord);
        }
        else {
            enWord="Not Available";
        }





        ImageButton btnSpeak = (ImageButton)findViewById(R.id.btnSpeak);
        btnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tts = new TextToSpeech(WordMeaningActivity.this, new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                     if(status == TextToSpeech.SUCCESS){
                         int result = tts.setLanguage(Locale.getDefault());
                         if(result==TextToSpeech.LANG_MISSING_DATA  || result==TextToSpeech.LANG_NOT_SUPPORTED){
                             Log.e("error","This Language is not supported");
                         }
                         else{
                             tts.speak(enWord, TextToSpeech.QUEUE_FLUSH,null);
                         }
                     }
                     else
                         Log.e("error","Initailization Failed!");
                    }
                });
            }
        });


        Toolbar toolbar = (Toolbar)findViewById(R.id.mToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(enWord);

        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);

        viewPager = (ViewPager)findViewById(R.id.tab_viewpager);

        if(viewPager !=null){
            setupViewPager(viewPager);

        }
        TabLayout tabLayout = (TabLayout)findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }
    private class ViewPagerAdapter extends FragmentPagerAdapter{
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager){
            super(manager);
        }
        void addFrag(Fragment fragment, String title){
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

    }


    private void setupViewPager(ViewPager viewPager){
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new FragmentDefinition(),"Definition");
        adapter.addFrag(new FragmentSynonyms(),"Synonyms");
        adapter.addFrag(new FragmentAntonyms(),"Antonyms");
        adapter.addFrag(new FragmentExample(),"Example");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            if(startedFromShare){
                Intent intent = new Intent(this,MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }else {
                onBackPressed();
            }

        }
        return super.onOptionsItemSelected(item);
    }
}