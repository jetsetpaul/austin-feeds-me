package com.austindroids.austinfeedsme.components;

import android.app.Application;

import com.austindroids.austinfeedsme.addeditevent.AddEditEventActivity;
import com.austindroids.austinfeedsme.events.EventsActivity;
import com.austindroids.austinfeedsme.eventsmap.EventsMapActivity;
import com.austindroids.austinfeedsme.modules.AustinFeedsMeApplicationModule;
import com.austindroids.austinfeedsme.modules.DataModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AustinFeedsMeApplicationModule.class, DataModule.class})
public interface ApplicationComponent {
    void inject(AddEditEventActivity addEditEventActivity);
    void inject(EventsActivity eventsActivity);
    void inject(EventsMapActivity eventsMapActivity);

    // Exported for child-components.
    Application application();
}
