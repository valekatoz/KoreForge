package net.kore.managers;

import net.kore.modules.Module;
import net.kore.modules.render.Gui;
import net.kore.ui.hud.Component;
import net.kore.ui.hud.DraggableComponent;
import org.reflections.Reflections;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


import java.lang.reflect.InvocationTargetException;
import java.util.Set;

public class ModuleManager {
    public CopyOnWriteArrayList<Module> modules = new CopyOnWriteArrayList<>();
    public  String classPath;

    public ModuleManager(String classPath)
    {
        this.classPath = classPath;
    }

    public void initReflection()
    {
        Reflections reflections = new Reflections(classPath);
        Set<Class<? extends Module>> moduleClasses = reflections.getSubTypesOf(Module.class);

        for (Class<? extends Module> clazz : moduleClasses)
        {
            try {
                Module module = clazz.getDeclaredConstructor().newInstance();
                modules.add(module);
                module.assign();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }
    public List<Module> getModules()
    {
        return modules;
    }
}