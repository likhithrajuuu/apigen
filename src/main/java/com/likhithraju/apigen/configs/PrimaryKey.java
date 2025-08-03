package com.likhithraju.apigen.configs;

import java.util.ArrayList;
import java.util.List;

public class PrimaryKey {
    private List<String> columns = new ArrayList<String>();

    public PrimaryKey(){

    }

    public PrimaryKey(String...keys)
    {
        if(keys.length > 0) {
			for(String key: keys) {
				add(key);
			}
		}
    }
    
    public List<String> getKeyNames(){
        return this.columns;
    }

    public List<String> getColumns() {
        return this.columns;
    }

    public void add(String keys) {
		columns.add(keys);
	}

    public void add(String...keys) {
		if(keys.length > 0) {
			for(String key: keys) {
				add(key);
			}
		}
	}
}
