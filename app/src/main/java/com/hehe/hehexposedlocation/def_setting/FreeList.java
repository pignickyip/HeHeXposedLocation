package com.hehe.hehexposedlocation.def_setting;

//
    /*
    * There are the free list of specify android apps
    * Packge_list mean the packet which can escape my noise
    * Keyword list that reduce the rate of noise
    * Notices that the list must not empty, if empty it cannot use
    * :::::::Connect to my database
    * ::::::::::::Get from the google play store
    *
    * */

public class FreeList{
    public static final String[] PACKGE_LIST = { "android"," " };//"android" , "com.google.android.gms"
    public static final String[] KEYWORD_LIST = { " " };
    public static final String[] APPSLICATION_CATEGORY_LIST = {
            "Art & Design" ,
            "Auto & Vehicles" ,
            "Beauty" ,
            "Books & Reference" ,
            "Business" ,
            "Comics" ,
            "Communications" ,
            "Dating" ,
            "Education" ,
            "Entertainment" ,
            "Events" ,
            "Finance" ,
            "Food & Drink",
            "Health & Fitness" ,
            "House & Home" ,
            "Libraries & Demo" ,
            "Lifestyle" ,
            "Maps & Navigation" ,
            "Medical" ,
            "Music & Audio" ,
            "News & Magazines" ,
            "Parenting" ,
            "Personalization" ,
            "Photography" ,
            "Productivity" ,
            "Shopping" ,
            "Social" ,
            "Sports" ,
            "Tools" ,
            "Travel & Local" ,
            "Video Players & Editors" ,
            "Weather" };
    public static final String[] APPSLICATION_GAME_CATEGORY_LIST = {
            "Action",
            "Adventure",
            "Arcade",
            "Board",
            "Card",
            "Casino",
            "Casual",
            "Educational",
            "Music",
            "Puzzle",
            "Racing",
            "Role Playing",
            "Simulation",
            "Sports",
            "Strategy",
            "Trivia",
            "Word" };
    public static final String HoHo = "The list for free";
}
