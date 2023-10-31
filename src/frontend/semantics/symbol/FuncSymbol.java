package frontend.semantics.symbol;

import java.util.ArrayList;

public class FuncSymbol extends Symbol {
    private String type;
    private ArrayList<ArrayList<Integer>> params;

    public FuncSymbol(String name) {
        this.name = name;
        this.params = new ArrayList<>();
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    // 一维数组 0，二维数组 0,x
    public void addParam(ArrayList<Integer> lens) {
        params.add(lens);
    }

    public ArrayList<ArrayList<Integer>> getParams() {
        return params;
    }

}
