package util;

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
}
