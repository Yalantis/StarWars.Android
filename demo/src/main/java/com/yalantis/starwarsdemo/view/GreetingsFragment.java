package com.yalantis.starwarsdemo.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.yalantis.starwarsdemo.R;
import com.yalantis.starwarsdemo.databinding.FragmentGreetingsBinding;
import com.yalantis.starwarsdemo.interfaces.GreetingFragmentInterface;

/**
 * Created by Artem Kholodnyi on 11/17/15.
 */
public class GreetingsFragment extends Fragment {
    public final static String TAG = GreetingsFragment.class.getCanonicalName();

    private FragmentGreetingsBinding binding;
    private GreetingFragmentInterface mListener;

    public static GreetingsFragment newInstance() {
        return new GreetingsFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListener = (GreetingFragmentInterface) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGreetingsBinding.inflate(onGetLayoutInflater(savedInstanceState), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.toolbar.setNavigationIcon(R.drawable.ic_menu);
        binding.btnSetupProfile.setOnClickListener(v -> mListener.onSetupProfileClick());
    }
}
