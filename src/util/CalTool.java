package util;

import midend.llvmir.value.BasicBlock;

import java.util.ArrayList;

public class CalTool {

    public static int getLLVMStrLength(String value) {
        int valueLength = 0;
        String chinese = "[\u0391-\uFFE5]";
        for (int i = 0; i < value.length(); i++) {
            String temp = value.substring(i, i + 1);
            if (temp.matches(chinese)) {
                valueLength += 3;
            } else {
                if (temp.equals("\\")) {
                    i++;
                }
                valueLength += 1;
            }
        }
        return valueLength;
    }

    // list1 - list2
    public static ArrayList<BasicBlock> sub(ArrayList<BasicBlock> list1, ArrayList<BasicBlock> list2) {
        ArrayList<BasicBlock> temp = new ArrayList<>(list1);
        temp.removeAll(list2);
        return temp;
    }

    // list = list ∪ {ele}
    public static ArrayList<BasicBlock> add(ArrayList<BasicBlock> list, BasicBlock ele) {
        ArrayList<BasicBlock> temp = new ArrayList<>();
        boolean flag = true;
        for (BasicBlock block : list) {
            if (block.equals(ele)) {
                flag = false;
            }
            temp.add(block);
        }
        if (flag) {
            temp.add(ele);
        }
        return temp;
    }
}
