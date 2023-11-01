package frontend.semantics.llvmir;

import frontend.semantics.llvmir.value.User;
import frontend.semantics.llvmir.value.Value;

public class Use {
    private User user;
    private Value value;

    public Use(User user, Value value) {
        this.user = user;
        this.value = value;
    }
}
