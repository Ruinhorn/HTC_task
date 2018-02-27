package csd.atlas.htc_task.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import csd.atlas.htc_task.acvtivities.EventActivity;
import csd.atlas.htc_task.controllers.EventParser;
import csd.atlas.htc_task.R;
import csd.atlas.htc_task.controllers.VkAuth;
import csd.atlas.htc_task.pojo.Event;


/**
 * Created by FRAME on 2/25/2018.
 */

public class RecyclerFragment extends Fragment {

    private static final int REQUEST_ENDDATE = 2;
    private static final int REQUEST_STARTDATE = 1;
    private static final int REQUEST_TOKEN = 0;
    private static final String DIALOG_DATE = "DialogDate";
    private static final String SAVED_TOKEN = "saved token";
    private static final String SAVED_BTTN_STATE = "bttn_state";
    private static final String SAVED_START_STATE = "saved_start_state";
    private static final String SAVED_END_STATE = "saved_end_state";

    private List<Event> mEvents;

    private EventAdapter mEventAdapter;
    private RecyclerView mEventRecyclerView;
    private Button mBttnStartDate;
    private Button mBttnEndDate;

    private Date mDateStart;
    private Date mDateEnd;
    private String mAccessToken;
    private boolean mBttnsShow;


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SAVED_TOKEN, mAccessToken);
        outState.putBoolean(SAVED_BTTN_STATE, mBttnsShow);
        outState.putSerializable(SAVED_START_STATE, mDateStart);
        outState.putSerializable(SAVED_END_STATE, mDateEnd);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.recycler_fragment, container, false);
        mBttnStartDate = (Button) v.findViewById(R.id.date_from);
        mBttnEndDate = (Button) v.findViewById(R.id.date_to);
        mEventRecyclerView = (RecyclerView) v.findViewById(R.id.recycler);

        if (savedInstanceState != null) {
            mAccessToken = savedInstanceState.getString(SAVED_TOKEN);
            mBttnsShow = savedInstanceState.getBoolean(SAVED_BTTN_STATE);
            mDateStart = (Date) savedInstanceState.getSerializable(SAVED_START_STATE);
            if (mDateStart != null) {
                mBttnStartDate.setText(DateFormat.getDateInstance(DateFormat.SHORT)
                        .format(mDateStart));
            }
            mDateEnd = (Date) savedInstanceState.getSerializable(SAVED_END_STATE);
            if (mDateEnd != null) {
                mBttnEndDate.setText(DateFormat.getDateInstance(DateFormat.SHORT)
                        .format(mDateEnd));
            }

        } else {
            FragmentManager manager = getFragmentManager();
            VkAuth dialog = VkAuth.newInstance();
            dialog.setTargetFragment(RecyclerFragment.this, REQUEST_TOKEN);
            dialog.show(manager, "TEST");
        }

        mEventRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mBttnStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance();
                dialog.setTargetFragment(RecyclerFragment.this, REQUEST_STARTDATE);
                dialog.show(manager, "STARTDATE");
            }
        });
        mBttnStartDate.setEnabled(mBttnsShow);

        mBttnEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance();
                dialog.setTargetFragment(RecyclerFragment.this, REQUEST_ENDDATE);
                dialog.show(manager, "ENDDATE");
            }
        });
        mBttnEndDate.setEnabled(mBttnsShow);

        setupAdapter();
        return v;
    }


    private void setBttns(boolean state) {
        mBttnsShow = state;
        mBttnStartDate.setEnabled(state);
        mBttnEndDate.setEnabled(state);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    private void setupAdapter() {
        if (mAccessToken != null) {
            List<Event> events = EventParser.get(getActivity())
                    .getResult();
            if (mEventAdapter == null) {
                mEventAdapter = new EventAdapter(events);
                mEventRecyclerView.setAdapter(mEventAdapter);
            } else {
                mEventAdapter.setAdaptedEvents(events);
                mEventAdapter.notifyDataSetChanged();
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//TODO
        switch (item.getItemId()) {
            case R.id.recycler_update_menu:
                setBttns(false);
                new GetAllEvents().execute();
                setupAdapter();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.recycler_fragment, menu);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    private class EventHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mEventTitleText;
        private TextView mEventSdateText;
        private Event mEvent;


        private EventHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_fragment, parent, false));
            mEventTitleText = (TextView) itemView.findViewById(R.id.event_title);
            mEventSdateText = (TextView) itemView.findViewById(R.id.event_date);
            itemView.setOnClickListener(this);
        }

        private void bindEvent(Event event) {
            mEvent = event;
            String dateToShow = "Дата проведения: с " +
                    DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.MEDIUM)
                            .format(mEvent.getStartDate()) + " по " +
                    DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM)
                            .format(mEvent.getEndDate());

            mEventTitleText.setText(event.getName());
            mEventSdateText.setText(dateToShow);
        }

        @Override
        public void onClick(View view) {
            Intent intent = EventActivity.newIntent(getContext(), mEvent.getGid());
            startActivity(intent);
        }

    }

    private class EventAdapter extends RecyclerView.Adapter<EventHolder> {

        private List<Event> mAdaptedEvents;

        public EventAdapter(List<Event> events) {
            mAdaptedEvents = events; // тут адаптер
        }

        @Override
        public EventHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new EventHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(EventHolder holder, int position) {
            Event event = mAdaptedEvents.get(position);
            holder.bindEvent(event);
        }


        @Override
        public int getItemCount() {
            return mAdaptedEvents.size();
        }

        public void setAdaptedEvents(List<Event> adaptedEvents) {
            mAdaptedEvents = adaptedEvents;
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case REQUEST_TOKEN:
                mAccessToken = data.getStringExtra(VkAuth.EXTRA_TOKEN);
                EventParser.get(getActivity()).setAccessToken(mAccessToken);
                new GetAllEvents().execute();
                Log.i("GOT________TOKEN", mAccessToken);
                break;
            case REQUEST_STARTDATE:
                mDateStart = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
                if (mDateEnd == null) {
                    EventParser.get(getActivity()).showEventsSortedByStartDate(mDateStart);
                    Log.i("GOT________STARTDATE", mDateStart.toString());
                } else {
                    EventParser.get(getActivity()).showEventsSortedByBothDates(
                            mDateStart, mDateEnd);
                    mBttnsShow = true;
                }
                mBttnStartDate.setText(DateFormat.getDateInstance(DateFormat.SHORT)
                        .format(mDateStart));
                setupAdapter();
                break;
            case REQUEST_ENDDATE:
                mDateEnd = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
                if (mDateStart == null) {
                    EventParser.get(getActivity()).showEventsSortedByEndDate(mDateEnd);
                    Log.i("GOT________ENDDATE", mDateEnd.toString());
                } else {
                    EventParser.get(getActivity()).showEventsSortedByBothDates(
                            mDateStart, mDateEnd);
                }
                mBttnEndDate.setText(DateFormat.getDateInstance(DateFormat.SHORT)
                        .format(mDateEnd));
                setupAdapter();
                break;
        }
    }

    private class GetAllEvents extends AsyncTask<Void, Void, List<Event>> { //утечки?
        @Override
        protected List<Event> doInBackground(Void... voids) {
            EventParser.get(getActivity()).showAllEvents();
            return EventParser.get(getActivity()).getResult();
        }

        @Override
        protected void onPostExecute(List<Event> events) {
            mEvents = events;
            setBttns(true);
        }
    }
}

