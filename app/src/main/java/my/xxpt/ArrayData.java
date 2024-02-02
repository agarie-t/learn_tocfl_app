package my.xxpt;

import java.util.ArrayList;

public class ArrayData {


    private ArrayList<DataReading> arrayList;

    public ArrayData(ArrayList<DataReading> arrayList) {
        this.arrayList = arrayList;
    }

    public ArrayList<DataReading> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<DataReading> arrayList) {
        this.arrayList = arrayList;
    }
}
