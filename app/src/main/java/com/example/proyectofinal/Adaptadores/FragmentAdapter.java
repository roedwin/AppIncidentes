package com.example.proyectofinal.Adaptadores;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.proyectofinal.Fragmentos.ActivosFragment;
import com.example.proyectofinal.Fragmentos.ResueltosFragment;

public class FragmentAdapter extends FragmentStateAdapter {

    public FragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new ActivosFragment();
            case 1:
                return new ResueltosFragment();
            default:
                return new ActivosFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
