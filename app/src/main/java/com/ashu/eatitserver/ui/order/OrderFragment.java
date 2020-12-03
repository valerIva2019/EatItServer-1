package com.ashu.eatitserver.ui.order;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.ashu.eatitserver.R;

public class OrderFragment extends Fragment {

    private OrderViewModel orderViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        orderViewModel =
                new ViewModelProvider(this).get(OrderViewModel.class);
        View root = inflater.inflate(R.layout.fragment_order, container, false);
        final TextView textView = root.findViewById(R.id.text_slideshow);
        orderViewModel.getText().observe(getViewLifecycleOwner(), s -> textView.setText(s));
        return root;
    }
}