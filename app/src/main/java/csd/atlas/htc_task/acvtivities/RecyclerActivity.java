package csd.atlas.htc_task.acvtivities;

import android.support.v4.app.Fragment;

import csd.atlas.htc_task.FragmentActivityHolder;
import csd.atlas.htc_task.fragments.RecyclerFragment;

public class RecyclerActivity extends FragmentActivityHolder {


    @Override
    protected Fragment createFragment() {
        return new RecyclerFragment();
    }
}
