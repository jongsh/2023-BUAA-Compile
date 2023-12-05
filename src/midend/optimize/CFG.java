package midend.optimize;

import midend.llvmir.value.BasicBlock;
import midend.llvmir.value.Function;
import util.CalTool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class CFG {
    private final Function function;
    // 控制流程图
    private final HashMap<BasicBlock, ArrayList<BasicBlock>> cfgNextList;
    private final HashMap<BasicBlock, ArrayList<BasicBlock>> cfgPrevList;
    // 支配树
    private final HashMap<BasicBlock, ArrayList<BasicBlock>> domList;
    private final HashMap<BasicBlock, ArrayList<BasicBlock>> dtNextList;
    private final HashMap<BasicBlock, BasicBlock> dtPrevList;
    private final HashMap<BasicBlock, ArrayList<BasicBlock>> dfList;

    public CFG(Function function) {
        this.cfgNextList = new HashMap<>();
        this.cfgPrevList = new HashMap<>();
        this.dtNextList = new HashMap<>();
        this.dtPrevList = new HashMap<>();
        this.domList = new HashMap<>();
        this.dfList = new HashMap<>();
        this.function = function;
        init();
        clearDeadBlock(); //     // 删除不可达的 block
        genDom();                // 生成严格支配集合
        genDT();                 // 生成支配树
        genDF();                 // 生成支配边界集合
    }

    public ArrayList<BasicBlock> getDFListOf(BasicBlock block) {
        return dfList.get(block);
    }

    public ArrayList<BasicBlock> getCFGChildrenList(BasicBlock block) {
        return cfgNextList.get(block);
    }

    public ArrayList<BasicBlock> getCFGFatherList(BasicBlock block) {
        return cfgPrevList.get(block);
    }

    public ArrayList<BasicBlock> getDTChildrenList(BasicBlock block) {
        return dtNextList.get(block);
    }

    public BasicBlock getDTFather(BasicBlock block) {
        return dtPrevList.get(block);
    }

    // 生成流图和支配关系
    private void init() {
        ArrayList<BasicBlock> basicBlocks = function.getBasicBlockList();
        for (BasicBlock from : basicBlocks) {
            addBlock(from);
        }
    }

    private void addBlock(BasicBlock basicBlock) {
        cfgNextList.put(basicBlock, basicBlock.getNextBlocks());
        if (!cfgPrevList.containsKey(basicBlock)) {
            cfgPrevList.put(basicBlock, new ArrayList<>());
        }
        for (BasicBlock next : basicBlock.getNextBlocks()) {
            if (!cfgPrevList.containsKey(next)) {
                cfgPrevList.put(next, new ArrayList<>());
            }
            cfgPrevList.get(next).add(basicBlock);
        }

        dtPrevList.put(basicBlock, basicBlock);
        dtNextList.put(basicBlock, new ArrayList<>());
        domList.put(basicBlock, new ArrayList<>());
        dfList.put(basicBlock, new ArrayList<>());
    }

    private void genDom() {
        ArrayList<BasicBlock> total = function.getBasicBlockList();
        for (BasicBlock block : total) {
            if (block.getName().equals("block_19")) {
                int m = 1;
            }
            ArrayList<BasicBlock> record = new ArrayList<>();
            record.add(total.get(0));  // 入口块
            record.add(block);         // 目标块
            genDomDFS(total.get(0), block, record);
            domList.put(block, CalTool.sub(total, record));
        }
    }

    private void genDomDFS(BasicBlock cur, BasicBlock target, ArrayList<BasicBlock> rec) {
        if (!cur.equals(target)) {
            rec.add(cur);
            for (BasicBlock next : cfgNextList.get(cur)) {
                if (!rec.contains(next)) {
                    genDomDFS(next, target, rec);
                }
            }
        }
    }

    private void genDT() {
        ArrayList<BasicBlock> total = function.getBasicBlockList();
        for (BasicBlock dom : total) {
            for (BasicBlock domed : domList.get(dom)) {
                dtPrevList.put(domed, dom);
            }
        }
        for (BasicBlock domed : total) {
            BasicBlock dom = dtPrevList.get(domed);
            if (!dom.equals(domed)) {
                dtNextList.get(dom).add(domed);
            }
        }
    }

    private void genDF() {
        ArrayList<BasicBlock> total = function.getBasicBlockList();
        for (BasicBlock vertex : total) {
            for (BasicBlock target : cfgNextList.get(vertex)) {
                // 遍历 CFG 的每一条边
                BasicBlock cur = vertex;
                while (!domList.get(cur).contains(target) && !cur.equals(target)) {
                    dfList.put(cur, CalTool.add(dfList.get(cur), target));
                    cur = dtPrevList.get(cur);
                }
            }
        }
    }

    private void clearDeadBlock() {
        ArrayList<BasicBlock> total = function.getBasicBlockList();
        HashSet<BasicBlock> aliveBlocks = new HashSet<>();
        findAliveBlock(total.get(0), aliveBlocks);
        for (int i = 0; i < total.size(); ++i) {
            if (!aliveBlocks.contains(total.get(i))) {
                for (BasicBlock nextBlock : cfgNextList.get(total.get(i))) {
                    cfgPrevList.get(nextBlock).remove(total.get(i));
                }
                cfgNextList.remove(total.get(i));
                cfgPrevList.remove(total.get(i));
                domList.remove(total.get(i));
                dtNextList.remove(total.get(i));
                dtPrevList.remove(total.get(i));
                dfList.remove(total.get(i));
                total.get(i).deleted();
                total.remove(i);
                i--;
            }
        }
    }

    private void findAliveBlock(BasicBlock entry, HashSet<BasicBlock> aliveBlocks) {
        aliveBlocks.add(entry);
        for (BasicBlock block : cfgNextList.get(entry)) {
            if (!aliveBlocks.contains(block)) {
                findAliveBlock(block, aliveBlocks);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (BasicBlock from : cfgNextList.keySet()) {
            sb.append(from.getName()).append(" --->");
            for (BasicBlock to : cfgNextList.get(from)) {
                sb.append("  ").append(to.getName());
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
