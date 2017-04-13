package org.stepik.api.objects.instructions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.objects.ObjectsContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author meanmail
 */
public class Instructions extends ObjectsContainer<Instruction> {
    private List<Instruction> instructions;
    private List<Rubric> rubrics;

    public List<Instruction> getInstructions() {
        if (instructions == null) {
            instructions = new ArrayList<>();
        }
        return instructions;
    }

    public List<Rubric> getRubrics() {
        if (rubrics == null) {
            rubrics = new ArrayList<>();
        }
        return rubrics;
    }

    @NotNull
    @Override
    public List<Instruction> getItems() {
        return getInstructions();
    }

    @NotNull
    @Override
    public Class<Instruction> getItemClass() {
        return Instruction.class;
    }
}
