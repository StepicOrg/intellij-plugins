package org.stepik.api.objects.steps;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.objects.AbstractObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author meanmail
 */
public class Step extends AbstractObject {
    private int lesson;
    private int position;
    private String status;
    private BlockView block;
    private Map<String, String> actions;
    private String progress;
    private List<String> subscriptions;
    private String instruction;
    private String session;
    @SerializedName("instruction_type")
    private String instructionType;
    @SerializedName("viewed_by")
    private int viewedBy;
    @SerializedName("passed_by")
    private int passedBy;
    @SerializedName("correct_ratio")
    private double correctRatio;
    private int worth;
    @SerializedName("is_solutions_unlocked")
    private boolean isSolutionsUnlocked;
    @SerializedName("solutions_unlocked_attempts")
    private int solutionsUnlockedAttempts;
    @SerializedName("has_submissions_restrictions")
    private boolean hasSubmissionsRestrictions;
    @SerializedName("max_submissions_count")
    private int maxSubmissionsCount;
    @SerializedName("create_date")
    private String createDate;
    @SerializedName("update_date")
    private String updateDate;
    @SerializedName("discussions_count")
    private int discussionsCount;
    @SerializedName("discussion_proxy")
    private String discussionProxy;
    @SerializedName("discussion_threads")
    private List<String> discussionThreads;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Step step = (Step) o;

        if (lesson != step.lesson) return false;
        if (position != step.position) return false;
        if (viewedBy != step.viewedBy) return false;
        if (passedBy != step.passedBy) return false;
        if (Double.compare(step.correctRatio, correctRatio) != 0) return false;
        if (worth != step.worth) return false;
        if (isSolutionsUnlocked != step.isSolutionsUnlocked) return false;
        if (solutionsUnlockedAttempts != step.solutionsUnlockedAttempts) return false;
        if (hasSubmissionsRestrictions != step.hasSubmissionsRestrictions) return false;
        if (maxSubmissionsCount != step.maxSubmissionsCount) return false;
        if (discussionsCount != step.discussionsCount) return false;
        if (status != null ? !status.equals(step.status) : step.status != null) return false;
        if (block != null ? !block.equals(step.block) : step.block != null) return false;
        if (actions != null ? !actions.equals(step.actions) : step.actions != null) return false;
        if (progress != null ? !progress.equals(step.progress) : step.progress != null) return false;
        if (subscriptions != null ? !subscriptions.equals(step.subscriptions) : step.subscriptions != null)
            return false;
        if (instruction != null ? !instruction.equals(step.instruction) : step.instruction != null) return false;
        if (session != null ? !session.equals(step.session) : step.session != null) return false;
        if (instructionType != null ? !instructionType.equals(step.instructionType) : step.instructionType != null)
            return false;
        if (createDate != null ? !createDate.equals(step.createDate) : step.createDate != null) return false;
        if (updateDate != null ? !updateDate.equals(step.updateDate) : step.updateDate != null) return false;
        //noinspection SimplifiableIfStatement
        if (discussionProxy != null ? !discussionProxy.equals(step.discussionProxy) : step.discussionProxy != null)
            return false;
        return discussionThreads != null ?
                discussionThreads.equals(step.discussionThreads) :
                step.discussionThreads == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        long temp;
        result = 31 * result + lesson;
        result = 31 * result + position;
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (block != null ? block.hashCode() : 0);
        result = 31 * result + (actions != null ? actions.hashCode() : 0);
        result = 31 * result + (progress != null ? progress.hashCode() : 0);
        result = 31 * result + (subscriptions != null ? subscriptions.hashCode() : 0);
        result = 31 * result + (instruction != null ? instruction.hashCode() : 0);
        result = 31 * result + (session != null ? session.hashCode() : 0);
        result = 31 * result + (instructionType != null ? instructionType.hashCode() : 0);
        result = 31 * result + viewedBy;
        result = 31 * result + passedBy;
        temp = Double.doubleToLongBits(correctRatio);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + worth;
        result = 31 * result + (isSolutionsUnlocked ? 1 : 0);
        result = 31 * result + solutionsUnlockedAttempts;
        result = 31 * result + (hasSubmissionsRestrictions ? 1 : 0);
        result = 31 * result + maxSubmissionsCount;
        result = 31 * result + (createDate != null ? createDate.hashCode() : 0);
        result = 31 * result + (updateDate != null ? updateDate.hashCode() : 0);
        result = 31 * result + discussionsCount;
        result = 31 * result + (discussionProxy != null ? discussionProxy.hashCode() : 0);
        result = 31 * result + (discussionThreads != null ? discussionThreads.hashCode() : 0);
        return result;
    }

    @NotNull
    public BlockView getBlock() {
        if (block == null) {
            block = new BlockView();
        }
        return block;
    }

    public void setBlock(@Nullable BlockView block) {
        this.block = block;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @NotNull
    public String getStatus() {
        if (status == null) {
            status = "";
        }
        return status;
    }

    public void setStatus(@Nullable String status) {
        this.status = status;
    }

    public int getLesson() {
        return lesson;
    }

    public void setLesson(int lesson) {
        this.lesson = lesson;
    }

    @NotNull
    public Map<String, String> getActions() {
        if (actions == null) {
            actions = new HashMap<>();
        }
        return actions;
    }

    public void setActions(@Nullable Map<String, String> actions) {
        this.actions = actions;
    }

    @Nullable
    public String getProgress() {
        return progress;
    }

    public void setProgress(@Nullable String progress) {
        this.progress = progress;
    }

    @NotNull
    public List<String> getSubscriptions() {
        if (subscriptions == null) {
            subscriptions = new ArrayList<>();
        }
        return subscriptions;
    }

    public void setSubscriptions(@Nullable List<String> subscriptions) {
        this.subscriptions = subscriptions;
    }

    @Nullable
    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(@Nullable String instruction) {
        this.instruction = instruction;
    }

    @Nullable
    public String getSession() {
        return session;
    }

    public void setSession(@Nullable String session) {
        this.session = session;
    }

    @Nullable
    public String getInstructionType() {
        return instructionType;
    }

    public void setInstructionType(@Nullable String instructionType) {
        this.instructionType = instructionType;
    }

    public int getViewedBy() {
        return viewedBy;
    }

    public void setViewedBy(int viewedBy) {
        this.viewedBy = viewedBy;
    }

    public int getPassedBy() {
        return passedBy;
    }

    public void setPassedBy(int passedBy) {
        this.passedBy = passedBy;
    }

    public double getCorrectRatio() {
        return correctRatio;
    }

    public void setCorrectRatio(double correctRatio) {
        this.correctRatio = correctRatio;
    }

    public int getWorth() {
        return worth;
    }

    public void setWorth(int worth) {
        this.worth = worth;
    }

    public boolean isSolutionsUnlocked() {
        return isSolutionsUnlocked;
    }

    public void setSolutionsUnlocked(boolean solutionsUnlocked) {
        isSolutionsUnlocked = solutionsUnlocked;
    }

    public int getSolutionsUnlockedAttempts() {
        return solutionsUnlockedAttempts;
    }

    public void setSolutionsUnlockedAttempts(int solutionsUnlockedAttempts) {
        this.solutionsUnlockedAttempts = solutionsUnlockedAttempts;
    }

    public boolean isHasSubmissionsRestrictions() {
        return hasSubmissionsRestrictions;
    }

    public void setHasSubmissionsRestrictions(boolean hasSubmissionsRestrictions) {
        this.hasSubmissionsRestrictions = hasSubmissionsRestrictions;
    }

    public int getMaxSubmissionsCount() {
        return maxSubmissionsCount;
    }

    public void setMaxSubmissionsCount(int maxSubmissionsCount) {
        this.maxSubmissionsCount = maxSubmissionsCount;
    }

    @Nullable
    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(@Nullable String createDate) {
        this.createDate = createDate;
    }

    @Nullable
    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(@Nullable String updateDate) {
        this.updateDate = updateDate;
    }

    public int getDiscussionsCount() {
        return discussionsCount;
    }

    public void setDiscussionsCount(int discussionsCount) {
        this.discussionsCount = discussionsCount;
    }

    @Nullable
    public String getDiscussionProxy() {
        return discussionProxy;
    }

    public void setDiscussionProxy(@Nullable String discussionProxy) {
        this.discussionProxy = discussionProxy;
    }

    @NotNull
    public List<String> getDiscussionThreads() {
        if (discussionThreads == null) {
            discussionThreads = new ArrayList<>();
        }
        return discussionThreads;
    }

    public void setDiscussionThreads(@Nullable List<String> discussionThreads) {
        this.discussionThreads = discussionThreads;
    }
}
