package com.jetbrains.tmp.learning.stepik.entities;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class Reply {
    @Expose
    private String code;
    @Expose
    private String language;
    @Expose
    private String score;
    @Expose
    private List<SolutionFile> solution;

    Reply(ArrayList<SolutionFile> files, String score) {
        this.score = score;
        solution = files;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public List<SolutionFile> getSolution() {
        return solution;
    }

    public void setSolution(List<SolutionFile> solution) {
        this.solution = solution;
    }
}