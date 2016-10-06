package org.stepik.plugin.collective;

public enum SupportedLanguages {
    JAVA("java8", "// ", "Main.java"),
    PYTHON("python3", "# ","main.py");

    private final String name;
    private final String comment;
    private final String mainFileName;


    private SupportedLanguages(String name, String comment, String mainFileName) {
        this.name = name;
        this.comment = comment;
        this.mainFileName = mainFileName;
    }

    public String getName(){
        return name;
    }

    public String getComment(){
        return comment;
    }

    public String getMainFileName(){
        return mainFileName;
    }
}