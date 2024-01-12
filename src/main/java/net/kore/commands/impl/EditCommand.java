package net.kore.commands.impl;

import net.kore.commands.Command;

public class EditCommand extends Command {
    public EditCommand()
    {
        super("edit", "move");
    }
    @Override
    public void execute(String[] args) throws Exception {

    }

    @Override
    public String getDescription() {
        return ".edit";
    }
}
