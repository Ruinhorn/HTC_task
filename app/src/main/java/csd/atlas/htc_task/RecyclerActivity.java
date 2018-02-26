package csd.atlas.htc_task;

import android.support.v4.app.Fragment;

public class RecyclerActivity extends FragmentActivityHolder {


    @Override
    protected Fragment createFragment() {
        return new RecyclerFragment();
    }
}
