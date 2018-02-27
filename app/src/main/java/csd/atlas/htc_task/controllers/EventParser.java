package csd.atlas.htc_task.controllers;


import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import csd.atlas.htc_task.pojo.Event;

/**
 * Created by FRAME on 2/25/2018.
 */

public class EventParser {
    private static EventParser sEventParser;
    private Context mContext;

    private String mAccessToken;
    private List<Event> mEvents;
    private List<Event> result;
    private final static String API_REQUEST =
            "https://api.vk.com/method/groups.search?q=%20&type=event&city_id=56&fields=start_date,finish_date,place,description&count=1000&access_token=";


    private EventParser(Context context) {
        mContext = context.getApplicationContext();
    }


    public static EventParser get(Context context) {
        if (sEventParser == null) {
            sEventParser = new EventParser(context);
        }
        return sEventParser;
    }

    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() + ": with " +
                        urlSpec);
            }
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public void parseVK() {
        ArrayList<Event> events = new ArrayList<>();
        String url = API_REQUEST + mAccessToken;
        try {
            String jsonString = getUrlString(url);
            JSONObject jsonObject = new JSONObject(jsonString);
            Log.i("CONNECT__________", jsonString);
            createItem(events, jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        sortByDate(events);
        mEvents = events;
    }

    // По хардкору, без гсона
    private void createItem(List<Event> events, JSONObject jsonBody) throws JSONException {
        JSONArray jsonArray = jsonBody.getJSONArray("response");
        for (int i = 1; i < jsonArray.length(); i++) {
            JSONObject eventJson = jsonArray.getJSONObject(i);
            Event event = new Event();
            event.setGid(eventJson.getLong("gid"));
            event.setName(eventJson.getString("name"));
            event.setThumbnail(eventJson.getString("photo_big"));
            if (eventJson.has("start_date")) {
                event.setStartDate(convertDate(eventJson.getLong("start_date")));
                event.setEndDate(convertDate(eventJson.getLong("finish_date")));
            } else {
                continue;
            }
            if (eventJson.has("description")) {
                event.setDescription(eventJson.getString("description"));
            } else {
                event.setDescription("Не указано");
            }
            try {
                JSONObject place = eventJson.getJSONObject("place");
                event.setLatitude(place.getDouble("latitude"));
                event.setLongitude(place.getDouble("longitude"));

            } catch (JSONException e) {
                Log.i("FAILED_TO_SET_COORD_", event.getName());
                event.setLatitude(56.8497600);
                event.setLongitude(53.2044800);
            }
            events.add(event);
        }
    }

    private Date convertDate(long unixstamp) {
        return new Date(unixstamp * 1000L);
    }

    private void sortByDate(List<Event> events) {
        Collections.sort(events, new Comparator<Event>() {
            @Override
            public int compare(Event event, Event t1) {
                return t1.getStartDate().compareTo(event.getStartDate());
            }
        });
    }

    //Ограничение по дате начала
    public void showEventsSortedByStartDate(Date start) {
        ArrayList<Event> sorted = new ArrayList<>();
        if (mEvents == null) {
            parseVK();
        }
        for (Event e : mEvents) {
            if (e.getStartDate().after(start)) {
                sorted.add(e);
            }
        }
        result = sorted;
    }

    //Ограничение по дате окончания
    public void showEventsSortedByEndDate(Date end) {
        ArrayList<Event> sorted = new ArrayList<>();
        if (mEvents == null) {
            parseVK();
        }
        for (Event e : mEvents) {
            if (e.getEndDate().before(end)) {
                sorted.add(e);
            }
        }
        result = sorted;
    }

    //Отображение в промежуке дат
    public void showEventsSortedByBothDates(Date start, Date end) {
        ArrayList<Event> sorted = new ArrayList<>();
        for (Event e : mEvents) {
            if (e.getEndDate().getTime() >= start.getTime() &&
                    e.getStartDate().getTime() <= end.getTime()) {
                sorted.add(e);
            }
        }
        result = sorted;
    }


    public void setAccessToken(String accessToken) {
        mAccessToken = accessToken;
    }

    public void showAllEvents() {
        parseVK();
        result = mEvents;
    }

    public List<Event> getResult() {
        return result;
    }

    public Event getEvent(long gID) {
        for (Event event : result) {
            if (event.getGid() == gID) {
                return event;
            }

        }
        return null;
    }
}
