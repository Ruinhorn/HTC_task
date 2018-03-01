package csd.atlas.htc_task.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import csd.atlas.htc_task.FragmentActivityHolder;
import csd.atlas.htc_task.fragments.EventFragment;


/**
 * Created by FRAME on 2/25/2018.
 */

public class EventActivity extends FragmentActivityHolder {

    private static final String EXTRA_EVENT_GID =
            "csd.atlas.htc_test.event_gid";


    public static Intent newIntent(Context packageContext, long gID) {
        Intent intent = new Intent(packageContext, EventActivity.class);
        intent.putExtra(EXTRA_EVENT_GID, gID);
        return intent;
    }


    @Override
    protected Fragment createFragment() {
        long gID = (long) getIntent().getSerializableExtra(EXTRA_EVENT_GID);
        return EventFragment.newInstance(gID);
    }

}
