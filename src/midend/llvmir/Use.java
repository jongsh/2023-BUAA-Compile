package midend.llvmir;

import midend.llvmir.value.User;
import midend.llvmir.value.Value;

public class Use {
    private User user;
    private Value value;

    public Use(User user, Value value) {
        this.user = user;
        this.value = value;
    }
}
