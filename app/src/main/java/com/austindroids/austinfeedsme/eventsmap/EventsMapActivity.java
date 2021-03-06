package com.austindroids.austinfeedsme.eventsmap;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.austindroids.austinfeedsme.AustinFeedsMeApplication;
import com.austindroids.austinfeedsme.R;
import com.austindroids.austinfeedsme.data.Event;
import com.austindroids.austinfeedsme.data.EventsRepository;
import com.austindroids.austinfeedsme.events.EventsFilterType;
import com.austindroids.austinfeedsme.utility.DateUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by daz on 8/5/16.
 */
public class EventsMapActivity extends AppCompatActivity implements
        EventsMapContract.View,
        OnMapReadyCallback,
        GoogleMap.OnInfoWindowLongClickListener {
    EventsMapContract.Presenter presenter;
    GoogleMap map;
    CameraPosition cameraPosition;
    SupportMapFragment mapFragment;
    private ViewPager viewPager;
    private PagerAdapter CardPagerAdapter;


    @Inject
    EventsRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_map);

        ((AustinFeedsMeApplication) this.getApplication()).component().inject(this);

        presenter = new EventsMapPresenter(repository, this);

        Toolbar mapToolbar = (Toolbar) findViewById(R.id.map_toolbar);
        setSupportActionBar(mapToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(CardPagerAdapter);
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        LatLng austin = new LatLng(30.27415, -97.73996);
        this.map = map;
        this.map.setMyLocationEnabled(true);
        this.map.moveCamera(CameraUpdateFactory.newLatLngZoom(austin, 13));
        this.map.setOnInfoWindowLongClickListener(this);

        this.map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //reference marker's event's position in arraylist (marker.getTag)
                //viewpager.setcurrentitem(position)
                int markerPosition = (int) marker.getTag();
                Toast.makeText(EventsMapActivity.this, marker.getTag().toString(), Toast.LENGTH_SHORT).show();
                viewPager.setCurrentItem(markerPosition);
                return true;
            }
        });
        presenter.loadEvents();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_eventsmap, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void showFilteringPopUpMenu() {
        PopupMenu popup = new PopupMenu(EventsMapActivity.this, findViewById(R.id.map_filter));
        popup.getMenuInflater().inflate(R.menu.filter_events, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.todays_events:
                        presenter.setFiltering(EventsFilterType.TODAYS_EVENTS);
                        break;
                    case R.id.this_weeks_events:
                        presenter.setFiltering(EventsFilterType.THIS_WEEKS_EVENTS);
                        break;
                    default:
                        presenter.setFiltering(EventsFilterType.ALL_EVENTS);
                        break;
                }
                presenter.loadEvents();
                return true;
            }
        });

        popup.show();
    }


    @Override
    public void onInfoWindowLongClick(Marker marker) {

        String rsvpLink = (String) marker.getTag();

        if (rsvpLink != null)
        {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(rsvpLink));
            startActivity(i);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        showFilteringPopUpMenu();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showEvents(List<Event> events) {
        map.clear();
        CardPagerAdapter adapter = new CardPagerAdapter(events);
        viewPager.setAdapter(adapter);

        for (Event event : events) {
            LatLng eventLocation = new LatLng(
                    Double.valueOf(event.getVenue().getLat()),
                    Double.valueOf(event.getVenue().getLon()));
            if (event.getFoodType().equals("beer")) {
                map.addMarker(new MarkerOptions()
                        .position(eventLocation)
                        .title(DateUtils.getLocalDateFromTimestamp(event.getTime()))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.beer_emoji))
                        .snippet(event.getGroup().getName() + "\n" + event.getName()))
                        .setTag(events.indexOf(event));
            } else if (event.getFoodType().equals("pizza")) {
                map.addMarker(new MarkerOptions()
                        .position(eventLocation)
                        .title(DateUtils.getLocalDateFromTimestamp(event.getTime()))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pizza_emoji_smaller))
                        .snippet(event.getGroup().getName() + "\n" + event.getName()))
                        .setTag(events.indexOf(event));
            } else if (event.getFoodType().equals("tacos")) {
                map.addMarker(new MarkerOptions()
                        .position(eventLocation)
                        .title(DateUtils.getLocalDateFromTimestamp(event.getTime()))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.taco_emoji))
                        .snippet(event.getGroup().getName() + "\n" + event.getName()))
                        .setTag(events.indexOf(event));
            } else {
                map.addMarker(new MarkerOptions()
                        .position(eventLocation)
                        .title(DateUtils.getLocalDateFromTimestamp(event.getTime()))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.apple_emoji)))
                        .setTag(events.indexOf(event));
            }
        }
    }

    public class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter{

        public MarkerInfoWindowAdapter()
        {
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            View v  = getLayoutInflater().inflate(R.layout.fragment_adapter, null);
            //get event object from marker click
            //identify views within infoWindow/viewpager
            //assign data to text/image views
            return v;
        }
    }
}
