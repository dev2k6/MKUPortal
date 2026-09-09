package vn.edu.mku.portal.data.network.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class StudentInfoResponse {

    @SerializedName("obj1")
    private List<StudentInfoObj1> obj1;

    @SerializedName("obj2")
    private List<StudentInfoObj2> obj2;

    public List<StudentInfoObj1> getObj1() {
        return obj1;
    }

    public List<StudentInfoObj2> getObj2() {
        return obj2;
    }

    public StudentInfoObj1 getFirstObj1() {
        return (obj1 != null && !obj1.isEmpty()) ? obj1.get(0) : null;
    }
}