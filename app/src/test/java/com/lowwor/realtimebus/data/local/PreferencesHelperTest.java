package com.lowwor.realtimebus.data.local;

import com.lowwor.realtimebus.BuildConfig;
import com.lowwor.realtimebus.BusRobolectricTestRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import rx.observers.TestSubscriber;

import static com.google.common.truth.Truth.assertThat;

/**
 * Created by lowworker on 2016/5/19 0019.
 */
@RunWith(BusRobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21,packageName = "com.lowwor.realtimebus")
public class PreferencesHelperTest {
    final PreferencesHelper mPreferencesHelper = new PreferencesHelper(RuntimeEnvironment.application);

    @Before
    public void setUp() {
        mPreferencesHelper.clear();
    }

    @Test
    public void getStartFromFirst_whenEmpty() throws Exception {
        assertThat(mPreferencesHelper.getIsStartFromFirst()).isEqualTo(true);
    }

    @Test
    public void putAndGetStartFromFirst() throws Exception {
        mPreferencesHelper.saveStartFromFirst(true);
        assertThat(mPreferencesHelper.getIsStartFromFirst()).isEqualTo(true);
    }


    @Test
    public void getAutoCompleteAsObservable() throws Exception {
        TestSubscriber<List<String>> testSubscriber = new TestSubscriber<>();
        mPreferencesHelper.getAutoCompleteAsObservable().subscribe(testSubscriber);
        mPreferencesHelper.saveAutoCompleteItem("3A");
        mPreferencesHelper.saveAutoCompleteItem("4A");


        List<String> items = new ArrayList<>();

        List<String> first = new ArrayList<>();

        items.add("3A");
        List<String> second = new ArrayList<>(items);

        items.add("4A");
        List<String> third = new ArrayList<>(items);



        testSubscriber.assertReceivedOnNext(Arrays.asList(first,second,third));
    }

    @Test
    public void getAutoCompleteAsObservable_whenEmpty() throws Exception {
        List<String> items = new ArrayList<>();

        TestSubscriber<List<String>> testSubscriber = new TestSubscriber<>();
        mPreferencesHelper.getAutoCompleteAsObservable().subscribe(testSubscriber);
        testSubscriber.assertReceivedOnNext(Collections.singletonList(items));

    }

    @Test
    public void saveAutoCompleteAsObservable_removeDuplicate() throws Exception {
        List<String> items = new ArrayList<>();

        TestSubscriber<List<String>> testSubscriber = new TestSubscriber<>();
        mPreferencesHelper.getAutoCompleteAsObservable().subscribe(testSubscriber);

        mPreferencesHelper.saveAutoCompleteItem("3A");
        mPreferencesHelper.saveAutoCompleteItem("3A");
        mPreferencesHelper.saveAutoCompleteItem("3A");
        mPreferencesHelper.saveAutoCompleteItem("3A");

        items.add("3A");
        testSubscriber.assertReceivedOnNext(Arrays.asList(new ArrayList<String>(),items));

    }


    @Test
    public void putAndGetLastQueryLine() throws Exception {
        mPreferencesHelper.saveLastQueryLine("10");
        assertThat(mPreferencesHelper.getLastQueryLine()).isEqualTo("10");
    }

    @Test
    public void getLastQueryLine_whenEmpty() throws Exception {
        assertThat(mPreferencesHelper.getLastQueryLine()).isEqualTo("3A");
    }

    @Test
    public void getShowNotification_whenEmpty() throws Exception {
        assertThat(mPreferencesHelper.getShowNotification()).isEqualTo(true);
    }

    @Test
    public void getTrackBackground_whenEmpty() throws Exception {
        assertThat(mPreferencesHelper.getTrackBackground()).isEqualTo(true);
    }

    @Test
    public void getShowPopupNotification_whenEmpty() throws Exception {
        assertThat(mPreferencesHelper.getShowPopupNotification()).isEqualTo(true);
    }

    @Test
    public void testGetAutoRefreshInterval() throws Exception {
        assertThat(mPreferencesHelper.getAutoRefreshInterval()).isEqualTo(3);

    }
}