package com.redhelmet.alert2me.ui.base;

import java.util.Collection;

public interface BindableAdapter <T extends Collection>{
    void setData(T data);
}
