package com.arian.vizpotifybackend.services.user;

import java.util.List;
import java.util.Map;

public interface IUserTopItemService<T, U> {

    Map<String, List<T>> getUserTopItems(String userId);

    boolean storeUserTopItems(String userId, Map<String, List<U>> topItems);

    Map<String, List<T>> fetchUserTopItemsFromDB(String userId);

    Map<String, List<T>> fetchUserTopItemsFromSpotifyAndSave(String userId);

    List<T> processItemsForTimeRange(String timeRange, String userId, List<U> items);

    U createTopItemObject(String userId, T itemDTO, String timeRange, int rank);
}