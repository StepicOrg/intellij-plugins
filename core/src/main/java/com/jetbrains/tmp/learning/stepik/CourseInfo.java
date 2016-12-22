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

    @SerializedName("title")
    private String myName;
    int id;
    boolean isAdaptive;
    boolean isPublic;
    int[] tags;
    List<Integer> sections;
    List<Integer> instructors = new ArrayList<>();

    List<StepikUser> myAuthors = new ArrayList<>();
    @SerializedName("summary")
    private String myDescription;
    @SerializedName("course_format")
    private String myType;
    //= "pycharm Python"; //course type in format "pycharm <language>"
    @Nullable
    private String username;

    public CourseInfo(){}

    private CourseInfo(String name, String description) {
        myName = name;
        myDescription = description;
    }

    public String getName() {
        return myName;
    }

    @NotNull
    public List<StepikUser> getAuthors() {
        return myAuthors;
    }

    public String getDescription() {
        return myDescription;
    }

    public String getType() {
        return myType;
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
                && that.getDescription().equals(myDescription);
    }

    @Override
    public int hashCode() {
        int result = getName() != null ? getName().hashCode() : 0;
        result = 31 * result + (myDescription != null ? myDescription.hashCode() : 0);
        return result;
    }

    @Nullable
    public String getUsername() {
        return username;
    }

    public void setUsername(@Nullable String username) {
        this.username = username;
    }

    public void setName(String name) {
        myName = name;
    }

    public void setAuthors(List<StepikUser> authors) {
        myAuthors = authors;
        instructors.addAll(authors.stream()
                .filter(author -> author.getId() > 0)
                .map(StepikUser::getId)
                .collect(Collectors.toList()));
    }

    public void addAuthor(StepikUser author) {
        if (myAuthors == null) {
            myAuthors = new ArrayList<>();
        }
        myAuthors.add(author);
    }

    public void setDescription(String description) {
        myDescription = description;
    }

    public void setType(String type) {
        myType = type;
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
