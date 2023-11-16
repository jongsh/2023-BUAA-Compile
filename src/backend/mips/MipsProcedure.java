package backend.mips;

import backend.mips.mipscmd.*;

import java.util.ArrayList;

public class MipsProcedure {
    private ArrayList<DataCmd> dataCmdList;
    private ArrayList<TextCmd> textCmdList;

    public MipsProcedure() {
        this.dataCmdList = new ArrayList<>();
        this.textCmdList = new ArrayList<>();
    }

    public void addDataCmd(DataCmd cmd) {
        this.dataCmdList.add(cmd);
    }

    public void addTextCmd(TextCmd cmd) {
        this.textCmdList.add(cmd);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (dataCmdList.size() > 0) {
            sb.append(".data\n");
            for (MipsCmd cmd : dataCmdList) {
                sb.append("\t").append(cmd).append("\n");
            }
        }
        sb.append("\n.text\n");
        for (int i = 0; i < textCmdList.size(); ++i) {
            if (textCmdList.get(i) instanceof JumpCmd && i + 1 < textCmdList.size()
                    && ((JumpCmd) textCmdList.get(i)).checkLabel(textCmdList.get(i + 1))) {
                continue;
            }
            if (!(textCmdList.get(i) instanceof LabelCmd)) {
                sb.append("\t");
            }
            sb.append("\t").append(textCmdList.get(i)).append("\n");
        }
        return sb.toString();
    }
}
