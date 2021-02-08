package og_spipes.model.filetree;

public class Leaf implements FileTree {
    //TODO later on rename to fileAbsolutePath
    private String file;
    private String name;

    public Leaf(String file, String name) {
        this.file = file;
        this.name = name;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
