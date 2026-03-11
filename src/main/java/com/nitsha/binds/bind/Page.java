package com.nitsha.binds.bind;

import java.util.ArrayList;
import java.util.List;

public class Page {
    public List<Bind> binds;

    public Page(List<Bind> binds) {
        this.binds = binds != null ? binds : new ArrayList<>();
    }
}
