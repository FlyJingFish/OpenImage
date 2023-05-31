package com.flyjingfish.openimage.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    public int id;
    public String name;
    public List<ImageEntity> photos = new ArrayList<>();

}
