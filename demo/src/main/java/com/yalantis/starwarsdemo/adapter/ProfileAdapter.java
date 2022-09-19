package com.yalantis.starwarsdemo.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.viewbinding.ViewBinding;

import com.yalantis.starwarsdemo.R;
import com.yalantis.starwarsdemo.databinding.ItemProfileGenderBinding;
import com.yalantis.starwarsdemo.databinding.ItemProfileOtherBinding;
import com.yalantis.starwarsdemo.databinding.ItemProfileSideBinding;
import com.yalantis.starwarsdemo.interfaces.ProfileAdapterListener;
import com.yalantis.starwarsdemo.model.User;
import com.yalantis.starwarsdemo.widget.BackgroundDrawableSwitchCompat;

/**
 * Created by Artem Kholodnyi on 11/17/15.
 */
public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {
    private static final int VIEW_TYPE_SIDE = 0;
    private static final int VIEW_TYPE_TEXT_FIELD = 1;
    private static final int VIEW_GENDER_FIELD = 2;
    private final ProfileAdapterListener mListener;
    private User mUser;

    private final String mFullNameLabel;
    private final String mHomeWorldLabel;
    private final String mBirthdayLabel;
    private final String mGenderLabel;

    public ProfileAdapter(Context context, User user, ProfileAdapterListener listener) {
        mUser = user;
        mListener = listener;

        mFullNameLabel = context.getString(R.string.label_full_name);
        mHomeWorldLabel = context.getString(R.string.label_homeworld);
        mBirthdayLabel = context.getString(R.string.label_birthday);
        mGenderLabel = context.getString(R.string.label_gender);
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0:
                return VIEW_TYPE_SIDE;
            case 1:
            case 2:
            case 3:
                return VIEW_TYPE_TEXT_FIELD;
            case 4:
                return VIEW_GENDER_FIELD;
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewBinding binding = inflateWithBinding(parent, viewType);
        return new ViewHolder(binding);
    }

    private ViewBinding inflateWithBinding(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case VIEW_TYPE_SIDE:
                return ItemProfileSideBinding.inflate(inflater, parent, false);
            case VIEW_TYPE_TEXT_FIELD:
                return ItemProfileOtherBinding.inflate(inflater, parent, false);
            case VIEW_GENDER_FIELD:
                return ItemProfileGenderBinding.inflate(inflater, parent, false);
            default:
                throw new IllegalStateException();
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        switch (position) {
            case 0:
//            holder.getBinding().setVariable(BR.callback, mListener);
//            holder.getBinding().setVariable(BR.user, mUser);
                holder.mySwitch.setCheckedImmediate(mUser.isDarkSide());
                holder.mySwitch.setOnCheckedChangeListener((CompoundButton.OnCheckedChangeListener)
                        (buttonView, isChecked) ->
                                mListener.onSideSwitch(holder.mySwitch));
                holder.label.setText(mUser.getSideText());
                break;
            case 1:
                holder.label.setText(mFullNameLabel);
                holder.value.setText(mUser.getFullName());
                break;
            case 2:
                holder.label.setText(mHomeWorldLabel);
                holder.value.setText(mUser.getHomeworld());
                break;
            case 3:
                holder.label.setText(mBirthdayLabel);
                holder.value.setText(mUser.getBirthday());
                break;
            case 4:
                holder.label.setText(mGenderLabel);
                break;
            default:
                throw new IllegalStateException("unknown");
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @Nullable
        BackgroundDrawableSwitchCompat mySwitch;
        @Nullable
        TextView label;
        @Nullable
        TextView value;

        public ViewHolder(ViewBinding binding) {
            super(binding.getRoot());
            if (binding instanceof ItemProfileSideBinding) {
                mySwitch = ((ItemProfileSideBinding) binding).sideSwitch;
                label = ((ItemProfileSideBinding) binding).tvLabel;
            } else if (binding instanceof ItemProfileOtherBinding) {
                label = ((ItemProfileOtherBinding) binding).tvLabel;
                value = ((ItemProfileOtherBinding) binding).tvValue;
            } else if (binding instanceof ItemProfileGenderBinding) {
                label = ((ItemProfileGenderBinding) binding).tvLabel;
            }
        }
    }
}
