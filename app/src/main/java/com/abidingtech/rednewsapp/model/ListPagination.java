package com.abidingtech.rednewsapp.model;

import java.util.ArrayList;
import java.util.List;

public class ListPagination<T> {


    public List<T> data;
    public String next_page_url;
    public String prev_page_url;
    public String last_page_url;
    public ListPagination() {
        this.data = new ArrayList<>();
    }

}
