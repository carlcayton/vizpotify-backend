package com.arian.vizpotifybackend.services.user;

import java.util.List;
import java.util.Map;

public interface UserTopItemService<T> {

    Map<String, List<T>> getUserTopItems(String userId);


    Map<String, List<T>> fetchUserTopItemsFromDB(String userId);

    Map<String, List<T>> fetchUserTopItemsFromSpotifyAndSave(String userId);


}
