package com.tky.lte.ui.event;

import java.util.ArrayList;

/**
 * Created by I am on 2018/6/26.
 */

public class AddNumberBean {
    private ArrayList<String> numbers;

    public ArrayList<String> getNumbers() {
        return numbers;
    }

    public void setNumbers(ArrayList<String> numbers) {

        //
        if(this.numbers == null)
            this.numbers = new ArrayList<String>();
        else
            this.numbers.clear();
        for (String str : numbers) {
            this.numbers.add(str);
        }
    }
}
