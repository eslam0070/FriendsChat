package com.egyeso.chatfy.Fragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class SelectionPagerAdapter extends FragmentPagerAdapter {
    public SelectionPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new RequestsFragment();

            case 1:
                return new ChatsFragment();

            case 2:
                return new FriendsFragment();

                default:
                    return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    public CharSequence getPageTitle(int position){
        switch (position){
            case 0:
                return "Requests";

            case 1:
                return "Chats";

            case 2:
                return "Friends";

                default:
                    return null;
        }
    }
}
