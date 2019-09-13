package com.project.sdl.tripplanner.HomePackage.AutoSuggestPackage;

import com.google.firebase.database.IgnoreExtraProperties;
import com.here.android.mpa.common.GeoBoundingBox;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.search.ContactDetail;
import com.here.android.mpa.search.DiscoveryLink;
import com.here.android.mpa.search.EditorialMedia;
import com.here.android.mpa.search.MediaCollectionPage;
import com.here.android.mpa.search.RatingMedia;
import com.here.android.mpa.search.Ratings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Manish Chougule on 02-08-2019.
 */

@IgnoreExtraProperties
public class Places {

    public String id;
    public String type;
    public String name;
    public String vicinity;
    public String category;
    public GeoCoordinate position;
    public GeoBoundingBox boundaryBox;
    public String url;
    public ArrayList<String> imageRefs;
    public String viewUrl;
    public MediaCollectionPage<RatingMedia> rating;
    public Ratings userRatings;
    public List<ContactDetail> contacts;
    public MediaCollectionPage<EditorialMedia> editorialMedia;
    public Map<String, DiscoveryLink> placeRelated;
    public Map<String,List<String>> alternativeNames;
    public String atributionText;


    public Places() {
        // Default constructor required for calls to DataSnapshot.getValue(AutoSuggest.class)
    }

    public Places(String id, String type,String name,String vicinity,
                  String category,GeoCoordinate position,GeoBoundingBox boundaryBox,
                  String url,ArrayList imageRefsParams,String viewUrl,MediaCollectionPage rating,
                  Ratings userRatings,List contacts,MediaCollectionPage editorialMedia,
                  Map placeRelated,Map alternativeNames,String atributionText) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.vicinity = vicinity;
        this.category = category;
        this.position = position;
        this.boundaryBox = boundaryBox;
        this.url = url;
        this.imageRefs = imageRefsParams;
        this.viewUrl = viewUrl;
        this.rating = rating;
        this.userRatings = userRatings;
        this.contacts = contacts;
        this.editorialMedia = editorialMedia;
        this.placeRelated = placeRelated;
        this.alternativeNames = alternativeNames;
        this.atributionText  = atributionText;


    }
}
