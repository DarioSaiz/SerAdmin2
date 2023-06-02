package com.example.seradmin.Tree;

import android.os.Bundle;
import android.view.Menu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.seradmin.R;
import com.google.firebase.storage.StorageReference;

public class MainTree extends AppCompatActivity {
    private final static int NUMBER_OF_FRAGMENTS = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_tree);

        ViewPager2 viewPager = findViewById(R.id.view_pager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), getLifecycle());
        viewPager.setAdapter(viewPagerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private static class ViewPagerAdapter extends FragmentStateAdapter {

        public ViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return new FileTreeFragment();
//            switch (position) {
//                case 0: return new FileTreeFragment();
//                case 1: return new LogTreeFragment();
//                case 2: return new RoomTreeFragment();
//                default: return new JsonTreeFragment();
//            }
        }

        @Override
        public int getItemCount() {
            return NUMBER_OF_FRAGMENTS;
        }
    }
}
