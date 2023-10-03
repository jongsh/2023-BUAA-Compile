package frontend.syntax.AST;
import frontend.syntax.SyntaxType;

public class LeafNode extends Node{
    // 叶子结点，每个结点代表一个终结符不可再推
    private final String content;

    public LeafNode(SyntaxType type, String content) {
        this.type = type;
        this.children = null;
        this.content = content;
    }

    @Override
    public String toString() {
        return this.type.toString() + " " + this.content;
    }
}
