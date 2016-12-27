package com.jetbrains.tmp.learning.stepik;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of class which contains information to be shawn in course description in tool window
 * and when project is being created
 */
public class CourseInfo {
    public static final CourseInfo INVALID_COURSE = new CourseInfo("INVALID", "Please, press refresh button");
    int id;
    List<Integer> sections;
    List<Integer> instructors = new ArrayList<>();
    @SerializedName("title")
    private String name;
    private boolean isAdaptive;
    private boolean isPublic;
    private int[] tags;
    private List<StepikUser> authors = new ArrayList<>();
    @SerializedName("summary")
    private String description;
    @SerializedName("course_format")
    private String type;
    //= "pycharm Python"; //course type in format "pycharm <language>"
    @Nullable
    private String username;

    public CourseInfo() {}

    private CourseInfo(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NotNull
    List<StepikUser> getAuthors() {
        return authors;
    }

    public void setAuthors(List<StepikUser> authors) {
        this.authors = authors;
        instructors.addAll(authors.stream()
                .filter(author -> author.getId() > 0)
                .map(StepikUser::getId)
                .collect(Collectors.toList()));
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CourseInfo that = (CourseInfo) o;
        if (that.getName() == null || that.getDescription() == null) return false;
        return that.getName().equals(getName())
                && that.getDescription().equals(description);
    }

    @Override
    public int hashCode() {
        int result = getName() != null ? getName().hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }

    @Nullable
    public String getUsername() {
        return username;
    }

    public void setUsername(@Nullable String username) {
        this.username = username;
    }

    void addAuthor(StepikUser author) {
        if (authors == null) {
            authors = new ArrayList<>();
        }
        authors.add(author);
    }

    public boolean isAdaptive() {
        return isAdaptive;
    }

    public void setAdaptive(boolean adaptive) {
        isAdaptive = adaptive;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int[] getTags() {
        return tags;
    }

    public void setTags(int[] tags) {
        this.tags = tags;
    }
}
