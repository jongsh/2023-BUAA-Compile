package frontend.semantics.llvmir.type;

import java.util.ArrayList;
import java.util.List;

public class ArrayType extends ValueType implements Initializable {

    private int cnt;
    private final ValueType type;
    private ArrayList<Initializable> initials;  // 全局变量初始化
    private final boolean zeroInitializer;

    // 局部数组
    public ArrayType(ArrayList<Integer> dimensions) {
        this.cnt = dimensions.get(0);
        this.type = (dimensions.size() == 1) ? new VarType(32)
                : new ArrayType(new ArrayList<>(dimensions.subList(1, dimensions.size())));
        this.initials = null;
        this.zeroInitializer = false;
    }

    // 全局数组
    public ArrayType(List<Integer> dimensions, List<Integer> integers) {
        this.cnt = dimensions.get(0);
        this.type = (dimensions.size() == 1) ? new VarType(32)
                : new ArrayType(new ArrayList<>(dimensions.subList(1, dimensions.size())));
        if (integers == null) {
            zeroInitializer = true;
            this.initials = null;
        } else {
            this.initials = new ArrayList<>();
            this.zeroInitializer = false;
            if (dimensions.size() == 1) {
                for (Integer integer : integers) {
                    this.initials.add(new VarType(32, integer));
                }
            } else {
                int nextLength = 1;
                for (int i = 1; i < dimensions.size(); ++i) {
                    nextLength *= dimensions.get(i);
                }
                for (int i = 0; i < integers.size(); i += nextLength) {
                    this.initials.add(
                            new ArrayType(dimensions.subList(1, dimensions.size()),
                                    integers.subList(i, i+nextLength))
                    );
                }
            }
        }

    }

    @Override
    public String toString() {
        return "[" + cnt + " x " + type + "]";
    }

    @Override
    public String toInitString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this);
        if (zeroInitializer) {
            return sb + " zeroinitializer";
        }
        sb.append(" [");
        for (Initializable initial : initials) {
            sb.append(initial.toInitString()).append(", ");
        }
        return sb.substring(0, sb.length() - 2) + "]";
    }
}