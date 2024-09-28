package com.arian.vizpotifybackend.user.topitems.common;

import java.util.List;
import java.util.Map;

public interface UserTopItemService<T> {

    Map<String, List<T>> getUserTopItems(String userId);


    Map<String, List<T>> fetchUserTopItemsFromDB(String userId);

    Map<String, List<T>> fetchUserTopItemsFromSpotifyAndSave(String userId);


}
