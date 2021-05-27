package og_spipes.model.filetree;

import java.util.List;

public class SubTree implements FileTree {
    private List<FileTree> children;
    private String name;
    private String id;

    public SubTree(List<FileTree> children, String name, String id) {
        this.children = children;
        this.name = name;
        this.id = id;
    }

    public List<FileTree> getChildren() {
        return children;
    }

    public void setChildren(List<FileTree> children) {
        this.children = children;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
